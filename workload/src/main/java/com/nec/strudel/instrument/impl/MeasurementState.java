package com.nec.strudel.instrument.impl;

public interface MeasurementState {

	boolean isMeasuring();

	MeasurementState ALWAYS = new MeasurementState() {
		public boolean isMeasuring() {
			return true;
		}
	};

	MeasurementState NEVER = new MeasurementState() {
		public boolean isMeasuring() {
			return false;
		}
	};
}