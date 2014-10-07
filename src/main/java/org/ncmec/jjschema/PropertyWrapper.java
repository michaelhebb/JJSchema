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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Danilo Reinert
 */
public class PropertyWrapper extends SchemaWrapper {

	enum ReferenceType {
		NONE, FORWARD, BACKWARD
	}

	final CustomSchemaWrapper ownerSchemaWrapper;
	final SchemaWrapper schemaWrapper;
	final Field field;
	final Method method;
	String name;
	boolean required;
	ManagedReference managedReference;
	ReferenceType referenceType;

	public PropertyWrapper(final CustomSchemaWrapper ownerSchemaWrapper,
			final Set<ManagedReference> managedReferences, final Method method, final Field field) {
		super(null);

		if (method == null) {
			throw new RuntimeException("Error at " + ownerSchemaWrapper.getJavaType().getName()
					+ ": Cannot instantiate a PropertyWrapper with a null method.");
		}

		this.ownerSchemaWrapper = ownerSchemaWrapper;
		this.field = field;
		this.method = method;

		String relativeId;

		Class<?> propertyType = method.getReturnType();
		Class<?> collectionType = null;
		final String propertiesStr = "/properties/";
		String itemsStr = "/items";

		if (Collection.class.isAssignableFrom(propertyType)) {
			collectionType = method.getReturnType();
			ParameterizedType genericType = (ParameterizedType) method.getGenericReturnType();
			propertyType = (Class<?>) genericType.getActualTypeArguments()[0];

			relativeId = propertiesStr + this.getName() + itemsStr;
		} else {
			relativeId = propertiesStr + this.getName();
		}

		this.processReference(propertyType);

		if (this.getAccessibleObject().getAnnotation(SchemaIgnore.class) != null) {
			this.schemaWrapper = new EmptySchemaWrapper();
		} else if (this.getReferenceType() == ReferenceType.BACKWARD) {
			SchemaWrapper sw;
			String id = processId(method.getReturnType());

			if (id != null) {
				sw = new RefSchemaWrapper(propertyType, id);
				ownerSchemaWrapper.pushReference(this.getManagedReference());
			} else {
				if (ownerSchemaWrapper.pushReference(this.getManagedReference())) {
					String relativeId1 = ownerSchemaWrapper.getRelativeId();
					if (relativeId1.endsWith(itemsStr)) {
						relativeId1 =
								relativeId1.substring(
										0,
										relativeId1.substring(0,
												relativeId1.length() - itemsStr.length())
												.lastIndexOf("/")
												- (propertiesStr.length() - 1));
					} else {
						relativeId1 =
								relativeId1.substring(0, relativeId1.lastIndexOf("/")
										- (propertiesStr.length() - 1));
					}
					sw = new RefSchemaWrapper(propertyType, relativeId1);
				} else {
					sw = new EmptySchemaWrapper();
				}
			}

			if (sw.isRefWrapper() && (collectionType != null)) {
				this.schemaWrapper =
						SchemaWrapperFactory.createArrayRefWrapper((RefSchemaWrapper) sw);
			} else {
				this.schemaWrapper = sw;
			}
		} else if (ownerSchemaWrapper.getJavaType() == propertyType) {
			SchemaWrapper sw =
					new RefSchemaWrapper(propertyType, ownerSchemaWrapper.getRelativeId());

			if (collectionType != null) {
				this.schemaWrapper =
						SchemaWrapperFactory.createArrayRefWrapper((RefSchemaWrapper) sw);
			} else {
				this.schemaWrapper = sw;
			}
		} else {
			if (this.getReferenceType() == ReferenceType.FORWARD) {
				ownerSchemaWrapper.pullReference(this.getManagedReference());
			}

			String relativeId1 = ownerSchemaWrapper.getRelativeId() + relativeId;
			if (collectionType != null) {
				this.schemaWrapper =
						SchemaWrapperFactory.createArrayWrapper(collectionType, propertyType,
								managedReferences, relativeId1);
			} else {
				this.schemaWrapper =
						SchemaWrapperFactory.createWrapper(propertyType, managedReferences,
								relativeId1);
			}

			this.processAttributes(this.getNode(), this.getAccessibleObject());
			this.processNullable();
		}
	}

	public Field getField() {
		return this.field;
	}

	public Method getMethod() {
		return this.method;
	}

	public SchemaWrapper getOwnerSchema() {
		return this.ownerSchemaWrapper;
	}

	public String getName() {
		if (this.name == null) {
			this.name = this.processPropertyName();
		}
		return this.name;
	}

	public boolean isRequired() {
		return this.required;
	}

	public ManagedReference getManagedReference() {
		return this.managedReference;
	}

	public ReferenceType getReferenceType() {
		return this.referenceType;
	}

	public boolean isReference() {
		return this.managedReference != null;
	}

	@Override
	public JsonNode asJson() {
		return this.schemaWrapper.asJson();
	}

	@Override
	public String getDollarSchema() {
		return this.schemaWrapper.getDollarSchema();
	}

	@Override
	public String getId() {
		return this.schemaWrapper.getId();
	}

	@Override
	public String getRef() {
		return this.schemaWrapper.getRef();
	}

	@Override
	public String getType() {
		return this.schemaWrapper.getType();
	}

	@Override
	public Class<?> getJavaType() {
		return this.schemaWrapper.getJavaType();
	}

	@Override
	public boolean isEnumWrapper() {
		return this.schemaWrapper.isEnumWrapper();
	}

	@Override
	public boolean isSimpleWrapper() {
		return this.schemaWrapper.isSimpleWrapper();
	}

	@Override
	public boolean isCustomWrapper() {
		return this.schemaWrapper.isCustomWrapper();
	}

	@Override
	public boolean isRefWrapper() {
		return this.schemaWrapper.isRefWrapper();
	}

	@Override
	public boolean isArrayWrapper() {
		return this.schemaWrapper.isArrayWrapper();
	}

	@Override
	public boolean isNullWrapper() {
		return this.schemaWrapper.isNullWrapper();
	}

	@Override
	public boolean isEmptyWrapper() {
		return this.schemaWrapper.isEmptyWrapper();
	}

	@Override
	public <T extends SchemaWrapper> T cast() {
		return this.schemaWrapper.cast();
	}

	protected void setRequired(final boolean required) {
		this.required = required;
	}

	protected AccessibleObject getAccessibleObject() {
		return (this.field == null) ? this.method : this.field;
	}

	protected static String processId(final Class<?> accessibleObject) {
		final Attributes attributes = accessibleObject.getAnnotation(Attributes.class);

		if (attributes != null) {
			if (!attributes.id().isEmpty()) {
				return attributes.id();
			}
		}

		return null;
	}

	protected void processAttributes(final ObjectNode node, final AccessibleObject accessibleObject) {
		final Attributes attributes = accessibleObject.getAnnotation(Attributes.class);

		if (attributes != null) {
			node.remove("$schema");

			if (!attributes.id().isEmpty()) {
				node.put("id", attributes.id());
			}

			if (!attributes.description().isEmpty()) {
				node.put("description", attributes.description());
			}

			if (!attributes.pattern().isEmpty()) {
				node.put("pattern", attributes.pattern());
			}

			if (!attributes.title().isEmpty()) {
				node.put("title", attributes.title());
			}

			if (attributes.maximum() > -1) {
				node.put("maximum", attributes.maximum());
			}

			if (attributes.exclusiveMaximum()) {
				node.put("exclusiveMaximum", true);
			}

			if (attributes.minimum() > -1) {
				node.put("minimum", attributes.minimum());
			}

			if (attributes.exclusiveMinimum()) {
				node.put("exclusiveMinimum", true);
			}

			if (attributes.enums().length > 0) {
				ArrayNode enumArray = node.putArray("enum");
				String[] enums = attributes.enums();
				for (String v : enums) {
					enumArray.add(v);
				}
			}

			if (attributes.uniqueItems()) {
				node.put("uniqueItems", true);
			}

			if (attributes.minItems() > 0) {
				node.put("minItems", attributes.minItems());
			}

			if (attributes.maxItems() > -1) {
				node.put("maxItems", attributes.maxItems());
			}

			if (attributes.multipleOf() > 0) {
				node.put("multipleOf", attributes.multipleOf());
			}

			if (attributes.minLength() > 0) {
				node.put("minLength", attributes.minLength());
			}

			if (attributes.maxLength() > -1) {
				node.put("maxLength", attributes.maxLength());
			}

			if (attributes.required()) {
				this.setRequired(true);
			}
		}
	}

	protected void processReference(final Class<?> propertyType) {
		boolean referenceExists = false;

		JsonManagedReference refAnn =
				this.getAccessibleObject().getAnnotation(JsonManagedReference.class);

		if (refAnn != null) {
			referenceExists = true;
			this.managedReference =
					new ManagedReference(this.getOwnerSchema().getJavaType(), refAnn.value(),
							propertyType);
			this.referenceType = ReferenceType.FORWARD;
		}

		JsonBackReference backRefAnn =
				this.getAccessibleObject().getAnnotation(JsonBackReference.class);

		if (backRefAnn != null) {

			if (referenceExists) {
				throw new RuntimeException("Error at "
						+ this.getOwnerSchema().getJavaType().getName() + ": Cannot reference "
						+ propertyType.getName() + " both as Managed and Back Reference.");
			}

			this.managedReference =
					new ManagedReference(propertyType, backRefAnn.value(), this.getOwnerSchema()
							.getJavaType());

			this.referenceType = ReferenceType.BACKWARD;
		}
	}

	@Override
	protected ObjectNode getNode() {
		return this.schemaWrapper.getNode();
	}

	@Override
	protected void processNullable() {
		final Nullable nullable = this.getAccessibleObject().getAnnotation(Nullable.class);

		if (nullable != null) {

			if (this.isEnumWrapper()) {
				((ArrayNode) this.getNode().get("enum")).add("null");
			} else {
				String oldType = this.getType();
				ArrayNode typeArray = this.getNode().putArray("type");
				typeArray.add(oldType);
				typeArray.add("null");
			}
		}
	}

	@Override
	protected String getNodeTextValue(final JsonNode node) {
		return this.schemaWrapper.getNodeTextValue(node);
	}

	@Override
	protected void setType(final String type) {
		this.schemaWrapper.setType(type);
	}

	private String processPropertyName() {
		return (this.field == null) ? firstToLowerCase(this.method.getName().replace("get", ""))
				: this.field.getName();
	}

	private static String firstToLowerCase(final String string) {
		return Character.toLowerCase(string.charAt(0))
				+ (string.length() > 1 ? string.substring(1) : "");
	}
}
