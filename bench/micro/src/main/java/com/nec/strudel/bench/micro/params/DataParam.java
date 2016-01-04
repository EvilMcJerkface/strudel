package com.nec.strudel.bench.micro.params;

import com.nec.strudel.session.ParamName;

/**
 * Parameters for data populator
 * @author tatemura
 *
 */
public enum DataParam implements ParamName {
	/**
	 * The number of users populated
	 */
	USER_NUM,
	/**
	 * The number of shared sets populated
	 */
	SET_NUM,

	/**
	 * The number of private items populated for
	 * each user.
	 */
	ITEMS_PER_USER,

	POSTS_PER_USER,

	/**
	 * The number of submissions (to someone)
	 * populated for each user (as a sender).
	 */
	SUBMISSIONS_PER_USER,

	/**
	 * The number of items populated for
	 * each shared items set.
	 */
	ITEMS_PER_SET,

	/**
	 * The length of the content of each item.
	 */
	CONTENT_LENGTH,

	MIN_USER_ID,
}
