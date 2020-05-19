package org.xandercat.pmdb.dao.aws;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dao.MovieDao;
import org.xandercat.pmdb.dto.Movie;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper.FailedBatch;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

@Component(value="awsMovieDao")
public class AwsMovieDaoImpl implements MovieDao {

	private static final Logger LOGGER = LogManager.getLogger(AwsMovieDaoImpl.class);
	
	@Autowired
	private DynamoDBMapper ddbm;
	
	@Override
	public void deleteMoviesForCollection(String collectionId) {
		//ddbm.d
		//TODO: implement deleteMoviesForCollection
	}

	@Override
	public Set<Movie> getMoviesForCollection(String collectionId) {
		Map<String, AttributeValue> parms = new HashMap<String, AttributeValue>();
		parms.put(":collectionId", new AttributeValue().withS(collectionId));
		DynamoDBQueryExpression<Movie> queryExpression = new DynamoDBQueryExpression<Movie>()
				.withFilterExpression("collectionId = :collectionId")
				.withExpressionAttributeValues(parms);
		PaginatedQueryList<Movie> queryList = ddbm.query(Movie.class, queryExpression);
		Set<Movie> movies = new HashSet<Movie>();
		movies.addAll(queryList);
		return movies;
	}

	@Override
	public Set<Movie> searchMoviesForCollection(String collectionId, String searchString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Movie getMovie(String id) {
		return ddbm.load(Movie.class, id);
	}

	@Override
	public void addMovie(Movie movie) {
		ddbm.save(movie);
	}

	@Override
	public void addMovies(Collection<Movie> movies) {
		List<FailedBatch> failedBatches = ddbm.batchSave(movies);
		if (failedBatches != null && failedBatches.size() > 0) {
			for (FailedBatch failedBatch : failedBatches) {
				if (failedBatch.getException() != null) {
					LOGGER.error("Failed batch on addMovies.", failedBatch.getException());
				} else {
					LOGGER.error("Failed batch on addMovies.  No exception present.");
				}
				if (failedBatch.getUnprocessedItems() != null && failedBatch.getUnprocessedItems().size() > 0) {
					LOGGER.warn("There are " + failedBatch.getUnprocessedItems().size() + " unprocessed items.  How should we handle these?");
				}
			}
		}
	}

	@Override
	public void updateMovie(Movie movie) {
		ddbm.save(movie);
	}

	@Override
	public void deleteMovie(String id) {
		Movie movie = ddbm.load(Movie.class, id);
		ddbm.delete(movie);
	}

	@Override
	public List<String> getTableColumnPreferences(String username) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTableColumnPreference(String attributeName, String username) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void reorderTableColumnPreference(int sourceIdx, int targetIdx, String username) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void deleteTableColumnPreference(int sourceIdx, String username) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public Integer getMaxTableColumnPreferenceIndex(String username) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getAttributeKeysForCollection(String collectionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAttributeValuesForCollection(String collectionId, String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}
}
