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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.InteractionFactory;
import com.nec.strudel.workload.session.SessionFactory.Builder;

public abstract class InteractionBuilder {

	public abstract <T> void build(InteractionFactory<T> factory,
			SessionFactory.Builder<T> builder);

	public abstract void buildWaitTimes(WaitTime.Builder builder);

	public WaitTime buildWaitTime(ThinkTime globalThinkTime) {
		WaitTime.Builder builder = new WaitTime.Builder();
		builder.set(globalThinkTime);
		this.buildWaitTimes(builder);
		return builder.build();
	}
	public static InteractionBuilder builder(InteractionSet ic, TransitionSet tc) {
		if (tc.isEmpty()) {
			return new RandomInteractionBuilder(ic);
		} else {
			return new MarkovInteractionBuilder(tc);
		}
	}

	public static class RandomInteractionBuilder extends InteractionBuilder {
		private InteractionSet ic;

		public RandomInteractionBuilder(InteractionSet ic) {
			this.ic = ic;
		}

		@Override
		public <T> void build(InteractionFactory<T> factory, Builder<T> builder) {
			Map<String, Double> probs =
					new HashMap<String, Double>();
	
			Set<String> names = factory.names();
			for (String name : ic.names()) {
				if (!names.contains(name)) {
					throw new ConfigException(
							"unknown interaction: " + name);
				}
				double prob = ic.getProb(name);
				if (prob > 0) {
					probs.put(name, prob);
				}
			}
			if (probs.isEmpty()) {
				if (ic.isEmpty()) {
					buildUniform(factory, builder, names);
				} else {
					buildUniform(factory, builder, ic.names());
				}
			} else {
				buildRandom(factory, builder, probs);
			}
		}
		private <T> void buildUniform(InteractionFactory<T> factory, Builder<T> builder,
				Set<String> names) {
			Map<String, Interaction<T>> intrMap =
					new HashMap<String, Interaction<T>>();
			for (String name : names) {
				intrMap.put(name, factory.create(name));
			}
			builder.uniform(intrMap);
		}
		private <T> void buildRandom(InteractionFactory<T> factory, Builder<T> builder,
				Map<String, Double> probs) {
			Map<String, Interaction<T>> intrMap =
					new HashMap<String, Interaction<T>>();
			for (String name : probs.keySet()) {
				intrMap.put(name, factory.create(name));
			}
			builder.random(intrMap, probs);
		}
		@Override
		public void buildWaitTimes(WaitTime.Builder builder) {
			for (String name : ic.names()) {
				ThinkTime thinkTime = ic.getThinkTime(name);
				if (thinkTime != null) {
					builder.set(name, thinkTime);
				}
			}
		}
	}
	public static class MarkovInteractionBuilder extends InteractionBuilder {
		private final TransitionSet tc;
		private final Set<String> froms = new HashSet<String>();
		private final Set<String> tos = new HashSet<String>();
		private final Set<String> knownNames = new HashSet<String>();

		public MarkovInteractionBuilder(TransitionSet tc) {
			this.tc = tc;
		}
		@Override
		public <T> void build(InteractionFactory<T> factory, Builder<T> builder) {
			MarkovStateModel.Builder<T> mb =
					new MarkovStateModel.Builder<T>();
			setStates(mb, factory);
	
			for (String name : tc.names()) {
				fromName(name, mb);
				Map<String, Double> nexts = tc.nexts(name);
				if (nexts.isEmpty()) {
					throw new ConfigException(
							"empty transition: " + name);
				}
				for (String to : nexts.keySet()) {
					tos.add(to);
				}
				mb.transition(name, nexts);
			}
	
			validate();
			builder.states(mb.build());
		}

		private <T> Set<String> setStates(MarkovStateModel.Builder<T> mb,
				InteractionFactory<T> factory) {
			for (String name : factory.names()) {
				Interaction<T> intr = factory.create(name);
				if (intr == null) {
					throw new ConfigException(
							"interaction not found: " + name);
				}
				knownNames.add(name);
				mb.state(name, intr);
			}
			knownNames.add(MarkovStateModel.START);
			knownNames.add(MarkovStateModel.END);
			return knownNames;
		}

		private <T> void fromName(String name, MarkovStateModel.Builder<T> mb) {
			if (froms.contains(name)) {
				throw new ConfigException(
						"duplicate transition from "
								+ name);
			}
			froms.add(name);
			if (!knownNames.contains(name)) {
				// i.e., not defined in Interactions
				if (!MarkovStateModel.isModifiedState(name)) {
					// i.e., not in the form of State@Mode
					mb.transientState(name);
				}
			}
		}
		private void validate() {
			for (String name : tos) {
				if ("START".equals(name)) {
					throw new ConfigException(
							"cannot transition to START");
				}
				if (!("END".equals(name) || froms.contains(name))) {
					throw new ConfigException(
							"transition to unknown state: " + name);
				}
			}
		}
		@Override
		public void buildWaitTimes(
				WaitTime.Builder builder) {
		}
		
	}

}