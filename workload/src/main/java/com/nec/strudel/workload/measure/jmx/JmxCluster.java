package com.nec.strudel.workload.measure.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.nec.strudel.Closeable;
import com.nec.strudel.exceptions.WorkloadException;

public class JmxCluster implements Closeable {
	private final List<JMXServiceURL> urls;
	private final List<JMXConnector> jmxcs = new ArrayList<JMXConnector>();

	public JmxCluster(List<JMXServiceURL> urls) {
		this.urls = urls;
	}
	public List<MBeanServerConnection> open() throws IOException {
		List<MBeanServerConnection> cons =
				new ArrayList<MBeanServerConnection>();
		for (JMXServiceURL url : urls) {
			JMXConnector jmxc = JMXConnectorFactory.connect(url);
			cons.add(jmxc.getMBeanServerConnection());
			jmxcs.add(jmxc);
		}
		return cons;
	}
	public int size() {
		return urls.size();
	}
	@Override
	public void close() {
		for (JMXConnector jmxc : jmxcs) {
			try {
				jmxc.close();
			} catch (IOException e) {
				throw new WorkloadException(
					"failed to close JMX connector", e);
			}
		}
	}
	@Override
	public int hashCode() {
		return urls.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof JmxCluster) {
			JmxCluster c = (JmxCluster) obj;
			return urls.equals(c.urls);
		}
		return false;
	}
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private List<JMXServiceURL> urls =
				new ArrayList<JMXServiceURL>();
		public JmxCluster build() {
			return new JmxCluster(urls);
		}
		public Builder url(String url) {
			try {
				JMXServiceURL u = new JMXServiceURL(url);
				urls.add(u);
			} catch (MalformedURLException e) {
				throw new RuntimeException("malformed URL:"
			+ url, e);
			}
			return this;
		}
		public Builder host(String host, int port) {
			String url = new StringBuilder()
			.append("service:jmx:rmi:///jndi/rmi://")
			.append(host).append(":").append(port)
			.append("/jmxrmi")
			.toString();
			return url(url);
		}
		public Builder host(String host) {
			String url = new StringBuilder()
			.append("service:jmx:rmi:///jndi/rmi://")
			.append(host)
			.append("/jmxrmi")
			.toString();
			return url(url);
		}
	}
}
