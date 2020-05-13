package org.xandercat.pmdb.controller;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.UserService;
import org.xandercat.pmdb.util.ViewUtil;

@Controller
public class AuthenticationController {

	private static final Logger LOGGER = LogManager.getLogger(AuthenticationController.class);
	
	@Value("${pmdb.environment.name}")
	private String environment;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CollectionService collectionService;
	
	@RequestMapping(value="/login.html", method=RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("environment", environment);
		return "authentication/login";
	}
	
	@RequestMapping("/login-error.html")
	public String loginError(Model model) {
		model.addAttribute("loginError", "Unable to login.");
		model.addAttribute("environment", environment);
		return "authentication/login";
	}
	
	@RequestMapping(value="/afterLogin.html", method=RequestMethod.GET)
	public String loginProcess(HttpSession session) {
		UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		PmdbUser user = (PmdbUser) authentication.getPrincipal();
		LOGGER.info("User logged in: " + user.getUsername());
		userService.updateLastAccess(user.getUsername());
		try {
			ViewUtil.updateNumShareOffers(collectionService, session, user.getUsername());
		} catch (Exception e) {
			LOGGER.error("Unable to retrieve number of share offers for user.", e);
		}
		return "redirect:/";
	}
}
