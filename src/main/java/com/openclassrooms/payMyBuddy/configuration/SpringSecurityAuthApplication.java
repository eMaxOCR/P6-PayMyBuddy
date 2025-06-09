package com.openclassrooms.payMyBuddy.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //Spring detect this class has configuration class.
@EnableWebSecurity 
public class SpringSecurityAuthApplication {
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@Bean //TODO infos
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		return http.authorizeHttpRequests(auth -> {
			auth.requestMatchers("/admin").hasRole("ADMIN"); 	//Define admin and his role
			auth.requestMatchers("/user").hasRole("USER");		//Define user and his role
			auth.anyRequest().authenticated(); 					//for http"s".
		}).formLogin(Customizer.withDefaults())
				.oauth2Login(Customizer.withDefaults())		//Setup OAuth.
				.build(); 										//Create login form page.
	}
	
	@Bean //For encrypt password
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public UserDetailsService users () {
		UserDetails user = User.builder()
			.username("user")
			.password(passwordEncoder().encode ("user") )
			.roles("USER").build ();
		UserDetails admin = User.builder()
			.username("admin")
			.password(passwordEncoder().encode ("admin") )
			.roles("USER", "ADMIN").build ();
		return new InMemoryUserDetailsManager (user, admin);
	}
	
	@Bean
	//Manage authentication sources
	public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
	    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
	authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
	    return authenticationManagerBuilder.build();
	}


}
