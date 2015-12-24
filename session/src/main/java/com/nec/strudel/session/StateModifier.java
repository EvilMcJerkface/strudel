package com.nec.strudel.session;

import javax.annotation.Nullable;

public interface StateModifier {

	StateModifier export(StateParam p);

	StateModifier set(StateParam name, Object value);

	StateModifier choose(StateParam p, ParamName listName);

	StateModifier chooseSubset(StateParam p, StateParam sizeName,
			ParamName listName);

	@Nullable
	Object get(ParamName name);

	@Nullable
	<T> T getOne(ParamName listName);

	boolean isSuccess();

}
