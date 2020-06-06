package org.xandercat.pmdb.config;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.SystemPropertyUtils;
import org.xandercat.pmdb.ws.ClientQueryParamMarshaller;

/**
 * General application configuration not specific to data, security, or web config.
 * 
 * @author Scott Arnold
 */
@Configuration
public class AppConfig {

	/**
	 * Bean that defines where properties are to be loaded from.
	 * 
	 * @return resource for properties
	 */
	@Bean
	public Resource propertiesResource() {
		String propertiesLocation = SystemPropertyUtils.resolvePlaceholders("environment/pmdb_${pmdb.environment}.properties");
		return new ClassPathResource(propertiesLocation);
	}
	
	/**
	 * Bean utilized to fill property placeholders throughout the application.
	 * 
	 * @param propertiesResource resource for properties
	 * 
	 * @return bean for property placeholders
	 */
	@Bean
	public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(Resource propertiesResource) {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		propertySourcesPlaceholderConfigurer.setLocation(propertiesResource);
		return propertySourcesPlaceholderConfigurer;
	}
	
	/**
	 * REST client for application.
	 * 
	 * @return REST client
	 */
	@Bean
	public Client restClient() {
		return ClientBuilder.newBuilder().build();
	}
	
	/**
	 * Bean for mapping request objects to REST GET query parameters.  This is a custom bean of the application and not something regularly
	 * used with REST web services.
	 * 
	 * @return bean for mapping request objects to REST GET query parameters.
	 */
	@Bean
	public ClientQueryParamMarshaller clientQueryParamMarshaller() {
		return new ClientQueryParamMarshaller();
	}
}
