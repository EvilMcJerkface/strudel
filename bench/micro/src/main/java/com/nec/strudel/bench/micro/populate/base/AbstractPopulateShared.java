package com.nec.strudel.bench.micro.populate.base;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nec.strudel.bench.micro.entity.Shared;
import com.nec.strudel.bench.micro.params.DataParam;
import com.nec.strudel.bench.micro.populate.ContentSet;
import com.nec.strudel.util.RandomSelector;
import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.ValidateReporter;

public abstract class AbstractPopulateShared<T> implements Populator<T, ContentSet> {

	@Override
	public String getName() {
		return "Shared";
	}

	@Override
	public ContentSet createParameter(PopulateParam param) {
		int setId = param.getId();
		int length = param.getInt(DataParam.CONTENT_LENGTH);
		RandomSelector<String> selector =
				RandomSelector.createAlphaString(length);
		return ContentSet.create(setId,
				param.getInt(DataParam.ITEMS_PER_SET),
				selector, param.getRandom());
	}
	protected abstract List<Shared> getSharedBySetId(T db, int setId);

	@Override
	public boolean validate(T db, ContentSet param,
			ValidateReporter reporter) {
		int setId = param.getGroupId();

		List<Shared> items = getSharedBySetId(db, setId);
		return validate(param, items, reporter);
	}

	protected boolean validate(ContentSet param, List<Shared> items,
			ValidateReporter reporter) {
		String[] contents = param.getContents();
		Set<String> contentSet =
				new HashSet<String>();
		for (int i = 0; i < contents.length; i++) {
			contentSet.add(contents[i]);
		}
		if (items.size() != contents.length) {
			reporter.error(contents.length + " items expected but "
					+ items.size() + " found");
			return false;
		}
		for (Shared shared : items) {
			if (!contentSet.contains(shared.getContent())) {
				reporter.error("invalid content in an shared("
						+ shared.getSharedId() + ")");
				return false;
			}
		}
		return true;
	}
}