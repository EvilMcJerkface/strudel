package com.nec.strudel.workload.api;

import javax.annotation.Nullable;


public interface PopulatorFactory<T> {

	/**
	 * Crates a populator.
	 * @param name
	 * @return null if there is no such populator.
	 */
	@Nullable
	Populator<T, ?> create(String name);
}
