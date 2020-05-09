package org.xandercat.pmdb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.xandercat.pmdb.util.ViewUtil;

@Controller
public class HomeController {

	@Value("${pmdb.environment.name}")
	private String environment;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@ModelAttribute("viewTab")
	public String getViewTab() {
		return ViewUtil.TAB_HOME;
	}
	
	@GetMapping("/")
	public String home(Model model) {
		String bar = jdbcTemplate.queryForObject("SELECT foo FROM testtable WHERE id = 1", String.class);
		model.addAttribute("message", "You are now logged in with the " + environment + " environment.  Database check: " + bar);
		return "home";
	}
}
