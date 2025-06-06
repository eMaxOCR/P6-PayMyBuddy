package com.openclassrooms.payMyBuddy.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user")
@DynamicUpdate
@Getter
@Setter
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "role")
	private String role;
	
	//Link ACCOUNT
	@OneToOne(									//One user for one account.
				mappedBy = "user",				//Linked by ACCOUNT entity.
				cascade = CascadeType.ALL		//All actions taken from USER will affect ACCOUNT. 
			)
	@JoinColumn(name = "userId", referencedColumnName = "id")
	private Account account;
	
	
	//Link TRANSACTION
	@OneToMany(
			mappedBy = "senderId",
			cascade = CascadeType.ALL
			)
	private List<Transaction> transactions = new ArrayList<>();
	
	
	//Link CONTACT
	@ManyToMany(							//USER can have many CONTACT, and CONTACT can have many USERS.
			fetch = FetchType.LAZY,			//When category is searched, it didn't take products with it. (Best performance)
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE,
					}
			)
	@JoinTable(		
			name = "contact",										//Joint Table's name
			joinColumns = @JoinColumn(name = "contactUserId"),		//Foreignkey from joint table.
			inverseJoinColumns = @JoinColumn(name = "userId")		//Foreignkey from joint table of the second entity.
		
			)
	private List<User> contact = new ArrayList<>();
	
	
}
