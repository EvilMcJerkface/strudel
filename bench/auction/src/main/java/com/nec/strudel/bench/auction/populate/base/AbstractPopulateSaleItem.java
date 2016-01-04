package com.nec.strudel.bench.auction.populate.base;

import com.nec.strudel.bench.auction.populate.SaleParamSet;
import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.ValidateReporter;

public abstract class AbstractPopulateSaleItem<T>
implements Populator<T, SaleParamSet> {

	@Override
	public String getName() {
		return "SaleItem";
	}

	@Override
	public SaleParamSet createParameter(PopulateParam param) {
		return SaleParamSet.create(param);
	}

	@Override
	public boolean validate(T db, SaleParamSet param, ValidateReporter reporter) {
		/**
		 * TODO implement
		 */
		return true;
	}
}