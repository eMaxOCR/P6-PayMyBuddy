package com.openclassrooms.payMyBuddy.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private AccountService accountService;
	
	/**
	 * Return all users.
	 * @return List of User
	 **/
	public Iterable<User> getAll(){
		return userRepository.findAll();
	}
	
	/**
	 * Return one user that match id.
	 * @Return One user.
	 * */
	public Optional<User> getById(Integer id){
		return userRepository.findById(id);
	}
	
	/**
	 * return User by it's "email" address.
	 * */
	public Optional<User> getByEmailAddress(String email) {
		return userRepository.findByEmail(email);
	}

	
	/**
	 * Get current user
	 * */
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Aucun utilisateur actuellement connecté.");
        }
		 String userEmail = authentication.getName();
	        return userRepository.findByEmail(userEmail)
	                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé dans la base de données pour l'email: " + userEmail));
	    
	}
	
	/**
	 * Add user into database.
	 * @return User
	 * */
	public User add(User user) {
		return userRepository.save(user);
	}
	
	/**
	 * Add relation/contact into database.
	 * @return User
	 * */
	public Boolean addContactByEmail(String mail) {
		User currentUser = getCurrentUser();
		if (currentUser == null) {
		    throw new IllegalStateException("Aucun utilisateur actuellement connecté.");
		}
		
		Optional<User> contactToAdd = getByEmailAddress(mail);	//Searching for user to add by he's email address.
	 	User contact = contactToAdd.get();
	    if (currentUser.getEmail().equals(contact.getEmail())) {
	        throw new IllegalArgumentException("Un utilisateur ne peut pas s'ajouter lui-même.");
	    }
	    
	    if (currentUser.addContact(contactToAdd)) {
	        userRepository.save(currentUser);
	        return true;
	    }
	    
	    return false;

	}
	
	/**
	 * Update current profile
	 * */
	@Transactional
	public User updateProfile(User user) {
		User currentUser = getCurrentUser();
		
		String userName = user.getUsername();
		if(userName != null) {
			currentUser.setUsername(userName);
		}
		
		String email = user.getEmail();
		if(email != null) {
			currentUser.setEmail(email);
		}
		
		String passeword = user.getPassword();
		if(!passeword.isBlank()) {
			currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
		}
				
		return userRepository.save(currentUser);
		
	}
	
	/**
	 * Delete user from database.
	 * */
	public void delete(Integer id) {
		userRepository.deleteById(id);
	}
	
	/**
	 * Register new user with text
	 * @param Username, Mail, Password 
	 * @return userId
	 * */
	@Transactional
	public Integer registerWithUsernameMailPassword(String username, String mail, String password) {
		User newUser = new User();
		newUser.setUsername(username);
		//TODO check if mail already exist (SQL Request?)
		newUser.setEmail(mail);
		newUser.setRole("USER");
		newUser.setPassword(passwordEncoder.encode(password));
		return add(newUser).getId();		
	}
	
	/**
	 * Register new user
	 * @param Username, Mail, Password 
	 * @return userId
	 * */
	@Transactional
	public User register(User user) {	
		User newUser = new User();
		newUser=user;
		newUser.setRole("USER");
		newUser.setPassword(passwordEncoder.encode(user.getPassword()));
		return add(newUser);	
	}
	
	
	
	
}
