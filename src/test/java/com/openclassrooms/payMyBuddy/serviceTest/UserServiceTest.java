package com.openclassrooms.payMyBuddy.serviceTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import com.openclassrooms.payMyBuddy.service.AccountService;
import com.openclassrooms.payMyBuddy.service.UserService;

@ExtendWith(MockitoExtension.class) 
public class UserServiceTest {
	
	@Mock 	//Actor
	UserRepository userRepository;
	
	@Mock
	private SecurityContext securityContext;
	
	@Mock
	private Authentication authentication;
	
	@Mock 
    private BCryptPasswordEncoder passwordEncoder;
	
	@Mock
	private AccountService accountService;
	
	@Spy			//To test addContactByEmail that use other functions.
	@InjectMocks	//Realisator that hire actor. It inject userRepository. 
	UserService userService;
	
	ArrayList<User> userList = new ArrayList<>();
	User user1;
	User user2;
	User newUser;
	
	@BeforeEach
	public void setupTest() {
		//ARRANGE
		//Create first fake user.
		User setUpUser1 = new User();
		Account user1Account = new Account();
		setUpUser1.setId(1);
		setUpUser1.setUsername("user1");
		setUpUser1.setPassword("password");
		setUpUser1.setEmail("user1@test.fr");
		setUpUser1.setAccount(user1Account);
		List<User>contacts = new ArrayList<>();
		setUpUser1.setContacts(contacts);
		
		//Create second fake user.
		User setUpUser2 = new User();
		Account user2Account = new Account();
		setUpUser2.setId(1);
		setUpUser2.setUsername("user1");
		setUpUser2.setPassword("password");
		setUpUser2.setEmail("user2@test.fr");
		setUpUser2.setAccount(user2Account);
		setUpUser2.setContacts(null);
		
		//Create third fake user
		User newUserSetup = new User();
		Account user3Account = new Account();
		newUserSetup.setId(3);
		newUserSetup.setUsername("user3");
		newUserSetup.setPassword("password");
		newUserSetup.setEmail("newuser@test.fr");
		newUserSetup.setAccount(user3Account);
		newUserSetup.setContacts(null);
		
		userList.add(setUpUser1);
		userList.add(setUpUser2);

		user1=setUpUser1;
		user2=setUpUser2;
		newUser=newUserSetup;

	}
	
	@BeforeEach
    void setupPerTest() {
       SecurityContextHolder.setContext(securityContext);
    }
	
	
	@Test
	void getAllTest() {
		//ARRANGE
		when(userService.getAll()).thenReturn(userList);
		//ACT
		Iterable<User> actualUsers = userService.getAll();
		//ASSERT
		assertEquals(actualUsers, userList);
	}
	
	@Test
	void getByIdTest() {
		//ARRANGE
		when(userService.getById(1)).thenReturn(Optional.of(user1));
		//ACT
		Optional<User> actualUser = userService.getById(1);
		//ASSERT
		assertEquals(actualUser, Optional.of(user1));
	}
	
	@Test
	void getByEmailAddressTest() {
		//ARRANGE
		when(userService.getByEmailAddress(any(String.class))).thenReturn(Optional.of(user1));
		//ACT
		Optional<User> userFound = userService.getByEmailAddress("user1@test.fr");
		//ASSERT
		assertEquals(userFound, Optional.of(user1));
	}
	
	@Test 
	void getCurrentUserTest(){
		//ARRANGE
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn("user1@test.fr");
		when(userRepository.findByEmail("user1@test.fr")).thenReturn(Optional.of(user1));
		//ACT
		User currentUser = userService.getCurrentUser();
		//ASSERT
		assertNotNull(currentUser);
		assertEquals("user1@test.fr", currentUser.getEmail());
		verify(authentication).getName();
        verify(userRepository).findByEmail("user1@test.fr");
		
	}
	
	@Test 
	void getCurrentUserFailedTest(){
		//ARRANGE
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn("user99@test.fr");
		when(userRepository.findByEmail("user99@test.fr")).thenReturn(Optional.empty());
		//ACT
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            userService.getCurrentUser();
        });

	}
	
	@Test
	void saveTest() {
		//ARRANGE
		when(userService.save(any(User.class))).thenReturn(newUser);
		//ACT
		User savedUser = userService.save(newUser);
		//ASSERT
		assertEquals(savedUser, newUser);
		
	}
	
	@Test
	void deleteTest() {
		//ACT
		userService.delete(1);
		//ASSERT
		verify(userRepository, times(1)).deleteById(1);
		
	}
	
	@Test
	void addContactTest(){
		//ARRANGE
		//setup security
		String email = "user1@test.fr";
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn(email);
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user1));
		
		//ACT
		//We add newUser into user1 contact list.
		Boolean result = userService.addContact(Optional.of(newUser));
		
		//ASSERT
		assertTrue(result);
  
	}
	
	@Test
	void addContactFailedTest(){
		//ARRANGE
		
		//ACT
		//We add newUser into user1 contact list.
		Boolean result = userService.addContact(Optional.empty());
		
		//ASSERT
		assertFalse(result);
  
	}
	
	@Test
	void addContactByEmailTest(){
		//ARRANGE
		String currentUserEmail = "user1@test.fr";
		String contactEmail = "newuser@test.fr";
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn(currentUserEmail);
		when(userRepository.findByEmail(currentUserEmail)).thenReturn(Optional.of(user1));

		//Finding contact
		doReturn(Optional.of(newUser)).when(userService).getByEmailAddress(contactEmail);
		//contact added
		doReturn(true).when(userService).addContact(Optional.of(newUser));
		
		//ACT
		HashMap<String, String> result = userService.addContactByEmail(contactEmail);
		
		//ASSERT
		assertNotNull(result);
		assertTrue(result.containsKey("success"));
  
	}
	
	@Test
	void addContactByEmptyEmailFailedTest(){
		//ARRANGE
		String currentUserEmail = "user1@test.fr";
		String contactToAdd = "";
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn(currentUserEmail);
		when(userRepository.findByEmail(currentUserEmail)).thenReturn(Optional.of(user1));

		//Finding contact
		doReturn(Optional.empty()).when(userService).getByEmailAddress(contactToAdd);
		
		//ACT
		HashMap<String, String> result = userService.addContactByEmail(contactToAdd);
		
		//ASSERT
		assertNotNull(result);
		assertTrue(result.containsKey("error"));
  
	}
	
	@Test
	void addContactBySameMainUserEmailFailedTest(){
		//ARRANGE
		String currentUserEmail = "user1@test.fr";
		String contactEmail = "user1@test.fr";
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn(currentUserEmail);
		when(userRepository.findByEmail(currentUserEmail)).thenReturn(Optional.of(user1));

		//Finding contact
		doReturn(Optional.of(user1)).when(userService).getByEmailAddress(contactEmail);
		
		//ACT
		HashMap<String, String> result = userService.addContactByEmail(contactEmail);
		
		//ASSERT
		assertNotNull(result);
		assertTrue(result.containsKey("error"));
  
	}
	
	@Test
	void addContactByEmailFailedTest(){
		//ARRANGE
		String currentUserEmail = "user1@test.fr";
		String contactEmail = "newuser@test.fr";
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn(currentUserEmail);
		when(userRepository.findByEmail(currentUserEmail)).thenReturn(Optional.of(user1));

		//Finding contact
		doReturn(Optional.of(newUser)).when(userService).getByEmailAddress(contactEmail);
		//contact added
		doReturn(false).when(userService).addContact(Optional.of(newUser));
		
		//ACT
		HashMap<String, String> result = userService.addContactByEmail(contactEmail);
		
		//ASSERT
		assertNotNull(result);
		assertTrue(result.containsKey("error"));
  
	}
	
	@Test
	void updateProfileTest() {
		User newUserInfo = new User();
		newUserInfo.setUsername("BestUser1");
		newUserInfo.setEmail("newinfo@test.fr");
		newUserInfo.setPassword("mdp");
		
		String currentUserEmail = "user1@test.fr";
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn(currentUserEmail);
				
		doReturn(Optional.of(user1)).when(userService).getByEmailAddress(currentUserEmail);
		when(passwordEncoder.encode(newUserInfo.getPassword())).thenReturn("mdphash");
		when(userRepository.save(any(User.class))).thenReturn(user1);
		
		//ACT
		HashMap<String, String> result = userService.updateProfile(newUserInfo);
		assertNotNull(result);
		assertTrue(result.containsKey("success"));
	}
	
	@Test
	void updateProfileFailedTest() {
		String currentUserEmail = "notexist@mail.fr";
		User userUnknown = new User();
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn(currentUserEmail);
				
		doReturn(Optional.empty()).when(userService).getByEmailAddress(currentUserEmail);
		
		//ACT
		HashMap<String, String> result = userService.updateProfile(userUnknown);
		assertNotNull(result);
		assertTrue(result.containsKey("error"));
	}
	
	@Test
	void registerTest() {
		User newUserInfo = new User();
		newUserInfo.setUsername("registerUser");
		newUserInfo.setEmail("signup@test.fr");
		newUserInfo.setPassword("mdp");
		
		Account newUserAccount = new Account();
		
		when(userService.getByEmailAddress(newUserInfo.getEmail())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(newUserInfo.getPassword())).thenReturn("mdphash");
		when(userRepository.save(any(User.class))).thenReturn(newUserInfo);
		when(accountService.addAccount(any(Account.class))).thenReturn(newUserAccount);
		
		HashMap<String, String> result = userService.register(newUserInfo);
		
		assertNotNull(result);
		assertTrue(result.containsKey("success"));
		
	}
	
	@Test
	void registerFailedTest() {
		User newUserInfo = new User();
		newUserInfo.setUsername("registerUser");
		newUserInfo.setEmail("existingmail@test.fr");
		newUserInfo.setPassword("mdp");
		
		when(userService.getByEmailAddress(newUserInfo.getEmail())).thenReturn(Optional.of(newUserInfo));

		HashMap<String, String> result = userService.register(newUserInfo);
		
		assertNotNull(result);
		assertTrue(result.containsKey("error"));
		
	}
	

}

