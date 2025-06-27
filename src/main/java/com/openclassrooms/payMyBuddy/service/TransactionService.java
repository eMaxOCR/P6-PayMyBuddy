package com.openclassrooms.payMyBuddy.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	@Autowired
	@Value("${transaction.fee.percentage}")
	private BigDecimal FEEPOURCENT;

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
	@Transactional															//Commits & Roll back
	public Boolean addTransaction(Transaction transaction) {
		User currentUser = userService.getCurrentUser();					//Get current user who made the transaction
		BigDecimal userBalance = currentUser.getAccount().getBalance();		//eg. 10
		BigDecimal transactionAmount = transaction.getAmount();				//eg. 10
		BigDecimal feeAmount =  transactionAmount.multiply(FEEPOURCENT);	//eg. 10 * 0.005 = 0.05€		
		
		if(	userBalance.compareTo(transactionAmount.add(feeAmount)) >= 0) {	//Check if current user account can handle transaction's amount with fee
			transaction.setSender(currentUser);
			
			Account currentUserAccount = currentUser.getAccount();																			//Get current user Account.
			currentUserAccount.setBalance(userBalance.subtract(transactionAmount.add(feeAmount)).setScale(2, RoundingMode.HALF_UP));		//Deduce transaction amount from current user.
			userService.save(currentUser);																									//Save current user balance.
			
			if(userService.getById(transaction.getReceiver().getId()).isPresent()) {														//Verify if user already exist to avoid hack
				User receiverUser = transaction.getReceiver();																				//Setup receiver User.
				Account receiverUserAccount = receiverUser.getAccount();  																	//Get receiver user account.
				receiverUserAccount.setBalance(receiverUserAccount.getBalance().add(transactionAmount).setScale(2, RoundingMode.HALF_UP));	//Add amount by rounding
				userService.save(receiverUser);																								//Save receiver User.
				
				transactionRepository.save(transaction);																					//Save transaction.
				
				return true;
			}
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
