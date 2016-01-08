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
package com.nec.strudel.workload.session;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nec.strudel.session.Interaction;


public final class FactoryUtil {
	private FactoryUtil() {
		// not instantiated
	}
	public static String getName(Interaction<?> intr) {
		Name nam = intr.getClass().getAnnotation(Name.class);
		if (nam != null) {
			return nam.value();
		}
		return convert(getLocalName(intr.getClass()));
	}
	private static String convert(String str) {
		if (str == null) {
			return null;
		}
		return str.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
	}
	private static String getLocalName(Class<?> cls) {
		Pattern p = Pattern.compile(".*?(\\w+)\\z");
		Matcher m = p.matcher(cls.getName());
		if (m.matches()) {
			return m.group(1);
		} else {
			return null;
		}
	}
}
