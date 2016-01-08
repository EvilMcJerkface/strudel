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

import com.nec.strudel.exceptions.ConfigException;
import com.nec.strudel.param.ParamConfig;
import com.nec.strudel.session.InteractionFactory;
import com.nec.strudel.util.ClassUtil;
import com.nec.strudel.workload.job.ConfigParam;
import com.nec.strudel.workload.out.Output;
import com.nec.strudel.workload.session.runner.SessionExecFactory;
import com.nec.strudel.workload.session.runner.SessionExecs;

/**
 * Session config contains the following elements:
 * <pre>
 * "Session" : {
 *   "maxCount" : 0,
 *   "maxTime": 0,
 *   "minTime" : 0,
 *   "Package" : null,
 *   "Factory" : null,
 *   "Interactions": {},
 *   "Transitions": {},
 *   "ThinkTime" : null
 *   "Params" : { NAME : VALUE }
 *   "Runner" : null
 * }
 * </pre>
 *Interactions contains a key-value:
 *<pre>
 * NAME : {
 *   "prob" : 0,
 *   "ThinkTime" : null
 * }
 *</pre>
 *Transition contains a key-value:
 *<pre>
 * NAME : {
 *   NAME_1 : { "prob" : 1},
 *   ...
 *   NAME_N : { "prob" : 1}
 * }
 *</pre>
 * @param <T>
 */
public class SessionConfig<T> {
	public static final String SESSION = "session";

	@SuppressWarnings("unchecked")
	public static <T> SessionConfig<T> create(ConfigParam param) {
		return param.getObject(SESSION, SessionConfig.class);
	}

	private int maxCount = 0;
	private int maxTime = 0;
	private int minTime = 0;
	private ParamConfig params = ParamConfig.empty();
	private ThinkTime thinkTime = ThinkTime.noTime();
	private String packageName = "";
	private String factory = "";
	private String execType = "";
	private String execFactory = "";
	private int sessionConcurrency = 0;
	private InteractionSet interactions = InteractionSet.empty();
	private TransitionSet transitions = TransitionSet.empty();

	public SessionConfig() {
	}

	public int getSessionConcurrency() {
		return sessionConcurrency;
	}
	public void setSessionConcurrency(int sessionConcurrency) {
		this.sessionConcurrency = sessionConcurrency;
	}

	public ParamConfig getParams() {
		return params;
	}
	public void setParams(ParamConfig params) {
		this.params = params;
	}
	public ThinkTime getThinkTime() {
		return thinkTime;
	}
	public void setThinkTime(ThinkTime thinkTime) {
		this.thinkTime = thinkTime;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public int getMinTime() {
		return minTime;
	}

	public void setMinTime(int minTime) {
		this.minTime = minTime;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getFactory() {
		return factory;
	}

	public void setFactory(String factory) {
		this.factory = factory;
	}

	public String getExecType() {
		return execType;
	}

	public void setExecType(String execType) {
		this.execType = execType;
	}

	public String getExecFactory() {
		return execFactory;
	}

	public void setExecFactory(String execFactory) {
		this.execFactory = execFactory;
	}

	public InteractionSet getInteractions() {
		return interactions;
	}

	public void setInteractions(InteractionSet interactions) {
		this.interactions = interactions;
	}

	public TransitionSet getTransitions() {
		return transitions;
	}

	public void setTransitions(TransitionSet transitions) {
		this.transitions = transitions;
	}

	public SessionFactory<T> createSessionFactory(String classPath) {
		SessionFactory.Builder<T> builder = new SessionFactory.Builder<T>()
			.maxCount(maxCount)
			.maxTime(maxTime)
			.minTime(minTime);

		InteractionBuilder ib = InteractionBuilder.builder(interactions, transitions);
		ib.build(createInteractionFactory(classPath), builder);

		return builder
				.waitTime(ib.buildWaitTime(thinkTime))
				.build();
	}

	public InteractionFactory<T> createInteractionFactory(String classPath) {
		if (!packageName.isEmpty()) {
			return new PackageInteractionFactory<T>(packageName, classPath);
		} else if (!factory.isEmpty()) {
			return ClassUtil.create(factory, classPath);
		} else {
			throw new ConfigException("missing packageName or factory");
		}
	}

	public SessionExecFactory<T> getSessionExecFactory(String classPath) {
		SessionExecFactory<T> factory =
				createSessionExecFactory(classPath);
		factory.initialize(this);
		return factory;
	}

	public Output output(String classPath) {
		return createSessionExecFactory(classPath).output(this);
	}

	private SessionExecFactory<T> createSessionExecFactory(String classPath) {
		if (!execType.isEmpty()) {
			return SessionExecs.createFactory(execType, classPath);
		} else if (!execFactory.isEmpty()) {
			return ClassUtil.create(execFactory, classPath);
		}
		return SessionExecs.createFactory(
				this, classPath);
	}

}
