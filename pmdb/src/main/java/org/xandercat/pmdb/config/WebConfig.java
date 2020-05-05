package org.xandercat.pmdb.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages={"org.xandercat.pmdb.controller"})
public class WebConfig implements WebMvcConfigurer {

	private static final Logger LOGGER = LogManager.getLogger(WebConfig.class);
	
	public void addViewControllers(ViewControllerRegistry registry) {
		LOGGER.warn("WebConfig addViewControllers called");
		registry.addViewController("/").setViewName("index");
	}

	public void configureViewResolvers(ViewResolverRegistry registry) {
		LOGGER.warn("WebConfig configureViewResolvers called");
		registry.jsp();  // registers JSP with default prefix "/WEB-INF/" and default suffix ".jsp"
	}
}
