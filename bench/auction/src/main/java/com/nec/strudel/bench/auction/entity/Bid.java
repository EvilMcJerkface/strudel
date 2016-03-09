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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.nec.strudel.entity.EntityUtil;
import com.nec.strudel.entity.Group;
import com.nec.strudel.entity.GroupId;
import com.nec.strudel.entity.GroupIdClass;
import com.nec.strudel.entity.On;
import com.nec.strudel.entity.Indexes;

@Group(parent = AuctionItem.class)
@Entity
@Indexes({
        @On(property = "auctionItemId", auto = true, name = "auction_bid_idx"),
        @On(property = "userId", name = "bidder_bid_idx")
})
@Table(indexes = {
        @Index(columnList = "SELLERID,ITEMNO"),
        @Index(columnList = "USERID") })
@GroupIdClass(ItemId.class)
@IdClass(BidId.class)
public class Bid {
    @GroupId
    @Id
    private int sellerId;
    @GroupId
    @Id
    private int itemNo;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int bidNo;
    private double bidAmount;
    private long bidDate;
    private int userId;

    /**
     * This join column is used to demonstrate the difference between JPA and
     * EntityDB. Currently, EntityDB does not support join queries and ignores
     * this column.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERID", insertable = false, updatable = false)
    private User user;

    public Bid(ItemId itemId, int bidNo) {
        this.sellerId = itemId.getSellerId();
        this.itemNo = itemId.getItemNo();
        this.bidNo = bidNo;
    }

    public Bid(ItemId itemId) {
        this.sellerId = itemId.getSellerId();
        this.itemNo = itemId.getItemNo();
    }

    public Bid() {
    }

    public BidId getId() {
        return new BidId(getAuctionItemId(), bidNo);
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

    public ItemId getAuctionItemId() {
        return new ItemId(sellerId, itemNo);
    }

    public int getBidNo() {
        return bidNo;
    }

    public void setBidNo(int bidNo) {
        this.bidNo = bidNo;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(double bidAmount) {
        this.bidAmount = bidAmount;
    }

    public long getBidDate() {
        return bidDate;
    }

    public void setBidDate(long bidDate) {
        this.bidDate = bidDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
