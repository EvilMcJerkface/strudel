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
package com.nec.strudel.workload.exec.event;

import javax.json.JsonObject;

import com.nec.strudel.metrics.Report;
import com.nec.strudel.workload.util.TimeValue;

public interface EventController {
	/**
	 * Gets the measured values
	 * @return Report that contains metrics values.
	 */
	Report getReport();
	/**
	 * Sets the slack time of the beginning of events.
	 * @param slack
	 */
	void setStartSlack(TimeValue slack);

	/**
	 * Handles an operation if it is known
	 * @param name
	 * @param data
	 * @return false if the operation is unknown. true
	 * if the operation is known (and handled).
	 */
	boolean operate(String name, JsonObject data);
}