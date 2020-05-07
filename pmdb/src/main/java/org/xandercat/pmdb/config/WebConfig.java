package org.xandercat.pmdb.config;

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
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages={"org.xandercat.pmdb"})
public class WebConfig implements WebMvcConfigurer {

	private static final Logger LOGGER = LogManager.getLogger(WebConfig.class);
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.US);
		return localeResolver;
	}
	
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;
	}
	
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setUseCodeAsDefaultMessage(true); // useful for test / debugging
		return messageSource;
	}
	
	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		LOGGER.info("WebConfig templateResolver bean creation called");
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(applicationContext);
		templateResolver.setTemplateMode(TemplateMode.HTML); // should be default; just being more explicit here
		templateResolver.setPrefix("/WEB-INF/view/");
		templateResolver.setSuffix(".html");
		return templateResolver;
	}
	
	@Bean
	public SpringTemplateEngine templateEngine(SpringResourceTemplateResolver templateResolver) {
		LOGGER.info("WebConfig templateEngine bean creation called");
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.addDialect(new LayoutDialect()); // for Thymeleaf hierarchical layout
		templateEngine.setEnableSpringELCompiler(true);
		return templateEngine;
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		LOGGER.info("WebConfig addViewControllers called");
		registry.addViewController("/").setViewName("home");  // with nothing else, default to home page; should redirect to login if not logged in
	}

	@Override
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

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LocaleChangeInterceptor localeChangeInterceptor = applicationContext.getBean(LocaleChangeInterceptor.class);
		registry.addInterceptor(localeChangeInterceptor);
	}
	
}
