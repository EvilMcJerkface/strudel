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

import com.nec.congenio.ConfigValue;
import com.nec.congenio.Values;
import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.target.impl.DatabaseConfig;
import com.nec.strudel.workload.cluster.Node;

public class WorkRequestParser {
    public static final String TAG_NAME = "work";
    public static final String NODE = "node";
    public static final String DATABASE = "database";

    /**
     * Converts WorkRequest to XML string
     * 
     * @param req
     * @return XML string
     */
    public static String toString(WorkRequest req) {
        WorkItem item = req.getWorkItem();
        return Values.builder(TAG_NAME)
                .add(NODE, req.getNode())
                .add(item.tagName(), item.getConfig())
                .add(DATABASE, req.getDatabase())
                .toXmlString();
    }

    /**
     * Converts XML string to WorkRequest
     * 
     * @param input
     * @return WorkRequest
     */
    public static WorkRequest parse(String input) {
        ConfigValue conf = Values.parseValue(input);
        Node node = conf.getObject(NODE, Node.class);
        WorkItem item = extractItem(conf);
        DatabaseConfig dbConfig = conf.getObject(DATABASE,
                DatabaseConfig.class);
        dbConfig.setContextClassPath(item.getClassPath());
        return new WorkRequest(node, item, dbConfig);
    }

    static WorkItem extractItem(ConfigValue conf) {
        for (String name : WorkItemSet.names()) {
            WorkItem item = conf.findObject(name, WorkItemSet.classOf(name));
            if (item != null) {
                return item;
            }
        }
        throw new ConfigException("work item not found in request");
    }

}