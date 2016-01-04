package com.nec.strudel.bench.micro.entity;

public class ItemId {

	private int userId;
	private int itemNo;

	public ItemId() {
	}
	public ItemId(int userId, int itemNo) {
		this.userId = userId;
		this.itemNo = itemNo;
	}

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getItemNo() {
		return itemNo;
	}
	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
	}

	private static final int HASH_BASE = 31;

	@Override
	public int hashCode() {
		return userId * HASH_BASE + itemNo;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ItemId) {
			ItemId iid = (ItemId) obj;
			return userId == iid.userId && itemNo == iid.itemNo;
		}
		return false;
	}

}
