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
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.ncmec.jjschema.Attributes;
import org.ncmec.jjschema.JsonSchemaFactory;
import org.ncmec.jjschema.JsonSchemaV4Factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.fge.jackson.JsonLoader;

public class ProductTest {

	ObjectWriter om = new ObjectMapper().writerWithDefaultPrettyPrinter();
	JsonSchemaFactory schemaFactory = new JsonSchemaV4Factory();

	{
		this.schemaFactory.setAutoPutDollarSchema(true);
	}

	@Test
	public void testProductSchema() throws IOException {
		JsonNode productSchema = this.schemaFactory.createSchema(Product.class);
		JsonNode productSchemaRes = JsonLoader.fromResource("/product_schema.json");
		assertEquals(productSchemaRes, productSchema);

		// TODO: Add support to custom Iterable classes?
		// NOTE that my implementation of ProductSet uses the ComplexProduct
		// class that inherits from Product class. That's an example of
		// inheritance support of JJSchema.
	}

	@Attributes(title = "Product", description = "A product from Acme's catalog")
	static class Product {

		@Attributes(required = true, description = "The unique identifier for a product")
		private long id;
		@Attributes(required = true, description = "Name of the product")
		private String name;
		@Attributes(required = true, minimum = 0, exclusiveMinimum = true)
		private BigDecimal price;
		@Attributes(minItems = 1, uniqueItems = true)
		private List<String> tags;

		public long getId() {
			return this.id;
		}

		public void setId(final long id) {
			this.id = id;
		}

		public String getName() {
			return this.name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public BigDecimal getPrice() {
			return this.price;
		}

		public void setPrice(final BigDecimal price) {
			this.price = price;
		}

		public List<String> getTags() {
			return this.tags;
		}

		public void setTags(final List<String> tags) {
			this.tags = tags;
		}

	}

	static class ComplexProduct extends Product {

		private Dimension dimensions;
		@Attributes(description = "Coordinates of the warehouse with the product")
		private Geo warehouseLocation;

		public Dimension getDimensions() {
			return this.dimensions;
		}

		public void setDimensions(final Dimension dimensions) {
			this.dimensions = dimensions;
		}

		public Geo getWarehouseLocation() {
			return this.warehouseLocation;
		}

		public void setWarehouseLocation(final Geo warehouseLocation) {
			this.warehouseLocation = warehouseLocation;
		}

	}

	static class Dimension {

		@Attributes(required = true)
		private double length;
		@Attributes(required = true)
		private double width;
		@Attributes(required = true)
		private double height;

		public double getLength() {
			return this.length;
		}

		public void setLength(final double length) {
			this.length = length;
		}

		public double getWidth() {
			return this.width;
		}

		public void setWidth(final double width) {
			this.width = width;
		}

		public double getHeight() {
			return this.height;
		}

		public void setHeight(final double height) {
			this.height = height;
		}

	}

	@Attributes($ref = "http://json-schema.org/geo", description = "A geographical coordinate")
	static class Geo {

		private BigDecimal latitude;
		private BigDecimal longitude;

		public BigDecimal getLatitude() {
			return this.latitude;
		}

		public void setLatitude(final BigDecimal latitude) {
			this.latitude = latitude;
		}

		public BigDecimal getLongitude() {
			return this.longitude;
		}

		public void setLongitude(final BigDecimal longitude) {
			this.longitude = longitude;
		}
	}

	@Attributes(title = "Product set")
	static class ProductSet implements Iterable<ComplexProduct> {

		// NOTE: all custom collection types must declare the wrapped collection
		// as the first field.
		private Set<ComplexProduct> products;

		public ProductSet(final Set<ComplexProduct> products) {
			this.products = products;
		}

		@Override
		public Iterator<ComplexProduct> iterator() {
			return this.products.iterator();
		}

	}

}
