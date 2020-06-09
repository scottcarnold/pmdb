package org.xandercat.pmdb.config;

import org.socialsignin.spring.data.dynamodb.config.AbstractDynamoDBConfiguration;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBTemplate;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xandercat.pmdb.util.PmdbAwsCredentialsProvider;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

/**
 * Configuration specific to AWS DynamoDB.
 * 
 * @author Scott Arnold
 */
@Configuration
@EnableDynamoDBRepositories("org.xandercat.pmdb.dao.repository")  // similar to @EnableJpaRepositories, defines where CRUD repo interfaces are
public class DynamoDBConfig extends AbstractDynamoDBConfiguration {

	@Autowired
	private ApplicationContext applicationContext;
	
	@Override
	public AmazonDynamoDB amazonDynamoDB() {
		return applicationContext.getBean(AmazonDynamoDB.class);
	}

	@Override
	public AWSCredentials amazonAWSCredentials() {
		return applicationContext.getBean(AWSCredentialsProvider.class).getCredentials();
	}

	@Override
	protected String[] getMappingBasePackages() {
		return new String[] { "org.xandercat.pmdb.dto" }; // defines where spring-data-dynamodb annotated classes are
	}

	/**
	 * Bean for providing AWS credentials.
	 * 
	 * @param accessKeyId      AWS access key id
	 * @param secretAccessKey  AWS secret access key
	 * 
	 * @return AWS credentials
	 * @throws Exception if AWS credentials provider cannot be created
	 */
	@Bean
	public AWSCredentialsProvider awsCredentialsProvider(
			@Value("${aws.key.id}") String accessKeyId, 
			@Value("${aws.secret.key}") String secretAccessKey) throws Exception {
		return new PmdbAwsCredentialsProvider(accessKeyId, secretAccessKey);
	}
	
	/**
	 * DynamoDB client.
	 * 
	 * @param awsCredentialsProvider  AWS credentials provider
	 * @param region                  AWS region
	 * 
	 * @return DynamoDB client
	 */
	@Bean
	public AmazonDynamoDB amazonDynamoDB(AWSCredentialsProvider awsCredentialsProvider,
			@Value("${aws.region}") String region) {
		return AmazonDynamoDBClientBuilder.standard()
			.withCredentials(awsCredentialsProvider)
			.withRegion(region)
			.build();
	}
	
	/**
	 * DynamoDB template for spring-data-dynamodb queries.
	 * 
	 * @param amazonDynamoDB  DynamoDB client
	 * 
	 * @return DynamoDB template
	 */
	@Bean
	public DynamoDBTemplate dynamoDBTemplate(AmazonDynamoDB amazonDynamoDB) {
		return new DynamoDBTemplate(amazonDynamoDB);
	}

}
