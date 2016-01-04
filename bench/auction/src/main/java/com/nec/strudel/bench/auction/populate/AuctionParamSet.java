package com.nec.strudel.bench.auction.populate;

import java.util.List;
import java.util.Random;

import com.nec.strudel.bench.auction.entity.AuctionItem;
import com.nec.strudel.bench.auction.entity.Bid;
import com.nec.strudel.bench.auction.entity.BuyNowAuction;
import com.nec.strudel.bench.auction.entity.ItemId;
import com.nec.strudel.bench.auction.util.ParamUtil;
import com.nec.strudel.util.RandomSelector;
import com.nec.strudel.workload.api.PopulateParam;

public class AuctionParamSet {
	public enum InParam {
		ITEM_NAME_LEN,
		INITIAL_BID,
		BUYNOW_ADJUSTER,
		BID_DATE_ADJUSTER,
		BNA_DATE_ADJUSTER,
		BIDS_PER_ITEM,
		BUYNOW_RATIO,
		USER_NUM,
		END_DATE_ADJUSTER,
		ITEMS_PER_USER,
	}

	private static final double CENT = 100;
	private final int sellerId;
	private final AuctionParam[] gparams;

	public static AuctionParamSet create(PopulateParam param) {
		int sellerId = param.getId();
		double initialBid =  param.getDouble(InParam.INITIAL_BID);
		double buyNowAdjuster =
				param.getDouble(InParam.BUYNOW_ADJUSTER);
		double buyNow = initialBid * buyNowAdjuster;

		int bidDateAdjuster = param.getInt(InParam.BID_DATE_ADJUSTER);
		int bnaDateAdjuster = param.getInt(InParam.BNA_DATE_ADJUSTER);
		int bids = param.getInt(InParam.BIDS_PER_ITEM);
		// % of items that has existing buy_now_auction
		int buynowRatio = param.getInt(InParam.BUYNOW_RATIO);
		int userNum = param.getInt(InParam.USER_NUM);

		int nameLen = param.getInt(InParam.ITEM_NAME_LEN);
		GroupParamGen gen = new GroupParamGen(param.getRandom(),
				RandomSelector.createAlphaString(nameLen),
				RandomSelector.create(0, bids + 1),
				RandomSelector.createBoolean(
						buynowRatio / CENT),
//				RandomSelector.create(1, userNum + 1),
				RandomSelector.integerExcept(1, userNum + 1,
						sellerId),
				RandomSelector.createDouble(
							initialBid, buyNow));
		gen.setBidDate(ParamUtil.dayBefore(bidDateAdjuster));
		gen.setBnaDate(ParamUtil.dayBefore(bnaDateAdjuster));
		int endDateAdjuster = param.getInt(InParam.END_DATE_ADJUSTER);
		gen.setEndDate(ParamUtil.dayAfter(endDateAdjuster));
		gen.setInitialBid(initialBid);
		gen.setBuyNow(buyNow);

		int numOfItems = param.getInt(InParam.ITEMS_PER_USER);
		AuctionParamSet ap = new AuctionParamSet(
				sellerId, gen.createParams(numOfItems));

		return ap;
	}

	public AuctionParamSet(int sellerId,
			AuctionParam... params) {
		this.sellerId = sellerId;
		this.gparams = params;
	}

	public int sellerId() {
		return sellerId;
	}

	public AuctionParam[] getParams() {
		return gparams;
	}


	static class GroupParamGen {
		private final Random rand;
		private final RandomSelector<String> itemName;
		private final RandomSelector<Integer> numOfBids;
		private final RandomSelector<Boolean> bnaExists;
		private final RandomSelector<Integer> otherUser;
		private final RandomSelector<Double> amount;
		private long bidDate;
		private long bnaDate;
		private long endDate;
		private double initialBid;
		private double buyNow;
		public GroupParamGen(Random rand,
				RandomSelector<String> itemName,
				RandomSelector<Integer> numOfBids,
				RandomSelector<Boolean> bnaExists,
				RandomSelector<Integer> otherUser,
				RandomSelector<Double> amount) {
			this.rand = rand;
			this.itemName = itemName;
			this.numOfBids = numOfBids;
			this.bnaExists = bnaExists;
			this.otherUser = otherUser;
			this.amount = amount;
		}
		public void setBidDate(long bidDate) {
			this.bidDate = bidDate;
		}
		public void setBnaDate(long bnaDate) {
			this.bnaDate = bnaDate;
		}
		public void setEndDate(long endDate) {
			this.endDate = endDate;
		}
		public void setBuyNow(double buyNow) {
			this.buyNow = buyNow;
		}
		public void setInitialBid(double initialBid) {
			this.initialBid = initialBid;
		}
		private double[] amounts(int num) {
			double[] amounts = new double[num];
			for (int i = 0; i < num; i++) {
				amounts[i] = amount.next(rand);
			}
			return amounts;
		}
		private int[] bidders(int num) {
			int[] ids = new int[num];
			for (int i = 0; i < num; i++) {
				ids[i] = otherUser.next(rand);
			}
			return ids;
		}
		public AuctionParam[] createParams(int size) {
			AuctionParam[] gparams = new AuctionParam[size];
			for (int i = 0; i < gparams.length; i++) {
				gparams[i] = createParam();
			}
			return gparams;
		}
		private AuctionParam createParam() {
			int num = numOfBids.next(rand);
			AuctionParam g = new AuctionParam(num);
			g.setName(itemName.next(rand));
			g.setBidDate(bidDate);
			g.setBnaDate(bnaDate);
			g.setAmounts(amounts(num));
			g.setBnaExists(bnaExists.next(rand));
			g.setBidders(bidders(num));
			g.setBuyer(otherUser.next(rand));
			g.setEndDate(endDate);
			g.setInitialBid(initialBid);
			g.setBuyNow(buyNow);
			return g;
		}
	}
	public static class AuctionParam  {
		private String name;
		private int numOfBids;
		private long bidDate;
		private long bnaDate;
		private double[] amounts;
		private int[] bidders;
		private int buyer;
		private boolean bnaExists;
		private double initialBid;
		private double buyNow;
		private long endDate;

		AuctionParam(int numOfBids) {
			this.numOfBids = numOfBids;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setBidDate(long bidDate) {
			this.bidDate = bidDate;
		}
		public void setBnaDate(long bnaDate) {
			this.bnaDate = bnaDate;
		}
		public void setAmounts(double[] amounts) {
			this.amounts = amounts;
		}
		public void setBidders(int[] bidders) {
			this.bidders = bidders;
		}
		public void setBnaExists(boolean bnaExists) {
			this.bnaExists = bnaExists;
		}
		public void setBuyer(int buyer) {
			this.buyer = buyer;
		}
		public void setEndDate(long endDate) {
			this.endDate = endDate;
		}
		public void setBuyNow(double buyNow) {
			this.buyNow = buyNow;
		}
		public void setInitialBid(double initialBid) {
			this.initialBid = initialBid;
		}
		public void build(AuctionItem item) {
			item.setItemName(getItemName());
			item.setBuyNow(getBuyNow());
			item.setInitialBid(getInitialBid());
			item.setMaxBid(getMaxBid());
			item.setEndDate(getEndDate());
		}
		public String getItemName() {
			return name;
		}
		public long getEndDate() {
			return endDate;
		}
		public int numOfBids() {
			return numOfBids;
		}
		public double getBuyNow() {
			return buyNow;
		}
		public double getInitialBid() {
			return initialBid;
		}
		public double getMaxBid() {
			if (bnaExists) {
				return AuctionItem.MAX_BID_SOLD;
			}
			double max = 0;
			for (double a : amounts) {
				if (max < a) {
					max = a;
				}
			}
			return max;
		}

		public BuyNowAuction bnaIfExists(AuctionItem item) {
			if (bnaExists) {
				ItemId itemId = item.getItemId();
				BuyNowAuction bna = new BuyNowAuction(itemId);
				bna.setBnaDate(bnaDate);
				bna.setBuyerId(buyer);
				return bna;
			} else {
				return null;
			}
		}

		public void build(List<Bid> bids) {
			if (bids.size() != numOfBids) {
				throw new RuntimeException(
					"inconsistent number of bids");
			}
			for (int i = 0; i < numOfBids; i++) {
				Bid bid = bids.get(i);
				bid.setBidAmount(amounts[i]);
				bid.setBidDate(bidDate);
				bid.setUserId(bidders[i]);
			}
		}
	}
}