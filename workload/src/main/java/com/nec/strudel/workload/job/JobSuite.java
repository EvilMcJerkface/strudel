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

import java.io.File;
import java.util.Iterator;

import com.nec.congenio.ConfigDescription;
import com.nec.congenio.ConfigValue;
import com.nec.strudel.exceptions.ConfigException;

/**
 * JobSuite is a set of Jobs, which are executed sequentially. The jobs must be
 * independent of each other (i.e., one job should not use the result of another
 * job).
 * 
 * <pre>
 * &lt;jobSuite&gt;
 *   &lt;output&gt;...&lt;/output&gt;
 *   &lt;id&gt;...&lt;/id&gt;
 *   &lt;foreach&gt;...&lt;/foreach&gt;
 *   &lt;foreach&gt;...&lt;/foreach&gt;
 *   ...
 *   &lt;job&gt;...&lt;/job&gt;
 * &lt;/jobSuite>
 * </pre>
 * 
 * @author tatemura
 *
 */
public class JobSuite implements Iterable<Job> {
    public static final String SUPER_SUITE = "superJobSuite.xml";
    public static final String TAG_NAME = "jobSuite";
    public static final String ELEM_OUTPUT = "output";
    public static final String ID = "id";
    private final JobInfo info;
    private final ConfigDescription cdl;
    private String id;
    private String output;

    private JobSuite(JobInfo info, ConfigDescription cdl) {
        this.info = info;
        this.cdl = cdl;
        this.id = info.getId();
        this.output = info.getOutDir();
    }


    public static JobSuite create(File file) {
        ConfigDescription base = baseDescription();
        String path = file.getAbsolutePath();
        ConfigDescription cdl = ConfigDescription.create(file, base);
        JobInfo info = new JobInfo(path);
        setupInfo(info, cdl);
        return new JobSuite(info, cdl);
    }

    /**
     * Gets a super JobSuite document that provides default values (id, output)
     */
    public static ConfigDescription baseDescription() {
        return ConfigDescription.create(JobSuite.class, SUPER_SUITE);
    }

    public static String findId(ConfigDescription cdl) {
        return cdl.get(ID);
    }

    public static String findOutput(ConfigDescription cdl) {
        return cdl.get(ELEM_OUTPUT);
    }

    public static void setupInfo(JobInfo info, ConfigDescription cdl) {
        String id = findId(cdl);
        if (id == null) {
            throw new ConfigException("mssing value: id");
        }
        String output = findOutput(cdl);
        if (output == null) {
            throw new ConfigException("mssing value: output");
        }
        info.setId(id);
        info.setOutDir(output);
    }

    @Override
    public Iterator<Job> iterator() {
        return new JobIterator(info,
                cdl.evaluate().iterator());
    }

    public String getOutput() {
        return output;
    }

    public String getId() {
        return id;
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
