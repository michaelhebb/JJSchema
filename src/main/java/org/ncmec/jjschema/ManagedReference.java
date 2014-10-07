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

public class ManagedReference {

	Class<?> collectionType;
	final Class<?> type;
	final String name;
	final Class<?> backwardType;

	public ManagedReference(final Class<?> type, final String name, final Class<?> backwardType) {
		this.type = type;
		this.name = name;
		this.backwardType = backwardType;
	}

	public ManagedReference(final Class<?> collectionType, final Class<?> type, final String name,
			final Class<?> backwardType) {
		this.collectionType = collectionType;
		this.type = type;
		this.name = name;
		this.backwardType = backwardType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
				(prime * result)
						+ ((this.collectionType == null) ? 0 : this.collectionType.getName()
								.hashCode());
		result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
		result = (prime * result) + ((this.type == null) ? 0 : this.type.getName().hashCode());
		result =
				(prime * result)
						+ ((this.backwardType == null) ? 0 : this.backwardType.getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		ManagedReference other = (ManagedReference) obj;
		if (this.collectionType == null) {
			if (other.collectionType != null) {
				return false;
			}
		} else if (!this.collectionType.getName().equals(other.collectionType.getName())) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!this.type.getName().equals(other.type.getName())) {
			return false;
		}
		if (this.backwardType == null) {
			if (other.backwardType != null) {
				return false;
			}
		} else if (!this.backwardType.getName().equals(other.backwardType.getName())) {
			return false;
		}
		return true;
	}

}
