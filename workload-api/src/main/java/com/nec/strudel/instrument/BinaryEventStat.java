package com.nec.strudel.instrument;

public interface BinaryEventStat {

	void event(boolean mode);

	double getTrueRatio();

}