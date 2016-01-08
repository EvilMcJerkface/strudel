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
package com.nec.strudel.workload.server;

import javax.annotation.Nullable;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.nec.strudel.workload.worker.Worker;

public class WorkStatus {
	public static final String VALUE_ID = "id";
	public static final String VALUE_STATE = "state";
	public static final String VALUE_MESSAGE = "message";
	private final String workId;
	private final String state;
	private final String message;

	public static WorkStatus stat(String workId, String state) {
		return new WorkStatus(workId, state, null);
	}
	public static WorkStatus unknown(String workId) {
		return new WorkStatus(workId, Worker.STATE_ERROR,
				"unknown work ID");
	}
	public static WorkStatus error(String workId, String msg) {
		return new WorkStatus(workId, Worker.STATE_ERROR, msg);
	}
	public static WorkStatus error(String msg) {
		return new WorkStatus(null, Worker.STATE_ERROR, msg);
	}

	WorkStatus(@Nullable String workId, String state,
			@Nullable String message) {
		this.workId = workId;
		this.state = state;
		this.message = message;
	}

	/**
	 * The ID of the work.
	 * @return null when the work (request)
	 * failed to acquire an ID (i.e., an error
	 * at initialization).
	 */
	@Nullable
	public String getWorkId() {
		return workId;
	}

	@Nullable
	public String getMessage() {
		return message;
	}
	public String getState() {
		return state;
	}
	public boolean isError() {
		return Worker.STATE_ERROR.equals(state);
	}
	public String toString() {
		return toJason().toString();
	}
	public JsonObject toJason() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (workId != null) {
			builder.add(VALUE_ID, getWorkId());
		}
		builder.add(VALUE_STATE, state);
		if (message != null) {
			builder.add(VALUE_MESSAGE, message);
		}
		return builder.build();
	}
	public static WorkStatus create(JsonObject json) {
		String workId = json.getString(VALUE_ID, null);
		String state = getJsonString(json, VALUE_STATE);
		String message = json.getString(VALUE_MESSAGE, null);
		return new WorkStatus(workId, state, message);
	}
	private static String getJsonString(JsonObject json, String name) {
		try {
			return json.getString(name);
		} catch (NullPointerException e) {
			throw new NullPointerException(name
					+ " not found in json: " + json);
		}
	}

}
