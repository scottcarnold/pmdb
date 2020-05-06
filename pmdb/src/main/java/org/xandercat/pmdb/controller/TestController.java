package org.xandercat.pmdb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

	@GetMapping("/")
	public String test(Model model) {
		model.addAttribute("message", "Now get templating working!");
		return "index";
	}
}
