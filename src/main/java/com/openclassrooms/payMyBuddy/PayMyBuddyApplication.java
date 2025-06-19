package com.openclassrooms.payMyBuddy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.openclassrooms.payMyBuddy.service.AccountService;
import com.openclassrooms.payMyBuddy.service.TransactionService;
import com.openclassrooms.payMyBuddy.service.UserService;

@SpringBootApplication
public class PayMyBuddyApplication implements CommandLineRunner{
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private UserService userService;
	

	public static void main(String[] args) {
		SpringApplication.run(PayMyBuddyApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception{
		
		System.out.println("Serveur Online !");
		
	}

}
