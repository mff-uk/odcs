package cz.cuni.mff.xrg.odcs.procurementExtractor.serialization;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cz.cuni.mff.xrg.odcs.procurementExtractor.data.ProcurementRecord;
import cz.cuni.mff.xrg.odcs.procurementExtractor.data.RdfData;
import cz.cuni.mff.xrg.odcs.procurementExtractor.repository.OdnRepositoryStoreInterface;

public class ProcurementRdfSerializer extends AbstractRdfSerializer<ProcurementRecord> {

    public final static String NS_PROCUREMENT = "http://opendata.cz/vocabulary/procurement.rdf#";
    // TODO: do we need that configurable? if we want the that RDF data
    // accessible over the net via that URL/URI (which is encouraged) it would
    // be either nice to "guess" it correctly from some other configuration or
    // have it in some per-ODN repository configuration
    public final static String OPENDATA_PROCUREMENTS_BASE_URI = "http://opendata.sk/dataset/procurements/";
    public final static String OPENDATA_PROCUREMENTS_CONTEXTS_KEY = "procurements";

    private final static DecimalFormat priceFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

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
    public ProcurementRdfSerializer(OdnRepositoryStoreInterface<RdfData> repository) throws IllegalArgumentException, ParserConfigurationException,
            TransformerConfigurationException {

        super(repository);
    }

    @Override
    public void addCustomRdfNsElements(Element rdfElement) {
        rdfElement.setAttribute("xmlns:pc", NS_PROCUREMENT);
    }

    @Override
    public void serializeRecord(Document doc, Element rdfElement, ProcurementRecord record) {
        Element concept = doc.createElement(TAG_NAME_SKOS_CONCEPT);
        concept.setAttribute("rdf:about", OPENDATA_PROCUREMENTS_BASE_URI + record.getId());

        // TODO: verify, that it is indeed a form of unique name identifying
        // single procurement
        concept.appendChild(appendTextNode(doc, "skos:prefLabel", record.getProcurementId()));
        // TODO: hardcoded strings are not nice ... meaning the URL mainly but ...
        concept.appendChild(appendResourceNode(doc, "dc:source", "rdf:resource", "http://datanest.fair-play.sk/datasets/2/records/" + record.getDatanestId()));
        concept.appendChild(appendTextNode(doc, "opendata:procurementSubject", record.getProcurementSubject()));
        concept.appendChild(appendTextNode(doc, "pc:price", priceFormat.format(record.getPrice())));
        // sometimes the currency is not filled in the source (so far only for
        // cases where the price was 0)
        if (record.getCurrency() != null)
            concept.appendChild(appendTextNode(doc, "opendata:currency", record.getCurrency().getCurrencyCode()));
        concept.appendChild(appendTextNode(doc, "opendata:xIsVatIncluded", Boolean.toString(record.isVatIncluded())));
        // TODO: use 'opendata:customer' child inside 'pc:buyerProfile' instead
        concept.appendChild(appendResourceNode(doc, "opendata:customer", "rdf:resource",
        // TODO: nie je toto bug? nema to byt ProcurementRdfSerializer?
                OrganizationRdfSerializer.ORGANIZATIONS_BASE_URI + record.getCustomerIco()));
        // TODO: use 'opendata:customer' child inside 'pc:Supplier' instead
        concept.appendChild(appendResourceNode(doc, "opendata:supplier", "rdf:resource",
        // TODO: nie je toto bug? nema to byt ProcurementRdfSerializer?
                OrganizationRdfSerializer.ORGANIZATIONS_BASE_URI + record.getSupplierIco()));

        for (String scrapNote : record.getScrapNotes())
            concept.appendChild(appendTextNode(doc, "opendata:xScrapNote", scrapNote));

        rdfElement.appendChild(concept);
    }

    @Override
    public void store(List<ProcurementRecord> records) throws Exception {

        RdfData rdfData = new RdfData(serialize(records), OPENDATA_PROCUREMENTS_BASE_URI, OPENDATA_PROCUREMENTS_CONTEXTS_KEY);
        getRepository().store(rdfData);
    }

}
