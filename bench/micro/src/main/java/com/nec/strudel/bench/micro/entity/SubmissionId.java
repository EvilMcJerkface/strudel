/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
