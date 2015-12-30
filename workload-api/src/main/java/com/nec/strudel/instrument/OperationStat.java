package com.nec.strudel.instrument;



public interface OperationStat extends OperationListener {

	@Override
	void operation(long microSec);

	double getOperationsPerSec();

	double getAverageOperationTime();

}