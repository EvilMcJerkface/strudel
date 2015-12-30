package com.nec.strudel.instrument;

public interface CountInstrument {

	void increment(String name);

	void add(String name, long value);

}