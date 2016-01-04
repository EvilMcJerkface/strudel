package com.nec.strudel.bench.auction.params;

import com.nec.strudel.session.StateParam;

/**
 * Dynamic parameters set during a session
 * @author tatemura
 *
 */
public enum TransParam implements StateParam {
	/**
	 * The ID of the current auction item the
	 * user is interested in.
	 */
	AUCTION_ITEM_ID,

	/**
	 * The ID of the current sale item the
	 * user is interested in.
	 */
	SALE_ITEM_ID,

	/**
	 * The available quantity of the current sale item
	 * the user is interested in. It is used to decide
	 * the quantity to buy.
	 */
	QNTY,

	/**
	 * The buy-now price of the current auction item
	 * the user is interested in. It is used to
	 * decide the bid price.
	 */
	BUYNOW,
	/**
	 * The max bid of the current auction item
	 * the user is interested in. It is used to
	 * decide the bid price.
	 */
	MAX_BID,

}
