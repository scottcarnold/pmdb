package org.xandercat.pmdb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@Value("${pmdb.environment.name}")
	private String environment;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@GetMapping("/")
	public String test(Model model) {
		String bar = jdbcTemplate.queryForObject("SELECT foo FROM testtable WHERE id = 1", String.class);
		model.addAttribute("message", "You are now logged in with the " + environment + " environment.  Database check: " + bar);
		return "home";
	}
}
