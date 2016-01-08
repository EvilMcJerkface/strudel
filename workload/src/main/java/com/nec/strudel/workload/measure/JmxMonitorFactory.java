package com.nec.strudel.workload.measure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.nec.strudel.workload.measure.ResourceMonitor.ValueCollector;
import com.nec.strudel.workload.measure.ResourceMonitor.ValueFetcher;
import com.nec.strudel.workload.measure.jmx.JmxCluster;
import com.nec.strudel.workload.measure.jmx.JmxMonitorConfig;
import com.nec.strudel.workload.measure.jmx.MonitorValue;

public final class JmxMonitorFactory {
	private JmxMonitorFactory() {
		// not instantiated
	}
	public static ResourceMonitor create(JmxMonitorConfig spec) {
		try {
			ResultAggregation aggr = spec.createAggr();
			JmxCluster cluster = spec.getCluster().toCluster();
			List<ValueFetcher> fetchers =
				new ArrayList<ValueFetcher>(cluster.size());
			List<Resource> resource =
					Resource.create(spec.getValues());
			for (MBeanServerConnection c : cluster.open()) {
				fetchers.add(new JmxValueFetcher(c, resource));
			}
			Map<ResourceKey, ValueCollector> collectors =
					createCollectors(spec, cluster.size());
			return new ResourceMonitor(fetchers,
					collectors, aggr, cluster);
		} catch (IOException e) {
			throw new RuntimeException("failed to open", e);
		}
	}


	protected static Map<ResourceKey, ValueCollector> createCollectors(
			JmxMonitorConfig spec, int size) {
		Map<ResourceKey, ValueCollector> collectors =
				new HashMap<ResourceKey, ValueCollector>();
		for (MonitorValue v : spec.getValues()) {
			ObjectName objectName = objectName(v.getObject());
			ResourceKey key = new ResourceKey(
					objectName,
					v.getAttr());
			ClusterAggregation aggr =
					ClusterAggregation.get(v.getAggr());
			String name = v.getName();
			if (name == null) {
				name = v.getAttr();
			}
			collectors.put(key, new ValueCollector(name,
					aggr, size));
		}
		return collectors;
	}

	static class ResourceKey {
		private final ObjectName objectName;
		private final String attr;
		public ResourceKey(ObjectName name, String attr) {
			this.objectName = name;
			this.attr = attr;
		}
		@Override
		public int hashCode() {
			return objectName.hashCode() + attr.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj instanceof ResourceKey) {
				ResourceKey k = (ResourceKey) obj;
				return objectName.equals(k.objectName)
						&& attr.equals(k.attr);
			}
			return false;
		}
	}

	static class JmxValueFetcher implements ValueFetcher {
		private final MBeanServerConnection con;
		private final List<Resource> resources;
		public JmxValueFetcher(MBeanServerConnection con,
				List<Resource> resources) {
			this.con = con;
			this.resources = resources;
		}
		@Override
		public Map<Object, Object> call() throws Exception {
			Map<Object, Object> result =
					new HashMap<Object, Object>();
			for (Resource r : resources) {
				for (Attribute a : fetch(con, r)) {
					ResourceKey key = new ResourceKey(
						r.getName(), a.getName());
					result.put(key, a.getValue());
				}
			}
			return result;
		}

		List<Attribute> fetch(MBeanServerConnection con,
				Resource r)
				throws InstanceNotFoundException,
				ReflectionException, IOException {
			ObjectName objectName = r.getName();
			String[] attributes = r.getAttributes();
			return con.getAttributes(
							objectName, attributes)
					.asList();
		}

	}
	static class Resource {
		private final ObjectName objectName;
		private final String[] attributes;
		Resource(ObjectName name, String... attrs) {
			this.objectName = name;
			this.attributes = attrs;
		}
		public ObjectName getName() {
			return objectName;
		}
		public String[] getAttributes() {
			return attributes;
		}
		public static List<Resource> create(
				MonitorValue[] specs) {
			Map<ObjectName, List<String>> res =
					new HashMap<ObjectName, List<String>>();
			for (MonitorValue spec : specs) {
				ObjectName objectName = objectName(spec.getObject());
				List<String> attrs =
						res.get(objectName);
				if (attrs == null) {
					attrs = new ArrayList<String>();
					res.put(objectName, attrs);
				}
				attrs.add(spec.getAttr());
			}
			List<Resource> list =
					new ArrayList<Resource>(res.size());
			for (Map.Entry<ObjectName, List<String>> e
					: res.entrySet()) {
				String[] attrs = e.getValue()
						.toArray(new String[0]);
				list.add(new Resource(e.getKey(), attrs));
			}
			return list;
		}
	}
	static ObjectName objectName(String objName) {
		try {
			return ObjectName.getInstance(objName);
		} catch (MalformedObjectNameException e1) {
			throw new RuntimeException("malformed object name: "
					+ objName, e1);
		}

	}

}
