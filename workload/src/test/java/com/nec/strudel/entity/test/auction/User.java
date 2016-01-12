package com.nec.strudel.entity.test.auction;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="USERS")
public class User {

	public User() {
	}
	public User(int userId) {
		this.userId = userId;
	}
	@Id
	private int userId;
	private String name;
	public String getName() {
		return name;
	}
	public int getUserId() {
		return userId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
