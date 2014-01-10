package cz.cuni.mff.xrg.odcs.politicalDonationExtractor.data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: janci Date: 4.12.2013 Time: 13:35 To change this template use File | Settings | File Templates.
 */
public class PoliticalPartyDonationRecord extends AbstractRecord {
    private String datanestId;
    private String donorName;
    private String donorSurname;
    private String donorTitle;
    private String name; // donor company
    private String ico; // donor ICO
    private float donationValue;
    private Currency currency;
    private String donorAddress;
    private String donorPsc;
    private String donorCity;
    private String recipientParty;
    private String year;
    private Date acceptDate;
    private String note;

    // TODO: rest of the items: datum prijatia, ... which was not
    // deemed useful now BUT might become handy later on (like in
    // crowdsourcing, having multiple items will help determine and correct
    // mistakes etc.)

    // TODO: for the purposes of "common use case" try adding the value of
    // 'pricateInEur' calculated during harvesting from 'price' and
    // 'currency' so as to avoid having to complicate the
    // search queries with stuff like
    // 'if EUR then price > 10; if SKK then price > 300'

    public String getDatanestId() {
        return datanestId;
    }

    public void setDatanestId(String datanestId) {
        this.datanestId = datanestId;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorSurname() {
        return donorSurname;
    }

    public void setDonorSurname(String donorSurname) {
        this.donorSurname = donorSurname;
    }

    public String getDonorTitle() {
        return donorTitle;
    }

    public void setDonorTitle(String donorTitle) {
        this.donorTitle = donorTitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public float getDonationValue() {
        return donationValue;
    }

    public void setDonationValue(float donationValue) {
        this.donationValue = donationValue;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getDonorAddress() {
        return donorAddress;
    }

    public void setDonorAddress(String donorAddress) {
        this.donorAddress = donorAddress;
    }

    public String getDonorPsc() {
        return donorPsc;
    }

    public void setDonorPsc(String donorPsc) {
        this.donorPsc = donorPsc;
    }

    public String getDonorCity() {
        return donorCity;
    }

    public void setDonorCity(String donorCity) {
        this.donorCity = donorCity;
    }

    public String getRecipientParty() {
        return recipientParty;
    }

    public void setRecipientParty(String recipientParty) {
        this.recipientParty = recipientParty;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Date getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(Date acceptDate) {
        this.acceptDate = acceptDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
