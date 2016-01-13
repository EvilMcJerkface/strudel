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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * WorkItem classes supported by the system
 * @author tatemura
 *
 */
public final class WorkItemSet {

	private WorkItemSet() {
	}
	private static final Map<String, Class<? extends WorkItem>> ITEM_CLASSES =
			new HashMap<String, Class<? extends WorkItem>>();
	static {
		ITEM_CLASSES.put(WorkloadTask.TAG_NAME, WorkloadTask.class);
		ITEM_CLASSES.put(PopulateWorkItem.TAG_NAME, PopulateWorkItem.class);
	}

	@Nullable
	public static Class<? extends WorkItem> classOf(String name) {
		return ITEM_CLASSES.get(name);
	}
	public static Set<String> names() {
		return Collections.unmodifiableSet(ITEM_CLASSES.keySet());
	}

}
