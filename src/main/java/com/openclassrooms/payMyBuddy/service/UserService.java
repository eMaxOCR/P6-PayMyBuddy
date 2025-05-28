package com.openclassrooms.payMyBuddy.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * Return all users.
	 * @return List of User
	 **/
	public Iterable<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	/**
	 * Return one user that match id.
	 * @Return One user.
	 * */
	public Optional<User> getUserById(Integer id){
		return userRepository.findById(id);
	}
	
	/**
	 * Add user into database.
	 * @return User
	 * */
	public User addUser(User user) {
		return userRepository.save(user);
	}
	
	/**
	 * Delete user from database.
	 * */
	public void deleteUser(Integer id) {
		userRepository.deleteById(id);
	}
	
	
}
