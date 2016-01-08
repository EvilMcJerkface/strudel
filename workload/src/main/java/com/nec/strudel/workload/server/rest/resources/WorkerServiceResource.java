package com.nec.strudel.workload.server.rest.resources;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.nec.strudel.workload.job.WorkRequest;
import com.nec.strudel.workload.server.WorkStatus;
import com.nec.strudel.workload.server.WorkerService;
import com.nec.strudel.workload.server.rest.WorkerServiceRepository;

@Path("/worker")
@Produces(MediaType.APPLICATION_JSON)
public class WorkerServiceResource {
	private static final Logger LOGGER =
		    Logger.getLogger(WorkerServiceResource.class);
	private WorkerService service;
	public WorkerServiceResource() {
		this.service = WorkerServiceRepository.getService(
				WorkResource.WORKER_SERVICE_NAME);
	}
	@GET
	public JsonArray list() {
		JsonArrayBuilder arry = Json.createArrayBuilder();
		for (String id : service.works()) {
			arry.add(id);
		}
		return arry.build();
	}
	@POST
	public JsonObject create(String input) {
		try {
			WorkRequest work = WorkRequest.parse(input);
			LOGGER.info("creating work for node="
					+ work.getNodeId());
			WorkStatus stat = service.init(work);
			LOGGER.info("created: " + stat.getWorkId());
			return stat.toJason();
		} catch (Exception e) {
			LOGGER.error("failed to create", e);
			return error(e);
		}
	}
	private JsonObject error(Exception e) {
		String msg = e.getClass().getName()
				+ ": "
				+ e.getMessage();
		return WorkStatus.error(msg)
				.toJason();
	}
}
