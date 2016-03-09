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

package com.nec.strudel.workload.measure;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.nec.strudel.Closeable;
import com.nec.strudel.workload.com.Caller;

public class ResourceMonitor implements Closeable {

    private final Closeable[] closeables;
    private final List<ValueFetcher> fetchers;
    private final Map<?, ValueCollector> collectors;
    private final ResultAggregation aggr;

    public ResourceMonitor(List<ValueFetcher> fetchers,
            Map<?, ValueCollector> collectors,
            ResultAggregation aggr, Closeable... closeables) {
        this.fetchers = fetchers;
        this.collectors = collectors;
        this.aggr = aggr;
        this.closeables = closeables;
    }

    public interface ValueFetcher extends Callable<Map<Object, Object>> {
    }

    public void process(Caller caller) throws InterruptedException {
        aggr.put(fetch(caller));
    }

    public JsonValue getResult() {
        return aggr.get();
    }

    @Override
    public void close() {
        for (Closeable c : closeables) {
            c.close();
        }
    }

    public int size() {
        return fetchers.size();
    }

    protected JsonObject fetch(Caller caller) throws InterruptedException {
        for (ValueCollector c : collectors.values()) {
            c.clear();
        }
        List<Future<Map<Object, Object>>> res = caller.call(fetchers);
        for (int i = 0; i < res.size(); i++) {
            try {
                Map<Object, Object> map = res.get(i).get();
                for (Map.Entry<Object, Object> e : map.entrySet()) {
                    collectors.get(e.getKey())
                            .set(i, e.getValue());
                }
            } catch (ExecutionException ex) {
                /**
                 * TODO appropriate exception:
                 */
                throw new RuntimeException(ex);
            }
        }
        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (ValueCollector c : collectors.values()) {
            builder.add(c.getName(), c.getValue());
        }
        return builder.build();
    }

    public static class ValueCollector {
        private final Object[] values;
        private final String name;
        private final ClusterAggregation aggr;

        public ValueCollector(String name,
                ClusterAggregation aggr, int size) {
            this.name = name;
            this.aggr = aggr;
            this.values = new Object[size];
        }

        public void clear() {
            for (int i = 0; i < values.length; i++) {
                values[i] = null;
            }
        }

        public void set(int idx, Object value) {
            this.values[idx] = value;
        }

        public String getName() {
            return name;
        }

        public JsonValue getValue() {
            return aggr.aggregate(values);
        }
    }

}