package com.contactmanager.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contactmanager.entities.User;
import com.contactmanager.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	
	public void saveUser(User user) {
		userRepository.save(user);
	}
	
	public Optional<User> getUser(String username) {
		return userRepository.findByUsernameOrEmail(username, username);
	}
}
