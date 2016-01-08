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
package com.nec.strudel.workload.server.rest.client;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;

import com.nec.strudel.exceptions.WorkloadException;
import com.nec.strudel.workload.job.WorkRequest;
import com.nec.strudel.workload.server.WorkStatus;
import com.nec.strudel.workload.server.WorkerService;
import com.nec.strudel.workload.server.rest.resources.WorkResource;

public class RestWorkerService implements WorkerService {
	private static final Logger LOGGER =
		    Logger.getLogger(RestWorkerService.class);
	public static final String STATE_RESOUCE = "status";
	public static final String STATE_PARAM = "stat";
	public static final String REPORT_RESOURCE = "report";
	public static final String START = WorkResource.START;
	public static final String STOP = WorkResource.STOP;
	private String url;
	private Client client;
	public RestWorkerService(String url) {
		this.url = url;
		client = ClientBuilder.newClient(new ClientConfig()
		.register(JsonProcessingFeature.class)
		.property(JsonGenerator.PRETTY_PRINTING, true));
	}

	@Override
	public WorkStatus init(WorkRequest work) {
		WebTarget target = client.target(url)
				.path(workerServicePath());
		try {
			return tryInit(work);
		} catch (WebApplicationException e) {
			LOGGER.error("failed to init work: "
					+ target.getUri(), e);
			throw e;
		} catch (ProcessingException e) {
			LOGGER.error("failed to init work: "
					+ target.getUri(), e);
			throw e;
		} catch (InterruptedException e) {
			String errorMsg = "init work interrupted: "
					+ target.getUri();
			LOGGER.error(errorMsg, e);
			throw new WorkloadException(errorMsg, e);
		}
	}
	private static final int MAX_TRIALS = 10;
	private static final long RETRY_WAIT = 200;
	private static final AtomicBoolean INIT = new AtomicBoolean(false);
	/**
	 * execute the first invocation exclusively
	 * (and other invocations non-exclusively)
	 * to work around a concurrency issue
	 * in org.glassfish.jersey.client.ClientRuntime.invoke()
	 * java.lang.IllegalAccessException:
	 * Class org.glassfish.hk2.utilities.reflection.ReflectionHelper
	 * can not access a member of class
	 * org.glassfish.json.jaxrs.JsonStructureBodyWriter
	 * with modifiers "private"
	 */
	private Response invoke(Invocation inv) {
		if (INIT.get()) {
			return inv.invoke();
		}
		synchronized (INIT) {
			if (!INIT.get()) {
				Response r = inv.invoke();
				INIT.set(true);
				return r;
			}
		}
		return inv.invoke();
	}
	protected WorkStatus tryInit(WorkRequest work)
			throws InterruptedException {
		int trial = 0;
		do {
			trial += 1;
			try {
				WebTarget target = client.target(url)
						.path(workerServicePath());
				/**
				 * org.glassfish.jersey
				 */
				Invocation inv = target.request()
				.buildPost(
				Entity.xml(work.toXMLString()));
				Response res = invoke(inv);
				JsonObject json =
				res.readEntity(JsonObject.class);
				WorkStatus stat = WorkStatus.create(json);
				if (stat.isError()) {
					String errorMsg =
					"failed to init work at "
					+ target.getUri() + " (retrying): "
					+ stat.getMessage();
					LOGGER.warn(errorMsg);
				} else {
					return stat;
				}
			} catch (ProcessingException e) {
				String errorMsg = "failed to init work at "
				+ url + " (retrying): "
				+ e.getMessage();
				if (trial < MAX_TRIALS) {
					LOGGER.warn(errorMsg);
				} else {
					LOGGER.warn(errorMsg, e);
				}
			}
			if (trial < MAX_TRIALS) {
				Thread.sleep(RETRY_WAIT);
			}
		} while (trial < MAX_TRIALS);
		String errorMsg = "retry failed to init work at "
				+ url;
		LOGGER.error(errorMsg);
		throw new WorkloadException(errorMsg);

	}

	@Override
	public WorkStatus start(String workId) {
		return setState(workId, START);
	}
	private WorkStatus setState(String workId, String state) {
		WebTarget target = client.target(url)
				.path(workPath(workId, STATE_RESOUCE));
		Form form = new Form();
		form.param(STATE_PARAM, state);
		Response res = target.request()
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.put(Entity.form(form));
		WorkStatus stat = toStatus(res);
		if (stat.isError()) {
			throw new WorkloadException(
				state + " failed for work ("
			+ workId + ") at " + url + " : "
			+ stat.getMessage());
		}
		return stat;
	}
	private WorkStatus toStatus(Response res) {
		MediaType type = res.getMediaType();
		if (!MediaType.APPLICATION_JSON_TYPE.equals(type)) {
			LOGGER.error("unexpected media type: " + type);
		}
		try {
			return WorkStatus.create(
				res.readEntity(JsonObject.class));
		} catch (Exception e) {
			LOGGER.error("failed to read the result: "
					+ "media type = " + type, e);
			return WorkStatus.error(e.getMessage());
		}
	}

	@Override
	public WorkStatus operate(String workId, String name,
			JsonObject arg) {
		WebTarget target = client.target(url)
				.path(workPath(workId, "c/" + name));
		Response res = target.request()
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(arg));
		WorkStatus stat = toStatus(res);
		if (stat.isError()) {
			throw new WorkloadException(
				name + " failed for work ("
			+ workId + ") at " + url + " : "
			+ stat.getMessage());
		}
		return stat;
	}

	@Override
	public WorkStatus stop(String workId) {
		return setState(workId, STOP);
	}

	@Override
	public WorkStatus getStatus(String workId) {
		WebTarget target = client.target(url)
				.path(workPath(workId));
		try {
			JsonObject res = target.request().get(JsonObject.class);
			return WorkStatus.create(res);
		} catch (WebApplicationException e) {
			LOGGER.error("failed to get status for workId=" + workId
				+ " as " + target.getUri(), e);
			throw e;
		}
	}
	@Override
	public JsonObject getReport(String workId) {
		WebTarget target = client.target(url)
				.path(workPath(workId, REPORT_RESOURCE));
		try {
			JsonObject res = target.request().get(JsonObject.class);
			return res;
		} catch (WebApplicationException e) {
			LOGGER.error("failed to get report for workId=" + workId
				+ " as " + target.getUri(), e);
			throw e;
		}
	}

	@Override
	public WorkStatus terminate(String workId) throws InterruptedException {
		WebTarget target = client.target(url)
				.path(workPath(workId));
		JsonObject json = null;
		try {
			json = target.request().delete(JsonObject.class);
		} catch (WebApplicationException e) {
			LOGGER.error("failed to terminate workId=" + workId
					+ " as " + target.getUri(), e);
			throw e;
		}
		WorkStatus stat = WorkStatus.create(json);
		if (stat.isError()) {
			throw new WorkloadException(
				"terminate failed for work ("
			+ workId + "): "
			+ stat.getMessage());
		}
		return stat;
	}

	@Override
	public Set<String> works() {
		WebTarget target = client.target(url)
				.path(workerServicePath());
		Response res = target.request()
//				.accept(MediaType.APPLICATION_JSON_TYPE)
				.get();
		JsonArray array = res.readEntity(JsonArray.class);
		Set<String> set = new HashSet<String>();
		for (JsonValue v : array) {
			set.add(v.toString());
		}
		return set;
	}

	private String workPath(String workId, String command) {
		return workPath(workId) + "/" + command;
	}
	private String workPath(String workId) {
		return "work/" + workId;
	}
	private String workerServicePath() {
		return "worker";
	}

}
