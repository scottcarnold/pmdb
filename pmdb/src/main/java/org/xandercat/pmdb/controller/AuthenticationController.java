package org.xandercat.pmdb.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.exception.BandwidthException;
import org.xandercat.pmdb.exception.PmdbException;
import org.xandercat.pmdb.form.useradmin.UserForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.UserService;
import org.xandercat.pmdb.util.ViewUtil;
import org.xandercat.pmdb.util.format.FormatUtil;

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
	
	@RequestMapping(value="/loginRegister", method=RequestMethod.GET)
	public String register(Model model) {
		model.addAttribute("userForm", new UserForm());
		return "authentication/register";
	}
	
	@RequestMapping(value="/loginRegisterSubmit", method=RequestMethod.POST)
	public String registerSubmit(Model model,
			@ModelAttribute("userForm") @Valid UserForm userForm,
			BindingResult result) {
		// password annotation ignores blank passwords, so need to check for that here
		if (StringUtils.isEmptyOrWhitespace(userForm.getPasswordPair().getFirst())) {
			result.rejectValue("passwordPair", "{userform.password.required}", "Password cannot be blank.");
		}
		if (FormatUtil.isValidUsername(userForm.getUsername()) && userService.getUser(userForm.getUsername()) != null) {
			result.rejectValue("username", "{userform.username.alreadyexists}", "A user with that username already exists.");
		}
		if (result.hasErrors()) {
			return "authentication/register";
		}
		PmdbUser user = userForm.toUser();
		try {
			userService.registerUser(user, userForm.getPasswordPair().getFirst().trim());
			model.addAttribute("registrationSuccess", Boolean.TRUE);
		} catch (PmdbException e) {
			LOGGER.error("Unable to register user.", e);
			ViewUtil.setErrorMessage(model, "The system was unable to register your account.");
		} catch (BandwidthException be) {
			if (be.isInitialTrigger()) {
				LOGGER.error("Excessive registrations.", be);
			}
			ViewUtil.setErrorMessage(model, "Too many user accounts have been created in a short amount of time.  Please try again later.");
		}
		return "authentication/registerResult";
	}
}
