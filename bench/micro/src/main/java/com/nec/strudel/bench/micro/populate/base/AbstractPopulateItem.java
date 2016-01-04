package com.nec.strudel.bench.micro.populate.base;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.params.DataParam;
import com.nec.strudel.bench.micro.populate.ContentSet;
import com.nec.strudel.util.RandomSelector;
import com.nec.strudel.workload.api.PopulateParam;
import com.nec.strudel.workload.api.Populator;
import com.nec.strudel.workload.api.ValidateReporter;

public abstract class AbstractPopulateItem<T> implements Populator<T, ContentSet> {

	@Override
	public String getName() {
		return "Item";
	}
	/**
	 * Populate Item using the ContentSet which
	 * contains a user ID and a set of content texts.
	 */
	@Override
	public abstract void process(T db, ContentSet param);


	@Override
	public ContentSet createParameter(PopulateParam param) {
		int userId = param.getId();
		int length = param.getInt(DataParam.CONTENT_LENGTH);
		RandomSelector<String> selector =
				RandomSelector.createAlphaString(length);
		return ContentSet.create(userId,
				param.getInt(DataParam.ITEMS_PER_USER),
				selector, param.getRandom());
	}
	@Override
	public boolean validate(T db, ContentSet param,
			ValidateReporter reporter) {
		int userId = param.getGroupId();

		List<Item> items = getItemsByUser(db, userId);
		return validate(param, items, reporter);
	}
	protected abstract List<Item> getItemsByUser(T db, int userId);

	protected boolean validate(ContentSet param, List<Item> items,
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
		for (Item item : items) {
			if (!contentSet.contains(item.getContent())) {
				reporter.error("invalid content in an item("
						+ item.getItemId() + ")");
				return false;
			}
		}
		return true;

	}

}