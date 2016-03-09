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
import com.nec.strudel.workload.job.WorkRequestParser;
import com.nec.strudel.workload.server.WorkStatus;
import com.nec.strudel.workload.server.WorkerService;
import com.nec.strudel.workload.server.rest.WorkerServiceRepository;

@Path("/worker")
@Produces(MediaType.APPLICATION_JSON)
public class WorkerServiceResource {
    private static final Logger LOGGER = Logger
            .getLogger(WorkerServiceResource.class);
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
            WorkRequest work = WorkRequestParser.parse(input);
            LOGGER.info("creating work for node="
                    + work.getNodeId());
            WorkStatus stat = service.init(work);
            LOGGER.info("created: " + stat.getWorkId());
            return stat.toJason();
        } catch (Exception ex) {
            LOGGER.error("failed to create", ex);
            return error(ex);
        }
    }

    private JsonObject error(Exception ex) {
        String msg = ex.getClass().getName()
                + ": "
                + ex.getMessage();
        return WorkStatus.error(msg)
                .toJason();
    }
}
