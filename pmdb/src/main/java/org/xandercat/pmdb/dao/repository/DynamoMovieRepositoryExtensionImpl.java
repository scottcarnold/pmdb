package org.xandercat.pmdb.dao.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.socialsignin.spring.data.dynamodb.core.DynamoDBTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.xandercat.pmdb.dto.Movie;
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
		Set<Movie> movies = new HashSet<Movie>();
		movies.addAll(queryList);
		return movies;
	}
	
	@Override
	public Set<Movie> searchMoviesForCollection(String collectionId, String searchString) {
		Set<Movie> movies = getMoviesForCollection(collectionId);
		searchString = searchString.toLowerCase();
		for (Iterator<Movie> iter = movies.iterator(); iter.hasNext();) {
			Movie movie = iter.next();
			int found = movie.getTitle().toLowerCase().indexOf(searchString);
			Iterator<String> attrIter = movie.getAttributes().values().iterator();
			while (found < 0 && attrIter.hasNext()) {
				String attrValue = attrIter.next().toLowerCase();
				found = attrValue.indexOf(searchString);
			}
			if (found < 0) {
				iter.remove();
			}
		}
		return movies;
	}

	@Override
	public List<String> getAttributeKeysForCollection(String collectionId) {
		Set<Movie> movies = getMoviesForCollection(collectionId);
		Set<String> attributeKeySet = new HashSet<String>();
		for (Movie movie : movies) {
			attributeKeySet.addAll(movie.getAttributes().keySet());
		}
		List<String> attributeKeys = new ArrayList<String>();
		attributeKeys.addAll(attributeKeySet);
		Collections.sort(attributeKeys);
		return attributeKeys;
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
		Set<String> attributeValues = new HashSet<String>();
		for (Movie movie : queryList) {
			attributeValues.add(movie.getAttribute(attributeName));
		}
		return attributeValues;
	}
}
