/*
 * Copyright (c) 2014, Danilo Reinert (daniloreinert@growbit.com)
 * 
 * This software is dual-licensed under:
 * 
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any later version; -
 * the Apache Software License (ASL) version 2.0.
 * 
 * The text of both licenses is available under the src/resources/ directory of this project (under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 * 
 * Direct link to the sources:
 * 
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt - ASL 2.0:
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package org.ncmec.jjschema;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author Danilo Reinert
 */

public class EnumSchemaWrapper extends SchemaWrapper {

	public <T> EnumSchemaWrapper(final Class<T> type) {
		super(type);
		this.processEnum(type);
		this.processNullable();
	}

	@Override
	public boolean isEnumWrapper() {
		return true;
	}

	// TODO: Shouldn't I check the Nullable annotation only on fields or methods?
	@Override
	protected void processNullable() {
		final Nullable nullable = this.getJavaType().getAnnotation(Nullable.class);
		if (nullable != null) {
			((ArrayNode) this.getNode().get("enum")).add("null");
		}
	}

	private <T> void processEnum(final Class<T> type) {
		ArrayNode enumArray = this.getNode().putArray("enum");
		for (T constant : type.getEnumConstants()) {
			String value = constant.toString();
			// Check if value is numeric
			try {
				// First verifies if it is an integer
				Long integer = Long.valueOf(value);
				enumArray.add(integer);
				this.setType("integer");
			}
			// If not then verifies if it is an floating point number
			catch (NumberFormatException e) {
				try {
					BigDecimal number = new BigDecimal(value);
					enumArray.add(number);
					this.setType("number");
				}
				// Otherwise add as String
				catch (NumberFormatException e1) {
					enumArray.add(value);
					this.setType("string");
				}
			}
		}
	}
}
