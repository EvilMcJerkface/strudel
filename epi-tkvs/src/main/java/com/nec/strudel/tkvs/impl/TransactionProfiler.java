/*******************************************************************************
 * Copyright 2015 Junichi Tatemura
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
package com.nec.strudel.tkvs.impl;

public interface TransactionProfiler {
	void getStart(String name);
	void getDone(String name);
	void getInBuffer(String name);
	void commitStart(String name);
	void commitSuccess(String name);
	void commitFail(String name);

	TransactionProfiler NO_PROF =
			new TransactionProfiler() {
				@Override
				public void getStart(String name) {
				}
				@Override
				public void getInBuffer(String name) {
				}
				@Override
				public void getDone(String name) {
				}
				@Override
				public void commitStart(String name) {
				}
				@Override
				public void commitSuccess(String name) {
				}
				@Override
				public void commitFail(String name) {
				}
	};
}
