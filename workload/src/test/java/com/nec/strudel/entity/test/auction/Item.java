package com.nec.strudel.entity.test.auction;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import com.nec.strudel.entity.Group;
import com.nec.strudel.entity.GroupId;

@Group(parent = User.class)
@Entity
@IdClass(ItemId.class)
public class Item {

	public Item() {
	}
	public Item(int sellerId, int itemNo) {
		this.sellerId = sellerId;
		this.itemNo = itemNo;
	}
	@Id @GroupId
	private int sellerId;
	@Id
	private int itemNo;
	private double maxBid;
	private double minBid;

	public int getItemNo() {
		return itemNo;
	}
	public int getSellerId() {
		return sellerId;
	}
	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
	}
	public void setSellerId(int sellerId) {
		this.sellerId = sellerId;
	}
	public double getMaxBid() {
		return maxBid;
	}
	public void setMaxBid(double maxBid) {
		this.maxBid = maxBid;
	}
	public double getMinBid() {
		return minBid;
	}
	public void setMinBid(double minBid) {
		this.minBid = minBid;
	}
}
