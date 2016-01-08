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
