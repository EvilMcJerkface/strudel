package com.nec.strudel.workload.util.event;

import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;


/**
 * A generator of event time sequence.
 * @author tatemura
 *
 * @param <R> the type of event result.
 */
@NotThreadSafe
public interface EventSeq<R> {

	/**
	 * Gets the set of timed events
	 * driven by the completion of
	 * an event.
	 * @param res the result of the
	 * completed event.
	 * @return empty if there is no event
	 * driven by the given result.
	 */
	Collection<TimedEvent<R>> next(R res);

	/**
	 * Gets the initial set of timed events.
	 */
	Collection<TimedEvent<R>> start();

	/**
	 * Polls to check if there are new timed
	 * events. The executor will call this periodically
	 * (at least once per second).
	 * <p>
	 * A typical use case is to represent arrival of
	 * new users (to initiate new sessions).
	 * @return an empty collection if there is
	 * no new timed event.
	 */
	Collection<TimedEvent<R>> poll();
}
