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
	//Request in DB to get user informations.
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.openclassrooms.payMyBuddy.model.User user = userRepository.findByUsername(username);	//Find user from DB
		
		return new User(user.getUsername(), user.getPassword(), getGrantedAuthorities(user.getRole()));
	}
	
	//TODO Create 
	private List<GrantedAuthority> getGrantedAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return authorities;
    }
}
