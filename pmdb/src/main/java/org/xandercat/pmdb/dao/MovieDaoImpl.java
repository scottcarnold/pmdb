package org.xandercat.pmdb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.util.CIString;

@Component
public class MovieDaoImpl implements MovieDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void deleteMoviesForCollection(int collectionId) {
		// note: relying on cascade delete for movie attributes
		final String sql = "DELETE FROM movie WHERE collection_id = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
			}
		});
	}

	@Override
	public List<Movie> getMoviesForCollection(int collectionId) {
		final String sql = "SELECT id, title FROM movie WHERE collection_id = ? ORDER BY title";
		final List<Movie> movies = new ArrayList<Movie>();
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Movie movie = new Movie();
				movie.setId(rs.getInt(1));
				movie.setTitle(rs.getString(2));
				movies.add(movie);
			}
		});
		for (Movie movie : movies) {
			movie.setAttributes(getMovieAttributes(movie.getId()));
		}
		return movies;
	}

	@Override
	public List<Movie> searchMoviesForCollection(int collectionId, String searchString) {
		final String lcSearchString = searchString.trim().toLowerCase();
		final String sql = "SELECT id, title FROM movie "
				+ " INNER JOIN movie_attributes ON movie.id = movie_attributes.movie_id"
				+ " WHERE collection_id = ? "
				+ " AND (LOWER(title) like ?"
				+ " OR LOWER(attribute_name) like ?"
				+ " OR LOWER(attribute_value) like ?)"
				+ " ORDER BY title";
		final List<Movie> movies = new ArrayList<Movie>();
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
				ps.setString(2, "%" + lcSearchString + "%");
				ps.setString(3, "%" + lcSearchString + "%");
				ps.setString(4, "%" + lcSearchString + "%");
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Movie movie = new Movie();
				movie.setId(rs.getInt(1));
				movie.setTitle(rs.getString(2));
				movies.add(movie);
			}
		});
		for (Movie movie : movies) {
			movie.setAttributes(getMovieAttributes(movie.getId()));
		}
		return movies;
	}

	@Override
	public Movie getMovie(int id) {
		final String sql = "SELECT id, title, collection_id FROM movie WHERE id = ?";
		final List<Movie> movies = new ArrayList<Movie>();
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, id);
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Movie movie = new Movie();
				movie.setId(rs.getInt(1));
				movie.setTitle(rs.getString(2));
				movie.setCollectionId(rs.getInt(3));
				movies.add(movie);
			}
		});
		if (movies.size() > 0) {
			movies.get(0).setAttributes(getMovieAttributes(movies.get(0).getId()));
		}
		return movies.size() == 0? null : movies.get(0);
	}

	@Override
	public void addMovie(Movie movie) {
		final String sql = "INSERT INTO movie(title, collection_id) VALUES (?, ?)";
		final String getIdSql = "SELECT LAST_INSERT_ID()";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, movie.getTitle());
				ps.setInt(2, movie.getCollectionId());
			}
		});
		int id = jdbcTemplate.queryForObject(getIdSql, Integer.class);
		movie.setId(id);
		for (Map.Entry<CIString, String> entry : movie.getAttributes().entrySet()) {
			addMovieAttribute(id, entry.getKey().toString(), entry.getValue());
		}
	}

	@Override
	public void updateMovie(Movie movie) {
		final String sql = "UPDATE movie SET title = ? WHERE id = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, movie.getTitle());
				ps.setInt(2, movie.getId());
			}
		});
		// do some set logic to figure out attributes
		Map<CIString, String> oldAttributes = getMovieAttributes(movie.getId());
		Set<CIString> oldKeys = oldAttributes.keySet();
		Set<CIString> newKeys = movie.getAttributes().keySet();
		Set<CIString> addKeys = new HashSet<CIString>();
		Set<CIString> deleteKeys = new HashSet<CIString>();
		Set<CIString> updateKeys = new HashSet<CIString>();
		addKeys.addAll(newKeys);
		addKeys.removeAll(oldKeys);
		deleteKeys.addAll(oldKeys);
		deleteKeys.removeAll(newKeys);
		updateKeys.addAll(oldKeys);
		updateKeys.removeAll(deleteKeys);
		for (CIString key : deleteKeys) {
			deleteMovieAttribute(movie.getId(), key.toString());
		}
		for (CIString key : addKeys) {
			addMovieAttribute(movie.getId(), key.toString(), movie.getAttributes().get(key));
		}
		for (CIString key : updateKeys) {
			updateMovieAttribute(movie.getId(), key.toString(), movie.getAttributes().get(key));
		}
	}

	@Override
	public void deleteMovie(int id) {
		// note: relying on cascade delete for movie attributes
		final String sql = "DELETE FROM movie WHERE id = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, id);
			}
		});
	}
	
	public TreeMap<CIString, String> getMovieAttributes(int id) {
		final String sql = "SELECT attribute_name, attribute_value FROM movie_attributes WHERE movie_id = ?";
		final TreeMap<CIString, String> movieAttributes = new TreeMap<CIString, String>();
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, id);
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				movieAttributes.put(new CIString(rs.getString(1)), rs.getString(2));
			}
		});
		return movieAttributes;
	}
	
	public void addMovieAttribute(int id, String key, String value) {
		final String sql = "INSERT INTO movie_attributes (movie_id, attribute_name, attribute_value) VALUES (?, ?, ?)";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, id);
				ps.setString(2, key);
				ps.setString(3, value);
			}
		});
	}
	
	public void deleteMovieAttribute(int id, String key) {
		final String sql = "DELETE FROM movie_attributes WHERE movie_id = ? AND LOWER(attribute_name) = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, id);
				ps.setString(2, key.toLowerCase());
			}
		});		
	}
	
	public void updateMovieAttribute(int id, String key, String value) {
		final String sql = "UPDATE movie_attributes SET attribute_value = ? WHERE movie_id = ? AND LOWER(attribute_name) = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, value);
				ps.setInt(2, id);
				ps.setString(3, key.toLowerCase());
			}			
		});
	}

	@Override
	public List<String> getTableColumnPreferences(String username) {
		return getTableColumnPreferences(username, null, null);
	}

	private List<String> getTableColumnPreferences(String username, Integer fromIdx, Integer toIdx) {
		final StringBuilder sql = new StringBuilder("SELECT attribute_name FROM movie_attributes_table_columns WHERE username = ?");
		if (fromIdx != null) {
			sql.append(" AND idx >= ?");
		}
		if (toIdx != null) {
			sql.append(" AND idx <= ?");
		}
		sql.append(" ORDER BY idx");
		final List<String> tableColumnPreferences = new ArrayList<String>();
		jdbcTemplate.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int i=0;
				ps.setString(++i, username);
				if (fromIdx != null) {
					ps.setInt(++i, fromIdx.intValue());
				}
				if (toIdx != null) {
					ps.setInt(++i, toIdx.intValue());
				}
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				tableColumnPreferences.add(rs.getString(1));
			}
		});
		return tableColumnPreferences;
	}
	
	@Override
	public Integer getMaxTableColumnPreferenceIndex(String username) {
		final String maxSql = "SELECT MAX(idx) FROM movie_attributes_table_columns WHERE username = ?";
		final List<Integer> max = new ArrayList<Integer>();
		jdbcTemplate.query(maxSql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				int maxIdx = rs.getInt(1);
				if (!rs.wasNull()) {
					max.add(Integer.valueOf(maxIdx));
				}
			}
		});
		return (max.size() == 0)? null : max.get(0);
	}

	@Override
	public void addTableColumnPreference(String attributeName, String username) {
		final String insertSql = "INSERT INTO movie_attributes_table_columns(username, idx, attribute_name) VALUES (?, ?, ?)";
		Integer max = getMaxTableColumnPreferenceIndex(username);
		final int nextIdx = (max != null)? max.intValue() + 1 : 0;
		jdbcTemplate.update(insertSql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
				ps.setInt(2, nextIdx);
				ps.setString(3, attributeName);
			}
		});
	}

	@Override
	public void reorderTableColumnPreference(int sourceIdx, int targetIdx, String username) {
		if (sourceIdx == targetIdx) {
			return;
		}
		updateTableColumnPreferenceIndex(sourceIdx, -1, username); // temporary holding index
		if (sourceIdx < targetIdx) {
			for (int i=sourceIdx+1; i<=targetIdx; i++) {
				updateTableColumnPreferenceIndex(i, i-1, username);
			}
		} else {
			for (int i=sourceIdx-1; i>=targetIdx; i--) {
				updateTableColumnPreferenceIndex(i, i+1, username);
			}
		}
		updateTableColumnPreferenceIndex(-1, targetIdx, username);
	}

	private void updateTableColumnPreferenceIndex(int fromIdx, int toIdx, String username) {
		final String sql = "UPDATE movie_attributes_table_columns SET idx = ? WHERE username = ? AND idx = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, toIdx);
				ps.setString(2, username);
				ps.setInt(3, fromIdx);
			}
		});
	}
	
	@Override
	public void deleteTableColumnPreference(int sourceIdx, String username) {
		List<String> shiftPreferences = getTableColumnPreferences(username, sourceIdx+1, null);
		final String sql = "DELETE FROM movie_attributes_table_columns WHERE username = ? AND idx >= ?";
		int rowsAffected = jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
				ps.setInt(2, sourceIdx);
			}
		});
		if (rowsAffected > 0) {
			for (String preference : shiftPreferences) {
				addTableColumnPreference(preference, username);
			}
		}
	}

	@Override
	public List<String> getAttributeKeysForCollection(int collectionId) {
		final String sql = "SELECT DISTINCT(attribute_name) FROM movie"
				+ " INNER JOIN movie_attributes ON movie.id = movie_attributes.movie_id"
				+ " WHERE movie.collection_id = ?";
		final List<String> attributeKeys = new ArrayList<String>();
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				attributeKeys.add(rs.getString(1));
			}
		});
		return attributeKeys;
	}
}
