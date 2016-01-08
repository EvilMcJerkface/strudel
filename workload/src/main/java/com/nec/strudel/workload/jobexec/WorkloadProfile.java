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
package com.nec.strudel.workload.jobexec;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nullable;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.nec.strudel.workload.job.JobInfo;
import com.nec.strudel.workload.out.Output;

public class WorkloadProfile {
	private static final String NAME = "name";
	private static final String START_DATE = "start_date";
	private static final String END_DATE = "end_date";
	private static final String JOB_FILE = "job_file";
	private static final String JOB_SAVED = "job_saved";
	private static final String JOB_ID = "job_id";
	private String startDate;
	private String endDate;
	private String name;
	private String jobFile;
	private String jobSaved;
	private int jobId;
	private DateFormat dateFormat =
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
	public String getStartDate() {
		return startDate;
	}


	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public void setStartDateNow() {
		this.startDate = dateFormat.format(new Date());
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDateNow() {
		this.endDate = dateFormat.format(new Date());
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public void setJobInfo(JobInfo info) {
		setJobFile(info.getPath());
		setJobSaved(info.getSavedPath());
		setJobId(info.getJobId());
	}

	public String getJobFile() {
		return jobFile;
	}


	public void setJobFile(String jobFile) {
		this.jobFile = jobFile;
	}


	@Nullable
	public String getJobSaved() {
		return jobSaved;
	}


	public void setJobSaved(@Nullable String jobSaved) {
		this.jobSaved = jobSaved;
	}

	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add(NAME, getName())
		.add(START_DATE, getStartDate())
		.add(END_DATE, getEndDate())
		.add(JOB_FILE, getJobFile())
		.add(JOB_ID, getJobId());
		if (jobSaved != null) {
			builder.add(JOB_SAVED, jobSaved);
		}

		return builder.build();
	}
	public Output toOutput() {
		return Output.names(
				NAME, START_DATE, END_DATE,
				JOB_FILE, JOB_SAVED, JOB_ID);
	}

}
