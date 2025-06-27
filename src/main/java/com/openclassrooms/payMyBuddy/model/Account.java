package com.openclassrooms.payMyBuddy.model;

import java.math.BigDecimal;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "balance")
	private BigDecimal balance;
		
	//Link USER
	@OneToOne(								//One ACCOUNT for one USER.
			cascade = CascadeType.ALL,		//All actions taken from product will affect comment. (For CRUD actions, and into linked objects (comments)
			orphanRemoval = true			//If product deleted, comment will be deleted too.)
			)
	
	@JoinColumn(name = "userId", referencedColumnName = "id")
	private User user;

}
