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
package com.nec.strudel.bench.auction.populate;

import java.util.List;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.populate.AuctionParamSet;
import com.nec.strudel.bench.auction.populate.AuctionParamSet.InParam;
import com.nec.strudel.bench.auction.populate.base.AbstractPopulateAuctionItem;
import com.nec.strudel.bench.test.populate.PopulateUtil;
import com.nec.strudel.workload.api.Populator;

public class AuctionItemParams {
	private final int userId;
	private final int userNum;
	private int itemsPerUser = 1;
	private int bidsPerItem = 1;

	int itemNameLen = 12;
	double initialBid = 100.0;
	int buyNowAdjuster = 1;
	int endDateAdjuster = 1;
	int bidDateAdjuster = 1;
	int bnaDateAdjuster = 1;
	int buynowRatio = 100;

	public AuctionItemParams(int userId, int userNum) {
		this.userId = userId;
		this.userNum = userNum;
	}


	public PopulateUtil.ParamBuilder getParam() {
    	PopulateUtil.ParamBuilder pb = PopulateUtil.param(userId)
    		.param(InParam.ITEMS_PER_USER.name(), itemsPerUser)
    		.param(InParam.ITEM_NAME_LEN.name(), itemNameLen)
    		.param(InParam.INITIAL_BID.name(), initialBid)
    		.param(InParam.BUYNOW_ADJUSTER.name(), buyNowAdjuster)
    		.param(InParam.END_DATE_ADJUSTER.name(), endDateAdjuster)
    		.param(InParam.BIDS_PER_ITEM.name(), bidsPerItem)
    		.param(InParam.USER_NUM.name(), userNum)
    		.param(InParam.BID_DATE_ADJUSTER.name(), bidDateAdjuster)
    		.param(InParam.BUYNOW_RATIO.name(), buynowRatio)
    		.param(InParam.BNA_DATE_ADJUSTER.name(), bnaDateAdjuster);
    	return pb;    		
	}
	public AuctionParamSet create(Populator<?, AuctionParamSet> pop) {
		return pop.createParameter(getParam().build());
	}
	public AuctionParamSet create() {
		return create(new AbstractPopulateAuctionItem<Object>() {

			@Override
			public void process(Object db, AuctionParamSet param) {
			}

			@Override
			protected List<AuctionItem> getItemsBySeller(Object db, int sellerId) {
				return null;
			}

			@Override
			protected AuctionItem getItem(Object db, ItemId id) {
				return null;
			}

			@Override
			protected BuyNowAuction getBuyNowAuction(Object db, ItemId id) {
				return null;
			}

			@Override
			protected List<Bid> getBidsByItemId(Object db, ItemId id) {
				return null;
			}
		});
	}
	public AuctionItemParams setItemsPerUser(int itemsPerUser) {
		this.itemsPerUser = itemsPerUser;
		return this;
	}
	public AuctionItemParams setBuynowRatio(int buynowRatio) {
		this.buynowRatio = buynowRatio;
		return this;
	}
	public AuctionItemParams setNumOfBids(int bidsPerItem) {
		this.bidsPerItem = bidsPerItem;
		return this;
	}
	
	public int getItemsPerUser() {
		return itemsPerUser;
	}

	public int sellerId() {
		return userId;
	}
	double getInitialBid() {
		return initialBid;
	}

	public int numOfBids() {
		return bidsPerItem;
	}


}