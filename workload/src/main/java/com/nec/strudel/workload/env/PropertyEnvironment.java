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

package com.nec.strudel.workload.env;

import java.util.Properties;

import com.nec.strudel.exceptions.ConfigException;

public abstract class PropertyEnvironment implements Environment {

    @Override
    public final void start(ExecConfig conf) {
        Properties prop = conf.toProperties();
        start(prop);
    }

    public abstract void start(Properties props);

    @Override
    public final void stop(ExecConfig conf) {
        Properties prop = conf.toProperties();
        stop(prop);
    }

    public abstract void stop(Properties props);

    public void startSuite(Properties props) {
        throw new ConfigException("startSuite not supported");
    }

    @Override
    public void startSuite(ExecConfig conf) {
        startSuite(conf.toProperties());
    }

    public void stopSuite(Properties props) {
        throw new ConfigException("stopSuite not supported");
    }

    @Override
    public void stopSuite(ExecConfig conf) {
        stopSuite(conf.toProperties());
    }

}
