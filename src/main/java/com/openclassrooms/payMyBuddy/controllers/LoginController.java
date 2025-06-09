package com.openclassrooms.payMyBuddy.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@RestController
public class LoginController {
	
	private OAuth2AuthorizedClientService authorizedClientService;
	
	@GetMapping("/user")
	public String getUser() {
		return "Welcome, user.";
	}
	
	@GetMapping("/admin")
	public String getAdmin() {
		return "Welcome, admin !";
	}
	
	@GetMapping("/")
	public String getUserInfo(Principal user) {	//Principal = user
		StringBuffer userInfo = new StringBuffer();
		if(user instanceof UsernamePasswordAuthenticationToken) {
			userInfo.append(getUsernamePasswordLogInInfo(user));
		}else if(user instanceof OAuth2AuthenticationToken){
			userInfo.append(getOAuth2LoginInfo(user));
		}
		return userInfo.toString();
	}
	
	private StringBuffer getUsernamePasswordLogInInfo(Principal user) {
		StringBuffer usernameInfo = new StringBuffer();
		UsernamePasswordAuthenticationToken token = ((UsernamePasswordAuthenticationToken) user); //Take user name after verify that token is authenticated.
		if(token.isAuthenticated()){
			User u = (User) token.getPrincipal();
			usernameInfo.append("Welcome" + u.getUsername());
		} else {
			usernameInfo.append("N/A");
		}
		
		return usernameInfo;
		
	}
	
	private StringBuffer getOAuth2LoginInfo(Principal user) {
		StringBuffer protectedInfo = new StringBuffer();
		OAuth2AuthenticationToken authToken = ((OAuth2AuthenticationToken) user);
		OAuth2AuthorizedClient authClient = this.authorizedClientService.loadAuthorizedClient(authToken.getAuthorizedClientRegistrationId(), authToken.getName());
		
		if(authToken.isAuthenticated()){
			   
			  Map<String,Object> userAttributes = ((DefaultOAuth2User) authToken.getPrincipal()).getAttributes();
			   
			  String userToken = authClient.getAccessToken().getTokenValue();
			  protectedInfo.append("Welcome, " + userAttributes.get("name")+"<br><br>");
			  //protectedInfo.append("e-mail: " + userAttributes.get("email")+"<br><br>");
			  //protectedInfo.append("Access Token: " + userToken+"<br><br>");	
		  }
		  else{
			  protectedInfo.append("NA");
		  }
		
		return protectedInfo;
	}
	
	/*Contructor
	 * */
	public LoginController(OAuth2AuthorizedClientService authorizedClientService) {
		   this.authorizedClientService = authorizedClientService;
		}
	
}
