metadata_tests.csv are the subset of TDWG BDQ TG2 tests related to OTHER (Record-level, Occurrence) terms, excluding the broad measure tests.

To obtain this subset of tests from the list of core tests, assuming a checkout of tdwg/bdq in bdq in the parent directory of rec_occur_qc: 

    cd rec_occur_qc/generation
    head -n 1 ../../bdq/tg2/core/TG2_tests.csv > metadata_tests.csv
    grep OTHER ../../bdq/tg2/core/TG2_tests.csv  | grep -v AllDarwinCoreTerms | grep -v AllValidationTestsRunOnSingleRecord | grep -v AllAmendmentTestsRunOnSingleRecord >> metadata_tests.csv
    grep DATAGENERALIZATIONS ../../bdq/tg2/core/TG2_tests.csv | grep -v AllDarwinCoreTerms | grep -v AllValidationTestsRunOnSingleRecord | grep -v AllAmendmentTestsRunOnSingleRecord  >> metadata_tests.csv
    grep OTHER ../../bdq/tg2/supplementary/TG2_supplementary_tests.csv  | grep -v AllDarwinCoreTerms | grep -v AllValidationTestsRunOnSingleRecord | grep -v AllAmendmentTestsRunOnSingleRecord >> metadata_tests.csv
    cp ../../bdq/tg2/core/TG2_tests_additional_guids.csv additional_guids.csv
    grep -v Method ../../bdq/tg2/supplementary/TG2_supplementary_additional_guids.csv >> additional_guids.csv

Turtle RDF generated using kurator-ffdq using (from a kurator-ffdq directory in the same parent directory as rec_occur_qc) with:

   ./test-util.sh -config ../rec_occur_qc/generation/rec_occur_qc_DwCMetadata_kurator_ffdq.config -in ../rec_occur_qc/generation/metadata_tests.csv -out ../rec_occur_qc/generation/metadata_tests.ttl -ieGuidFile ../bdq/tg2/core/information_element_guids.csv  -guidFile ../rec_occur_qc/generation/additional_guids.csv

Test method stubs can be generated using kurator-ffdq using (from a kurator-ffdq directory in the same parent directory as rec_occur_qc) with:

    ./test-util.sh -config ../rec_occur_qc/generation/rec_occur_qc_DwCMetadata_stubs_kurator_ffdq.config -in ../rec_occur_qc/generation/metadata_tests.csv -out ../rec_occur_qc/generation/metadata_tests.ttl -generateClass -srcDir ../rec_occur_qc/src/main/java -ieGuidFile ../bdq/tg2/core/information_element_guids.csv  -guidFile ../rec_occur_qc/generation/additional_guids.csv

Add comments to the end of java class noting out of date implementations using kurator-ffdq (from a kurator-ffdq directory in the same parent directory as rec_occur_qc) with: 

    ./test-util.sh -config ../rec_occur_qc/generation/rec_occur_qc_DwCMetadata_kurator_ffdq.config -in ../rec_occur_qc/generation/metadata_tests.csv -out ../rec_occur_qc/generation/metadata_tests.ttl -appendClass -srcDir ../rec_occur_qc/src/main/java -ieGuidFile ../bdq/tg2/core/information_element_guids.csv  -guidFile ../rec_occur_qc/generation/additional_guids.csv
