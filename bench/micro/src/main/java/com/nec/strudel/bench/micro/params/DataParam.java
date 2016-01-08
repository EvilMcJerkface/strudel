/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
