package org.xandercat.pmdb.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages={"org.xandercat.pmdb.controller"})
public class WebConfig implements WebMvcConfigurer {

	private static final Logger LOGGER = LogManager.getLogger(WebConfig.class);
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		LOGGER.info("WebConfig templateResolver bean creation called");
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(applicationContext);
		templateResolver.setPrefix("/WEB-INF/view/");
		templateResolver.setSuffix(".html");
		return templateResolver;
	}
	
	@Bean
	public SpringTemplateEngine templateEngine(SpringResourceTemplateResolver templateResolver) {
		LOGGER.info("WebConfig templateEngine bean creation called");
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.setEnableSpringELCompiler(true);
		return templateEngine;
	}
	
	public void addViewControllers(ViewControllerRegistry registry) {
		LOGGER.info("WebConfig addViewControllers called");
		registry.addViewController("/").setViewName("index");
	}

	public void configureViewResolvers(ViewResolverRegistry registry) {
		LOGGER.info("WebConfig configureViewResolvers called");
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		SpringTemplateEngine templateEngine = applicationContext.getBean(SpringTemplateEngine.class);
		if (templateEngine == null) {
			LOGGER.warn("TemplateEngine not found in applicationContext");
		}
		viewResolver.setTemplateEngine(templateEngine);
		registry.viewResolver(viewResolver);
	}
}
