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
import org.xandercat.pmdb.exception.PmdbException;
import org.xandercat.pmdb.exception.ServiceLimitExceededException;
import org.xandercat.pmdb.form.useradmin.UserForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.UserService;
import org.xandercat.pmdb.util.Alerts;
import org.xandercat.pmdb.util.ViewUtil;
import org.xandercat.pmdb.util.format.FormatUtil;

/**
 * Controller for basic authentication and registration functions.
 * 
 * @author Scott Arnold
 */
@Controller
public class AuthenticationController {

	private static final Logger LOGGER = LogManager.getLogger(AuthenticationController.class);
	
	@Value("${pmdb.environment.name}")
	private String environment;

	@Autowired
	private UserService userService;
	
	@Autowired
	private CollectionService collectionService;
	
	/**
	 * Page for presenting user login.
	 * 
	 * @param model  model
	 * 
	 * @return page for presenting user login
	 */
	@RequestMapping(value="/login.html", method=RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("environment", environment);
		return "authentication/login";
	}
	
	/**
	 * Page for presenting user login error.
	 * 
	 * @param model  model
	 * 
	 * @return page for presenting user login error
	 */
	@RequestMapping("/login-error.html")
	public String loginError(Model model) {
		model.addAttribute("loginError", "Unable to login.");
		model.addAttribute("environment", environment);
		return "authentication/login";
	}
	
	/**
	 * Process user login and redirect to user home page. Prepares session for user and updates user login information.
	 * 
	 * @param session  session
	 * 
	 * @return redirect to user home page
	 */
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
	
	/**
	 * Page for presenting new user registration.
	 * 
	 * @param model  model
	 * 
	 * @return page for presenting new user registration
	 */
	@RequestMapping(value="/loginRegister", method=RequestMethod.GET)
	public String register(Model model) {
		model.addAttribute("userForm", new UserForm());
		return "authentication/register";
	}
	
	/**
	 * Process new user registration and present registration result.
	 * 
	 * @param model     model
	 * @param userForm  user information form
	 * @param result    binding result
	 * 
	 * @return registration result page
	 */
	@RequestMapping(value="/loginRegisterSubmit", method=RequestMethod.POST)
	public String registerSubmit(Model model,
			@ModelAttribute("userForm") @Valid UserForm userForm,
			BindingResult result) {
		// password annotation ignores blank passwords, so need to check for that here
		if (StringUtils.isEmptyOrWhitespace(userForm.getPasswordPair().getFirst())) {
			result.rejectValue("passwordPair", "{userform.password.required}", "Password cannot be blank.");
		}
		if (FormatUtil.isValidUsername(userForm.getUsername()) && userService.getUser(userForm.getUsername()).isPresent()) {
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
			Alerts.setErrorMessage(model, "The system was unable to register your account.");
		} catch (ServiceLimitExceededException slee) {
			if (slee.isInitialTrigger()) {
				LOGGER.error("Excessive registrations.", slee);
			}
			Alerts.setErrorMessage(model, "Too many user accounts have been created in a short amount of time.  Please try again later.");
		}
		return "authentication/registerResult";
	}
}
