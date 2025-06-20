package com.openclassrooms.payMyBuddy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.openclassrooms.payMyBuddy.model.Transaction;
import com.openclassrooms.payMyBuddy.model.User;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer>{
	
	
	/**
	 * Searching all transaction from User that has been receiver or sender.
	 * @param username.
	 * */
    @Query("SELECT t FROM Transaction t WHERE t.sender = :user AND t.receiver = :user")
    public List<Transaction> findAllByUser(@Param("user") User user);
	
}
