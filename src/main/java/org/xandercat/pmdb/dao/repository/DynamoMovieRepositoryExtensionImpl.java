package org.xandercat.pmdb.dao.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.socialsignin.spring.data.dynamodb.core.DynamoDBTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.util.MovieTitleComparator;
import org.xandercat.pmdb.util.format.FormatUtil;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class DynamoMovieRepositoryExtensionImpl implements DynamoMovieRepositoryExtension {

	@Autowired
	private DynamoDBTemplate dynamoDBTemplate;
	
	private Set<Movie> getMoviesForCollection(String collectionId) {
		Map<String, AttributeValue> parms = new HashMap<String, AttributeValue>();
		parms.put(":collectionId", new AttributeValue().withS(collectionId));
		DynamoDBQueryExpression<Movie> queryExpression = new DynamoDBQueryExpression<Movie>()
				.withIndexName("idx_global_movie_collection_id")
				.withConsistentRead(false)
				.withKeyConditionExpression("collectionId = :collectionId") 
				.withExpressionAttributeValues(parms);
		PaginatedQueryList<Movie> queryList = dynamoDBTemplate.query(Movie.class, queryExpression);
		return queryList.stream().collect(Collectors.toSet());
	}
	
	@Override
	public Set<Movie> searchMoviesForCollection(String collectionId, String searchString) {
		Set<Movie> movies = getMoviesForCollection(collectionId);
		final String lcSearchString = searchString.toLowerCase();
		return movies.stream().filter(movie -> {
			if (movie.getTitle().toLowerCase().indexOf(lcSearchString) >= 0) return true;
			return movie.getAttributes().values().stream()
					.filter(value -> value.toLowerCase().indexOf(lcSearchString) >= 0)
					.count() > 0;
		}).collect(Collectors.toSet());
	}

	@Override
	public List<String> getAttributeKeysForCollection(String collectionId) {
		Set<Movie> movies = getMoviesForCollection(collectionId);
		return movies.stream()
				.flatMap(movie -> movie.getAttributes().keySet().stream())
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}

	@Override
	public Set<String> getAttributeValuesForCollection(String collectionId, String attributeName) {
		Map<String, AttributeValue> parms = new HashMap<String, AttributeValue>();
		parms.put(":collectionId", new AttributeValue().withS(collectionId));
		DynamoDBQueryExpression<Movie> queryExpression = new DynamoDBQueryExpression<Movie>()
				.withIndexName("idx_global_movie_collection_id")
				.withConsistentRead(false)
				.withKeyConditionExpression("collectionId = :collectionId") // collectionId is global secondary index
				.withFilterExpression("attribute_exists(" + FormatUtil.convertToDynamoKey(attributeName) + ")")
				.withExpressionAttributeValues(parms);
		PaginatedQueryList<Movie> queryList = dynamoDBTemplate.query(Movie.class, queryExpression);
		return queryList.stream()
				.map(movie -> movie.getAttribute(attributeName))
				.collect(Collectors.toSet());
	}

	@Override
	public List<Movie> getMoviesWithoutAttribute(String collectionId, String attributeKey) {
		return getMoviesForCollection(collectionId).stream()
				.filter(movie -> FormatUtil.isBlank(movie.getAttribute(attributeKey)))
				.sorted(new MovieTitleComparator())
				.collect(Collectors.toList());
	}
}
