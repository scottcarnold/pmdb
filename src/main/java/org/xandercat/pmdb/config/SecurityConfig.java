package org.xandercat.pmdb.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application security configuration.
 * 
 * @author Scott Arnold
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger LOGGER = LogManager.getLogger(SecurityConfig.class);
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	/**
	 * Password encoder for user passwords.
	 * 
	 * @return password encoder for user passwords
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		LOGGER.info("SecurityConfig configure authentication manager builder method called");
		//auth.inMemoryAuthentication().withUser("user").password(passwordEncoder().encode("password")).roles("USER");
		auth.userDetailsService(userDetailsService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.info("SecurityConfig configure http security method called");
		http
			.authorizeRequests()
				.antMatchers("/login*").permitAll()
				.antMatchers("/css/**").permitAll()
				.antMatchers("/fonts/**").permitAll()
				.antMatchers("/js/**").permitAll()
				.antMatchers("/public/**").permitAll()
				.antMatchers("/useradmin/**").hasRole("ADMIN")
				.antMatchers("/**").hasRole("USER")
			.and().logout()
				.logoutSuccessUrl("/login.html") // after logout, go back to login
			.and().formLogin()
				.usernameParameter("username")
				.passwordParameter("password")
				.loginPage("/login.html")
				.failureUrl("/login-error.html")
				.loginProcessingUrl("/loginProcess.html")
				.defaultSuccessUrl("/afterLogin.html", true)
			.and().requiresChannel()
				.antMatchers("/**").requiresSecure(); // force everything to be HTTPS -- side note: csrf token validation fails if not over HTTPS but csrf can be disabled if needed
	}

}
