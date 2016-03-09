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

import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.params.ResultMode;
import com.nec.strudel.bench.auction.params.TransParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

/**
 * Retrieves the purchase info (AuctionBuyNow) if the auction item (specified by
 * AUCTION_ITEM_ID) has been sold. The buyer is retrieved if it is sold.
 */
public abstract class AbstractViewAuctionBuyNow<T> implements Interaction<T> {

    public enum OutParam implements LocalParam {
        BNA, BUYER
    }

    @Override
    public void prepare(ParamBuilder builder) {
        builder
                .use(TransParam.AUCTION_ITEM_ID);
    }

    @Override
    public void complete(StateModifier modifier) {
        // do nothing
    }

    public Result resultOf(BuyNowAuction bna, User buyer, Param param,
            ResultBuilder res) {
        if (bna == null) {
            ItemId itemId = getItemId(param);
            res.warn("bna not found: id=" + itemId);
            return res.success(ResultMode.EMPTY_RESULT);
        }
        if (buyer == null) {
            ItemId itemId = getItemId(param);
            res.warn("buyer (" + bna.getBuyerId()
                    + ") not found for bna="
                    + itemId);
        }
        return res
                .set(OutParam.BNA, bna)
                .set(OutParam.BUYER, buyer)
                .success();
    }

    public ItemId getItemId(Param param) {
        return param.getObject(
                TransParam.AUCTION_ITEM_ID);
    }

}