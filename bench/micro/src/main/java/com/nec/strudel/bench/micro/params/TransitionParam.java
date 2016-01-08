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
