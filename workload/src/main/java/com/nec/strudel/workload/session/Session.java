package com.nec.strudel.workload.session;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.nec.strudel.session.impl.State;


/**
 * A Session generates a sequence of interactions.
 * @author tatemura
 *
 */
@NotThreadSafe
public interface Session<T> {

	/**
	 * Identifies what to do next.
	 * @param state the current state of the session
	 * @return the next interaction to execute; null
	 * if the session ends.
	 */
	@Nullable
	UserAction<T> next(State state);

}
