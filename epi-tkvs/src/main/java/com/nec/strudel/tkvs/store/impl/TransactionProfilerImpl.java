package com.nec.strudel.tkvs.store.impl;

import com.nec.strudel.instrument.CountInstrument;
import com.nec.strudel.instrument.Instrument;
import com.nec.strudel.instrument.TimeInstrument;
import com.nec.strudel.tkvs.impl.TransactionProfiler;

public class TransactionProfilerImpl implements TransactionProfiler {

	@Instrument(name = "commit_fail")
	private CountInstrument commitFail;
	@Instrument(name = "get_buffered")
	private CountInstrument getBuffered;
	@Instrument
	private TimeInstrument get;
	@Instrument
	private TimeInstrument commit;


	public TransactionProfilerImpl() {
	}

	public void setGet(TimeInstrument get) {
		this.get = get;
	}
	public TimeInstrument getGet() {
		return get;
	}
	public void setCommit(TimeInstrument commit) {
		this.commit = commit;
	}
	public TimeInstrument getCommit() {
		return commit;
	}
	public void setGetBuffered(CountInstrument getBuffered) {
		this.getBuffered = getBuffered;
	}
	public CountInstrument getGetBuffered() {
		return getBuffered;
	}
	public void setCommitFail(CountInstrument commitFail) {
		this.commitFail = commitFail;
	}
	public CountInstrument getCommitFail() {
		return commitFail;
	}
	
	@Override
	public void commitStart(String name) {
		commit.start(name);
	}
	@Override
	public void commitFail(String name) {
		commit.end();
		commitFail.increment(name);
	}
	@Override
	public void commitSuccess(String name) {
		commit.end();
	}
	@Override
	public void getDone(String name) {
		get.end();
	}
	@Override
	public void getStart(String name) {
		get.start(name);
	}
	@Override
	public void getInBuffer(String name) {
		getBuffered.increment(name);
	}

}
