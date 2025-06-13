package com.openclassrooms.payMyBuddy.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.springframework.ui.Model;
import java.security.Principal;
import java.util.Map;
import com.openclassrooms.payMyBuddy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;


@Controller
public class LoginController {
	
	private OAuth2AuthorizedClientService authorizedClientService;
	
	@Autowired
	private UserService userService;
		
	/**
	 * Show sign-up page
	 * */
	@GetMapping("/signup")
	public String signup() {
		return "signup";
	}
	
	/**
	 * Take information from sign-up web page.
	 * */
	@PostMapping("/signup")
	public String register(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
		//TODO afficher les message d'erreur. (ça ne marche pas)
		try {
            userService.registerWithUser(user); // Appelle la méthode d'enregistrement de notre service
            redirectAttributes.addFlashAttribute("success", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            return "redirect:/login"; // Redirige vers la page de connexion après succès
        } catch (IllegalArgumentException e) { // Si l'email est déjà utilisé, ou autre erreur du service
            redirectAttributes.addFlashAttribute("error", e.getMessage()); // Affiche le message d'erreur
            return "redirect:/signup"; // Reste sur la page d'inscription avec le message d'erreur
        } catch (Exception e) { // Gérer d'autres erreurs inattendues
            redirectAttributes.addFlashAttribute("error", "Une erreur inattendue est survenue lors de l'inscription.");
            return "redirect:/login";
        }
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/profil")
	public String getProfil(Model model) {
		User currentUser = userService.findCurrentUser();
		model.addAttribute("user", currentUser);
		return "profil";
	}
	
	/**
	 * Go to relation web page
	 * */
	@GetMapping("/relation")
	public String relation() {
		return "relation";
	}
	
	/**
	 * Add one relation to user
	 * @param String email
	 * */
	@PostMapping("/relation")
	public String addRelation(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
		if(userService.getByEmailAddress(email).isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Il n'existe pas d'utilisateur avec cette adresse.");
			return "redirect:/relation";
		}
		//TODO add contact.
		redirectAttributes.addFlashAttribute("success", "Relation ajoutée avec succès.");
		return "redirect:/relation";
	}
	
	@GetMapping("/transfert")
	public String transfert() {
		return "transfert";
	}
	
	
	@GetMapping("/403")
	public String showAccessDeniedPage() {
		return "403";
	}
	
	
//	@GetMapping("/")
//	public String getUserInfo(Principal user) {	//Principal = user
//		StringBuffer userInfo = new StringBuffer();
//		if(user instanceof UsernamePasswordAuthenticationToken) {
//			userInfo.append(getUsernamePasswordLogInInfo(user));
//		}else if(user instanceof OAuth2AuthenticationToken){
//			userInfo.append(getOAuth2LoginInfo(user));
//		}
//		return userInfo.toString();
//	}
//	
//	private StringBuffer getUsernamePasswordLogInInfo(Principal user) {
//		StringBuffer usernameInfo = new StringBuffer();
//		UsernamePasswordAuthenticationToken token = ((UsernamePasswordAuthenticationToken) user); //Take user name after verify that token is authenticated.
//		if(token.isAuthenticated()){
//			User u = (User) token.getPrincipal();
//			//usernameInfo.append("Welcome" + u.getUsername());
//		} else {
//			usernameInfo.append("N/A");
//		}
//		
//		return usernameInfo;
//		
//	}
	
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
	
//	public LoginController(OAuth2AuthorizedClientService authorizedClientService, UserService userService) {
//		this.authorizedClientService = authorizedClientService;
//		this.userService = userService; // Initialisation du service
//	}

	
}
