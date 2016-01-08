package com.nec.strudel.workload.util;

public final class TimeUtil {
	private TimeUtil() {
		// not instantiated
	}
    public static final long MICS_PER_MS = 1000;
    public static final long NS_PER_MICS = 1000;
    public static final long NS_PER_MS = NS_PER_MICS * MICS_PER_MS;
    public static final long MS_PER_SECOND = 1000;
    public static final long MICS_PER_SECOND = MICS_PER_MS * MS_PER_SECOND;
    public static final long NS_PER_SECOND = NS_PER_MICS * MICS_PER_SECOND;
	public static final long SECONDS_PER_MIN = 60;
	public static final long MIN_PER_HOUR = 60;
    public static final long SECONDS_PER_HOUR = SECONDS_PER_MIN * MIN_PER_HOUR;
    public static final long HOURS_PER_DAY = 24;
    public static final long SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
    public static final long MS_PER_HOUR = MS_PER_SECOND * SECONDS_PER_HOUR;
    public static final long MS_PER_DAY = MS_PER_SECOND * SECONDS_PER_DAY;


	public static String formatTime(long ms) {
		long x = ms / MS_PER_SECOND;
		int seconds = (int) (x % SECONDS_PER_MIN);
		x /= SECONDS_PER_MIN;
		int minutes = (int) (x % MIN_PER_HOUR);
		x /= MIN_PER_HOUR;
		int hours = (int) (x % HOURS_PER_DAY);
		x /= HOURS_PER_DAY;

		int days = (int) x;
		StringBuilder sb = new StringBuilder();
		if (days > 1) {
			sb.append(days).append(" days ");
		} else if (days == 1) {
			sb.append("1 day ");
		}
		if (hours > 1) {
			sb.append(hours).append(" hours ");
		} else if (hours == 1) {
			sb.append("1 hour ");
		}
		if (minutes > 1) {
			sb.append(minutes).append(" minutes ");
		} else if (minutes == 1) {
			sb.append("1 minute ");
		}
		if (seconds > 1) {
			sb.append(seconds).append(" seconds");
		} else if (seconds == 1) {
			sb.append("1 second");
		}
		return sb.toString();
	}
	public static String formatTimeMS(long ms) {
		String s = formatTime(ms);
		if (s.isEmpty()) {
			return ms + " msec";
		} else {
			return s;
		}
	}
	public static String nanoToMS(long nano) {
		return nanoToMS(nano, "%.2f");
	}
	public static String nanoToMS(long nano, String format) {
		double ms  = ((double) nano) / TimeUtil.NS_PER_MS;
		return String.format(format, ms);
	}

}
