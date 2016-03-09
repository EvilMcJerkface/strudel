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

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * {
 *   "port"? : int (-1),
 *   "urls"? : [string] ([]),
 *   "hosts"? : string (""),
 *   
 * }
 * </pre>
 *
 */
public class JmxClusterConfig {
    public static final String HOST_SEPARATOR = ",";
    private int port = -1;
    private String[] urls = new String[0];
    private String hosts = "";

    public JmxClusterConfig() {
    }

    public JmxCluster toCluster() {
        int port = getPort();
        JmxCluster.Builder builder = JmxCluster.builder();
        List<String> hosts = findHosts();
        if (!hosts.isEmpty()) {
            for (String host : hosts) {
                if (port >= 0 && !host.contains(":")) {
                    builder.host(host, port);
                } else {
                    builder.host(host);
                }
            }
        } else {
            for (String url : getUrls()) {
                builder.url(url);
            }
        }
        return builder.build();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    List<String> findHosts() {
        List<String> hosts = new ArrayList<String>();
        String hostlist = getHosts();
        for (String h : hostlist.split(HOST_SEPARATOR)) {
            h = h.trim();
            if (!h.isEmpty()) {
                hosts.add(h);
            }
        }
        return hosts;
    }

}
