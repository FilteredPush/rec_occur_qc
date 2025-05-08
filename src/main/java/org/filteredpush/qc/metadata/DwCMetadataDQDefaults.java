/**
 * DwCMetadataDQDefaults.java
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Amendment;
import org.datakurator.ffdq.annotations.Consulted;
import org.datakurator.ffdq.annotations.Parameter;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.annotations.ProvidesVersion;
import org.datakurator.ffdq.annotations.Specification;
import org.datakurator.ffdq.annotations.Validation;
import org.datakurator.ffdq.annotations.Mechanism;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;

/**
 * @author mole
 *
 */
@Mechanism(value="09fd4e2f-cf10-4665-aa74-bcf8e3795163",label="Kurator: Metadata/Record-Level Validator - DwCMetadataDQ:v1.1.0")
public class DwCMetadataDQDefaults extends DwCMetadataDQ {

	private static final Log logger = LogFactory.getLog(DwCMetadataDQDefaults.class);
	
    /**
    * Proposes an amendment to the value of dwc:basisOfRecord using the default bdq:sourceAuthority.
    *
    * Provides: #63 AMENDMENT_BASISOFRECORD_STANDARDIZED
    * Version: 2024-07-24
    *
    * @param basisOfRecord the provided dwc:basisOfRecord to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_BASISOFRECORD_STANDARDIZED", description="Proposes an amendment to the value of dwc:basisOfRecord using the bdq:sourceAuthority.")
    @Provides("07c28ace-561a-476e-a9b9-3d5ad6e35933")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/07c28ace-561a-476e-a9b9-3d5ad6e35933/2024-07-24")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord is bdq:Empty; AMENDED the value of dwc:basisOfRecord if it could be unambiguously interpreted as a value in the bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority default = 'Darwin Core basisOfRecord' {[https://dwc.tdwg.org/terms/#dwc:basisOfRecord]} {dwc:basisOfRecord vocabulary [https://rs.gbif.org/vocabulary/dwc/basis_of_record.xml]}")
    public static DQResponse<AmendmentValue> amendmentBasisofrecordStandardized(
        @ActedUpon("dwc:basisOfRecord") String basisOfRecord
    ) {
    	return amendmentBasisofrecordStandardized(basisOfRecord,null);
    	
    }

    /**
     * Does the value of dwc:basisOfRecord occur in the bdq:sourceAuthority?
     * Using the default sourceAuthority.
     *
     * Provides: #104 VALIDATION_BASISOFRECORD_STANDARD
     * Version: 2024-08-18
     *
     * @param basisOfRecord the provided dwc:basisOfRecord to evaluate
     * @return DQResponse the response of type ComplianceValue to return
     */
    @Validation(label="VALIDATION_BASISOFRECORD_STANDARD", description="Does the value of dwc:basisOfRecord occur in the bdq:sourceAuthority?")
    @Provides("42408a00-bf71-4892-a399-4325e2bc1fb8")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/42408a00-bf71-4892-a399-4325e2bc1fb8/2024-08-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:basisOfRecord is bdq:Empty; COMPLIANT if the value of dwc:basisOfRecord is valid in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core basisOfRecord' {[https://dwc.tdwg.org/terms/#dwc:basisOfRecord]}{dwc:basisOfRecord vocabulary [https://rs.gbif.org/vocabulary/dwc/basis_of_record.xml]}")
    
    public static DQResponse<ComplianceValue> validationBasisofrecordStandard(@ActedUpon("dwc:basisOfRecord") String basisOfRecord) {
    	return validationBasisofrecordStandard(basisOfRecord, null);
    }
	
    /**
     * Does the value of dwc:occurrenceStatus occur in the bdq:sourceAuthority?
     * Using the default sourceAuthority.
     *
     * Provides: 116 VALIDATION_OCCURRENCESTATUS_STANDARD
     * Version: 2023-09-18
     *
     * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_OCCURRENCESTATUS_STANDARD", description="Does the value of dwc:occurrenceStatus occur in the bdq:sourceAuthority?")
    @Provides("7af25f1e-a4e2-4ff4-b161-d1f25a5c3e47")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/7af25f1e-a4e2-4ff4-b161-d1f25a5c3e47/2023-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:occurrenceStatus is bdq:Empty; COMPLIANT if the value of dwc:occurrenceStatus is resolved in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'GBIF OccurrenceStatus Vocabulary' [https://api.gbif.org/v1/vocabularies/OccurrenceStatus]} {'dwc:occurrenceStatus vocabulary API' [https://api.gbif.org/v1/vocabularies/OccurrenceStatus/concepts]}")
    
    public static DQResponse<ComplianceValue> validationOccurrencestatusStandard(
    		@ActedUpon("dwc:occurrenceStatus") String occurrenceStatus) {
    	return validationOccurrencestatusStandard(occurrenceStatus, null);
    }
    
    /**
    * Does the value of dcterms:license occur in bdq:sourceAuthority?
    *
    * Provides: VALIDATION_LICENSE_STANDARD
    * Version: 2024-03-21
    *
    * @param license the provided dcterms:license to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_LICENSE_STANDARD", description="Does the value of dcterms:license occur in bdq:sourceAuthority?")
    @Provides("3136236e-04b6-49ea-8b34-a65f25e3aba1")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/3136236e-04b6-49ea-8b34-a65f25e3aba1/2024-03-21")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dcterms:license is bdq:Empty; COMPLIANT if the value of the term dcterms:license is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Creative Commons 4.0 Licenses or CC0' {[https://creativecommons.org/]} { Regular Expression ^(http(s){0,1}://creativecommons.org/licenses/(by|by-sa|by-nc|by-nc-sa|by-nd|by-nc-nd)/4.0/((deed|legalcode)(.(id|eu|da|de|en|es|fr|fy|hr|it|lv|lt|mi|ni|no|pl|pt|ro|si|fi|sv|tr|cs|el|ru|uk|ar|jp|zh-hans|zh-hant|ko)){0,1})|(http(s){0,1}://creativecommons.org/publicdomain/zero/1.0/((deed|legalcode)(.(id|eu|da|de|en|es|fr|fy|hr|it|lv|lt|ni|no|pl|pt|ro|si|fi|sv|tr|cs|el|ru|uk|ar|jp|zh-hans|zh-hant|ko)){0,1})))$ }")
    public static DQResponse<ComplianceValue> validationLicenseStandard(
        @ActedUpon("dcterms:license") String license
    ) {
    	return validationLicenseStandard(license,null);
    }
   
    /**
    * Propose amendment to the value of dwc:license using the default bdq:sourceAuthority.
    *
    * Provides: 133 AMENDMENT_LICENSE_STANDARDIZED
    * Version: 2023-09-18
    *
    * @param license the provided dcterms:license to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_LICENSE_STANDARDIZED", description="Propose amendment to the value of dwc:license using bdq:sourceAuthority.")
    @Provides("dcbe5bd2-42a0-4aab-bb4d-8f148c6490f8")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/dcbe5bd2-42a0-4aab-bb4d-8f148c6490f8/2023-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; AMENDED value of dcterms:license if it could be unambiguously interpreted as a value in bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority default = 'Creative Commons' {[https://creativecommons.org/]} {Creative Commons licenses [https://creativecommons.org/about/cclicenses/]}")
    public static DQResponse<AmendmentValue> amendmentLicenseStandardized(
        @ActedUpon("dcterms:license") String license
    ) {
    	return amendmentLicenseStandardized(license, null);
    }
    
    /**
    * Does the value of dwc:lifeStage occur in bdq:sourceAuthority, using the default source authority?
    *
    * Provides: #270 VALIDATION_LIFESTAGE_STANDARD
    * Version: 2024-02-09
    *
    * @param lifeStage the provided dwc:lifeStage to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_LIFESTAGE_STANDARD", description="Does the value of dwc:lifeStage occur in bdq:sourceAuthority?")
    @Provides("be40d19e-1fe7-42ed-b9d0-961f4cf3eb6a")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/be40d19e-1fe7-42ed-b9d0-961f4cf3eb6a/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:lifeStage is EMPTY; COMPLIANT if the value of dwc:lifeStage is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core lifeStage [https://dwc.tdwg.org/list/#dwc_lifeStage]} {dwc:lifeStage vocabulary' [https://api.gbif.org/v1/vocabularies/LifeStage/concepts]}")
    public static DQResponse<ComplianceValue> validationLifestageStandard(
        @ActedUpon("dwc:lifeStage") String lifeStage
    ) {
    	return validationLifestageStandard(lifeStage,null);
    }
    
    /**
    * Does the value of dwc:pathway occur in bdq:sourceAuthority, using 
    * the default sourceAuthority?
    *
    * Provides: 277 VALIDATION_PATHWAY_STANDARD
    * Version: 2024-02-09
    *
    * @param pathway the provided dwc:pathway to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_PATHWAY_STANDARD", description="Does the value of dwc:pathway occur in bdq:sourceAuthority?")
    @Provides("5424e933-bee7-4125-839e-d8743ea69f93")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/5424e933-bee7-4125-839e-d8743ea69f93/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:pathway is EMPTY; COMPLIANT if the value of dwc:pathway is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core pathway' {[https://dwc.tdwg.org/list/#dwc_pathway]} {dwc:pathway vocabulary API [https://api.gbif.org/v1/vocabularies/Pathway/concepts]}")
    public static DQResponse<ComplianceValue> validationPathwayStandard(
        @ActedUpon("dwc:pathway") String pathway
    ) {
    	return validationPathwayStandard(pathway, null);
    }
    
    /**
    * Does the value of dwc:sex occur in bdq:sourceAuthority? Uses the default source authority.
    *
    * Provides: 283 VALIDATION_SEX_STANDARD
    * Version: 2024-02-09
    *
    * @param sex the provided dwc:sex to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_SEX_STANDARD", description="Does the value of dwc:sex occur in bdq:sourceAuthority?")
    @Provides("88d8598b-3318-483d-9475-a5acf9887404")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/88d8598b-3318-483d-9475-a5acf9887404/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:sex is EMPTY; COMPLIANT if the value of dwc:sex is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core sex' {[https://dwc.tdwg.org/list/#dwc_sex]} {dwc:sex vocabulary API [NO CURRENT API EXISTS]}")
    public static DQResponse<ComplianceValue> validationSexStandard(
        @ActedUpon("dwc:sex") String sex
    ) {
    	return validationSexStandard(sex, null);
    }
    
    /**
    * Propose amendment to the value of dwc:sex using bdq:sourceAuthority.
    * Uses the default source authority
    *
    * Provides: 284 AMENDMENT_SEX_STANDARDIZED
    * Version: 2024-03-25
    *
    * @param sex the provided dwc:sex to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_SEX_STANDARDIZED", description="Propose amendment to the value of dwc:sex using bdq:sourceAuthority.")
    @Provides("33c45ae1-e2db-462a-a59e-7169bb01c5d6")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/33c45ae1-e2db-462a-a59e-7169bb01c5d6/2024-03-25")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:sex is EMPTY; AMENDED the value of dwc:sex if it can be unambiguously matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED")
    public static DQResponse<AmendmentValue> amendmentSexStandardized(
        @ActedUpon("dwc:sex") String sex
    ) {
    	return amendmentSexStandardized(sex, null);
    }
    
    /**
    * Does the value of dwc:degreeOfEstablishment occur in bdq:sourceAuthority?
    * Uses the default source authority
    *
    * Provides: 275 VALIDATION_DEGREEOFESTABLISHMENT_STANDARD
    * Version: 2024-02-09
    *
    * @param degreeOfEstablishment the provided dwc:degreeOfEstablishment to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_DEGREEOFESTABLISHMENT_STANDARD", description="Does the value of dwc:degreeOfEstablishment occur in bdq:sourceAuthority?")
    @Provides("060e7734-607d-4737-8b2c-bfa17788bf1a")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/060e7734-607d-4737-8b2c-bfa17788bf1a/2024-02-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:degreeOfEstablishment is EMPTY; COMPLIANT if the value of dwc:degreeOfEstablishment is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT.")
    public static DQResponse<ComplianceValue> validationDegreeofestablishmentStandard(
        @ActedUpon("dwc:degreeOfEstablishment") String degreeOfEstablishment
    ) {
    	return validationDegreeofestablishmentStandard(degreeOfEstablishment, null);
    }
    
    /**
    * Proposes an amendment to the value of dwc:degreeOfEstablishment using the default bdq:sourceAuthority.
    *
    * Provides: 276 AMENDMENT_DEGREEOFESTABLISHMENT_STANDARDIZED
    * Version: 2024-04-16
    *
    * @param degreeOfEstablishment the provided dwc:degreeOfEstablishment to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_DEGREEOFESTABLISHMENT_STANDARDIZED", description="Proposes an amendment to the value of dwc:degreeOfEstablishment using the bdq:sourceAuthority.")
    @Provides("74ef1034-e289-4596-b5b0-cde73796697d")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/74ef1034-e289-4596-b5b0-cde73796697d/2024-04-16")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:degreeOfEstablishment is bdq:Empty; AMENDED the value of dwc:degreeOfEstablishment if it can be unambiguously matched to a term in the bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority default = 'Degree of Establishment Controlled Vocabulary List of Terms' {[https://dwc.tdwg.org/doe/]} {GBIF vocabulary API [https://api.gbif.org/v1/vocabularies/DegreeOfEstablishment/concepts]}")
    
    public static DQResponse<AmendmentValue> amendmentDegreeofestablishmentStandardized(
        @ActedUpon("dwc:degreeOfEstablishment") String degreeOfEstablishment
    ) {
    	return amendmentDegreeofestablishmentStandardized(degreeOfEstablishment, null);
    }
    
    /**
    * Propose an amendment to the value of dwc:pathway using the default bdq:sourceAuthority.
    *
    * Provides: 278 AMENDMENT_PATHWAY_STANDARDIZED
    * Version: 2024-09-18
    *
    * @param pathway the provided dwc:pathway to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_PATHWAY_STANDARDIZED", description="Propose an amendment to the value of dwc:pathway using the bdq:sourceAuthority.")
    @Provides("f9205977-f145-44f5-8cb9-e3e2e35ce908")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/f9205977-f145-44f5-8cb9-e3e2e35ce908/2024-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:pathway is bdq:Empty; AMENDED the value of dwc:pathway if it can be unambiguously matched to a term in the bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority default = 'Pathway Controlled Vocabulary List of Terms' {[https://dwc.tdwg.org/pw/]} {GBIF vocabulary API [https://api.gbif.org/v1/vocabularies/Pathway/concepts]}")
    
    public static DQResponse<AmendmentValue> amendmentPathwayStandardized(
        @ActedUpon("dwc:pathway") String pathway
    ) {
    	return amendmentPathwayStandardized(pathway,null);
    }
    
    /**
    * Does the value of dwc:establishmentMeans occur in the default bdq:sourceAuthority?
    *
    * Provides: 268 VALIDATION_ESTABLISHMENTMEANS_STANDARD
    * Version: 2024-02-08
    *
    * @param establishmentMeans the provided dwc:establishmentMeans to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_ESTABLISHMENTMEANS_STANDARD", description="Does the value of dwc:establishmentMeans occur in bdq:sourceAuthority?")
    @Provides("4eb48fdf-7299-4d63-9d08-246902e2857f")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/4eb48fdf-7299-4d63-9d08-246902e2857f/2024-02-08")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:establishmentMeans is EMPTY; COMPLIANT if the value of dwc:establishmentMeans is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'Darwin Core establishmentMeans' {[https://dwc.tdwg.org/list/#dwc_establishmentMeans]} {dwc:establishmentMeans vocabulary API [https://api.gbif.org/v1/vocabularies/EstablishmentMeans/concepts]}")
    public static DQResponse<ComplianceValue> validationEstablishmentmeansStandard(
        @ActedUpon("dwc:establishmentMeans") String establishmentMeans
    ) {
    	return validationEstablishmentmeansStandard(establishmentMeans, null);
    }
    
    /**
    * Propose amendment to the value of dwc:establishmentMeans using bdq:sourceAuthority.
    *
    * Provides: 269 AMENDMENT_ESTABLISHMENTMEANS_STANDARDIZED
    * Version: 2024-02-08
    *
    * @param establishmentMeans the provided dwc:establishmentMeans to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_ESTABLISHMENTMEANS_STANDARDIZED", description="Propose amendment to the value of dwc:establishmentMeans using bdq:sourceAuthority.")
    @Provides("15d15927-7a22-43f8-88d6-298f5eb45c4c")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/15d15927-7a22-43f8-88d6-298f5eb45c4c/2024-02-08")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:establishmentMeans is EMPTY; AMENDED the value of dwc:establishmentMeans if it can be unambiguously matched to a term in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority default = 'Darwin Core establishmentMeans' {[https://dwc.tdwg.org/list/#dwc_establishmentMeans]} {dwc:establishmentMeans vocabulary API [https://api.gbif.org/v1/vocabularies/EstablishmentMeans/concepts]}")
    public static DQResponse<AmendmentValue> amendmentEstablishmentmeansStandardized(
        @ActedUpon("dwc:establishmentMeans") String establishmentMeans
    ) {
    	return amendmentEstablishmentmeansStandardized(establishmentMeans, null);
    }
    
    /**
    * Proposes an amendment to the value of dwc:typeStatus using the default bdq:sourceAuthority.
    *
    * Provides: 286 AMENDMENT_TYPESTATUS_STANDARDIZED
    * Version: 2024-11-11
    *
    * @param typeStatus the provided dwc:typeStatus to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_TYPESTATUS_STANDARDIZED", description="Proposes an amendment to the value of dwc:typeStatus using the bdq:sourceAuthority.")
    @Provides("b3471c65-b53e-453b-8282-abfa27bf1805")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/b3471c65-b53e-453b-8282-abfa27bf1805/2024-11-11")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL PREREQUISITES_NOT_MET if dwc:typeStatus is bdq:Empty; AMENDED the value of the first word in each &#124; delimited portion of dwc:typeStatus if it can be unambiguously matched to a term in the bdq:sourceAuthority; otherwise NOT_AMENDED.. bdq:sourceAuthority default = 'GBIF TypeStatus Vocabulary' {[https://api.gbif.org/v1/vocabularies/TypeStatus]} {dwc:typeStatus vocabulary API [https://api.gbif.org/v1/vocabularies/TypeStatus]}")
    public static DQResponse<AmendmentValue> amendmentTypestatusStandardized(
        @ActedUpon("dwc:typeStatus") String typeStatus
    ) {
    	return amendmentTypestatusStandardized(typeStatus, null);
    }
    
    /**
    * Does the value of dwc:typeStatus occur in bdq:sourceAuthority?
    * Uses the default sourceAuthority.
    *
    * Provides: 285 VALIDATION_TYPESTATUS_STANDARD
    * Version: 2024-11-11
    *
    * @param typeStatus the provided dwc:typeStatus to evaluate as ActedUpon.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_TYPESTATUS_STANDARD", description="Does the value of dwc:typeStatus occur in bdq:sourceAuthority?")
    @Provides("4833a522-12eb-4fe0-b4cf-7f7a337a6048")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/4833a522-12eb-4fe0-b4cf-7f7a337a6048/2024-11-11")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:typeStatus is bdq:Empty; COMPLIANT if the value of the first word in each &#124; delimited portion of dwc:typeStatus is in the bdq:sourceAuthority; otherwise NOT_COMPLIANT.. bdq:sourceAuthority default = 'GBIF TypeStatus Vocabulary' {[https://api.gbif.org/v1/vocabularies/TypeStatus]} {dwc:typeStatus vocabulary API [https://api.gbif.org/v1/vocabularies/TypeStatus/concepts]}")
    
    public static DQResponse<ComplianceValue> validationTypestatusStandard(
        @ActedUpon("dwc:typeStatus") String typeStatus
    ) {
    	return validationTypestatusStandard(typeStatus, null);
    }
    
    /**
    * Propose an amendment of the value of dwc:occurrenceStatus to the default value of
    * present if dwc:occurrenceStatus, dwc:individualCount and dwc:organismQuantity are empty.
    *
    * Provides: 75 AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT
    * Version: 2024-11-13
    *
    * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate as ActedUpon.
    * @param individualCount the provided dwc:individualCount to evaluate as Consulted.
    * @param organismQuantity the provided dwc:organismQuantity to evaluate as Consulted.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT", description="Proposes an amendment of the value of dwc:occurrenceStatus to the default parameter value if dwc:occurrenceStatus, dwc:individualCount and dwc:organismQuantity are empty.")
    @Provides("96667a0a-ae59-446a-bbb0-b7f2b0ca6cf5")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/96667a0a-ae59-446a-bbb0-b7f2b0ca6cf5/2024-11-13")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:occurrenceStatus is bdq:NotEmpty; FILLED_IN the value of dwc:occurrenceStatus using the bdq:defaultOccurrenceStatus Parameter value if dwc:occurrenceStatus,  dwc:individualCount and dwc:organismQuantity are bdq:Empty; otherwise NOT_AMENDED. bdq:defaultOccurrenceStatus default = 'present'")
    public static DQResponse<AmendmentValue> amendmentOccurrencestatusAssumeddefault(
        @ActedUpon("dwc:occurrenceStatus") String occurrenceStatus, 
        @Consulted("dwc:individualCount") String individualCount, 
        @Consulted("dwc:organismQuantity") String organismQuantity
    ) {
    	return DwCMetadataDQ.amendmentOccurrencestatusAssumeddefault(occurrenceStatus, individualCount, organismQuantity, "present");
    }
    
    /**
    * Proposes an amendment to the value of dwc:occurrenceStatus using the default bdq:sourceAuthority.
    *
    * Provides: 115 AMENDMENT_OCCURRENCESTATUS_STANDARDIZED
    * Version: 2025-03-03
    *
    * @param occurrenceStatus the provided dwc:occurrenceStatus to evaluate as ActedUpon.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_OCCURRENCESTATUS_STANDARDIZED", description="Propose amendment to the value of dwc:occurrenceStatus using bdq:sourceAuthority.")
    @Provides("f8f3a093-042c-47a3-971a-a482aaaf3b75")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/f8f3a093-042c-47a3-971a-a482aaaf3b75/2025-03-03")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:ocurrenceStatus is bdq:Empty; AMENDED the value of dwc:occurrenceStatus if it can be unambiguously interpreted as a value in the bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority default = 'Regex present/absent' {['^(present|absent)$'] {\"dwc:occurrenceStatus vocabulary API\" [https://api.gbif.org/v1/vocabularies/OccurrenceStatus/concepts]}")
    public static DQResponse<AmendmentValue> amendmentOccurrencestatusStandardized(
        @ActedUpon("dwc:occurrenceStatus") String occurrenceStatus
    ) {
    	return amendmentOccurrencestatusStandardized(occurrenceStatus, null);
    }
    
}
