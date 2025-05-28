package com.openclassrooms.payMyBuddy.model;

import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "account")
@DynamicUpdate
@Getter
@Setter
public class Account {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "accountId")
	private int accountId;
	
	@Column(name = "userId")
	private int accountUserId;
	
	@Column(name = "balance")
	private int balance;

}
