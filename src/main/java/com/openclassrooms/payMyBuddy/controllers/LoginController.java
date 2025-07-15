package com.openclassrooms.payMyBuddy.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.springframework.ui.Model;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController {
		
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
            HashMap<String, String> updateProfilStatus = userService.register(user); ;
			Map.Entry<String,String> entry = updateProfilStatus.entrySet().iterator().next();
			redirectAttributes.addFlashAttribute(entry.getKey(), entry.getValue());	
			return "redirect:/signup";
           
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur inattendue est survenue lors de l'inscription : " + e);
            return "redirect:/signup";
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
			HashMap<String, String> updateProfilStatus = userService.updateProfile(newUserInformation);
			Map.Entry<String,String> entry = updateProfilStatus.entrySet().iterator().next();
			redirectAttributes.addFlashAttribute(entry.getKey(), entry.getValue());	
			return "redirect:/login";
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
		try {
			HashMap<String, String> transfertStatus = transactionService.addTransaction(transaction);	//Define HashMap with function's return information.
			Map.Entry<String,String> entry = transfertStatus.entrySet().iterator().next();				//Get the informations and send it to web page.
			redirectAttributes.addFlashAttribute(entry.getKey(), entry.getValue());	
			return "redirect:/transfert";																//Redirection		
		}catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Une erreur est survenue : " + e.getMessage());
			return "redirect:/transfert"; 
		}

	}
	
	/**
	 * When forbidden, show 403.
	 * */
	@GetMapping("/403")
	public String showAccessDeniedPage() {
		return "403";
	}
	
}
