package com.nec.strudel.bench.micro.entity;


public class SubmissionId {

	private int receiverId;
	private int senderId;
	private int submitNo;

	public SubmissionId() {
	}
	public SubmissionId(int receiverId, int senderId, int submitNo) {
		this.receiverId = receiverId;
		this.senderId = senderId;
		this.submitNo = submitNo;
	}
	public PairId getPairId() {
		return new PairId(receiverId, senderId);
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
	public int getSubmitNo() {
		return submitNo;
	}
	public void setSubmitNo(int submitNo) {
		this.submitNo = submitNo;
	}
	private static final int HASH_BASE = 31;
	@Override
	public int hashCode() {
		int c = receiverId * HASH_BASE + senderId;
		c = c * HASH_BASE + submitNo;
		return c;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof SubmissionId) {
			SubmissionId sid = (SubmissionId) obj;
			return receiverId == sid.receiverId
					&& senderId == sid.senderId
					&& submitNo == sid.submitNo;
		}
		return false;
	}
}
