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

package com.nec.strudel.param;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.nec.congenio.ConfigValue;
import com.nec.congenio.ValueBuilder;
import com.nec.congenio.Values;
import com.nec.congenio.annotation.MapOf;
import com.nec.strudel.util.RandomSelector;

/**
 * Parameters used for a workload.
 * 
 * <pre>
 * "Params" : {
 *  NAME1 : CONSTANT_VALUE,
 *  NAME2 : { "random" : {
 *               "partition" : (true | node | thread | false)?
 *               "type" : (int | double)
 *               "min" : MIN_VALUE,
 *               "max" : MAX_VALUE,
 *             }
 *          }
 *  NAME3: { "perm" : {
 *               "partition" : (true | node | thread | false)?
 *               "type" : int?
 *               "min" : MIN_VALUE,
 *               "max" : MAX_VALUE,
 *             }
 *          },
 * }
 * </pre>
 * 
 * Random is random selection with replacement (every time one value is chosen
 * from the whole set), Perm is random selection without replacement (i.e.
 * random permutation). When the list is exhausted, the entire list is replaced
 * (i.e., permutation is done again).
 */
@MapOf(Param.class)
public class ParamConfig {
    public static final ParamSequence EMPTY_PARAM_SEQ = new ConstantParamSeq(
            new HashMap<String, Object>());

    public static ParamConfig empty() {
        Map<String, Param> params = Collections.emptyMap();
        return new ParamConfig(params);
    }

    private final Map<String, Param> params;

    public ParamConfig(Map<String, Param> params) {
        this.params = params;
    }

    public ParamConfig() {
        params = new HashMap<String, Param>();
    }

    public void put(String name, Param param) {
        this.params.put(name, param);
    }

    public void put(String name, String value) {
        put(name, new Param(value));
    }

    public void put(String name, RandomConfig value) {
        put(name, new Param(value));
    }

    public int size() {
        return params.size();
    }

    public ConfigValue getConfig() {
        ValueBuilder builder = Values.builder("params");
        for (Map.Entry<String, Param> e : params.entrySet()) {
            builder.add(e.getKey(), e.getValue().toConfig());
        }
        return builder.build();
    }

    public ParamSequence[] createParamSeqVector(int nid, int nodes,
            int threads) {
        nodes = Math.max(nodes, 1);
        Builder builder = new Builder(threads);
        for (Map.Entry<String, Param> en : params.entrySet()) {
            String name = en.getKey();
            Param param = en.getValue();
            RandomConfig conf = param.getRandomConf();
            if (conf == null) {
                builder.constant(name, param.getValue());
            } else if (conf.threadPartition()) {
                builder.add(name,
                        conf.createSelectors(nid, nodes, threads));
            } else {
                builder.add(name,
                        conf.createSelector(nid, nodes));
            }

        }
        return builder.vector();
    }

    public ParamSequence createParamSeq(int nid, int nodes) {
        nodes = Math.max(nodes, 1);
        Builder builder = new Builder();
        for (Map.Entry<String, Param> en : params.entrySet()) {
            String name = en.getKey();
            Param param = en.getValue();
            RandomConfig conf = param.getRandomConf();
            if (conf == null) {
                builder.constant(name, param.getValue());
            } else {
                builder.add(name,
                        conf.createSelector(nid, nodes));
            }
        }
        return builder.create();
    }

    public ParamSequence createParamSeq() {
        Builder builder = new Builder();
        for (Map.Entry<String, Param> en : params.entrySet()) {
            String name = en.getKey();
            Param param = en.getValue();
            RandomConfig conf = param.getRandomConf();
            if (conf == null) {
                builder.constant(name, param.getValue());
            } else {
                builder.add(name, conf.createSelector());
            }
        }
        return builder.create();
    }

    static class Builder {
        private int size;
        Map<String, Object> constants = new HashMap<String, Object>();
        Map<String, RandomSelector<?>> shared = new HashMap<String, RandomSelector<?>>();
        Map<String, RandomSelector<?>[]> individual = new HashMap<String, RandomSelector<?>[]>();

        public Builder() {
            this.size = 1;
        }

        public Builder(int size) {
            this.size = size;
        }

        public Builder constant(String name, Object value) {
            constants.put(name, value);
            return this;
        }

        public Builder add(String name, RandomSelector<?> rand) {
            shared.put(name, rand);
            return this;
        }

        public Builder add(String name, RandomSelector<?>[] rands) {
            if (size == 1 && rands.length == 1) {
                return add(name, rands[0]);
            } else {
                individual.put(name, rands);
            }
            return this;
        }

        public ParamSequence create() {
            if (shared.isEmpty()) {
                return new ConstantParamSeq(constants);
            } else {
                return new RandomParamSeq(constants, shared);
            }
        }

        public ParamSequence[] vector() {
            if (individual.isEmpty()) {
                return share(create(), size);
            }

            ParamSequence[] seqs = new ParamSequence[size];
            for (int i = 0; i < seqs.length; i++) {
                Map<String, RandomSelector<?>> rs = new HashMap<String, RandomSelector<?>>(
                        shared);
                for (Map.Entry<String, RandomSelector<?>[]> e : individual
                        .entrySet()) {
                    RandomSelector<?>[] rands = e.getValue();
                    rs.put(e.getKey(), rands[i]);
                }
                seqs[i] = new RandomParamSeq(constants, rs);
            }
            return seqs;

        }

        private ParamSequence[] share(ParamSequence seq, int size) {
            ParamSequence[] seqs = new ParamSequence[size];
            for (int i = 0; i < seqs.length; i++) {
                seqs[i] = seq;
            }
            return seqs;
        }
    }

}
