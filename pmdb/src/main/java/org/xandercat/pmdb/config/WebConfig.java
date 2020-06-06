package org.xandercat.pmdb.config;

import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import com.fasterxml.jackson.databind.ObjectMapper;

import nz.net.ultraq.thymeleaf.LayoutDialect;

/**
 * Application web configuration.
 * 
 * @author Scott Arnold
 */
@EnableWebMvc
@Configuration
@ComponentScan(basePackages={"org.xandercat.pmdb"})
public class WebConfig implements WebMvcConfigurer {

	private static final Logger LOGGER = LogManager.getLogger(WebConfig.class);
	
	@Autowired
	private ApplicationContext applicationContext;
	
	/**
	 * LocaleResolver for i18n support.
	 * 
	 * @return LocaleResolver
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.US);
		return localeResolver;
	}
	
	/**
	 * Interceptor for locale change and i18n support.  Defines parameter that can be used to specify what language to use.
	 * 
	 * @return interceptor for locale change
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;
	}
	
	/**
	 * Source for application messages for i18n support.
	 * 
	 * @return message source for application messages
	 */
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setUseCodeAsDefaultMessage(true); // useful for test / debugging
		return messageSource;
	}
	
	/**
	 * Template resolver for view.  Constructs Thymeleaf resolver, defines where view files are located,
	 * and what extension they use.
	 * 
	 * @return template resolver
	 */
	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		LOGGER.debug("WebConfig templateResolver bean creation called");
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(applicationContext);
		templateResolver.setTemplateMode(TemplateMode.HTML); // should be default; just being more explicit here
		templateResolver.setPrefix("/WEB-INF/view/");
		templateResolver.setSuffix(".html");
		return templateResolver;
	}
	
	/**
	 * Template engine for view.  Constructs and configures Thymeleaf template engine.
	 * 
	 * @param templateResolver Thymeleaf template resolver
	 * @return
	 */
	@Bean
	public SpringTemplateEngine templateEngine(SpringResourceTemplateResolver templateResolver) {
		LOGGER.debug("WebConfig templateEngine bean creation called");
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.addDialect(new LayoutDialect()); // for Thymeleaf hierarchical layout
		templateEngine.addDialect(new SpringSecurityDialect());  // for Thymeleaf security extras
		templateEngine.setEnableSpringELCompiler(true);
		return templateEngine;
	}
	
	/**
	 * Bean for handling file uploads.
	 * 
	 * @return multipart resolver
	 */
	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver(); // for file upload support
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		LOGGER.debug("WebConfig addViewControllers called");
		//registry.addViewController("/").setViewName("home");  // can essentially set up noop controllers with this if desired
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		LOGGER.debug("WebConfig configureViewResolvers called");
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		SpringTemplateEngine templateEngine = applicationContext.getBean(SpringTemplateEngine.class);
		if (templateEngine == null) {
			LOGGER.warn("TemplateEngine not found in applicationContext");
		}
		viewResolver.setTemplateEngine(templateEngine);
		registry.viewResolver(viewResolver);
	}
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		ObjectMapper objectMapper = new ObjectMapper();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper); // for JSON responses
		converters.add(converter);
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LocaleChangeInterceptor localeChangeInterceptor = applicationContext.getBean(LocaleChangeInterceptor.class);
		registry.addInterceptor(localeChangeInterceptor);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler("/css/**", "/js/**", "/fonts/**")
			.addResourceLocations("/WEB-INF/css/", "/WEB-INF/js/", "/WEB-INF/fonts/");
	}
	
}
