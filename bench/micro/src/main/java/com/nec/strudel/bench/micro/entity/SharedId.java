package com.nec.strudel.bench.micro.entity;


public class SharedId {
	private int setId;
	private int itemNo;
	public SharedId() {
	}
	public SharedId(int setId, int itemNo) {
		this.setId = setId;
		this.itemNo = itemNo;
	}
	public int getSetId() {
		return setId;
	}
	public void setSetId(int setId) {
		this.setId = setId;
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
		return setId * HASH_BASE + itemNo;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof SharedId) {
			SharedId s = (SharedId) obj;
			return this.setId == s.setId && this.itemNo == s.itemNo;
		}
		return false;
	}
}
