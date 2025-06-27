package com.openclassrooms.payMyBuddy.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.openclassrooms.payMyBuddy.service.TransactionService;
import com.openclassrooms.payMyBuddy.service.UserService;

import jakarta.transaction.Transactional;

import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
	@Autowired
	private TransactionService transactionService;

		
	/**
	 * Access to "sign-up" web page
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
		try {
            userService.register(user); 
            redirectAttributes.addFlashAttribute("success", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) { 
            redirectAttributes.addFlashAttribute("error", e.getMessage()); 
            return "redirect:/signup"; 
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur inattendue est survenue lors de l'inscription.");
            return "redirect:/login";
        }
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	/**
	 * Access to "Profile" web page
	 * */
	@GetMapping("/profil")
	public String getProfil(Model model, Principal principal) {
		String userEmail = principal.getName();
		Optional<User> userOptional = userService.getByEmailAddress(userEmail);
		User user = userOptional.get();
		model.addAttribute("user", user);
		model.addAttribute("account", user.getAccount());
		return "profil";
	}
	
	/**
	 * Update profile
	 * */
	@PostMapping("/profil")
	public String updateProfil(@ModelAttribute("user") User newUserInformation, RedirectAttributes redirectAttributes) {
		try {
			String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
			Optional<User> existingUserOptional = userService.getByEmailAddress(userEmail);
			User currentUser = existingUserOptional.get();
			if (currentUser == null) {
	            redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé.");
	            return "redirect:/profil";
	        }
			
			userService.updateProfile(newUserInformation);
			
			redirectAttributes.addFlashAttribute("success", "Profil mis à jour");
			return "redirect:/login?logout=true";
		}catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Une erreur inattendue est survenue lors de la mise à jour.");
			return "redirect:/profil";
		}
	}
	
	/**
	 * Access to "Relation" web page
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
		try {
			HashMap<String, String> relationStatus = userService.addContactByEmail(email);
			Map.Entry<String,String> entry = relationStatus.entrySet().iterator().next();
			redirectAttributes.addFlashAttribute(entry.getKey(), entry.getValue());
			return "redirect:/relation";
			
		}catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Une erreur inattendue est survenue : " + e);
			return "redirect:/relation";
		}
	}
	
	
	/**
	 * Access to "Transfer" web page 
	 * */
	@GetMapping("/transfert")
	public String showTransactions(Model model, Principal principal) {
		User currentUser = userService.getCurrentUser();
		List<User> relations = currentUser.getContacts();
		model.addAttribute("relations", relations);				//All user's contacts
		model.addAttribute("transaction", new Transaction());	//Transaction model							
		model.addAttribute("transactionList", transactionService.getAllTransactionsFromUser(currentUser));		//All transactions							//All transactions
		model.addAttribute("currentUserName", currentUser.getUsername());
		return "transfert";
	}
	
	/**
	 * Create "Transfer"
	 * */
	@PostMapping("/transfert")
	public String createTransaction(@ModelAttribute Transaction transaction, RedirectAttributes redirectAttributes) {
		//TODO service must check, return HASHMAP.
			try {
				if(transaction.getAmount().compareTo(new BigDecimal(0)) == 0) {
					redirectAttributes.addFlashAttribute("error", "Le montant doit être supérieur à 0");
					return "redirect:/transfert"; 
				}else if(transactionService.addTransaction(transaction)) {
					redirectAttributes.addFlashAttribute("success", "💸 Paiement effectué 💸");
					return "redirect:/transfert";
				}else {
					redirectAttributes.addFlashAttribute("error", "Vous n'avez pas assez d'argent 😫");
					return "redirect:/transfert"; 
				}
				
			}catch (Exception e) {
				redirectAttributes.addFlashAttribute("error", "Une erreur est survenue : " + e.getMessage());
				return "redirect:/transfert"; 
			}

	}
	
	
	@GetMapping("/403")
	public String showAccessDeniedPage() {
		return "403";
	}
	
	private StringBuffer getOAuth2LoginInfo(Principal user) {
		StringBuffer protectedInfo = new StringBuffer();
		OAuth2AuthenticationToken authToken = ((OAuth2AuthenticationToken) user);
		OAuth2AuthorizedClient authClient = this.authorizedClientService.loadAuthorizedClient(authToken.getAuthorizedClientRegistrationId(), authToken.getName());
		
		if(authToken.isAuthenticated()){
			   
			  Map<String,Object> userAttributes = ((DefaultOAuth2User) authToken.getPrincipal()).getAttributes();
			   
			  String userToken = authClient.getAccessToken().getTokenValue();
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
