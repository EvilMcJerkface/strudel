package com.nec.strudel.workload.session.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.nec.strudel.exceptions.WorkloadException;
import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.session.Result;
import com.nec.strudel.target.Target;
import com.nec.strudel.util.RandomSelector;
import com.nec.strudel.workload.job.ConfigParam;
import com.nec.strudel.workload.session.SessionContainer;
import com.nec.strudel.workload.session.SessionFactory;
import com.nec.strudel.workload.session.SessionProfiler;
import com.nec.strudel.workload.session.StateFactory;

public class UserPoolClosedSessionRunner<T>
extends AbstractSessionRunner<T> {
	public static final String TAG_NEW_USER_RATIO = "NewUserRatio";
	public static final String SESSION_CONCURRENCY = "SessionConcurrency";
	private static final Logger LOGGER =
            Logger.getLogger(UserPoolClosedSessionRunner.class);
	private final List<SessionContainer<T>> pool =
			new ArrayList<SessionContainer<T>>();
	private final int size;
	private final RandomSelector<Boolean> useNew;
	private final Random rand;

	public UserPoolClosedSessionRunner(int id,
			SessionFactory<T> sfactory,
			Instrumented<T> con,
			Target<T> target, StateFactory states,
			Instrumented<? extends SessionProfiler> prof, ConfigParam param) {
		super(id, sfactory, con, target, states, prof);
		this.rand = states.getRandom();
		double ratio = param.getDouble(TAG_NEW_USER_RATIO, 0);
		this.useNew = RandomSelector.createBoolean(ratio);
		this.size = param.getInt(SESSION_CONCURRENCY, 1);
	}

	@Override
	public void runSessions() {
        for (SessionContainer<T> sc = nextSession();
                isRunning() && sc != null; sc = nextSession()) {
            runAction(sc);
        }
	}
	@Override
	public int getSessionConcurrency() {
		return size;
	}

    private void runAction(SessionContainer<T> sc) {
        String name = sc.nextName();
        try {
            Result r = doAction(sc);
            inspectResult(name, r);
            actionDone(sc);
        } catch (WorkloadException e) {
            int id = getId();
            LOGGER.debug("one session failed @" + id);
        	LOGGER.error("session failed due to exception", e);
        }

    }
    private void actionDone(SessionContainer<T> sc) {
    	if (!sc.isActive()) {
    		pool.remove(sc);
    	}
    }
    private void created(SessionContainer<T> sc) {
    	if (pool.size() < size) {
        	pool.add(sc);
    	} else {
        	int idx = rand.nextInt(pool.size());
    		pool.set(idx, sc);
    	}
    }
    private boolean useNewSession() {
    	return pool.size() < size || useNew.next(rand);
    }
    private SessionContainer<T> chooseExisting() {
    	if (pool.isEmpty()) {
    		return null;
    	}
    	int idx = rand.nextInt(pool.size());
    	return pool.get(idx);
    }
    private SessionContainer<T> nextSession() {
    	if (useNewSession()) {
            SessionContainer<T> next = newSession();
    		created(next);
    		return next;
    	} else {
    		return chooseExisting();
    	}
    }

}
