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

package org.ncmec.jjschema.inheritance;

// TODO: Extend BaseItem to demonstrate the problem
public class WarrantyItem extends BaseItem {

	private String type;
	private long contractTermInMonths = 60;
	private boolean termsAndConditionsAccepted;

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public boolean isTermsAndConditionsAccepted() {
		return this.termsAndConditionsAccepted;
	}

	public void setTermsAndConditionsAccepted(final boolean termsAndConditionsAccepted) {
		this.termsAndConditionsAccepted = termsAndConditionsAccepted;
	}

	public long getContractTermInMonths() {
		return this.contractTermInMonths;
	}

	public void setContractTermInMonths(final long contractTermInMonths) {
		this.contractTermInMonths = contractTermInMonths;
	}

}