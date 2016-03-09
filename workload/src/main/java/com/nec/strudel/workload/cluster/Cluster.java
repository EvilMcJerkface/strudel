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

package com.nec.strudel.workload.cluster;

/**
 * <pre>
 * {
 *   "size": N,
 *   "urls" : ["url"]
 * }
 * </pre>
 * 
 * @author tatemura
 *
 */
public class Cluster {

    private String[] urls;
    private int size;

    public Cluster() {
        urls = new String[0];
    }

    public Cluster(int size, String[] urls) {
        this.size = size;
        this.urls = urls;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public int size() {
        return Math.min(urls.length, size);
    }

    public boolean isLocal() {
        for (String u : urls) {
            if (!u.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Node[] nodes() {
        int size = size();
        Node[] nodes = new Node[size];
        for (int nodeId = 0; nodeId < size; nodeId++) {
            nodes[nodeId] = Node.create(nodeId, size, urls[nodeId]);
        }
        return nodes;
    }

    public Cluster limit(int nodeNum) {
        return new Cluster(Math.min(nodeNum, this.size), urls);
    }

}
