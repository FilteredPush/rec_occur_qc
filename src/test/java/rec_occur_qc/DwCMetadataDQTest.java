/**
 * DwCMetadataDQTest.java
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
package rec_occur_qc;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.api.result.IssueValue;
import org.datakurator.ffdq.model.ResultState;
import org.filteredpush.qc.metadata.DwCMetadataDQ;
import org.filteredpush.qc.metadata.DwCMetadataDQDefaults;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class DwCMetadataDQTest {

	private static final Log logger = LogFactory.getLog(DwCMetadataDQTest.class);

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationOccurrenceidStandard(java.lang.String)}.
	 */
	@Test
	public void testValidationOccurrenceidStandard() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationLicenseStandard(java.lang.String)}.
	 */
	@Test
	public void testValidationLicenseStandard() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#amendmentDctypeStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentDctypeStandardized() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationOccurrenceidNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationOccurrenceidNotempty() {
		
        // Specification
        // COMPLIANT if dwc:occurrenceID is not EMPTY; otherwise NOT_COMPLIANT 
        // 
		
		String occurrenceId = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationOccurrenceidNotempty(occurrenceId);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		occurrenceId = "";
		result = DwCMetadataDQ.validationOccurrenceidNotempty(occurrenceId);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationBasisofrecordNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationBasisofrecordNotempty() {
		
        // Specification
        // COMPLIANT if dwc:basisOfRecord is not EMPTY; otherwise NOT_COMPLIANT 
        // 
		
		String basisOfRecord = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationBasisofrecordNotempty(basisOfRecord);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		basisOfRecord = "";
		result = DwCMetadataDQ.validationBasisofrecordNotempty(basisOfRecord);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#amendmentBasisofrecordStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentBasisofrecordStandardized() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#amendmentOccurrencestatusAssumeddefault(java.lang.String)}.
	 */
	@Test
	public void testAmendmentOccurrencestatusAssumeddefault() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationDctypeStandard(java.lang.String)}.
	 */
	@Test
	public void testValidationDctypeStandard() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#issueEstablishmentmeansNotempty(java.lang.String)}.
	 */
	@Test
	public void testIssueEstablishmentmeansNotempty() {
		
        // Specification
        // POTENTIAL_ISSUE if dwc:establishmentMeans is not EMPTY; 
        // otherwise NOT_ISSUE 
		
		String establishmentMeans = "foo";
		DQResponse<IssueValue> result = DwCMetadataDQ.issueEstablishmentmeansNotempty(establishmentMeans);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(IssueValue.POTENTIAL_PROBLEM.getLabel(), result.getValue().getLabel());
		
		establishmentMeans = "";
		result = DwCMetadataDQ.issueEstablishmentmeansNotempty(establishmentMeans);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(IssueValue.NOT_PROBLEM.getLabel(), result.getValue().getLabel());		
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationLicenseNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationLicenseNotempty() {
		
        // Specification
        // COMPLIANT if dcterms:license is not EMPTY; otherwise NOT_COMPLIANT 
        // 
		String license = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationLicenseNotempty(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		license = "";
		result = DwCMetadataDQ.validationLicenseNotempty(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationDctypeNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationDctypeNotempty() {
		
        // Specification
        // COMPLIANT if dc:type is not EMPTY; otherwise NOT_COMPLIANT 
        // 
		
		String dcType = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationDctypeNotempty(dcType);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		dcType = "";
		result = DwCMetadataDQ.validationDctypeNotempty(dcType);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationBasisofrecordStandard(java.lang.String)}.
	 */
	@Test
	public void testValidationBasisofrecordStandard() {
		
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord 
        // is EMPTY; COMPLIANT if the value of dwc:basisOfRecord is 
        // valid using the bdq:sourceAuthority; otherwise NOT_COMPLIANT 
		
		String basisOfRecord = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQDefaults.validationBasisofrecordStandard(basisOfRecord);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		basisOfRecord = "";
		result = DwCMetadataDQDefaults.validationBasisofrecordStandard(basisOfRecord);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());	
		
		basisOfRecord = "Fossil Specimen";
		result = DwCMetadataDQDefaults.validationBasisofrecordStandard(basisOfRecord);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#amendmentOccurrencestatusStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentOccurrencestatusStandardized() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationOccurrencestatusStandard(java.lang.String)}.
	 */
	@Test
	public void testValidationOccurrencestatusStandard() {
		
        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:occurrenceStatus 
        // is EMPTY; COMPLIANT if the value of dwc:occurrenceStatus 
		
		String occurrenceStatus = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQDefaults.validationOccurrencestatusStandard(occurrenceStatus);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		occurrenceStatus = "present";
		result = DwCMetadataDQDefaults.validationOccurrencestatusStandard(occurrenceStatus);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		occurrenceStatus = "absent";
		result = DwCMetadataDQDefaults.validationOccurrencestatusStandard(occurrenceStatus);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		occurrenceStatus = "Present";
		result = DwCMetadataDQDefaults.validationOccurrencestatusStandard(occurrenceStatus);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		occurrenceStatus = "";
		result = DwCMetadataDQDefaults.validationOccurrencestatusStandard(occurrenceStatus);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());	
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationOccurrencestatusNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationOccurrencestatusNotempty() {
		
        // Specification
        // COMPLIANT if dwc:occurrenceStatus is not EMPTY; otherwise 
        // NOT_COMPLIANT
		
		String occurrenceStatus = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationOccurrencestatusNotempty(occurrenceStatus);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		occurrenceStatus = "";
		result = DwCMetadataDQ.validationOccurrencestatusNotempty(occurrenceStatus);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#amendmentLicenseStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentLicenseStandardized() {
		fail("Not yet implemented");
	}

}
