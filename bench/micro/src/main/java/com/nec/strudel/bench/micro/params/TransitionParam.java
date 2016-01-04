package com.nec.strudel.bench.micro.params;

import com.nec.strudel.session.StateParam;

/**
 * dynamic session parameters that are set during a session.
 * @author tatemura
 *
 */
public enum TransitionParam implements StateParam {

	/**
	 * The private item of interest, which is chosen from
	 * a set of items retrieved in an interaction.
	 */
	ITEM,
	/**
	 * The post of interest, which is chosen from
	 * a set of posts retrieved in an interaction.
	 */
	POST,
	/**
	 * The shared item of interest, which is chosen from
	 * a set of items retrieved in an interaction.
	 */
	SHARED,

	/**
	 * The submission of interest (the user's submission
	 * to somebody) which the user may update.
	 */
	SUBMISSION,

	PEER_USER_ID,
}
