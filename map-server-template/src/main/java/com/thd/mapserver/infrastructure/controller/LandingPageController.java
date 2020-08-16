package com.thd.mapserver.infrastructure.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingPageController {

	//TODO: get all collections (SQL) -> Parse them to List<FeatureCollection> -> pass them to the model (model.addAttribute)
	@GetMapping("/")
	public String test(Model model) {
		final var collections = List.of(new FeatureCollection("test1", "collection 1"),
				new FeatureCollection("test2", "collection 2"));
		model.addAttribute("collections", collections); // A Attribute can be accessed via the ${attributeName} syntax
														// in the html template
		return "index"; // name of the template page located under resources/templates
	}

	@GetMapping("/conformance")
	public String getLandingPage() {
		return "conformance";
	}
	
	public static class FeatureCollection {
		private String name;
		private String description;

		public FeatureCollection(String name, String description) {
			this.name = name;
			this.description = description;
		}
		
		public String getName() {
			return this.name;
		}
		
		public String getDescription() {
			return this.description;
		}
	}

}
