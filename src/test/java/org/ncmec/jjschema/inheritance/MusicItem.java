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

import java.math.BigDecimal;

public class MusicItem extends BaseItem {

	private BigDecimal price;
	private String artistName;
	private String releaseYear;

	public String getArtistName() {
		return this.artistName;
	}

	public void setArtistName(final String artistName) {
		this.artistName = artistName;
	}

	public String getReleaseYear() {
		return this.releaseYear;
	}

	public void setReleaseYear(final String releaseYear) {
		this.releaseYear = releaseYear;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(final BigDecimal price) {
		this.price = price;
	}

}
