package com.nec.strudel.bench.auction.entity;

public class SaleId {

	private int sellerId;
	private int itemNo;
	private int bnsNo;
	public SaleId(ItemId itemId, int bnsNo) {
		this.sellerId = itemId.getSellerId();
		this.itemNo = itemId.getItemNo();
		this.bnsNo = bnsNo;
	}
	public SaleId() {
	}

	public ItemId getItemId() {
		return new ItemId(sellerId, itemNo);
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

	public int getBnsNo() {
		return bnsNo;
	}
	public void setBnsNo(int bnsNo) {
		this.bnsNo = bnsNo;
	}

	@Override
	public String toString() {
		return sellerId + "."
				+ itemNo + "."
				+ bnsNo;
	}
	private static final int HASH_BASE = 31;

	@Override
	public int hashCode() {
		int c = sellerId * HASH_BASE + itemNo;
		return c * HASH_BASE + bnsNo;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof SaleId) {
			SaleId id = (SaleId) obj;
			return sellerId == id.sellerId
					&& itemNo == id.itemNo
					&& bnsNo == id.bnsNo;
		}
		return false;
	}
}
