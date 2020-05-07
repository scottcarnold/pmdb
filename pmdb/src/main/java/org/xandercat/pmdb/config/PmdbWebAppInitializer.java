package org.xandercat.pmdb.config;

import javax.servlet.Filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class PmdbWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	private static final Logger LOGGER = LogManager.getLogger(PmdbWebAppInitializer.class);
	
	@Override
	protected Filter[] getServletFilters() {
		return new Filter[] { new DelegatingFilterProxy("springSecurityFilterChain") };
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		LOGGER.info("Returning root config classes to framework.");
		return new Class[] { WebConfig.class, SecurityConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

}
