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
import java.util.Random;

import javax.annotation.concurrent.ThreadSafe;

import com.nec.strudel.session.ParamName;

/**
 * A sequence of the same parameter set.
 * 
 * @author tatemura
 *
 */
@ThreadSafe
public class ConstantParamSeq implements ParamSequence {
    private final Map<String, Object> param;

    public ConstantParamSeq(Map<String, Object> param) {
        this.param = Collections.unmodifiableMap(param);
    }

    @Override
    public Map<String, Object> nextParam(Random rand) {
        return param;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, Object> param = new HashMap<String, Object>();

        public Builder param(ParamName name, Object value) {
            return param(name.name(), value);
        }

        public Builder param(String name, Object value) {
            param.put(name, value);
            return this;
        }

        public ConstantParamSeq build() {
            return new ConstantParamSeq(param);
        }
    }
}
