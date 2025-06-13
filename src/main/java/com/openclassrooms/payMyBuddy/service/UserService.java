package com.openclassrooms.payMyBuddy.service;

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
	 * Find current user
	 * */
	public User findCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // Pas d'utilisateur connecté ou authentification anonyme/non complète
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            // Si le principal est une instance de UserDetails (ce qui est généralement le cas avec Spring Security)
            String email = ((UserDetails) principal).getUsername(); // Ou getEmail() si votre UserDetails utilise l'email comme nom d'utilisateur

            // On récupère l'utilisateur depuis la base de données via son email (ou username)
            Optional<User> userOptional = getByEmailAddress(email); // Assurez-vous d'avoir une méthode findByEmail dans votre UserRepository

            return userOptional.orElse(null); // Retourne l'utilisateur s'il est trouvé, sinon null
        } else if (principal instanceof String) {
            // Dans certains cas (par exemple, authentification anonyme), le principal peut être une String
            // Vous pouvez gérer ce cas si nécessaire, mais souvent on ne le traite pas comme un "vrai" utilisateur loggé
            return null;
        }

        return null;
    }
	
	/**
	 * Add user into database.
	 * @return User
	 * */
	public User add(User user) {
		return userRepository.save(user);
	}
	
	/**
	 * Add relation into database.
	 * @return User
	 * */
	public User addContact(User user) {
		return userRepository.;
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
	public User registerWithUser(User user) {	
		User newUser = new User();
		newUser=user;
		newUser.setRole("USER");
		newUser.setPassword(passwordEncoder.encode(user.getPassword()));
		return add(newUser);	
	}
	
	
	
	
}
