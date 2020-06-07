package org.xandercat.pmdb.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.dto.CloudUserSearchResults;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.form.useradmin.SearchForm;
import org.xandercat.pmdb.form.useradmin.UserForm;
import org.xandercat.pmdb.service.UserService;
import org.xandercat.pmdb.util.Alerts;
import org.xandercat.pmdb.util.ApplicationProperties;
import org.xandercat.pmdb.util.ViewUtil;

/**
 * Controller for administrators to perform user management functions.
 * 
 * @author Scott Arnold
 */
@Controller
public class UserAdminController {

	private static final Logger LOGGER = LogManager.getLogger(UserAdminController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@ModelAttribute("viewTab")
	public String getViewTab() {
		return ViewUtil.TAB_USER_ADMIN;
	}
	
	/**
	 * Page for user administration.
	 * 
	 * @param model  model
	 * 
	 * @return page for user administration
	 */
	@GetMapping("/useradmin")
	public String userAdmin(Model model) {
		SearchForm searchForm = new SearchForm();
		model.addAttribute("searchForm", searchForm);
		return "useradmin/useradmin";
	}

	/**
	 * Search users.
	 * 
	 * @param model       model
	 * @param searchForm  user search form
	 * @param result      binding result
	 * 
	 * @return page for user administration
	 */
	@RequestMapping("/useradmin/search")
	public String userAdminSearch(Model model,
			@ModelAttribute("searchForm") @Valid SearchForm searchForm,
			BindingResult result) {
		if (result.hasErrors()) {
			// anything to do here?
		} else {
			List<PmdbUser> results = userService.searchUsers(searchForm.getSearchString());
			model.addAttribute("results", results);
			model.addAttribute("awsEnabled", applicationProperties.isAwsEnabled());
			if (applicationProperties.isAwsEnabled() && searchForm.isSyncCloud()) {
				CloudUserSearchResults cloudSearchResults = userService.syncCloudUsers(results, searchForm.getSearchString());
				model.addAttribute("usernamesNotInCloud", cloudSearchResults.getUsernamesNotInCloud());
				model.addAttribute("usernamesOnlyInCloud", cloudSearchResults.getUsernamesOnlyInCloud());
			}
		}
		return "useradmin/useradmin";
	}
	
	/**
	 * Page for adding a new user.
	 * 
	 * @param model  model
	 * 
	 * @return page for adding a new user
	 */
	@RequestMapping("/useradmin/addNewUser")
	public String addNewUser(Model model) {
		model.addAttribute("userForm", new UserForm());
		return "useradmin/adduser";
	}

	/**
	 * Page for editing an existing user.
	 * 
	 * @param model     model
	 * @param username  username of user to edit
	 * 
	 * @return page for editing an existing user
	 */
	@RequestMapping("/useradmin/editUser")
	public String editUser(Model model, @RequestParam String username) {
		PmdbUser user = userService.getUser(username).get();
		boolean administrator = userService.isAdministrator(username);
		UserForm userForm = new UserForm(user, administrator);
		model.addAttribute("userForm", userForm);
		return "useradmin/edituser";
	}
	
	/**
	 * Processing adding a new user.
	 * 
	 * @param model     model
	 * @param userForm  user form
	 * @param result    binding result
	 * 
	 * @return page for user administration
	 */
	@RequestMapping("/useradmin/addNewUserSubmit")
	public String addNewUserSubmit(Model model,
			@ModelAttribute("userForm") @Valid UserForm userForm,
			BindingResult result) {
		String newPassword = StringUtils.isEmptyOrWhitespace(userForm.getPasswordPair().getFirst())? null : userForm.getPasswordPair().getFirst().trim();
		if (newPassword == null) {
			// special case; new user must be setting a new password, so blank value is not acceptable
			result.rejectValue("password", "{userform.password.required}", "A password must be provided for new users.");
		}
		if (result.hasErrors()) {
			return "useradmin/adduser";
		}
		try {
			userService.saveUser(userForm.toUser(), newPassword, true);
			Alerts.setMessage(model, "User " + userForm.getUsername() + " saved.");
		} catch (Exception e) {
			LOGGER.error("Unexpected error when saving new user.", e);
			Alerts.setErrorMessage(model, "An unexpected error occurred while attempting to save user. User could not be saved.");
		}
		return userAdmin(model);
	}
	
	/**
	 * Process editing user details for a user.
	 * 
	 * @param model     model
	 * @param userForm  user form
	 * @param result    binding result
	 * 
	 * @return page for user administration
	 */
	@RequestMapping("/useradmin/editUserSubmit")
	public String editUserSubmit(Model model,
			@ModelAttribute("userForm") @Valid UserForm userForm,
			BindingResult result) {
		if (result.hasErrors()) {
			return "useradmin/edituser";
		}
		String newPassword = StringUtils.isEmptyOrWhitespace(userForm.getPasswordPair().getFirst())? null : userForm.getPasswordPair().getFirst().trim();
		try {
			userService.saveUser(userForm.toUser(), newPassword, false);
			Alerts.setMessage(model, "User " + userForm.getUsername() + " saved.");
		} catch (Exception e) {
			LOGGER.error("Unexpected error when updating user.", e);
			Alerts.setErrorMessage(model, "An unexpected error occurred while attempting to save user. User could not be saved.");
		}
		return userAdmin(model);
	}

	/**
	 * Process of copying user credentials from local database to the cloud.
	 * 
	 * @param model     model
	 * @param username  username to sync
	 * 
	 * @return page for user administration
	 */
	@RequestMapping("/useradmin/syncUserToCloud")
	public String syncUserToCloud(Model model, @RequestParam String username) {
		try {
			userService.syncUserToCloud(username);
			Alerts.setMessage(model, "User " + username + " synced to cloud.");
		} catch (WebServicesException e) {
			LOGGER.error("Unable to sync user to cloud.", e);
			Alerts.setErrorMessage(model, "User " + username + " could not be synced to cloud.");
		}
		return userAdmin(model);
	}
	
	/**
	 * Process of copying user credentials from cloud to local database.  User will be in a disabled state
	 * after copy down and will need to have details re-added to their account.
	 * 
	 * @param model     model
	 * @param username  user to sync
	 * 
	 * @return page for user administration
	 */
	@RequestMapping("/useradmin/syncUserFromCloud")
	public String syncUserFromCloud(Model model, @RequestParam String username) {
		try {
			userService.syncUserFromCloud(username);
			Alerts.setMessage(model, "User " + username + " synced from cloud. User will need to be edited to enable user and add user details.");
		} catch (Exception e) {
			LOGGER.error("Unable to sync user from cloud.", e);
			Alerts.setErrorMessage(model, "User " + username + " could not be synced from cloud.");
		}
		return userAdmin(model);
	}
	
	/**
	 * Delete a user.  User can only be deleted if they have never logged in before.  If administrator needs to remove access for a user
	 * that has previously logged in, the user account should just be disabled.
	 * 
	 * @param model     model
	 * @param username  username of user to delete
	 * 
	 * @return page for user administration
	 */
	@RequestMapping(value="/useradmin/deleteUser", method=RequestMethod.POST)
	public String deleteUser(Model model, @RequestParam String username) {
		try {
			userService.deleteUser(username);
			Alerts.setMessage(model, "User deleted.");
		} catch (Exception e) {
			LOGGER.error("User could not be deleted.", e);
			Alerts.setErrorMessage(model, "User could not be deleted.");
		}
		return userAdmin(model);
	}
}
