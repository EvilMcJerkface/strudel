package com.nec.strudel.workload.server.rest.resources;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.nec.strudel.workload.server.WorkStatus;
import com.nec.strudel.workload.server.WorkerService;
import com.nec.strudel.workload.server.rest.WorkerServiceRepository;

@Path("/work/{workid}")
@Produces(MediaType.APPLICATION_JSON)
public class WorkResource {
	public static final String START = "start";
	public static final String STOP = "stop";
	private static final Logger LOGGER =
		    Logger.getLogger(WorkResource.class);
	public static final String WORKER_SERVICE_NAME = "workerservice";
	private WorkerService service;
	public WorkResource() {
		this.service = WorkerServiceRepository.getService(
				WORKER_SERVICE_NAME);
	}
	@GET
	public JsonObject getStat(@PathParam("workid") String workId) {
		WorkStatus stat = service.getStatus(workId);
		JsonObject json = stat.toJason();
		return json;
	}

	@GET
	@Path("/report")
	public JsonObject getReport(@PathParam("workid") String workId) {
		return service.getReport(workId);
	}

	@PUT
	@Path("/status")
	public JsonObject setStat(@PathParam("workid") String workId,
			@FormParam("stat") String stat) {
		if (START.equals(stat)) {
			return start(workId);
		} else if (STOP.equals(stat)) {
			return stop(workId);
		}
		return error(workId, "unknown status:" + stat);
	}

	@POST
	@Path("/c/{name}")
	public JsonObject postCommand(@PathParam("workid") String workId,
			@PathParam("name") String name, String value) {
		LOGGER.info("command "
				+ name  + "("
				+ value + "): " + workId);
		try {
			JsonReader reader = Json.createReader(
					new StringReader(value));
			JsonObject json = reader.readObject();
			reader.close();
			return service.operate(workId, name, json).toJason();
		} catch (JsonException e) {
			return error(workId, e.getMessage());
		}
	}

	private JsonObject start(String workId) {
		LOGGER.info("start: " + workId);
		return service.start(workId).toJason();
	}

	private JsonObject stop(String workId) {
		LOGGER.info("stop: " + workId);
		return service.stop(workId).toJason();
	}

	@DELETE
	public JsonObject delete(@PathParam("workid") String workId) {
		try {
			LOGGER.info("delete: " + workId);
			JsonObject res = service.terminate(workId).toJason();
			LOGGER.info("delete done: " + workId);
			return res;
		} catch (InterruptedException e) {
			LOGGER.info("delete interrupted", e);
			return error(workId, "interrupted");
		}
	}
	private JsonObject error(String workId, String msg) {
		return WorkStatus.error(workId, msg).toJason();
	}
}
