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
package com.nec.strudel.workload.jobexec.com;

import com.nec.strudel.workload.com.Command;
import com.nec.strudel.workload.com.CompositeCommand;
import com.nec.strudel.workload.out.OutputSet;

public class OutputFinder {
	public static OutputSet output(Command com) {
		return new OutputFinder().findOutput(com);
	}
	public OutputSet findOutput(Command com) {
		OutputSet.Builder ob = OutputSet.builder();
		findOutput(com, ob);
		return ob.build();
	}
	private void findOutput(Command com, OutputSet.Builder ob) {
		if (com instanceof WorkloadCommand) {
			ob.add(((WorkloadCommand) com).outputs());
		} else if (com instanceof CompositeCommand) {
			CompositeCommand coms = (CompositeCommand) com;
			for (Command c : coms.commands()) {
				findOutput(c, ob);
			}
		}
	}
	
}