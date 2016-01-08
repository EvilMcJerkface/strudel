/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
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
package com.nec.strudel.workload.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.nec.strudel.session.Interaction;


@Immutable
public class MarkovStateModel<T> {
	public static final String END = "END";
	public static final String START = "START";

	public static String modifiedState(String name, String mode) {
		return name + "@" + mode;
	}
	public static boolean isModifiedState(String name) {
		return name.contains("@");
	}

	public static class Builder<T> {
		private final Map<String, MarkovState<T>> states =
			new HashMap<String, MarkovState<T>>();
		private final List<Transition> trans =
			new ArrayList<Transition>();

		/**
		 * Creates a "transient" state that has no interaction
		 * to execute
		 * @param name the name of the state, which must be unique.
		 */
		public Builder<T> transientState(String name) {
			MarkovState<T> state = MarkovState.transientState(name);
			return state(state);
		}
		/**
		 * Creates a state that executes the specified interaction.
		 * @param interaction
		 */
		public Builder<T> state(String name, Interaction<T> intr) {
			return state(MarkovState.create(name, intr));
		}
		/**
		 * Creates a state transition.
		 * @param name the current state
		 * @param nexts next states and their probability
		 */
		public Builder<T> transition(String name,
		        Map<String, Double> nexts) {
			trans.add(new Transition(name, nexts));
			return this;
		}
		protected Builder<T> state(MarkovState<T> s) {
			states.put(s.getName(), s);
			return this;
		}
		public MarkovStateModel<T> build() {
			return new MarkovStateModel<T>(states, trans);
		}
	}
	private final Map<String, MarkovState<T>> states;
	private final List<MarkovStateModel.Transition> trans;
	MarkovStateModel(Map<String, MarkovState<T>> states,
			List<MarkovStateModel.Transition> trans) {
		this.states = Collections.unmodifiableMap(states);
		this.trans = Collections.unmodifiableList(trans);
	}
	public Map<String, MarkovState<T>> getStates() {
		return states;
	}
	public MarkovState<T> getState(String name) {
		return states.get(name);
	}
	public List<MarkovStateModel.Transition> getTransitions() {
		return trans;
	}
	public static class Transition {
		private final String name;
		private final Map<String, Double> nexts;
		public Transition(String name,
				Map<String, Double> nexts) {
			this.name = name;
			this.nexts = Collections.unmodifiableMap(nexts);
		}
		public String getName() {
			return name;
		}
		public Map<String, Double> nextStates() {
			return nexts;
		}
	}


	public static class MarkovState<T> {
		/**
		 * The end state has a NULL interaction, which means that
		 * the session ends.
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public static final MarkovState END_STATE =
			new MarkovState(MarkovStateModel.END, null, true);

		/**
		 * Creates a "transient" state that has no interaction
		 * to execute
		 * @param name the name of the state, which must be unique.
		 * @return a Markov state.
		 */
		public static <T> MarkovState<T> transientState(String name) {
			return new MarkovState<T>(name, null, false);
		}
		/**
		 * Creates a state that executes the specified interaction.
		 * @param interaction
		 * @return a Markov state whose name is same as the
		 * interaction's name.
		 */
		public static <T> MarkovState<T> create(
				String name,
		        Interaction<T> interaction) {
			return new MarkovState<T>(name,
					interaction, true);
		}
		private final String name;
		private final Interaction<T> interaction;
		private final boolean hasInteraction;
		MarkovState(String name, Interaction<T> interaction,
				boolean hasInteraction) {
			this.name = name;
			this.interaction = interaction;
			this.hasInteraction = hasInteraction;
		}
		public String getName() {
			return name;
		}
		public Interaction<T> getInteraction() {
			return interaction;
		}
		public boolean hasInteraction() {
			return hasInteraction;
		}
	}
}
