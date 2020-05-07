package org.xandercat.pmdb.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@Value("${pmdb.environment.name}")
	private String environment;
	
	@GetMapping("/home")
	public String test(Model model) {
		model.addAttribute("message", "You are now logged in with the " + environment + " environment.");
		return "home";
	}
}
