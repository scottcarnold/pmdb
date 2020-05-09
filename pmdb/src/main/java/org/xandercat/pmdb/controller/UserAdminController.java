package org.xandercat.pmdb.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.form.useradmin.SearchForm;
import org.xandercat.pmdb.service.UserService;
import org.xandercat.pmdb.util.ViewUtil;

@Controller
public class UserAdminController {

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
			List<PmdbUser> results = userService.searchUsers(searchForm.getUsername());
			model.addAttribute("results", results);
		}
		return "useradmin/useradmin";
	}
}
