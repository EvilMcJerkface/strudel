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
