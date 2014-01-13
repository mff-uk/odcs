package cz.cuni.mff.xrg.odcs.procurementExtractor.data;

public enum DatanestType {

    ORGANIZATION("org"), POLITICAL("pol"), PROCUREMENT("pro");
    private String datanestCode;

    private DatanestType(String datanestCode) {
        this.datanestCode = datanestCode;
    }

    public String getDatanestCode() {
        return datanestCode;
    }

    public void setDatanestCode(String datanestCode) {
        this.datanestCode = datanestCode;
    }

    @Override
    public String toString() {
        return "DatanestType{" + "datanestCode='" + datanestCode + '\'' + '}';
    }
}
