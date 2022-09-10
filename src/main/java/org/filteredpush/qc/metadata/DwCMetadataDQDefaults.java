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
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.annotations.Validation;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.ComplianceValue;

/**
 * @author mole
 *
 */
public class DwCMetadataDQDefaults extends DwCMetadataDQ {

	private static final Log logger = LogFactory.getLog(DwCMetadataDQDefaults.class);

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
}
