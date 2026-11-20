# rec_occur_qc
Data Quality library for selected dwc:Record level and dwc:Occurrence terms, including implementations of BDQ Standard tests from the TDWG Biodiversity Data Quality Task Group 2 labeled as OTHER.  

DOI: 10.5281/zenodo.14968501

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.14968501.svg)](https://doi.org/10.5281/zenodo.14968501)

This library provides tests for evaluating the quality of data expressed using Darwin Core terms for occurrence and record level data.  The tests are implemented as static methods in the class org.filteredpush.qc.metadata.DwCMetadataDQ and are annotated with the API provided by the ffdq-api for reporting data quality assertions in terms of the bdqffdq Framework.  The tests are based on the TDWG Biodiversity Data Quality Task Group 2 test specifications in the (draft) BDQ Standard.

See: [TG2 Tests labeled OTHER](https://github.com/tdwg/bdq/issues?q=is%3Aopen+label%3AOTHER+NOT+measure)

## BDQ Standard Tests

rec_occur_qc implements the following CORE Tests of the draft TDWG [BDQ Standard](https://bdq.tdwg.org/) ([review draft](https://bdq.tdwg.org/draft/)). Each links to its entry in the BDQ Quick Reference Guide.

**Validations**

- [VALIDATION_BASISOFRECORD_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_BASISOFRECORD_NOTEMPTY)
- [VALIDATION_BASISOFRECORD_STANDARD](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_BASISOFRECORD_STANDARD)
- [VALIDATION_DCTYPE_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_DCTYPE_NOTEMPTY)
- [VALIDATION_DCTYPE_STANDARD](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_DCTYPE_STANDARD)
- [VALIDATION_DEGREEOFESTABLISHMENT_STANDARD](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_DEGREEOFESTABLISHMENT_STANDARD)
- [VALIDATION_ESTABLISHMENTMEANS_STANDARD](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_ESTABLISHMENTMEANS_STANDARD)
- [VALIDATION_LICENSE_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_LICENSE_NOTEMPTY)
- [VALIDATION_LICENSE_STANDARD](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_LICENSE_STANDARD)
- [VALIDATION_OCCURRENCEID_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_OCCURRENCEID_NOTEMPTY)
- [VALIDATION_OCCURRENCESTATUS_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_OCCURRENCESTATUS_NOTEMPTY)
- [VALIDATION_OCCURRENCESTATUS_STANDARD](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_OCCURRENCESTATUS_STANDARD)
- [VALIDATION_PATHWAY_STANDARD](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_PATHWAY_STANDARD)
- [VALIDATION_SEX_STANDARD](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_SEX_STANDARD)
- [VALIDATION_TYPESTATUS_STANDARD](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#VALIDATION_TYPESTATUS_STANDARD)

**Amendments**

- [AMENDMENT_BASISOFRECORD_STANDARDIZED](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_BASISOFRECORD_STANDARDIZED)
- [AMENDMENT_DCTYPE_STANDARDIZED](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_DCTYPE_STANDARDIZED)
- [AMENDMENT_DEGREEOFESTABLISHMENT_STANDARDIZED](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_DEGREEOFESTABLISHMENT_STANDARDIZED)
- [AMENDMENT_ESTABLISHMENTMEANS_STANDARDIZED](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_ESTABLISHMENTMEANS_STANDARDIZED)
- [AMENDMENT_LICENSE_STANDARDIZED](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_LICENSE_STANDARDIZED)
- [AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT)
- [AMENDMENT_OCCURRENCESTATUS_STANDARDIZED](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_OCCURRENCESTATUS_STANDARDIZED)
- [AMENDMENT_PATHWAY_STANDARDIZED](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_PATHWAY_STANDARDIZED)
- [AMENDMENT_SEX_STANDARDIZED](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_SEX_STANDARDIZED)
- [AMENDMENT_TYPESTATUS_STANDARDIZED](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#AMENDMENT_TYPESTATUS_STANDARDIZED)

**Measures**

- [MEASURE_AMENDMENTS_PROPOSED](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#MEASURE_AMENDMENTS_PROPOSED)
- [MEASURE_VALIDATIONTESTS_COMPLIANT](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#MEASURE_VALIDATIONTESTS_COMPLIANT)
- [MEASURE_VALIDATIONTESTS_NOTCOMPLIANT](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#MEASURE_VALIDATIONTESTS_NOTCOMPLIANT)
- [MEASURE_VALIDATIONTESTS_PREREQUISITESNOTMET](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#MEASURE_VALIDATIONTESTS_PREREQUISITESNOTMET)

**Issues**

- [ISSUE_ANNOTATION_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#ISSUE_ANNOTATION_NOTEMPTY)
- [ISSUE_DATAGENERALIZATIONS_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#ISSUE_DATAGENERALIZATIONS_NOTEMPTY)
- [ISSUE_ESTABLISHMENTMEANS_NOTEMPTY](https://bdq.tdwg.org/draft/docs/terms/bdqtest/#ISSUE_ESTABLISHMENTMEANS_NOTEMPTY)

### Supplementary Tests

These Tests are not in the BDQ Standard; each links to its tdwg/bdq issue.

**Validations**

- VALIDATION_DEGREEOFESTABLISHMENT_NOTEMPTY ([tdwg/bdq#288](https://github.com/tdwg/bdq/issues/288))
- VALIDATION_DISPOSITION_NOTEMPTY ([tdwg/bdq#225](https://github.com/tdwg/bdq/issues/225))
- VALIDATION_INDIVIDUALCOUNT_INTEGER ([tdwg/bdq#290](https://github.com/tdwg/bdq/issues/290))
- VALIDATION_INDIVIDUALCOUNT_NOTEMPTY ([tdwg/bdq#232](https://github.com/tdwg/bdq/issues/232))
- VALIDATION_LIFESTAGE_NOTEMPTY ([tdwg/bdq#235](https://github.com/tdwg/bdq/issues/235))
- VALIDATION_LIFESTAGE_STANDARD ([tdwg/bdq#270](https://github.com/tdwg/bdq/issues/270))
- VALIDATION_OCCURRENCEID_STANDARD ([tdwg/bdq#23](https://github.com/tdwg/bdq/issues/23))
- VALIDATION_PATHWAY_NOTEMPTY ([tdwg/bdq#289](https://github.com/tdwg/bdq/issues/289))
- VALIDATION_PREPARATIONS_NOTEMPTY ([tdwg/bdq#260](https://github.com/tdwg/bdq/issues/260))
- VALIDATION_RECORDEDBY_NOTEMPTY ([tdwg/bdq#242](https://github.com/tdwg/bdq/issues/242))
- VALIDATION_RECORDNUMBER_NOTEMPTY ([tdwg/bdq#243](https://github.com/tdwg/bdq/issues/243))
- VALIDATION_RELATIONSHIPOFRESOURCE_NOTEMPTY ([tdwg/bdq#261](https://github.com/tdwg/bdq/issues/261))
- VALIDATION_REPRODUCTIVECONDITION_NOTEMPTY ([tdwg/bdq#262](https://github.com/tdwg/bdq/issues/262))
- VALIDATION_SEX_NOTEMPTY ([tdwg/bdq#263](https://github.com/tdwg/bdq/issues/263))

**Amendments**

- AMENDMENT_LIFESTAGE_STANDARDIZED ([tdwg/bdq#271](https://github.com/tdwg/bdq/issues/271))
- AMENDMENT_PREPARATIONS_STANDARDIZED ([tdwg/bdq#280](https://github.com/tdwg/bdq/issues/280))
- AMENDMENT_REPRODUCTIVECONDITION_STANDARDIZED ([tdwg/bdq#282](https://github.com/tdwg/bdq/issues/282))

Also experimental, and not a BDQ Test proposal: VALIDATION_PREPARATIONS_STANDARD.


# Include using maven

Available in [![Maven Central](https://img.shields.io/maven-central/v/org.filteredpush/rec_occur_qc)](https://central.sonatype.com/artifact/org.filteredpush/rec_occur_qc)

    <dependency>
        <groupId>org.filteredpush</groupId>
        <artifactId>rec_occur_qc</artifactId>
        <version>1.2.0</version>
    </dependency>

# Building

    mvn package

Library jar will be produced in /target/rec_occur_qc-{version}.jar

An executable jar will be produced in /rec_occur_qc-{version}-{gitcommit}-executable.jar.  This jar is not installed in the local maven repository or deployed to maven central with maven install or maven deploy.


## DwCMetadataDQ - implementations of standard tests in the fittnes for use framework

This library provides the classes org.filteredpush.qc.metadata.DwCMetadataDQ and DwCMetadataDQDefaults.

DwCMetadataDQ provides a set of static methods for working with various Darwin Core metadata terms returning objects that implement the API provided by ffdq-api for reporting data quality assertions in terms of the Fit4U Framework. 

As of 2024-07-28, the current set of Other test specifications in the core (along with most Supplementary) test suite by the TDWG BDQIG TG2 tests and assertions task group have implementations in DwCMetadataDQ.  Methods for tests that can be parameterized can be invoked from methods in DwCMetadataDQDefaults that do not take a parameter, but use the default value for any parameter as provided for in the test specifications.   Methods implementing CORE and Supplementary tests are annotated with @Provides, @Validation/Amendment/Measure, @ProvidesVersion, and @Specification annotations in the form:   

    @Validation(label="VALIDATION_ESTABLISHMENTMEANS_STANDARD", description="Does the value of dwc:establishmentMeans occur in bdqval:sourceAuthority?")
    @Provides("4eb48fdf-7299-4d63-9d08-246902e2857f")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/version/4eb48fdf-7299-4d63-9d08-246902e2857f-2024-02-08")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdqval:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:establishmentMeans is EMPTY; COMPLIANT if the value of dwc:establishmentMeans is in the bdqval:sourceAuthority; otherwise NOT_COMPLIANT. bdqval:sourceAuthority default = 'Darwin Core establishmentMeans' {[https://dwc.tdwg.org/list/#dwc_establishmentMeans]} {dwc:establishmentMeans vocabulary API [https://api.gbif.org/v1/vocabularies/EstablishmentMeans/concepts]}")
    public static DQResponse<ComplianceValue> validationEstablishmentmeansStandard(
        @ActedUpon("dwc:establishmentMeans") String establishmentMeans,
        @Parameter(name="bdqval:sourceAuthority") String sourceAuthority
    ) {
       ...
    } 

For Tests in the BDQ standard, `@ProvidesVersion` is the IRI of the Test's version on its DateLastUpdated, in rs.tdwg.org's canonical form `https://rs.tdwg.org/bdqtest/terms/version/<uuid>-<date>` (served during the public review at https://bdq-public-review.rs.tdwg.org/). For Tests not in the standard (e.g. Supplementary), which have no rs.tdwg.org IRI, it is `urn:uuid:<uuid>`, with the version date in the method's javadoc.

As of version 1.0.1, all of the annotations are up to date with the 2025-03-03 BDQ Standard specifications.

DwCMetadataDQ also implements the four SingleRecord Measures whose ActedUpon information element is the set of responses of the other Tests run on the same record: MEASURE_VALIDATIONTESTS_COMPLIANT, MEASURE_VALIDATIONTESTS_NOTCOMPLIANT, and MEASURE_VALIDATIONTESTS_PREREQUISITESNOTMET (bdqval:AllValidationTestsRunOnSingleRecord), and MEASURE_AMENDMENTS_PROPOSED (bdqval:AllAmendmentTestsRunOnSingleRecord), following their 2026-11-12 specifications.  Each takes a collection of zero or more DQResponse objects and returns RUN_HAS_RESULT with the number of those responses that have the counted outcome, 0 if there are none.  Assembling that collection is the responsibility of the implementation framework that runs the tests: it must present, for each record, the responses of one run under one use case, on that single record, one for each distinct Test of the counted type (VALIDATION or AMENDMENT) that was run.

    @Measure(label="MEASURE_VALIDATIONTESTS_COMPLIANT", description="A count of the distinct VALIDATION Tests run on a record that have Response.result=COMPLIANT.", dimension=Dimension.RELIABILITY)
    @Provides("45fb49eb-4a1b-4b49-876f-15d5034dfc73")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/version/45fb49eb-4a1b-4b49-876f-15d5034dfc73-2026-11-12")
    public static DQResponse<NumericalValue> measureValidationtestsCompliant(
        @ActedUpon("bdqval:AllValidationTestsRunOnSingleRecord") Collection<? extends DQResponse<?>> validationResponses
    )

These tests are subject to change if there are further changes to the TDWG BDQ TG2 BDQ Standard specifications.   The current test specifications can be found [in a csv file](https://github.com/tdwg/bdq/blob/master/tg2/core/TG2_tests.csv) with rationalle management in [issues in the tdwg/bdq issue tracker](https://github.com/tdwg/bdq/labels/TIME).  The TDWG BDQ TG2 is preparing a draft standard for biodiversity data quality including these tests.  As of 2025-03-03, specifications for the the tests implemented here are expected to be stable.  

These test implementations can be validated against the TDWG BDQ TG2 [test validation data] (https://github.com/tdwg/bdq/blob/master/tg2/core/TG2_test_validation_data.csv) using [bdqtestrunner](https://github.com/FilteredPush/bdqtestrunner).  As of 2025-03-03, all relevant tests pass against the test validation data, details below.

The unit test below shows an example of a call on DwCMetadataDQ.amendmentLicenseStandardized() to perform an amendment of presented values of dc:license, and invocations of getResultState(), getValue(), and getComment() on the returned implementation of the DQResponse interface.

	@Test
	public void testAmendmentLicenseStandardized() {
		String license = "";
		DQResponse<AmendmentValue> result = DwCMetadataDQ.amendmentLicenseStandardized(license, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		assertNotNull(result.getComment());
		
		license = "foo";
		result = DwCMetadataDQ.amendmentLicenseStandardized(license, "https://invalid/invalidauthority");
		logger.debug(result.getComment());
		assertEquals(ResultState.EXTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		assertNotNull(result.getComment());
		
		license = "http://creativecommons.org/publicdomain/zero/1.0/legalcode";
		result = DwCMetadataDQ.amendmentLicenseStandardized(license, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		assertNotNull(result.getComment());
		
		license = "CC-BY";
		result = DwCMetadataDQ.amendmentLicenseStandardized(license, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		assertEquals("https://creativecommons.org/licenses/by/4.0/", result.getValue().getObject().get("dc:license"));
		assertNotNull(result.getComment());
		
	}
    
The APIs for both the java annotations for the framework and the result objects for the framework are not expected to change.    

26 of 26 implementations of the TDWG BDQ TG2 test specifications are passing against all related validation data (245 test cases) with the 2025-03-03 [test validation data](https://github.com/tdwg/bdq/tree/master/tg2/core/TG2_test_validation_data.csv) including [non-printing characters](https://github.com/tdwg/bdq/tree/master/tg2/core/TG2_test_validation_data_nonprintingchars.csv)). 

	java -jar bdqtestrunner-1.0.1-SNAPSHOT-2428fbb-executable.jar -c DwCMetadataDQDefaults > output.log

	Validation Test Data From: https://raw.githubusercontent.com/tdwg/bdq/master/tg2/core/TG2_test_validation_data.csv
	2025-03-04T09:37:29.655184712
	Validating Test Implementations In:
	org.filteredpush.qc.metadata.DwCMetadataDQDefaults
	Ran 26 tests against the validation data.
	74ef1034-e289-4596-b5b0-cde73796697d 10 P:10 F: 0 AMENDMENT_DEGREEOFESTABLISHMENT_STANDARDIZED #276
	33c45ae1-e2db-462a-a59e-7169bb01c5d6 10 P:10 F: 0 AMENDMENT_SEX_STANDARDIZED #284
	4833a522-12eb-4fe0-b4cf-7f7a337a6048  9 P: 9 F: 0 VALIDATION_TYPESTATUS_STANDARD #285
	88d8598b-3318-483d-9475-a5acf9887404 10 P:10 F: 0 VALIDATION_SEX_STANDARD #283
	acc8dff2-d8d1-483a-946d-65a02a452700  9 P: 9 F: 0 ISSUE_ESTABLISHMENTMEANS_NOTEMPTY #94
	3136236e-04b6-49ea-8b34-a65f25e3aba1 11 P:11 F: 0 VALIDATION_LICENCE_STANDARD #38
	cdaabb0d-a863-49d0-bc0f-738d771acba5 11 P:11 F: 0 VALIDATION_DCTYPE_STANDARD #91
	c486546c-e6e5-48a7-b286-eba7f5ca56c4  8 P: 8 F: 0 VALIDATION_OCCURRENCEID_NOTEMPTY #47
	eb4a17f6-6bea-4cdd-93dd-d5a7e9d1eccf 11 P:11 F: 0 VALIDATION_OCCURRENCESTATUS_NOTEMPTY #117
	13d5a10e-188e-40fd-a22c-dbaa87b91df2 10 P:10 F: 0 ISSUE_DATAGENERALIZATIONS_NOTNOTEMPTY #72
	42408a00-bf71-4892-a399-4325e2bc1fb8 15 P:15 F: 0 VALIDATION_BASISOFRECORD_STANDARD #104
	f9205977-f145-44f5-8cb9-e3e2e35ce908 10 P:10 F: 0 AMENDMENT_PATHWAY_STANDARDIZED #278
	bd385eeb-44a2-464b-a503-7abe407ef904  7 P: 7 F: 0 AMENDMENT_DCTYPE_STANDARDIZED #41
	4eb48fdf-7299-4d63-9d08-246902e2857f  9 P: 9 F: 0 VALIDATION_ESTABLISHMENTMEANS_STANDARD #268
	07c28ace-561a-476e-a9b9-3d5ad6e35933 10 P:10 F: 0 AMENDMENT_BASISOFRECORD_STANDARDIZED #63
	374b091a-fc90-4791-91e5-c1557c649169  9 P: 9 F: 0 VALIDATION_DCTYPE_NOTEMPTY #103
	ac2b7648-d5f9-48ca-9b07-8ad5879a2536 10 P:10 F: 0 VALIDATION_BASISOFRECORD_NOTEMPTY #58
	15d15927-7a22-43f8-88d6-298f5eb45c4c 10 P:10 F: 0 AMENDMENT_ESTABLISHMENTMEANS_STANDARDIZED #269
	5424e933-bee7-4125-839e-d8743ea69f93  9 P: 9 F: 0 VALIDATION_PATHWAY_STANDARD #277
	b3471c65-b53e-453b-8282-abfa27bf1805 10 P:10 F: 0 AMENDMENT_TYPESTATUS_STANDARDIZED #286
	7af25f1e-a4e2-4ff4-b161-d1f25a5c3e47 10 P:10 F: 0 VALIDATION_OCCURRENCESTATUS_STANDARD #116
	15f78619-811a-4c6f-997a-a4c7888ad849  8 P: 8 F: 0 VALIDATION_LICENCE_NOTEMPTY #99
	96667a0a-ae59-446a-bbb0-b7f2b0ca6cf5  6 P: 6 F: 0 AMENDMENT_OCCURRENCESTATUS_ASSUMEDDEFAULT #75
	f8f3a093-042c-47a3-971a-a482aaaf3b75  7 P: 7 F: 0 AMENDMENT_OCCURRENCESTATUS_STANDARDIZED #115
	060e7734-607d-4737-8b2c-bfa17788bf1a  9 P: 9 F: 0 VALIDATION_DEGREEOFESTABLISHMENT_STANDARD #275
	dcbe5bd2-42a0-4aab-bb4d-8f148c6490f8  7 P: 7 F: 0 AMENDMENT_LICENSE_STANDARDIZED #133
	Test cases: 245
	Total cases with no implementation: 1059
	Total dataID validation rows: 1305
	Header Lines Skipped: 1

# Developer deployment: 

To deploy a snapshot to the snapshotRepository: 

    mvn clean deploy

To deploy a new release to maven central, set the version in pom.xml to a non-snapshot version, update the @Mechanism metadata (in the classes, generation configuration, and rdf), then deploy with the release profile (which adds package signing and deployment to release staging), then confirm release.

1. Set version in pom.xml to a non-snapshot version

2. Update @Mechanism metadata in: 

	generation/metadata_tests.tt
	generation/rec_occur_qc_DwCMetadata_kurator_ffdq.config  
	generation/rec_occur_qc_DwCMetadata_stubs_kurator_ffdq.config
	src/main/java/org/filteredpush/qc/metadata/DwCMetadataDQ.java
	src/main/java/org/filteredpush/qc/metadata/DwCMetadataDQDefaults.java 

3. Deploy using the release profile.

    mvn clean deploy -P release

4. Confirm release by loging to the sonatype oss repository hosting nexus instance (https://oss.sonatype.org/index.html#welcome), and searching for the artifact, or checking the staging repositories if it is held up in staging.

## Related BDQ libraries and applications

This library is one of a set of libraries and applications for the draft TDWG [BDQ Standard](https://bdq.tdwg.org/) (Biodiversity Data Quality), about to go into public review ([review draft](https://bdq.tdwg.org/draft/)). None of them are part of the standard.

| Repository | Kind | Role | Available from |
|---|---|---|---|
| [ffdq-api](https://github.com/kurator-org/ffdq-api) | Library | Java annotations and result API for Test implementations | [Maven Central](https://central.sonatype.com/artifact/org.datakurator/ffdq-api), [latest release](https://github.com/kurator-org/ffdq-api/releases/latest) |
| [kurator-ffdq](https://github.com/kurator-org/kurator-ffdq) | Library | bdqffdq model, RDF reports, Test RDF generation and Test runner | [Maven Central](https://central.sonatype.com/artifact/org.datakurator/kurator-ffdq), [latest release](https://github.com/kurator-org/kurator-ffdq/releases/latest) |
| [sci_name_qc](https://github.com/FilteredPush/sci_name_qc) | Test implementations | NAME Tests | [Maven Central](https://central.sonatype.com/artifact/org.filteredpush/sci_name_qc), [latest release](https://github.com/FilteredPush/sci_name_qc/releases/latest) |
| [event_date_qc](https://github.com/FilteredPush/event_date_qc) | Test implementations | TIME Tests | [Maven Central](https://central.sonatype.com/artifact/org.filteredpush/event_date_qc), [latest release](https://github.com/FilteredPush/event_date_qc/releases/latest) |
| [rec_occur_qc](https://github.com/FilteredPush/rec_occur_qc) (this repository) | Test implementations | OTHER (metadata) Tests and SingleRecord Measures | [Maven Central](https://central.sonatype.com/artifact/org.filteredpush/rec_occur_qc), [latest release](https://github.com/FilteredPush/rec_occur_qc/releases/latest) |
| [geo_ref_qc](https://github.com/FilteredPush/geo_ref_qc) | Test implementations | SPACE Tests | [Maven Central](https://central.sonatype.com/artifact/org.filteredpush/geo_ref_qc), [latest release](https://github.com/FilteredPush/geo_ref_qc/releases/latest) |
| [bdqtestrunner](https://github.com/FilteredPush/bdqtestrunner) | Application | Checks Test implementations against the BDQ Test validation data | [latest release](https://github.com/FilteredPush/bdqtestrunner/releases/latest) |
| [bdq_workbench](https://github.com/FilteredPush/bdq_workbench) | Application | Runs BDQ Tests over Darwin Core data for a Use Case | [latest release](https://github.com/FilteredPush/bdq_workbench/releases/latest) |
| [bdq_usecase_builder](https://github.com/FilteredPush/bdq_usecase_builder) | Application | Authors Use Cases and Test definitions | [latest release](https://github.com/FilteredPush/bdq_usecase_builder/releases/latest) |
| [bdq_issue_to_csv](https://github.com/kurator-org/bdq_issue_to_csv) | Application | Converts tdwg/bdq Test issues to a term-version CSV | [latest release](https://github.com/kurator-org/bdq_issue_to_csv/releases/latest) |
