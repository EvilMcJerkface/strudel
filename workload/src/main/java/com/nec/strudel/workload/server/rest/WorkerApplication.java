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
package com.nec.strudel.workload.server.rest;


import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.stream.JsonGenerator;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.nec.strudel.workload.server.rest.resources.WorkResource;
import com.nec.strudel.workload.server.rest.resources.WorkerServiceResource;

public class WorkerApplication extends ResourceConfig {
	public WorkerApplication() {
		registerClasses(
				WorkerServiceResource.class,
				WorkResource.class
		);
		register(JsonProcessingFeature.class);
		register(new LoggingFilter(logger(), false));
		property(JsonGenerator.PRETTY_PRINTING, true);
	}
	static Logger logger() {
		Logger logger = Logger.getAnonymousLogger();
		logger.setLevel(Level.WARNING);
		return logger;
	}
}