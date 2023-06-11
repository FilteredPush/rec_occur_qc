/**
 * DwCMetadataDQ.java
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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Amendment;
import org.datakurator.ffdq.annotations.Issue;
import org.datakurator.ffdq.annotations.Mechanism;
import org.datakurator.ffdq.annotations.Parameter;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.annotations.ProvidesVersion;
import org.datakurator.ffdq.annotations.Validation;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.api.result.IssueValue;
import org.datakurator.ffdq.model.ResultState;

/**
 * Provides implementation of TDWG BDQ TG2 OTHER tests (related to metadata found in Record-level
 * terms and Occurrence terms).
 * 
 * #72	13d5a10e-188e-40fd-a22c-dbaa87b91df2	ISSUE_DATAGENERALIZATIONS_NOTEMPTY 
 * #94	acc8dff2-d8d1-483a-946d-65a02a452700	ISSUE_ESTABLISHMENTMEANS_NOTEMPTY
 * #58	ac2b7648-d5f9-48ca-9b07-8ad5879a2536	VALIDATION_BASISOFRECORD_NOTEMPTY
 * #103	374b091a-fc90-4791-91e5-c1557c649169	VALIDATION_DCTYPE_NOTEMPTY
 * #99	15f78619-811a-4c6f-997a-a4c7888ad849	VALIDATION_LICENSE_NOTEMPTY
 * #47	c486546c-e6e5-48a7-b286-eba7f5ca56c4	VALIDATION_OCCURRENCEID_NOTEMPTY
 * #117	eb4a17f6-6bea-4cdd-93dd-d5a7e9d1eccf	VALIDATION_OCCURRENCESTATUS_NOTEMPTY
 * #104 42408a00-bf71-4892-a399-4325e2bc1fb8	VALIDATION_BASISOFRECORD_STANDARD 
 * #116	7af25f1e-a4e2-4ff4-b161-d1f25a5c3e47	VALIDATION_OCCURRENCESTATUS_STANDARD
 * 
 * 
 * TODO: Implement:
 * 
 * 29	fecaa8a3-bbd8-4c5a-a424-13c37c4bb7b1	ISSUE_ANNOTATION_NOTEMPTY
 * 63	07c28ace-561a-476e-a9b9-3d5ad6e35933	AMENDMENT_BASISOFRECORD_STANDARDIZED
 * 41	bd385eeb-44a2-464b-a503-7abe407ef904	AMENDMENT_DCTYPE_STANDARDIZED
 * 133	dcbe5bd2-42a0-4aab-bb4d-8f148c6490f8	AMENDMENT_LICENSE_STANDARDIZED
 * 75	96667a0a-ae59-446a-bbb0-b7f2b0ca6cf5	AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT
 * 115	f8f3a093-042c-47a3-971a-a482aaaf3b75	AMENDMENT_OCCURRENCESTATUS_STANDARDIZED
 * 91	cdaabb0d-a863-49d0-bc0f-738d771acba5	VALIDATION_DCTYPE_STANDARD
 * 38	3136236e-04b6-49ea-8b34-a65f25e3aba1	VALIDATION_LICENSE_STANDARD
 * 23	3cfe9ab4-79f8-4afd-8da5-723183ef16a3	VALIDATION_OCCURRENCEID_STANDARD
 * 46	3f335517-f442-4b98-b149-1e87ff16de45	VALIDATION_SCIENTIFICNAME_FOUND
 *
 * 
 * @author mole
 *
 */
@Mechanism(value="09fd4e2f-cf10-4665-aa74-bcf8e3795163",label="Kurator: Metadata/Record-Level Validator - DwCMetadataDQ:v0.0.1")
public class DwCMetadataDQ {

	private static final Log logger = LogFactory.getLog(DwCMetadataDQ.class);

    /**
     * Is there a value in dwc:dataGeneralizations?
     *
     * Provides: ISSUE_DATAGENERALIZATIONS_NOTEMPTY
     * Version: 2022-11-08
     *
     * @param dataGeneralizations the provided dwc:dataGeneralizations to evaluate
     * @return DQResponse the response of type IssueValue to return
     */
    @Issue(label="ISSUE_DATAGENERALIZATIONS_NOTEMPTY", description="Is there a value in dwc:dataGeneralizations?")
    @Provides("13d5a10e-188e-40fd-a22c-dbaa87b91df2")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/13d5a10e-188e-40fd-a22c-dbaa87b91df2/2022-11-08")
    public static DQResponse<IssueValue> issueDatageneralizationsNotempty(@ActedUpon("dwc:dataGeneralizations") String dataGeneralizations) {
        DQResponse<IssueValue> result = new DQResponse<IssueValue>();

        // Specification
        // POTENTIAL_ISSUE if dwc:dataGeneralizations is not EMPTY; 
        // otherwise NOT_ISSUE 

		if (MetadataUtils.isEmpty(dataGeneralizations)) {
			result.addComment("No value provided for dataGeneralizations.");
			result.setValue(IssueValue.NOT_ISSUE);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dataGeneralizations.  The information present in dwc:dataGeneralizations may (through obfuscation of spatial information or other changes to the data) make the data unfit for desired purpose.  Determining whether the data are fit for your use will require examination of the asserted data generalizations and consideration against your data quality needs.");
			result.setValue(IssueValue.POTENTIAL_ISSUE);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}

        
        return result;
    }
	
    /**
     * Does the value of dwc:occurrenceID occur in bdqSourceAuthority?
     *
     * Provides: VALIDATION_OCCURRENCEID_STANDARD
     *
     * @param occurrenceID the provided dwc:occurrenceID to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_OCCURRENCEID_STANDARD", description="Does the value of dwc:occurrenceID occur in bdqSourceAuthority?")
    @Provides("3cfe9ab4-79f8-4afd-8da5-723183ef16a3")
    public DQResponse<ComplianceValue> validationOccurrenceidStandard(@ActedUpon("dwc:occurrenceID") String occurrenceID) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:occurrenceID is EMPTY; 
        // COMPLIANT if the value of dwc:occurrenceID follows a format 
        // commonly associated with globally unique identifiers (GUIDs); 
        // otherwise NOT_COMPLIANT 

        return result;
    }

    /**
     * Does the value of dcterms:license occur in bdq:sourceAuthority?
     *
     * Provides: VALIDATION_LICENSE_STANDARD
     *
     * @param license the provided dcterms:license to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_LICENSE_STANDARD", description="Does the value of dcterms:license occur in bdq:sourceAuthority?")
    @Provides("3136236e-04b6-49ea-8b34-a65f25e3aba1")
    public DQResponse<ComplianceValue> validationLicenseStandard(@ActedUpon("dcterms:license") String license) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dcterms:license 
        // is EMPTY; COMPLIANT if the value of the term dcterms:license 
        // is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority 
        // default = "Creative Commons" [https://creativecommons.org/] 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * Propose amendment to the value of dc:type using the DCMI type vocabulary.
     *
     * Provides: AMENDMENT_DCTYPE_STANDARDIZED
     *
     * @param type the provided dc:type to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_DCTYPE_STANDARDIZED", description="Propose amendment to the value of dc:type using the DCMI type vocabulary.")
    @Provides("bd385eeb-44a2-464b-a503-7abe407ef904")
    public DQResponse<AmendmentValue> amendmentDctypeStandardized(@ActedUpon("dc:type") String type) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the DCMI type vocabulary 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // value of dc:type is EMPTY; AMENDED the value of dc:type 
        // if it can be unambiguously interpreted as a value in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority is "DCMI Type 
        // Vocabulary" [https://www.dublincore.org/specifications/dublin-core/dcmi-type-vocabulary/] 
        // 

        return result;
    }

    /**
     * Is there a value in dwc:occurrenceID?
     *
     * Provides: VALIDATION_OCCURRENCEID_NOTEMPTY
     *
     * @param occurrenceID the provided dwc:occurrenceID to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_OCCURRENCEID_NOTEMPTY", description="Is there a value in dwc:occurrenceID?")
    @Provides("c486546c-e6e5-48a7-b286-eba7f5ca56c4")
    public static DQResponse<ComplianceValue> validationOccurrenceidNotempty(@ActedUpon("dwc:occurrenceID") String occurrenceID) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:occurrenceID is not EMPTY; otherwise NOT_COMPLIANT 
        // 
       
		if (MetadataUtils.isEmpty(occurrenceID)) {
			result.addComment("No value provided for occurrenceID.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for occurrenceID.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}

        return result;
    }

    /**
     * Is there a value in dwc:basisOfRecord?
     *
     * Provides: VALIDATION_BASISOFRECORD_NOTEMPTY
     *
     * @param basisOfRecord the provided dwc:basisOfRecord to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_BASISOFRECORD_NOTEMPTY", description="Is there a value in dwc:basisOfRecord?")
    @Provides("ac2b7648-d5f9-48ca-9b07-8ad5879a2536")
    public static DQResponse<ComplianceValue> validationBasisofrecordNotempty(@ActedUpon("dwc:basisOfRecord") String basisOfRecord) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:basisOfRecord is not EMPTY; otherwise NOT_COMPLIANT 
        // 
        
		if (MetadataUtils.isEmpty(basisOfRecord)) {
			result.addComment("No value provided for basisOfRecord.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for basisOfRecord.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}

        return result;
    }

    /**
     * Propose amendment to the value of dwc:basisOfRecord using bdq:sourceAuthority.
     *
     * Provides: AMENDMENT_BASISOFRECORD_STANDARDIZED
     *
     * @param basisOfRecord the provided dwc:basisOfRecord to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_BASISOFRECORD_STANDARDIZED", description="Propose amendment to the value of dwc:basisOfRecord using bdq:sourceAuthority.")
    @Provides("07c28ace-561a-476e-a9b9-3d5ad6e35933")
    public DQResponse<AmendmentValue> amendmentBasisofrecordStandardized(@ActedUpon("dwc:basisOfRecord") String basisOfRecord) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord 
        // is EMPTY; AMENDED the value of dwc:basisOfRecord if it could 
        // be unambiguously interpreted as a value in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority default = "Darwin 
        // Core Terms" [https://dwc.tdwg.org/terms/#dwc:basisOfRecord] 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * Propose amendment to the value of dwc:occurrenceStatus, if it is empty, to the default parameter value.
     *
     * Provides: AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT
     *
     * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT", description="Propose amendment to the value of dwc:occurrenceStatus, if it is empty, to the default parameter value.")
    @Provides("96667a0a-ae59-446a-bbb0-b7f2b0ca6cf5")
    public DQResponse<AmendmentValue> amendmentOccurrencestatusAssumeddefault(@ActedUpon("dwc:occurrenceStatus") String occurrenceStatus) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // FILLED_IN the value of dwc:occurrenceStatus using the Parameter 
        // value if the dwc:occurrence.Status is EMPTY; otherwise NOT_AMENDED 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // Default value: occurrenceStatus="present"

        return result;
    }

    /**
     * Does the value in dc:type occur as a value in the DCMI type vocabulary?
     *
     * Provides: VALIDATION_DCTYPE_STANDARD
     *
     * @param type the provided dc:type to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DCTYPE_STANDARD", description="Does the value in dc:type occur as a value in the DCMI type vocabulary?")
    @Provides("cdaabb0d-a863-49d0-bc0f-738d771acba5")
    public DQResponse<ComplianceValue> validationDctypeStandard(@ActedUpon("dc:type") String type) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // value of dc:type is EMPTY; COMPLIANT if the value of dc:type 
        // is found in bdq:sourceAuthority; otherwise NOT_COMPLIANT 
        // bdq:sourceAuthority is "DCMI Type Vocabulary" [https://www.dublincore.org/specifications/dublin-core/dcmi-type-vocabulary/] 
        // 

        return result;
    }

    /**
     * Is there a value in dwc:establishmentMeans?
     *
     * Provides: ISSUE_ESTABLISHMENTMEANS_NOTEMPTY
     *
     * @param establishmentMeans the provided dwc:establishmentMeans to evaluate
     * @return DQResponse the response of type IssueValue to return
     */
    @Issue(label="ISSUE_ESTABLISHMENTMEANS_NOTEMPTY", description="Is there a value in dwc:establishmentMeans?")
    @Provides("acc8dff2-d8d1-483a-946d-65a02a452700")
    public static DQResponse<IssueValue> issueEstablishmentmeansNotempty(@ActedUpon("dwc:establishmentMeans") String establishmentMeans) {
        DQResponse<IssueValue> result = new DQResponse<IssueValue>();

        // Specification
        // POTENTIAL_ISSUE if dwc:establishmentMeans is not EMPTY; 
        // otherwise NOT_ISSUE 
        
		if (MetadataUtils.isEmpty(establishmentMeans)) {
			result.addComment("No value provided for establishmentMeans.");
			result.setValue(IssueValue.NOT_ISSUE);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for establishmentMeans.  The Occurrence may reflect introduction through the direct or indirect activity of modern humans.");
			result.setValue(IssueValue.POTENTIAL_ISSUE);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}

        return result;
    }

    /**
     * Is there a value in dcterms:license?
     *
     * Provides: VALIDATION_LICENSE_NOTEMPTY
     *
     * @param license the provided dcterms:license to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_LICENSE_NOTEMPTY", description="Is there a value in dcterms:license?")
    @Provides("15f78619-811a-4c6f-997a-a4c7888ad849")
    public static DQResponse<ComplianceValue> validationLicenseNotempty(@ActedUpon("dcterms:license") String license) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dcterms:license is not EMPTY; otherwise NOT_COMPLIANT 
        // 

		if (MetadataUtils.isEmpty(license)) {
			result.addComment("No value provided for license.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for license.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}
        
        return result;
    }

    /**
     * Is there a value in dc:type?
     *
     * Provides: VALIDATION_DCTYPE_NOTEMPTY
     *
     * @param type the provided dc:type to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DCTYPE_NOTEMPTY", description="Is there a value in dc:type?")
    @Provides("374b091a-fc90-4791-91e5-c1557c649169")
    public static DQResponse<ComplianceValue> validationDctypeNotempty(@ActedUpon("dc:type") String type) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dc:type is not EMPTY; otherwise NOT_COMPLIANT 
        // 
        
		if (MetadataUtils.isEmpty(type)) {
			result.addComment("No value provided for dc:type.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dc:type.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}

        return result;
    }


    /**
     * Does the value of dwc:basisOfRecord occur in bdq:sourceAuthority?
     *
     * Provides: #104 VALIDATION_BASISOFRECORD_STANDARD
     *
     * @param basisOfRecord the provided dwc:basisOfRecord to evaluate
     * @param sourceAuthority the source authority for basis of record values to evaluate against, Darwin Core Terms used as default.
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_BASISOFRECORD_STANDARD", description="Does the value of dwc:basisOfRecord occur in bdq:sourceAuthority?")
    @Provides("42408a00-bf71-4892-a399-4325e2bc1fb8")
    public static DQResponse<ComplianceValue> validationBasisofrecordStandard(
    		@ActedUpon("dwc:basisOfRecord") String basisOfRecord,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord 
        // is EMPTY; COMPLIANT if the value of dwc:basisOfRecord is 
        // valid using the bdq:sourceAuthority; otherwise NOT_COMPLIANT 
        // 
        
        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "Darwin Core Terms" [https://dwc.tdwg.org/terms/#dwc:basisOfRecord] 
        
        if (MetadataUtils.isEmpty(sourceAuthority)) { 
        	sourceAuthority = "Darwin Core Terms";
        }
        if (sourceAuthority.equals("dwc:basistOfRecord")) { 
        	sourceAuthority = "Darwin Core Terms";
        } else if (sourceAuthority.equals("https://dwc.tdwg.org/terms/#dwc:basisOfRecord")) {
        	sourceAuthority = "Darwin Core Terms";
    	}
        
        if (MetadataUtils.isEmpty(basisOfRecord)) { 
			result.addComment("No value provided for dwc:basisOfRecord.");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else {
        	List<String> values = null;
        	if (sourceAuthority.equals("Darwin Core Terms")) {
        		// "Recommended best practice is to use the standard label of one of the Darwin Core classes."
        		// e.g. skos:prefLabel "Fossil Specimen"@en;  rdfs:label "Fossil Specimen"@en;
        		values = List.of("Dataset","Event","Event Attribute","Event Measurement","Fossil Specimen","Geological Context","Human Observation","Identification","Living Specimen","Location","Machine Observation","Material Citation","Material Sample","Measurement or Fact","Occurrence","Occurrence Measurement","Organism","Preserved Specimen","Resource Relationship","Sample","Sample Attribute","Sampling Event","Sampling Location","Taxon");
        	} 
        	
        	if (values==null) { 
        		result.addComment("Unsuported bdq:sourceAuthority [" + sourceAuthority +"].");
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	} else { 
        		if (values.contains(basisOfRecord)) { 
        			result.addComment("Provided value for dwc:basisOfRecord conforms to sourceAuthority.");
        			result.setValue(ComplianceValue.COMPLIANT);
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        		} else {
        			result.addComment("Provided value for dwc:basisOfRecord ["+ basisOfRecord +"] not found in specified sourceAuthority.");
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        		}
        	}
        }
        
        return result;
    }

    /**
     * Propose amendment to the value of dwc:occurrenceStatus using bdq:sourceAuthority.
     *
     * Provides: AMENDMENT_OCCURRENCESTATUS_STANDARDIZED
     *
     * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_OCCURRENCESTATUS_STANDARDIZED", description="Propose amendment to the value of dwc:occurrenceStatus using bdq:sourceAuthority.")
    @Provides("f8f3a093-042c-47a3-971a-a482aaaf3b75")
    public DQResponse<AmendmentValue> amendmentOccurrencestatusStandardized(@ActedUpon("dwc:occurrenceStatus") String occurrenceStatus) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:ocurrenceStatus 
        // is EMPTY; AMENDED the value of dwc:occurrenceStatus if could 
        // be unambiguously interpreted as a value in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED dq:sourceAuthority default = "Darwin 
        // Core Standard" [https://dwc.tdwg.org/terms/#occurrenceStatus] 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }


    /**
     * Does the value of dwc:occurrenceStatus occur in bdq:sourceAuthority?
     *
     * Provides: VALIDATION_OCCURRENCESTATUS_STANDARD
     *
     * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate
     * @param sourceAuthority the provided source authority against which to evaluate occurrenceStatus
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_OCCURRENCESTATUS_STANDARD", description="Does the value of dwc:occurrenceStatus occur in bdq:sourceAuthority?")
    @Provides("7af25f1e-a4e2-4ff4-b161-d1f25a5c3e47")
    public static DQResponse<ComplianceValue> validationOccurrencestatusStandard(
    		@ActedUpon("dwc:occurrenceStatus") String occurrenceStatus,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
        
        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:occurrenceStatus 
        // is EMPTY; COMPLIANT if the value of dwc:occurrenceStatus 
        // is resolved by the bdq:sourceAuthority; otherwise NOT_COMPLIANT 

        //  Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "Darwin Core Standard" [https://dwc.tdwg.org/terms/#occurrenceStatus] 

        if (MetadataUtils.isEmpty(sourceAuthority)) { 
        	sourceAuthority = "Darwin Core Standard";
        }
        if (sourceAuthority.equals("dwc:occurrenceStatus")) { 
        	sourceAuthority = "Darwin Core Terms";
        } else if (sourceAuthority.equals("https://dwc.tdwg.org/terms/#occurrenceStatus")) {
        	sourceAuthority = "Darwin Core Terms";
    	}
        
        if (MetadataUtils.isEmpty(occurrenceStatus)) { 
			result.addComment("No value provided for dwc:basisOfRecord.");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else {
        	List<String> values = null;
        	if (sourceAuthority.equals("Darwin Core Standard")) {
        		// "For Occurrences, the default vocabulary is recommended to consist of "present" and "absent", but can be extended by implementers with good justification."
        		values = List.of("present","absent");
        	} 
        	
        	if (values==null) { 
        		result.addComment("Unsuported bdq:sourceAuthority [" + sourceAuthority +"].");
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	} else { 
        		if (values.contains(occurrenceStatus)) { 
        			result.addComment("Provided value for dwc:occurrenceStatus conforms to sourceAuthority.");
        			result.setValue(ComplianceValue.COMPLIANT);
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        		} else {
        			result.addComment("Provided value for dwc:occurrenceStatus ["+ occurrenceStatus +"] not found in specified sourceAuthority.");
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        		}
        	}
        }
        
        return result;
    }

    /**
     * Is there a value in dwc:occurrenceStatus?
     *
     * Provides: VALIDATION_OCCURRENCESTATUS_NOTEMPTY
     *
     * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_OCCURRENCESTATUS_NOTEMPTY", description="Is there a value in dwc:occurrenceStatus?")
    @Provides("eb4a17f6-6bea-4cdd-93dd-d5a7e9d1eccf")
    public static DQResponse<ComplianceValue> validationOccurrencestatusNotempty(@ActedUpon("dwc:occurrenceStatus") String occurrenceStatus) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:occurrenceStatus is not EMPTY; otherwise 
        // NOT_COMPLIANT 

		if (MetadataUtils.isEmpty(occurrenceStatus)) {
			result.addComment("No value provided for occurrenceStatus.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for occurrenceStatus.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}
        
        return result;
    }

    /**
     * Propose amendment to the value of dwc:license using bdq:sourceAuthority.
     *
     * Provides: AMENDMENT_LICENSE_STANDARDIZED
     *
     * @param license the provided dcterms:license to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_LICENSE_STANDARDIZED", description="Propose amendment to the value of dwc:license using bdq:sourceAuthority.")
    @Provides("dcbe5bd2-42a0-4aab-bb4d-8f148c6490f8")
    public DQResponse<AmendmentValue> amendmentLicenseStandardized(@ActedUpon("dcterms:license") String license) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; AMENDED value of dcterms:license if it 
        // could be unambiguously interpreted as a value in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED. bdq:sourceAuthority default = "Creative 
        // Commons" [https://creativecommons.org/] 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

}
