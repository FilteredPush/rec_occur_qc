/**
 * DwCMetadataDQDefaults.java
 *
 * Copyright 2022 President and Fellows of Harvard College
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
 *
 */
package org.filteredpush.qc.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Amendment;
import org.datakurator.ffdq.annotations.Parameter;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.annotations.ProvidesVersion;
import org.datakurator.ffdq.annotations.Specification;
import org.datakurator.ffdq.annotations.Validation;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;

/**
 * @author mole
 *
 */
public class DwCMetadataDQDefaults extends DwCMetadataDQ {

	private static final Log logger = LogFactory.getLog(DwCMetadataDQDefaults.class);
	
    /**
    * Propose amendment to the value of dwc:basisOfRecord using bdq:sourceAuthority.
    *
    * Provides: AMENDMENT_BASISOFRECORD_STANDARDIZED
    * Version: 2023-09-18
    *
    * @param basisOfRecord the provided dwc:basisOfRecord to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_BASISOFRECORD_STANDARDIZED", description="Propose amendment to the value of dwc:basisOfRecord using bdq:sourceAuthority.")
    @Provides("07c28ace-561a-476e-a9b9-3d5ad6e35933")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/07c28ace-561a-476e-a9b9-3d5ad6e35933/2023-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord is EMPTY; AMENDED the value of dwc:basisOfRecord if it could be unambiguously interpreted as a value in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority default = 'Darwin Core basisOfRecord' {[https://dwc.tdwg.org/terms/#dwc:basisOfRecord]} {dwc:basisOfRecord vocabulary [https://rs.gbif.org/vocabulary/dwc/basis_of_record.xml]}")
    public static DQResponse<AmendmentValue> amendmentBasisofrecordStandardized(
        @ActedUpon("dwc:basisOfRecord") String basisOfRecord
    ) {
    	return amendmentBasisofrecordStandardized(basisOfRecord,null);
    	
    }

    /**
     * Does the value of dwc:basisOfRecord occur in bdq:sourceAuthority?
     *
     * Provides: #104 VALIDATION_BASISOFRECORD_STANDARD
     *
     * @param basisOfRecord the provided dwc:basisOfRecord to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_BASISOFRECORD_STANDARD", description="Does the value of dwc:basisOfRecord occur in bdq:sourceAuthority?")
    @Provides("42408a00-bf71-4892-a399-4325e2bc1fb8")
    public static DQResponse<ComplianceValue> validationBasisofrecordStandard(@ActedUpon("dwc:basisOfRecord") String basisOfRecord) {
    	return validationBasisofrecordStandard(basisOfRecord, null);
    }
	
    /**
     * Does the value of dwc:occurrenceStatus occur in bdq:sourceAuthority?
     *
     * Provides: VALIDATION_OCCURRENCESTATUS_STANDARD
     *
     * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_OCCURRENCESTATUS_STANDARD", description="Does the value of dwc:occurrenceStatus occur in bdq:sourceAuthority?")
    @Provides("7af25f1e-a4e2-4ff4-b161-d1f25a5c3e47")
    public static DQResponse<ComplianceValue> validationOccurrencestatusStandard(
    		@ActedUpon("dwc:occurrenceStatus") String occurrenceStatus) {
    	return validationOccurrencestatusStandard(occurrenceStatus, null);
    }
    
    /**
    * Does the value of dcterms:license occur in bdq:sourceAuthority?
    *
    * Provides: VALIDATION_LICENSE_STANDARD
    * Version: 2023-09-17
    *
    * @param license the provided dcterms:license to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_LICENSE_STANDARD", description="Does the value of dcterms:license occur in bdq:sourceAuthority?")
    @Provides("3136236e-04b6-49ea-8b34-a65f25e3aba1")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/3136236e-04b6-49ea-8b34-a65f25e3aba1/2023-09-17")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dcterms:license is EMPTY; COMPLIANT if the value of the term dcterms:license is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority default = 'Creative Commons' {[https://creativecommons.org/]} {Creative Commons licenses [https://creativecommons.org/about/cclicenses/]}")
    public static DQResponse<ComplianceValue> validationLicenseStandard(
        @ActedUpon("dcterms:license") String license
    ) {
    	return validationLicenseStandard(license,null);
    }
   
}
