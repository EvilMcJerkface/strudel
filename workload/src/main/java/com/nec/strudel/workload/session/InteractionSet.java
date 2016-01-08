package com.nec.strudel.workload.session;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.nec.congenio.annotation.MapOf;

@MapOf(InteractionConfig.class)
public class InteractionSet {
	public static InteractionSet empty() {
		return new InteractionSet();
	}
	private final Map<String, InteractionConfig> intrs;

	public InteractionSet(Map<String, InteractionConfig> intrs) {
		this.intrs = intrs;
	}
	public InteractionSet() {
		intrs = Collections.emptyMap();
	}
	public Set<String> names() {
		return intrs.keySet();
	}
	public boolean isEmpty() {
		return intrs.isEmpty();
	}
	public double getProb(String name) {
		InteractionConfig v = intrs.get(name);
		if (v != null) {
			return v.getProb();
		} else {
			return 0;
		}
	}
	@Nullable
	public ThinkTime getThinkTime(String name) {
		InteractionConfig v = intrs.get(name);
		if (v != null) {
			return v.getThinkTime();
		} else {
			return null;
		}
	}
}