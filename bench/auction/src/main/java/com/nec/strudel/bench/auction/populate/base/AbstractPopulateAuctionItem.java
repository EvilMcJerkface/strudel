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
package com.nec.strudel.bench.auction.populate.base;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.populate.AuctionParamSet;
import com.nec.strudel.bench.auction.populate.AuctionParamSet.AuctionParam;
import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.ValidateReporter;

public abstract class AbstractPopulateAuctionItem<T>
implements Populator<T, AuctionParamSet> {


	@Override
	public String getName() {
		return "AuctionItem";
	}

	@Override
	public AuctionParamSet createParameter(PopulateParam param) {
		return AuctionParamSet.create(param);
	}

	protected abstract List<AuctionItem> getItemsBySeller(T db, int sellerId);
	protected abstract AuctionItem getItem(T db, ItemId id);
	protected abstract BuyNowAuction getBuyNowAuction(T db, ItemId id);
	protected abstract List<Bid> getBidsByItemId(T db, ItemId id);

	@Override
	public boolean validate(T db, AuctionParamSet param,
			ValidateReporter reporter) {
		int sellerId = param.sellerId();
		AuctionParam[] ps = param.getParams();
		List<AuctionItem> items = getItemsBySeller(db, sellerId);
		sort(items);
		if (ps.length != items.size()) {
			diag(param, items, db, reporter);
			return false;
		}
		boolean success = true;
		for (int i = 0; i < ps.length; i++) {
			boolean s = validate(db, ps[i], items.get(i), reporter);
			success = s && success;
		}
		return success;
	}
	/**
	 * NOTE: this assumes that the automatically generated
	 * itemNo values are in the increasing order.
	 */
	protected void sort(List<AuctionItem> items) {
		Collections.sort(items, new Comparator<AuctionItem>() {

			@Override
			public int compare(AuctionItem o1, AuctionItem o2) {
				return o1.getItemNo() - o2.getItemNo();
			}
		});
	}
	protected void diag(AuctionParamSet aps, List<AuctionItem> items,
			T db, ValidateReporter reporter) {
		int sellerId = aps.sellerId();
		AuctionParam[] ps = aps.getParams();
		int size = ps.length;
		if (items.size() > size) {
			reporter.error(
			"get by seller_auction_index size=" + items.size()
				+ " more than expected=" + size
				+ " for seller=" + sellerId);
		} else if (items.isEmpty()) {
			reporter.error(
					"get by seller_auction_index is EMPTY"
					+ " (expected size=" + size
					+ ") for seller="
					+ sellerId);
		} else {
			int missing = size - items.size();
			reporter.error(
					"get by seller_auction_index missing "
					+ missing
					+ (missing > 1 ? " items" : " item")
					+ " (out of " + size
					+ ") for seller=" + sellerId);
			Set<ItemId> ids = getIds(items);
			for (int i = 1; i <= size; i++) {
				ItemId id = new ItemId(sellerId, i);
				if (!ids.contains(id)) {
					AuctionItem item = getItem(db, id);
					if (item == null) {
						reporter.error(
						"missing auction item id="
						+ id);
					} else {
						reporter.error(
						"auction item (" + id
						+ ") exists, but missing in"
						+ " seller_auction_index");
					}
				}
			}
		}
	}
	private Set<ItemId> getIds(List<AuctionItem> items) {
		Set<ItemId> ids = new HashSet<ItemId>();
		for (AuctionItem item : items) {
			ids.add(item.getItemId());
		}
		return ids;
	}
	private boolean validate(T db, AuctionParam p, AuctionItem item,
			ValidateReporter reporter) {
		boolean success = true;
		AuctionItem expected = new AuctionItem(item.getSellerId(), item.getItemNo());
		p.build(expected);
		if (!expected.equals(item)) {
			reporter.error("item expected " + expected
					+ " but " + item);
			success = false;
		}
		BuyNowAuction bna = getBuyNowAuction(db, item.getItemId());
		BuyNowAuction expectedBna = p.bnaIfExists(expected);
		if (expectedBna != null) {
			if (!expectedBna.equals(bna)) {
				reporter.error("bna expected " + expectedBna
						+ " but " + bna);
				success = false;
			}
		} else if (bna != null) {
			reporter.error("bna id=" + item.getItemId()
					+ " should not exist but it does");
			success = false;
		}
		List<Bid> bids = getBidsByItemId(db, item.getItemId());
		if (p.numOfBids() != bids.size()) {
			reporter.error(
			"get by auction_bid_index size=" + bids.size()
			+ " expected=" + p.numOfBids()
			+ " for id=" + item.getItemId());
			success = false;
		}
		/**
		 * TODO check Bids
		 */
		return success;
	}

}