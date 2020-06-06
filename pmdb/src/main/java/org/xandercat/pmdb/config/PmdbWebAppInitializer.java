package org.xandercat.pmdb.config;

import java.io.File;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration.Dynamic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Application initialization.
 * 
 * @author Scott Arnold
 */
public class PmdbWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	private static final Logger LOGGER = LogManager.getLogger(PmdbWebAppInitializer.class);
	
	private File uploadFolder = new File(System.getProperty("java.io.tmpdir"));
	
	@Override
	protected Filter[] getServletFilters() {
		return new Filter[] { new DelegatingFilterProxy("springSecurityFilterChain") };
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		LOGGER.debug("Returning root config classes to framework.");
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

	@Override
	protected void customizeRegistration(Dynamic registration) {
		MultipartConfigElement multipartConfig = new MultipartConfigElement(
				uploadFolder.getAbsolutePath(), 1024*1024*10, 1024*1024*12, 1024*1024*5);
		registration.setMultipartConfig(multipartConfig);
	}
}
