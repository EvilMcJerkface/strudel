package com.nec.strudel.entity.test.auction;

public class ItemId {
	private int sellerId;
	private int itemNo;

	public ItemId() {
	}
	public int getItemNo() {
		return itemNo;
	}
	public int getSellerId() {
		return sellerId;
	}
	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
	}
	public void setSellerId(int userId) {
		this.sellerId = userId;
	}
}
