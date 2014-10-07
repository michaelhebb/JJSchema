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

import java.util.Collection;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Danilo Reinert
 */

public class ArraySchemaWrapper extends SchemaWrapper {

	final SchemaWrapper itemsSchemaWrapper;

	public ArraySchemaWrapper(final Class<?> type, final Class<?> parametrizedType,
			final Set<ManagedReference> managedReferences, final String relativeId) {
		super(type);

		this.setType("array");

		if (parametrizedType != null) {
			if (!Collection.class.isAssignableFrom(type)) {
				throw new RuntimeException("Cannot instantiate a SchemaWrapper of a"
						+ " non Collection class with a Parametrized Type.");
			}

			if (managedReferences == null) {
				this.itemsSchemaWrapper = SchemaWrapperFactory.createWrapper(parametrizedType);
			} else {
				this.itemsSchemaWrapper =
						SchemaWrapperFactory.createWrapper(parametrizedType, managedReferences,
								relativeId);
			}

			this.setItems(this.itemsSchemaWrapper.asJson());
		} else {
			this.itemsSchemaWrapper = null;
		}
	}

	public ArraySchemaWrapper(final Class<?> type, final Class<?> parametrizedType,
			final Set<ManagedReference> managedReferences) {
		this(type, parametrizedType, managedReferences, null);
	}

	public ArraySchemaWrapper(final Class<?> type, final Class<?> parametrizedType) {
		this(type, parametrizedType, null);
	}

	public ArraySchemaWrapper(final Class<?> type, final RefSchemaWrapper refSchemaWrapper) {
		super(type);

		this.setType("array");
		this.itemsSchemaWrapper = refSchemaWrapper;
		this.setItems(this.itemsSchemaWrapper.asJson());
	}

	public Class<?> getJavaParametrizedType() {
		return this.itemsSchemaWrapper.getJavaType();
	}

	public SchemaWrapper getItemsSchema() {
		return this.itemsSchemaWrapper;
	}

	@Override
	public boolean isArrayWrapper() {
		return true;
	}

	protected JsonNode getItems() {
		return this.getNode().get("items");
	}

	protected void setItems(final JsonNode itemsNode) {
		this.getNode().replace("items", itemsNode);
	}
}
