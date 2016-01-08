package com.nec.strudel.workload.session;

import javax.annotation.concurrent.Immutable;

import com.nec.strudel.session.Interaction;


/**
 * An emulated user action including wait times and an interaction.
 * @author tatemura
 *
 */
@Immutable
public class UserAction<T> {
	private final String name;
	private final long prepareTime;
	private final long thinkTime;
	private final Interaction<T> interaction;
	public UserAction(String name, Interaction<T> interaction,
	        long prepareTime, long thinkTime) {
		this.name = name;
		this.interaction = interaction;
		this.prepareTime = prepareTime;
		this.thinkTime = thinkTime;
	}
	public String getName() {
		return name;
	}
	/**
	 * Time to spend after the emulated user chooses
	 * this interaction before starting the
	 * interaction.
	 * <p>
	 * In TPC-C, this corresponds to the "keying time,"
	 * the time for the emulated user to fill an input form.
	 * @return prepare time in msec.
	 */
	public long getPrepareTime() {
		return prepareTime;
	}
	/**
	 * An interaction that emulates the user's request.
	 */
	public Interaction<T> getInteraction() {
		return interaction;
	}
	/**
	 * Time to spend after this interaction
	 * is done before the user chooses the next interaction.
	 * @return think time in msec.
	 */
	public long getThinkTime() {
		return thinkTime;
	}

}
