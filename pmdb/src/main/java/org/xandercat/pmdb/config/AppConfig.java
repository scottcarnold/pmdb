package org.xandercat.pmdb.config;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.SystemPropertyUtils;

@Configuration
public class AppConfig {

	@Bean
	public Resource propertiesResource() {
		String propertiesLocation = SystemPropertyUtils.resolvePlaceholders("environment/pmdb_${pmdb.environment}.properties");
		return new ClassPathResource(propertiesLocation);
	}
	
	@Bean
	public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(Resource propertiesResource) {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		propertySourcesPlaceholderConfigurer.setLocation(propertiesResource);
		return propertySourcesPlaceholderConfigurer;
	}
	
	@Bean
	public Client restClient() {
		return ClientBuilder.newBuilder().build();
	}
}
