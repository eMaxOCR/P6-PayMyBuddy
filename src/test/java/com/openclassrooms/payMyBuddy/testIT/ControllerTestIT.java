package com.openclassrooms.payMyBuddy.testIT;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.AccountService;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order; 
import java.math.BigDecimal;
import java.util.Optional;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerTestIT {
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	UserService userService;
	
	@Autowired
	TransactionService transactionService;	
	
	@Autowired
	AccountService accountService;
	

	
	@Test
	@Order(1)
	public void signUpTest()throws Exception {
		String email = "user1@test.test";
		
		mockMvc.perform(post("/signup").with(csrf())
			.param("username", "testuser")
			.param("email", email)
			.param("password", "test"))
			.andExpect(redirectedUrl("/signup"));
		
		 Optional<User> createdUser = userService.getByEmailAddress(email);
	     assertTrue(createdUser.isPresent());
	}
	
	@Test
	@Order(2)
	public void signUpTest2Test()throws Exception {
		String email = "user2@test.test";
		
		mockMvc.perform(post("/signup").with(csrf())
			.param("username", "testuser2")
			.param("email", email)
			.param("password", "test2"))
			.andExpect(redirectedUrl("/signup"));
		
		 Optional<User> createdUser = userService.getByEmailAddress(email);
	     assertTrue(createdUser.isPresent());
	}

	
	@Test
	@Order(3)
	public void addRelationForUser1Test()throws Exception {
		String user1Mail = "user1@test.test";
		//Connection to user1
		MvcResult result = mockMvc.perform(formLogin("/login").user(user1Mail).password("test"))
				.andExpect(redirectedUrl("/transfert"))
				.andReturn();
		MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
		
		//Adding the user2 to user1's relations.
		mockMvc.perform(post("/relation").with(csrf()).session(session)
				.param("email", "user2@test.test"))
				.andExpect(redirectedUrl("/relation"));
		
		Optional<User> user1 = userService.getByEmailAddress(user1Mail);
		assertNotNull(transactionService.getAllTransactionsFromUser(user1.get()));

	}
	
	@Test
	@Order(4)
	public void sendMoneyToUser2FromUser1Test()throws Exception {
		String user1Mail = "user1@test.test";
		//Connection to user1
		MvcResult result = mockMvc.perform(formLogin("/login").user(user1Mail).password("test"))
				.andExpect(redirectedUrl("/transfert"))
				.andReturn();
		MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
		
		//Adding money for transaction.
		Optional<User> user1Optional = userService.getByEmailAddress(user1Mail);
		User user1 = user1Optional.get();
		Account user1Account = user1.getAccount();
		user1Account.setBalance(new BigDecimal(45));
		userService.save(user1);
		
		//Send money for user 2.
		mockMvc.perform(post("/transfert").with(csrf()).session(session)
				.param("receiver", "2")
				.param("description", "Test transaction")
				.param("amount", "45"))
				.andExpect(redirectedUrl("/transfert"));
		
		Double transactionAmount = null;
		for(Transaction x:transactionService.getAllTransactionsFromUser(user1)) {
			transactionAmount = x.getAmount().doubleValue();
		}
		assertEquals(45, transactionAmount);
	}
	
	@Test
	@Order(5)
	public void updateProfil() throws Exception {
		String currentUser1Mail = "user1@test.test";
		String newUsername = "user2Updated";
		String newMail = "user2updated@test.test";
		String newPassword = "newPassword";
		//Connection to user1
		MvcResult result = mockMvc.perform(formLogin("/login").user(currentUser1Mail).password("test"))
				.andExpect(redirectedUrl("/transfert"))
				.andReturn();
		MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
		
		mockMvc.perform(post("/profil").with(csrf()).session(session)
				.param("username", newUsername)
				.param("email", newMail)
				.param("password", newPassword))
				.andExpect(redirectedUrl("/login"));
		
		Optional<User> userUpdated = userService.getByEmailAddress(newMail);
		assertEquals(newUsername,userUpdated.get().getUsername());
		assertEquals(newMail,userUpdated.get().getEmail());
		
	}
	
}
