package org.xandercat.pmdb.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.util.ViewUtil;

@Controller
public class HomeController {

	@Value("${pmdb.environment.name}")
	private String environment;
	
	@Autowired
	private CollectionService collectionService;
	
	@ModelAttribute("viewTab")
	public String getViewTab() {
		return ViewUtil.TAB_HOME;
	}
	
	@GetMapping("/")
	public String home(Model model, Principal principal) {
		MovieCollection defaultMovieCollection = collectionService.getDefaultMovieCollection(principal.getName());
		if (defaultMovieCollection == null) {
			// send them to collections so they can set a default movie collection
			return "redirect:/collections";
		}
		model.addAttribute("movieCollection", defaultMovieCollection);
		return "home";
	}
}
