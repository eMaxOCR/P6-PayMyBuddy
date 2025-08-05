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
	public Iterable<User> getAll() {
		return userRepository.findAll();
	}

	/**
	 * Return one user that match id.
	 * @param user id
	 * @Return User.
	 */
	public Optional<User> getById(Integer id) {
		return userRepository.findById(id);
	}

	/**
	 * return User by it's "email" address.
	 * @return User
	 */
	public Optional<User> getByEmailAddress(String email) {
		return userRepository.findByEmail(email);
	}

	/**
	 * Get current user
	 * @return user
	 */
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userEmail = authentication.getName();
		return userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalStateException(
				"Utilisateur non trouvé dans la base de données pour l'email: " + userEmail));

	}

	/**
	 * save user into database.
	 * @param User
	 * @return User
	 */
	public User save(User user) {
		return userRepository.save(user);
	}

	/**
	 * Add USER into CONTACT
	 * @return Boolean
	 * @param USER
	 */
	public Boolean addContact(Optional<User> contactOptional) {
		if (contactOptional.isPresent()) { // Check if already exist
			User currentUser = getCurrentUser();
			User contactToAdd = contactOptional.get(); // Convert Optional<User> to User.
			currentUser.getContacts().add(contactToAdd);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Add relation/contact into database.
	 * @param email
	 * @return User
	 */
	public HashMap<String, String> addContactByEmail(String mail) {

		User currentUser = getCurrentUser(); // Get current user information
		HashMap<String, String> returnInfo = new HashMap<>(); // Setup hash map that contain the result
		returnInfo.put("error", "Une erreur est survenue");
		Optional<User> contactToAdd = getByEmailAddress(mail); // Searching for user to add by he's email address.

		if (contactToAdd.isEmpty()) {
			returnInfo.put("error", "Il n'existe pas d'utilisateur avec cette adresse mail.");
			return returnInfo;
		}

		User contact = contactToAdd.get();
		if (currentUser.getEmail().equals(contact.getEmail())) {
			returnInfo.put("error", "Il n'est pas possible de s'ajouter.");
			return returnInfo;
		} else if (addContact(contactToAdd)) {
			userRepository.save(currentUser);
			returnInfo.put("success", "Relation ajoutée");
			return returnInfo;
		}

		return returnInfo;

	}

	/**
	 * Update current profile
	 * @param user
	 * @return HashMap
	 */
	public HashMap<String, String> updateProfile(User user) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName(); // Get mail address from																				// connected user
		Optional<User> existingUserOptional = getByEmailAddress(userEmail); // Get user information from address.
		HashMap<String, String> returnInfo = new HashMap<>(); // Setup hash map that contain the result
		returnInfo.put("error", "Une erreur est survenue");
		
		if (existingUserOptional.isEmpty()) {
			returnInfo.put("error", "Utilisateur non trouvé");
			return returnInfo;
		} 
		
		User currentUser = existingUserOptional.get();
		
		String userName = user.getUsername();
		if (userName != null) {
			currentUser.setUsername(userName);
		}

		String email = user.getEmail();
		if (email != null) {
			currentUser.setEmail(email);
		}

		String passeword = user.getPassword();
		if (!passeword.isBlank()) {
			currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		userRepository.save(currentUser);
		returnInfo.put("success", "Profil mis à jour");
		return returnInfo;
		

	}

	/**
	 * Delete user from database.
	 * @param User id
	 */
	public void delete(Integer id) {
		userRepository.deleteById(id);
	}

	/**
	 * Register new user
	 * 
	 * @param Username, Mail, Password
	 * @return userId
	 */
	@Transactional
	public HashMap<String, String> register(User user) {

		HashMap<String, String> returnInfo = new HashMap<>(); // Setup hash map that contain the result
		returnInfo.put("error", "Une erreur est survenue");

		if (getByEmailAddress(user.getEmail()).isEmpty()) {
			User newUser = new User();
			newUser = user;
			newUser.setRole("USER");
			newUser.setPassword(passwordEncoder.encode(user.getPassword()));
			save(newUser);

			Account newAccount = new Account();
			newAccount.setUser(newUser);
			newAccount.setBalance(new BigDecimal(0));
			accountService.addAccount(newAccount);
			save(newUser);

			returnInfo.put("success", "Vous avez été enregistré avec succès 🫡");

			return returnInfo;
		} else {
			returnInfo.put("error", "Addresse mail déjà utilisée");
			return returnInfo;
		}
	}

}
