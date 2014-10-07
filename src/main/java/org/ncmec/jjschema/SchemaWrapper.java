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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.SchemaVersion;

/**
 * @author Danilo Reinert
 */
public abstract class SchemaWrapper {
	private final Class<?> type;
	private final ObjectNode node = SchemaWrapperFactory.MAPPER.createObjectNode();

	public SchemaWrapper(final Class<?> type) {
		this.type = type;
	}

	public JsonNode asJson() {
		return this.node;
	}

	public String getDollarSchema() {
		return this.getNodeTextValue(this.node.get("$schema"));
	}

	public SchemaWrapper putDollarSchema() {
		this.node.put("$schema", SchemaVersion.DRAFTV4.getLocation().toString());
		return this;
	}

	public String getId() {
		return this.getNodeTextValue(this.node.get("id"));
	}

	public String getRef() {
		return this.getNodeTextValue(this.node.get("$ref"));
	}

	public String getType() {
		return this.getNodeTextValue(this.node.get("type"));
	}

	public Class<?> getJavaType() {
		return this.type;
	}

	@SuppressWarnings("static-method")
	public boolean isEnumWrapper() {
		return false;
	}

	@SuppressWarnings("static-method")
	public boolean isSimpleWrapper() {
		return false;
	}

	@SuppressWarnings("static-method")
	public boolean isCustomWrapper() {
		return false;
	}

	@SuppressWarnings("static-method")
	public boolean isRefWrapper() {
		return false;
	}

	@SuppressWarnings("static-method")
	public boolean isArrayWrapper() {
		return false;
	}

	@SuppressWarnings("static-method")
	public boolean isEmptyWrapper() {
		return false;
	}

	@SuppressWarnings("static-method")
	public boolean isNullWrapper() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T extends SchemaWrapper> T cast() {
		return (T) this;
	}

	protected ObjectNode getNode() {
		return this.node;
	}

	// TODO: Shouldn't I check the Nullable annotation only on fields or methods?
	protected void processNullable() {
		final Nullable nullable = this.type.getAnnotation(Nullable.class);
		if (nullable != null) {
			String oldType = this.node.get("type").asText();
			ArrayNode typeArray = this.node.putArray("type");
			typeArray.add(oldType);
			typeArray.add("null");
		}
	}

	@SuppressWarnings("static-method")
	protected String getNodeTextValue(final JsonNode jsonNode) {
		return jsonNode == null ? null : jsonNode.textValue();
	}

	protected void setType(final String type) {
		this.node.put("type", type);
	}
}
