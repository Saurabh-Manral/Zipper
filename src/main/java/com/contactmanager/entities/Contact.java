package com.contactmanager.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CONTACT")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int cid;
	@NotNull(message = "Name should not be blank")
	@Size(min = 3, max = 30, message = "Name must be 3 to 30 characters long")
	public String name;
	public String secondName;
	@NotNull(message = "Work should not be blank")
	@Size(min = 3, max = 30, message = "Work must be 3 to 15 characters long")
	public String work;
	@Column(unique = true)
	@NotNull(message = "Email should not be blank")
	public String email;
	@Column(unique = true)
	@NotNull(message = "Phone should not be blank")
	@Size(message = "Phone number should be 10 number long")
	public String phone;
	public String image;
	@NotNull(message = "Description should not be blank")
	@Size(max = 1000, message = "Description should be 1 to 1000 characters long")
	public String description;
	
	@ManyToOne
	public User user;
	
	//getter and setters
	public void setUser(User user) {
		this.user = user;
	}
	public User getUser() {
		return this.user;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	//default constructor
	public Contact() {
		
	}
	
	//all args constructor
	public Contact(int cid, String name, String secondName, String work, String email, String phone, String image,
			String description, User user) {
		super();
		this.cid = cid;
		this.name = name;
		this.secondName = secondName;
		this.work = work;
		this.email = email;
		this.phone = phone;
		this.image = image;
		this.description = description;
		this.user = user;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.cid==((Contact)obj).getCid();
	}
	
	
}
