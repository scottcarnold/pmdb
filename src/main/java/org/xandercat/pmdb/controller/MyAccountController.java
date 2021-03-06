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
import org.xandercat.pmdb.util.Alerts;
import org.xandercat.pmdb.util.ViewUtil;
import org.xandercat.pmdb.util.format.FormatUtil;

/**
 * Controller for user updating their own account.
 * 
 * @author Scott Arnold
 */
@Controller
public class MyAccountController {

	private static final Logger LOGGER = LogManager.getLogger(MyAccountController.class);
	
	@Autowired
	UserService userService;
	
	@ModelAttribute("viewTab")
	public String getViewTab() {
		return ViewUtil.TAB_MY_ACCOUNT;
	}
	
	/**
	 * Page for editing user details.
	 * 
	 * @param model      model
	 * @param principal  principal
	 * 
	 * @return page for editing user details
	 */
	@RequestMapping("/myaccount")
	public String editUser(Model model, Principal principal) {
		PmdbUser user = userService.getUser(principal.getName()).get();
		boolean administrator = userService.isAdministrator(principal.getName());
		UserForm userForm = new UserForm(user, administrator);
		model.addAttribute("userForm", userForm);
		return "myaccount/edituser";
	}
	
	/**
	 * Processing editing of user details.
	 * 
	 * @param model      model
	 * @param principal  principal
	 * @param userForm   form for user details
	 * @param result     binding result
	 * 
	 * @return page for editing user details
	 */
	@RequestMapping("/myaccount/editUserSubmit")
	public String editUserSubmit(Model model, Principal principal,
			@ModelAttribute("userForm") @Valid UserForm userForm,
			BindingResult result) {
		if (result.hasErrors()) {
			return "myaccount/edituser";
		}
		try {
			String newPassword = FormatUtil.isBlank(userForm.getPasswordPair().getFirst())? null : userForm.getPasswordPair().getFirst().trim();
			userService.saveMyAccountUser(userForm.toUser(), newPassword, principal.getName());
			Alerts.setMessage(model, "Account information saved.");
		} catch (Exception e) {
			LOGGER.error("Unexpected error when updating user.", e);
			Alerts.setErrorMessage(model, "An unexpected error occurred while attempting to save account information.");
		}
		return editUser(model, principal);		
	}

}
