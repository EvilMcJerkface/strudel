package com.nec.strudel.bench.auction.params;

public final class ResultMode {
	private ResultMode() {
		// not instantiated
	}
	public static final String EMPTY_RESULT = "EMPTY_RESULT";
	public static final String AUCTION_EXPIRED = "AUCTION_EXPIRED";
	public static final String AUCTION_SOLD = "AUCTION_SOLD";
	public static final String LOSING_BID = "LOSING_BID";
	public static final String SALE_NO_QTY = "SALE_NO_QTY";
	public static final String MISSING_PARAM = "MISSING_PARAM";
	public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
}
