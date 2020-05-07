package org.xandercat.pmdb.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity(debug=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger LOGGER = LogManager.getLogger(SecurityConfig.class);
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		LOGGER.info("SecurityConfig configure authentication manager builder method called");
		auth.inMemoryAuthentication().withUser("user").password(passwordEncoder().encode("password")).roles("USER");
	}

//	@Override
//	public void configure(WebSecurity web) throws Exception {	
//	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.info("SecurityConfig configure http security method called");
		http.csrf().disable().authorizeRequests()
			.antMatchers("/login*").anonymous()  // note -- you actually can't visit the login pages after logging in with this setup
			.antMatchers("/**").hasRole("USER")
			.and().formLogin()
			.usernameParameter("username")
			.passwordParameter("password")
			.loginPage("/login.html")
			.failureUrl("/login-error.html")
			.loginProcessingUrl("/loginProcess.html")
			.defaultSuccessUrl("/afterLogin.html");
	}

}
