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