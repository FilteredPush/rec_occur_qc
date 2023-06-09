metadata_tests.csv are the subset of TDWG BDQ TG2 tests related to OTHER (Record-level, Occurrence) terms, excluding the broad measure tests.

To obtain this subset of tests from the list of core tests, assuming a checkout of tdwg/bdq in bdq in the parent directory of rec_occur_qc: 

    grep "IE Category" ../../bdq/tg2/core/TG2_tests.csv > metadata_tests.csv
    grep OTHER ../../bdq/tg2/core/TG2_tests.csv  | grep -v AllDarwinCoreTerms  >> metadata_tests.csv
    grep DATAGENERALIZATIONS ../../bdq/tg2/core/TG2_tests.csv | grep -v AllDarwinCoreTerms  >> metadata_tests.csv

Turtle RDF generated using kurator-ffdq using (from a kurator-ffdq directory in the same parent directory as rec_occur_qc) with:

   ./test-util.sh -config ../rec_occur_qc/generation/rec_occur_qc_DwCMetadata_kurator_ffdq.config -in ../rec_occur_qc/generation/metadata_tests.csv -out ../rec_occur_qc/generation/metadata_tests.ttl

Test method stubs can be generated using kurator-ffdq using (from a kurator-ffdq directory in the same parent directory as rec_occur_qc) with:

    ./test-util.sh -config ../rec_occur_qc/generation/rec_occur_qc_DwCMetadata_kurator_ffdq.config -in ../rec_occur_qc/generation/metadata_tests.csv -out ../rec_occur_qc/generation/metadata_tests.ttl -generateClass -srcDir ../rec_occur_qc/src/main/java

