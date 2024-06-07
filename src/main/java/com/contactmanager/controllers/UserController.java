package com.contactmanager.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactmanager.entities.Contact;
import com.contactmanager.entities.User;
import com.contactmanager.helper.Message;
import com.contactmanager.repositories.ContactRepository;
import com.contactmanager.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		//getting user info from database
		User user = userService.getUser(principal.getName()).get();
		model.addAttribute("user", user);
	}

	//dashboard home
	@GetMapping("/index")
	public String dashboard(Model model) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	//open add form handler
	@GetMapping("/add_contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact";
	}
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact, BindingResult result, @RequestParam("profileImage") MultipartFile file ,Principal principal, Model model, HttpSession session) {
		
		if(result.hasErrors()) {
			model.addAttribute("contact", contact);
			System.out.println("validation error");
			System.out.println(result.toString());
			return "normal/add_contact";
		}
		
		try {
		
			//Saving user in the database
			User user = this.userService.getUser(principal.getName()).get();
		
			//Processing and uploading file
			if(file.isEmpty()) {
				System.out.println("File is empty");
				contact.setImage("contact.png");
			} else {
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("/static/images").getFile();
			
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("image is uploaded");
			}
		
			contact.setUser(user);
			user.getContacts().add(contact);
		
			this.userService.saveUser(user);
			
			//returning the response
			model.addAttribute("contact", new Contact());
			session.setAttribute("message", new Message("Your contact is added !! Add more...", "success"));
		
		} catch(Exception e) {
			System.out.println("Error " + e.getMessage());
			session.setAttribute("message", new Message("Something went wrong !! Try again...", "danger"));
		}
		model.addAttribute("session", session);
		return "normal/add_contact";
	}
	
	//show contacts handler
	//per page = 5[n]
	//current page = 0 [page]
	@GetMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable("page") int page ,Model model, Principal principal) {
		model.addAttribute("title", "Show User Contacts");
		
		String userName = principal.getName();
		User user = this.userService.getUser(userName).get();
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
		
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";
	}
	
	//showing particular contact details
	@RequestMapping("/{cid}/contact")
	public String showContactDetail(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		
		//
		String userName = principal.getName();
		User user = this.userService.getUser(userName).get();
		
		if(user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		
		return "normal/contact_detail";
		
	}
	
	//delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model model, HttpSession session, Principal principal) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		
		//contact.setUser(null);
		User user = this.userService.getUser(principal.getName()).get();
		user.getContacts().remove(contact);
		this.userService.saveUser(user);
		
		session.setAttribute("message", new Message("Contact deleted successfully...", "success"));
		
		return "redirect:/user/show_contacts/0";
	}
	
	//open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model m) {
		
		m.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepository.findById(cid).get();
		m.addAttribute("contact", contact);
		return "normal/update_form";
	}
	
	//update contact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, Model m, HttpSession session, Principal principal) {
		
		try {
			//old contact details
			Contact oldContactDetail = this.contactRepository.findById(contact.cid).get();
			
			//image
			if(!file.isEmpty()) {
				//delete old photo
				File deleteFile = new ClassPathResource("/static/images").getFile();
				File file1 = new File(deleteFile, oldContactDetail.getImage());
				file1.delete();
				
				//update new photo
				File saveFile = new ClassPathResource("/static/images").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
				
			} else {
				contact.setImage(oldContactDetail.getImage());
			}
			User user = this.userService.getUser(principal.getName()).get();
			contact.setUser(user);
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Your contact is updated...", "success"));
		} catch(Exception e) {
			
		}
		
		System.out.println("contact name" + contact.getName());
		
		return "redirect:/user"+contact.getCid()+"/contact";
	}
}
