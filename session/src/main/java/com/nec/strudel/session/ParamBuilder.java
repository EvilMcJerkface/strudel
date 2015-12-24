package com.nec.strudel.session;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

public interface ParamBuilder {

	ParamBuilder use(StateParam p);

	ParamBuilder use(LocalParam p, StateParam src);

	boolean defined(StateParam p);

	@Nullable
	<T> T get(StateParam p);

	<T> List<T> getList(StateParam p);

	ParamBuilder set(LocalParam p, Object value);

	ParamBuilder randomInt(LocalParam name, StateParam minName,
			StateParam maxName);

	/**
	 * Defines a random integer ID
	 * @param name the parameter to be defined
	 * @param minName the minimum ID
	 * @param sizeName the size of (consecutive) ID numbers.
	 * @param excludeName the ID to be excluded
	 * @return this
	 */
	ParamBuilder randomIntId(LocalParam name, StateParam minName,
			StateParam sizeName, StateParam excludeName);

	int getRandomIntId(StateParam minName, StateParam sizeName,
			StateParam excludeName);

	/**
	 * Defines a random integer ID
	 * @param name the parameter to be defined
	 * @param minName the minimum ID
	 * @param sizeName the size of (consecutive) ID numbers.
	 * @param excludeName the ID to be excluded
	 * @return this
	 */
	ParamBuilder randomIntId(LocalParam name, StateParam minName,
			StateParam sizeName);

	int getRandomIntId(StateParam minName, StateParam sizeName);

	Set<Integer> getRandomIntIdSet(StateParam countName, StateParam minName,
			StateParam sizeName);

	ParamBuilder randomDouble(LocalParam name, StateParam minName,
			StateParam maxName);

	ParamBuilder randomAlphaString(LocalParam p, int length);

	ParamBuilder randomAlphaString(LocalParam p, StateParam lengthParam);

	int getInt(StateParam pname);

	double getDouble(StateParam name);

	int getRandomInt(StateParam minName, StateParam maxName);

	int getRandomInt(int min, int max);

	double getRandomDouble(StateParam minName, StateParam maxName);

	double getRandomDouble(double min, double max);

	double getRandomDouble();

	int getRandomInt(int max);

	String getRandomAlphaString(int length);

}