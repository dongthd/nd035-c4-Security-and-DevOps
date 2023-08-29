package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		log.info("Find by user Id: {}", id);
		return ResponseEntity.of(userRepository.findById(id));
	}

	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		log.info("Find by username: {}", username);
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}

	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) throws Exception {
		log.info("Begin create user with username {}", createUserRequest.getUsername());
		User userExists = userRepository.findByUsername(createUserRequest.getUsername());
		if(userExists != null) {
			log.info("Create user with username {} fail -> Username is exists", createUserRequest.getUsername());
			throw new Exception("Username is exists");
		}

		if(createUserRequest.getPassword().length() < 6) {
			log.info("Create user with username {} fail -> Password must more 6 character", createUserRequest.getUsername());
			throw new Exception("Password must more 6 character");
		}

		if(!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			log.info("Create user with username {} fail -> Confirm password is not same", createUserRequest.getUsername());
			throw new Exception("Confirm password is not same");
		}

		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		userRepository.save(user);
		log.info("User created successfully");
		log.info("End create user with username {}", createUserRequest.getUsername());
		return ResponseEntity.ok(user);
	}
	
}
