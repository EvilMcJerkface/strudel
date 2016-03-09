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

import com.nec.strudel.target.impl.DatabaseConfig;
import com.nec.strudel.workload.cluster.Node;
import com.nec.strudel.workload.util.TimeValue;

/**
 * A unit of work given to a worker node
 * 
 * <pre>
 * "work" : {
 *   "node" : Node,
 *   "database" : Database,
 *   WorkItem.tagName : WorkItem
 * }
 * </pre>
 */
public class WorkRequest {
    private final Node node;
    private final WorkItem item;
    private final DatabaseConfig database;

    public static WorkRequest createLocal(
            WorkItem item, DatabaseConfig dbConfig) {
        return new WorkRequest(Node.empty(), item, dbConfig);
    }

    public WorkRequest(Node node,
            WorkItem item, DatabaseConfig dbConfig) {
        this.node = node;
        this.item = item;
        this.database = dbConfig;
    }

    public Node getNode() {
        return node;
    }

    public int getNodeId() {
        return node.getId();
    }

    public int getNodeNum() {
        return node.getNum();
    }

    public WorkItem getWorkItem() {
        return item;
    }

    public String getType() {
        return item.getType();
    }

    public String getClassPath() {
        return item.getClassPath();
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public WorkConfig getConfig() {
        return new WorkConfig(
                node.getId(),
                node.getNum(),
                item, database);

    }

    public TimeValue startSlackTime() {
        return item.startSlackTime();
    }

}
