package org.xandercat.pmdb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
		//TODO: Also load the attributes
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
		//TODO: Also load the attributes
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
		//TODO: Also load the attributes
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
		//TODO: Also save any attributes
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
		// TODO also update any attributes
	}

	@Override
	public void deleteMovie(int id) {
		final String sql = "DELETE FROM movie WHERE id = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, id);
			}
		});
	}
}
