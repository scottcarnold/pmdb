package org.xandercat.pmdb.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class PmdbInitializer implements WebApplicationInitializer {

	private static final Logger LOGGER = LogManager.getLogger(PmdbInitializer.class);
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		LOGGER.warn("PmdbInitializer onStartup called");
		AnnotationConfigWebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext();
		webAppContext.register(WebConfig.class);
		webAppContext.setServletContext(servletContext);
		ServletRegistration.Dynamic appServlet = servletContext.addServlet("dispatcher", new DispatcherServlet(webAppContext));
		appServlet.setLoadOnStartup(1);
		appServlet.addMapping("/");
	}

}
