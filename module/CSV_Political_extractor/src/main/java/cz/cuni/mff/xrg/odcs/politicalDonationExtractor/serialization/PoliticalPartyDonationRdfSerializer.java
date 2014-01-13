package cz.cuni.mff.xrg.odcs.politicalDonationExtractor.serialization;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import cz.cuni.mff.xrg.odcs.politicalDonationExtractor.repository.OdnRepositoryStoreInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cz.cuni.mff.xrg.odcs.politicalDonationExtractor.data.PoliticalPartyDonationRecord;
import cz.cuni.mff.xrg.odcs.politicalDonationExtractor.data.RdfData;

/**
 * Created with IntelliJ IDEA. User: janci Date: 4.12.2013 Time: 13:36 To change this template use File | Settings | File Templates.
 */
public class PoliticalPartyDonationRdfSerializer extends AbstractRdfSerializer<PoliticalPartyDonationRecord> {

    // TODO: do we need that configurable? if we want the that RDF data
    // accessible over the net via that URL/URI (which is encouraged) it would
    // be either nice to "guess" it correctly from some other configuration or
    // have it in some per-ODN repository configuration
    public final static String OPENDATA_PPD_BASE_URI = "http://opendata.sk/dataset/political_party_donations/";
    public final static String OPENDATA_PPD_CONTEXTS_KEY = "political_party_donations";

    private final static DecimalFormat donationValueFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

    /**
     * Initialize serializer to use given repository.
     * 
     * @param repository
     *            repository to use for storage of record
     * 
     * @throws IllegalArgumentException
     *             if repository is {@code null}
     * @throws javax.xml.parsers.ParserConfigurationException
     *             when XML document builder fails to initialize
     * @throws javax.xml.transform.TransformerConfigurationException
     *             when XML document transformer fails to initialize
     */
    public PoliticalPartyDonationRdfSerializer(OdnRepositoryStoreInterface<RdfData> repository) throws IllegalArgumentException, ParserConfigurationException,
            TransformerConfigurationException {

        super(repository);
    }

    @Override
    public void serializeRecord(Document doc, Element rdfElement, PoliticalPartyDonationRecord record) {
        Element concept = doc.createElement(TAG_NAME_SKOS_CONCEPT);
        concept.setAttribute("rdf:about", OPENDATA_PPD_BASE_URI + record.getId());

        // TODO: this is a) ugly and b) "suspect" (i.e. I feel like it's not
        // entirely "in the spirit" of RDF => re-think, re-research, ...
        StringBuffer label = new StringBuffer();
        if (record.getDonorName() != null)
            label.append(record.getDonorName()).append(" ");
        if (record.getDonorSurname() != null)
            label.append(record.getDonorSurname()).append(" ");
        if (record.getDonorTitle() != null)
            label.append(record.getDonorTitle()).append(" ");
        if (record.getName() != null)
            label.append(record.getName()).append(" ");
        label.append(" - ");
        label.append(donationValueFormat.format(record.getDonationValue()));
        label.append(" - ");
        label.append(record.getRecipientParty());
        concept.appendChild(appendTextNode(doc, "skos:prefLabel", label.toString().trim()));

        concept.appendChild(appendResourceNode(doc, "dc:source", "rdf:resource", "http://datanest.fair-play.sk/datasets/32/records/" + record.getDatanestId()));
        // TODO: use FOAF for people and Good Relations for companies
        // and use only URIs or something ... as a link - we have or will have
        // organizations and people repository so the main point twill be the URI,
        // subsequent data will be useful for clean-up when proper link could not
        // be found automatically
        if (record.getDonorName() != null)
            concept.appendChild(appendTextNode(doc, "opendata:donorName", record.getDonorName()));
        if (record.getDonorSurname() != null)
            concept.appendChild(appendTextNode(doc, "opendata:donorSurname", record.getDonorSurname()));
        if (record.getDonorTitle() != null)
            concept.appendChild(appendTextNode(doc, "opendata:donorTitle", record.getDonorTitle()));
        if (record.getName() != null)
            concept.appendChild(appendTextNode(doc, "opendata:donorCompanyName", record.getName()));
        if (record.getIco() != null) {
            concept.appendChild(appendResourceNode(doc, "opendata:donorCompany", "rdf:resource",
            // TODO: nie je toto bug? nema to byt Political..RdfSerializer?
                    OrganizationRdfSerializer.ORGANIZATIONS_BASE_URI + record.getIco()));

        }
        // TODO: adresa, mesto a PSC darcu

        concept.appendChild(appendTextNode(doc, "opendata:giftValue", donationValueFormat.format(record.getDonationValue())));
        concept.appendChild(appendTextNode(doc, "opendata:giftCurrency", record.getCurrency().getCurrencyCode()));
        concept.appendChild(appendTextNode(doc, "opendata:recipientParty", record.getRecipientParty()));
        if (record.getAcceptDate() != null) {
            String acceptDate = sdf.format(record.getAcceptDate());
            concept.appendChild(appendTextNode(doc, "opendata:acceptDate", acceptDate));
        }
        if (record.getNote() != null)
            concept.appendChild(appendTextNode(doc, "opendata:xNote", record.getNote()));

        rdfElement.appendChild(concept);
    }

    @Override
    public void store(List<PoliticalPartyDonationRecord> records) throws Exception {

        RdfData rdfData = new RdfData(serialize(records), OPENDATA_PPD_BASE_URI, OPENDATA_PPD_CONTEXTS_KEY);
        getRepository().store(rdfData);
    }

}
