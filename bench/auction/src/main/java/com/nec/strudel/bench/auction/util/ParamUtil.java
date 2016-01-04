package com.nec.strudel.bench.auction.util;

import java.util.GregorianCalendar;

public final class ParamUtil {
	private ParamUtil() {
		// not called
	}
    private static final long DAYS_TO_MS = 24 * 3600 * 1000;

    /**
     * Generates a future time.
     * @param days the number of days
     * @return the time the given days after the
     * current time
     */
	public static long dayAfter(int days) {
		long timeDiff = days * DAYS_TO_MS;
		return now() + timeDiff;
	}

	public static long dayBefore(int days) {
		long timeDiff = days * DAYS_TO_MS;
		return now() - timeDiff;
	}
	public static long now() {
		return new GregorianCalendar().getTimeInMillis();
	}
}
