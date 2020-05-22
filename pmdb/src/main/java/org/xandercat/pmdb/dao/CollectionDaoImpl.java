package org.xandercat.pmdb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xandercat.pmdb.dto.CollectionPermission;
import org.xandercat.pmdb.dto.MovieCollection;

@Component
public class CollectionDaoImpl implements CollectionDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private KeyGenerator keyGenerator;
	
	@Override
	public List<MovieCollection> getViewableMovieCollections(String username) {
		return getSharedMovieCollections(username, true);
	}
	
	@Override
	public List<MovieCollection> getShareOfferMovieCollections(String username) {
		return getSharedMovieCollections(username, false);
	}

	private List<MovieCollection> getSharedMovieCollections(String username, boolean accepted) {
		final String sql = "SELECT id, name, owner, cloud, allowEdit FROM collection"
				+ " INNER JOIN collection_permission ON collection.id = collection_permission.collection_id"
				+ " WHERE username = ? AND accepted = ?";
		final List<MovieCollection> movieCollections = new ArrayList<MovieCollection>();
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
				ps.setBoolean(2, accepted);
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				MovieCollection movieCollection = new MovieCollection();
				movieCollection.setId(rs.getString(1));
				movieCollection.setName(rs.getString(2));
				movieCollection.setOwner(rs.getString(3), username);
				movieCollection.setCloud(rs.getBoolean(4));
				movieCollection.setEditable(rs.getBoolean(5));
				movieCollections.add(movieCollection);
			}
		});
		return movieCollections;
	}

	@Override
	public Optional<MovieCollection> getViewableMovieCollection(String collectionId, String username) {
		return getViewableMovieCollections(username).stream()
				.filter(movieCollection -> movieCollection.getId().equals(collectionId))
				.findAny();
	}

	@Override
	@Transactional
	public void addMovieCollection(MovieCollection movieCollection) {
		final String sql = "INSERT INTO collection (id, name, owner, cloud) VALUES (?, ?, ?, ?)";
		String key = keyGenerator.getKey();
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, key);
				ps.setString(2, movieCollection.getName());
				ps.setString(3, movieCollection.getOwner());
				ps.setBoolean(4, movieCollection.isCloud());
			}
		});
		shareCollection(key, movieCollection.getOwner(), true);
		acceptShareOffer(key, movieCollection.getOwner());
		movieCollection.setId(key);
	}

	@Override
	public void updateMovieCollection(MovieCollection movieCollection) {
		final String sql = "UPDATE collection SET name = ? WHERE id = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, movieCollection.getName());
				ps.setString(2, movieCollection.getId());
			}
		});
	}

	@Override
	@Transactional
	public void deleteMovieCollection(String collectionId) {
		final String sql = "DELETE FROM collection WHERE id = ?";
		final String shareSql = "DELETE FROM collection_permission WHERE collection_id = ?";
		final String defSql = "DELETE FROM collection_default WHERE collection_id = ?";
		jdbcTemplate.update(shareSql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, collectionId);
			}
		});
		jdbcTemplate.update(defSql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, collectionId);
			}
		});
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, collectionId);
			}
		});
	}

	@Override
	public void shareCollection(String collectionId, String username, boolean editable) {
		final String sql = "INSERT INTO collection_permission(collection_id, username, allowEdit, accepted) VALUES (?, ?, ?, ?)";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, collectionId);
				ps.setString(2, username);
				ps.setBoolean(3, editable);
				ps.setBoolean(4, false);
			}
		});
	}

	@Override
	public void updateEditable(String collectionId, String username, boolean editable) {
		final String sql = "UPDATE collection_permission SET allowEdit = ? WHERE collection_id = ? AND username = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setBoolean(1, editable);
				ps.setString(2, collectionId);
				ps.setString(3, username);
			}
		});
	}

	@Override
	@Transactional
	public boolean unshareCollection(String collectionId, String username) {
		final String sql = "DELETE FROM collection_permission WHERE collection_id = ? AND username = ?";
		final String defSql = "DELETE FROM collection_default WHERE collection_id = ? AND username = ?"; // may or may not exist
		jdbcTemplate.update(defSql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, collectionId);
				ps.setString(2, username);
			}
		});	
		int rowsAffected = jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, collectionId);
				ps.setString(2, username);
			}
		});
		return rowsAffected > 0;
	}

	@Override
	public Optional<String> getDefaultCollectionId(String username) {
		final String sql = "SELECT collection_id FROM collection_default WHERE username = ?";
		List<String> ids = new ArrayList<String>();
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				ids.add(rs.getString(1));
			}
		});
		return ids.stream().findAny();
	}

	@Override
	public void setDefaultCollection(String username, String collectionId) {
		Optional<String> currentDefault = getDefaultCollectionId(username);
		final String insertSql = "INSERT INTO collection_default(collection_id, username) VALUES (?, ?)";
		final String updateSql = "UPDATE collection_default SET collection_id = ? WHERE username = ?";
		String sql = (currentDefault.isPresent())? updateSql : insertSql;
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, collectionId);
				ps.setString(2, username);
			}
		});
	}

	@Override
	public boolean acceptShareOffer(String collectionId, String username) {
		final String sql = "UPDATE collection_permission SET accepted = 1 WHERE collection_id = ? AND username = ?";
		int rowsAffected = jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, collectionId);
				ps.setString(2, username);
			}
		});
		return rowsAffected > 0;
	}

	@Override
	@Transactional
	public List<CollectionPermission> getCollectionPermissions(String collectionId) {
		final String ownerSql = "SELECT owner FROM collection WHERE id = ?";
		String owner = jdbcTemplate.queryForObject(ownerSql, String.class, collectionId);
		final String sql = "SELECT username, allowEdit, accepted FROM collection_permission WHERE collection_id = ? AND username <> ?";
		final List<CollectionPermission> permissions = new ArrayList<CollectionPermission>();
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, collectionId);
				ps.setString(2, owner);
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				CollectionPermission permission = new CollectionPermission();
				permission.setCollectionId(collectionId);
				permission.setUsername(rs.getString(1));
				permission.setAllowEdit(rs.getBoolean(2));
				permission.setAccepted(rs.getBoolean(3));
				permissions.add(permission);
			}
		});
		return permissions;
	}

	@Override
	public Optional<CollectionPermission> getCollectionPermission(String collectionId, String username) {
		return getCollectionPermissions(collectionId).stream()
				.filter(permission -> permission.getUsername().equals(username))
				.findAny();
	}
}
