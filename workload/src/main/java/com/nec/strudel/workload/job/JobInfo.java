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
