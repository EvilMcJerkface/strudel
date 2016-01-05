package com.nec.strudel.tkvs.store.hbase;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;

import com.google.protobuf.ServiceException;
import com.nec.strudel.target.DatabaseCreator;
import com.nec.strudel.tkvs.TkvStoreException;

public class HBaseLifecycle extends DatabaseCreator {
	public static final String SPLIT_AFTER_POPULATION =
			"tkvs.hbase.postpopulation.split";
	public static final String BALANCE_AFTER_POPULATION =
			"tkvs.hbase.postpopulation.balance";
	public static final String SLEEP_AFTER_POPULATION =
			"tkvs.hbase.postpopulation.sleep.sec";
	public static final String SLEEP_AFTER_SPLIT =
			"tkvs.hbase.postpopulation.split.sleep.sec";
	public static final long DEFAULT_SLEEP_AFTER_SPLIT = 5;

	public static final String CREATE_TABLE_TRIAL =
			"tkvs.hbase.prepopulation.create.table.trial";
	public static final String CREATE_TABLE_RETRY_SLEEP_SEC =
			"tkvs.hbase.prepopulation.create.table.retry.sleep.sec";
	public static final int DEFAULT_TBLE_TRIAL = 5;
	public static final long DEFAULT_TABLE_RETRY_WAIT = 30;
	
	private final HTableDescriptor[] tabs;
	private final Configuration conf;

	public HBaseLifecycle(Configuration conf, HTableDescriptor... tabs) {
		this.tabs = tabs;
		this.conf = conf;
	}

	@Override
	public void close() {

	}

	@Override
	public void initialize() {
		HBaseControl ctrl = null;
		try {
			ctrl = new HBaseControl(conf, tabs);
			int trial = conf.getInt(CREATE_TABLE_TRIAL, DEFAULT_TBLE_TRIAL);
			long waitSec = conf.getLong(CREATE_TABLE_RETRY_SLEEP_SEC,
					DEFAULT_TABLE_RETRY_WAIT);
			boolean success = ctrl.tryCreateTables(trial, waitSec);
			if (!success) {
				throw new TkvStoreException(
					"create table not successful after retrial");
			}
			
//		} catch (MasterNotRunningException e) {
//			throw new TkvStoreException(
//		   "HBaseStore failed to create (Master not running)", e);
//		} catch (ZooKeeperConnectionException e) {
//			throw new TkvStoreException(
//			"HBaseStore failed to create (ZooKeeper connection)", e);
		} catch (InterruptedException e) {
			throw new TkvStoreException(
					"create table interrupted", e);
		} finally {
			if (ctrl != null) {
				ctrl.close();
			}
		}

	}


	@Override
	public void prepare() {
		try {
			optimize();
		} catch (MasterNotRunningException e) {
			throw new TkvStoreException(
			"HBaseStore failed (master not running)", e);
		} catch (ZooKeeperConnectionException e) {
			throw new TkvStoreException(
			"HBaseStore failed (ZooKeeper connection)", e);
		} catch (IOException e) {
			throw new TkvStoreException(
			"HBaseStore failed (IOException): "
			+ e.getMessage(), e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		} catch (ServiceException e) {
			throw new TkvStoreException(
			"HBaseStore failed to trigger balancer", e);
		}

	}
	void optimize() throws MasterNotRunningException,
	ZooKeeperConnectionException, IOException, InterruptedException, ServiceException {
		HBaseControl ctrl = new HBaseControl(conf, tabs);
		try {
			int split = conf.getInt(SPLIT_AFTER_POPULATION, 0);
			if (split > 0) {
				long sleep = conf.getLong(SLEEP_AFTER_SPLIT,
						DEFAULT_SLEEP_AFTER_SPLIT);
				ctrl.splitTables(split, sleep);
			}
			if (conf.getBoolean(BALANCE_AFTER_POPULATION, false)) {
				ctrl.triggerBalancer();
			}
		} finally {
			ctrl.close();
		}
		long sleep = conf.getLong(SLEEP_AFTER_POPULATION, 0);
		if (sleep > 0) {
			sleep(sleep);
		}
	}
	void sleep(long sec) {
		if (!Thread.currentThread().isInterrupted()) {
			long sleepMsec = TimeUnit.SECONDS.toMillis(sec);
			try {
				Thread.sleep(sleepMsec);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

		}
	}

}
