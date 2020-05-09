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
import org.springframework.web.bind.annotation.RequestParam;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.form.useradmin.SearchForm;
import org.xandercat.pmdb.form.useradmin.UserForm;
import org.xandercat.pmdb.service.UserService;
import org.xandercat.pmdb.util.PmdbException;
import org.xandercat.pmdb.util.ViewUtil;

@Controller
public class UserAdminController {

	private static final Logger LOGGER = LogManager.getLogger(UserAdminController.class);
	
	@Autowired
	private UserService userService;
	
	@ModelAttribute("viewTab")
	public String getViewTab() {
		return ViewUtil.TAB_USER_ADMIN;
	}
	
	@GetMapping("/useradmin")
	public String userAdmin(Model model) {
		SearchForm searchForm = new SearchForm();
		model.addAttribute("searchForm", searchForm);
		return "useradmin/useradmin";
	}

	@RequestMapping("/useradmin/search")
	public String userAdminSearch(Model model,
			@ModelAttribute("searchForm") @Valid SearchForm searchForm,
			BindingResult result) {
		if (result.hasErrors()) {
			// anything to do here?
		} else {
			List<PmdbUser> results = userService.searchUsers(searchForm.getSearchString());
			model.addAttribute("results", results);
		}
		return "useradmin/useradmin";
	}
	
	@RequestMapping("/useradmin/addNewUser")
	public String addNewUser(Model model) {
		model.addAttribute("userForm", new UserForm());
		return "useradmin/adduser";
	}

	@RequestMapping("/useradmin/editUser")
	public String editUser(Model model, @RequestParam String username) {
		PmdbUser user = userService.getUser(username);
		boolean administrator = userService.isAdministrator(username);
		UserForm userForm = new UserForm(user, administrator);
		model.addAttribute("userForm", userForm);
		return "useradmin/edituser";
	}
	
	@RequestMapping("/useradmin/addNewUserSubmit")
	public String addNewUserSubmit(Model model,
			@ModelAttribute("userForm") @Valid UserForm userForm,
			BindingResult result) {
		if (result.hasErrors()) {
			return "useradmin/adduser";
		}
		try {
			userService.saveUser(userForm, true);
			model.addAttribute("message", "User " + userForm.getUsername() + " saved.");
		} catch (PmdbException e) {
			LOGGER.error("Unexpected error when saving new user.", e);
			model.addAttribute("message", "An unexpected error occurred while attempting to save user. User could not be saved.");
		}
		return userAdmin(model);
	}
	
	@RequestMapping("/useradmin/editUserSubmit")
	public String editUserSubmit(Model model,
			@ModelAttribute("userForm") @Valid UserForm userForm,
			BindingResult result) {
		if (result.hasErrors()) {
			return "useradmin/edituser";
		}
		try {
			userService.saveUser(userForm, false);
			model.addAttribute("message", "User " + userForm.getUsername() + " saved.");
		} catch (PmdbException e) {
			LOGGER.error("Unexpected error when updating user.", e);
			model.addAttribute("message", "An unexpected error occurred while attempting to save user. User could not be saved.");
		}
		return userAdmin(model);
	}
}
