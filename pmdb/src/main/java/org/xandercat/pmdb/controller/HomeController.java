package org.xandercat.pmdb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/home")
	public String test(Model model) {
		model.addAttribute("message", "You are now logged in");
		return "home";
	}
}
