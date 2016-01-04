package com.nec.strudel.bench.auction.entity;


public class BidId {
	private int sellerId;
	private int itemNo;
	private int bidNo;
	public BidId(ItemId itemId, int bidNo) {
		this.sellerId = itemId.getSellerId();
		this.itemNo = itemId.getItemNo();
		this.bidNo = bidNo;
	}
	public BidId() {
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
	public void setBidNo(int bidNo) {
		this.bidNo = bidNo;
	}
	public int getBidNo() {
		return bidNo;
	}
	@Override
	public String toString() {
		return sellerId + "."
				+ itemNo + "."
				+ bidNo;
	}
	private static final int HASH_BASE = 31;

	@Override
	public int hashCode() {
		int c = sellerId * HASH_BASE + itemNo;
		return c * HASH_BASE + bidNo;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof BidId) {
			BidId id = (BidId) obj;
			return sellerId == id.sellerId
					&& itemNo == id.itemNo
					&& bidNo == id.bidNo;
		}
		return false;
	}
}
