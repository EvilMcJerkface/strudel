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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

import com.nec.strudel.entity.EntityUtil;
import com.nec.strudel.entity.On;
import com.nec.strudel.entity.Indexes;

@Entity
@Indexes({
	@On(property = "sellerId", auto = true,
			name = "seller_auction_idx")
})
@Table(indexes={@Index(columnList="SELLERID")})
@IdClass(ItemId.class)
public class AuctionItem {
	public static final double MAX_BID_SOLD = -1;
	@Id private int sellerId;
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int itemNo;
	private String itemName;
	private double buyNow;
	private double initialBid;
	private double maxBid;
	private long endDate;

	public static boolean isSold(AuctionItem item) {
		return item.getMaxBid() == MAX_BID_SOLD;
	}
	public static void sell(AuctionItem item) {
		item.setMaxBid(MAX_BID_SOLD);
	}
	public AuctionItem(int sellerId, int itemNo) {
		this.sellerId = sellerId;
		this.itemNo = itemNo;
	}
	public AuctionItem(int sellerId) {
		this.sellerId = sellerId;
	}

	public AuctionItem() {
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

	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public double getBuyNow() {
		return buyNow;
	}

	public void setBuyNow(double buyNow) {
		this.buyNow = buyNow;
	}

	public double getInitialBid() {
		return initialBid;
	}

	public void setInitialBid(double initialBid) {
		this.initialBid = initialBid;
	}

	public double getMaxBid() {
		return maxBid;
	}

	public void setMaxBid(double maxBid) {
		this.maxBid = maxBid;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
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
