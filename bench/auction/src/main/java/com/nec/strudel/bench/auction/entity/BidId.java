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
        int code = sellerId * HASH_BASE + itemNo;
        return code * HASH_BASE + bidNo;
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
