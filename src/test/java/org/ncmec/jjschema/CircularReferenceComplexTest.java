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

import java.io.IOException;

import org.junit.Test;
import org.ncmec.jjschema.model.Person;
import org.ncmec.jjschema.model.Task;
import org.ncmec.jjschema.model.TaskList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author Danilo Reinert
 */
public class CircularReferenceComplexTest {

	static ObjectWriter WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();
	JsonSchemaFactory schemaFactory = new JsonSchemaV4Factory();

	@Test
	public void testGenerateSchema() throws IOException {

		JsonNode taskSchema = this.schemaFactory.createSchema(Task.class);
		System.out.println(WRITER.writeValueAsString(taskSchema));

		JsonNode personSchema = this.schemaFactory.createSchema(Person.class);
		System.out.println(WRITER.writeValueAsString(personSchema));

		JsonNode taskListSchema = this.schemaFactory.createSchema(TaskList.class);
		System.out.println(WRITER.writeValueAsString(taskListSchema));
	}

}
