/**
 * DwCMetadataDQ.java
 *
 * Copyright 2022-2024 President and Fellows of Harvard College
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

import java.net.URI;
import java.net.URISyntaxException;
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
import org.filteredpush.qc.metadata.util.LSID;
import org.filteredpush.qc.metadata.util.MetadataSingleton;
import org.filteredpush.qc.metadata.util.MetadataUtils;
import org.filteredpush.qc.metadata.util.RFC8141URN;
import org.filteredpush.qc.metadata.util.URNFormatException;

/**
 * Provides implementation of TDWG BDQ TG2 OTHER tests (related to metadata found in Record-level
 * terms and Occurrence terms).
 * 
 * CORE tests: 
 * #72	13d5a10e-188e-40fd-a22c-dbaa87b91df2	ISSUE_DATAGENERALIZATIONS_NOTEMPTY 
 * #94	acc8dff2-d8d1-483a-946d-65a02a452700	ISSUE_ESTABLISHMENTMEANS_NOTEMPTY
 * #47	c486546c-e6e5-48a7-b286-eba7f5ca56c4	VALIDATION_OCCURRENCEID_NOTEMPTY
 * #99	15f78619-811a-4c6f-997a-a4c7888ad849	VALIDATION_LICENSE_NOTEMPTY
 * #38	3136236e-04b6-49ea-8b34-a65f25e3aba1	VALIDATION_LICENSE_STANDARD
 * #133 dcbe5bd2-42a0-4aab-bb4d-8f148c6490f8	AMENDMENT_LICENSE_STANDARDIZED
 * #91	cdaabb0d-a863-49d0-bc0f-738d771acba5	VALIDATION_DCTYPE_STANDARD
 * #103	374b091a-fc90-4791-91e5-c1557c649169	VALIDATION_DCTYPE_NOTEMPTY
 * #41	bd385eeb-44a2-464b-a503-7abe407ef904	AMENDMENT_DCTYPE_STANDARDIZED
 * #58	ac2b7648-d5f9-48ca-9b07-8ad5879a2536	VALIDATION_BASISOFRECORD_NOTEMPTY
 * #104 42408a00-bf71-4892-a399-4325e2bc1fb8	VALIDATION_BASISOFRECORD_STANDARD 
 * 63	07c28ace-561a-476e-a9b9-3d5ad6e35933	AMENDMENT_BASISOFRECORD_STANDARDIZED
 * #117	eb4a17f6-6bea-4cdd-93dd-d5a7e9d1eccf	VALIDATION_OCCURRENCESTATUS_NOTEMPTY
 * #116	7af25f1e-a4e2-4ff4-b161-d1f25a5c3e47	VALIDATION_OCCURRENCESTATUS_STANDARD
 * #75	96667a0a-ae59-446a-bbb0-b7f2b0ca6cf5	AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT
 * #115	f8f3a093-042c-47a3-971a-a482aaaf3b75	AMENDMENT_OCCURRENCESTATUS_STANDARDIZED
 * #277 5424e933-bee7-4125-839e-d8743ea69f93	VALIDATION_PATHWAY_STANDARD
 * #278 f9205977-f145-44f5-8cb9-e3e2e35ce908	AMENDMENT_PATHWAY_STANDARDIZED
 * #283 88d8598b-3318-483d-9475-a5acf9887404	VALIDATION_SEX_STANDARD 
 * #284 33c45ae1-e2db-462a-a59e-7169bb01c5d6	AMENDMENT_SEX_STANDARDIZED
 * #275 060e7734-607d-4737-8b2c-bfa17788bf1a	VALIDATION_DEGREEOFESTABLISHMENT_STANDARD
 * #276 74ef1034-e289-4596-b5b0-cde73796697d	AMENDMENT_DEGREEOFESTABLISHMENT_STANDARDIZED
 * #268 4eb48fdf-7299-4d63-9d08-246902e2857f	VALIDATION_ESTABLISHMENTMEANS_STANDARD
 * #269 15d15927-7a22-43f8-88d6-298f5eb45c4c	AMENDMENT_ESTABLISHMENTMEANS_STANDARDIZED
 * 
 * CORE Tests under consideration for reworking or removal: 
 * #285 4833a522-12eb-4fe0-b4cf-7f7a337a6048 	VALIDATION_TYPESTATUS_STANDARD
 * #286 b3471c65-b53e-453b-8282-abfa27bf1805	AMENDMENT_TYPESTATUS_STANDARDIZED
 * 
 * TODO: Implement:
 * 
 * 29	fecaa8a3-bbd8-4c5a-a424-13c37c4bb7b1	ISSUE_ANNOTATION_NOTEMPTY
 *
 * And the following supplementary tests: 
 * 
 * #23  VALIDATION_OCCURRENCEID_STANDARD 3cfe9ab4-79f8-4afd-8da5-723183ef16a3
 * #235 VALIDATION_LIFESTAGE_NOTEMPTY 34b9eec9-03d5-4dc9-94b7-5b05ddcaaa87
 * #270 VALIDATION_LIFESTAGE_STANDARD be40d19e-1fe7-42ed-b9d0-961f4cf3eb6a
 * #271 AMENDMENT_LIFESTAGE_STANDARDIZED 07e79079-42e9-48e9-826e-4874ae34bce3	
 * #225 VALIDATION_DISPOSITION_NOTEMPTY b4c17611-2703-474f-b46a-93b08ecfee16
 * #232 VALIDATION_INDIVIDUALCOUNT_NOTEMPTY aff0facd-1d2a-40a5-a55a-61f950cd68a0
 * #290 VALIDATION_INDIVIDUALCOUNT_INTEGER 43abded0-c3bf-44e7-8c1f-c4207608b1fa
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
@Mechanism(value="09fd4e2f-cf10-4665-aa74-bcf8e3795163",label="Kurator: Metadata/Record-Level Validator - DwCMetadataDQ:v1.1.1-SNAPSHOT")
public class DwCMetadataDQ {

	private static final Log logger = LogFactory.getLog(DwCMetadataDQ.class);
    
	/**
	 * a list of dc:type literal values.
	 */
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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/13d5a10e-188e-40fd-a22c-dbaa87b91df2/2023-09-18")
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
	
    
    /**
     * Is there a value in dwc:occurrenceID?
     *
     * Provides: #47 VALIDATION_OCCURRENCEID_NOTEMPTY
     * Version: 2023-09-17
     *
     * @param occurrenceID the provided dwc:occurrenceID to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_OCCURRENCEID_NOTEMPTY", description="Is there a value in dwc:occurrenceID?")
    @Provides("c486546c-e6e5-48a7-b286-eba7f5ca56c4")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/c486546c-e6e5-48a7-b286-eba7f5ca56c4/2023-09-17")
    @Specification("COMPLIANT if dwc:occurrenceID is bdq:NotEmpty; otherwise NOT_COMPLIANT. ")
    public static DQResponse<ComplianceValue> validationOccurrenceidNotempty(@ActedUpon("dwc:occurrenceID") String occurrenceID) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
         // COMPLIANT if dwc:occurrenceID is bdq:NotEmpty; otherwise 
         // NOT_COMPLIANT 
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
     * Provides: 58 VALIDATION_BASISOFRECORD_NOTEMPTY
     * Version: 2023-09-17
     *
     * @param basisOfRecord the provided dwc:basisOfRecord to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_BASISOFRECORD_NOTEMPTY", description="Is there a value in dwc:basisOfRecord?")
    @Provides("ac2b7648-d5f9-48ca-9b07-8ad5879a2536")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/ac2b7648-d5f9-48ca-9b07-8ad5879a2536/2023-09-17")
    @Specification("COMPLIANT if dwc:basisOfRecord is bdq:NotEmpty; otherwise NOT_COMPLIANT. ")
    public static DQResponse<ComplianceValue> validationBasisofrecordNotempty(@ActedUpon("dwc:basisOfRecord") String basisOfRecord) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:basisOfRecord is bdq:NotEmpty; otherwise 
        // NOT_COMPLIANT 
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
     * Provides: 94 ISSUE_ESTABLISHMENTMEANS_NOTEMPTY
     * Version: 2023-09-18
     *
     * @param establishmentMeans the provided dwc:establishmentMeans to evaluate
     * @return DQResponse the response of type IssueValue to return
     */
    @Issue(label="ISSUE_ESTABLISHMENTMEANS_NOTEMPTY", description="Is there a value in dwc:establishmentMeans?")
    @Provides("acc8dff2-d8d1-483a-946d-65a02a452700")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/acc8dff2-d8d1-483a-946d-65a02a452700/2023-09-18")
    @Specification("POTENTIAL_ISSUE if dwc:establishmentMeans is bdq:NotEmpty; otherwise NOT_ISSUE. ")
    public static DQResponse<IssueValue> issueEstablishmentmeansNotempty(@ActedUpon("dwc:establishmentMeans") String establishmentMeans) {
        DQResponse<IssueValue> result = new DQResponse<IssueValue>();

        // Specification
        // POTENTIAL_ISSUE if dwc:establishmentMeans is bdq:NotEmpty; 
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
     * Provides: 99 VALIDATION_LICENSE_NOTEMPTY
     * Version: 2023-09-18
     *
     * @param license the provided dcterms:license to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_LICENSE_NOTEMPTY", description="Is there a value in dcterms:license?")
    @Provides("15f78619-811a-4c6f-997a-a4c7888ad849")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/15f78619-811a-4c6f-997a-a4c7888ad849/2023-09-18")
    @Specification("COMPLIANT if dcterms:license is bdq:NotEmpty; otherwise NOT_COMPLIANT. ")
    public static DQResponse<ComplianceValue> validationLicenseNotempty(@ActedUpon("dcterms:license") String license) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dcterms:license is bdq:NotEmpty; otherwise 
        // NOT_COMPLIANT 
        // 

		if (MetadataUtils.isEmpty(license)) {
			result.addComment("No value provided for dcterms:license.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dcterms:license.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}
        
        return result;
    }

    /**
     * Is there a value in dc:type?
     *
     * Provides: 103 VALIDATION_DCTYPE_NOTEMPTY
     * Version: 2023-09-18
     *
     * @param type the provided dc:type to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DCTYPE_NOTEMPTY", description="Is there a value in dc:type?")
    @Provides("374b091a-fc90-4791-91e5-c1557c649169")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/374b091a-fc90-4791-91e5-c1557c649169/2023-09-18")
    @Specification("COMPLIANT if dc:type is bdq:NotEmpty; otherwise NOT_COMPLIANT. ")
    public static DQResponse<ComplianceValue> validationDctypeNotempty(@ActedUpon("dc:type") String type) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dc:type is bdq:NotEmpty; otherwise NOT_COMPLIANT 
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
     * Does the value of dwc:basisOfRecord occur in the bdq:sourceAuthority?
     *
     * Provides: #104 VALIDATION_BASISOFRECORD_STANDARD
     * Version: 2024-08-18
     *
     * @param basisOfRecord the provided dwc:basisOfRecord to evaluate
     * @param sourceAuthority the source authority for basis of record values to evaluate against, use null for default value.
     * @return DQResponse the response of type ComplianceValue to return
     */
    @Validation(label="VALIDATION_BASISOFRECORD_STANDARD", description="Does the value of dwc:basisOfRecord occur in the bdq:sourceAuthority?")
    @Provides("42408a00-bf71-4892-a399-4325e2bc1fb8")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/42408a00-bf71-4892-a399-4325e2bc1fb8/2024-08-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord is bdq:Empty; COMPLIANT if the value of dwc:basisOfRecord is valid in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core basisOfRecord' {[https://dwc.tdwg.org/terms/#dwc:basisOfRecord]}{dwc:basisOfRecord vocabulary [https://rs.gbif.org/vocabulary/dwc/basis_of_record.xml]}")
    public static DQResponse<ComplianceValue> validationBasisofrecordStandard(
    		@ActedUpon("dwc:basisOfRecord") String basisOfRecord,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord 
        // is bdq:Empty; COMPLIANT if the value of dwc:basisOfRecord 
        // is valid in the bdq:sourceAuthority; otherwise NOT_COMPLIANT 
        // 
        // 
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord 
        // is EMPTY; COMPLIANT if the value of dwc:basisOfRecord is 
        // valid using the bdq:sourceAuthority; otherwise NOT_COMPLIANT 
        // 
        
        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "Darwin Core basisOfRecord" 
        // {[https://dwc.tdwg.org/terms/#dwc:basisOfRecord]}{dwc:basisOfRecord 
        // vocabulary [https://rs.gbif.org/vocabulary/dwc/basis_of_record.xml]} 
        
        if (MetadataUtils.isEmpty(sourceAuthority)) { 
        	sourceAuthority = "Darwin Core basisOfRecord";
        }
        if (sourceAuthority.equals("dwc:basistOfRecord")) { 
        	sourceAuthority = "Darwin Core basisOfRecord";
        } else if (sourceAuthority.equals("https://dwc.tdwg.org/terms/#dwc:basisOfRecord")) {
        	sourceAuthority = "Darwin Core basisOfRecord";
    	}
        
        if (MetadataUtils.isEmpty(basisOfRecord)) { 
			result.addComment("No value provided for dwc:basisOfRecord.");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else {
        	List<String> values = null;
        	if (sourceAuthority.equals("Darwin Core basisOfRecord")) {
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
     * Does the value of dwc:occurrenceStatus occur in the bdq:sourceAuthority?
     *
     * Provides: 116 VALIDATION_OCCURRENCESTATUS_STANDARD
     * Version: 2025-02-18
     *
     * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate
     * @param sourceAuthority the provided source authority against which to evaluate occurrenceStatus
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_OCCURRENCESTATUS_STANDARD", description="Does the value of dwc:occurrenceStatus occur in the bdq:sourceAuthority?")
    @Provides("7af25f1e-a4e2-4ff4-b161-d1f25a5c3e47")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/7af25f1e-a4e2-4ff4-b161-d1f25a5c3e47/2025-02-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:occurrenceStatus is bdq:Empty; COMPLIANT if the value of dwc:occurrenceStatus is resolved in the bdq:sourceAuthority; otherwise NOT_COMPLIANT.  bdq:sourceAuthority default = 'Regex present/absent'")
    public static DQResponse<ComplianceValue> validationOccurrencestatusStandard(
    		@ActedUpon("dwc:occurrenceStatus") String occurrenceStatus,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
        
        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:occurrenceStatus 
        // is bdq:Empty; COMPLIANT if the value of dwc:occurrenceStatus 
        // is resolved in the bdq:sourceAuthority; otherwise NOT_COMPLIANT 
        //
      
        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "Regex present/absent"
        // {["^(present|absent)$"]} 

        // NOTE GBIF vocabulary incorrectly uses values "Present" and "Absent"
        
        if (MetadataUtils.isEmpty(sourceAuthority)) { 
        	sourceAuthority = "Regex present/absent";
        }
        if (sourceAuthority.equals("dwc:occurrenceStatus")) { 
        	sourceAuthority = "Darwin Core recommended OccurrenceStatus values";
    	}
        
        if (MetadataUtils.isEmpty(occurrenceStatus)) { 
			result.addComment("No value provided for dwc:basisOfRecord.");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else {
        	List<String> values = null;
        	if (sourceAuthority.equals("Darwin Core recommended OccurrenceStatus values") || sourceAuthority.equals("Regex present/absent")) {
        		// "For Occurrences, the default vocabulary is recommended to consist of "present" and "absent", but can be extended by implementers with good justification."
        		values = List.of("present","absent");
        	} else if (sourceAuthority.equals("GBIF OccurrenceStatus Vocabulary")) {
        		// GBIF vocabulary uses upper case Present and Absent, not all lower case.
        		values = List.of("Present","Absent");
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
     * Provides: 117 VALIDATION_OCCURRENCESTATUS_NOTEMPTY
     * Version: 2023-09-18
     *
     * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_OCCURRENCESTATUS_NOTEMPTY", description="Is there a value in dwc:occurrenceStatus?")
    @Provides("eb4a17f6-6bea-4cdd-93dd-d5a7e9d1eccf")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/eb4a17f6-6bea-4cdd-93dd-d5a7e9d1eccf/2023-09-18")
    @Specification("COMPLIANT if dwc:occurrenceStatus is bdq:NotEmpty; otherwise NOT_COMPLIANT. ")
    public static DQResponse<ComplianceValue> validationOccurrencestatusNotempty(@ActedUpon("dwc:occurrenceStatus") String occurrenceStatus) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:occurrenceStatus is bdq:NotEmpty; otherwise 
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
    * Does dwc:occurrenceID contain a valid identifier??
    *
    * Provides: 23 VALIDATION_OCCURRENCEID_STANDARD
    * Version: 2024-04-02
    *
    * @param occurrenceID the provided dwc:occurrenceID to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_OCCURRENCEID_STANDARD", description="Does dwc:occurrenceID contain a valid identifier?")
    @Provides("3cfe9ab4-79f8-4afd-8da5-723183ef16a3")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/3cfe9ab4-79f8-4afd-8da5-723183ef16a3/2024-04-02")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:ocurrenceID is EMPTY; COMPLIANT if (1) dwc:occurrenceID is a validly formed LSID, or (2) dwc:occurrenceID is a validly formed URN with at least NID and NSS present, or (3) dwc:occurrenceID is in the form scope:value, or (4) dwc:occurrenceID is a validly formed URI with host and path where path consists of more than just \"/\"; otherwise NOT_COMPLIANT.")
    public static DQResponse<ComplianceValue> validationOccurrenceidStandard(
        @ActedUpon("dwc:occurrenceID") String occurrenceID
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
		// INTERNAL_PREREQUISITES_NOT_MET if dwc:ocurrenceID is EMPTY; COMPLIANT if (1)
		// dwc:occurrenceID is a validly formed LSID, or (2) dwc:occurrenceID is a
		// validly formed URN with at least NID and NSS present, or (3) dwc:occurrenceID
		// is in the form scope:value, or (4) dwc:occurrenceID is a validly formed URI
		// with host and path where path consists of more than just "/"; otherwise
		// NOT_COMPLIANT.        
        
        if (MetadataUtils.isEmpty(occurrenceID))  {
        	result.addComment("No value provided for scientificNameId.");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else if (occurrenceID.matches("^[0-9]+$")) { 
        	result.addComment("Provided occurrenceID ["+ occurrenceID +"] is a bare integer without an authority and this is incomplete.");
        	result.setResultState(ResultState.RUN_HAS_RESULT);
        	result.setValue(ComplianceValue.NOT_COMPLIANT);
        } else { 
        	try { 
        		RFC8141URN urn = new RFC8141URN(occurrenceID);
        		if (urn.getNid().equalsIgnoreCase("lsid")) { 
        			try { 
        				LSID lsid = new LSID(occurrenceID);
        				lsid.getAuthority();
        				lsid.getNamespace();
        				lsid.getObjectID();
        				result.addComment("Provided occurrenceID recognized as an LSID.");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.COMPLIANT);
        			} catch (URNFormatException e2) { 
        				logger.debug(e2.getMessage());
        				result.addComment("Provided value for occurrenceID ["+occurrenceID+"] claims to be an lsid, but is not correctly formatted as such.");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        			}
        		} else { 
        			logger.debug(urn.getNid());
        			logger.debug(urn.getNss());
        			if (urn.getNid().length()>0 && urn.getNss().length()>0) { 
        				result.addComment("Provided occurrenceID recognized as an URN.");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.COMPLIANT);
        			} else { 
        				result.addComment("Provided occurrenceID appears to be a URN, but doesn't have both NID and NSS");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        			}
        		}
        	} catch (URNFormatException e) { 
        		logger.debug(e.getMessage());
        		if (occurrenceID.toLowerCase().matches("^[a-z]+:[0-9]+$")) { 
        			result.addComment("Provided occurrenceID ["+occurrenceID+"] matches the pattern scope:value where value is an integer.");
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.setValue(ComplianceValue.COMPLIANT);
        		} else if (occurrenceID.toLowerCase().matches("^[a-z]+:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) { 
        			result.addComment("Provided occurrenceID ["+occurrenceID+"] matches the pattern scope:value where value is a uuid.");
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.setValue(ComplianceValue.COMPLIANT);
        		} else { 
        			try {
        				URI uri = new URI(occurrenceID);
        				logger.debug(uri.getScheme());
        				logger.debug(uri.getAuthority());
        				logger.debug(uri.getHost());
        				logger.debug(uri.getPath());
        				if (uri.getHost()!=null && uri.getPath()!=null 
        						&& uri.getHost().length()>0 && uri.getPath().length()>0
        						&& !uri.getPath().equals("/")) {
        					if (uri.getHost().equalsIgnoreCase("www.gbif.org") && uri.getPath().equals("/occurrence/")) { 
        						result.addComment("Provided occurrenceID recognized as GBIF occurrence URL, but lacks the ID ["+occurrenceID+"]");
        						result.setResultState(ResultState.RUN_HAS_RESULT);
        						result.setValue(ComplianceValue.NOT_COMPLIANT);
        					} else { 
        						result.addComment("Provided occurrenceID recognized as an URI with host, and path.");
        						result.setResultState(ResultState.RUN_HAS_RESULT);
        						result.setValue(ComplianceValue.COMPLIANT);
        					}
        				} else { 
        					result.addComment("Provided occurrenceID may be a URI, but doesn't have host and path ["+occurrenceID+"]");
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					result.setValue(ComplianceValue.NOT_COMPLIANT);
        				}
        			} catch (URISyntaxException e1) {
        				logger.debug(e1);
        				result.addComment("Provided value for occurrenceID ["+occurrenceID+"] is not a LSID, URN, URI, or identifier in the form scope:value.");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        			}
        		}
        	}
        }

        return result;
    }

    
    /**
    * Does the value of dcterms:license occur in bdq:sourceAuthority?
    *
    * Provides:  #38 VALIDATION_LICENSE_STANDARD
    * Version: 2024-03-21
    *
    * @param license the provided dcterms:license to evaluate as ActedUpon.
    * @param sourceAuthority the specified source authority for licences.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_LICENSE_STANDARD", description="Does the value of dcterms:license occur in bdq:sourceAuthority?")
    @Provides("3136236e-04b6-49ea-8b34-a65f25e3aba1")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/3136236e-04b6-49ea-8b34-a65f25e3aba1/2024-03-21")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dcterms:license is bdq:Empty; COMPLIANT if the value of the term dcterms:license is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Creative Commons 4.0 Licenses or CC0' {[https://creativecommons.org/]} { Regular Expression ^(http(s){0,1}://creativecommons.org/licenses/(by|by-sa|by-nc|by-nc-sa|by-nd|by-nc-nd)/4.0/((deed|legalcode)(.(id|eu|da|de|en|es|fr|fy|hr|it|lv|lt|mi|ni|no|pl|pt|ro|si|fi|sv|tr|cs|el|ru|uk|ar|jp|zh-hans|zh-hant|ko)){0,1})|(http(s){0,1}://creativecommons.org/publicdomain/zero/1.0/((deed|legalcode)(.(id|eu|da|de|en|es|fr|fy|hr|it|lv|lt|ni|no|pl|pt|ro|si|fi|sv|tr|cs|el|ru|uk|ar|jp|zh-hans|zh-hant|ko)){0,1})))$ }")
    public static DQResponse<ComplianceValue> validationLicenseStandard(
        @ActedUpon("dcterms:license") String license,
    	@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dcterms:license 
        // is bdq:Empty; COMPLIANT if the value of the term dcterms:license 
        // is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "Creative Commons 4.0 Licenses or CC0" {[https://creativecommons.org/]} { Regular Expression ^(http(s){0,1}://creativecommons.org/licenses/(by|by-sa|by-nc|by-nc-sa|by-nd|by-nc-nd)/4.0/((deed|legalcode)(.(id|eu|da|de|en|es|fr|fy|hr|it|lv|lt|mi|ni|no|pl|pt|ro|si|fi|sv|tr|cs|el|ru|uk|ar|jp|zh-hans|zh-hant|ko)){0,1})|(http(s){0,1}://creativecommons.org/publicdomain/zero/1.0/((deed|legalcode)(.(id|eu|da|de|en|es|fr|fy|hr|it|lv|lt|ni|no|pl|pt|ro|si|fi|sv|tr|cs|el|ru|uk|ar|jp|zh-hans|zh-hant|ko)){0,1})))$ }
        
        String DEFAULT_SOURCE_AUTHORITY = "Creative Commons 4.0 Licenses or CC0"; 
 
        if (MetadataUtils.isEmpty(sourceAuthority)) {
        	sourceAuthority = DEFAULT_SOURCE_AUTHORITY;
        }
        
        try { 
        	String pattern = "";
        	MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        	if (sourceAuthorityObject.getAuthority().equals(EnumMetadataSourceAuthority.INVALID)) { 
        		throw new SourceAuthorityException("Invalid source authority.");
        	} 

        	if (sourceAuthority.equals(DEFAULT_SOURCE_AUTHORITY)) { 
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
        		throw new SourceAuthorityException("Unknown source authority.");
        	}
        	logger.debug(pattern);
        	if (pattern.length() > 0) { 
        		if (MetadataUtils.isEmpty(license)) {
        			result.addComment("No value provided for dwc:licence.");
        			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		} else if (license.matches(pattern)) {	
        			result.addComment("Provided value for dcterms:license conforms to expectations.");
        			result.setValue(ComplianceValue.COMPLIANT);
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        		} else { 
        			result.addComment("Provided value for dcterms:licence [" + license +"] does not conform to expectations.");
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        		}
        	}
        } catch (SourceAuthorityException e) {
        	result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        } catch (Exception ex) {
        	result.addComment("Error evaluating dcterms:license " + ex.getMessage());
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        }
        
        return result;
    }

    
    /**
    * Proposes an amendment to the value of dc:type using the DCMI type vocabulary.
    * 
    * Supports difference in case, leading or trailing spaces, IRI form instead of literal, soundex, 
    * and single step edit distance for proposing amended values.
    *
    * Provides: #41 AMENDMENT_DCTYPE_STANDARDIZED
    * Version: 2024-08-16
    *
    * @param type the provided dc:type to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_DCTYPE_STANDARDIZED", description="Proposes an amendment to the value of dc:type using the DCMI type vocabulary.")
    @Provides("bd385eeb-44a2-464b-a503-7abe407ef904")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/bd385eeb-44a2-464b-a503-7abe407ef904/2024-08-16")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the value of dc:type is bdq:Empty; AMENDED the value of dc:type if it can be unambiguously interpreted as a term name in the bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority is 'DCMI Type Vocabulary' {[http://purl.org/dc/terms/DCMIType]} {'DCMI Type Vocabulary List Of Terms' [https://www.dublincore.org/specifications/dublin-core/dcmi-type-vocabulary/2010-10-11/]}")
    public static DQResponse<AmendmentValue> amendmentDctypeStandardized(
        @ActedUpon("dc:type") String type
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // value of dc:type is bdq:Empty; AMENDED the value of dc:type 
        // if it can be unambiguously interpreted as a term name in 
        // the bdq:sourceAuthority; otherwise NOT_AMENDED 
        //
        // bdq:sourceAuthority is "DCMI Type Vocabulary" {[http://purl.org/dc/terms/DCMIType]} 
        // {"DCMI Type Vocabulary List Of Terms" [https://www.dublincore.org/specifications/dublin-core/dcmi-type-vocabulary/2010-10-11/]} 
        // 
        
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
    * Proposes an amendment to the value of dwc:basisOfRecord using the bdq:sourceAuthority.
    *
    * Provides: #63 AMENDMENT_BASISOFRECORD_STANDARDIZED
    * Version: 2024-07-24
    *
    * @param basisOfRecord the provided dwc:basisOfRecord to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_BASISOFRECORD_STANDARDIZED", description="Proposes an amendment to the value of dwc:basisOfRecord using the bdq:sourceAuthority.")
    @Provides("07c28ace-561a-476e-a9b9-3d5ad6e35933")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/07c28ace-561a-476e-a9b9-3d5ad6e35933/2024-07-24")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord is bdq:Empty; AMENDED the value of dwc:basisOfRecord if it could be unambiguously interpreted as a value in the bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority default = 'Darwin Core basisOfRecord' {[https://dwc.tdwg.org/terms/#dwc:basisOfRecord]} {dwc:basisOfRecord vocabulary [https://rs.gbif.org/vocabulary/dwc/basis_of_record.xml]}")
    public static DQResponse<AmendmentValue> amendmentBasisofrecordStandardized(
        @ActedUpon("dwc:basisOfRecord") String basisOfRecord,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord 
        // is bdq:Empty; AMENDED the value of dwc:basisOfRecord if 
        // it could be unambiguously interpreted as a value in the 
        // bdq:sourceAuthority; otherwise NOT_AMENDED 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "Darwin Core basisOfRecord" 
        // {[https://dwc.tdwg.org/terms/#dwc:basisOfRecord]} {dwc:basisOfRecord 
        // vocabulary [https://rs.gbif.org/vocabulary/dwc/basis_of_record.xml]} 
        
		// Note: The term dwc:basisOfRecord has the comment "Recommended best practice
		// is to use a controlled vocabulary such as the set of local names of the
		// identifiers for classes in Darwin Core." The list of these values can be
		// determined by searching
		// https://github.com/tdwg/dwc/blob/master/vocabulary/term_versions.csv for rows
		// with status="recommended" and
		// rdf_type="http://www.w3.org/2000/01/rdf-schema#Class". For example, the term
		// http://rs.tdwg.org/dwc/terms/PreservedSpecimen has a local name
		// PreservedSpecimen. For tests against a dwc:Occurrence record, the set of
		// valid terms is more limited and embodied in the resource found at
		// https://rs.gbif.org/vocabulary/dwc/basis_of_record.xml, which contains the
		// local name for the identifier, as well as preferred and alternate labels from
		// which to standardize values.
        
        String DEFAULT_SOURCE_AUTHORITY = "Darwin Core basisOfRecord";
        List<String> basisOfRecordLiteralList = null;
        if (sourceAuthority == null) {
        	sourceAuthority = DEFAULT_SOURCE_AUTHORITY;
        }
        try {
        	if (sourceAuthority.equals(DEFAULT_SOURCE_AUTHORITY)) {
        		// https://rs.gbif.org/vocabulary/dwc/basis_of_record.xml
        		// "Recommended best practice is to use the standard label of one of the Darwin Core classes."
        		// e.g. skos:prefLabel "Fossil Specimen"@en;  rdfs:label "Fossil Specimen"@en;
        		basisOfRecordLiteralList = List.of("Dataset","Event","EventAttribute","EventMeasurement","FossilSpecimen","GeologicalContext","HumanObservation","Identification","LivingSpecimen","Location","MachineObservation","MaterialCitation","MaterialSample","MeasurementOrFact","Occurrence","OccurrenceMeasurement","Organism","PreservedSpecimen","ResourceRelationship","Sample","SampleAttribute","SamplingEvent","SamplingLocation","Taxon");
        	} else { 
        		result.addComment("Using non default sourceAuthority " + sourceAuthority);
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
        				logger.debug(aLiteral);
        				result.addComment("Provided value for dwc:basisOfRecord ["+basisOfRecord+"] corrected to form a valid dwc:basisOfRecord literal.");
        				result.setResultState(ResultState.AMENDED);
        				Map<String, String> newValues = new HashMap<>();
        				newValues.put("dwc:basisOfRecord", aLiteral) ;
        				result.setValue(new AmendmentValue(newValues));
        			} else if (basisOfRecord.trim().replaceAll(uriMatcher, "").toUpperCase().equals(aLiteral.toUpperCase())) { 
        				logger.debug(aLiteral);
        				matched = true;	
        				result.addComment("Provided value for dwc:basisOfRecord ["+basisOfRecord+"] corrected to form a valid dwc:basisOfRecord literal, the provided value was an IRI, and dwc:basisOfRecord must be a literal.");
        				result.setResultState(ResultState.AMENDED);
        				Map<String, String> newValues = new HashMap<>();
        				newValues.put("dwc:basisOfRecord", aLiteral) ;
        				result.setValue(new AmendmentValue(newValues));
        			} else if (typeSoundex.equals(literalSoundex)) {
        				logger.debug(literalSoundex);
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
        				// single character difference
        				logger.debug(aLiteral);
        				matched = true;	
        				result.addComment("Provided value for dwc:basisOfRecord ["+basisOfRecord+"] is one edit away from ["+aLiteral+"] so corrected to this valid dwc:basisOfRecord literal.");
        				result.setResultState(ResultState.AMENDED);
        				Map<String, String> newValues = new HashMap<>();
        				newValues.put("dwc:basisOfRecord", aLiteral) ;
        				result.setValue(new AmendmentValue(newValues));
        			} else if (basisOfRecord.length() > 4 && aLiteral.toLowerCase().contains(basisOfRecord.toLowerCase())) {
        				// substring match
        				logger.debug(aLiteral);
        				Iterator<String> iduplicates = basisOfRecordLiteralList.iterator();
        				int totalSubstringMatchCount = 0;
        				while (iduplicates.hasNext()) {
        					// make sure the substring match is to only one term
        					String term = iduplicates.next();
        					if (term.toLowerCase().contains(basisOfRecord.toLowerCase())) { 
        						totalSubstringMatchCount++;
        					}
        				}
        				logger.debug(totalSubstringMatchCount);
        				if (totalSubstringMatchCount==1) { 
        					matched = true;	
        					result.addComment("Provided value for dwc:basisOfRecord ["+basisOfRecord+"] is a unique substring of ["+aLiteral+"] so corrected to this valid dwc:basisOfRecord literal.");
        					result.setResultState(ResultState.AMENDED);
        					Map<String, String> newValues = new HashMap<>();
        					newValues.put("dwc:basisOfRecord", aLiteral) ;
        					result.setValue(new AmendmentValue(newValues));
        				}
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
    * Provides: 75 AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT
    * Version: 2024-11-13
    *
    * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate as ActedUpon.
    * @param individualCount the provided dwc:individualCount to evaluate as Consulted.
    * @param organismQuantity the provided dwc:organismQuantity to evaluate as Consulted.
    * @param defaultOccurrenceStatus the value to use as the default dwc:occurrenceStatus.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT", description="Proposes an amendment of the value of dwc:occurrenceStatus to the default parameter value if dwc:occurrenceStatus, dwc:individualCount and dwc:organismQuantity are empty.")
    @Provides("96667a0a-ae59-446a-bbb0-b7f2b0ca6cf5")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/96667a0a-ae59-446a-bbb0-b7f2b0ca6cf5/2024-11-13")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:occurrenceStatus is bdq:NotEmpty; FILLED_IN the value of dwc:occurrenceStatus using the bdq:defaultOccurrenceStatus Parameter value if dwc:occurrenceStatus,  dwc:individualCount and dwc:organismQuantity are bdq:Empty; otherwise NOT_AMENDED. bdq:defaultOccurrenceStatus default = 'present'")
    public static DQResponse<AmendmentValue> amendmentOccurrencestatusAssumeddefault(
        @ActedUpon("dwc:occurrenceStatus") String occurrenceStatus, 
        @Consulted("dwc:individualCount") String individualCount, 
        @Consulted("dwc:organismQuantity") String organismQuantity,
        @Parameter(name="bdq:defaultOccurrenceStatus") String defaultOccurrenceStatus
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
		// INTERNAL_PREREQUISITES_NOT_MET if dwc:occurrenceStatus is bdq:NotEmpty; 
		// FILLED_IN the value of dwc:occurrenceStatus using the 
		// bdq:defaultOccurrenceStatus Parameter value if dwc:occurrenceStatus, 
		// dwc:individualCount and dwc:organismQuantity are bdq:Empty; otherwise NOT_AMENDED

        // Parameters. This test is defined as parameterized.
        // bdq:defaultOccurrenceStatus
		// default = "present"

        if (MetadataUtils.isEmpty(defaultOccurrenceStatus)) { 
        	defaultOccurrenceStatus = "present";
        }
        
        if (MetadataUtils.isEmpty(occurrenceStatus) && MetadataUtils.isEmpty(individualCount) && MetadataUtils.isEmpty(organismQuantity)) { 
        	result.addComment("Set dwc:occurrenceStatus to the default value " + defaultOccurrenceStatus);
        	result.setResultState(ResultState.FILLED_IN);
        	Map<String, String> newValues = new HashMap<>();
        	newValues.put("dwc:occurrenceStatus", defaultOccurrenceStatus) ;
        	result.setValue(new AmendmentValue(newValues));
        } else { 
        	result.addComment("dwc:occurrenceStatus not changed, at least one of dwc:occurrenceStatus, dwc:individualCount, or dwc:organismQuantity contains a value.");
        	result.setResultState(ResultState.NOT_AMENDED);
        }
        
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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/cdaabb0d-a863-49d0-bc0f-738d771acba5/2023-09-18")
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

    
    /**
    * Proposes an amendment to the value of dwc:occurrenceStatus using the bdq:sourceAuthority.
    *
    * Provides: 115 AMENDMENT_OCCURRENCESTATUS_STANDARDIZED
    * Version: 2025-03-03
    *
    * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_OCCURRENCESTATUS_STANDARDIZED", description="Propose amendment to the value of dwc:occurrenceStatus using bdq:sourceAuthority.")
    @Provides("f8f3a093-042c-47a3-971a-a482aaaf3b75")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/f8f3a093-042c-47a3-971a-a482aaaf3b75/2025-03-03")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:ocurrenceStatus is bdq:Empty; AMENDED the value of dwc:occurrenceStatus if it can be unambiguously interpreted as a value in the bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority default = 'Regex present/absent' {['^(present|absent)$'] {\"dwc:occurrenceStatus vocabulary API\" [https://api.gbif.org/v1/vocabularies/OccurrenceStatus/concepts]}")
    public static DQResponse<AmendmentValue> amendmentOccurrencestatusStandardized(
        @ActedUpon("dwc:occurrenceStatus") String occurrenceStatus,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if 
        // dwc:ocurrenceStatus is bdq:Empty; AMENDED the value 
        // of dwc:occurrenceStatus if it can be unambiguously 
        // interpreted as a value in the bdq:sourceAuthority; otherwise NOT_AMENDED
        
        // Parameter
        // bdq:sourceAuthority default = "Regex present/absent"  
        // {["^(present|absent)$"]} 
        // {"dwc:occurrenceStatus vocabulary API" [https://api.gbif.org/v1/vocabularies/OccurrenceStatus/concepts]} 

        // NOTE: GBIF vocabulary (incorrectly) uses "Present" and "Absent".

        String DEFAULT_SOURCE_AUTHORITY = "Regex present/absent";
        
        if (MetadataUtils.isEmpty(occurrenceStatus)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Provided dwc:occurrenceStatus is empty");
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		sourceAuthority = DEFAULT_SOURCE_AUTHORITY;
        	}
        	try { 
        		if (!sourceAuthority.equals(DEFAULT_SOURCE_AUTHORITY)) { 
        			result.addComment("Usinng non-default sourceAuthoriy: " + sourceAuthority);
        		}
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (sourceAuthorityObject.getAuthority().equals(EnumMetadataSourceAuthority.INVALID)) { 
        			throw new SourceAuthorityException("Invalid Source Authority");
        		}
        		if (!MetadataSingleton.getInstance().isLoaded()) { 
        			throw new SourceAuthorityException("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError());
        		}
        		if (occurrenceStatus.equals("present")) { 
        			result.setResultState(ResultState.NOT_AMENDED);
        			result.addComment("Provided dwc:occurrenceStatus is a valid value");
        		} else if (occurrenceStatus.equals("absent")) { 
        			result.setResultState(ResultState.NOT_AMENDED);
        			result.addComment("Provided dwc:occurrenceStatus is a valid value");
        		} else if (!occurrenceStatus.equals("present") && occurrenceStatus.trim().toLowerCase().equals("present")) { 
        			result.setResultState(ResultState.AMENDED);
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:occurrenceStatus", "present") ;
        			result.setValue(new AmendmentValue(values));
        			result.addComment("Provided dwc:occurrenceStatus interpreted as present");
        		} else if (!occurrenceStatus.equals("absent") && occurrenceStatus.trim().toLowerCase().equals("absent")) { 
        			result.setResultState(ResultState.AMENDED);
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:occurrenceStatus", "absent") ;
        			result.setValue(new AmendmentValue(values));
        			result.addComment("Provided dwc:occurrenceStatus interpreted as absent");
        		} else if (occurrenceStatus.trim().equals("1")) { 
        			result.setResultState(ResultState.AMENDED);
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:occurrenceStatus", "present") ;
        			result.setValue(new AmendmentValue(values));
        			result.addComment("Provided dwc:occurrenceStatus interpreted as present");
        		} else if (occurrenceStatus.trim().equals("0")) { 
        			result.setResultState(ResultState.AMENDED);
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:occurrenceStatus", "absent") ;
        			result.setValue(new AmendmentValue(values));
        			result.addComment("Provided dwc:occurrenceStatus interpreted as absent");
        		} else if (MetadataSingleton.getInstance().getOccurrenceStatusValues().containsKey(occurrenceStatus.trim().toLowerCase())) { 
        			result.setResultState(ResultState.AMENDED);
        			Map<String, String> values = new HashMap<>();
        			String match = MetadataSingleton.getInstance().getOccurrenceStatusValues().get(occurrenceStatus.trim().toLowerCase());
        			match = match.toLowerCase(); // NOTE: Darwin Core examples and recommendation is lower case, GBIF vocabulary is capitalized.
        			values.put("dwc:occurrenceStatus", match);
        			result.setValue(new AmendmentValue(values));
        			result.addComment("Provided dwc:occurrenceStatus ["+ occurrenceStatus +"] interpreted from vocabulary.");
        		} else { 
        			result.setResultState(ResultState.NOT_AMENDED);
        			result.addComment("Provided dwc:occurrenceStatus ["+occurrenceStatus+"] not interpretable");
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception ex) {
        		result.addComment("Error evaluating dwc:occurrenceStaus: " + ex.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	}
        }
        return result;
    }

    /**
    * Propose amendment to the value of dcterms:license using bdq:sourceAuthority.
    *
    * Provides: 133 AMENDMENT_LICENSE_STANDARDIZED
    * Version: 2023-09-18
    *
    * @param license the provided dcterms:license to evaluate as ActedUpon.
    * @param sourceAuthority the source authority to consult.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_LICENSE_STANDARDIZED", description="Propose amendment to the value of dwc:license using bdq:sourceAuthority.")
    @Provides("dcbe5bd2-42a0-4aab-bb4d-8f148c6490f8")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/dcbe5bd2-42a0-4aab-bb4d-8f148c6490f8/2023-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; AMENDED value of dcterms:license if it could be unambiguously interpreted as a value in bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority default = 'Creative Commons' {[https://creativecommons.org/]} {Creative Commons licenses [https://creativecommons.org/about/cclicenses/]}")
    public static DQResponse<AmendmentValue> amendmentLicenseStandardized(
        @ActedUpon("dcterms:license") String license,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; AMENDED value of dcterms:license if it 
        // could be unambiguously interpreted as a value in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED. 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "Creative 
        // Commons" {[https://creativecommons.org/]} {Creative Commons 
        // licenses [https://creativecommons.org/about/cclicenses/]} 

        if (MetadataUtils.isEmpty(license)) { 
        	result.addComment("No Value provided for dcterms:license");
			result.setResultState(ResultState.NOT_AMENDED);
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		sourceAuthority = "Creative Commons";
        	}
        	try { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (sourceAuthorityObject.getAuthority().equals(EnumMetadataSourceAuthority.INVALID)) { 
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        		}
        		String pattern = "";
        		if (sourceAuthorityObject.getAuthority().equals(EnumMetadataSourceAuthority.CREATIVE_COMMONS)) { 
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
        			if (!MetadataSingleton.getInstance().isLoaded()) { 
        				result.addComment("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError() );
        				result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        			}
        			// TODO: Alternate patterns here.
        		}
        	    if (pattern.length() > 0) { 
        	        if (MetadataUtils.isEmpty(license)) {
        	        	result.addComment("No value provided for dcterms:license.");
        				result.setResultState(ResultState.NOT_AMENDED);	
        	        } else if (license.matches(pattern)) {	
                		result.addComment("Provided value for dcterms:license conforms to expectations.");
        				result.setResultState(ResultState.NOT_AMENDED);	
        			} else {
        				String match = "";
        				// "(by|by-sa|by-nc|by-nc-sa|by-nd|by-nc-nd)/4[.]0/"
        				if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CC0")) { 
        					match = "https://creativecommons.org/publicdomain/zero/1.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCBY")) { 
        					match = "https://creativecommons.org/licenses/by/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCBYSA")) { 
        					match = "https://creativecommons.org/licenses/by-sa/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCSABY")) { 
        					match = "https://creativecommons.org/licenses/by-sa/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCBYNC")) { 
        					match = "https://creativecommons.org/licenses/by-nc/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCNCBY")) { 
        					match = "https://creativecommons.org/licenses/by-nc/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCBYND")) { 
        					match = "https://creativecommons.org/licenses/by-nd/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCNDBY")) { 
        					match = "https://creativecommons.org/licenses/by-nd/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCBYNCSA")) { 
        					match = "https://creativecommons.org/licenses/by-nc-sa/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCNCSABY")) { 
        					match = "https://creativecommons.org/licenses/by-nc-sa/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCBYSANC")) { 
        					match = "https://creativecommons.org/licenses/by-nc-sa/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCSANCBY")) { 
        					match = "https://creativecommons.org/licenses/by-nc-sa/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCBYNCD")) { 
        					match = "https://creativecommons.org/licenses/by-nc-nd/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCNDSABY")) { 
        					match = "https://creativecommons.org/licenses/by-nc-nd/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCBYSAND")) { 
        					match = "https://creativecommons.org/licenses/by-nc-nd/4.0";
        				} else if (license.trim().toUpperCase().replace("-","").replace(" ","").equals("CCSANDBY")) { 
        					match = "https://creativecommons.org/licenses/by-nc-nd/4.0";
        				} 
        				if (match.length()>0) {
        					match = match + "/legalcode";   // terminal legalcode expected as given in Darwin Core examples.
        					result.setResultState(ResultState.AMENDED);	
        					Map<String, String> values = new HashMap<>();
        					values.put("dcterms:license", match) ;
        					result.setValue(new AmendmentValue(values));
        					result.addComment("Provided value for dcterms:license ["+license+"] modified to match controlled vocabulary.");
        				} else { 
        					result.addComment("Provided value of dcterms:license [" + license + "] unable to be conformed to the the sourceAuthority");
        					result.setResultState(ResultState.NOT_AMENDED);
        				}
        			}
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception e) {
        		result.addComment("Error evaluating dcterms:license: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	}
        }
        
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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/cd281f7e-13b3-43ae-8677-de06ffa70bb4/2024-02-07")
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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/3eefe72c-4c7d-4dee-89b6-e9d91d3f1981/2024-02-07")
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
    * Provides: 268 VALIDATION_ESTABLISHMENTMEANS_STANDARD
    * Version: 2024-02-08
    *
    * @param establishmentMeans the provided dwc:establishmentMeans to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_ESTABLISHMENTMEANS_STANDARD", description="Does the value of dwc:establishmentMeans occur in bdq:sourceAuthority?")
    @Provides("4eb48fdf-7299-4d63-9d08-246902e2857f")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/4eb48fdf-7299-4d63-9d08-246902e2857f/2024-02-08")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:establishmentMeans is EMPTY; COMPLIANT if the value of dwc:establishmentMeans is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core establishmentMeans' {[https://dwc.tdwg.org/list/#dwc_establishmentMeans]} {dwc:establishmentMeans vocabulary API [https://api.gbif.org/v1/vocabularies/EstablishmentMeans/concepts]}")
    public static DQResponse<ComplianceValue> validationEstablishmentmeansStandard(
        @ActedUpon("dwc:establishmentMeans") String establishmentMeans,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:establishmentMeans 
        // is EMPTY; COMPLIANT if the value of dwc:establishmentMeans 
        // is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. 
        // bdq:sourceAuthority default = "Darwin Core establishmentMeans" 
        // {[https://dwc.tdwg.org/list/#dwc_establishmentMeans]} {dwc:establishmentMeans 
        // vocabulary API [https://api.gbif.org/v1/vocabularies/EstablishmentMeans/concepts]} 
        // 

        // Parameters. This test is defined as parameterized.
		// bdq:sourceAuthority default = "Establishment Means
		// Controlled Vocabulary List of Terms" {[https://dwc.tdwg.org/em/]} {GBIF
		// vocabulary API
		// [https://api.gbif.org/v1/vocabularies/EstablishmentMeans/concepts]}
        
        if (MetadataUtils.isEmpty(establishmentMeans)) { 
        	result.addComment("No Value provided for dwc:establishmentMeans");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		sourceAuthority = "GBIF EstablishmentMeans Vocabulary";
        	}
        	try { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (sourceAuthorityObject.getAuthority().equals(EnumMetadataSourceAuthority.INVALID)) { 
        			throw new SourceAuthorityException("Invalid source authority.");
        		}
        		if (!MetadataSingleton.getInstance().isLoaded()) { 
        			result.addComment("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError() );
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        		} else { 
        			result.setResultState(ResultState.RUN_HAS_RESULT);	
        			if (MetadataSingleton.getInstance().getEstablishmentMeansTerms().containsKey(establishmentMeans)) { 
        				result.addComment("Provided value of dwc:establishmentMeans found in the sourceAuthority");
        				result.setValue(ComplianceValue.COMPLIANT);
        			} else {
        				result.addComment("Provided value of dwc:establishmentMeans [" + establishmentMeans + "] not found in the sourceAuthority");
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        			}
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception e) {
        		result.addComment("Error evaluating dwc:establishmentMeans: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	}
        }

        return result;
    }

    /**
    * Propose amendment to the value of dwc:establishmentMeans using bdq:sourceAuthority.
    *
    * Provides: 269 AMENDMENT_ESTABLISHMENTMEANS_STANDARDIZED
    * Version: 2024-02-08
    *
    * @param establishmentMeans the provided dwc:establishmentMeans to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_ESTABLISHMENTMEANS_STANDARDIZED", description="Propose amendment to the value of dwc:establishmentMeans using bdq:sourceAuthority.")
    @Provides("15d15927-7a22-43f8-88d6-298f5eb45c4c")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/15d15927-7a22-43f8-88d6-298f5eb45c4c/2024-02-08")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:establishmentMeans is EMPTY; AMENDED the value of dwc:establishmentMeans if it can be unambiguously matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority default = 'Darwin Core establishmentMeans' {[https://dwc.tdwg.org/list/#dwc_establishmentMeans]} {dwc:establishmentMeans vocabulary API [https://api.gbif.org/v1/vocabularies/EstablishmentMeans/concepts]}")
    public static DQResponse<AmendmentValue> amendmentEstablishmentmeansStandardized(
        @ActedUpon("dwc:establishmentMeans") String establishmentMeans,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:establishmentMeans 
        // is EMPTY; AMENDED the value of dwc:establishmentMeans if 
        // it can be unambiguously matched to a term in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "Darwin 
        // Core establishmentMeans" {[https://dwc.tdwg.org/list/#dwc_establishmentMeans]} 
        // {dwc:establishmentMeans vocabulary API [https://api.gbif.org/v1/vocabularies/EstablishmentMeans/concepts]} 
        
        if (MetadataUtils.isEmpty(establishmentMeans)) { 
        	result.addComment("No Value provided for dwc:establishmentMeans");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		sourceAuthority = "GBIF EstablishmentMeans Vocabulary";
        	}
        	try { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (sourceAuthorityObject.getAuthority().equals(EnumMetadataSourceAuthority.INVALID)) { 
        			throw new SourceAuthorityException("Invalid source authority.");
        		}
        		if (!MetadataSingleton.getInstance().isLoaded()) { 
        			result.addComment("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError() );
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        		} else { 
        			if (MetadataSingleton.getInstance().getEstablishmentMeansTerms().containsKey(establishmentMeans)) { 
        				result.addComment("Provided value of dwc:establishmentMeans found in the sourceAuthority");
        				result.setResultState(ResultState.NOT_AMENDED);	
        			} else {
        				if (MetadataSingleton.getInstance().getEstablishmentMeansValues().containsKey(establishmentMeans.trim().toLowerCase())) { 
        					String match = MetadataSingleton.getInstance().getEstablishmentMeansValues().get(establishmentMeans.trim().toLowerCase());
        					result.setResultState(ResultState.AMENDED);	
        					Map<String, String> values = new HashMap<>();
        					values.put("dwc:establishmentMeans", match) ;
        					result.setValue(new AmendmentValue(values));
        				} else { 
        					logger.debug(establishmentMeans);
        					// try a broader net
        					Iterator<String> i = MetadataSingleton.getInstance().getEstablishmentMeansValues().keySet().iterator();
        					boolean matched = false;
        					String lastMatchedKey = "";
        					String matchKey = "";
        					while (i.hasNext()) { 
        						String aValue = i.next();
        						// Try for unique match of abbreviation to first few letters of term, or if a longer string, a substring in the term
        						String trimValue = establishmentMeans.trim().toLowerCase().replace(".", "");
        						if (
        							(aValue.toLowerCase().startsWith(trimValue) && establishmentMeans.endsWith(".") )
        							 || 
        							(trimValue.length()>4 && aValue.toLowerCase().contains(trimValue))
        						) { 
        							if (!matched) { 
        								matched = true;
        								matchKey =  MetadataSingleton.getInstance().getEstablishmentMeansValues().get(aValue);
        								logger.debug(matchKey);
        								lastMatchedKey = matchKey;
        							} else { 
        								matchKey =  MetadataSingleton.getInstance().getEstablishmentMeansValues().get(aValue);
        								if (lastMatchedKey != matchKey) { 
        									logger.debug(matchKey);
        									// non-unique match.
        									matchKey = "";
        								}
        							}
        						}
         					}
        					if (matched && matchKey.length()>0) { 
        						result.addComment("Provided value of dwc:establishmentMeans [" + establishmentMeans + "] conformed to the the sourceAuthority");
        						result.setResultState(ResultState.AMENDED);	
        						Map<String, String> values = new HashMap<>();
        						values.put("dwc:establishmentMeans", matchKey) ;
        						result.setValue(new AmendmentValue(values));
        					} else { 
        						result.addComment("Provided value of dwc:establishmentMeans [" + establishmentMeans + "] unable to be conformed to the the sourceAuthority");
        						result.setResultState(ResultState.NOT_AMENDED);
        					}
        				}
        			}
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception e) {
        		result.addComment("Error evaluating dwc:establishmentMeans: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	}
        }

        return result;
    }

    /**
    * Propose amendment to the value of dwc:lifeStage using bdq:sourceAuthority.
    *
    * Provides: 271 AMENDMENT_LIFESTAGE_STANDARDIZED
    * Version: 2024-02-09
    *
    * @param lifeStage the provided dwc:lifeStage to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_LIFESTAGE_STANDARDIZED", description="Propose amendment to the value of dwc:lifeStage using bdq:sourceAuthority.")
    @Provides("07e79079-42e9-48e9-826e-4874ae34bce3")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/07e79079-42e9-48e9-826e-4874ae34bce3/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:lifeStage is EMPTY; AMENDED the value of dwc:lifeStage if it can be unambiguously matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority default = 'Darwin Core lifeStage [https://dwc.tdwg.org/list/#dwc_lifeStage]} {dwc:lifeStage vocabulary API' [https://api.gbif.org/v1/vocabularies/LifeStage/concepts]}")
    public static DQResponse<AmendmentValue> amendmentLifestageStandardized(
        @ActedUpon("dwc:lifeStage") String lifeStage,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:lifeStage 
        // is EMPTY; AMENDED the value of dwc:lifeStage if it can be 
        // unambiguously matched to a term in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "Darwin Core lifeStage
        // [https://dwc.tdwg.org/list/#dwc_lifeStage]} 
        // {dwc:lifeStage vocabulary API" [https://api.gbif.org/v1/vocabularies/LifeStage/concepts]} 

        if (MetadataUtils.isEmpty(lifeStage)) { 
        	result.addComment("No Value provided for dwc:lifeStage");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		sourceAuthority = "GBIF LifeStage Vocabulary";
        	}
        	try { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (sourceAuthorityObject.getAuthority().equals(EnumMetadataSourceAuthority.INVALID)) { 
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        		}
        		if (!MetadataSingleton.getInstance().isLoaded()) { 
        			result.addComment("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError() );
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        		} else { 
        			if (MetadataSingleton.getInstance().getLifeStageTerms().containsKey(lifeStage)) { 
        				result.addComment("Provided value of dwc:lifeStage found in the sourceAuthority");
        				result.setResultState(ResultState.NOT_AMENDED);	
        			} else {
        				if (MetadataSingleton.getInstance().getLifeStageValues().containsKey(lifeStage.trim().toLowerCase())) { 
        					String match = MetadataSingleton.getInstance().getLifeStageValues().get(lifeStage.trim().toLowerCase());
        					result.setResultState(ResultState.AMENDED);	
        					Map<String, String> values = new HashMap<>();
        					values.put("dwc:lifeStage", match) ;
        					result.setValue(new AmendmentValue(values));
        				} else { 
        					result.addComment("Provided value of dwc:lifeStage [" + lifeStage + "] unable to be conformed to the the sourceAuthority");
        					result.setResultState(ResultState.NOT_AMENDED);
        				}
        			}
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception e) {
        		result.addComment("Error evaluating dwc:lifeStage: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	}
        }
        
        return result;
    }

    /**
    * Does the value of dwc:degreeOfEstablishment occur in bdq:sourceAuthority?
    *
    * Provides: 275 VALIDATION_DEGREEOFESTABLISHMENT_STANDARD
    * Version: 2024-02-09
    *
    * @param degreeOfEstablishment the provided dwc:degreeOfEstablishment to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_DEGREEOFESTABLISHMENT_STANDARD", description="Does the value of dwc:degreeOfEstablishment occur in bdq:sourceAuthority?")
    @Provides("060e7734-607d-4737-8b2c-bfa17788bf1a")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/060e7734-607d-4737-8b2c-bfa17788bf1a/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:degreeOfEstablishment is EMPTY; COMPLIANT if the value of dwc:degreeOfEstablishment is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT.")
    public static DQResponse<ComplianceValue> validationDegreeofestablishmentStandard(
        @ActedUpon("dwc:degreeOfEstablishment") String degreeOfEstablishment,
    	@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // specification
		// EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available;
		// INTERNAL_PREREQUISITES_NOT_MET if dwc:degreeOfEstablishment is EMPTY;
		// COMPLIANT if the value of dwc:degreeOfEstablishment is in the
		// bdq:sourceAuthority; otherwise NOT_COMPLIANT.

        // Parameters. This test is defined as parameterized.
		// bdq:sourceAuthority default = "Degree of Establishment Controlled Vocabulary
		// List of Terms" {[https://dwc.tdwg.org/doe/]} {GBIF vocabulary API
		// [https://api.gbif.org/v1/vocabularies/DegreeOfEstablishment/concepts]}
        
        if (MetadataUtils.isEmpty(degreeOfEstablishment)) { 
        	result.addComment("No Value provided for dwc:degreeOfEstablishment");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		sourceAuthority = "GBIF DegreeOfEstablishment Vocabulary";
        	}
        	try { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (!MetadataSingleton.getInstance().isLoaded()) { 
        			result.addComment("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError() );
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        		} else { 
        			result.setResultState(ResultState.RUN_HAS_RESULT);	
        			if (MetadataSingleton.getInstance().getDegreeOfEstablishmentTerms().containsKey(degreeOfEstablishment)) { 
        				result.addComment("Provided value of dwc:degreeOfEstablishment found in the sourceAuthority");
        				result.setValue(ComplianceValue.COMPLIANT);
        			} else {
        				result.addComment("Provided value of dwc:degreeOfEstablishment [" + degreeOfEstablishment + "] not found in the sourceAuthority");
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        			}
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception e) {
        		result.addComment("Error evaluating dwc:degreeOfEstablishment: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	}
        }

        return result;
    }

    /**
    * Proposes an amendment to the value of dwc:degreeOfEstablishment using the bdq:sourceAuthority.
    *
    * Provides: 276 AMENDMENT_DEGREEOFESTABLISHMENT_STANDARDIZED
    * Version: 2024-04-16
    *
    * @param degreeOfEstablishment the provided dwc:degreeOfEstablishment to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_DEGREEOFESTABLISHMENT_STANDARDIZED", description="Proposes an amendment to the value of dwc:degreeOfEstablishment using the bdq:sourceAuthority.")
    @Provides("74ef1034-e289-4596-b5b0-cde73796697d")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/74ef1034-e289-4596-b5b0-cde73796697d/2024-04-16")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:degreeOfEstablishment is bdq:Empty; AMENDED the value of dwc:degreeOfEstablishment if it can be unambiguously matched to a term in the bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority default = 'Degree of Establishment Controlled Vocabulary List of Terms' {[https://dwc.tdwg.org/doe/]} {GBIF vocabulary API [https://api.gbif.org/v1/vocabularies/DegreeOfEstablishment/concepts]}")
    public static DQResponse<AmendmentValue> amendmentDegreeofestablishmentStandardized(
        @ActedUpon("dwc:degreeOfEstablishment") String degreeOfEstablishment,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:degreeOfEstablishment 
        // is bdq:Empty; AMENDED the value of dwc:degreeOfEstablishment 
        // if it can be unambiguously matched to a term in the bdq:sourceAuthority; 
        // otherwise NOT_AMENDED 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "Degree of Establishment Controlled Vocabulary List of Terms" 
        // {[https://dwc.tdwg.org/doe/]} 
        // {GBIF vocabulary API [https://api.gbif.org/v1/vocabularies/DegreeOfEstablishment/concepts]} 

        if (MetadataUtils.isEmpty(degreeOfEstablishment)) { 
        	result.addComment("No Value provided for dwc:degreeOfEstablishment");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		sourceAuthority = "Degree of Establishment Controlled Vocabulary List of Terms";
        	}
        	try { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (sourceAuthorityObject.getAuthority().equals(EnumMetadataSourceAuthority.INVALID)) { 
        			throw new SourceAuthorityException("Invalid Source Authority.");
        		}
        		if (!MetadataSingleton.getInstance().isLoaded()) { 
        			result.addComment("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError() );
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        		} else { 
        			if (MetadataSingleton.getInstance().getDegreeOfEstablishmentTerms().containsKey(degreeOfEstablishment)) { 
        				result.addComment("Provided value of dwc:degreeOfEstablishment ["+degreeOfEstablishment+"] found in the sourceAuthority");
        				result.setResultState(ResultState.NOT_AMENDED);	
        			} else {
        				// Handle camel case value
        				if (degreeOfEstablishment.trim().toLowerCase().equals("widespread invasive")) { 
        					degreeOfEstablishment = "widespreadInvasive";
        				}
        				if (degreeOfEstablishment.trim().toLowerCase().equals("widespreadinvasive")) { 
        					degreeOfEstablishment = "widespreadInvasive";
        				}
        				if (MetadataSingleton.getInstance().getDegreeOfEstablishmentValues().containsKey(degreeOfEstablishment.trim().toLowerCase())) { 
        					String match = MetadataSingleton.getInstance().getDegreeOfEstablishmentValues().get(degreeOfEstablishment.trim().toLowerCase());
        					result.setResultState(ResultState.AMENDED);	
        					Map<String, String> values = new HashMap<>();
        					values.put("dwc:degreeOfEstablishment", match) ;
        					result.setValue(new AmendmentValue(values));
        					result.addComment("Provided value of dwc:degreeOfEstablishment [" + degreeOfEstablishment + "] conformed to the sourceAuthority");
        				} else { 
        					// try a broader net
        					Iterator<String> i = MetadataSingleton.getInstance().getDegreeOfEstablishmentTerms().keySet().iterator();
        					boolean matched = false;
        					String matchKey = "";
        					while (i.hasNext()) { 
        						String aValue = i.next();
        						// Try for unique match of abbreviation to first few letters of term, or if a longer string, a substring in the term
        						String trimValue = degreeOfEstablishment.trim().toLowerCase().replace(".", "");
        						if (
        							(aValue.toLowerCase().startsWith(trimValue) && degreeOfEstablishment.endsWith(".") )
        							 || 
        							(trimValue.length()>4 && aValue.toLowerCase().contains(trimValue))
        						) { 
        							if (!matched) { 
        								matched = true;
        								matchKey =  MetadataSingleton.getInstance().getDegreeOfEstablishmentValues().get(aValue);
        							} else { 
        								// non-unique match.
        								matchKey = "";
        							}
        						}
         					}
        					if (matched && matchKey.length()>0) { 
        						result.addComment("Provided value of dwc:degreeOfEstablshment [" + degreeOfEstablishment + "] conformed to the the sourceAuthority");
        						result.setResultState(ResultState.AMENDED);	
        						Map<String, String> values = new HashMap<>();
        						values.put("dwc:degreeOfEstablishment", matchKey) ;
        						result.setValue(new AmendmentValue(values));
        					} else { 
        						result.addComment("Provided value of dwc:degreeOfEstablishment [" + degreeOfEstablishment + "] unable to be conformed to the sourceAuthority");
        						result.setResultState(ResultState.NOT_AMENDED);
        					}
        					
        				}
        			}
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception e) {
        		result.addComment("Error evaluating dwc:degreeOfEstablishment: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	}
        }
        
        return result;
    }

    /**
    * Does the value of dwc:pathway occur in bdq:sourceAuthority?
    *
    * Provides: 277 VALIDATION_PATHWAY_STANDARD
    * Version: 2024-02-09
    *
    * @param pathway the provided dwc:pathway to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_PATHWAY_STANDARD", description="Does the value of dwc:pathway occur in bdq:sourceAuthority?")
    @Provides("5424e933-bee7-4125-839e-d8743ea69f93")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/5424e933-bee7-4125-839e-d8743ea69f93/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:pathway is EMPTY; COMPLIANT if the value of dwc:pathway is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core pathway' {[https://dwc.tdwg.org/list/#dwc_pathway]} {dwc:pathway vocabulary API [https://api.gbif.org/v1/vocabularies/Pathway/concepts]}")
    public static DQResponse<ComplianceValue> validationPathwayStandard(
        @ActedUpon("dwc:pathway") String pathway,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:pathway 
        // is EMPTY; COMPLIANT if the value of dwc:pathway is in the 
        // bdq:sourceAuthority; otherwise NOT_COMPLIANT. 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "Pathway Controlled Vocabulary List of Terms" 
        // {[https://dwc.tdwg.org/pw/]} 
        // {GBIF vocabulary API [https://api.gbif.org/v1/vocabularies/Pathway/concepts]}

        if (MetadataUtils.isEmpty(pathway)) { 
        	result.addComment("No Value provided for dwc:pathway");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		// TODO: Implement tdwg vocabulary lookup
        		sourceAuthority = "GBIF Pathway Vocabulary";
        	}
        	try { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (!MetadataSingleton.getInstance().isLoaded()) { 
        			result.addComment("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError() );
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        		} else { 
        			result.setResultState(ResultState.RUN_HAS_RESULT);	
        			if (MetadataSingleton.getInstance().getPathwayTerms().containsKey(pathway)) { 
        				result.addComment("Provided value of dwc:pathway found in the sourceAuthority");
        				result.setValue(ComplianceValue.COMPLIANT);
        			} else {
        				result.addComment("Provided value of dwc:pathway [" + pathway + "] not found in the sourceAuthority");
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        			}
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception e) {
        		
        	}
        }
        
        return result;
    }

    /**
    * Propose an amendment to the value of dwc:pathway using the bdq:sourceAuthority.
    *
    * Provides: 278 AMENDMENT_PATHWAY_STANDARDIZED
    * Version: 2024-09-18
    *
    * @param pathway the provided dwc:pathway to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_PATHWAY_STANDARDIZED", description="Propose an amendment to the value of dwc:pathway using the bdq:sourceAuthority.")
    @Provides("f9205977-f145-44f5-8cb9-e3e2e35ce908")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/f9205977-f145-44f5-8cb9-e3e2e35ce908/2024-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:pathway is bdq:Empty; AMENDED the value of dwc:pathway if it can be unambiguously matched to a term in the bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority default = 'Pathway Controlled Vocabulary List of Terms' {[https://dwc.tdwg.org/pw/]} {GBIF vocabulary API [https://api.gbif.org/v1/vocabularies/Pathway/concepts]}")
    public static DQResponse<AmendmentValue> amendmentPathwayStandardized(
        @ActedUpon("dwc:pathway") String pathway,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:pathway 
        // is bdq:Empty; AMENDED the value of dwc:pathway if it can 
        // be unambiguously matched to a term in the bdq:sourceAuthority; 
        // otherwise NOT_AMENDED 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "Pathway Controlled Vocabulary List of Terms" 
        // {[https://dwc.tdwg.org/pw/]} 
        // {GBIF vocabulary API [https://api.gbif.org/v1/vocabularies/Pathway/concepts]} 

        if (MetadataUtils.isEmpty(pathway)) { 
        	result.addComment("No Value provided for dwc:pathway");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		sourceAuthority = "GBIF Pathway Vocabulary";
        	}
        	try { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (sourceAuthorityObject.getAuthority().equals(EnumMetadataSourceAuthority.INVALID)) { 
        			throw new SourceAuthorityException("Invalid Source Authority.");
        		}
        		if (!MetadataSingleton.getInstance().isLoaded()) { 
        			result.addComment("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError() );
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        		} else { 
        			if (MetadataSingleton.getInstance().getPathwayTerms().containsKey(pathway)) { 
        				result.addComment("Provided value of dwc:pathway found in the sourceAuthority");
        				result.setResultState(ResultState.NOT_AMENDED);	
        			} else {
        				if (MetadataSingleton.getInstance().getPathwayValues().containsKey(pathway.trim().toLowerCase())) { 
        					String match = MetadataSingleton.getInstance().getPathwayValues().get(pathway.trim().toLowerCase());
        					result.setResultState(ResultState.AMENDED);	
        					Map<String, String> values = new HashMap<>();
        					values.put("dwc:pathway", match) ;
        					result.setValue(new AmendmentValue(values));
        				} else { 
        					
        					// try a broader net
        					Iterator<String> i = MetadataSingleton.getInstance().getPathwayTerms().keySet().iterator();
        					boolean matched = false;
        					String matchKey = "";
        					while (i.hasNext()) { 
        						String aValue = i.next();
        						// Try for unique match of abbreviation to first few letters of term, or if a longer string, a substring in the term
        						String trimValue = pathway.trim().toLowerCase().replace(".", "");
        						if (
        							(aValue.toLowerCase().startsWith(trimValue) && pathway.endsWith(".") )
        							 || 
        							(trimValue.length()>4 && aValue.toLowerCase().contains(trimValue))
        						) { 
        							if (!matched) { 
        								matched = true;
        								matchKey =  MetadataSingleton.getInstance().getPathwayValues().get(aValue);
        							} else { 
        								// non-unique match.
        								matchKey = "";
        							}
        						}
         					}
        					if (matched && matchKey.length()>0) { 
        						result.addComment("Provided value of dwc:pathway [" + pathway + "] conformed to the the sourceAuthority");
        						result.setResultState(ResultState.AMENDED);	
        						Map<String, String> values = new HashMap<>();
        						values.put("dwc:pathway", matchKey) ;
        						result.setValue(new AmendmentValue(values));
        					} else { 
        						result.addComment("Provided value of dwc:pathway [" + pathway + "] unable to be conformed to the the sourceAuthority");
        						result.setResultState(ResultState.NOT_AMENDED);
        					}
        					
        				}
        			}
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception e) {
        		result.addComment("Error evaluating dwc:pathway: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	}
        }
        
        return result;
    }

    /**
    * Does the value of dwc:preparations occur in bdq:sourceAuthority?
    *
    * Provides: #279 VALIDATION_PREPARATIONS_STANDARD
    * Version: 2024-02-08
    *
    * @param preparations the provided dwc:preparations to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_PREPARATIONS_STANDARD", description="Does the value of dwc:preparations occur in bdq:sourceAuthority?")
    @Provides("4758ea74-d2d3-4656-b17b-4b30147af4dc")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/4758ea74-d2d3-4656-b17b-4b30147af4dc/2024-02-08")
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
    * Provides: 283 VALIDATION_SEX_STANDARD
    * Version: 2024-02-09
    *
    * @param sex the provided dwc:sex to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_SEX_STANDARD", description="Does the value of dwc:sex occur in bdq:sourceAuthority?")
    @Provides("88d8598b-3318-483d-9475-a5acf9887404")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/88d8598b-3318-483d-9475-a5acf9887404/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:sex is EMPTY; COMPLIANT if the value of dwc:sex is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core sex' {[https://dwc.tdwg.org/list/#dwc_sex]} {dwc:sex vocabulary API [NO CURRENT API EXISTS]}")
    public static DQResponse<ComplianceValue> validationSexStandard(
        @ActedUpon("dwc:sex") String sex,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
		// EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available;
		// INTERNAL_PREREQUISITES_NOT_MET if dwc:sex is EMPTY; COMPLIANT if the value of
		// dwc:sex is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT.

        // Parameters. This test is defined as parameterized.
		// bdq:sourceAuthority default = "GBIF Sex Vocabulary"
		// [https://api.gbif.org/v1/vocabularies/Sex]} {"dwc:sex vocabulary API"
		// [https://api.gbif.org/v1/vocabularies/Sex/concepts]}

        if (MetadataUtils.isEmpty(sex)) { 
        	result.addComment("No Value provided for dwc:sex");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		sourceAuthority = "GBIF Sex Vocabulary";
        	}
        	try { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (!MetadataSingleton.getInstance().isLoaded()) { 
        			result.addComment("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError() );
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        		} else { 
        			result.setResultState(ResultState.RUN_HAS_RESULT);	
        			if (MetadataSingleton.getInstance().getSexTerms().containsKey(sex)) { 
        				result.addComment("Provided value of dwc:sex found in the sourceAuthority");
        				result.setValue(ComplianceValue.COMPLIANT);
        			} else {
        				result.addComment("Provided value of dwc:sex [" + sex + "] not found in the sourceAuthority");
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        			}
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception e) {
        		result.addComment("Error evaluating dwc:sex: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	}
        }
        
        return result;
    }

    /**
    * Propose amendment to the value of dwc:sex using bdq:sourceAuthority.
    *
    * Provides: 284 AMENDMENT_SEX_STANDARDIZED
    * Version: 2024-03-25
    *
    * @param sex the provided dwc:sex to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_SEX_STANDARDIZED", description="Propose amendment to the value of dwc:sex using bdq:sourceAuthority.")
    @Provides("33c45ae1-e2db-462a-a59e-7169bb01c5d6")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/33c45ae1-e2db-462a-a59e-7169bb01c5d6/2024-03-25")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:sex is EMPTY; AMENDED the value of dwc:sex if it can be unambiguously matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED")
    public static DQResponse<AmendmentValue> amendmentSexStandardized(
        @ActedUpon("dwc:sex") String sex,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // specification
		// EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available;
		// INTERNAL PREREQUISITES_NOT_MET if dwc:sex is EMPTY; AMENDED the value of
		// dwc:sex if it can be unambiguously matched to a term in bdq:sourceAuthority;
		// otherwise NOT_AMENDED

        // Parameters. This test is defined as parameterized.
		// bdq:sourceAuthority default = "GBIF Sex Vocabulary"
		// [https://api.gbif.org/v1/vocabularies/Sex]} {"dwc:sex vocabulary API"
		// [https://api.gbif.org/v1/vocabularies/Sex/concepts]}

        if (MetadataUtils.isEmpty(sex)) { 
        	result.addComment("No Value provided for dwc:sex");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		sourceAuthority = "GBIF Sex Vocabulary";
        	}
        	try { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (sourceAuthorityObject.getAuthority().equals(EnumMetadataSourceAuthority.INVALID)) { 
        			throw new SourceAuthorityException("Invalid source authority");
        		}
        		if (!MetadataSingleton.getInstance().isLoaded()) { 
        			result.addComment("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError() );
        			throw new SourceAuthorityException("Error loading data from sourceAuthority");
        		} else { 
        			if (MetadataSingleton.getInstance().getSexTerms().containsKey(sex)) { 
        				result.addComment("Provided value of dwc:sex found in the sourceAuthority");
        				result.setResultState(ResultState.NOT_AMENDED);	
        			} else {
        				if (MetadataSingleton.getInstance().getSexValues().containsKey(sex.trim().toLowerCase())) { 
        					String match = MetadataSingleton.getInstance().getSexValues().get(sex.trim().toLowerCase());
        					result.setResultState(ResultState.AMENDED);	
        					Map<String, String> values = new HashMap<>();
        					values.put("dwc:sex", match) ;
        					result.setValue(new AmendmentValue(values));
        				} else { 
        					Iterator<String> i = MetadataSingleton.getInstance().getSexTerms().keySet().iterator();
        					boolean matched = false;
        					String matchKey = "";
        					while (i.hasNext()) { 
        						String aValue = i.next();
        						if (aValue.toLowerCase().startsWith(sex.trim().toLowerCase().replace(".", ""))) { 
        							if (!matched) { 
        								matched = true;
        								matchKey =  MetadataSingleton.getInstance().getSexValues().get(aValue);
        							} else { 
        								// non-unique match.
        								matchKey = "";
        							}
        						}
         					}
        					if (matched && matchKey.length()>0) { 
        						result.addComment("Provided value of dwc:sex [" + sex + "] conformed to the the sourceAuthority");
        						result.setResultState(ResultState.AMENDED);	
        						Map<String, String> values = new HashMap<>();
        						values.put("dwc:sex", matchKey) ;
        						result.setValue(new AmendmentValue(values));
        					} else { 
        						result.addComment("Provided value of dwc:sex [" + sex + "] unable to be conformed to the the sourceAuthority");
        						result.setResultState(ResultState.NOT_AMENDED);
        					}
        				}
        			}
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception e) {
        		result.addComment("Error evaluating dwc:sex: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	}
        }
        
        return result;
    }

    /**
    * Does the value of dwc:typeStatus occur in bdq:sourceAuthority?
    *
    * Provides: 285 VALIDATION_TYPESTATUS_STANDARD
    * Version: 2024-11-11
    *
    * @param typeStatus the provided dwc:typeStatus to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_TYPESTATUS_STANDARD", description="Does the value of dwc:typeStatus occur in bdq:sourceAuthority?")
    @Provides("4833a522-12eb-4fe0-b4cf-7f7a337a6048")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/4833a522-12eb-4fe0-b4cf-7f7a337a6048/2024-11-11")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:typeStatus is bdq:Empty; COMPLIANT if the value of the first word in each &#124; delimited portion of dwc:typeStatus is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT.. bdq:sourceAuthority default = 'GBIF TypeStatus Vocabulary' {[https://api.gbif.org/v1/vocabularies/TypeStatus]} {dwc:typeStatus vocabulary API [https://api.gbif.org/v1/vocabularies/TypeStatus/concepts]}")
    public static DQResponse<ComplianceValue> validationTypestatusStandard(
        @ActedUpon("dwc:typeStatus") String typeStatus,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:typeStatus 
        // is bdq:Empty; COMPLIANT if the value of the first word in 
        // each | delimited portion of dwc:typeStatus is in the 
        // bdq:sourceAuthority; otherwise NOT_COMPLIANT. 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "GBIF TypeStatus Vocabulary" 
        // {[https://api.gbif.org/v1/vocabularies/TypeStatus]} {dwc:typeStatus 
        // vocabulary API [https://api.gbif.org/v1/vocabularies/TypeStatus/concepts]} 
        
        if (MetadataUtils.isEmpty(typeStatus)) { 
        	result.addComment("No Value provided for dwc:typeStatus");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		sourceAuthority = "GBIF TypeStatus Vocabulary";
        	}
        	try { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (!MetadataSingleton.getInstance().isLoaded()) { 
        			result.addComment("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError() );
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        		} else { 
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			
        			List<String> typeStatusBits = new ArrayList<String>();
        			if (typeStatus.contains("|")) { 
        				// Handle pipe delimited list.
        				typeStatusBits = Arrays.asList(typeStatus.split("[|]"));
        			} else { 
        				typeStatusBits.add(typeStatus);
        			}
        			boolean allCompliant = true;
        			Iterator<String> i = typeStatusBits.iterator();
        			int fails = 0;
        			while (i.hasNext()) { 
        				String bit = i.next().trim();
        				String firstWord = bit;
        				if (bit.contains(" ")) { 
        					// evaluate just the first word
        					firstWord = bit.substring(0,bit.indexOf(" "));
        				}
        				if (!MetadataSingleton.getInstance().getTypeStatusTerms().containsKey(firstWord)) {
        					result.addComment("Provided first word of element in dwc:typeStatus [" + firstWord + "] not found in the sourceAuthority");
        					allCompliant = false;
        					fails = fails + 1;
        				} else { 
        					result.addComment("Provided first word of element in dwc:typeStatus [" + firstWord + "] was found in the sourceAuthority");
        				}
        			}
        			if (allCompliant) { 
        				result.addComment("Provided value of first word in each pipe delimited part of dwc:typeStatus found in the sourceAuthority " + sourceAuthority);
        				result.setValue(ComplianceValue.COMPLIANT);
        			} else {
        				result.addComment("Provided value of " + fails + " of " + typeStatusBits.size() +" first words of pipe delimited bits of dwc:typeStatus [" + typeStatus + "] not found in the sourceAuthority " + sourceAuthority);
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        			}
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception e) {
        		result.addComment("Error evaluating dwc:typeStatus: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	}
        }
        return result;
    }

    /**
    * Proposes an amendment to the value of dwc:typeStatus using the bdq:sourceAuthority.
    *
    * Provides: 286 AMENDMENT_TYPESTATUS_STANDARDIZED
    * Version: 2024-11-11
    *
    * @param typeStatus the provided dwc:typeStatus to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_TYPESTATUS_STANDARDIZED", description="Proposes an amendment to the value of dwc:typeStatus using the bdq:sourceAuthority.")
    @Provides("b3471c65-b53e-453b-8282-abfa27bf1805")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/b3471c65-b53e-453b-8282-abfa27bf1805/2024-11-11")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:typeStatus is bdq:Empty; AMENDED the value of the first word in each &#124; delimited portion of dwc:typeStatus if it can be unambiguously matched to a term in the bdq:sourceAuthority; otherwise NOT_AMENDED.. bdq:sourceAuthority default = 'GBIF TypeStatus Vocabulary' {[https://api.gbif.org/v1/vocabularies/TypeStatus]} {dwc:typeStatus vocabulary API [https://api.gbif.org/v1/vocabularies/TypeStatus]}")
    public static DQResponse<AmendmentValue> amendmentTypestatusStandardized(
        @ActedUpon("dwc:typeStatus") String typeStatus,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:typeStatus 
        // is bdq:Empty; AMENDED the value of the first word in each 
        // | delimited portion of dwc:typeStatus if it can be 
        // unambiguously matched to a term in the bdq:sourceAuthority; 
        // otherwise NOT_AMENDED. 
        // 
        
        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "GBIF TypeStatus Vocabulary" 
        // {[https://api.gbif.org/v1/vocabularies/TypeStatus]} {dwc:typeStatus 
        // vocabulary API [https://api.gbif.org/v1/vocabularies/TypeStatus]} 
        
        if (MetadataUtils.isEmpty(typeStatus)) { 
        	result.addComment("No Value provided for dwc:typeStatus");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		sourceAuthority = "GBIF TypeStatus Vocabulary";
        	}
        	try { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (sourceAuthorityObject.getAuthority().equals(EnumMetadataSourceAuthority.INVALID)) { 
        			throw new SourceAuthorityException("Invalid source authority");
        		}
        		if (!MetadataSingleton.getInstance().isLoaded()) { 
        			result.addComment("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError() );
        			throw new SourceAuthorityException("Error loading data from sourceAuthority");
        		} else {
        			boolean changeProposed = false;
        			boolean allFound = true;
        			List<String> typeStatusBits = new ArrayList<String>();
        			List<String> outputBits = new ArrayList<String>();
        			if (typeStatus.contains("|")) { 
        				// Handle pipe delimited list.
        				typeStatusBits = Arrays.asList(typeStatus.split("[|]"));
        			} else { 
        				typeStatusBits.add(typeStatus);
        			}
        			// Iterate through bits
        			Iterator<String> i = typeStatusBits.iterator();
        			while (i.hasNext()) { 
        				String typeStatusBit = i.next().trim();
        				String outputBit = typeStatusBit;
        				String typeStatusWord = typeStatusBit;
        				if (typeStatusBit.contains(" ")) { 
        					typeStatusWord = typeStatusBit.substring(0, typeStatusWord.indexOf(" "));
        				}
        				if (MetadataSingleton.getInstance().getTypeStatusTerms().containsKey(typeStatusWord)) { 
        				   result.addComment("Provided value of first word ["+typeStatusWord+"] of a pipe delimited element of dwc:typeStatus found in the sourceAuthority");
        				   outputBit = typeStatusBit;
        				} else { 
        					allFound = false;
            				if (MetadataSingleton.getInstance().getTypeStatusValues().containsKey(typeStatusWord.trim().toLowerCase())) {
            					String match = MetadataSingleton.getInstance().getTypeStatusValues().get(typeStatusWord.trim().toLowerCase());
            					changeProposed=true;
            					outputBit = outputBit.replace(typeStatusWord,match);
            					result.addComment("Provided first word from of dwc:typeStatus [" + typeStatusWord + "] conformed to the the sourceAuthority");
            				} else {
            					Iterator<String> it = MetadataSingleton.getInstance().getTypeStatusTerms().keySet().iterator();
            					boolean matched = false;
            					String matchKey = "";
            					while (it.hasNext()) { 
            						String aValue = it.next();
            						if (aValue.toLowerCase().startsWith(typeStatusWord.trim().toLowerCase().replace(".", ""))) { 
            							if (!matched) { 
            								matched = true;
            								matchKey =  MetadataSingleton.getInstance().getTypeStatusValues().get(aValue);
            							} else { 
            								// non-unique match.
            								matchKey = "";
            							}
            						}
             					}
            					if (matched && matchKey.length()>0) { 
            						changeProposed = true;
            						result.addComment("Provided first word from of dwc:typeStatus [" + typeStatusWord + "] conformed to the the sourceAuthority");
            						outputBit = outputBit.replace(typeStatusWord,matchKey);
            					} else { 
            						result.addComment("Provided first word from value of dwc:typeStatus [" + typeStatusWord + "] unable to be conformed to the the sourceAuthority");
            						outputBit = outputBit;
            					}
            				}
        				}
        				outputBits.add(outputBit);
        			}
        			if (allFound) { 
        				result.addComment("All provided first words of value of dwc:typeStatus found in the sourceAuthority " + sourceAuthority);
        				result.setResultState(ResultState.NOT_AMENDED);	
        			} else { 
        				if (changeProposed) { 
        					Iterator<String> j = outputBits.iterator();
        					String separator = "";
        					StringBuilder output = new StringBuilder();
        					while (j.hasNext()) { 
        						output.append(separator).append(j.next());
        						separator = " | ";
        					}
       						result.addComment("First word(s) of provided value of dwc:typeStatus [" + typeStatus + "] conformed to the the sourceAuthority " + sourceAuthority);
       						result.setResultState(ResultState.AMENDED);	
       						Map<String, String> values = new HashMap<>();
       						values.put("dwc:typeStatus", output.toString()) ;
       						result.setValue(new AmendmentValue(values));
        				} else { 
       						result.addComment("No first word from provided value of dwc:typeStatus [" + typeStatus + "] able to be conformed to the the sourceAuthority " + sourceAuthority);
       						result.setResultState(ResultState.NOT_AMENDED);
        				}
        			}
        			
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception e) {
        		result.addComment("Error evaluating dwc:typeStatus: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	}
        }
        
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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/0fa16c7e-eb9c-4add-9193-aca6087d6636/2024-02-10")
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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/fffdc42b-b15e-4450-9e6a-f4181a319106/2024-02-10")
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

    /**
    * Is dwc:individualCount an Integer ?
    *
    * Provides: 290 VALIDATION_INDIVIDUALCOUNT_INTEGER
    * Version: 2024-02-11
    *
    * @param individualCount the provided dwc:individualCount to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_INDIVIDUALCOUNT_INTEGER", description="Is dwc:individualCount an Integer ?")
    @Provides("43abded0-c3bf-44e7-8c1f-c4207608b1fa")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/43abded0-c3bf-44e7-8c1f-c4207608b1fa/2024-02-11")
    @Specification("COMPLIANT if the value of dwc:individualCount is interpretable an integer; otherwise NOT_COMPLIANT. ")
    public static DQResponse<ComplianceValue> validationIndividualcountInteger(
        @ActedUpon("dwc:individualCount") String individualCount
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if the value of dwc:individualCount is interpretable 
        // an integer; otherwise NOT_COMPLIANT. 

        result.setResultState(ResultState.RUN_HAS_RESULT);
        if (MetadataUtils.isEmpty(individualCount)) { 
        	result.setValue(ComplianceValue.NOT_COMPLIANT);
        	result.addComment("Provided value for individualCount is empty, not an integer.");
        } else if (individualCount.trim().matches("^[0-9]+$")) { 
        	result.setValue(ComplianceValue.COMPLIANT);
        	result.addComment("Provided value for individualCount is an integer.");
        } else { 
        	result.setValue(ComplianceValue.NOT_COMPLIANT);
        	result.addComment("Provided value for individualCount ["+individualCount+"] is not an integer.");
        }
        
        return result;
    }


    /**
    * Propose amendment to the value of dwc:preparations using bdq:sourceAuthority.
    *
    * Provides: 282 AMENDMENT_REPRODUCTIVECONDITION_STANDARDIZED
    * Version: 2024-02-09
    *
    * @param reproductiveCondition the provided dwc:reproductiveCondition to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_REPRODUCTIVECONDITION_STANDARDIZED", description="Propose amendment to the value of dwc:preparations using bdq:sourceAuthority.")
    @Provides("10ef7660-3e08-4b74-95d3-66131275cf31")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/10ef7660-3e08-4b74-95d3-66131275cf31/2024-02-09")
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
    * Provides: 280 AMENDMENT_PREPARATIONS_STANDARDIZED
    * Version: 2024-02-09
    *
    * @param preparations the provided dwc:preparations to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_PREPARATIONS_STANDARDIZED", description="Propose amendment to the value of dwc:preparations using bdq:sourceAuthority.")
    @Provides("8d0cbfee-4524-4da3-83d4-bebc4c4f7cd2")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/8d0cbfee-4524-4da3-83d4-bebc4c4f7cd2/2024-02-09")
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
    * Does the value of dwc:lifeStage occur in bdq:sourceAuthority?
    *
    * Provides: #270 VALIDATION_LIFESTAGE_STANDARD
    * Version: 2024-02-09
    *
    * @param lifeStage the provided dwc:lifeStage to evaluate as ActedUpon.
    * @param sourceAuthority the bdq:sourceAuthority to consult.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_LIFESTAGE_STANDARD", description="Does the value of dwc:lifeStage occur in bdq:sourceAuthority?")
    @Provides("be40d19e-1fe7-42ed-b9d0-961f4cf3eb6a")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/be40d19e-1fe7-42ed-b9d0-961f4cf3eb6a/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:lifeStage is EMPTY; COMPLIANT if the value of dwc:lifeStage is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core lifeStage [https://dwc.tdwg.org/list/#dwc_lifeStage]} {dwc:lifeStage vocabulary' [https://api.gbif.org/v1/vocabularies/LifeStage/concepts]}")
    public static DQResponse<ComplianceValue> validationLifestageStandard(
        @ActedUpon("dwc:lifeStage") String lifeStage,
        @Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:lifeStage 
        // is EMPTY; COMPLIANT if the value of dwc:lifeStage is in 
        // the bdq:sourceAuthority; otherwise NOT_COMPLIANT. 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority
        // bdq:sourceAuthority default = "GBIF LifeStage Vocabulary" 
        // [https://api.gbif.org/v1/vocabularies/LifeStage]} {"dwc:lifeStage vocabulary API" 
        // [https://api.gbif.org/v1/vocabularies/LifeStage/concepts]}
        
        if (MetadataUtils.isEmpty(lifeStage)) { 
        	result.addComment("No Value provided for dwc:lifeStage");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (MetadataUtils.isEmpty(sourceAuthority)) { 
        		sourceAuthority = "GBIF LifeStage Vocabulary";
        	}
        	try { 
        		MetadataSourceAuthority sourceAuthorityObject = new MetadataSourceAuthority(sourceAuthority);
        		if (!MetadataSingleton.getInstance().isLoaded()) { 
        			result.addComment("Error accessing sourceAuthority: " + MetadataSingleton.getInstance().getLoadError() );
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        		} else { 
        			result.setResultState(ResultState.RUN_HAS_RESULT);	
        			if (MetadataSingleton.getInstance().getLifeStageTerms().containsKey(lifeStage)) { 
        				result.addComment("Provided value of dwc:lifeStage found in the sourceAuthority");
        				result.setValue(ComplianceValue.COMPLIANT);
        			} else {
        				result.addComment("Provided value of dwc:lifeStage [" + lifeStage + "] not found in the sourceAuthority");
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        			}
        		}
        	} catch (SourceAuthorityException e) { 
        		result.addComment("Error with specified bdq:sourceAuthority ["+ sourceAuthority +"]: " + e.getMessage());
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);	
        	} catch (Exception e) {
        		
        	}
        }

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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/76067adf-1422-4d03-89e3-9067c3043700/2024-02-07")
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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/2aa1b7a0-0473-4a90-bf11-a02137c5c65b/2024-02-07")
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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/3bd2477c-6497-43b0-94e6-b811eed1b1cb/2024-02-04")
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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/986ad95d-ffa1-4e3b-a6cb-ed943c87be0d/2024-02-04")
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
    * Provides: #235 VALIDATION_LIFESTAGE_NOTEMPTY
    * Version: 2024-01-29
    *
    * @param lifeStage the provided dwc:lifeStage to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_LIFESTAGE_NOTEMPTY", description="Is there a value in dwc:lifeStage?")
    @Provides("34b9eec9-03d5-4dc9-94b7-5b05ddcaaa87")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/34b9eec9-03d5-4dc9-94b7-5b05ddcaaa87/2024-01-29")
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
    * Provides: #232 VALIDATION_INDIVIDUALCOUNT_NOTEMPTY
    * Version: 2024-01-29
    *
    * @param individualCount the provided dwc:individualCount to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_INDIVIDUALCOUNT_NOTEMPTY", description="Is there a value in dwc:individualCount?")
    @Provides("aff0facd-1d2a-40a5-a55a-61f950cd68a0")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/aff0facd-1d2a-40a5-a55a-61f950cd68a0/2024-01-29")
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
    * Provides: #225 VALIDATION_DISPOSITION_NOTEMPTY
    * Version: 2024-01-29
    *
    * @param disposition the provided dwc:disposition to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_DISPOSITION_NOTEMPTY", description="Is there a value in dwc:disposition?")
    @Provides("b4c17611-2703-474f-b46a-93b08ecfee16")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/b4c17611-2703-474f-b46a-93b08ecfee16/2024-01-29")
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

}
