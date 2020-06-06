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

/**
 * Data configuration for generic data and RDBMS services.
 *  
 * @author Scott Arnold
 */
@Configuration
@EnableTransactionManagement
public class DataConfig {

	private static final Logger LOGGER = LogManager.getLogger(DataConfig.class);
	
	/**
	 * Bean to provide application the JNDI linkage for a data source.
	 * 
	 * @param dataSourceJndiName JNDI name for datasource
	 * 
	 * @return JNDI object factory bean for datasource
	 */
	@Bean
	public JndiObjectFactoryBean dataSource(@Value("${datasource.jndi.name}") String dataSourceJndiName) {
		LOGGER.info("Getting DataSource as JNDI resource with JNDI name: " + dataSourceJndiName);
		JndiObjectFactoryBean jndiBean = new JndiObjectFactoryBean();
		jndiBean.setJndiName(dataSourceJndiName);
		jndiBean.setProxyInterface(DataSource.class);
		return jndiBean;
	}
	
	/**
	 * JDBC template for queries against the datasource.
	 * 
	 * @param dataSource datasource for application
	 * 
	 * @return JDBC template for datasource
	 */
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	/**
	 * Bean for datasource transaction management.
	 * 
	 * @param dataSource datasource for transaction management
	 * 
	 * @return transaction manager for datasource
	 */
	@Bean
	public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
  
	/**
	 * Bean for generating keys for use with both local and cloud datasources.
	 * 
	 * @return bean for generating key values
	 */
	@Bean
	public KeyGenerator keyGenerator() {
		return new RandomKeyGenerator();
	}
}
