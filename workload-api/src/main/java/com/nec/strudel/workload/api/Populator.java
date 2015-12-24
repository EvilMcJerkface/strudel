package com.nec.strudel.workload.api;

import javax.annotation.concurrent.ThreadSafe;


/**
 * A process to populate a subset of the initial data. In a typical setting,
 * multiple populators of the same type are processed with multiple
 * threads/servers at the same time. Thus, it should be designed so that
 * they do not conflict with each other: e.g., inserting mutually disjoint
 * subsets of the data.
 * <p>
 * How data should be partitioned into such subsets depends on the type of
 * the database.
 * @author tatemura
 *
 * @param <T> type of the database
 * @param <P> type of a parameter for one population set
 */
@ThreadSafe
public interface Populator<T, P> {

	String getName();

	P createParameter(PopulateParam param);
	void process(T db, P param);

	boolean validate(T db, P param, ValidateReporter reporter);
}
