package com.nec.strudel.tkvs.store.hbase;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

public final class HBaseUtil {

	private HBaseUtil() {
	}

	public static Configuration config(Properties props) {
		/**
		 * NOTE: without this, HBaseConfiguration fails to
		 * find hbase-default.xml in its own resources.
		 */
		Thread.currentThread().setContextClassLoader(
				HBaseConfiguration.class.getClassLoader());
		Configuration conf =
				HBaseConfiguration.create();
		for (Map.Entry<Object, Object> e : props.entrySet()) {
           conf.set(e.getKey().toString(), e.getValue().toString());
		}
		return conf;
	}
}
