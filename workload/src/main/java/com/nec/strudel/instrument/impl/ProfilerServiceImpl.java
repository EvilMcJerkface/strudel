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
package com.nec.strudel.instrument.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.nec.strudel.Closeable;
import com.nec.strudel.instrument.BinaryEventStat;
import com.nec.strudel.instrument.InstrumentUtil;
import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.OperationStat;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.instrument.stat.BinaryEventMonitor;
import com.nec.strudel.instrument.stat.OperationMonitor;
import com.nec.strudel.management.ManagementService;
import com.nec.strudel.management.NoService;

public class ProfilerServiceImpl implements Closeable, ProfilerService {
	public static ProfilerServiceImpl create(MeasurementState measure,
			ManagementService mx) {
		return new ProfilerServiceImpl(measure, mx);
	}
	public static ProfilerService alwaysProfile(ManagementService mx) {
		return new ProfilerServiceImpl(MeasurementState.ALWAYS, mx);
	}
	public static ProfilerServiceImpl noService() {
		return new ProfilerServiceImpl(MeasurementState.NEVER, new NoService());
	}

	private final MeasurementState measure;
	private final ManagementService mx;
	private final List<Object> registered =
			new ArrayList<Object>();
	private final ConcurrentHashMap<String, Object> namedRegistered =
			new ConcurrentHashMap<String, Object>();

	public ProfilerServiceImpl(MeasurementState measure,
			ManagementService mx) {
		this.measure = measure;
		this.mx = mx;
	}
	public void register(Object managedObject) {
		mx.register(managedObject);
		registered.add(managedObject);
	}
	public void forceRegister(Object managedObject) {
		forceClean(managedObject);
		mx.register(managedObject);
		registered.add(managedObject);
	}
	@SuppressWarnings("unchecked")
	public synchronized <T> T getOrRegister(String name, T obj) {
		Object existing = namedRegistered.putIfAbsent(name, obj);
		if (existing == null) {
			forceClean(obj);
			mx.register(obj);
			return obj;
		} else {
			return (T) existing;
		}
	}
	protected synchronized void forceClean(Object obj) {
		String objectName = mx.registerName(obj);
		if (mx.isRegistered(objectName)) {
			mx.unregister(objectName);
		}
	}
	public synchronized <T> T getOrRegister(T obj) {
		return getOrRegister(mx.registerName(obj), obj);
	}
	@Override
	public void close() {
		for (Object obj : registered) {
			mx.unregister(obj);
		}
		for (Object obj : namedRegistered.values()) {
			mx.unregister(obj);
		}
	}

	public MeasurementState getMeasurementState() {
		return measure;
	}
	@Override
	public <T> Instrumented<T> createProfiler(
			Class<T> cls, Object stat) {
		stat = getOrRegister(stat);
		ProfilerDescriptor desc =
				ProfilerDescriptor.of(cls);
		T prof = desc.create(this, stat);
		return InstrumentUtil.profiled(prof, desc.extractInstrument(prof));
	}

	public <T> Instrumented<T> createProfiler(Class<T> cls) {
		ProfilerDescriptor desc =
				ProfilerDescriptor.of(cls);
		T prof = desc.create(this);
		return InstrumentUtil.profiled(prof, desc.extractInstrument(prof));
	}

	@Override
	public OperationStat createOperationStat(int windowSize, long windowStepMs) {
		return OperationMonitor.create(windowSize, windowStepMs);
	}
	@Override
	public BinaryEventStat createBinaryEventStat(int windowSize, long windowStepMs) {
		return new BinaryEventMonitor(windowSize, windowStepMs);
	}
}
