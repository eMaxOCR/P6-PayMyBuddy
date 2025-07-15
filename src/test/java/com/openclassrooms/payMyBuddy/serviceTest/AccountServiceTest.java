package com.openclassrooms.payMyBuddy.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.repository.AccountRepository;
import com.openclassrooms.payMyBuddy.service.AccountService;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
	
	@Mock
	AccountRepository accountRepository;
	
	@InjectMocks
	AccountService accountService;
	
	ArrayList<Account> accountList = new ArrayList<>();
	Account mainAccount;
	
	@BeforeEach
	public void setupTest() {
		Account account1 = new Account();
		account1.setId(1);
		Account account2 = new Account();
		
		accountList.add(account1);
		accountList.add(account2);
		
		mainAccount = account1;
		
	}
	
	@Test
	void getAllAccountsTest() {
		//ARRANGE
		when(accountService.getAllAccounts()).thenReturn(accountList);
		//ACT
		Iterable<Account> accounts = accountService.getAllAccounts();
		//ASSERT
		assertEquals(accounts, accountList);
		
	}
	
	@Test
	void addAccountTest() {
		//ARRANGE
		when(accountService.addAccount(any(Account.class))).thenReturn(mainAccount);
		//ACT
		Account newAccount = accountService.addAccount(mainAccount);
		//ASSERT
		assertEquals(newAccount, mainAccount);
		
	}
	
	@Test
	void deleteAccount() {
		//ACT
		accountService.deleteAccount(1);
		//ASSERT
		verify(accountRepository, times(1)).deleteById(1);
		
	}
	

}
