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