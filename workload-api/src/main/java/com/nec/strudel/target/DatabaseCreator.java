package com.nec.strudel.target;


/**
 * An abstract class for life-cycle management of a database as
 * a workload target.
 * It supports two operations:
 * <ul>
 * <li> init: called before data population (for example,
 * required database schema is generated).
 * <li> prepare: called after data population and before
 * workloads (for example, some analysis and optimization
 * may be performed on the populated data).
 * </ul>
 * A subclass must implement two abstract methods that correspond
 * to these operations.
 */
public abstract class DatabaseCreator implements TargetLifecycle {
	public static final String INIT = "init";
	public static final String PREPARE = "prepare";
	/**
	 * An instance of DataBaseCreator that ignores operations.
	 */
	public static final DatabaseCreator NULL_CREATOR = new DatabaseCreator() {
	
		@Override
		public void close() {
		}
	
		@Override
		public void initialize() {
		}
	
		@Override
		public void prepare() {
		}
		
	};
	/**
	 * Initializes the database before population.
	 * For example, the implementation may generate
	 * schema.
	 */
	public abstract void initialize();

	/**
	 * Prepares the database after population.
	 * For example, the implementation may run analysis
	 * to acquire data statistics used for optimization.
	 */
	public abstract void prepare();

	@Override
	public void operate(String name) {
		if (INIT.equals(name)) {
			initialize();
		} else if (PREPARE.equals(name)) {
			prepare();
		}
		/**
		 * TODO else? exception?
		 */
		
	}

}
