package com.nec.strudel.tkvs.store.hbase;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.log4j.Logger;

import com.google.protobuf.ServiceException;

public class HBaseControl {
	private static final Logger LOGGER = Logger.getLogger(HBaseControl.class);
	private final HTableDescriptor[] tabs;
	private final Configuration conf;
	private HBaseAdmin admin;
	public HBaseControl(Configuration conf, HTableDescriptor... tabs) {
		this.conf = conf;
		this.tabs = tabs;
	}

	/**
	 * @throws MasterNotRunningException
	 * @throws ZooKeeperConnectionException
	 * @throws IOException
	 */
	public void createTables() throws IOException {
		HBaseAdmin admin = admin();
		for (HTableDescriptor tab : tabs) {
			TableName tablename = tab.getTableName();
			if (!admin.tableExists(tablename)) {
				try {
					LOGGER.info("creating table:"
							+ tablename.getNameAsString());
					admin.createTable(tab);
					LOGGER.info("table created:"
							+ tablename.getNameAsString());
				} catch (TableExistsException e) {
					/**
					 * OK, someone has created the table
					 * concurrently.
					 */
					LOGGER.info("table already exists:"
							+ tablename.getNameAsString());
				}
			}
		}
	}
	/**
	 * @throws MasterNotRunningException
	 * @throws ZooKeeperConnectionException
	 */
	public boolean tryCreateTables(int maxTrial, long waitSec)
			throws InterruptedException {
		LOGGER.info("try to create tables (maxtrial=" + maxTrial
				+ ", wait-sec=" + waitSec);
		long sleepMsec = TimeUnit.SECONDS.toMillis(waitSec);
		for (int i = 0; i < maxTrial; i++) {
			try {
				createTables();
				return true;
			} catch (IOException e) {
				LOGGER.warn("HBase create table failed "
						+ (i < maxTrial ? " (retrying)" : ""), e);
				close(); // create table will reopen
				Thread.sleep(sleepMsec);
			}
		}
		return false;
	}
	/**
	 * 
	 * @throws MasterNotRunningException
	 * @throws ZooKeeperConnectionException
	 * @throws ServiceException
	 * @throws IOException
	 */
	public boolean triggerBalancer() throws ServiceException, IOException {
		return admin().balancer();
	}
	/**
	 * 
	 * @throws MasterNotRunningException
	 * @throws ZooKeeperConnectionException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void splitTables() throws IOException, InterruptedException {
		HBaseAdmin admin = admin();
		for (HTableDescriptor tab : tabs) {
			admin.split(tab.getName());
		}
	}
	public static final int MAX_TRIAL = 4;
	/**
	 * @return the count of splits that are successfully
	 * done
	 * @throws MasterNotRunningException
	 * @throws ZooKeeperConnectionException
	 * @throws InterruptedException
	 */
	public int splitTables(int count, long waitSec) throws InterruptedException {
		LOGGER.info("try splitting tables (count=" + count
				+ ", wait-sec=" + waitSec);
		long sleepMsec = TimeUnit.SECONDS.toMillis(waitSec);
		final int maxTrial = count * MAX_TRIAL;
		int trial = 0;
		int success = 0;
		while (success < count && trial <= maxTrial) {
			try {
				trial += 1;
				splitTables();
				success += 1;
			} catch (IOException e) {
				LOGGER.warn("HBase table split failed "
						+ (trial <= maxTrial ? " (retrying)" : ""), e);
				sleepMsec += TimeUnit.SECONDS.toMillis(waitSec);
			}
			Thread.sleep(sleepMsec);
		}
		if (success < count) {
			LOGGER.error("split table successful only "
					+ success + " times (requested =" + count + ")");
		}
		return success;
	}

	HBaseAdmin admin() throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
		if (admin == null) {
			LOGGER.info("creating HBaseAdmin");
			admin = new HBaseAdmin(conf);
			LOGGER.info("HBaseAdmin created");
		}
		return admin;
	}
	public void close() {
		if (admin != null) {
			try {
				admin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			admin = null;
		}
	}
}
