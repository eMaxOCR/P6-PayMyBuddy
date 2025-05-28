package com.openclassrooms.payMyBuddy.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.repository.AccountRepository;

@Service
public class AccountService {
	
	@Autowired
	private AccountRepository accountRepository;
	
	/**
	 * Return all accounts.
	 * @return List of accounts
	 **/
	public Iterable<Account> getAllAccounts(){
		return accountRepository.findAll();
	}
	
	/**
	 * Return one account that match id.
	 * @Return One account.
	 * */
	public Optional<Account> Account(Integer id){
		return accountRepository.findById(id);
	}
	
	/**
	 * Add account into database.
	 * @return Account
	 * */
	public Account addAccount(Account account) {
		return accountRepository.save(account);
	}
	
	/**
	 * Delete account from database.
	 * */
	public void deleteAccount(Integer id) {
		accountRepository.deleteById(id);
	}

}
