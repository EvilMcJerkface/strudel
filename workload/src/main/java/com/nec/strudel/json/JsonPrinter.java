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
package com.nec.strudel.json;

import java.io.PrintStream;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

public class JsonPrinter {
    private final PrintStream out;
    public JsonPrinter(PrintStream out) {
    	this.out = out;
    }

	public void print(JsonObject rep) {
		out.println(toString(rep));
	}
	String toString(JsonObject json) {
		StringBuilder sb = new StringBuilder();
		toString(json, sb, 0);
		return sb.toString();
	}
	void toString(JsonObject json, StringBuilder sb, int indent) {
		sb.append("{");
		boolean contd = false;
		for (Map.Entry<String, JsonValue> e : json.entrySet()) {
			if (contd) {
				sb.append(",\n");
			} else {
				contd = true;
				sb.append("\n");
			}
			indent(indent + 1, sb);
			sb.append("\"").append(e.getKey()).append("\": ");
			toString(e.getValue(), sb, indent + 1);
		}
		if (contd) {
			sb.append("\n");
			indent(indent, sb);
		}
		sb.append("}");
	}
	void toString(JsonArray json, StringBuilder sb, int indent) {
		sb.append("[");
		boolean contd = false;
		for (JsonValue v : json) {
			if (contd) {
				sb.append(",\n");
			} else {
				contd = true;
				sb.append("\n");
			}
			indent(indent + 1, sb);
			toString(v, sb, indent + 1);
		}
		sb.append("]");
	}
	void toString(JsonValue json, StringBuilder sb, int indent) {
		if (json.getValueType() == ValueType.OBJECT) {
			toString((JsonObject) json, sb, indent);
		} else if (json.getValueType() == ValueType.ARRAY) {
			toString((JsonArray) json, sb, indent);
		} else {
			sb.append(json.toString());
		}
	}
	void indent(int indent, StringBuilder sb) {
		for (int i = 0; i < indent; i++) {
			sb.append("  ");
		}
	}

}
