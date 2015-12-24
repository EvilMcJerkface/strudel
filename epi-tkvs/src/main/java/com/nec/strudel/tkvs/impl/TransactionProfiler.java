package com.nec.strudel.tkvs.impl;

import javax.json.Json;
import javax.json.JsonObject;

public interface TransactionProfiler {
	void getStart(String name);
	void getDone(String name);
	void getInBuffer(String name);
	void commitStart(String name);
	void commitSuccess(String name);
	void commitFail(String name);
	JsonObject getValues();

	TransactionProfiler NO_PROF =
			new TransactionProfiler() {
				@Override
				public void getStart(String name) {
				}
				@Override
				public void getInBuffer(String name) {
				}
				@Override
				public void getDone(String name) {
				}
				@Override
				public void commitStart(String name) {
				}
				@Override
				public void commitSuccess(String name) {
				}
				@Override
				public void commitFail(String name) {
				}
				@Override
				public JsonObject getValues() {
					return Json.createObjectBuilder()
							.build();
				}
	};
}
