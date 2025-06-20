package com.openclassrooms.payMyBuddy.model;

import org.hibernate.annotations.DynamicUpdate;
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
	private Double balance;
		
	//Link USER
	@OneToOne //One ACCOUNT for one USER.
	@JoinColumn(name = "userId", referencedColumnName = "id")
	private User user;

}
