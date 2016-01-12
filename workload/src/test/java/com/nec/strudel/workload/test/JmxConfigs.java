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
package com.nec.strudel.workload.test;

import com.nec.strudel.workload.measure.jmx.JmxClusterConfig;
import com.nec.strudel.workload.measure.jmx.JmxMonitorConfig;

public final class JmxConfigs {

	private JmxConfigs() {
	}

	public static final ResourceFile<JmxMonitorConfig> MONITOR001 =
			Resources.of("jmx/jmxmonitor001", JmxMonitorConfig.class);
	public static final ResourceFile<JmxClusterConfig> CLUSTER000 =
			Resources.of("jmx/jmxcluster000", JmxClusterConfig.class);
	public static final ResourceFile<JmxClusterConfig> CLUSTER001 =
			Resources.of("jmx/jmxcluster001", JmxClusterConfig.class);
	public static final ResourceFile<JmxClusterConfig> CLUSTER002 =
			Resources.of("jmx/jmxcluster002", JmxClusterConfig.class);
}
