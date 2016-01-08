/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
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
