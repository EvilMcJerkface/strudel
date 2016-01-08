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
package com.nec.strudel.workload.job;

/**
 * an encapsulation of additional information on job (suite)
 * which does not appear in the XML document itself (such as its
 * file name)
 * @author tatemura
 *
 */
public class JobInfo {
	private String outDir;
	private String path;
	private String savedPath;
	private int jobId;
	public JobInfo() {
	}
	public JobInfo(String path) {
		this.path = path;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getOutDir() {
		return outDir;
	}
	public void setOutDir(String outDir) {
		this.outDir = outDir;
	}
	public String getSavedPath() {
		return savedPath;
	}
	public void setSavedPath(String savedPath) {
		this.savedPath = savedPath;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	public int getJobId() {
		return jobId;
	}
	public JobInfo copy(int jobId) {
		JobInfo info = new JobInfo(path);
		info.setSavedPath(savedPath);
		info.setJobId(jobId);
		info.setOutDir(getOutDir());
		return info;
	}

}
