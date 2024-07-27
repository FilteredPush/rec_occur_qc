/** 
 * MetadataSourceAuthority.java 
 * 
 * Copyright 2024 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.filteredpush.qc.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Identify source authorities for geospatial data, handling both specific services
 * and services which can take some form of dataset identifier to add to queries.
 *
 * @author mole
 */
public class MetadataSourceAuthority {
	
	private EnumMetadataSourceAuthority authority;
	private String authoritySubDataset;
	
	private static final Log logger = LogFactory.getLog(MetadataSourceAuthority.class);
	
	/**
	 * Create a MetadataSourceAuthority with a default value.
	 */
	public MetadataSourceAuthority() { 
		authority = EnumMetadataSourceAuthority.DWC_BASISOFRECORD;
		updateDefaultSubAuthorities();
	}
	
	/**
	 * Construct a scientific name source authority descriptor where additional information on a sub data set
	 * is not needed.
	 *
	 * @param authority the authority
	 * @throws org.filteredpush.qc.metadata.SourceAuthorityException if the authority specified requires a sub data set specification
	 */
	public MetadataSourceAuthority(EnumMetadataSourceAuthority authority) throws SourceAuthorityException { 
		this.authority = authority;
		authoritySubDataset = null;
		updateDefaultSubAuthorities();
	}
	
	/**
	 * Utility constructor to construct a metadata source authority from a string instead of the enum.
	 *
	 * @param authorityString a value matching the name of an item in EnumMetadataSourceAuthority
	 * @throws org.filteredpush.qc.metadata.SourceAuthorityException
	 *   if the string is not matched to the enumeration, or if the specified
	 *   source authority requires the specification of an authoritySubDataset.
	 */
	public MetadataSourceAuthority(String authorityString) throws SourceAuthorityException {
		logger.debug(authorityString);
		if (authorityString==null) { authorityString = ""; }
	    if (authorityString.toUpperCase().equals("DARWIN CORE BASISOFRECORD")) {
	    	this.authority = EnumMetadataSourceAuthority.DWC_BASISOFRECORD;	
	    	
	    } else if (authorityString.toUpperCase().equals("GBIF LIFESTAGE VOCABULARY")) { 
	    	this.authority = EnumMetadataSourceAuthority.GBIF_LIFESTAGE;	
	    } else if (authorityString.equals("https://api.gbif.org/v1/vocabularies/LifeStage")) { 
	    	this.authority = EnumMetadataSourceAuthority.GBIF_LIFESTAGE;
	    	
	    } else if (authorityString.toUpperCase().equals("GBIF PATHWAY VOCABULARY")) { 
	    	this.authority = EnumMetadataSourceAuthority.GBIF_PATHWAY;	
	    } else if (authorityString.equals("https://api.gbif.org/v1/vocabularies/Pathway")) { 
	    	this.authority = EnumMetadataSourceAuthority.GBIF_PATHWAY;
	    	
	    } else if (authorityString.toUpperCase().equals("GBIF TYPESTATUS VOCABULARY")) { 
	    	this.authority = EnumMetadataSourceAuthority.GBIF_TYPESTATUS;	
	    } else if (authorityString.equals("https://api.gbif.org/v1/vocabularies/TypeStatus")) { 
	    	this.authority = EnumMetadataSourceAuthority.GBIF_TYPESTATUS;	
	    	
	    } else if (authorityString.toUpperCase().equals("GBIF SEX VOCABULARY")) { 
	    	this.authority = EnumMetadataSourceAuthority.GBIF_SEX;	
	    } else if (authorityString.equals("https://api.gbif.org/v1/vocabularies/Sex")) { 
	    	this.authority = EnumMetadataSourceAuthority.GBIF_SEX;		
	    	
	    } else if (authorityString.toUpperCase().equals("GBIF DEGREEOFESTABLISHMENT VOCABULARY")) { 
	    	this.authority = EnumMetadataSourceAuthority.GBIF_DEGREEOFESTABLISHMENT;	
	    } else if (authorityString.equals("https://api.gbif.org/v1/vocabularies/DegreeOfEstablishment")) { 
	    	this.authority = EnumMetadataSourceAuthority.GBIF_DEGREEOFESTABLISHMENT;	
	    } else if (authorityString.toUpperCase().equals("DEGREE OF ESTABLISHMENT CONTROLLED VOCABULARY LIST OF TERMS")) { 
	    	// TODO: This should point at the TDWG vocabulary
	    	this.authority = EnumMetadataSourceAuthority.GBIF_DEGREEOFESTABLISHMENT;	
	    	
	    } else if (authorityString.toUpperCase().startsWith("HTTPS://INVALID/")) { 
	    	this.authority = EnumMetadataSourceAuthority.INVALID;	
	    } else { 
	    	throw new SourceAuthorityException("Unable to construct a SourceAuthority from string [" + authorityString + "]");
	    }
		authoritySubDataset = null;
		updateDefaultSubAuthorities();
	}
	
	/**
	 * Construct a scientific name source authority descriptor.
	 *
	 * @param authority the authority to use
	 * @param authoritySubDataset the specific authority to use.
	 */
	public MetadataSourceAuthority(EnumMetadataSourceAuthority authority, String authoritySubDataset) {
		this.authority = authority;
		this.authoritySubDataset = authoritySubDataset;
		updateDefaultSubAuthorities();
	}
	
	/**
	 * <p>Getter for the field <code>authority</code>.</p>
	 *
	 * @return a {@link org.filteredpush.qc.metadata.EnumMetadataSourceAuthority} object.
	 */
	public EnumMetadataSourceAuthority getAuthority() {
		return authority;
	}

	/**
	 * <p>Getter for the field <code>authoritySubDataset</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAuthoritySubDataset() {
		return authoritySubDataset;
	}	
	
	/**
	 * For those authorities which have sub datasets (none specified yet)
	 * Set the authoritySubDataset to the correct value for the specified authority
	 */
	private void updateDefaultSubAuthorities() { 
		switch (this.authority) {
		default:
			// don't overwrite a specified sub authority/
			break;
		}
	}

	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		
		return authority.getName();
	}

}
