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
package com.nec.strudel.json.func;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import com.nec.strudel.json.JsonValues;

/**
 * NOTE: zero division results in JsonValue.NULL
 *
 */
public class Div implements Func {
	private static final Div DIV = new Div();
	public static Func of(Func dividend, Func divisor) {
		return new Apply(DIV, dividend, divisor);
	}
	public Div() {
	}

	@Override
	public JsonValue get(JsonValue... input) {
		JsonValue dividend = input[0];
		JsonValue divisor = input[1];
		if (dividend.getValueType() == ValueType.OBJECT) {
			if (divisor.getValueType() == ValueType.OBJECT) {
				return compute((JsonObject) dividend,
						(JsonObject) divisor).build();
			}
		} else if (dividend.getValueType() == ValueType.NUMBER) {
			if (divisor.getValueType() == ValueType.NUMBER) {
				return compute((JsonNumber) dividend,
						(JsonNumber) divisor);
			}
		} else if (dividend == JsonValue.NULL ||
				divisor == JsonValue.NULL) {
			return JsonValue.NULL;
		}
		/**
		 * TODO support OBJECT div NUMBER and NUMBER div OBJECT
		 */
		throw new RuntimeException("unsupported type for DIV");
	}
	JsonObjectBuilder compute(JsonObject dividend, JsonObject divisor) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		for (String name : divisor.keySet()) {
			double c = getDouble(divisor, name);
			if (c != 0) {
				double t = getDouble(dividend, name);
				b.add(name, t / c);
			}
		}
		return b;
	}
	JsonValue compute(JsonNumber dividend, JsonNumber divisor) {
		double v1 = dividend.doubleValue();
		double v2 = divisor.doubleValue();
		if (v2 == 0) {
			return JsonValue.NULL;
		}
		return JsonValues.toValue(v1 / v2);
	}
    double getDouble(JsonObject obj, String name) {
    	JsonNumber v = obj.getJsonNumber(name);
    	if (v != null) {
    		return v.doubleValue();
    	} else {
    		return 0;
    	}
    }

	@Override
	public void output(JsonObjectBuilder out, String name,
			JsonValue... input) {
		JsonValue dividend = input[0];
		JsonValue divisor = input[1];
		if (dividend.getValueType() == ValueType.OBJECT) {
			if (divisor.getValueType() == ValueType.OBJECT) {
				out.add(name, compute((JsonObject) dividend,
						(JsonObject) divisor));
			}
		} else if (dividend.getValueType() == ValueType.NUMBER) {
			if (divisor.getValueType() == ValueType.NUMBER) {
				JsonValue value = compute(
						(JsonNumber) dividend,
						(JsonNumber) divisor);
				if (value != null) {
					out.add(name, value);
				}
			}
		} else if (dividend == JsonValue.NULL || divisor == JsonValue.NULL) {
			out.add(name, JsonValue.NULL);
		}
		// else throw exception
	}

}
