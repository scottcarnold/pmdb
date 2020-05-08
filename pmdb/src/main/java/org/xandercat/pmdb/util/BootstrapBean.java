package org.xandercat.pmdb.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.config.PmdbGrantedAuthority;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.service.UserService;

/**
 * Convenience bean for adding an initial admin account on a new install where no users exist.
 * 
 * @author Scott Arnold
 */
@Component
public class BootstrapBean implements InitializingBean {

	private static final Logger LOGGER = LogManager.getLogger(BootstrapBean.class);
	
	@Autowired
	private UserService userService;
	
	@Value("${pmdb.bootstrap.enable:false}")
	private boolean enableBootstrap;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (enableBootstrap) {
			LOGGER.info("Bootstrap of administrator account is enabled.");
			int userCount = userService.getUserCount();
			if (userCount == 0) {
				PmdbUser user = new PmdbUser("admin");
				user.setFirstName("Administrator");
				user.setEnabled(true);
				user.setGrantedAuthorities(PmdbGrantedAuthority.ROLE_ADMIN, PmdbGrantedAuthority.ROLE_USER);
				userService.addUser(user, "password");
				LOGGER.info("Bootstrap created administrator account: " + user.getUsername());
			}
		}
	}

}
