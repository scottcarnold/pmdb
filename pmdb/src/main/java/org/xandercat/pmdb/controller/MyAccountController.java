package org.xandercat.pmdb.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.form.useradmin.UserForm;
import org.xandercat.pmdb.service.UserService;
import org.xandercat.pmdb.util.PmdbException;
import org.xandercat.pmdb.util.ViewUtil;

@Controller
public class MyAccountController {

	private static final Logger LOGGER = LogManager.getLogger(MyAccountController.class);
	
	@Autowired
	UserService userService;
	
	@ModelAttribute("viewTab")
	public String getViewTab() {
		return ViewUtil.TAB_MY_ACCOUNT;
	}
	
	@RequestMapping("/myaccount")
	public String editUser(Model model, Principal principal) {
		PmdbUser user = userService.getUser(principal.getName());
		boolean administrator = userService.isAdministrator(principal.getName());
		UserForm userForm = new UserForm(user, administrator);
		model.addAttribute("userForm", userForm);
		return "myaccount/edituser";
	}
	
	@RequestMapping("/myaccount/editUserSubmit")
	public String editUserSubmit(Model model, Principal principal,
			@ModelAttribute("userForm") @Valid UserForm userForm,
			BindingResult result) {
		if (result.hasErrors()) {
			return "myaccount/edituser";
		}
		try {
			userService.saveMyAccountUser(userForm, principal.getName());
			model.addAttribute("message", "Account information saved.");
		} catch (PmdbException e) {
			LOGGER.error("Unexpected error when updating user.", e);
			model.addAttribute("message", "An unexpected error occurred while attempting to save account information.");
		}
		return editUser(model, principal);		
	}

}
