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
    public boolean validate(T db, SaleParamSet param,
            ValidateReporter reporter) {
        /**
         * TODO implement
         */
        return true;
    }
}