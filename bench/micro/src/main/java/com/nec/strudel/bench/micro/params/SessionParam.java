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

import com.nec.strudel.session.StateParam;

/**
 * Parameters that are given to a session when it starts.
 * 
 * @author tatemura
 *
 */
public enum SessionParam implements StateParam {

    /**
     * The ID of the user of the session.
     */
    USER_ID,
    /**
     * The length of the content of an item created or updated.
     */
    CONTENT_LENGTH,

    /**
     * The minimum user ID (e.g., 0 or 1)
     */
    MIN_USER_ID,

    /**
     * The number of users. There must be users with ID: MIN_USER_ID,
     * MIN_USER_ID + 1, ..., MIN_USER_ID + USER_NUM - 1
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
     * The minimum value of an auto sequence ID (system dependent value)
     */
    MIN_SEQ_NO,

    NUM_UPDATE_ITEMS,
}
