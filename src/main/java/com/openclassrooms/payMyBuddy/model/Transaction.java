package com.openclassrooms.payMyBuddy.model;

import java.math.BigDecimal;

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
	
	@ManyToOne //Many transaction for one user.
	@JoinColumn(name = "senderId", referencedColumnName = "id")
	private User sender;
	
	@ManyToOne //Many transaction for one user.
	@JoinColumn(name = "receiverId", referencedColumnName = "id")
	private User receiver;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "amount")
	private BigDecimal amount;
	
}
