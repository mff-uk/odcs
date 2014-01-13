package cz.cuni.mff.xrg.odcs.organizationExtractor.data;

import java.util.Date;

public class OrganizationRecord extends AbstractRecord {
    private String datanestId; // TODO: consider making it an Integer
    private String source;
    private String name;
    private String legalForm; // TODO: consider making it an enumeration, to easily catch errors and to ease categorization
    private String seat;
    private String ico;
    private Date dateFrom;
    private Date dateTo;

    public String getDatanestId() {
        return datanestId;
    }

    public void setDatanestId(String datanestId) {
        this.datanestId = datanestId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLegalForm() {
        return legalForm;
    }

    public void setLegalForm(String legalForm) {
        this.legalForm = legalForm;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    @Override
    public String toString() {
        return "OrganizationRecord{" + "datanestId='" + datanestId + '\'' + ", source='" + source + '\'' + ", name='" + name + '\'' + ", legalForm='"
                + legalForm + '\'' + ", seat='" + seat + '\'' + ", ico='" + ico + '\'' + ", dateFrom=" + dateFrom + ", dateTo=" + dateTo + '}';
    }
}
