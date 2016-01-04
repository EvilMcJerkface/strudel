package com.nec.strudel.bench.auction.populate;

import java.util.List;
import java.util.Random;

import com.nec.strudel.bench.auction.entity.BuyNowSale;
import com.nec.strudel.bench.auction.entity.SaleItem;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.util.RandomSelector;
import com.nec.strudel.workload.api.PopulateParam;

public class SaleParamSet {
	public static enum InParam {
		USER_NUM,
		ITEMS_PER_USER,
		ITEM_NAME_LEN,
		BUYNOW_PER_ITEM,
		BUYNOW_RATIO,
		PRICE,
		QNTY,
		BNS_DATE_ADJUSTER,
	}

	private static final int RATIO = 100;
	private int sellerId;
	private SaleParam[] params;
	public static SaleParamSet create(PopulateParam param) {
		ParamGen gen = new ParamGen(param);
		int sellerId = param.getId();
		SaleParam[] params = gen.getParams();
		return new SaleParamSet(sellerId, params);
	}
	public SaleParamSet(int sellerId, SaleParam[] params) {
		this.params = params;
		this.sellerId = sellerId;
	}
	public int sellerId() {
		return sellerId;
	}
	public SaleParam[] getParams() {
		return params;
	}

	static class ParamGen {
		private int sellerId;
		private int numOfItems;
		private double price;
		private long bnsDate;
		private RandomSelector<String> name;
		private RandomSelector<Boolean> bnsExists;
		private RandomSelector<Integer> numOfBns;
		private RandomSelector<Integer> userId;
		private Random rand;
		private int qnty;
		ParamGen(PopulateParam param) {
			this.rand = param.getRandom();
			this.sellerId = param.getId();
			int userNum = param.getInt(InParam.USER_NUM);
			this.userId = RandomSelector.integerExcept(
						1, userNum + 1, sellerId);
			this.numOfItems = param.getInt(
					InParam.ITEMS_PER_USER);
			int buynows = param.getInt(InParam.BUYNOW_PER_ITEM);
			this.numOfBns = RandomSelector.create(1, buynows);
			// % of items that has existing buy_now_sale
			int buynowRatio = param.getInt(InParam.BUYNOW_RATIO);
			this.bnsExists = RandomSelector.createBoolean(
					buynowRatio / RATIO);
			this.price = param.getDouble(InParam.PRICE);
			this.qnty = param.getInt(InParam.QNTY);
			int bnsDateAdjuster = param.getInt(
					InParam.BNS_DATE_ADJUSTER);
			this.bnsDate = ParamUtil.dayBefore(bnsDateAdjuster);
			this.name = RandomSelector.createAlphaString(
					param.getInt(InParam.ITEM_NAME_LEN));
		}
		public SaleParam[] getParams() {
			SaleParam[] params = new SaleParam[numOfItems];
			for (int i = 0; i < params.length; i++) {
				params[i] = createParam();
			}
			return params;
		}
		private SaleParam createParam() {
			int num = numOfBns();
			SaleParam p = new SaleParam(num);
			p.setName(name.next(rand));
			p.setPrice(price);
			p.setItemQnty(qnty);
			p.setBuyerIds(buyerIds(num));
			p.setBnsDates(bnsDates(num));
			p.setBnsQnts(bnsQnts(num));
			return p;
		}
		private int numOfBns() {
			if (bnsExists.next(rand)) {
				return numOfBns.next(rand);
			} else {
				return 0;
			}
		}
		private int[] buyerIds(int num) {
			int[] ids = new int[num];
			for (int i = 0; i < ids.length; i++) {
				ids[i] = userId.next(rand);
			}
			return ids;
		}
		private long[] bnsDates(int num) {
			long[] dates = new long[num];
			for (int i = 0; i < dates.length; i++) {
				dates[i] = bnsDate;
			}
			return dates;
		}
		private int[] bnsQnts(int num) {
			int[] qnts = new int[num];
			for (int i = 0; i < qnts.length; i++) {
				qnts[i] = this.qnty;
			}
			return qnts;
		}

	}

	public static class SaleParam {
		private String name;
		private int numOfBns;
		private int itemQnty;
		private double price;
		private int[] buyerIds;
		private long[] bnsDates;
		private int[] bnsQnts;

		public SaleParam(int numOfBns) {
			this.numOfBns = numOfBns;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setItemQnty(int itemQnty) {
			this.itemQnty = itemQnty;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public void setBuyerIds(int[] buyerIds) {
			this.buyerIds = buyerIds;
		}
		public void setBnsDates(long[] bnsDates) {
			this.bnsDates = bnsDates;
		}
		public void setBnsQnts(int[] bnsQnts) {
			this.bnsQnts = bnsQnts;
		}
		public void build(SaleItem item) {
			item.setItemName(name);
			item.setPrice(price);
			item.setQnty(itemQnty);
		}

		public int numOfBns() {
			return numOfBns;
		}

		public void build(List<BuyNowSale> bnss) {
			for (int i = 0; i < buyerIds.length; i++) {
				BuyNowSale bns = bnss.get(i);
				bns.setBnsDate(bnsDates[i]);
				bns.setBuyerId(buyerIds[i]);
				bns.setQnty(bnsQnts[i]);
			}
		}

	}
}