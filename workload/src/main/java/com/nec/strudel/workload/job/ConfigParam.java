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

package com.nec.strudel.workload.job;

import javax.annotation.Nullable;

import com.nec.congenio.ConfigValue;
import com.nec.congenio.Values;

public class ConfigParam {

    public static ConfigParam empty() {
        return new ConfigParam(Values.NONE);
    }

    public static ConfigParam create(ConfigValue conf) {
        return new ConfigParam(conf);
    }

    private final ConfigValue conf;

    public ConfigParam(ConfigValue conf) {
        this.conf = conf;
    }

    public <T> T getObject(String name, Class<T> cls) {
        return conf.getObject(name, cls);
    }

    @Nullable
    public String findString(String name) {
        return conf.find(name);
    }

    public String getString(String name) {
        return conf.get(name);
    }

    public int getInt(String name, int defaultValue) {
        return conf.getInt(name, defaultValue);
    }

    public int getInt(String name) {
        return conf.getInt(name);
    }

    public double getDouble(String name, double defaultValue) {
        return conf.getDouble(name, defaultValue);
    }
}
