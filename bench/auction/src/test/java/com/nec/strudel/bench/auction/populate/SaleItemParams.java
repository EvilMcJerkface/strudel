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

import com.nec.strudel.bench.auction.populate.SaleParamSet;
import com.nec.strudel.bench.auction.populate.SaleParamSet.InParam;
import com.nec.strudel.bench.auction.populate.base.AbstractPopulateSaleItem;
import com.nec.strudel.bench.test.populate.PopulateUtil;
import com.nec.strudel.workload.api.Populator;

public class SaleItemParams {

	private int userId;
	private int itemsPerUser = 1;
	private double price = 100.0;
	private int qnty = 10;
	private int buynowPerItem = 1;
	private final int userNum;
	private int buynowRatio = 100;
	private int bnsDateAdjuster = 1;
	private int nameLen = 15;
	public SaleItemParams(int userId, int userNum) {
		this.userId = userId;
		this.userNum = userNum;
	}
	public PopulateUtil.ParamBuilder getParam() {
    	PopulateUtil.ParamBuilder pb = PopulateUtil.param(userId)
    		.param(InParam.ITEMS_PER_USER, itemsPerUser)
    		.param(InParam.BUYNOW_RATIO, buynowRatio)
    		.param(InParam.ITEM_NAME_LEN, nameLen)
    		.param(InParam.PRICE, price)
    		.param(InParam.QNTY, qnty)
    		.param(InParam.BUYNOW_PER_ITEM, buynowPerItem)
    		.param(InParam.BNS_DATE_ADJUSTER, bnsDateAdjuster)
    		.param(InParam.USER_NUM, userNum);
    	return pb;
    }
	public SaleParamSet create(Populator<?, SaleParamSet> pop) {
		return pop.createParameter(getParam().build());
	}
	public SaleParamSet create() {
		return create(new AbstractPopulateSaleItem<Object>() {
			@Override
			public void process(Object db, SaleParamSet param) {
			}
		});
	}
	public SaleItemParams setBuynowRatio(int buynowRatio) {
		this.buynowRatio = buynowRatio;
		return this;
	}
	public SaleItemParams setItemsPerUser(int itemsPerUser) {
		this.itemsPerUser = itemsPerUser;
		return this;
	}
	public SaleItemParams setNumOfBns(int buyNowPerItem) {
		this.buynowPerItem = buyNowPerItem;
		return this;
	}
}