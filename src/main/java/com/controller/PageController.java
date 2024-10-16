package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

//	@GetMapping("/services")
//	public String servicesPage() {
//		return "services";
//	}

	@GetMapping("/aboutus")
	public String aboutUsPage() {
		return "aboutus";
	}

	@GetMapping("/results")
	public String resultsPage() {
		return "results";
	}
		
}