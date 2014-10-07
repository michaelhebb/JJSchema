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

import java.util.AbstractCollection;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Danilo Reinert
 */

public class SchemaWrapperFactory {

	public static ObjectMapper MAPPER = new ObjectMapper();

	public static SchemaWrapper createWrapper(final Class<?> type) {
		return createWrapper(type, null);
	}

	public static SchemaWrapper createArrayWrapper(final Class<?> type,
			final Class<?> parametrizedType) {
		return new ArraySchemaWrapper(type, parametrizedType);
	}

	public static SchemaWrapper createWrapper(final Class<?> type,
			final Set<ManagedReference> managedReferences) {
		return createWrapper(type, managedReferences, null);
	}

	public static SchemaWrapper createWrapper(final Class<?> type,
			final Set<ManagedReference> managedReferences, final String relativeId) {
		// If it is void then return null
		if ((type == Void.class) || (type == void.class) || (type == null)) {
			return new NullSchemaWrapper(type);
		}
		// If it is a simple type, then just put the type
		else if (SimpleTypeMappings.isSimpleType(type)) {
			return new SimpleSchemaWrapper(type);
		}
		// If it is an Enum than process like enum
		else if (type.isEnum()) {
			return new EnumSchemaWrapper(type);
		}
		// If none of the above possibilities were true, then it is a custom object
		else {
			if (managedReferences != null) {
				if (relativeId != null) {
					return new CustomSchemaWrapper(type, managedReferences, relativeId);
				}
				return new CustomSchemaWrapper(type, managedReferences);
			}
			return new CustomSchemaWrapper(type);
		}
	}

	public static SchemaWrapper createArrayWrapper(final Class<?> type,
			final Class<?> parametrizedType, final Set<ManagedReference> managedReferences) {
		return new ArraySchemaWrapper(type, parametrizedType, managedReferences);
	}

	public static SchemaWrapper createArrayWrapper(final Class<?> type,
			final Class<?> parametrizedType, final Set<ManagedReference> managedReferences,
			final String relativeId) {
		return new ArraySchemaWrapper(type, parametrizedType, managedReferences, relativeId);
	}

	public static SchemaWrapper createArrayRefWrapper(final RefSchemaWrapper refSchemaWrapper) {
		return new ArraySchemaWrapper(AbstractCollection.class, refSchemaWrapper);
	}
}
