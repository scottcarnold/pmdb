package org.xandercat.pmdb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

	@GetMapping("/testthis")
	public String test() {
		return "view/test";
	}
}
