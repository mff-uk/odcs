package cz.cuni.mff.xrg.odcs.procurementExtractor.serialization;

import java.io.File;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cz.cuni.mff.xrg.odcs.procurementExtractor.data.OrganizationRecord;
import cz.cuni.mff.xrg.odcs.procurementExtractor.data.RdfData;
import cz.cuni.mff.xrg.odcs.procurementExtractor.repository.OdnRepositoryStoreInterface;

public class OrganizationRdfSerializer extends AbstractRdfSerializer<OrganizationRecord> {

    public final static String NS_ADMS = "http://www.w3.org/ns/adms#";
    public final static String NS_DCTERMS = "http://purl.org/dc/terms/";
    // One thing should have one URI. For Slovak companies it maybe will be this one even officially.
    // URI might be understood this way:
    // a) Slovak government has a dataset (http://data.gov.sk/...)
    // b) where we're distinguishing real things by ID (.../id/...)
    // c) which (the dataset) is being curated by Ministry of Interior (../interior/...)
    // d) and contains information about organizations (.../organization/...)
    // e) distinguished by ICO (the last thing appended to base URI: .../<ico>)
    public final static String ORGANIZATIONS_BASE_URI = "http://data.gov.sk/id/interior/organization/";
    public final static String IDENTIFIERS_BASE_URI = "http://data.gov.sk/id/interior/identifier/";
    public final static String OPENDATA_ORGANIZATIONS_CONTEXTS_KEY = "organizations";
    public final static String TAG_NAME_ADMS_IDENTIFIER = "adms:Identifier";
    public final static String TAG_NAME_ORG_REGORG = "rov:RegisteredOrganization";
    public final static String ORG_SCHEMA_AGENCY = "Ministry of Interior, Slovak Republic";
    public final static String IDENTIFIERS_TYPE_URI = "http://data.gov.sk/def/interior/identifier/ico";

    /**
     * Initialize serializer to use given repository.
     * 
     * @throws IllegalArgumentException
     *             if repository is {@code null}
     * @throws javax.xml.parsers.ParserConfigurationException
     *             when XML document builder fails to initialize
     * @throws javax.xml.transform.TransformerConfigurationException
     *             when XML document transformer fails to initialize
     */
    public OrganizationRdfSerializer(OdnRepositoryStoreInterface<RdfData> repository) throws IllegalArgumentException, ParserConfigurationException,
            TransformerConfigurationException {

        super(repository);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformer = transformerFactory.newTransformer();
        // TODO: nice for debugging, but might hurt performance in production
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }

    @Override
    public void addCustomRdfNsElements(Element rdfElement) {
        rdfElement.setAttribute("xmlns:adms", NS_ADMS);
        rdfElement.setAttribute("xmlns:dcterms", NS_DCTERMS);
    }

    @Override
    public void serializeRecord(Document doc, Element rdfElement, OrganizationRecord record) {
        // *** organization ***
        Element concept1 = doc.createElement(TAG_NAME_ORG_REGORG);
        concept1.setAttribute("rdf:about", ORGANIZATIONS_BASE_URI + record.getIco());

        concept1.appendChild(appendTextNode(doc, "rov:legalName", record.getName()));
        concept1.appendChild(appendResourceNode(doc, "dc:source", "rdf:resource", record.getSource()));
        concept1.appendChild(appendTextNode(doc, "dc:type", record.getLegalForm()));
        if (record.getDateFrom() != null) {
            String dateFrom = sdf.format(record.getDateFrom());
            concept1.appendChild(appendTextNode(doc, "opendata:dateFrom", dateFrom));
        }
        if (record.getDateTo() != null) {
            String dateTo = sdf.format(record.getDateTo());
            concept1.appendChild(appendTextNode(doc, "opendata:dateTo", dateTo));
        }
        // concept.appendChild(appendTextNode(doc, "opendata:seat", record.getSeat()));
        Element fullAddress = appendTextNode(doc, "locn:fullAddress", record.getSeat());
        fullAddress.setAttribute("rdf:datatype", "xsd:string");
        Element address = doc.createElement("locn:address");
        // TODO: does the fullAddress have to contain also the organization name
        // (i.e. is it as written on envelope)?public final static String
        address.appendChild(fullAddress);
        // TODO: parse out PSC from full address
        // address.appendChild(appendTextNode(doc, "locn:postCode", record.getSeat()));
        Element primarySite = doc.createElement("org:registeredSite");
        primarySite.appendChild(address);
        concept1.appendChild(primarySite);
        concept1.appendChild(appendResourceNode(doc, "opendata:ico", "rdf:resource", IDENTIFIERS_BASE_URI + record.getIco()));

        rdfElement.appendChild(concept1);

        // *** organization's ICO ***
        Element concept2 = doc.createElement(TAG_NAME_ADMS_IDENTIFIER);
        concept2.setAttribute("rdf:about", IDENTIFIERS_BASE_URI + record.getIco());

        Element notation = appendTextNode(doc, "skos:notation", record.getIco());
        notation.setAttribute("rdf:datatype", "xsd:string");
        concept2.appendChild(notation);

        Element schemaAgency = appendTextNode(doc, "adms:schemaAgency", ORG_SCHEMA_AGENCY);
        schemaAgency.setAttribute("rdf:datatype", "xsd:string");
        concept2.appendChild(schemaAgency);

        concept2.appendChild(appendResourceNode(doc, "dcterms:type", "rdf:resource", IDENTIFIERS_TYPE_URI));

        rdfElement.appendChild(concept2);
    }

    @Override
    public void store(List<OrganizationRecord> records) throws IllegalArgumentException {

        RdfData rdfData = null;
        File fileRdf = null;

        try {
            rdfData = new RdfData(serialize(records), ORGANIZATIONS_BASE_URI, OPENDATA_ORGANIZATIONS_CONTEXTS_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getRepository().store(rdfData);
    }
}
