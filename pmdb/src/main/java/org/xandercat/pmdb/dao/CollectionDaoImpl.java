package org.xandercat.pmdb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xandercat.pmdb.dto.CollectionPermission;
import org.xandercat.pmdb.dto.MovieCollection;

@Component
public class CollectionDaoImpl implements CollectionDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<MovieCollection> getViewableMovieCollections(String username) {
		return getSharedMovieCollections(username, true);
	}
	
	@Override
	public List<MovieCollection> getShareOfferMovieCollections(String username) {
		return getSharedMovieCollections(username, false);
	}

	private List<MovieCollection> getSharedMovieCollections(String username, boolean accepted) {
		final String sql = "SELECT id, name, owner, allowEdit FROM collection"
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
				movieCollection.setId(rs.getInt(1));
				movieCollection.setName(rs.getString(2));
				movieCollection.setOwner(rs.getString(3), username);
				movieCollection.setEditable(rs.getBoolean(4));
				movieCollections.add(movieCollection);
			}
		});
		return movieCollections;
	}

	@Override
	public MovieCollection getViewableMovieCollection(int collectionId, String username) {
		List<MovieCollection> viewableMovieCollections = getViewableMovieCollections(username);
		for (MovieCollection movieCollection : viewableMovieCollections) {
			if (movieCollection.getId() == collectionId) {
				return movieCollection;
			}
		}
		return null;
	}

	@Override
	@Transactional
	public void addMovieCollection(MovieCollection movieCollection) {
		final String sql = "INSERT INTO collection (name, owner) VALUES (?, ?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, movieCollection.getName());
				ps.setString(2, movieCollection.getOwner());				
				return ps;
			}
		}, keyHolder);
		int collectionId = keyHolder.getKey().intValue();
		shareCollection(collectionId, movieCollection.getOwner(), true);
		acceptShareOffer(collectionId, movieCollection.getOwner());
		movieCollection.setId(collectionId);
	}

	@Override
	public void updateMovieCollection(MovieCollection movieCollection) {
		final String sql = "UPDATE collection SET name = ? WHERE id = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, movieCollection.getName());
				ps.setInt(2, movieCollection.getId());
			}
		});
	}

	@Override
	@Transactional
	public void deleteMovieCollection(int collectionId) {
		final String sql = "DELETE FROM collection WHERE id = ?";
		final String shareSql = "DELETE FROM collection_permission WHERE collection_id = ?";
		final String defSql = "DELETE FROM collection_default WHERE collection_id = ?";
		jdbcTemplate.update(shareSql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
			}
		});
		jdbcTemplate.update(defSql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
			}
		});
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
			}
		});
	}

	@Override
	public void shareCollection(int collectionId, String username, boolean editable) {
		final String sql = "INSERT INTO collection_permission(collection_id, username, allowEdit, accepted) VALUES (?, ?, ?, ?)";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
				ps.setString(2, username);
				ps.setBoolean(3, editable);
				ps.setBoolean(4, false);
			}
		});
	}

	@Override
	public void updateEditable(int collectionId, String username, boolean editable) {
		final String sql = "UPDATE collection_permission SET allowEdit = ? WHERE collection_id = ? AND username = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setBoolean(1, editable);
				ps.setInt(2, collectionId);
				ps.setString(3, username);
			}
		});
	}

	@Override
	@Transactional
	public boolean unshareCollection(int collectionId, String username) {
		final String sql = "DELETE FROM collection_permission WHERE collection_id = ? AND username = ?";
		final String defSql = "DELETE FROM collection_default WHERE collection_id = ? AND username = ?"; // may or may not exist
		jdbcTemplate.update(defSql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
				ps.setString(2, username);
			}
		});	
		int rowsAffected = jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
				ps.setString(2, username);
			}
		});
		return rowsAffected > 0;
	}

	@Override
	public Integer getDefaultCollectionId(String username) {
		final String sql = "SELECT collection_id FROM collection_default WHERE username = ?";
		List<Integer> ids = new ArrayList<Integer>();
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				ids.add(Integer.valueOf(rs.getInt(1)));
			}
		});
		return ids.size() > 0? ids.get(0) : null;
	}

	@Override
	public void setDefaultCollection(String username, int collectionId) {
		Integer currentDefault = getDefaultCollectionId(username);
		final String insertSql = "INSERT INTO collection_default(collection_id, username) VALUES (?, ?)";
		final String updateSql = "UPDATE collection_default SET collection_id = ? WHERE username = ?";
		String sql = (currentDefault == null)? insertSql : updateSql;
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
				ps.setString(2, username);
			}
		});
	}

	@Override
	public boolean acceptShareOffer(int collectionId, String username) {
		final String sql = "UPDATE collection_permission SET accepted = 1 WHERE collection_id = ? AND username = ?";
		int rowsAffected = jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
				ps.setString(2, username);
			}
		});
		return rowsAffected > 0;
	}

	@Override
	@Transactional
	public List<CollectionPermission> getCollectionPermissions(int collectionId) {
		final String ownerSql = "SELECT owner FROM collection WHERE id = ?";
		String owner = jdbcTemplate.queryForObject(ownerSql, String.class, collectionId);
		final String sql = "SELECT username, allowEdit, accepted FROM collection_permission WHERE collection_id = ? AND username <> ?";
		final List<CollectionPermission> permissions = new ArrayList<CollectionPermission>();
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
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
	public CollectionPermission getCollectionPermission(int collectionId, String username) {
		List<CollectionPermission> permissions = getCollectionPermissions(collectionId);
		for (CollectionPermission permission : permissions) {
			if (permission.getUsername().equals(username)) {
				return permission;
			}
		}
		return null;
	}
}
