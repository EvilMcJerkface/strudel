package com.nec.strudel.workload.job;

import java.io.File;
import java.io.Writer;
import java.util.Iterator;

import javax.annotation.Nullable;

import com.nec.congenio.ConfigDescription;
import com.nec.congenio.ConfigValue;
import com.nec.strudel.exceptions.ConfigException;

/**
 * JobSuite is a set of Jobs, which are
 * executed sequentially. The jobs must be independent
 * of each other.
 * <pre>
 * &lt;JobSuite&gt;
 *   &lt;foreach&gt;...&lt;/foreach&gt;
 *   &lt;foreach&gt;...&lt;/foreach&gt;
 *   ...
 *   &lt;Output&gt;...&lt;/Output&gt;
 *   &lt;Job&gt;...&lt;/Job&gt;
 * &lt;/JobSuite>
 * </pre>
 * @author tatemura
 *
 */
public class JobSuite implements Iterable<Job> {
	public static final String TAG_NAME = "jobSuite";
	public static final String ELEM_OUTPUT = "output";
	private final JobInfo info;
	private final ConfigDescription cdl;

	public static JobSuite create(File file) {
		String path = file.getAbsolutePath();
		ConfigDescription cdl = ConfigDescription.create(file);
		JobInfo info = new JobInfo(path);
		JobSuite js = new JobSuite(info, cdl);
		return js;
	}

	public JobSuite(JobInfo info, ConfigDescription cdl) {
		this.info = info;
		this.cdl = cdl;
	}
	public JobInfo info() {
		return info;
	}
	@Override
	public Iterator<Job> iterator() {
		info.setOutDir(getOutput());
		return new JobIterator(info,
				cdl.evaluate().iterator());
	}

	/**
	 * Write (save) JobSuite (before
	 * loop unfolding and reference resolution)
	 */
	public void write(Writer writer) {
		cdl.write(writer);
	}

	@Nullable
	public String get(String name) {
		return cdl.get(name);
	}
	public String getOutput() {
		String output = get(ELEM_OUTPUT);
		if (output == null) {
			throw new ConfigException("not found: " + ELEM_OUTPUT);
		}
		return output;
	}

	static class JobIterator implements Iterator<Job> {
		private final Iterator<ConfigValue> itr;
		private final JobInfo info;
		private int id = 0;
		JobIterator(JobInfo info, Iterator<ConfigValue> itr) {
			this.info = info;
			this.itr = itr;
		}
		@Override
		public boolean hasNext() {
			return itr.hasNext();
		}

		@Override
		public Job next() {
			ConfigValue jobsuite = itr.next();
			return new Job(nextInfo(), jobsuite
					.getValue(Job.TAG_NAME));
		}
		private JobInfo nextInfo() {
			JobInfo info1 = info.copy(id);
			id++;
			return info1;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
