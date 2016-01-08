package com.nec.strudel.json.func;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.nec.strudel.json.JsonValues;

public class Now implements Func {
	public static Func now() {
		return new Now();
	}
	public static Func now(String format) {
		return new Now(format);
	}
	public static final String DEFAULT_FORMAT =
			"yyyy-MM-dd'T'HH:mm:ssz";
	private DateFormat dateFormat;

	public Now() {
		dateFormat =
				new SimpleDateFormat(DEFAULT_FORMAT);
	}
	public Now(String dateFormat) {
		this.dateFormat =
				new SimpleDateFormat(dateFormat);
	}

	@Override
	public JsonValue get(JsonValue... input) {
		return JsonValues.toValue(dateFormat.format(new Date()));
	}

	@Override
	public void output(JsonObjectBuilder out,
			String name, JsonValue... input) {
		out.add(name, get(input));
	}

}
