package com.nec.strudel.bench.auction.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.nec.strudel.entity.EntityUtil;


@Entity
@Table(name="USERS")
public class User {
	@Id private int userId;
	private String uname;

	public User(int id, String name) {
		setUserId(id);
		setUname(name);
	}
	public User(int id) {
		this.userId = id;
	}
	public User() {
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setUname(String name) {
		this.uname = name;
	}
	public String getUname() {
		return uname;
	}
	@Override
    public int hashCode() {
    	return EntityUtil.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
    	return EntityUtil.equals(this, obj);
    }
    @Override
    public String toString() {
    	return EntityUtil.toString(this);
    }

}
