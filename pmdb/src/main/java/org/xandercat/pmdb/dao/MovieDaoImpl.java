package org.xandercat.pmdb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dto.Movie;

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
		final String sql = "SELECT id, title FROM movie WHERE collection_id = ? "
				+ " AND LOWER(title) like ?"
				+ " ORDER BY title";
		final List<Movie> movies = new ArrayList<Movie>();
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, collectionId);
				ps.setString(2, "%" + searchString.trim() + "%");
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
		for (Map.Entry<String, String> entry : movie.getAttributes().entrySet()) {
			addMovieAttribute(id, entry.getKey(), entry.getValue());
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
		Map<String, String> oldAttributes = getMovieAttributes(movie.getId());
		Set<String> oldKeys = oldAttributes.keySet();
		Set<String> newKeys = movie.getAttributes().keySet();
		Set<String> addKeys = new HashSet<String>();
		Set<String> deleteKeys = new HashSet<String>();
		Set<String> updateKeys = new HashSet<String>();
		addKeys.addAll(newKeys);
		addKeys.removeAll(oldKeys);
		deleteKeys.addAll(oldKeys);
		deleteKeys.removeAll(newKeys);
		updateKeys.addAll(oldKeys);
		updateKeys.removeAll(deleteKeys);
		for (String key : deleteKeys) {
			deleteMovieAttribute(movie.getId(), key);
		}
		for (String key : addKeys) {
			addMovieAttribute(movie.getId(), key, movie.getAttributes().get(key));
		}
		for (String key : updateKeys) {
			updateMovieAttribute(movie.getId(), key, movie.getAttributes().get(key));
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
	
	public Map<String, String> getMovieAttributes(int id) {
		final String sql = "SELECT attribute_name, attribute_value FROM movie_attributes WHERE movie_id = ?";
		final Map<String, String> movieAttributes = new HashMap<String, String>();
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, id);
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				movieAttributes.put(rs.getString(1), rs.getString(2));
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
		final String sql = "DELETE FROM movie_attributes WHERE movie_id = ? AND attribute_name = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, id);
				ps.setString(2, key);
			}
		});		
	}
	
	public void updateMovieAttribute(int id, String key, String value) {
		final String sql = "UPDATE movie_attributes SET attribute_value = ? WHERE movie_id = ? AND attribute_name = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, value);
				ps.setInt(2, id);
				ps.setString(3, key);
			}			
		});
	}
}
