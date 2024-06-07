package com.contactmanager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contactmanager.entities.User;
import com.contactmanager.services.UserService;
import com.contactmanager.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserService userService;
	
	

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home: Contact Manager");
		return "home";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("title", "Signup: Contact Manager");
		return "signup";
	}
	
	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("title", "Login: Contact Manager");
		return "login";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About: Contact Manager");
		return "about";
	}
	
	@PostMapping("/do_signup")
	public String register(@Valid @ModelAttribute("user") User user, BindingResult result, @RequestParam(value="agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session) {
		try {
			
			if(!agreement) {
				throw new Exception("You have not agreed the term and conditions.");
			}
			
			if(result.hasErrors()) {
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userService.saveUser(user);
			
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));
			
			return "signup";
			
		} catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!!" + e.getMessage(), "alert-danger"));
			
			return "signup";
		}
	}
}
