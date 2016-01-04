package com.nec.strudel.bench.micro.entity;

public class PairId {
	private int receiverId;
	private int senderId;
	public PairId() {
	}
	public PairId(int receiverId, int senderId) {
		this.receiverId = receiverId;
		this.senderId = senderId;
	}
	public int getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}
	public int getSenderId() {
		return senderId;
	}
	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	private static final int HASH_BASE = 31;
	@Override
	public int hashCode() {
		return senderId * HASH_BASE + receiverId;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof PairId) {
			PairId pid = (PairId) obj;
			return receiverId == pid.receiverId
					&& senderId == pid.senderId;
		}
		return false;
	}
}
