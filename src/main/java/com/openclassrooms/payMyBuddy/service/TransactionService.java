package com.openclassrooms.payMyBuddy.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.repository.TransactionRepository;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	/**
	 * Return all transaction.
	 * @return List of transaction
	 **/
	public Iterable<Transaction> getAllTransactions(){
		return transactionRepository.findAll();
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
	public Transaction addTransaction(Transaction transaction) {
		return transactionRepository.save(transaction);
	}
	
	/**
	 * Delete transaction from database.
	 * */
	public void deleteTransaction(Integer id) {
		transactionRepository.deleteById(id);
	}
}
