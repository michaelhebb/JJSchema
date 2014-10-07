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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Danilo Reinert
 */
public class SchemaIgnoreTest {

	static ObjectMapper MAPPER = new ObjectMapper();
	JsonSchemaFactory v4generator = new JsonSchemaV4Factory();


	/**
	 * Test if @SchemaIgnore works correctly
	 */
	@Test
	public void testGenerateSchema() {

		JsonNode schema = this.v4generator.createSchema(Sale.class);
		// System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(schema));
		JsonNode properties = schema.get("properties");
		assertEquals(1, properties.size());

		schema = this.v4generator.createSchema(SaleItem.class);
		// System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(schema));
		properties = schema.get("properties");
		assertEquals(2, properties.size());
	}

	static class Sale {
		int id;
		@SchemaIgnore
		@JsonManagedReference
		List<SaleItem> saleItems;

		public int getId() {
			return this.id;
		}

		public void setId(final int id) {
			this.id = id;
		}

		public List<SaleItem> getSaleItems() {
			return this.saleItems;
		}

		public void setSaleItems(final List<SaleItem> saleItems) {
			this.saleItems = saleItems;
		}
	}

	static class SaleItem {
		int idSale;
		int seqNumber;
		@SchemaIgnore
		@JsonBackReference
		Sale parent;

		public int getIdSale() {
			return this.idSale;
		}

		public void setIdSale(final int idSale) {
			this.idSale = idSale;
		}

		public int getSeqNumber() {
			return this.seqNumber;
		}

		public void setSeqNumber(final int seqNumber) {
			this.seqNumber = seqNumber;
		}

		public Sale getParent() {
			return this.parent;
		}

		public void setParent(final Sale parent) {
			this.parent = parent;
		}
	}
}
