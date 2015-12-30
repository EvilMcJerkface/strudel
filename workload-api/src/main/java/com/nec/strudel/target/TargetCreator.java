package com.nec.strudel.target;

import javax.annotation.Nullable;


public interface TargetCreator<T> {
	Target<T> create(TargetConfig config);

	TargetLifecycle createLifecycle(TargetConfig config);

	@Nullable
	Class<?> instrumentedClass(TargetConfig config);
}
