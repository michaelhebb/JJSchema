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
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.ncmec.jjschema.Attributes;
import org.ncmec.jjschema.JsonSchemaFactory;
import org.ncmec.jjschema.JsonSchemaV4Factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class EmployeeTest {
	private final ObjectMapper MAPPER = new ObjectMapper();
	ObjectWriter ow = this.MAPPER.writerWithDefaultPrettyPrinter();

	static class Employee {
		@Attributes(required = true, minLength = 5, maxLength = 50, description = "Name")
		private String name;

		public String getName() {
			return this.name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		private boolean retired;

		public boolean isRetired() {
			return this.retired;
		}

		public void setRetired(final boolean retired) {
			this.retired = retired;
		}
	}

	JsonSchemaFactory schemaFactory = new JsonSchemaV4Factory();

	{
		this.schemaFactory.setAutoPutDollarSchema(true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testEmployeeSchema() throws IOException {
		JsonNode employeeSchema = this.schemaFactory.createSchema(Employee.class);
		// System.out.println(ow.writeValueAsString(employeeSchema));
		String str = this.MAPPER.writeValueAsString(employeeSchema);
		Map<String, Object> result = this.MAPPER.readValue(str, Map.class);
		assertNotNull(result);
		assertEquals("object", result.get("type"));
		assertNotNull(result.get("required"));
		List required = (List) result.get("required");
		assertEquals("name", required.get(0));
		assertNotNull(result.get("properties"));
		Map properties = (Map) ((Map) result.get("properties")).get("name");
		assertEquals("string", properties.get("type"));
		assertEquals("Name", properties.get("description"));
		assertEquals(5, properties.get("minLength"));
		assertEquals(50, properties.get("maxLength"));
		properties = (Map) ((Map) result.get("properties")).get("retired");
		assertEquals("boolean", properties.get("type"));
	}

}
