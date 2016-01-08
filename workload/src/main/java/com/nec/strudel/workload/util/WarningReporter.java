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