package com.openclassrooms.payMyBuddy.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.TransactionRepository;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import com.openclassrooms.payMyBuddy.service.UserService;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
	
	@Mock
	private TransactionRepository transactionRepository;
	
	@Mock
	private UserService userService;
	
	private TransactionService transactionService;
	
	private List<Transaction> allTransactionList = new ArrayList<>();
	private List<Transaction> userTransactionList = new ArrayList<>();
	private User user = new User();
	BigDecimal TEST_FEE_PERCENT = new BigDecimal("0.000"); //0.005
		
	@BeforeEach
	public void setup() {
		//Create fake transactions
		Transaction transaction1 = new Transaction();
		transaction1.setId(1);
		Transaction transaction2 = new Transaction();
		
		allTransactionList.add(transaction1);
		allTransactionList.add(transaction2);
		
		//Create User with transaction.
		List<Transaction> userTransaction = new ArrayList<>();
		userTransaction.add(transaction1);
		user.setTransactions(userTransaction);
		
		this.transactionService = new TransactionService(transactionRepository, userService, TEST_FEE_PERCENT);
		
	}
	
	@Test
	void getAllTransactionsTest() {
		when(transactionRepository.findAll()).thenReturn(allTransactionList);
		
		Iterable<Transaction> transactions = transactionService.getAllTransactions();
		
		assertEquals(transactions, allTransactionList);
	}
	
	@Test
	void getAllTransactionsFromUserTest() {
		when(transactionRepository.findAllByUser(user)).thenReturn(userTransactionList);
		
		Iterable<Transaction> transactions = transactionService.getAllTransactionsFromUser(user);
		
		assertEquals(transactions, userTransactionList);
	}
	
	@Test
	void deleteTransactionTest() {
		transactionService.deleteTransaction(1);
		
		verify(transactionRepository, times(1)).deleteById(1);
	}
	
	@Test
	void addTransactionTest() {
		//ARRANGE
		//Main user
		User user = new User();
		Account account = new Account();
		account.setBalance(new BigDecimal(100));
		user.setAccount(account);
		
		//Receiver user
		User userReceiver = new User();
		userReceiver.setId(1);
		Account accountReceiver = new Account();
		accountReceiver.setBalance(new BigDecimal(100));
		userReceiver.setAccount(account);
		
		//Create transaction
		Transaction transaction  = new Transaction();
		transaction.setAmount(new BigDecimal(10));
		transaction.setReceiver(userReceiver);
		
		when(userService.getCurrentUser()).thenReturn(user);
		when(userService.getById(1)).thenReturn(Optional.of(userReceiver));
		
		//ACT
		HashMap<String, String> result = transactionService.addTransaction(transaction);
		
		//ASSERT
		assertNotNull(result);
		assertTrue(result.containsKey("success"));
	}
	
	@Test
	void addTransactionNullTest() {
		//ARRANGE
		//Main user
		User user = new User();
		Account account = new Account();
		account.setBalance(new BigDecimal(100));
		user.setAccount(account);
		
		//Receiver user
		User userReceiver = new User();
		userReceiver.setId(1);
		Account accountReceiver = new Account();
		accountReceiver.setBalance(new BigDecimal(100));
		userReceiver.setAccount(account);
		
		//Create transaction
		Transaction transaction  = new Transaction();
		transaction.setAmount(null);
		transaction.setReceiver(userReceiver);
		
		when(userService.getCurrentUser()).thenReturn(user);
		
		//ACT
		HashMap<String, String> result = transactionService.addTransaction(transaction);
		
		//ASSERT
		assertNotNull(result);
		assertTrue(result.containsKey("error"));
	}
	
	@Test
	void addTransactionZeroTest() {
		//ARRANGE
		//Main user
		User user = new User();
		Account account = new Account();
		account.setBalance(new BigDecimal(100));
		user.setAccount(account);
		
		//Receiver user
		User userReceiver = new User();
		userReceiver.setId(1);
		Account accountReceiver = new Account();
		accountReceiver.setBalance(new BigDecimal(100));
		userReceiver.setAccount(account);
		
		//Create transaction
		Transaction transaction  = new Transaction();
		transaction.setAmount(new BigDecimal(0));
		transaction.setReceiver(userReceiver);
		
		when(userService.getCurrentUser()).thenReturn(user);
		
		//ACT
		HashMap<String, String> result = transactionService.addTransaction(transaction);
		
		//ASSERT
		assertNotNull(result);
		assertTrue(result.containsKey("error"));
	}
	
	@Test
	void addTransactionNotEnougthMoneyTest() {
		//ARRANGE
		//Main user
		User user = new User();
		Account account = new Account();
		account.setBalance(new BigDecimal(0));
		user.setAccount(account);
		
		//Receiver user
		User userReceiver = new User();
		userReceiver.setId(1);
		Account accountReceiver = new Account();
		accountReceiver.setBalance(new BigDecimal(100));
		userReceiver.setAccount(account);
		
		//Create transaction
		Transaction transaction  = new Transaction();
		transaction.setAmount(new BigDecimal(100));
		transaction.setReceiver(userReceiver);
		
		when(userService.getCurrentUser()).thenReturn(user);
		
		//ACT
		HashMap<String, String> result = transactionService.addTransaction(transaction);
		
		//ASSERT
		assertNotNull(result);
		assertTrue(result.containsKey("error"));
	}

}
