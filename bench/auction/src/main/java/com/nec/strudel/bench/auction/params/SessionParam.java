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

package com.nec.strudel.bench.auction.params;

import com.nec.strudel.session.StateParam;

/**
 * Static parameters defined for each session.
 * 
 * @author tatemura
 *
 */
public enum SessionParam implements StateParam {
    /**
     * The ID of the user of the session.
     */
    USER_ID,
    /**
     * The total number of users.
     */
    TOTAL_USER,

    /**
     * The minimum user ID in the data set. The set of user IDs must be
     * {MIN_USER_ID, MIN_USER_ID + 1, ..., MIN_USER_ID + TOTAL_USER - 1}.
     */
    MIN_USER_ID,
    /**
     * The minimum value for the initial bid price of an auction item
     */
    AUCTION_INIT_BID_MIN,
    /**
     * The maximum value for the initial bid price of an auction item
     */
    AUCTION_INIT_BID_MAX,
    /**
     * The minimum buy-now ratio (more than 1 to be meaningful). The buy-now
     * ratio is used to decide the buy-now price of an auction item relative to
     * its initial bid price: BUY_NOW = INIT_BID * BUY_NOW_RATIO
     */
    AUCTION_BUY_NOW_RATIO_MIN,
    /**
     * The maximum buy-now ratio. The buy-now ratio is used to decide the
     * buy-now price of an auction item relative to its initial bid price:
     * BUY_NOW = INIT_BID * BUY_NOW_RATIO
     */
    AUCTION_BUY_NOW_RATIO_MAX,
    /**
     * The minimum duration (days) for an auction item to be open.
     */
    AUCTION_DURATION_DATE_MIN,
    /**
     * The maximum duration (days) for an auction item to be open.
     */
    AUCTION_DURATION_DATE_MAX,

    /**
     * A parameter used to decide the bidding price for an auction item: BID =
     * random([MAX_BID, MAX_BID + (BUY_NOW - MAX_BID) / BID_AMOUNT_ADJUSTER])
     */
    BID_AMOUNT_ADJUSTER,

    /**
     * The minimum value for the price of a sale item.
     */
    SALE_PRICE_MIN,
    /**
     * The maximum value for the price of a sale item.
     */
    SALE_PRICE_MAX,
    /**
     * The minimum available number (integer) of stocks for a sale item.
     */
    SALE_QNTY_MIN,
    /**
     * The maximum available number (integer) of stocks for a sale item.
     */
    SALE_QNTY_MAX,

    /**
     * A parameter used to decide the quantity to buy a sale item: QTY_TO_BUY =
     * random([1, QNTY/QNTY_ADJUSTER])
     */
    QNTY_ADJUSTER,
    /**
     * the length of an item name.
     */
    ITEM_NAME_LEN,
}
