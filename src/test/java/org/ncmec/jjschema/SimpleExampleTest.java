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

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.ncmec.jjschema.Attributes;
import org.ncmec.jjschema.JsonSchemaFactory;
import org.ncmec.jjschema.JsonSchemaV4Factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author reinert
 */
public class SimpleExampleTest {

	static ObjectMapper MAPPER = new ObjectMapper();
	JsonSchemaFactory schemaFactory = new JsonSchemaV4Factory();

	@Test
	public void testGenerateSchema() throws JsonProcessingException, IOException {

		final InputStream in = SimpleExampleTest.class.getResourceAsStream("/simple_example.json");
		if (in == null) {
			throw new IOException("resource not found");
		}
		JsonNode fromResource = MAPPER.readTree(in);
		JsonNode fromJavaType = this.schemaFactory.createSchema(SimpleExample.class);

		assertEquals(fromResource, fromJavaType);
	}

	@Attributes(title = "Example Schema")
	static class SimpleExample {
		@Attributes(required = true)
		private String firstName;
		@Attributes(required = true)
		private String lastName;
		@Attributes(description = "Age in years", minimum = 0)
		private int age;

		public String getFirstName() {
			return this.firstName;
		}

		public void setFirstName(final String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return this.lastName;
		}

		public void setLastName(final String lastName) {
			this.lastName = lastName;
		}

		public int getAge() {
			return this.age;
		}

		public void setAge(final int age) {
			this.age = age;
		}
	}
}
