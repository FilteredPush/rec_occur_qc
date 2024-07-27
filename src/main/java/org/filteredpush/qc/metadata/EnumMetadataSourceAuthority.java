/** 
 * EnumMetadataSourceAuthority.java 
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

/**
 * A list of source authorities for which implementations exist in this package.
 *
 * @author mole
 */
public enum EnumMetadataSourceAuthority {

	/**
	 * GBIF LifeStage vocabulary
	 */
	GBIF_LIFESTAGE,
	/**
	 * GBIF Pathway vocabulary
	 */
	GBIF_PATHWAY,
	/**
	 * GBIF Type Status vocabulary
	 */
	GBIF_TYPESTATUS,
	/**
	 * GBIF Sex vocabulary
	 */
	GBIF_SEX,
	/**
	 * GBIF Degree of Establishment Vocabulary
	 */
	GBIF_DEGREEOFESTABLISHMENT,
	/**
	 * Darwin Core Class Names
	 */
    DWC_BASISOFRECORD,
	/**
	 * Invalid Source Authority
	 */
    INVALID;

	/**
	 * getName get the name of the enumerated object.
	 *
	 * @return the name of the object.
	 */
	public String getName() {
		// return the exact name of the enum instance.
		return name();
	}
	
}
