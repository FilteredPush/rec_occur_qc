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
package org.filteredpush.qc.metadata;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
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

	protected static final String dcTypeLiterals = "Collection,Dataset,Event,Image,InteractiveResource,MovingImage,PhysicalObject,Service,Software,Sound,StillImage,Text";

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#issueDatageneralizationsNotempty(java.lang.String)}.
	 */
	@Test
	public void issueDatageneralizationsNotempty() {
		
		String dataGeneralizations = "foo";
		DQResponse<IssueValue> result = DwCMetadataDQ.issueDatageneralizationsNotempty(dataGeneralizations);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(IssueValue.POTENTIAL_ISSUE.getLabel(), result.getValue().getLabel());
		
		dataGeneralizations="placed on quarter degree grid";  
		result = DwCMetadataDQ.issueDatageneralizationsNotempty(dataGeneralizations);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(IssueValue.POTENTIAL_ISSUE.getLabel(), result.getValue().getLabel());
		
		dataGeneralizations = "";
		result = DwCMetadataDQ.issueDatageneralizationsNotempty(dataGeneralizations);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(IssueValue.NOT_ISSUE.getLabel(), result.getValue().getLabel());
		
		dataGeneralizations = " ";  // space only
		result = DwCMetadataDQ.issueDatageneralizationsNotempty(dataGeneralizations);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(IssueValue.NOT_ISSUE.getLabel(), result.getValue().getLabel());
		
    	dataGeneralizations = null;
    	DQResponse<IssueValue> response = DwCMetadataDQ.issueDatageneralizationsNotempty(dataGeneralizations);
    	assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
    	assertEquals(IssueValue.NOT_ISSUE.getLabel(), response.getValue().getLabel());

    	dataGeneralizations = "Some generalization";
    	response = DwCMetadataDQ.issueDatageneralizationsNotempty(dataGeneralizations);
    	assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
    	assertEquals(IssueValue.POTENTIAL_ISSUE.getLabel(), response.getValue().getLabel());
	}
	
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
	public void validationLicenseStandard() {
		String license = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		license = "";
		result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());	
		
		license = "https://creativecommons.org/licenses/by-sa/4.0/";
		result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		license = "https://creativecommons.org/licenses/by-sa/4.0/deed";
		result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		license = "https://creativecommons.org/licenses/by-sa/4.0/legalcode.es";
		result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		license = "https://creativecommons.org/licenses/by-sa/4.0/deed.zh-hans";
		result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		license = "https://creativecommons.org/publicdomain/zero/1.0/";
		result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		license = "https://creativecommons.org/publicdomain/zero/1.0/deed";
		result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		license = "https://creativecommons.org/publicdomain/zero/1.0/deed.en";
		result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		license = "https://creativecommons.org/publicdomain/zero/1.0/legalcode.es";
		result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		
		license = "CC BY-SA";  // string literal, not appropriate for license.
		result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		license = "CC_BY_SA_4_0"; // GBIF internal string constant
		result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		license = "https://creativecommons.org/licenses/by-sa/4.0/deed.zz";  // not a language code
		result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		license = "https//creativecommons.org/licenses/by-sa/4.0/deed.cy";  // no translation (yet)
		result = DwCMetadataDQDefaults.validationLicenseStandard(license);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#amendmentDctypeStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentDctypeStandardized() {
		
		String dcType = "";
		DQResponse<AmendmentValue> result = DwCMetadataDQ.amendmentDctypeStandardized(dcType);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		assertNotNull(result.getComment());
	
		ArrayList<String> dcTypeLiteralList = new ArrayList<String>(Arrays.asList(dcTypeLiterals.split(",")));
		Iterator<String> i = dcTypeLiteralList.iterator();
		while (i.hasNext()){ 
			dcType=i.next();
		
			result = DwCMetadataDQ.amendmentDctypeStandardized(dcType);
			logger.debug(result.getComment());
			assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
			assertNull(result.getValue());
			assertNotNull(result.getComment());
			
			result = DwCMetadataDQ.amendmentDctypeStandardized(dcType + " ");
			logger.debug(result.getComment());
			assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
			assertEquals(dcType,result.getValue().getObject().get("dc:type"));
			assertNotNull(result.getComment());
			
			result = DwCMetadataDQ.amendmentDctypeStandardized("https://purl.org/dc/dcmitype/" + dcType);
			logger.debug(result.getComment());
			assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
			assertEquals(dcType,result.getValue().getObject().get("dc:type"));
			assertNotNull(result.getComment());
			
			result = DwCMetadataDQ.amendmentDctypeStandardized("http://purl.org/dc/dcmitype/" + dcType);
			logger.debug(result.getComment());
			assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
			assertEquals(dcType,result.getValue().getObject().get("dc:type"));
			assertNotNull(result.getComment());
			
			result = DwCMetadataDQ.amendmentDctypeStandardized(dcType.substring(0, dcType.length()-1) + "l");
			logger.debug(result.getComment());
			assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
			assertEquals(dcType,result.getValue().getObject().get("dc:type"));
			assertNotNull(result.getComment());
		} 
		
		dcType = "evnt";
		result = DwCMetadataDQ.amendmentDctypeStandardized(dcType);
		logger.debug(result.getComment());
		assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		assertEquals("Event",result.getValue().getObject().get("dc:type"));
		assertNotNull(result.getComment());
		
		dcType = "foo";
		result = DwCMetadataDQ.amendmentDctypeStandardized(dcType);
		logger.debug(result.getComment());
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		assertNotNull(result.getComment());
		
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
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord 
        // is EMPTY; AMENDED the value of dwc:basisOfRecord if it could 
        // be unambiguously interpreted as a value in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority default = "Darwin 
        // Core basisOfRecord" {[https://dwc.tdwg.org/terms/#dwc:basisOfRecord]} 
        // {dwc:basisOfRecord vocabulary [https://rs.gbif.org/vocabulary/dwc/basis_of_record.xml]} 
        // 
       	List<String> basisOfRecordLiteralListSpaces = List.of("Dataset","Event","Event Attribute","Event Measurement","Fossil Specimen","Geological Context","Human Observation","Identification","Living Specimen","Location","Machine Observation","Material Citation","Material Sample","Measurement or Fact","Occurrence","Occurrence Measurement","Organism","Preserved Specimen","Resource Relationship","Sample","Sample Attribute","Sampling Event","Sampling Location","Taxon");
        List<String> values = List.of("Dataset","Event","EventAttribute","EventMeasurement","FossilSpecimen","GeologicalContext","HumanObservation","Identification","LivingSpecimen","Location","MachineObservation","MaterialCitation","MaterialSample","MeasurementOrFact","Occurrence","OccurrenceMeasurement","Organism","PreservedSpecimen","ResourceRelationship","Sample","SampleAttribute","SamplingEvent","SamplingLocation","Taxon");
		
		String basisOfRecord = "";
		String sourceAuthority = "Darwin Core basisOfRecord";
		DQResponse<AmendmentValue> result = DwCMetadataDQ.amendmentBasisofrecordStandardized(basisOfRecord,sourceAuthority);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		assertNotNull(result.getComment());
		
		basisOfRecord = "Fossil Specimen";
		result = DwCMetadataDQ.amendmentBasisofrecordStandardized(basisOfRecord,sourceAuthority);
		logger.debug(result.getComment());
		assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		assertNotNull(result.getValue());
		assertEquals("FossilSpecimen", result.getValue().getObject().get("dwc:basisOfRecord"));
		assertNotNull(result.getComment());
		
		Iterator<String> i = values.iterator();
		while (i.hasNext()) { 
			basisOfRecord = i.next();
			result = DwCMetadataDQ.amendmentBasisofrecordStandardized(basisOfRecord,sourceAuthority);
			logger.debug(result.getComment());
			assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
			assertNull(result.getValue());
			assertNotNull(result.getComment());
			
			String testVal = " " + basisOfRecord + ". ";
			result = DwCMetadataDQ.amendmentBasisofrecordStandardized(testVal,sourceAuthority);
			logger.debug(result.getComment());
			assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
			assertNotNull(result.getValue());
			assertEquals(basisOfRecord, result.getValue().getObject().get("dwc:basisOfRecord"));
			assertNotNull(result.getComment());
		}
		
		i = basisOfRecordLiteralListSpaces.iterator();
		while (i.hasNext()) { 
			basisOfRecord = i.next();
			String matchVal = basisOfRecord.replaceAll(" ", "");
			if (matchVal.equals("MeasurementorFact")) { 
				matchVal = "MeasurementOrFact";
			}
		
			String testVal = " " + basisOfRecord + " ";
			result = DwCMetadataDQ.amendmentBasisofrecordStandardized(testVal,sourceAuthority);
			logger.debug(result.getComment());
			assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
			assertNotNull(result.getValue());
			assertEquals(matchVal, result.getValue().getObject().get("dwc:basisOfRecord"));
			assertNotNull(result.getComment());
		}
		 
		basisOfRecord = "French";  // not a basisOfRecord
		result = DwCMetadataDQ.amendmentBasisofrecordStandardized(basisOfRecord,sourceAuthority);
		logger.debug(result.getComment());
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		assertNotNull(result.getComment());
		
		basisOfRecord = "Datasets"; // soundex Dataset
		result = DwCMetadataDQ.amendmentBasisofrecordStandardized(basisOfRecord,sourceAuthority);
		logger.debug(result.getComment());
		assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		assertNotNull(result.getValue());
		assertEquals("Dataset", result.getValue().getObject().get("dwc:basisOfRecord"));
		assertNotNull(result.getComment());
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
		
		String dcType = "PhysicalObject";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationDctypeStandard(dcType);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		ArrayList<String> dcTypeLiteralList = new ArrayList<String>(Arrays.asList(dcTypeLiterals.split(",")));
		Iterator<String> i = dcTypeLiteralList.iterator();
		while (i.hasNext()){ 
			dcType=i.next();
			
			result = DwCMetadataDQ.validationDctypeStandard(dcType);
			logger.debug(result.getComment());
			assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
			assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
			assertNotNull(result.getComment());
			
			dcType = " " + dcType;
			result = DwCMetadataDQ.validationDctypeStandard(dcType);
			logger.debug(result.getComment());
			assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
			assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
			assertNotNull(result.getComment());
			
		}
		
		dcType = "";
		result = DwCMetadataDQ.validationDctypeStandard(dcType);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		assertNotNull(result.getComment());
		
		dcType = "foo";
		result = DwCMetadataDQ.validationDctypeStandard(dcType);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		// correct value for dcterms:type, but not for dctype
		dcType = "http://purl.org/dc/dcmitype/Collection";
		result = DwCMetadataDQ.validationDctypeStandard(dcType);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
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
		assertEquals(IssueValue.POTENTIAL_ISSUE.getLabel(), result.getValue().getLabel());
		
		establishmentMeans = "";
		result = DwCMetadataDQ.issueEstablishmentmeansNotempty(establishmentMeans);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(IssueValue.NOT_ISSUE.getLabel(), result.getValue().getLabel());		
		
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
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		basisOfRecord = "FossilSpecimen";
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

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationPathwayNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationPathwayNotempty() {
		
		String pathway = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationPathwayNotempty(pathway);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		pathway = "";
		result = DwCMetadataDQ.validationPathwayNotempty(pathway);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationDegreeofestablishmentNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationDegreeofestablishmentNotempty() {
		
		String degreeOfEstablishment = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationDegreeofestablishmentNotempty(degreeOfEstablishment);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		degreeOfEstablishment = "";
		result = DwCMetadataDQ.validationDegreeofestablishmentNotempty(degreeOfEstablishment);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationReproductiveconditionNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationReproductiveconditionNotempty() {
		
		String reproductiveCondition = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationReproductiveconditionNotempty(reproductiveCondition);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		reproductiveCondition = "";
		result = DwCMetadataDQ.validationReproductiveconditionNotempty(reproductiveCondition);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationRelationshipofresourceNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationRelationshipofresourceNotempty() {
		
		String relationshipOfResource = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationRelationshipofresourceNotempty(relationshipOfResource);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		relationshipOfResource = "";
		result = DwCMetadataDQ.validationRelationshipofresourceNotempty(relationshipOfResource);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationLifestageNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationLifestageNotempty() {
		
		String lifeStage = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationLifestageNotempty(lifeStage);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		lifeStage = "";
		result = DwCMetadataDQ.validationLifestageNotempty(lifeStage);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationRecordnumberNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationRecordnumberNotempty() {
		
		String recordNumber = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationRecordnumberNotempty(recordNumber);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		recordNumber = "";
		result = DwCMetadataDQ.validationRecordnumberNotempty(recordNumber);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationSexNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationSexNotempty() {
		
		String sex = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationSexNotempty(sex);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		sex = "";
		result = DwCMetadataDQ.validationSexNotempty(sex);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationPreparationsNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationPreparationsNotempty() {
		
		String preparations = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationPreparationsNotempty(preparations);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		preparations = "";
		result = DwCMetadataDQ.validationPreparationsNotempty(preparations);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
	}
	
	
	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationRecordedbyNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationRecordedbyNotempty() {
		
		String recordedBy = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationRecordedbyNotempty(recordedBy);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		recordedBy = "";
		result = DwCMetadataDQ.validationRecordedbyNotempty(recordedBy);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
	}
	
	
	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationIndividualcountNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationIndividualcountNotempty() {
		
		String individualCount = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationIndividualcountNotempty(individualCount);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		individualCount = "1";
		result = DwCMetadataDQ.validationIndividualcountNotempty(individualCount);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		individualCount = "";
		result = DwCMetadataDQ.validationIndividualcountNotempty(individualCount);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
	}
	
	
	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationDispositionNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationDispositionNotempty() {
		
		String disposition = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationDispositionNotempty(disposition);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		disposition = "";
		result = DwCMetadataDQ.validationDispositionNotempty(disposition);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
	}
	

	
	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationLifestageStandard(java.lang.String)}.
	 */
	@Test
	public void testValidationLifestageStandard() {
		String lifeStage = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationLifestageStandard(lifeStage,null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		lifeStage = "";
		result = DwCMetadataDQ.validationLifestageStandard(lifeStage,null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNotNull(result.getComment());
		
		lifeStage = "Larva";
		result = DwCMetadataDQ.validationLifestageStandard(lifeStage,null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.metadata.DwCMetadataDQ#validationPathwayStandard(java.lang.String)}.
	 */
	@Test
	public void validationPathwayStandard() {
		String pathway = "foo";
		DQResponse<ComplianceValue> result = DwCMetadataDQ.validationPathwayStandard(pathway,"GBIF Pathway Vocabulary");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		pathway = "";
		result = DwCMetadataDQ.validationPathwayStandard(pathway,"GBIF Pathway Vocabulary");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());	
		
		pathway = "transportStowaway";
		result = DwCMetadataDQ.validationPathwayStandard(pathway,"GBIF Pathway Vocabulary");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		pathway = "contaminateBait";
		result = DwCMetadataDQ.validationPathwayStandard(pathway,"GBIF Pathway Vocabulary");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		pathway = "contaminantNursery";
		result = DwCMetadataDQ.validationPathwayStandard(pathway,"GBIF Pathway Vocabulary");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		pathway = "otherEscape";
		result = DwCMetadataDQ.validationPathwayStandard(pathway,"GBIF Pathway Vocabulary");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		pathway = "corridorAndDispersal";
		result = DwCMetadataDQ.validationPathwayStandard(pathway,"GBIF Pathway Vocabulary");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
	}
	
}
