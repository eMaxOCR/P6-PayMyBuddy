package com.openclassrooms.payMyBuddy.configuration;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.openclassrooms.payMyBuddy.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	/**
	 * Request in DB to get user informations.
	 * Method in Spring Security that's used to retrieve user information from a data source (like a database) based on their username
	 * */
	public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
		com.openclassrooms.payMyBuddy.model.User user = userRepository.findByEmail(mail)
				.orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur n'a été trouvé avec cette adresse mail:" + mail));	//Find user from DB
		
		return new User(
				user.getEmail(), 
				user.getPassword(), 
				getGrantedAuthorities(user.getRole())
		);
	}
	
	/**
	 * Helper function that converts a user's role (e.g., "USER", "ADMIN") into a format that Spring Security can understand and use for authorization.
	 * */
	private List<GrantedAuthority> getGrantedAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return authorities;
    }
}
