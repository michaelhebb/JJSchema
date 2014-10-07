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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

/**
 * @author Danilo Reinert
 */
public class CustomSchemaWrapper extends SchemaWrapper implements Iterable<PropertyWrapper> {

	private final List<PropertyWrapper> propertyWrappers;
	private boolean required;
	private final Set<ManagedReference> managedReferences;
	private String relativeId = "#";

	public CustomSchemaWrapper(final Class<?> type) {
		this(type, new HashSet<ManagedReference>());
	}

	public CustomSchemaWrapper(final Class<?> type, final Set<ManagedReference> managedReferences) {
		this(type, managedReferences, null);
	}

	public CustomSchemaWrapper(final Class<?> type, final Set<ManagedReference> managedReferences,
			final String relativeId) {
		super(type);

		this.setType("object");
		this.processNullable();
		this.processAttributes(this.getNode(), type);
		this.propertyWrappers = Lists.newArrayListWithExpectedSize(type.getDeclaredFields().length);
		this.managedReferences = managedReferences;

		if (relativeId != null) {
			this.addTokenToRelativeId(relativeId);
		}

		this.processProperties();
	}

	public String getRelativeId() {
		return this.relativeId;
	}

	protected void addTokenToRelativeId(final String token) {
		if (token.startsWith("#")) {
			this.relativeId = token;
		} else {
			this.relativeId = this.relativeId + "/" + token;
		}
	}

	public void addProperty(final PropertyWrapper propertyWrapper) {
		this.propertyWrappers.add(propertyWrapper);

		if (!this.getNode().has("properties")) {
			this.getNode().putObject("properties");
		}

		((ObjectNode) this.getNode().get("properties")).replace(propertyWrapper.getName(),
				propertyWrapper.asJson());

		if (propertyWrapper.isRequired()) {
			this.addRequired(propertyWrapper.getName());
		}
	}

	public boolean isRequired() {
		return this.required;
	}

	public void addRequired(final String name) {
		if (!this.getNode().has("required")) {
			this.getNode().putArray("required");
		}

		ArrayNode requiredNode = (ArrayNode) this.getNode().get("required");
		requiredNode.add(name);
	}

	public boolean pullReference(final ManagedReference managedReference) {
		if (this.managedReferences.contains(managedReference)) {
			return false;
		}

		this.managedReferences.add(managedReference);
		return true;
	}

	public boolean pushReference(final ManagedReference managedReference) {
		return this.managedReferences.remove(managedReference);
	}

	@Override
	public boolean isCustomWrapper() {
		return true;
	}

	/**
	 * Returns an iterator over a set of elements of PropertyWrapper.
	 *
	 * @return an Iterator.
	 */
	@Override
	public Iterator<PropertyWrapper> iterator() {
		return this.propertyWrappers.iterator();
	}

	protected void processProperties() {
		HashMap<Method, Field> properties = this.findProperties();

		for (Method method : properties.keySet()) {
			PropertyWrapper propertyWrapper =
					new PropertyWrapper(this, this.managedReferences, method,
							properties.get(method));

			if (!propertyWrapper.isEmptyWrapper()) {
				this.addProperty(propertyWrapper);
			}
		}
	}

	private HashMap<Method, Field> findProperties() {
		Field[] fields = this.getJavaType().getDeclaredFields();
		Method[] methods = this.getJavaType().getMethods();

		// Ordering the properties
		Arrays.sort(methods, new Comparator<Method>() {
			@Override
			public int compare(final Method m1, final Method m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});

		LinkedHashMap<Method, Field> props = new LinkedHashMap<Method, Field>();
		// get valid properties (get method and respective field (if exists))
		for (Method method : methods) {
			Class<?> declaringClass = method.getDeclaringClass();

			if (declaringClass.equals(Object.class)
					|| Collection.class.isAssignableFrom(declaringClass)) {
				continue;
			}

			if (isGetter(method)) {
				boolean hasField = false;

				for (Field field : fields) {
					String name = getNameFromGetter(method);
					if (field.getName().equalsIgnoreCase(name)) {
						props.put(method, field);
						hasField = true;
						break;
					}
				}

				if (!hasField) {
					props.put(method, null);
				}
			}
		}

		return props;
	}

	private static boolean isGetter(final Method method) {
		return method.getName().startsWith("get") || method.getName().startsWith("is");
	}

	private static String getNameFromGetter(final Method getter) {
		String[] getterPrefixes = { "get", "is" };
		String methodName = getter.getName();

		String fieldName = null;
		for (String prefix : getterPrefixes) {
			if (methodName.startsWith(prefix)) {
				fieldName = methodName.substring(prefix.length());
			}
		}

		if (fieldName == null) {
			return null;
		}

		return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
	}

	protected void setRequired(final boolean required) {
		this.required = required;
	}

	protected void processAttributes(final ObjectNode node, final Class<?> type) {
		final Attributes attributes = type.getAnnotation(Attributes.class);
		if (attributes != null) {
			if (!attributes.id().isEmpty()) {
				node.put("id", attributes.id());
			}

			if (!attributes.description().isEmpty()) {
				node.put("description", attributes.description());
			}

			if (!attributes.type().isEmpty()) {
				node.put("type", attributes.type());
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
				node.put("minLength", attributes.minItems());
			}

			if (attributes.maxLength() > -1) {
				node.put("maxLength", attributes.maxItems());
			}

			if (attributes.required()) {
				this.setRequired(true);
			}
		}
	}
}
