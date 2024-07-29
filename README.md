# rec_occur_qc
Data Quality library for selected dwc:Record level and dwc:Occurrence terms.

Implementations of tests from the TDWG Biodiversity Data Quality Task Group 2 labeled as OTHER. 

See: [TG2 Tests labeled OTHER](https://github.com/tdwg/bdq/issues?q=is%3Aopen+label%3AOTHER+NOT+measure)


# Include using maven

Available in Maven Central.

    <dependency>
        <groupId>org.filteredpush</groupId>
        <artifactId>rec_occur_qc</artifactId>
        <version>1.0.0</version>
    </dependency>

# Building

    mvn package

Library jar will be produced in /target/rec_occur_qc-{version}.jar

An executable jar will be produced in /rec_occur_qc-{version}-{gitcommit}-executable.jar.  This jar is not installed in the local maven repository or deployed to maven central with maven install or maven deploy.

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

