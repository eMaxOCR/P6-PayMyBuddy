package com.openclassrooms.payMyBuddy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	private Integer id;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "email", unique = true)
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
			mappedBy = "sender",
			cascade = CascadeType.ALL
			)
	private List<Transaction> transactions = new ArrayList<>();
	
	
	//Link CONTACT
	@ManyToMany
	@JoinTable(		
			name = "contact",											//Joint Table's name
			joinColumns = @JoinColumn(name = "userId"),					//Foreignkey from joint table.
			inverseJoinColumns = @JoinColumn(name = "contactUserId")	//Foreignkey from joint table of the second entity.
		
			)
	private List<User> contacts = new ArrayList<>();
	
	/**
	 * Add USER into CONTACT
	 * @param USER
	 * */
	public Boolean addContact(Optional<User> contactOptional) {
		if(contactOptional.isPresent()) {				//Check if already exist
			User contactToAdd = contactOptional.get();	//Convert Optional<User> to User.
			contacts.add(contactToAdd);					//Add USER into contact.
			return true;
		}else {
			return false;
		}
	}	
	
}
