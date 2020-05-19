package org.xandercat.pmdb.dao.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dao.CollectionDaoCrudOps;
import org.xandercat.pmdb.dto.MovieCollection;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

@Component(value="awsCollectionDao")
public class AwsCollectionDaoImpl implements CollectionDaoCrudOps {

	@Autowired
	private DynamoDBMapper ddbm;
	
	@Override
	public void addMovieCollection(MovieCollection movieCollection) {
		ddbm.save(movieCollection);
	}

	@Override
	public void updateMovieCollection(MovieCollection movieCollection) {
		ddbm.save(movieCollection);
	}

	@Override
	public void deleteMovieCollection(String collectionId) {
		MovieCollection movieCollection = ddbm.load(MovieCollection.class, collectionId);
		ddbm.delete(movieCollection);
	}
}
