package com.openclassrooms.payMyBuddy.controllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // POUR LE CSRF

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.openclassrooms.payMyBuddy.controllers.LoginController;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import com.openclassrooms.payMyBuddy.service.UserService;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ControllerTest {
		
	@MockBean
	private UserService userService;
	
	@MockBean
	private TransactionService transactionService;
	
	@Mock
	private Model model;
	
	@Mock
	private Principal principal;
	
	@Mock
	private RedirectAttributes redirectAttributes;
	
	@InjectMocks
	LoginController loginController;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void signupTest() throws Exception {
		//ARRANGE
		//ACT
		String viewName = loginController.signup();
		//ASSERT
		assertEquals("signup", viewName);

	}
	
	@Test
	void registerTest() throws Exception {
		//ARRANGE
		User newUser = new User();
		newUser.setEmail("test@test.fr");
		newUser.setPassword("test");
		
		HashMap<String, String>successResponse = new HashMap<>();
		successResponse.put("success", "Inscription réussie !");
		when(userService.register(newUser)).thenReturn(successResponse);
		
		//ACT
		String viewName = loginController.register(newUser, redirectAttributes);
		//ASSERT
		verify(userService, times(1)).register(newUser);
		verify(redirectAttributes, times(1)).addFlashAttribute("success", "Inscription réussie !");
		assertEquals("redirect:/signup", viewName);
//		mockMvc.perform(post("/signup")
//				.flashAttr("user", newUser)						//Put user
//				.with(csrf()))									//Put "CSRF" for Thymleaf
//				.andExpect(flash().attributeExists("success")); //Expect success
	}
	
	@Test
	void registerFailedTest() throws Exception {	
		//ARRANGE
		User newUser = new User();
		newUser.setEmail("test@test.fr");
		newUser.setPassword("test");
		
		HashMap<String, String>errorResponse = new HashMap<>();
		errorResponse.put("error", "Une erreur inattendue est survenue lors de l'inscription : ");
		when(userService.register(any(User.class))).thenThrow(new RuntimeException());
		
		//ACT
		String viewName = loginController.register(newUser, redirectAttributes);
		
		//ASSERT
		assertTrue(errorResponse.containsKey("error"));
		
	}
	
	@Test
	void loginTest() throws Exception {
		//ARRANGE
		//ACT
		String viewName = loginController.login();
		//ASSERT
		assertEquals("login", viewName);
	}
	
	@Test
	void profilAccessTest() throws Exception {
		//ARRANGE
		String email = "test@test.fr";
		
		User user = new User();
		user.setEmail(email);
		user.setUsername("DavidT");
		//ACT
		when(principal.getName()).thenReturn(email);
		when(userService.getByEmailAddress(email)).thenReturn(Optional.of(user));
		
		String viewName = loginController.getProfil(model, principal);
		//ASSERT
		assertEquals("profil", viewName);
		verify(userService, times(1)).getByEmailAddress(email);
	}
	
	@Test
	void updateProfilTest() throws Exception {
		//ARRANGE
		User newUser = new User();
		newUser.setEmail("david-B@test.fr");
		newUser.setUsername("DavidB");
		
		HashMap<String, String>successResponse = new HashMap<>();
		successResponse.put("success", "Profil mis à jour");
		
		//ACT
		when(userService.updateProfile(newUser)).thenReturn(successResponse);
		String viewName = loginController.updateProfil(newUser, redirectAttributes);
		
		//ASSERT	
		verify(userService, times(1)).updateProfile(newUser);
		verify(redirectAttributes, times(1)).addFlashAttribute("success", "Profil mis à jour");
		assertEquals("redirect:/login", viewName);
	}
	
	@Test
	void updateProfilFailedTest() throws Exception {
		//ARRANGE
		User newUser = new User();
		newUser.setEmail("david-B@test.fr");
		newUser.setUsername("DavidB");
		
		HashMap<String, String>successResponse = new HashMap<>();
		successResponse.put("error", "Une erreur inattendue est survenue lors de la mise à jour.");
		
		//ACT
		when(userService.updateProfile(newUser)).thenThrow(new RuntimeException());
		String viewName = loginController.updateProfil(newUser, redirectAttributes);
		
		//ASSERT	
		verify(userService, times(1)).updateProfile(newUser);
		verify(redirectAttributes, times(1)).addFlashAttribute("error", "Une erreur inattendue est survenue lors de la mise à jour.");
	}
	
	@Test
	void relationTest() throws Exception {
		//ARRANGE
		//ACT
		String viewName = loginController.relation();
		//ASSERT
		assertEquals("relation", viewName);
	}
	
	@Test
	void addRelationTest() throws Exception {
		String email = "test@test.fr";
		
		HashMap<String, String>successResponse = new HashMap<>();
		successResponse.put("success", "Relation ajoutée");
		//ACT
		when(userService.addContactByEmail(email)).thenReturn(successResponse);
		String viewName = loginController.addRelation(email, redirectAttributes);
		//ASSERT
		verify(userService, times(1)).addContactByEmail(email);
		verify(redirectAttributes, times(1)).addFlashAttribute("success", "Relation ajoutée");
		assertEquals("redirect:/relation", viewName);
	}
	
	@Test
	void addRelationFailedTest() throws Exception {
		String email = "test@test.fr";
		
		HashMap<String, String>errorResponse = new HashMap<>();
		errorResponse.put("error", "Une erreur inattendue est survenue : ");
		//ACT
		when(userService.addContactByEmail(email)).thenThrow(new RuntimeException());
		String viewName = loginController.addRelation(email, redirectAttributes);
		//ASSERT
		verify(userService, times(1)).addContactByEmail(email);
		assertTrue(errorResponse.containsKey("error"));
	}
	
	@Test
	void showTransactionsTest() throws Exception{
		//ARRANGE
		User user = new User();
		Transaction transaction = new Transaction();
		List<Transaction>transactions = new ArrayList<>();
		transactions.add(transaction);
		user.setTransactions(transactions);
		//ACT
		when(transactionService.getAllTransactions()).thenReturn(transactions);
		when(userService.getCurrentUser()).thenReturn(user);
		String viewName = loginController.showTransactions(model, principal);
		//ASSERT
		verify(userService, times(1)).getCurrentUser();
		verify(transactionService, times(1)).getAllTransactionsFromUser(user);
		assertNotNull(user.getTransactions());
		
	}
	
	@Test
	void createTransactionTest() throws Exception{
		//ARRANGE
		Transaction transaction = new Transaction();
		
		HashMap<String, String>successResponse = new HashMap<>();
		successResponse.put("success", "💸 Paiement effectué 💸");

		when(transactionService.addTransaction(transaction)).thenReturn(successResponse);
		
		//ACT
		String viewName = loginController.createTransaction(transaction, redirectAttributes);
		
		//ASSERT
		verify(transactionService, times(1)).addTransaction(transaction);
		assertTrue(successResponse.containsKey("success"));
		
		
	}
	
	@Test
	void createTransactionFailedTest() throws Exception{
		//ARRANGE
		Transaction transaction = new Transaction();
		
		HashMap<String, String>successResponse = new HashMap<>();
		successResponse.put("error", "");

		when(transactionService.addTransaction(transaction)).thenThrow(new RuntimeException());
		
		//ACT
		String viewName = loginController.createTransaction(transaction, redirectAttributes);
		
		//ASSERT
		verify(transactionService, times(1)).addTransaction(transaction);
		assertTrue(successResponse.containsKey("error"));
		
		
	}
	
		
	@Test
	void error403Test() throws Exception {
		//ARRANGE
		//ACT
		String viewName = loginController.showAccessDeniedPage();
		//ASSERT
		assertEquals("403", viewName);
	}
	
	

}
