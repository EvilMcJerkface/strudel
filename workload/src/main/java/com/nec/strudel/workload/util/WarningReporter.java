package com.nec.strudel.workload.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

public class WarningReporter {
	private final Logger logger;
    private final String[] warns;
    private int warnIdx = 0;
    private int warnCount = 0;

    public WarningReporter(int max, Logger logger) {
    	this.logger = logger;
    	this.warns = new String[max];
	}
	public List<String> report() {
		if (warnCount > 0) {
			int max = Math.min(warnCount, warns.length);
			String[] out = new String[max];
			for (int i = 0; i < max; i++) {
				out[i] = warns[i];
			}
			return Arrays.asList(out);
		} else {
			return Collections.emptyList();
		}
	}

	public void warn(String message) {
		logger.warn(message);
		warnCount++;
		warns[warnIdx] = message;
		warnIdx++;
		if (warnIdx >= warns.length) {
			warnIdx = 0;
		}
	}

}