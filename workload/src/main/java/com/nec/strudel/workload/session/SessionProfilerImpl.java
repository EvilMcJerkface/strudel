package com.nec.strudel.workload.session;

import javax.annotation.concurrent.NotThreadSafe;

import com.nec.strudel.instrument.Instrument;
import com.nec.strudel.instrument.impl.TimeOutput;
import com.nec.strudel.instrument.impl.TimeProfiler;
import com.nec.strudel.json.func.Div;
import com.nec.strudel.json.func.Sum;
import com.nec.strudel.json.func.Value;
import com.nec.strudel.session.Result;
import com.nec.strudel.workload.exec.ReportNames;
import com.nec.strudel.workload.out.Output;
import com.nec.strudel.workload.session.runner.SessionStatMonitor;

@NotThreadSafe
public class SessionProfilerImpl implements SessionProfiler {
	public static final String PROFILE_NAME = "session";
	public static final String INTERACTION = "interaction";
	public static final String COUNT_TIMES = TimeOutput.timeOf(INTERACTION);
	public static final String COUNT_COUNTS = TimeOutput.countOf(INTERACTION);

	public static final String INTERACTION_PER_SEC =
			"interaction_per_sec";
	public static final String AVG_EXEC_TIME =
			"average_exec_time";
	public static final String COUNT_AVG_TIMES =
			"interaction_avg_exec_time";
	private static final Output OUTPUT =
			Output.builder()
			.add(INTERACTION_PER_SEC,
				Div.of(
					Sum.of(Value.of(COUNT_COUNTS)),
					Value.of(ReportNames.VALUE_MEASURE)))

			.add(AVG_EXEC_TIME,
				Div.of(
					Sum.of(Value.of(COUNT_TIMES)),
					Sum.of(Value.of(COUNT_COUNTS))))

			.add(new TimeOutput(INTERACTION)
					.avg(COUNT_AVG_TIMES).outputs())
			.build();

	@Instrument
	private TimeProfiler interaction;
	private SessionStatMonitor mon;

	public SessionProfilerImpl() {
	}
	public TimeProfiler getInteraction() {
		return interaction;
	}
	public void setInteraction(TimeProfiler interaction) {
		this.interaction = interaction;
	}
	public void setMon(SessionStatMonitor mon) {
		this.mon = mon;
	}

	@Override
	public void newSession() {
		mon.newSession();
	}

	@Override
	public void startInteraction(String name) {
		interaction.start(name);
	}

	@Override
	public void finishInteraction(Result result) {
		long micro = interaction.end();
		if (micro > 0) {
			mon.interaction(micro, result.isSuccess());
		}
	}


	public static Output output() {
		return OUTPUT;
	}
	public static SessionProfiler noProfile() {
		return new SessionProfiler() {
			@Override
			public void startInteraction(String name) {
			}

			@Override
			public void newSession() {
			}

			@Override
			public void finishInteraction(Result result) {
			}
		};
	}
}