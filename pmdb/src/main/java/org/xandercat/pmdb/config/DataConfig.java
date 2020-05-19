package org.xandercat.pmdb.config;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.xandercat.pmdb.dao.KeyGenerator;
import org.xandercat.pmdb.dao.RandomKeyGenerator;
import org.xandercat.pmdb.util.PmdbAwsCredentialsProvider;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

@Configuration
@EnableTransactionManagement
public class DataConfig {

	private static final Logger LOGGER = LogManager.getLogger(DataConfig.class);
	
	@Bean
	public JndiObjectFactoryBean dataSource(@Value("${datasource.jndi.name}") String dataSourceJndiName) {
		LOGGER.info("Getting DataSource as JNDI resource with JNDI name: " + dataSourceJndiName);
		JndiObjectFactoryBean jndiBean = new JndiObjectFactoryBean();
		jndiBean.setJndiName(dataSourceJndiName);
		jndiBean.setProxyInterface(DataSource.class);
		return jndiBean;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	@Bean
	public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
  
	@Bean
	public KeyGenerator keyGenerator() {
		return new RandomKeyGenerator();
	}
	
	@Bean
	public AWSCredentialsProvider awsCredentialsProvider(
			@Value("${aws.key.id}") String accessKeyId, 
			@Value("${aws.secret.key}") String secretAccessKey) throws Exception {
		return new PmdbAwsCredentialsProvider(accessKeyId, secretAccessKey);
	}
	
	@Bean
	public AmazonDynamoDB amazonDynamoDB(AWSCredentialsProvider awsCredentialsProvider) {
		return AmazonDynamoDBClientBuilder.standard()
			.withCredentials(awsCredentialsProvider)
			.withRegion(Regions.US_EAST_2.getName())
			.build();
	}
	
	@Bean
	public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
		return new DynamoDBMapper(amazonDynamoDB);
	}
	
//	AWS SDK V2.x beans; not using 2.x due to lack of finished Spring integration
//	@Bean
//	public DynamoDbClient dynamoDbClient(AwsCredentialsProvider awsCredentialsProvider) {
//		return DynamoDbClient.builder()
//				.region(Region.US_EAST_2)
//				.credentialsProvider(awsCredentialsProvider)
//				.build();
//	}
}
