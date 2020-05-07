package org.xandercat.pmdb.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AuthenticationController {

	private static final Logger LOGGER = LogManager.getLogger(AuthenticationController.class);
	
	@RequestMapping(value="/login.html", method=RequestMethod.GET)
	public String login() {
		return "authentication/login";
	}
	
	@RequestMapping("/login-error.html")
	public String loginError(Model model) {
		model.addAttribute("loginError", "Unable to login.");
		return "authentication/login";
	}
	
	@RequestMapping(value="/afterLogin.html", method=RequestMethod.GET)
	public String loginProcess() {
		UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		LOGGER.info("User logged in: " + user.getUsername());
		return "redirect:/home";
	}
}
