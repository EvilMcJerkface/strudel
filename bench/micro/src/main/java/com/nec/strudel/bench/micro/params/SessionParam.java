package com.nec.strudel.bench.micro.params;

import com.nec.strudel.session.StateParam;

/**
 * Parameters that are given to a session when
 * it starts.
 * @author tatemura
 *
 */
public enum SessionParam implements StateParam {

	/**
	 * The ID of the user of the session.
	 */
	USER_ID,
	/**
	 * The length of the content of an item
	 * created or updated.
	 */
	CONTENT_LENGTH,

	/**
	 * The minimum user ID (e.g., 0 or 1)
	 */
	MIN_USER_ID,

	/**
	 * The number of users. There must be users with ID:
	 * MIN_USER_ID, MIN_USER_ID + 1, ..., MIN_USER_ID + USER_NUM - 1
	 */
	USER_NUM,

	/**
	 * The minimum set ID (e.g., 0 or 1)
	 */
	MIN_SET_ID,

	/**
	 * The number of shared item sets.
	 */
	SET_NUM,

	/**
	 * The (initial) number of private items per user
	 */
	ITEMS_PER_USER,
	/**
	 * The (initial) number of posts per user
	 */
	POSTS_PER_USER,
	/**
	 * The (initial) number of shared items per set.
	 */
	ITEMS_PER_SET,
	/**
	 * The minimum value of an auto sequence ID
	 * (system dependent value)
	 */
	MIN_SEQ_NO,

	NUM_UPDATE_ITEMS,
}
