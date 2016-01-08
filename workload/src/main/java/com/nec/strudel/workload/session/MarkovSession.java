package com.nec.strudel.workload.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.impl.State;
import com.nec.strudel.util.RandomSelector;
import com.nec.strudel.workload.session.MarkovStateModel.MarkovState;
import com.nec.strudel.workload.session.MarkovStateModel.Transition;

/**
 * A session that generates a sequence of user actions based on
 * state transition.
 * @author tatemura
 *
 */
public class MarkovSession<T> extends SessionBaseImpl<T> {

	private final Map<String, RandomSelector<MarkovState<T>>> transMap;
	private final long minTime;
	private final WaitTime wait;

	/**
	 * @param maxCount a positive number specifies the maximum
	 * interaction: if the session generates this number of interactions,
	 * the session goes to END regardless of the transition model.
	 * @param maxTime a positive number specifies the maximum duration:
	 * if the duration becomes longer than this value, the session goes to
	 * END regardless of the transition model.
	 * @param minTime a positive number specifies the User Session Minimum
	 * Duration. If the session reaches END before this duration, the
	 * session generates the next interaction from START.
	 * @param msm
	 */
	@SuppressWarnings("unchecked")
	public MarkovSession(int maxCount, long maxTime, long minTime,
			MarkovStateModel<T> msm, WaitTime wait) {
		super(maxCount, maxTime);
		this.minTime = minTime;
		this.wait = wait;
		transMap = new HashMap<String,
		                RandomSelector<MarkovState<T>>>();


		for (Transition t : msm.getTransitions()) {
			Map<MarkovState<T>, Double> probs =
				new HashMap<MarkovState<T>, Double>();

			for (Map.Entry<String, Double> e
			        : t.nextStates().entrySet()) {
				String name = e.getKey();
				if (MarkovStateModel.END.equals(name)) {
					probs.put(MarkovState.END_STATE,
					        e.getValue());
				} else {
					MarkovState<T> state =
					    msm.getState(name);
					if (state == null) {
						throw new ConfigException(
						    "null state for " + name);
					}
					/**
					 * TODO exception if state == null
					 */

					probs.put(state, e.getValue());
				}
			}
			if (probs.isEmpty()) {
				throw new ConfigException(
					"empty transition: " + t.getName());
			}
			transMap.put(t.getName(), RandomSelector.create(probs));
		}
	}

	public long getMinTime() {
		return minTime;
	}

	@Override
	protected UserAction<T> getFirst(State state) {
		return findNext(MarkovStateModel.START,
				state.getResultMode(), state.getRandom());
	}

	@Override
	protected UserAction<T> getNext(UserAction<T> current, State state) {
		UserAction<T> action = findNext(
				current.getName(),
				state.getResultMode(),
				state.getRandom());
		if (action == null && minTime > 0) {
			long duration =
			    System.currentTimeMillis() - startTime();
			if (duration < minTime) {
				/**
				 * NOTE it has not reach the User Session
				 * Minimum Duration
				 */
				return getFirst(state);
			}
		}
		return action;
	}
	private MarkovState<T> nextState(String name,
			String mode, Random rand) {
		RandomSelector<MarkovState<T>> s = null;
		if (!mode.isEmpty()) {
			s = transMap.get(
				MarkovStateModel.modifiedState(name, mode));
		}
		if (s == null) {
			s = transMap.get(name);
		}
		if (s == null) {
			//TODO exception
		}
		return s.next(rand);
	}

	private UserAction<T> findNext(String current,
			String mode, Random rand) {
		MarkovState<T> s = nextState(current, mode, rand);
		while (!s.hasInteraction()) {
			s = nextState(s.getName(), "", rand);
		}
		return actionFor(s.getName(), s.getInteraction(), rand);
	}
	UserAction<T> actionFor(String name, Interaction<T> intr, Random rand) {
		if (intr == null) {
			return null;
		}
		return new UserAction<T>(name, intr,
				wait.prepareTime(name, rand),
				wait.thinkTime(name, rand));
	}

}
