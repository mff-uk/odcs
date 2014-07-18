package cz.cuni.mff.xrg.odcs.dpu.triplegeneratortordfextractor;

public class TripleGeneratorToRDFExtractorConfig {

    private int tripleCount = 1000000;

    private int commitSize = 50000;

    public int getTripleCount() {
        return tripleCount;
    }

    public void setTripleCount(int tripletCount) {
        this.tripleCount = tripletCount;
    }

    public int getCommitSize() {
        return commitSize;
    }

    public void setCommitSize(int commitSize) {
        this.commitSize = commitSize;
    }

}
