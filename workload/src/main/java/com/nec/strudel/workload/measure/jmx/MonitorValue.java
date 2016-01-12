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
package com.nec.strudel.workload.measure.jmx;

public class MonitorValue {
	private String name;
	private String object;
	private String attr;
	private String aggr;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getAggr() {
		return aggr;
	}
	public void setAggr(String aggr) {
		this.aggr = aggr;
	}
}