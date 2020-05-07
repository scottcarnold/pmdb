package org.xandercat.pmdb.config;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
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
}
