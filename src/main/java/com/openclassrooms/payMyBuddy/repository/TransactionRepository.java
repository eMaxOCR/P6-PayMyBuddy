package com.openclassrooms.payMyBuddy.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.openclassrooms.payMyBuddy.model.Transaction;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer>{

}
