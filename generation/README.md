metadata_tests.csv are the subset of TDWG BDQ TG2 tests related to OTHER (Record-level, Occurrence) terms, excluding the broad measure tests.

Turtle RDF generated using kurator-ffdq using (from a kurator-ffdq directory in the same parent directory as rec_occur_qc) with:

   ./test-util.sh -config ../rec_occur_qc/generation/rec_occur_qc_DwCMetadata_kurator_ffdq.config -in ../rec_occur_qc/generation/metadata_tests.csv -out ../rec_occur_qc/generation/metadata_tests.ttl

Test method stubs can be generated using kurator-ffdq using (from a kurator-ffdq directory in the same parent directory as rec_occur_qc) with:

    ./test-util.sh -config ../rec_occur_qc/generation/rec_occur_qc_DwCMetadata_kurator_ffdq.config -in ../rec_occur_qc/generation/metadata_tests.csv -out ../rec_occur_qc/generation/metadata_tests.ttl -generateClass -srcDir ../rec_occur_qc/src/main/java

