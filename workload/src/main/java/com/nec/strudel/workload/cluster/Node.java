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

public class Node {

    public static Node empty() {
        return new Node(0, 0, "");
    }

    public static Node create(int nodeId, int nodeNum, String url) {
        return new Node(nodeId, nodeNum, url);
    }

    private int id;
    private int num;
    private String url;

    public Node(int id, int num, String url) {
        this.id = id;
        this.num = num;
        this.url = url;
    }

    public Node() {
        this.url = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * The number of nodes in the cluster.
     * 
     * @return 0 if there is no node (i.e., local execution).
     */
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLocal() {
        return url.isEmpty();
    }
}