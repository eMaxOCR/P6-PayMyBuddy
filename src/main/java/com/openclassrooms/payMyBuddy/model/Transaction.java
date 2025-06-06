package com.openclassrooms.payMyBuddy.model;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transaction")
@DynamicUpdate
@Getter
@Setter
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "senderId")
	private int senderId;
	
	@Column(name = "receiverId")
	private int receiverId;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "amount")
	private int amount;
	
	//Link USER
	@ManyToOne //Many TRANSACTIONS for one USER
	@JoinColumn(name = "userId", referencedColumnName = "id")
	private User user;
	
}
