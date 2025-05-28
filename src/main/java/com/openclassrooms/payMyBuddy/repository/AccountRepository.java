package com.openclassrooms.payMyBuddy.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.openclassrooms.payMyBuddy.model.Account;

@Repository
public interface AccountRepository extends CrudRepository<Account, Integer> {

}
