package com.nec.strudel.bench.auction.interactions.base;

import com.nec.strudel.bench.auction.entity.User;
import com.nec.strudel.bench.auction.params.SessionParam;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.LocalParam;
import com.nec.strudel.session.Param;
import com.nec.strudel.session.ParamBuilder;
import com.nec.strudel.session.Result;
import com.nec.strudel.session.ResultBuilder;
import com.nec.strudel.session.StateModifier;

public abstract class AbstractViewUser<T> implements Interaction<T> {

	public enum OutParam implements LocalParam {
		USER
	}

	@Override
	public void prepare(ParamBuilder builder) {
		builder.use(SessionParam.USER_ID);
	}

	@Override
	public void complete(StateModifier modifier) {
	    // do nothing
	}

	public int getUserId(Param param) {
		return param.getInt(SessionParam.USER_ID);
	}

	public Result resultOf(User user, Param param, ResultBuilder res) {
		if (user == null) {
			int userId = param.getInt(SessionParam.USER_ID);
			res.warn("user not found: id=" + userId);
		}
		res.set(OutParam.USER, user);
		return res.success();
	}

}