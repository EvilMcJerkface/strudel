package com.nec.strudel.instrument.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.nec.strudel.instrument.GetOperationListener;
import com.nec.strudel.instrument.OperationListener;
public final class ProfilerFactory {
	private ProfilerFactory() {
		// utility class not instantiated
	}

	public static TimeProfiler timeProfiler(
			String name, ProfilerServiceImpl ps, Object stat) {
		OperationListener mon = getListener(name, stat);
		TimeProfiler tp = new TimeProfiler(name,
				ps.getMeasurementState(),
				mon);
		return tp;
	}
	public static TimeProfiler timeProfiler(
			String name, ProfilerServiceImpl ps) {
		TimeProfiler tp = new TimeProfiler(name,
				ps.getMeasurementState());
		return tp;
	}
	static OperationListener getListener(String name, Object stat) {
		Class<?> cls = stat.getClass();
		try {
			for (Method m : cls.getMethods()) {
				GetOperationListener lsn = m.getAnnotation(GetOperationListener.class);
				if (lsn != null && name.equals(lsn.value())) {
					return (OperationListener) m.invoke(stat);
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException("failed to get listener for " + name);
	}
}
