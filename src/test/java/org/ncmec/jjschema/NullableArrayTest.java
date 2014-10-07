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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.ncmec.jjschema.JsonSchemaFactory;
import org.ncmec.jjschema.JsonSchemaV4Factory;
import org.ncmec.jjschema.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author reinert
 */
public class NullableArrayTest {

	static ObjectMapper MAPPER = new ObjectMapper();
	JsonSchemaFactory schemaFactory = new JsonSchemaV4Factory();


	@Test
	public void testGenerateSchema() {

		JsonNode schema = this.schemaFactory.createSchema(Something.class);
		System.out.println(schema);

		JsonNode expected = MAPPER.createArrayNode().add("array").add("null");

		assertEquals(expected, schema.get("properties").get("names").get("type"));

	}

	static class Something {

		private int id;
		@Nullable
		private List<String> names;

		public int getId() {
			return this.id;
		}

		public void setId(final int id) {
			this.id = id;
		}

		public List<String> getNames() {
			return this.names;
		}

		public void setNames(final List<String> names) {
			this.names = names;
		}

	}
}
