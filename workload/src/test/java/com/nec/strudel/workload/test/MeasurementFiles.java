package com.nec.strudel.workload.test;

import com.nec.strudel.workload.measure.MeasurementConfig;

public enum MeasurementFiles implements ResourceFile<MeasurementConfig> {
	MEASURE001,
	MEASURE002,
	MEASURE003;

	private final String file;
	private MeasurementFiles(String file) {
		this.file = file;
	}
	private MeasurementFiles() {
		this.file = this.name().toLowerCase();
	}
	@Override
	public String file() {
		return file;
	}
	@Override
	public Class<MeasurementConfig> resourceClass() {
		return MeasurementConfig.class;
	}
}
