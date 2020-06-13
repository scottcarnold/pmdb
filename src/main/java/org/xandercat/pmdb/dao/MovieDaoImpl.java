package org.xandercat.pmdb.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.util.MovieTitleComparator;
import org.xandercat.pmdb.util.format.FormatUtil;

@Component
public class MovieDaoImpl implements MovieDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private KeyGenerator keyGenerator;
	
	@Override
	public void deleteMoviesForCollection(String collectionId) {
		// note: relying on cascade delete for movie attributes
		final String sql = "DELETE FROM movie WHERE collection_id = ?";
		jdbcTemplate.update(sql, ps -> ps.setString(1, collectionId)); 
	}

	@Override
	@Transactional
	public Set<Movie> getMoviesForCollection(String collectionId) {
		final String sql = "SELECT movie.id, movie.title, attribute_name, attribute_value FROM movie "
				+ " LEFT JOIN movie_attributes on movie.id = movie_attributes.movie_id"
				+ " WHERE movie.collection_id = ? ORDER BY movie.id";
		final Map<String, Movie> movies = new HashMap<String, Movie>();
		jdbcTemplate.query(sql, ps -> ps.setString(1, collectionId), rs -> {
			Movie movie = movies.get(rs.getString(1));
			if (movie == null) {
				movie = new Movie();
				movie.setId(rs.getString(1));
				movie.setTitle(rs.getString(2));
				movies.put(movie.getId(), movie);
			}
			if (FormatUtil.isNotBlank(rs.getString(3))) { // for movies with 0 attributes found on left join
				movie.addAttribute(rs.getString(3), rs.getString(4));
			}
		});
		return movies.values().stream().collect(Collectors.toSet());
	}	
	
	@Override
	@Transactional
	public Set<Movie> searchMoviesForCollection(String collectionId, String searchString) {
		final String lcSearchString = searchString.trim().toLowerCase();
		final String sql = "SELECT movie.id, movie.title, movie.collection_id FROM movie "
				+ " LEFT JOIN movie_attributes ON movie.id = movie_attributes.movie_id"
				+ " WHERE collection_id = ? "
				+ " AND (LOWER(title) like ?"
				+ " OR LOWER(attribute_value) like ?)"
				+ " ORDER BY movie.id";
		final Set<Movie> movies = new HashSet<Movie>();
		jdbcTemplate.query(sql, ps -> {
			ps.setString(1, collectionId);
			ps.setString(2, "%" + lcSearchString + "%");
			ps.setString(3, "%" + lcSearchString + "%");
		}, rs -> {
			Movie movie = new Movie();
			movie.setId(rs.getString(1));
			movie.setTitle(rs.getString(2));
			movie.setCollectionId(rs.getString(3));
			movie.setAttributes(getMovieAttributes(movie.getId()));
			movies.add(movie);
		});
		return movies;
	}

	@Override
	public Optional<Movie> getMovie(String id) {
		final String sql = "SELECT id, title, collection_id FROM movie WHERE id = ?";
		final List<Movie> movies = new ArrayList<Movie>();
		jdbcTemplate.query(sql, ps -> ps.setString(1, id), rs -> {
			Movie movie = new Movie();
			movie.setId(rs.getString(1));
			movie.setTitle(rs.getString(2));
			movie.setCollectionId(rs.getString(3));
			movie.setAttributes(getMovieAttributes(movie.getId()));
			movies.add(movie);
		});
		return movies.stream().findAny();
	}

	@Override
	@Transactional
	public void addMovie(Movie movie) {
		addMovieInternal(movie);
	}
	
	@Override
	@Transactional
	public void addMovies(Collection<Movie> movies) {
		for (Movie movie : movies) {
			addMovieInternal(movie);
		}
	}
	
	private void addMovieInternal(Movie movie) {
		final String sql = "INSERT INTO movie(id, title, collection_id) VALUES (?, ?, ?)";
		movie.setId(keyGenerator.getKey());
		jdbcTemplate.update(sql, ps -> {
			ps.setString(1, movie.getId());
			ps.setString(2, movie.getTitle());
			ps.setString(3, movie.getCollectionId());			
		});
		movie.getAttributes().forEach( (attrKey, value) -> addMovieAttribute(movie.getId(), attrKey, value));
	}
	
	@Override
	@Transactional
	public void updateMovie(Movie movie) {
		final String sql = "UPDATE movie SET title = ? WHERE id = ?";
		jdbcTemplate.update(sql, ps -> {
			ps.setString(1, movie.getTitle());
			ps.setString(2, movie.getId());			
		});
		
		// do some set logic to figure out attributes
		Set<String> oldKeys = getMovieAttributes(movie.getId()).keySet();
		Set<String> newKeys = movie.getAttributes().keySet();
		
		Set<String> deleteKeys = oldKeys.stream()
				.filter(oldKey -> !newKeys.contains(oldKey))
				.collect(Collectors.toSet());
		
		Set<String> addKeys = newKeys.stream()
				.filter(newKey -> !oldKeys.contains(newKey))
				.collect(Collectors.toSet());
		
		Set<String> updateKeys = oldKeys.stream()
				.filter(oldKey -> !deleteKeys.contains(oldKey))
				.collect(Collectors.toSet());
		
		deleteKeys.forEach(key -> deleteMovieAttribute(movie.getId(), key));
		addKeys.forEach(key -> addMovieAttribute(movie.getId(), key, movie.getAttribute(key)));	
		updateKeys.forEach(key -> updateMovieAttribute(movie.getId(), key, movie.getAttribute(key)));
	}

	@Override
	public void deleteMovie(String id) {
		// note: relying on cascade delete for movie attributes
		final String sql = "DELETE FROM movie WHERE id = ?";
		jdbcTemplate.update(sql, ps -> ps.setString(1, id));
	}
	
	private Map<String, String> getMovieAttributes(String id) {
		final String sql = "SELECT attribute_name, attribute_value FROM movie_attributes WHERE movie_id = ?";
		final Map<String, String> movieAttributes = new HashMap<String, String>();
		jdbcTemplate.query(sql, ps -> ps.setString(1, id), rs -> { movieAttributes.put(rs.getString(1), rs.getString(2)); });
		return movieAttributes;
	}
	
	private void addMovieAttribute(String id, String key, String value) {
		if (!FormatUtil.isAlphaNumeric(key, false)) {
			// due to need to pass keys around in templates and elsewhere, make life easier by enforcing no special characters in attribute keys
			throw new IllegalArgumentException("Attribute keys must be alphanumeric, containing only letters, numbers, and spaces.");
		}
		final String sql = "INSERT INTO movie_attributes (movie_id, attribute_name, attribute_value) VALUES (?, ?, ?)";
		jdbcTemplate.update(sql, ps -> {
			ps.setString(1, id);
			ps.setString(2, key);
			ps.setString(3, value);
		});
	}
	
	private void deleteMovieAttribute(String id, String key) {
		final String sql = "DELETE FROM movie_attributes WHERE movie_id = ? AND LOWER(attribute_name) = ?";
		jdbcTemplate.update(sql, ps -> {
			ps.setString(1, id);
			ps.setString(2, key.toLowerCase());
		});		
	}
	
	public void updateMovieAttribute(String id, String key, String value) {
		final String sql = "UPDATE movie_attributes SET attribute_value = ? WHERE movie_id = ? AND LOWER(attribute_name) = ?";
		jdbcTemplate.update(sql, ps -> {
			ps.setString(1, value);
			ps.setString(2, id);
			ps.setString(3, key.toLowerCase());		
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
		jdbcTemplate.query(sql.toString(), ps -> {
			int i=0;
			ps.setString(++i, username);
			if (fromIdx != null) {
				ps.setInt(++i, fromIdx.intValue());
			}
			if (toIdx != null) {
				ps.setInt(++i, toIdx.intValue());
			}			
		}, rs -> {
			tableColumnPreferences.add(rs.getString(1));
		});
		return tableColumnPreferences;
	}
	
	@Override
	public Optional<Integer> getMaxTableColumnPreferenceIndex(String username) {
		final String maxSql = "SELECT MAX(idx) FROM movie_attributes_table_columns WHERE username = ?";
		final List<Integer> max = new ArrayList<Integer>();
		jdbcTemplate.query(maxSql, ps -> ps.setString(1, username), rs -> {
			int maxIdx = rs.getInt(1);
			if (!rs.wasNull()) {
				max.add(Integer.valueOf(maxIdx));
			}
		});
		return max.stream().findAny();
	}

	@Override
	@Transactional
	public void addTableColumnPreference(String attributeName, String username) {
		final String insertSql = "INSERT INTO movie_attributes_table_columns(username, idx, attribute_name) VALUES (?, ?, ?)";
		Optional<Integer> max = getMaxTableColumnPreferenceIndex(username);
		final int nextIdx = max.isPresent()? max.get().intValue() + 1 : 0;
		jdbcTemplate.update(insertSql, ps -> {
			ps.setString(1, username);
			ps.setInt(2, nextIdx);
			ps.setString(3, attributeName);
		});
	}

	@Override
	@Transactional
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
		jdbcTemplate.update(sql, ps -> {
			ps.setInt(1, toIdx);
			ps.setString(2, username);
			ps.setInt(3, fromIdx);
		});
	}
	
	@Override
	@Transactional
	public void deleteTableColumnPreference(int sourceIdx, String username) {
		List<String> shiftPreferences = getTableColumnPreferences(username, sourceIdx+1, null);
		final String sql = "DELETE FROM movie_attributes_table_columns WHERE username = ? AND idx >= ?";
		int rowsAffected = jdbcTemplate.update(sql, ps -> {
			ps.setString(1, username);
			ps.setInt(2, sourceIdx);
		});
		if (rowsAffected > 0) {
			shiftPreferences.forEach(preference -> addTableColumnPreference(preference, username));
		}
	}

	@Override
	public List<String> getAttributeKeysForCollection(String collectionId) {
		final String sql = "SELECT DISTINCT(attribute_name) FROM movie"
				+ " INNER JOIN movie_attributes ON movie.id = movie_attributes.movie_id"
				+ " WHERE movie.collection_id = ?"
				+ " ORDER BY LOWER(attribute_name)";
		final List<String> attributeKeys = new ArrayList<String>();
		jdbcTemplate.query(sql, ps -> ps.setString(1, collectionId), rs -> { attributeKeys.add(rs.getString(1)); });
		return attributeKeys;
	}

	@Override
	public Set<String> getAttributeValuesForCollection(String collectionId, String attributeName) {
		final String sql = "SELECT DISTINCT(attribute_value) FROM movie"
				+ " INNER JOIN movie_attributes ON movie.id = movie_attributes.movie_id"
				+ " WHERE movie.collection_id = ? AND attribute_name = ?";
		final Set<String> attributeValues = new HashSet<String>();
		jdbcTemplate.query(sql, ps -> {
			ps.setString(1, collectionId);
			ps.setString(2, attributeName);
		}, rs -> {
			attributeValues.add(rs.getString(1));
		});
		return attributeValues;
	}

	@Override
	public List<Movie> getMoviesWithoutAttribute(String collectionId, String attributeKey) {
		return getMoviesForCollection(collectionId).stream()
				.filter(movie -> FormatUtil.isBlank(movie.getAttribute(attributeKey)))
				.sorted(new MovieTitleComparator())
				.collect(Collectors.toList());
	}
}
