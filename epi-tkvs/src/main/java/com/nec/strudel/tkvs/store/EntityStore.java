package com.nec.strudel.tkvs.store;

import com.nec.strudel.entity.EntityDB;
import com.nec.strudel.instrument.Instrumented;
import com.nec.strudel.instrument.Profiler;
import com.nec.strudel.instrument.ProfilerService;
import com.nec.strudel.target.Target;
import com.nec.strudel.tkvs.EntityDBImpl;
import com.nec.strudel.tkvs.TransactionalDB;

public class EntityStore implements Target<EntityDB> {
	private final Target<TransactionalDB> tkvStore;
	public EntityStore(Target<TransactionalDB> tkvStore) {
		this.tkvStore = tkvStore;
	}

	@Override
	public EntityDB open() {
		return new EntityDBImpl(tkvStore.open());
	}

	@Override
	public Instrumented<EntityDB> open(ProfilerService profs) {
		return new ConnectionWrapper(tkvStore.open(profs));
	}

	@Override
	public void close() {
		tkvStore.close();
	}
	@Override
	public void beginUse(EntityDB target) {
	}
	@Override
	public void endUse(EntityDB target) {
	}

	private static class ConnectionWrapper
	implements Instrumented<EntityDB> {
		private final Instrumented<TransactionalDB> con;
		ConnectionWrapper(Instrumented<TransactionalDB> con) {
			this.con = con;
		}

		@Override
		public EntityDB getObject() {
			return new EntityDBImpl(con.getObject());
		}
		@Override
		public Profiler getProfiler() {
			return con.getProfiler();
		}

	}

}
