package com.openclassrooms.payMyBuddy.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.payMyBuddy.model.Account;
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
	 * save user into database.
	 * @return User
	 * */
	public User save(User user) {
		return userRepository.save(user);
	}
	
	/**
	 * Add relation/contact into database.
	 * @return User
	 * */
	public HashMap<String, String> addContactByEmail(String mail) {
		
		User currentUser = getCurrentUser();					//Get current user information
		HashMap<String, String> returnInfo = new HashMap<>();	//Setup hash map that contain the result
		returnInfo.put("error", "Une erreur est survenue");
		Optional<User> contactToAdd = getByEmailAddress(mail);	//Searching for user to add by he's email address.
		
		if (contactToAdd.isEmpty()) {
			returnInfo.put("error", "Il n'existe pas d'utilisateur avec cette adresse mail.");
		    return returnInfo;
		}
					
	 	User contact = contactToAdd.get();
	    if (currentUser.getEmail().equals(contact.getEmail())) {
	    	returnInfo.put("error", "Il n'est pas possible de s'ajouter.");
		    return returnInfo;
	    }else if (currentUser.addContact(contactToAdd)) {
	        userRepository.save(currentUser);
	        returnInfo.put("success", "Relation ajoutée");
		    return returnInfo;
	    }
	    
	    return returnInfo;

	}
	
	/**
	 * Update current profile
	 * */
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
	public Integer registerWithUsernameMailPassword(String username, String mail, String password) {
		User newUser = new User();
		newUser.setUsername(username);
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
		add(newUser); //TODO essayer avec la cascade.
		
		Account newAccount = new Account();
		newAccount.setUser(newUser);
		newAccount.setBalance(new BigDecimal(0));
		accountService.addAccount(newAccount);
		add(newUser);
		
		return newUser;	
	}
	
	
	
	
}
