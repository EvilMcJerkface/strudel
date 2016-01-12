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
