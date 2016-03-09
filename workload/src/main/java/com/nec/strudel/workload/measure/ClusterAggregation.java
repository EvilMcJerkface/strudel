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

package com.nec.strudel.workload.measure;

import java.math.BigDecimal;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonValue;

import com.nec.strudel.json.JsonValues;

public abstract class ClusterAggregation {
    public enum Op {
        SUM(new Sum()), AVG(new Avg()), ARRAY(new Array());
        private final ClusterAggregation aggr;

        Op(ClusterAggregation aggr) {
            this.aggr = aggr;
        }

        protected ClusterAggregation getAggr() {
            return aggr;
        }
    }

    public static ClusterAggregation get(String operator) {
        return Op.valueOf(operator.toUpperCase()).getAggr();
    }

    public static ClusterAggregation get(Op operator) {
        return operator.getAggr();
    }

    public abstract JsonValue aggregate(Object... values);

    static class Sum extends ClusterAggregation {

        @Override
        public JsonValue aggregate(Object... values) {
            BigDecimal sum = BigDecimal.ZERO;
            for (Object o : values) {
                JsonNumber num = JsonValues.toNumber(o);
                sum = sum.add(num.bigDecimalValue());
            }
            return JsonValues.toValue(sum);
        }
    }

    static class Avg extends ClusterAggregation {

        @Override
        public JsonValue aggregate(Object... values) {
            if (values.length == 0) {
                return JsonValue.NULL;
            }
            BigDecimal sum = BigDecimal.ZERO;
            int count = 0;
            for (Object o : values) {
                JsonNumber num = JsonValues.toNumber(o);
                if (num != null) {
                    sum = sum.add(num.bigDecimalValue());
                    count += 1;
                }
            }
            if (count == 0) {
                return JsonValue.NULL;
            } else if (count == 1) {
                return JsonValues.toValue(sum);
            } else {
                double sumValue = sum.doubleValue();
                return JsonValues.toValue(sumValue / count);
            }
        }
    }

    static class Array extends ClusterAggregation {

        @Override
        public JsonValue aggregate(Object... values) {
            JsonArrayBuilder builder = Json.createArrayBuilder();
            for (Object o : values) {
                builder.add(JsonValues.toValue(o));
            }
            return builder.build();
        }
    }

}