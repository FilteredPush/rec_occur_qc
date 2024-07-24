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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.language.MatchRatingApproachEncoder;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.similarity.EditDistance;
import org.apache.commons.text.similarity.EditDistanceFrom;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Amendment;
import org.datakurator.ffdq.annotations.Consulted;
import org.datakurator.ffdq.annotations.Issue;
import org.datakurator.ffdq.annotations.Mechanism;
import org.datakurator.ffdq.annotations.Parameter;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.annotations.ProvidesVersion;
import org.datakurator.ffdq.annotations.Specification;
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
 * #91	cdaabb0d-a863-49d0-bc0f-738d771acba5	VALIDATION_DCTYPE_STANDARD
 * #38	3136236e-04b6-49ea-8b34-a65f25e3aba1	VALIDATION_LICENSE_STANDARD
 * #41	bd385eeb-44a2-464b-a503-7abe407ef904	AMENDMENT_DCTYPE_STANDARDIZED
 * 63	07c28ace-561a-476e-a9b9-3d5ad6e35933	AMENDMENT_BASISOFRECORD_STANDARDIZED
 * 
 * 
 * TODO: Implement:
 * 
 * 29	fecaa8a3-bbd8-4c5a-a424-13c37c4bb7b1	ISSUE_ANNOTATION_NOTEMPTY
 * 133	dcbe5bd2-42a0-4aab-bb4d-8f148c6490f8	AMENDMENT_LICENSE_STANDARDIZED
 * 75	96667a0a-ae59-446a-bbb0-b7f2b0ca6cf5	AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT
 * 115	f8f3a093-042c-47a3-971a-a482aaaf3b75	AMENDMENT_OCCURRENCESTATUS_STANDARDIZED
 * 23	3cfe9ab4-79f8-4afd-8da5-723183ef16a3	VALIDATION_OCCURRENCEID_STANDARD
 * 46	3f335517-f442-4b98-b149-1e87ff16de45	VALIDATION_SCIENTIFICNAME_FOUND
 *
 * And the following supplementary tests: 
 * 
 * #224 VALIDATION_MODIFIED_NOTEMPTY e17918fc-25ca-4a3a-828b-4502432b98c4
 * #235 VALIDATION_LIFESTAGE_NOTEMPTY 34b9eec9-03d5-4dc9-94b7-5b05ddcaaa87
 * #225 VALIDATION_DISPOSITION_NOTEMPTY b4c17611-2703-474f-b46a-93b08ecfee16
 * #232 VALIDATION_INDIVIDUALCOUNT_NOTEMPTY aff0facd-1d2a-40a5-a55a-61f950cd68a0
 * #242 VALIDATION_RECORDEDBY_NOTEMPTY 986ad95d-ffa1-4e3b-a6cb-ed943c87be0d
 * #243 VALIDATION_RECORDNUMBER_NOTEMPTY 3bd2477c-6497-43b0-94e6-b811eed1b1cb
 * #260 VALIDATION_PREPARATIONS_NOTEMPTY 2aa1b7a0-0473-4a90-bf11-a02137c5c65b
 * #263 VALIDATION_SEX_NOTEMPTY 76067adf-1422-4d03-89e3-9067c3043700
 * #261 VALIDATION_RELATIONSHIPOFRESOURCE_NOTEMPTY cd281f7e-13b3-43ae-8677-de06ffa70bb4
 * #262 VALIDATION_REPRODUCTIVECONDITION_NOTEMPTY 3eefe72c-4c7d-4dee-89b6-e9d91d3f1981
 * #288 VALIDATION_DEGREEOFESTABLISHMENT_NOTEMPTY 0fa16c7e-eb9c-4add-9193-aca6087d6636
 * #289 VALIDATION_PATHWAY_NOTEMPTY fffdc42b-b15e-4450-9e6a-f4181a319106
 * 
 * @author mole
 *
 */
@Mechanism(value="09fd4e2f-cf10-4665-aa74-bcf8e3795163",label="Kurator: Metadata/Record-Level Validator - DwCMetadataDQ:v0.0.1")
public class DwCMetadataDQ {

	private static final Log logger = LogFactory.getLog(DwCMetadataDQ.class);
    
	protected static final String dcTypeLiterals = "Collection,Dataset,Event,Image,InteractiveResource,MovingImage,PhysicalObject,Service,Software,Sound,StillImage,Text";

    /**
     * Is there a value in dwc:dataGeneralizations?
     *
     * Provides: #72 ISSUE_DATAGENERALIZATIONS_NOTEMPTY
     * Version: 2023-09-18
     *
     * @param dataGeneralizations the provided dwc:dataGeneralizations to evaluate
     * @return DQResponse the response of type IssueValue to return
     */
    @Issue(label="ISSUE_DATAGENERALIZATIONS_NOTEMPTY", description="Is there a value in dwc:dataGeneralizations?")
    @Provides("13d5a10e-188e-40fd-a22c-dbaa87b91df2")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/13d5a10e-188e-40fd-a22c-dbaa87b91df2/2022-11-08")
    @Specification("POTENTIAL_ISSUE if dwc:dataGeneralizations is not EMPTY; otherwise NOT_ISSUE ")
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
	
// TODO: Implementation of VALIDATION_OCCURRENCEID_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/c486546c-e6e5-48a7-b286-eba7f5ca56c4/2023-09-17 see line: 120
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

// TODO: Implementation of VALIDATION_BASISOFRECORD_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/ac2b7648-d5f9-48ca-9b07-8ad5879a2536/2023-09-17 see line: 150
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
        		// "Recommended best practice is to use the local name of one of the Darwin Core classes."
        		values = List.of("Dataset","Event","EventAttribute","EventMeasurement","FossilSpecimen","GeologicalContext","HumanObservation","Identification","LivingSpecimen","Location","MachineObservation","MaterialCitation","MaterialSample","MeasurementOrFact","Occurrence","OccurrenceMeasurement","Organism","PreservedSpecimen","ResourceRelationship","Sample","Sample Attribute","SamplingEvent","SamplingLocation","Taxon");
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
    * Does the value of dwc:occurrenceID occur in bdq:SourceAuthority?
    *
    * Provides: 23 VALIDATION_OCCURRENCEID_STANDARD
    * Version: 2023-09-17
    *
    * @param occurrenceID the provided dwc:occurrenceID to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_OCCURRENCEID_STANDARD", description="Does the value of dwc:occurrenceID occur in bdq:SourceAuthority?")
    @Provides("3cfe9ab4-79f8-4afd-8da5-723183ef16a3")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/3cfe9ab4-79f8-4afd-8da5-723183ef16a3/2023-09-17")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:occurrenceID is EMPTY; COMPLIANT if the value of dwc:occurrenceID follows a format commonly associated with globally unique identifiers (GUIDs); otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationOccurrenceidStandard(
        @ActedUpon("dwc:occurrenceID") String occurrenceID
    ) {
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
    * Provides:  #38 VALIDATION_LICENSE_STANDARD
    * Version: 2023-09-17
    *
    * @param license the provided dcterms:license to evaluate as ActedUpon.
    * @param sourceAuthority the specified source authority for licences.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_LICENSE_STANDARD", description="Does the value of dcterms:license occur in bdq:sourceAuthority?")
    @Provides("3136236e-04b6-49ea-8b34-a65f25e3aba1")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/3136236e-04b6-49ea-8b34-a65f25e3aba1/2023-09-17")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dcterms:license is EMPTY; COMPLIANT if the value of the term dcterms:license is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority default = 'Creative Commons' {[https://creativecommons.org/]} {Creative Commons licenses [https://creativecommons.org/about/cclicenses/]}")
    public static DQResponse<ComplianceValue> validationLicenseStandard(
        @ActedUpon("dcterms:license") String license,
    	@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dcterms:license 
        // is EMPTY; COMPLIANT if the value of the term dcterms:license 
        // is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority 
        // 

        // TODO: Implementation ahead of specification, change default to obtain regex.
        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority
        // default = "Creative Commons" {[https://creativecommons.org/]} 
        // {Creative Commons licenses [https://creativecommons.org/about/cclicenses/]} 
        
 
        if (sourceAuthority==null) { sourceAuthority = "Creative Commons"; }
        String pattern = "";
        if (sourceAuthority.equals("Creative Commons")) { 
        	// regex to match cc licences, version 4, and public domain dedication, version 1
        	pattern = "^(http(s){0,1}://creativecommons[.]org/licenses/"
        			+ "(by|by-sa|by-nc|by-nc-sa|by-nd|by-nc-nd)/4[.]0/"
        			+ "((deed|legalcode)"
        			+ "([.](id|eu|da|de|en|es|fr|fy|hr|it|lv|lt|mi|ni|no|pl|pt|ro|si|fi|sv|tr|cs|el|ru|uk|ar|jp|zh-hans|zh-hant|ko)"
        			+ "){0,1}){0,1})"
        			+ "|"
        			+ "(http(s){0,1}://creativecommons[.]org/publicdomain/zero/1[.]0/"
        			+ "((deed|legalcode)"
        			+ "([.](id|eu|da|de|en|es|fr|fy|hr|it|lv|lt|ni|no|pl|pt|ro|si|fi|sv|tr|cs|el|ru|uk|ar|jp|zh-hans|zh-hant|ko)"
        			+ "){0,1}){0,1})$";
        	Pattern licencePattern = Pattern.compile(pattern);
        	Matcher m = licencePattern.matcher(license);
        } else { 
			result.addComment("Unknown Source Authority ["+sourceAuthority+"]");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        }
        logger.debug(pattern);
        if (pattern.length() > 0) { 
        	if (MetadataUtils.isEmpty(license)) {
        		result.addComment("No value provided for dwc:licence.");
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	} else if (license.matches(pattern)) {	
        		result.addComment("Provided value for dwc:license conforms to expectations.");
        		result.setValue(ComplianceValue.COMPLIANT);
        		result.setResultState(ResultState.RUN_HAS_RESULT);
        	} else { 
        		result.addComment("Provided value for dwc:licence [" + license +"] does not conform to expectations.");
        		result.setValue(ComplianceValue.NOT_COMPLIANT);
        		result.setResultState(ResultState.RUN_HAS_RESULT);
        	}
        }
		
        return result;
    }

    /**
    * Propose amendment to the value of dc:type using the DCMI type vocabulary.
    * 
    * Supports difference in case, leading or trailing spaces, IRI form instead of literal, soundex, 
    * and single step edit distance for proposing amended values.
    *
    * Provides: #41 AMENDMENT_DCTYPE_STANDARDIZED
    * Version: 2023-09-17
    *
    * @param type the provided dc:type to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_DCTYPE_STANDARDIZED", description="Propose amendment to the value of dc:type using the DCMI type vocabulary.")
    @Provides("bd385eeb-44a2-464b-a503-7abe407ef904")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/bd385eeb-44a2-464b-a503-7abe407ef904/2023-09-17")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the value of dc:type is EMPTY; AMENDED the value of dc:type if it can be unambiguously interpreted as a value in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority is 'Dublin Core Metadata Initiative (DCMI)' {[https://www.dublincore.org/]} {DCMI Type Vocabulary' [https://www.dublincore.org/specifications/dublin-core/dcmi-type-vocabulary/]}")
    public static DQResponse<AmendmentValue> amendmentDctypeStandardized(
        @ActedUpon("dc:type") String type
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // value of dc:type is EMPTY; AMENDED the value of dc:type 
        // if it can be unambiguously interpreted as a value in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority is "Dublin Core 
        // Metadata Initiative (DCMI)" {[https://www.dublincore.org/]} 
        // {DCMI Type Vocabulary" [https://www.dublincore.org/specifications/dublin-core/dcmi-type-vocabulary/]} 
        // 
        // TODO: sourceAuthority is not correct, needs update, should be explicit in specification.
        
        ArrayList<String> dcTypeLiteralList = new ArrayList<String>(Arrays.asList(dcTypeLiterals.split(",")));
        
        // the value that should be used for dcterms:type starts with this string (followed by one of the literals)
        String uriMatcher = "^http(s){0,1}://purl.org/dc/dcmitype/";
        
     	if (MetadataUtils.isEmpty(type)) { 
			result.addComment("Provided value for dc:type is empty.");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
     	} else if (dcTypeLiteralList.contains(type)) { 
			result.addComment("Provided value for dc:type is a valid dc:type literal.");
			result.setResultState(ResultState.NOT_AMENDED);
     	} else if (dcTypeLiteralList.contains(type.trim())) { 
			result.addComment("Provided value for dc:type trimed to form a valid dc:type literal.");
			result.setResultState(ResultState.AMENDED);
			Map<String, String> values = new HashMap<>();
			values.put("dc:type", type.trim()) ;
			result.setValue(new AmendmentValue(values));
     	} else {
     		// Try: Trim, wrong case, uri prefix instead of literal
     		boolean matched = false;
     		Soundex soundex = Soundex.US_ENGLISH_SIMPLIFIED;
     		String typeSoundex = soundex.encode(type);
     		LevenshteinDistance editDistance = new LevenshteinDistance(3);
     		Iterator<String> i = dcTypeLiteralList.iterator();
     		while (i.hasNext() && !matched) { 
     			String aLiteral = i.next();
     			String literalSoundex = soundex.encode(aLiteral);
     			logger.debug(literalSoundex);
     			logger.debug(editDistance.apply(type, aLiteral));
     			if (type.trim().toUpperCase().equals(aLiteral)) { 
     				matched = true;	
     				result.addComment("Provided value for dc:type ["+type+"] corrected to form a valid dc:type literal.");
     				result.setResultState(ResultState.AMENDED);
     				Map<String, String> values = new HashMap<>();
     				values.put("dc:type", aLiteral) ;
     				result.setValue(new AmendmentValue(values));
     			} else if (type.trim().replaceAll(uriMatcher, "").toUpperCase().equals(aLiteral.toUpperCase())) { 
     				matched = true;	
     				result.addComment("Provided value for dc:type ["+type+"] corrected to form a valid dc:type literal, the provided value was an IRI, and dc:type must be a literal.");
     				result.setResultState(ResultState.AMENDED);
     				Map<String, String> values = new HashMap<>();
     				values.put("dc:type", aLiteral) ;
     				result.setValue(new AmendmentValue(values));
     			} else if (typeSoundex.equals(literalSoundex)) {
     				// check that soundex match is unambiguous 
     				int totalMatches = 0;
     				Iterator<String> ic = dcTypeLiteralList.iterator();
     				while (ic.hasNext() && !matched) { 
     					String aLiteralCheck = ic.next();
     					literalSoundex = soundex.encode(aLiteralCheck);
     					if (typeSoundex.equals(literalSoundex)) { 
     						totalMatches++;
     					}
     				}
     				if (totalMatches==1) { 
     					matched = true;	
     					result.addComment("Provided value for dc:type ["+type+"] sounds like ["+aLiteral+"] so corrected to this valid dc:type literal.");
     					result.setResultState(ResultState.AMENDED);
     					Map<String, String> values = new HashMap<>();
     					values.put("dc:type", aLiteral) ;
     					result.setValue(new AmendmentValue(values));
     				}
     			} else if (editDistance.apply(type, aLiteral)==1) { 
   					matched = true;	
   					result.addComment("Provided value for dc:type ["+type+"] is one edit away from ["+aLiteral+"] so corrected to this valid dc:type literal.");
   					result.setResultState(ResultState.AMENDED);
   					Map<String, String> values = new HashMap<>();
   					values.put("dc:type", aLiteral) ;
   					result.setValue(new AmendmentValue(values));
     			}
     		}
     		if (!matched) { 
     			result.addComment("Unable to propose an amendment to conform the provided value for dc:type ["+type+"] to a valid dc:type literal.");
     			result.setResultState(ResultState.NOT_AMENDED);
     		}
     	}
     	
        return result;
    }

    /**
    * Propose amendment to the value of dwc:basisOfRecord using bdq:sourceAuthority.
    *
    * Provides: #63 AMENDMENT_BASISOFRECORD_STANDARDIZED
    * Version: 2023-07-24
    *
    * @param basisOfRecord the provided dwc:basisOfRecord to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_BASISOFRECORD_STANDARDIZED", description="Propose amendment to the value of dwc:basisOfRecord using bdq:sourceAuthority.")
    @Provides("07c28ace-561a-476e-a9b9-3d5ad6e35933")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/07c28ace-561a-476e-a9b9-3d5ad6e35933/2023-07-24")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord is EMPTY; AMENDED the value of dwc:basisOfRecord if it could be unambiguously interpreted as a value in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority default = 'Darwin Core basisOfRecord' {[https://dwc.tdwg.org/terms/#dwc:basisOfRecord]} {dwc:basisOfRecord vocabulary [https://rs.gbif.org/vocabulary/dwc/basis_of_record.xml]}")
    public static DQResponse<AmendmentValue> amendmentBasisofrecordStandardized(
        @ActedUpon("dwc:basisOfRecord") String basisOfRecord,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord 
        // is EMPTY; AMENDED the value of dwc:basisOfRecord if it could 
        // be unambiguously interpreted as a value in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority 
        // 

        // Parameters. This test is defined as parameterized.
        // dwc:basisOfRecord vocabulary
        // default = "Darwin 
        // Core basisOfRecord" {[https://dwc.tdwg.org/terms/#dwc:basisOfRecord]} 
        // {dwc:basisOfRecord vocabulary [https://rs.gbif.org/vocabulary/dwc/basis_of_record.xml]} 
        
        List<String> basisOfRecordLiteralList = null;
        if (sourceAuthority == null) {
        	sourceAuthority = "Darwin Core basisOfRecord";
        }
        try {
        	if (sourceAuthority.equals("Darwin Core basisOfRecord")) {
        		// https://rs.gbif.org/vocabulary/dwc/basis_of_record.xml
        		// "Recommended best practice is to use the standard label of one of the Darwin Core classes."
        		// e.g. skos:prefLabel "Fossil Specimen"@en;  rdfs:label "Fossil Specimen"@en;
        		basisOfRecordLiteralList = List.of("Dataset","Event","EventAttribute","EventMeasurement","FossilSpecimen","GeologicalContext","HumanObservation","Identification","LivingSpecimen","Location","MachineObservation","MaterialCitation","MaterialSample","MeasurementOrFact","Occurrence","OccurrenceMeasurement","Organism","PreservedSpecimen","ResourceRelationship","Sample","SampleAttribute","SamplingEvent","SamplingLocation","Taxon");
        	} else { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (sourceAuthorityObject.getAuthority().equals(EnumMetadataSourceAuthority.DWC_BASISOFRECORD)) { 
        			basisOfRecordLiteralList = List.of("Dataset","Event","EventAttribute","EventMeasurement","FossilSpecimen","GeologicalContext","HumanObservation","Identification","LivingSpecimen","Location","MachineObservation","MaterialCitation","MaterialSample","MeasurementOrFact","Occurrence","OccurrenceMeasurement","Organism","PreservedSpecimen","ResourceRelationship","Sample","SampleAttribute","SamplingEvent","SamplingLocation","Taxon");
        		} else if (sourceAuthorityObject.getAuthority().equals(EnumMetadataSourceAuthority.INVALID)) { 
        			throw new SourceAuthorityException("Invalid source authority");
        		}
        	} 

        	// possible IRI values that would need to be corrected to the label
        	// http://rs.tdwg.org/dwc/dwctype/FossilSpecimen  deprecated.
        	// http://rs.tdwg.org/dwc/terms/FossilSpecimen
        	String uriMatcher = "^http(s){0,1}://rs.tdwg.org/dwc/(dwctype|terms)/";

        	if (MetadataUtils.isEmpty(basisOfRecord)) { 
        		result.addComment("Provided value for dwc:basisOfRecord is empty.");
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	} else if (basisOfRecordLiteralList.contains(basisOfRecord)) { 
        		result.addComment("Provided value for dwc:basisOfRecord is a valid dwc:basisOfRecord literal.");
        		result.setResultState(ResultState.NOT_AMENDED);
        	} else if (basisOfRecordLiteralList.contains(basisOfRecord.trim())) { 
        		result.addComment("Provided value for dwc:basisOfRecord trimed to form a valid dwc:basisOfRecord literal.");
        		result.setResultState(ResultState.AMENDED);
        		Map<String, String> newValues = new HashMap<>();
        		newValues.put("dwc:basisOfRecord", basisOfRecord.trim()) ;
        		result.setValue(new AmendmentValue(newValues));
        	} else if (basisOfRecordLiteralList.contains(basisOfRecord.replaceAll("[^A-Za-z]", ""))) { 
        		result.addComment("Provided value for dwc:basisOfRecord removed punctuation to form a valid dwc:basisOfRecord literal.");
        		result.setResultState(ResultState.AMENDED);
        		Map<String, String> newValues = new HashMap<>();
        		newValues.put("dwc:basisOfRecord", basisOfRecord.replaceAll("[^A-Za-z]", "")) ;
        		result.setValue(new AmendmentValue(newValues));
        	} else {
        		// Try: Trim, wrong case, uri prefix instead of literal
        		boolean matched = false;
        		Soundex soundex = Soundex.US_ENGLISH_SIMPLIFIED;
        		String typeSoundex = soundex.encode(basisOfRecord.trim().replace(" ", ""));
        		LevenshteinDistance editDistance = new LevenshteinDistance(3);
        		Iterator<String> i = basisOfRecordLiteralList.iterator();
        		while (i.hasNext() && !matched) { 
        			String aLiteral = i.next();
        			String literalSoundex = soundex.encode(aLiteral.replace(" ", ""));
        			logger.debug(literalSoundex);
        			logger.debug(editDistance.apply(basisOfRecord, aLiteral));
        			if (basisOfRecord.trim().replace(" ", "").toUpperCase().equals(aLiteral.toUpperCase().replace(" ", ""))) { 
        				matched = true;	
        				result.addComment("Provided value for dwc:basisOfRecord ["+basisOfRecord+"] corrected to form a valid dwc:basisOfRecord literal.");
        				result.setResultState(ResultState.AMENDED);
        				Map<String, String> newValues = new HashMap<>();
        				newValues.put("dwc:basisOfRecord", aLiteral) ;
        				result.setValue(new AmendmentValue(newValues));
        			} else if (basisOfRecord.trim().replaceAll(uriMatcher, "").toUpperCase().equals(aLiteral.toUpperCase())) { 
        				matched = true;	
        				result.addComment("Provided value for dwc:basisOfRecord ["+basisOfRecord+"] corrected to form a valid dwc:basisOfRecord literal, the provided value was an IRI, and dwc:basisOfRecord must be a literal.");
        				result.setResultState(ResultState.AMENDED);
        				Map<String, String> newValues = new HashMap<>();
        				newValues.put("dwc:basisOfRecord", aLiteral) ;
        				result.setValue(new AmendmentValue(newValues));
        			} else if (typeSoundex.equals(literalSoundex)) {
        				// check that soundex match is unambiguous 
        				int totalMatches = 0;
        				Iterator<String> ic = basisOfRecordLiteralList.iterator();
        				while (ic.hasNext() && !matched) { 
        					String aLiteralCheck = ic.next();
        					literalSoundex = soundex.encode(aLiteralCheck);
        					if (typeSoundex.equals(literalSoundex)) { 
        						totalMatches++;
        					}
        				}
        				if (totalMatches==1) { 
        					matched = true;	
        					result.addComment("Provided value for dwc:basisOfRecord ["+basisOfRecord+"] sounds like ["+aLiteral+"] so corrected to this valid dwc:basisOfRecord literal.");
        					result.setResultState(ResultState.AMENDED);
        					Map<String, String> newValues = new HashMap<>();
        					newValues.put("dwc:basisOfRecord", aLiteral) ;
        					result.setValue(new AmendmentValue(newValues));
        				}
        			} else if (editDistance.apply(basisOfRecord, aLiteral)==1) { 
        				matched = true;	
        				result.addComment("Provided value for dwc:basisOfRecord ["+basisOfRecord+"] is one edit away from ["+aLiteral+"] so corrected to this valid dwc:basisOfRecord literal.");
        				result.setResultState(ResultState.AMENDED);
        				Map<String, String> newValues = new HashMap<>();
        				newValues.put("dwc:basisOfRecord", aLiteral) ;
        				result.setValue(new AmendmentValue(newValues));
        			}
        		}
        		if (!matched) { 
        			result.addComment("Unable to propose an amendment to conform the provided value for dwc:type ["+basisOfRecord+"] to a valid dwc:basisOfRecord literal.");
        			result.setResultState(ResultState.NOT_AMENDED);
        		}
        	}
        } catch (SourceAuthorityException e) {
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Error with specified sourceAuthority. " + e.getMessage());
        }

        return result;
    }

    /**
    * Propose an amendment of the value of dwc:occurrenceStatus to the default parameter value if dwc:occurrenceStatus, dwc:individualCount and dwc:organismQuantity are empty.
    *
    * Provides: AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT
    * Version: 2023-09-18
    *
    * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate as ActedUpon.
    * @param individualCount the provided dwc:individualCount to evaluate as Consulted.
    * @param organismQuantity the provided dwc:organismQuantity to evaluate as Consulted.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT", description="Propose an amendment of the value of dwc:occurrenceStatus to the default parameter value if dwc:occurrenceStatus, dwc:individualCount and dwc:organismQuantity are empty.")
    @Provides("96667a0a-ae59-446a-bbb0-b7f2b0ca6cf5")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/96667a0a-ae59-446a-bbb0-b7f2b0ca6cf5/2023-09-18")
    @Specification("FILLED_IN the value of dwc:occurrenceStatus using the Parameter value if dwc:occurrence.Status,  dwc:individualCount and dwc:organismQuantity are EMPTY; otherwise NOT_AMENDED dwc:occurrenceStatus default = 'present'")
    public DQResponse<AmendmentValue> amendmentOccurrencestatusAssumeddefault(
        @ActedUpon("dwc:occurrenceStatus") String occurrenceStatus, 
        @Consulted("dwc:individualCount") String individualCount, 
        @Consulted("dwc:organismQuantity") String organismQuantity
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // FILLED_IN the value of dwc:occurrenceStatus using the Parameter 
        // value if dwc:occurrence.Status, dwc:individualCount and 
        // dwc:organismQuantity are EMPTY; otherwise NOT_AMENDED dwc:occurrenceStatus 
        // default = "present" 

        //TODO: Parameters. This test is defined as parameterized.
        // dwc:occurrenceStatus

        return result;
    }

    /**
    * Does the value in dc:type occur as a value in the DCMI type vocabulary?
    *
    * Provides: #91 VALIDATION_DCTYPE_STANDARD
    * Version: 2023-09-18
    *
    * @param type the provided dc:type to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_DCTYPE_STANDARD", description="Does the value in dc:type occur as a value in the DCMI type vocabulary?")
    @Provides("cdaabb0d-a863-49d0-bc0f-738d771acba5")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/cdaabb0d-a863-49d0-bc0f-738d771acba5/2023-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the value of dc:type is EMPTY; COMPLIANT if the value of dc:type is found in  bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority is 'Dublin Core Metadata Initiative (DCMI)' {[https://www.dublincore.org/]} {DCMI Type Vocabulary' [https://www.dublincore.org/specifications/dublin-core/dcmi-type-vocabulary/]}")
    public static DQResponse<ComplianceValue> validationDctypeStandard(
        @ActedUpon("dc:type") String type
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // value of dc:type is EMPTY; COMPLIANT if the value of dc:type 
        // is found in bdq:sourceAuthority; otherwise NOT_COMPLIANT 
        // bdq:sourceAuthority is "Dublin Core Metadata Initiative 
        // (DCMI)" {[https://www.dublincore.org/]} {DCMI Type Vocabulary" 
        // [https://www.dublincore.org/specifications/dublin-core/dcmi-type-vocabulary/]} 
        // 

        ArrayList<String> dcTypeLiteralList = new ArrayList<String>(Arrays.asList(dcTypeLiterals.split(",")));
        
     	if (MetadataUtils.isEmpty(type)) { 
     		result.addComment("Provided value for dc:type is empty");
     		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
     	} else if (dcTypeLiteralList.contains(type)) { 
			result.addComment("Provided value for dc:type is a valid dc:type literal.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
     	} else { 
			result.addComment("Provided value for dc:type is not a valid dc:type literal.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
     	}
     	
        
        return result;
    }

// TODO: Implementation of ISSUE_ESTABLISHMENTMEANS_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/acc8dff2-d8d1-483a-946d-65a02a452700/2023-09-18 see line: 180
// TODO: Implementation of VALIDATION_LICENSE_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/15f78619-811a-4c6f-997a-a4c7888ad849/2023-09-18 see line: 210
// TODO: Implementation of VALIDATION_DCTYPE_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/374b091a-fc90-4791-91e5-c1557c649169/2023-09-18 see line: 240
// TODO: Implementation of VALIDATION_BASISOFRECORD_STANDARD is not up to date with current version: https://rs.tdwg.org/bdq/terms/42408a00-bf71-4892-a399-4325e2bc1fb8/2023-09-18 see line: 272
    /**
    * Propose amendment to the value of dwc:occurrenceStatus using bdq:sourceAuthority.
    *
    * Provides: AMENDMENT_OCCURRENCESTATUS_STANDARDIZED
    * Version: 2023-09-18
    *
    * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_OCCURRENCESTATUS_STANDARDIZED", description="Propose amendment to the value of dwc:occurrenceStatus using bdq:sourceAuthority.")
    @Provides("f8f3a093-042c-47a3-971a-a482aaaf3b75")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/f8f3a093-042c-47a3-971a-a482aaaf3b75/2023-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:ocurrenceStatus is EMPTY; AMENDED the value of dwc:occurrenceStatus if could be unambiguously interpreted as a value in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority = 'Darwin Core occurrenceStatus' {https://dwc.tdwg.org/list/#dwc_occurrenceStatus} {dwc:occurrenceStatus vocabulary [https://rs.gbif.org/vocabulary/gbif/occurrence_status_2020-07-15.xml]}")
    public DQResponse<AmendmentValue> amendmentOccurrencestatusStandardized(
        @ActedUpon("dwc:occurrenceStatus") String occurrenceStatus
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:ocurrenceStatus 
        // is EMPTY; AMENDED the value of dwc:occurrenceStatus if could 
        // be unambiguously interpreted as a value in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority = "Darwin Core 
        // occurrenceStatus" {https://dwc.tdwg.org/list/#dwc_occurrenceStatus} 
        // {dwc:occurrenceStatus vocabulary [https://rs.gbif.org/vocabulary/gbif/occurrence_status_2020-07-15.xml]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // dwc:occurrenceStatus vocabulary

        return result;
    }

// TODO: Implementation of VALIDATION_OCCURRENCESTATUS_STANDARD is not up to date with current version: https://rs.tdwg.org/bdq/terms/7af25f1e-a4e2-4ff4-b161-d1f25a5c3e47/2023-09-18 see line: 339
// TODO: Implementation of VALIDATION_OCCURRENCESTATUS_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/eb4a17f6-6bea-4cdd-93dd-d5a7e9d1eccf/2023-09-18 see line: 402
    /**
    * Propose amendment to the value of dwc:license using bdq:sourceAuthority.
    *
    * Provides: AMENDMENT_LICENSE_STANDARDIZED
    * Version: 2023-09-18
    *
    * @param license the provided dcterms:license to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_LICENSE_STANDARDIZED", description="Propose amendment to the value of dwc:license using bdq:sourceAuthority.")
    @Provides("dcbe5bd2-42a0-4aab-bb4d-8f148c6490f8")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/dcbe5bd2-42a0-4aab-bb4d-8f148c6490f8/2023-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; AMENDED value of dcterms:license if it could be unambiguously interpreted as a value in bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority default = 'Creative Commons' {[https://creativecommons.org/]} {Creative Commons licenses [https://creativecommons.org/about/cclicenses/]}")
    public DQResponse<AmendmentValue> amendmentLicenseStandardized(
        @ActedUpon("dcterms:license") String license
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; AMENDED value of dcterms:license if it 
        // could be unambiguously interpreted as a value in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED. bdq:sourceAuthority default = "Creative 
        // Commons" {[https://creativecommons.org/]} {Creative Commons 
        // licenses [https://creativecommons.org/about/cclicenses/]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Is there a value in dwc:relationshipOfResource?
    *
    * Provides: #261 VALIDATION_RELATIONSHIPOFRESOURCE_NOTEMPTY
    * Version: 2024-02-07
    *
    * @param relationshipOfResource the provided dwc:relationshipOfResource to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_RELATIONSHIPOFRESOURCE_NOTEMPTY", description="Is there a value in dwc:relationshipOfResource?")
    @Provides("cd281f7e-13b3-43ae-8677-de06ffa70bb4")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/cd281f7e-13b3-43ae-8677-de06ffa70bb4/2024-02-07")
    @Specification("COMPLIANT if dwc:relationshipOfResource is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationRelationshipofresourceNotempty(
        @ActedUpon("dwc:relationshipOfResource") String relationshipOfResource
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:relationshipOfResource is not EMPTY; otherwise 
        // NOT_COMPLIANT 

		if (MetadataUtils.isEmpty(relationshipOfResource)) {
			result.addComment("No value provided for dwc:relationshipOfResource.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dwc:relationshipOfResource.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}
        
        return result;
    }

    /**
    * Is there a value in dwc:reproductiveCondition?
    *
    * Provides: #262 VALIDATION_REPRODUCTIVECONDITION_NOTEMPTY
    * Version: 2024-02-07
    *
    * @param reproductiveCondition the provided dwc:reproductiveCondition to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_REPRODUCTIVECONDITION_NOTEMPTY", description="Is there a value in dwc:reproductiveCondition?")
    @Provides("3eefe72c-4c7d-4dee-89b6-e9d91d3f1981")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/3eefe72c-4c7d-4dee-89b6-e9d91d3f1981/2024-02-07")
    @Specification("COMPLIANT if dwc:reproductiveCondition is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationReproductiveconditionNotempty(
        @ActedUpon("dwc:reproductiveCondition") String reproductiveCondition
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:reproductiveCondition is not EMPTY; otherwise 
        // NOT_COMPLIANT 

		if (MetadataUtils.isEmpty(reproductiveCondition)) {
			result.addComment("No value provided for dwc:reproductiveCondition.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dwc:reproductiveCondition.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}
        
        return result;
    }

    /**
    * Does the value of dwc:establishmentMeans occur in bdq:sourceAuthority?
    *
    * Provides: VALIDATION_ESTABLISHMENTMEANS_STANDARD
    * Version: 2024-02-08
    *
    * @param establishmentMeans the provided dwc:establishmentMeans to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_ESTABLISHMENTMEANS_STANDARD", description="Does the value of dwc:establishmentMeans occur in bdq:sourceAuthority?")
    @Provides("4eb48fdf-7299-4d63-9d08-246902e2857f")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/4eb48fdf-7299-4d63-9d08-246902e2857f/2024-02-08")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:establishmentMeans is EMPTY; COMPLIANT if the value of dwc:establishmentMeans is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core establishmentMeans' {[https://dwc.tdwg.org/list/#dwc_establishmentMeans]} {dwc:establishmentMeans vocabulary API [https://api.gbif.org/v1/vocabularies/EstablishmentMeans/concepts]}")
    public DQResponse<ComplianceValue> validationEstablishmentmeansStandard(
        @ActedUpon("dwc:establishmentMeans") String establishmentMeans
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:establishmentMeans 
        // is EMPTY; COMPLIANT if the value of dwc:establishmentMeans 
        // is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. 
        // bdq:sourceAuthority default = "Darwin Core establishmentMeans" 
        // {[https://dwc.tdwg.org/list/#dwc_establishmentMeans]} {dwc:establishmentMeans 
        // vocabulary API [https://api.gbif.org/v1/vocabularies/EstablishmentMeans/concepts]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Propose amendment to the value of dwc:establishmentMeans using bdq:sourceAuthority.
    *
    * Provides: AMENDMENT_ESTABLISHMENTMEANS_STANDARDIZED
    * Version: 2024-02-08
    *
    * @param establishmentMeans the provided dwc:establishmentMeans to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_ESTABLISHMENTMEANS_STANDARDIZED", description="Propose amendment to the value of dwc:establishmentMeans using bdq:sourceAuthority.")
    @Provides("15d15927-7a22-43f8-88d6-298f5eb45c4c")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/15d15927-7a22-43f8-88d6-298f5eb45c4c/2024-02-08")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:establishmentMeans is EMPTY; AMENDED the value of dwc:establishmentMeans if it can be unambiguously matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority default = 'Darwin Core establishmentMeans' {[https://dwc.tdwg.org/list/#dwc_establishmentMeans]} {dwc:establishmentMeans vocabulary API [https://api.gbif.org/v1/vocabularies/EstablishmentMeans/concepts]}")
    public DQResponse<AmendmentValue> amendmentEstablishmentmeansStandardized(
        @ActedUpon("dwc:establishmentMeans") String establishmentMeans
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:establishmentMeans 
        // is EMPTY; AMENDED the value of dwc:establishmentMeans if 
        // it can be unambiguously matched to a term in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority default = "Darwin 
        // Core establishmentMeans" {[https://dwc.tdwg.org/list/#dwc_establishmentMeans]} 
        // {dwc:establishmentMeans vocabulary API [https://api.gbif.org/v1/vocabularies/EstablishmentMeans/concepts]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Propose amendment to the value of dwc:lifeStage using bdq:sourceAuthority.
    *
    * Provides: AMENDMENT_LIFESTAGE_STANDARDIZED
    * Version: 2024-02-09
    *
    * @param lifeStage the provided dwc:lifeStage to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_LIFESTAGE_STANDARDIZED", description="Propose amendment to the value of dwc:lifeStage using bdq:sourceAuthority.")
    @Provides("07e79079-42e9-48e9-826e-4874ae34bce3")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/07e79079-42e9-48e9-826e-4874ae34bce3/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:lifeStage is EMPTY; AMENDED the value of dwc:lifeStage if it can be unambiguously matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority default = 'Darwin Core lifeStage [https://dwc.tdwg.org/list/#dwc_lifeStage]} {dwc:lifeStage vocabulary API' [https://api.gbif.org/v1/vocabularies/LifeStage/concepts]}")
    public DQResponse<AmendmentValue> amendmentLifestageStandardized(
        @ActedUpon("dwc:lifeStage") String lifeStage
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:lifeStage 
        // is EMPTY; AMENDED the value of dwc:lifeStage if it can be 
        // unambiguously matched to a term in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority default = "Darwin 
        // Core lifeStage [https://dwc.tdwg.org/list/#dwc_lifeStage]} 
        // {dwc:lifeStage vocabulary API" [https://api.gbif.org/v1/vocabularies/LifeStage/concepts]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Does the value of dwc:degreeOfEstablishment occur in bdq:sourceAuthority?
    *
    * Provides: VALIDATION_DEGREEOFESTABLISHMENT_STANDARD
    * Version: 2024-02-09
    *
    * @param degreeOfEstablishment the provided dwc:degreeOfEstablishment to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_DEGREEOFESTABLISHMENT_STANDARD", description="Does the value of dwc:degreeOfEstablishment occur in bdq:sourceAuthority?")
    @Provides("060e7734-607d-4737-8b2c-bfa17788bf1a")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/060e7734-607d-4737-8b2c-bfa17788bf1a/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:degreeOfEstablishment is EMPTY; COMPLIANT if the value of dwc:degreeOfEstablishment is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core degreeOfEstablishment' {[https://dwc.tdwg.org/list/#dwc_degreeOfEstablishment]} {dwc:degreeOfEstablishment vocabulary API [https://api.gbif.org/v1/vocabularies/DegreeOfEstablishment/concepts]}")
    public DQResponse<ComplianceValue> validationDegreeofestablishmentStandard(
        @ActedUpon("dwc:degreeOfEstablishment") String degreeOfEstablishment
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:degreeOfEstablishment 
        // is EMPTY; COMPLIANT if the value of dwc:degreeOfEstablishment 
        // is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. 
        // bdq:sourceAuthority default = "Darwin Core degreeOfEstablishment" 
        // {[https://dwc.tdwg.org/list/#dwc_degreeOfEstablishment]} 
        // {dwc:degreeOfEstablishment vocabulary API [https://api.gbif.org/v1/vocabularies/DegreeOfEstablishment/concepts]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Propose amendment to the value of dwc:degreeOfEstablishment using bdq:sourceAuthority.
    *
    * Provides: AMENDMENT_DEGREEOFESTABLISHMENT_STANDARDIZED
    * Version: 2024-02-09
    *
    * @param degreeOfEstablishment the provided dwc:degreeOfEstablishment to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_DEGREEOFESTABLISHMENT_STANDARDIZED", description="Propose amendment to the value of dwc:degreeOfEstablishment using bdq:sourceAuthority.")
    @Provides("74ef1034-e289-4596-b5b0-cde73796697d")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/74ef1034-e289-4596-b5b0-cde73796697d/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:degreeOfEstablishment is EMPTY; AMENDED the value of dwc:degreeOfEstablishment if it can be unambiguously matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority default = 'Darwin Core degreeOfEstablishment' {[https://dwc.tdwg.org/list/#dwc_degreeOfEstablishment]} {dwc:degreeOfEstablishment vocabulary API [https://api.gbif.org/v1/vocabularies/DegreeOfEstablishment/concepts]}")
    public DQResponse<AmendmentValue> amendmentDegreeofestablishmentStandardized(
        @ActedUpon("dwc:degreeOfEstablishment") String degreeOfEstablishment
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:degreeOfEstablishment 
        // is EMPTY; AMENDED the value of dwc:degreeOfEstablishment 
        // if it can be unambiguously matched to a term in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority default = "Darwin 
        // Core degreeOfEstablishment" {[https://dwc.tdwg.org/list/#dwc_degreeOfEstablishment]} 
        // {dwc:degreeOfEstablishment vocabulary API [https://api.gbif.org/v1/vocabularies/DegreeOfEstablishment/concepts]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Does the value of dwc:pathway occur in bdq:sourceAuthority?
    *
    * Provides: VALIDATION_PATHWAY_STANDARD
    * Version: 2024-02-09
    *
    * @param pathway the provided dwc:pathway to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_PATHWAY_STANDARD", description="Does the value of dwc:pathway occur in bdq:sourceAuthority?")
    @Provides("5424e933-bee7-4125-839e-d8743ea69f93")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/5424e933-bee7-4125-839e-d8743ea69f93/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:pathway is EMPTY; COMPLIANT if the value of dwc:pathway is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core pathway' {[https://dwc.tdwg.org/list/#dwc_pathway]} {dwc:pathway vocabulary API [https://api.gbif.org/v1/vocabularies/Pathway/concepts]}")
    public DQResponse<ComplianceValue> validationPathwayStandard(
        @ActedUpon("dwc:pathway") String pathway
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:pathway 
        // is EMPTY; COMPLIANT if the value of dwc:pathway is in the 
        // bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority 
        // default = "Darwin Core pathway" {[https://dwc.tdwg.org/list/#dwc_pathway]} 
        // {dwc:pathway vocabulary API [https://api.gbif.org/v1/vocabularies/Pathway/concepts]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Propose amendment to the value of dwc:pathway using bdq:sourceAuthority.
    *
    * Provides: AMENDMENT_PATHWAY_STANDARDIZED
    * Version: 2024-02-09
    *
    * @param pathway the provided dwc:pathway to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_PATHWAY_STANDARDIZED", description="Propose amendment to the value of dwc:pathway using bdq:sourceAuthority.")
    @Provides("f9205977-f145-44f5-8cb9-e3e2e35ce908")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/f9205977-f145-44f5-8cb9-e3e2e35ce908/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:pathway is EMPTY; AMENDED the value of dwc:pathway if it can be unambiguously matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority default = 'Darwin Core pathway' {[https://dwc.tdwg.org/list/#dwc_pathway]} {dwc:pathway vocabulary API [https://api.gbif.org/v1/vocabularies/Pathway/concepts]}")
    public DQResponse<AmendmentValue> amendmentPathwayStandardized(
        @ActedUpon("dwc:pathway") String pathway
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:pathway 
        // is EMPTY; AMENDED the value of dwc:pathway if it can be 
        // unambiguously matched to a term in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority default = "Darwin 
        // Core pathway" {[https://dwc.tdwg.org/list/#dwc_pathway]} 
        // {dwc:pathway vocabulary API [https://api.gbif.org/v1/vocabularies/Pathway/concepts]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Does the value of dwc:preparations occur in bdq:sourceAuthority?
    *
    * Provides: VALIDATION_PREPARATIONS_STANDARD
    * Version: 2024-02-08
    *
    * @param preparations the provided dwc:preparations to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_PREPARATIONS_STANDARD", description="Does the value of dwc:preparations occur in bdq:sourceAuthority?")
    @Provides("4758ea74-d2d3-4656-b17b-4b30147af4dc")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/4758ea74-d2d3-4656-b17b-4b30147af4dc/2024-02-08")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:preparations is EMPTY; COMPLIANT if the value of each element in the list of elements in dwc:preparations is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core Preparations {[https://dwc.tdwg.org/list/#dwc_preparations]} {dwc:preparations vocabulary API [NO CURRENT API EXISTS]}")
    public DQResponse<ComplianceValue> validationPreparationsStandard(
        @ActedUpon("dwc:preparations") String preparations
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:preparations 
        // is EMPTY; COMPLIANT if the value of each element in the 
        // list of elements in dwc:preparations is in the bdq:sourceAuthority; 
        // otherwise NOT_COMPLIANT. bdq:sourceAuthority default = "Darwin 
        // Core Preparations {[https://dwc.tdwg.org/list/#dwc_preparations]} 
        // {dwc:preparations vocabulary API [NO CURRENT API EXISTS]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Does the value of dwc:sex occur in bdq:sourceAuthority?
    *
    * Provides: VALIDATION_SEX_STANDARD
    * Version: 2024-02-09
    *
    * @param sex the provided dwc:sex to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_SEX_STANDARD", description="Does the value of dwc:sex occur in bdq:sourceAuthority?")
    @Provides("88d8598b-3318-483d-9475-a5acf9887404")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/88d8598b-3318-483d-9475-a5acf9887404/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:sex is EMPTY; COMPLIANT if the value of dwc:sex is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core sex' {[https://dwc.tdwg.org/list/#dwc_sex]} {dwc:sex vocabulary API [NO CURRENT API EXISTS]}")
    public DQResponse<ComplianceValue> validationSexStandard(
        @ActedUpon("dwc:sex") String sex
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:sex 
        // is EMPTY; COMPLIANT if the value of dwc:sex is in the bdq:sourceAuthority; 
        // otherwise NOT_COMPLIANT. bdq:sourceAuthority default = "Darwin 
        // Core sex" {[https://dwc.tdwg.org/list/#dwc_sex]} {dwc:sex 
        // vocabulary API [NO CURRENT API EXISTS]} 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Propose amendment to the value of dwc:sex using bdq:sourceAuthority.
    *
    * Provides: AMENDMENT_SEX_STANDARDIZED
    * Version: 2024-02-09
    *
    * @param sex the provided dwc:sex to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_SEX_STANDARDIZED", description="Propose amendment to the value of dwc:sex using bdq:sourceAuthority.")
    @Provides("33c45ae1-e2db-462a-a59e-7169bb01c5d6")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/33c45ae1-e2db-462a-a59e-7169bb01c5d6/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:sex is EMPTY; AMENDED the value of dwc:sex if it can be unambiguously matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority default = 'Darwin Core sex' {[https://dwc.tdwg.org/list/#dwc_sex]} {dwc:sex vocabulary API [NO CURRENT API EXISTS]}")
    public DQResponse<AmendmentValue> amendmentSexStandardized(
        @ActedUpon("dwc:sex") String sex
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:sex 
        // is EMPTY; AMENDED the value of dwc:sex if it can be unambiguously 
        // matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED 
        // bdq:sourceAuthority default = "Darwin Core sex" {[https://dwc.tdwg.org/list/#dwc_sex]} 
        // {dwc:sex vocabulary API [NO CURRENT API EXISTS]} 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Does the value of dwc:typeStatus occur in bdq:sourceAuthority?
    *
    * Provides: VALIDATION_TYPESTATUS_STANDARD
    * Version: 2024-02-09
    *
    * @param typeStatus the provided dwc:typeStatus to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_TYPESTATUS_STANDARD", description="Does the value of dwc:typeStatus occur in bdq:sourceAuthority?")
    @Provides("4833a522-12eb-4fe0-b4cf-7f7a337a6048")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/4833a522-12eb-4fe0-b4cf-7f7a337a6048/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:typeStatus is EMPTY; COMPLIANT if the value of dwc:typeStatus is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core typeStatus' {[https://dwc.tdwg.org/list/#dwc_typeStatus]} {dwc:typeStatus vocabulary API [(https://gbif.github.io/parsers/apidocs/org/gbif/api/vocabulary/TypeStatus.html]}")
    public DQResponse<ComplianceValue> validationTypestatusStandard(
        @ActedUpon("dwc:typeStatus") String typeStatus
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:typeStatus 
        // is EMPTY; COMPLIANT if the value of dwc:typeStatus is in 
        // the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority 
        // default = "Darwin Core typeStatus" {[https://dwc.tdwg.org/list/#dwc_typeStatus]} 
        // {dwc:typeStatus vocabulary API [(https://gbif.github.io/parsers/apidocs/org/gbif/api/vocabulary/TypeStatus.html]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Propose amendment to the value of dwc:typeStatus using bdq:sourceAuthority.
    *
    * Provides: AMENDMENT_TYPESTATUS_STANDARDIZED
    * Version: 2024-02-09
    *
    * @param typeStatus the provided dwc:typeStatus to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_TYPESTATUS_STANDARDIZED", description="Propose amendment to the value of dwc:typeStatus using bdq:sourceAuthority.")
    @Provides("b3471c65-b53e-453b-8282-abfa27bf1805")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/b3471c65-b53e-453b-8282-abfa27bf1805/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:typeStatus is EMPTY; AMENDED the value of dwc:typeStatus if it can be unambiguously matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority default = 'Darwin Core typeStatus' {[https://dwc.tdwg.org/list/#dwc_typeStatus]} {dwc:typeStatus vocabulary API [(https://gbif.github.io/parsers/apidocs/org/gbif/api/vocabulary/TypeStatus.html]}")
    public DQResponse<AmendmentValue> amendmentTypestatusStandardized(
        @ActedUpon("dwc:typeStatus") String typeStatus
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:typeStatus 
        // is EMPTY; AMENDED the value of dwc:typeStatus if it can 
        // be unambiguously matched to a term in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority default = "Darwin 
        // Core typeStatus" {[https://dwc.tdwg.org/list/#dwc_typeStatus]} 
        // {dwc:typeStatus vocabulary API [(https://gbif.github.io/parsers/apidocs/org/gbif/api/vocabulary/TypeStatus.html]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Is there a value in dwc:degreeOfEstablishment?
    *
    * Provides: #288 VALIDATION_DEGREEOFESTABLISHMENT_NOTEMPTY
    * Version: 2024-02-10
    *
    * @param degreeOfEstablishment the provided dwc:degreeOfEstablishment to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_DEGREEOFESTABLISHMENT_NOTEMPTY", description="Is there a value in dwc:degreeOfEstablishment?")
    @Provides("0fa16c7e-eb9c-4add-9193-aca6087d6636")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/0fa16c7e-eb9c-4add-9193-aca6087d6636/2024-02-10")
    @Specification("COMPLIANT if dwc:degreeOfEstablishment is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationDegreeofestablishmentNotempty(
        @ActedUpon("dwc:degreeOfEstablishment") String degreeOfEstablishment
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:degreeOfEstablishment is not EMPTY; otherwise 
        // NOT_COMPLIANT 
        
		if (MetadataUtils.isEmpty(degreeOfEstablishment)) {
			result.addComment("No value provided for dwc:degreeOfEstablishment.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dwc:degreeOfEstablishment.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}

        return result;
    }

    /**
    * Is there a value in dwc:pathway?
    *
    * Provides: #289 VALIDATION_PATHWAY_NOTEMPTY
    * Version: 2024-02-10
    *
    * @param pathway the provided dwc:pathway to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_PATHWAY_NOTEMPTY", description="Is there a value in dwc:pathway?")
    @Provides("fffdc42b-b15e-4450-9e6a-f4181a319106")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/fffdc42b-b15e-4450-9e6a-f4181a319106/2024-02-10")
    @Specification("COMPLIANT if dwc:pathway is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationPathwayNotempty(
        @ActedUpon("dwc:pathway") String pathway
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:pathway is not EMPTY; otherwise NOT_COMPLIANT 
        // 

		if (MetadataUtils.isEmpty(pathway)) {
			result.addComment("No value provided for dwc:pathway.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dwc:pathway.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}
        
        return result;
    }

// TODO: Implementation of ISSUE_DATAGENERALIZATIONS_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/13d5a10e-188e-40fd-a22c-dbaa87b91df2/2023-09-18 see line: 87
    /**
    * Is dwc:individualCount an Integer ?
    *
    * Provides: VALIDATION_INDIVIDUALCOUNT_INTEGER
    * Version: 2024-02-11
    *
    * @param individualCount the provided dwc:individualCount to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_INDIVIDUALCOUNT_INTEGER", description="Is dwc:individualCount an Integer ?")
    @Provides("43abded0-c3bf-44e7-8c1f-c4207608b1fa")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/43abded0-c3bf-44e7-8c1f-c4207608b1fa/2024-02-11")
    @Specification("COMPLIANT if the value of dwc:individualCount is interpretable an integer; otherwise NOT_COMPLIANT. ")
    public DQResponse<ComplianceValue> validationIndividualcountInteger(
        @ActedUpon("dwc:individualCount") String individualCount
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // COMPLIANT if the value of dwc:individualCount is interpretable 
        // an integer; otherwise NOT_COMPLIANT. 

        return result;
    }


    /**
    * Propose amendment to the value of dwc:preparations using bdq:sourceAuthority.
    *
    * Provides: AMENDMENT_REPRODUCTIVECONDITION_STANDARDIZED
    * Version: 2024-02-09
    *
    * @param reproductiveCondition the provided dwc:reproductiveCondition to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_REPRODUCTIVECONDITION_STANDARDIZED", description="Propose amendment to the value of dwc:preparations using bdq:sourceAuthority.")
    @Provides("10ef7660-3e08-4b74-95d3-66131275cf31")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/10ef7660-3e08-4b74-95d3-66131275cf31/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:reproductiveCondition is EMPTY; AMENDED the value of dwc:reproductiveCondition if it can be unambiguously matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority default = 'Darwin Core reproductionCondition' {[https://dwc.tdwg.org/list/#dwc_reproductiveCondition]} {dwc:reproductiveCondition vocabulary API [NO CURRENT API EXISTS]}")
    public DQResponse<AmendmentValue> amendmentReproductiveconditionStandardized(
        @ActedUpon("dwc:reproductiveCondition") String reproductiveCondition
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:reproductiveCondition 
        // is EMPTY; AMENDED the value of dwc:reproductiveCondition 
        // if it can be unambiguously matched to a term in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority default = "Darwin 
        // Core reproductionCondition" {[https://dwc.tdwg.org/list/#dwc_reproductiveCondition]} 
        // {dwc:reproductiveCondition vocabulary API [NO CURRENT API 
        // EXISTS]} 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Propose amendment to the value of dwc:preparations using bdq:sourceAuthority.
    *
    * Provides: AMENDMENT_PREPARATIONS_STANDARDIZED
    * Version: 2024-02-09
    *
    * @param preparations the provided dwc:preparations to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_PREPARATIONS_STANDARDIZED", description="Propose amendment to the value of dwc:preparations using bdq:sourceAuthority.")
    @Provides("8d0cbfee-4524-4da3-83d4-bebc4c4f7cd2")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/8d0cbfee-4524-4da3-83d4-bebc4c4f7cd2/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:preparations is EMPTY; AMENDED the value of dwc:preparations if at least one item in the list of preparations, comparing and amending item by item in the list, ignoring leading or trailing whitespace in each item,  can be unambiguously matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority default = 'Darwin Core preparations {[https://dwc.tdwg.org/list/#dwc_preparations]} {dwc:preparations vocabulary API [NO CURRENT API EXISTS]}")
    public DQResponse<AmendmentValue> amendmentPreparationsStandardized(
        @ActedUpon("dwc:preparations") String preparations
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:preparations 
        // is EMPTY; AMENDED the value of dwc:preparations if at least 
        // one item in the list of preparations, comparing and amending 
        // item by item in the list, ignoring leading or trailing whitespace 
        // in each item, can be unambiguously matched to a term in 
        // bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority 
        // default = "Darwin Core preparations {[https://dwc.tdwg.org/list/#dwc_preparations]} 
        // {dwc:preparations vocabulary API [NO CURRENT API EXISTS]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Does the value of dcterms:modified a valid ISO date?
    *
    * Provides: VALIDATION_MODIFIED_STANDARD
    * Version: 2024-02-08
    *
    * @param modified the provided dcterms:modified to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_MODIFIED_STANDARD", description="Does the value of dcterms:modified a valid ISO date?")
    @Provides("c253f11a-6161-4692-bfce-4328f1961630")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/c253f11a-6161-4692-bfce-4328f1961630/2024-02-08")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dcterms:modified is EMPTY; COMPLIANT if the value of dcterms:modified is a valid ISO 8601-1 date; otherwise NOT_COMPLIANT. ")
    public DQResponse<ComplianceValue> validationModifiedStandard(
        @ActedUpon("dcterms:modified") String modified
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dcterms:modified is EMPTY; 
        // COMPLIANT if the value of dcterms:modified is a valid ISO 
        // 8601-1 date; otherwise NOT_COMPLIANT. 

        return result;
    }

    /**
    * Does the value of dwc:lifeStage occur in bdq:sourceAuthority?
    *
    * Provides: VALIDATION_LIFESTAGE_STANDARD
    * Version: 2024-02-09
    *
    * @param lifeStage the provided dwc:lifeStage to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_LIFESTAGE_STANDARD", description="Does the value of dwc:lifeStage occur in bdq:sourceAuthority?")
    @Provides("be40d19e-1fe7-42ed-b9d0-961f4cf3eb6a")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/be40d19e-1fe7-42ed-b9d0-961f4cf3eb6a/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:lifeStage is EMPTY; COMPLIANT if the value of dwc:lifeStage is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core lifeStage [https://dwc.tdwg.org/list/#dwc_lifeStage]} {dwc:lifeStage vocabulary' [https://api.gbif.org/v1/vocabularies/LifeStage/concepts]}")
    public DQResponse<ComplianceValue> validationLifestageStandard(
        @ActedUpon("dwc:lifeStage") String lifeStage
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:lifeStage 
        // is EMPTY; COMPLIANT if the value of dwc:lifeStage is in 
        // the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority 
        // default = "Darwin Core lifeStage [https://dwc.tdwg.org/list/#dwc_lifeStage]} 
        // {dwc:lifeStage vocabulary" [https://api.gbif.org/v1/vocabularies/LifeStage/concepts]} 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
    * Is there a value in dwc:sex?
    *
    * Provides: #263 VALIDATION_SEX_NOTEMPTY
    * Version: 2024-02-07
    *
    * @param sex the provided dwc:sex to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_SEX_NOTEMPTY", description="Is there a value in dwc:sex?")
    @Provides("76067adf-1422-4d03-89e3-9067c3043700")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/76067adf-1422-4d03-89e3-9067c3043700/2024-02-07")
    @Specification("COMPLIANT if dwc:sex is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationSexNotempty(
        @ActedUpon("dwc:sex") String sex
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:sex is not EMPTY; otherwise NOT_COMPLIANT 
        // 
		if (MetadataUtils.isEmpty(sex)) {
			result.addComment("No value provided for dwc:sex.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dwc:sex.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}

        return result;
    }


    /**
    * Is there a value in dwc:preparations?
    *
    * Provides: #260 VALIDATION_PREPARATIONS_NOTEMPTY
    * Version: 2024-02-07
    *
    * @param preparations the provided dwc:preparations to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_PREPARATIONS_NOTEMPTY", description="Is there a value in dwc:preparations?")
    @Provides("2aa1b7a0-0473-4a90-bf11-a02137c5c65b")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/2aa1b7a0-0473-4a90-bf11-a02137c5c65b/2024-02-07")
    @Specification("COMPLIANT if dwc:preparations is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationPreparationsNotempty(
        @ActedUpon("dwc:preparations") String preparations
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:preparations is not EMPTY; otherwise NOT_COMPLIANT 
        
		if (MetadataUtils.isEmpty(preparations)) {
			result.addComment("No value provided for dwc:preparations.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dwc:preparations.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}

        return result;
    }

    /**
    * Is there a value in dwc:recordNumber?
    *
    * Provides: #243 VALIDATION_RECORDNUMBER_NOTEMPTY
    * Version: 2024-02-04
    *
    * @param recordNumber the provided dwc:recordNumber to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_RECORDNUMBER_NOTEMPTY", description="Is there a value in dwc:recordNumber?")
    @Provides("3bd2477c-6497-43b0-94e6-b811eed1b1cb")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/3bd2477c-6497-43b0-94e6-b811eed1b1cb/2024-02-04")
    @Specification("COMPLIANT if dwc:recordNumber is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationRecordnumberNotempty(
        @ActedUpon("dwc:recordNumber") String recordNumber
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:recordNumber is not EMPTY; otherwise NOT_COMPLIANT 
        
		if (MetadataUtils.isEmpty(recordNumber)) {
			result.addComment("No value provided for dwc:recordNumber.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dwc:recordNumber.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}

        return result;
    }

    /**
    * Is there a value in dwc:recordedBy?
    *
    * Provides: #242 VALIDATION_RECORDEDBY_NOTEMPTY
    * Version: 2024-02-04
    *
    * @param recordedBy the provided dwc:recordedBy to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_RECORDEDBY_NOTEMPTY", description="Is there a value in dwc:recordedBy?")
    @Provides("986ad95d-ffa1-4e3b-a6cb-ed943c87be0d")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/986ad95d-ffa1-4e3b-a6cb-ed943c87be0d/2024-02-04")
    @Specification("COMPLIANT if dwc:recordedBy is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationRecordedbyNotempty(
        @ActedUpon("dwc:recordedBy") String recordedBy
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:recordedBy is not EMPTY; otherwise NOT_COMPLIANT 
         
		if (MetadataUtils.isEmpty(recordedBy)) {
			result.addComment("No value provided for dwc:recordedBy.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dwc:recordedBy.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}
        
        return result;
    }

    /**
    * Is there a value in dwc:lifeStage?
    *
    * Provides: VALIDATION_LIFESTAGE_NOTEMPTY
    * Version: 2024-01-29
    *
    * @param lifeStage the provided dwc:lifeStage to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_LIFESTAGE_NOTEMPTY", description="Is there a value in dwc:lifeStage?")
    @Provides("34b9eec9-03d5-4dc9-94b7-5b05ddcaaa87")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/34b9eec9-03d5-4dc9-94b7-5b05ddcaaa87/2024-01-29")
    @Specification("COMPLIANT if dwc:lifeStage is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationLifestageNotempty(
        @ActedUpon("dwc:lifeStage") String lifeStage
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:lifeStage is not EMPTY; otherwise NOT_COMPLIANT 
        
		if (MetadataUtils.isEmpty(lifeStage)) {
			result.addComment("No value provided for dwc:lifeStage.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dwc:lifeStage.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}
        
        return result;
    }

    /**
    * Is there a value in dwc:individualCount?
    *
    * Provides: VALIDATION_INDIVIDUALCOUNT_NOTEMPTY
    * Version: 2024-01-29
    *
    * @param individualCount the provided dwc:individualCount to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_INDIVIDUALCOUNT_NOTEMPTY", description="Is there a value in dwc:individualCount?")
    @Provides("aff0facd-1d2a-40a5-a55a-61f950cd68a0")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/aff0facd-1d2a-40a5-a55a-61f950cd68a0/2024-01-29")
    @Specification("COMPLIANT if dwc:individualCount is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationIndividualcountNotempty(
        @ActedUpon("dwc:individualCount") String individualCount
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:individualCount is not EMPTY; otherwise 
        // NOT_COMPLIANT 

		if (MetadataUtils.isEmpty(individualCount)) {
			result.addComment("No value provided for dwc:individualCount.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dwc:individualCount.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}
        
        return result;
    }

    /**
    * Is there a value in dwc:disposition?
    *
    * Provides: VALIDATION_DISPOSITION_NOTEMPTY
    * Version: 2024-01-29
    *
    * @param disposition the provided dwc:disposition to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_DISPOSITION_NOTEMPTY", description="Is there a value in dwc:disposition?")
    @Provides("b4c17611-2703-474f-b46a-93b08ecfee16")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/b4c17611-2703-474f-b46a-93b08ecfee16/2024-01-29")
    @Specification("COMPLIANT if dwc:disposition is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationDispositionNotempty(
        @ActedUpon("dwc:disposition") String disposition
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:disposition is not EMPTY; otherwise NOT_COMPLIANT 
        
		if (MetadataUtils.isEmpty(disposition)) {
			result.addComment("No value provided for dwc:disposition.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dwc:disposition.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}

        return result;
    }

    /**
    * Is there a value in dcterms:modified?
    *
    * Provides: #224 VALIDATION_MODIFIED_NOTEMPTY
    * Version: 2024-01-29
    *
    * @param modified the provided dcterms:modified to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_MODIFIED_NOTEMPTY", description="Is there a value in dcterms:modified?")
    @Provides("e17918fc-25ca-4a3a-828b-4502432b98c4")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/e17918fc-25ca-4a3a-828b-4502432b98c4/2024-01-29")
    @Specification("COMPLIANT if dcterms:modified is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationModifiedNotempty(
        @ActedUpon("dcterms:modified") String modified
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dcterms:modified is not EMPTY; otherwise NOT_COMPLIANT 

		if (MetadataUtils.isEmpty(modified)) {
			result.addComment("No value provided for dcterms:modified.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dcterms:modified.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}
        
        return result;
    }

}
