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
