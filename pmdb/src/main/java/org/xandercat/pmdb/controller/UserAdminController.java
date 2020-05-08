package org.xandercat.pmdb.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.form.useradmin.SearchForm;

@Controller
public class UserAdminController {

	@GetMapping("/useradmin")
	public String userAdmin(Model model) {
		SearchForm searchForm = new SearchForm();
		searchForm.setUsername("Does this work?");
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
			List<PmdbUser> results = new ArrayList<PmdbUser>();
			results.add(new PmdbUser("Testuser"));
			model.addAttribute("results", results);
		}
		return "useradmin/useradmin";
	}
}
