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
package com.nec.strudel.bench.auction.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

import com.nec.strudel.entity.EntityUtil;
import com.nec.strudel.entity.Group;
import com.nec.strudel.entity.On;
import com.nec.strudel.entity.Indexes;

@Group(parent = AuctionItem.class)
@Entity
@Indexes({
	@On(property = "buyerId",
			name = "buyer_auction_idx")
})
@Table(indexes={@Index(columnList="BUYERID")})
@IdClass(ItemId.class)
public class BuyNowAuction {

	@Id private int sellerId;
	@Id private int itemNo;
	private long bnaDate;
	private int buyerId;

	public BuyNowAuction(ItemId itemId) {
		this.sellerId = itemId.getSellerId();
		this.itemNo = itemId.getItemNo();
	}
	public BuyNowAuction(int sellerId, int itemNo) {
		this.sellerId = sellerId;
		this.itemNo = itemNo;
	}
	public BuyNowAuction() {
	}

	public int getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(int buyerId) {
		this.buyerId = buyerId;
	}

	public int getSellerId() {
		return sellerId;
	}

	public int getItemNo() {
		return itemNo;
	}

	public ItemId getItemId() {
		return new ItemId(sellerId, itemNo);
	}

	public long getBnaDate() {
		return bnaDate;
	}

	public void setBnaDate(long bnaDate) {
		this.bnaDate = bnaDate;
	}
	public void setSellerId(int sellerId) {
		this.sellerId = sellerId;
	}
	public void setItemNo(int itemNo) {
		this.itemNo = itemNo;
	}
	@Override
    public int hashCode() {
    	return EntityUtil.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
    	return EntityUtil.equals(this, obj);
    }
    @Override
    public String toString() {
    	return EntityUtil.toString(this);
    }

}
