package com.openclassrooms.payMyBuddy.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private UserService userService;

	/**
	 * Return all transaction.
	 * @return List of transaction
	 **/
	public Iterable<Transaction> getAllTransactions(){
		return transactionRepository.findAll();
	}
	
	/**
	 * Return all transaction that user is sender or receiver.
	 * @return List of transaction
	 **/
	public Iterable<Transaction> getAllTransactionsFromUser(User user){
		return transactionRepository.findAllByUser(user);
	}
	
	/**
	 * Return one transaction that match id.
	 * @Return One transaction.
	 * */
	public Optional<Transaction> getTransactionById(Integer id){
		return transactionRepository.findById(id);
	}
	
	/**
	 * Add transaction into database.
	 * @return transaction
	 * */
	@Transactional	//Commits & Roll back
	public Boolean addTransaction(Transaction transaction) {
		User currentUser = new User();
		currentUser = userService.getCurrentUser();
		Double userBalance = currentUser.getAccount().getBalance();		
			
		if(	transaction.getAmount() <= currentUser.getAccount().getBalance()) {								//Check current user account.
			transaction.setSender(currentUser);
			
			Account currentUserAccount = currentUser.getAccount();											//Get current user Account.
			currentUserAccount.setBalance(userBalance - transaction.getAmount());							//Deduce transaction amount from current user.
			userService.save(currentUser);																	//Save current user balance.
			
			User receiverUser = new User();																	//Setup receiver User.
			receiverUser = transaction.getReceiver();
			Account receiverUserAccount = receiverUser.getAccount();  										//Get receiver user account.
			receiverUserAccount.setBalance(receiverUserAccount.getBalance() + transaction.getAmount());		//Add amout.
			userService.save(receiverUser);																	//Save receiver User.
			
			transactionRepository.save(transaction);														//Save transaction.
			
			return true;
		}
		
		return false;			
	}
	
	/**
	 * Delete transaction from database.
	 * */
	public void deleteTransaction(Integer id) {
		transactionRepository.deleteById(id);
	}
}
