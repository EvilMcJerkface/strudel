package com.nec.strudel.session;

import java.util.Set;

import javax.annotation.Nullable;

import com.nec.strudel.session.Interaction;


public interface InteractionFactory<T> {

	/**
	 * Creates (or gets) an instance of Interaction
	 * specified with a given name.
	 * @param name the name of the interaction.
	 * @return null if there is no such interaction.
	 */
	@Nullable
	Interaction<T> create(String name);

	Set<String> names();
}
