package com.nec.strudel.workload.session.test.tool;

import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.impl.State;
import com.nec.strudel.workload.session.Session;
import com.nec.strudel.workload.session.SessionFactory;
import com.nec.strudel.workload.session.UserAction;

/**
 * A session that repeats the same interaction
 * for a given number of times.
 * @author tatemura
 *
 * @param <T>
 */
public class RepeatSession<T> implements Session<T> {
	private long prepareTime;
	private long thinkTime;
	private final String name;
	private final Interaction<T> interaction;
	private int count = 0;
	private final int max;

	public RepeatSession(String name,
			Interaction<T> interaction, int repeatCount) {
		this.name = name;
		this.max = repeatCount;
		this.interaction = interaction;
	}
	@Override
	public UserAction<T> next(State state) {
		if (count++ < max) {
			return new UserAction<T>(name, interaction, prepareTime, thinkTime);
		} else {
			return null;
		}
	}
	public RepeatSession<T> setThinkTime(long thinkTime) {
		this.thinkTime = thinkTime;
		return this;
	}
	public RepeatSession<T> setPrepareTime(long prepareTime) {
		this.prepareTime = prepareTime;
		return this;
	}

	/**
	 * Gets a factory that clones this session.
	 */
	public SessionFactory<T> toFactory() {
		return new SessionFactory<T>() {
			@Override
			public Session<T> create() {
				RepeatSession<T> s = new RepeatSession<T>(name, interaction, max);
				s.setPrepareTime(prepareTime);
				s.setThinkTime(thinkTime);
				return s;
			}
		};
	}
}
