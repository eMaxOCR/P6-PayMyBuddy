package com.openclassrooms.payMyBuddy.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.openclassrooms.payMyBuddy.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
	
	/**
	 * Searching User by it's "username".
	 * @param username.
	 * */
	public Optional<User> findByUsername(String username);
	
	/**
	 * Searching User by it's "email" address.
	 * */
	public Optional<User> findByEmail(String email);
	
}
