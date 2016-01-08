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
package com.nec.strudel.bench.auction.populate.base;

import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.util.RandomSelector;
import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.ValidateReporter;

public abstract class AbstractPopulateUser<T>
implements Populator<T, User> {

	public enum InParam {
		UNAME_LENGTH
	}

	@Override
	public String getName() {
	    return "User";
	}

	@Override
	public User createParameter(PopulateParam param) {
	    final int id = param.getId(); // used as user ID
	    int nameLength = param.getInt(InParam.UNAME_LENGTH);
	    String name = RandomSelector.createAlphaString(nameLength)
	    	       .next(param.getRandom());
	    User user = new User(id, name);
		return user;
	}
    @Override
    public boolean validate(T db, User expected,
    		ValidateReporter reporter) {
    	int id = expected.getUserId();
    	String name = expected.getUname();
        User user = getUser(db, id);
        if (user == null) {
        	reporter.error("missing user id=" + id);
        	return false;
        }
        if (!name.equals(user.getUname())) {
        	reporter.error("user(" + id + ") name ="
        			+ user.getUname()
        			+ "(expected:" + name + ")");
        	return false;
        }
    	return true;
    }

	protected abstract User getUser(T db, int id);
}