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
package com.nec.strudel.tkvs.store.hbase;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

public final class HBaseUtil {

	private HBaseUtil() {
	}

	public static Configuration config(Properties props) {
		/**
		 * NOTE: without this, HBaseConfiguration fails to
		 * find hbase-default.xml in its own resources.
		 */
		Thread.currentThread().setContextClassLoader(
				HBaseConfiguration.class.getClassLoader());
		Configuration conf =
				HBaseConfiguration.create();
		for (Map.Entry<Object, Object> e : props.entrySet()) {
           conf.set(e.getKey().toString(), e.getValue().toString());
		}
		return conf;
	}
}
