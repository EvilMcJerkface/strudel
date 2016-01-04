package com.nec.strudel.bench.auction.entity;


public class ItemId {

	private int sellerId;
	private int itemNo;

	public ItemId(int uid, int itemNo) {
		this.sellerId = uid;
		this.itemNo = itemNo;
	}
	public ItemId() {
	}
	public int getSellerId() {
		return sellerId;
	}
	public void setSellerId(int sellerId) {
		this.sellerId = sellerId;
	}
	public int getItemNo() {
		return itemNo;
	}
	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
	}

	public String toString() {
		return sellerId + "." + itemNo;
	}
	private static final int HASH_BASE = 31;

	@Override
	public int hashCode() {
		return sellerId * HASH_BASE + itemNo;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ItemId) {
			ItemId id = (ItemId) obj;
			return sellerId == id.sellerId && itemNo == id.itemNo;
		}
		return false;
	}
}
