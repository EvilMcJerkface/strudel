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

package com.nec.strudel.workload.server.rest;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.nec.strudel.workload.server.WorkerManager;
import com.nec.strudel.workload.server.rest.resources.WorkResource;

public class WorkerServer {
    private static final Logger LOGGER = Logger.getLogger(WorkerServer.class);
    public static final String PROP_PORT = "worker.port";
    private static final String DEFAULT_HOST = "localhost";
    private static final String LOCAL_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 8080;
    private static final long WAIT_TIME = 10000;
    private int port;
    private String host;
    private volatile boolean running;

    public WorkerServer(Properties props) {
        String portValue = props.getProperty(PROP_PORT);
        if (portValue != null) {
            port = Integer.parseInt(portValue);
        } else {
            port = DEFAULT_PORT;
        }
        host = findHost();
    }

    private String findHost() {
        try {
            String addr = InetAddress
                    .getLocalHost().getHostAddress();
            if (LOCAL_ADDRESS.equals(addr)) {
                LOGGER.warn(
                        "InetAddress.getLocalHost() returned "
                                + "localhost address " + LOCAL_ADDRESS);
            }
            return addr;
        } catch (UnknownHostException ex) {
            LOGGER.warn("InetAddress.getLocalHost() failed."
                    + " using 'localhost'...", ex);
            return DEFAULT_HOST;
        }
    }

    public URI getUri() {
        return URI.create("http://"
                + getHost() + ":" + getPort() + "/");
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public void run() {
        running = true;
        setServiceRepository();
        URI uri = getUri();
        LOGGER.info("starting up http server: " + uri);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                uri,
                resourceConfig());
        try {
            while (running) {
                synchronized (this) {
                    this.wait(WAIT_TIME);
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            running = false;
        } finally {
            server.shutdownNow();
        }
    }

    public void stop() {
        running = false;
        this.notifyAll();
    }

    ResourceConfig resourceConfig() {
        return new WorkerApplication();
    }

    void setServiceRepository() {
        WorkerServiceRepository.registerService(
                WorkResource.WORKER_SERVICE_NAME,
                new WorkerManager());
    }

    public static void main(String[] args) throws Exception {
        Properties props = System.getProperties();
        new WorkerServer(props).run();
    }
}
