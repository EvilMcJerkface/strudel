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

package com.nec.strudel.bench.auction.interactions.base;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

/**
 * Retrieves the information on the auction item of the current interest
 * (specified by AUCTION_ITEM_ID). It retrieves the auction item as well as its
 * seller. If it has been sold, it also retrieves the buyer. The successful
 * result may have the following modes:
 * <ul>
 * <li>EMPTY_RESULT: there is no such item.
 * <li>AUCTION_SOLD: the item has been sold.
 * <li>AUCTION_EXPIRED: the item has been expired.
 * </ul>
 * It will modify the transient states:
 * <ul>
 * <li>MAX_BID the current maximum bidding price.
 * <li>BUY_NOW the price to buy the item immediately.
 * </ul>
 * These values can be used in the next interaction to decide the price to bid
 * or buy.
 */
public abstract class AbstractViewAuctionItem<T> implements Interaction<T> {

    public enum OutParam implements LocalParam {
        SELLER, BUYER,
    }

    @Override
    public void prepare(ParamBuilder builder) {
        builder
                .use(TransParam.AUCTION_ITEM_ID);
    }

    @Override
    public void complete(StateModifier modifier) {
        modifier
                .export(TransParam.AUCTION_ITEM_ID)
                .export(TransParam.MAX_BID)
                .export(TransParam.BUYNOW);
    }

    public ItemId getItemId(Param param) {
        return param.getObject(
                TransParam.AUCTION_ITEM_ID);
    }

    public Result resultOf(AuctionItem item, User seller, BuyNowAuction bna,
            User buyer,
            Param param, ResultBuilder res) {
        if (item == null) {
            ItemId itemId = getItemId(param);
            return res.warn("auction item not found:"
                    + itemId)
                    .success(ResultMode.EMPTY_RESULT);
        }
        if (seller == null) {
            ItemId itemId = getItemId(param);
            res.warn("seller (" + itemId.getSellerId()
                    + ") not found for item="
                    + itemId);
        }

        if (bna != null && buyer == null) {
            res.warn("buyer (" + bna.getBuyerId()
                    + ") of item ("
                    + item.getItemId()
                    + ") not found");
        }
        res
                .set(TransParam.AUCTION_ITEM_ID, item.getItemId())
                .set(TransParam.MAX_BID, item.getMaxBid())
                .set(TransParam.BUYNOW, item.getBuyNow())
                .set(OutParam.SELLER, seller)
                .set(OutParam.BUYER, buyer);

        if (AuctionItem.isSold(item)) {
            return res.success(ResultMode.AUCTION_SOLD);
        } else if (item.getEndDate() < ParamUtil.now()) {
            return res.success(ResultMode.AUCTION_EXPIRED);
        } else {
            return res.success();
        }
    }

}