package cz.cuni.mff.xrg.odcs.procurementExtractor.serialization;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import cz.cuni.mff.xrg.odcs.procurementExtractor.data.AbstractRecord;
import cz.cuni.mff.xrg.odcs.procurementExtractor.data.RdfData;
import cz.cuni.mff.xrg.odcs.procurementExtractor.repository.OdnRepositoryStoreInterface;

public abstract class AbstractRdfSerializer<RecordType extends AbstractRecord> extends AbstractSerializer<RecordType, String, RdfData> {

    public final static String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public final static String NS_DC = "http://purl.org/dc/elements/1.1/";
    public final static String NS_LOCN = "http://www.w3.org/ns/locn#";
    public final static String NS_ORG = "http://www.w3.org/ns/org#";
    public final static String NS_ROV = "http://www.w3.org/TR/vocab-regorg/";
    public final static String NS_SKOS = "http://www.w3.org/2004/02/skos/core#";
    public final static String NS_OPENDATA = "http://sk.eea.opendata/2011/02/opendicts#";

    public final static String TAG_NAME_SKOS_CONCEPT = "skos:Concept";

    public final static String OPENDATA_DATE_FORMAT = "dd.MM.yyyy";

    public final static String OPENDATA_COMBINED_REPO_NAME = "all";
    public final static String OPENDATA_COMBINED_BASE_URI = "http://opendata.sk/dataset/all/";

    public final static String ERR_CONVERSION = "unable to convert the data into RDF";

    protected final static SimpleDateFormat sdf = new SimpleDateFormat(OPENDATA_DATE_FORMAT);

    protected DocumentBuilder docBuilder;
    protected Transformer transformer;

    /**
     * Initialize serializer to use given repository.
     * 
     * 
     * @throws IllegalArgumentException
     *             if repository is {@code null}
     * @throws javax.xml.parsers.ParserConfigurationException
     *             when XML document builder fails to initialize
     * @throws javax.xml.transform.TransformerConfigurationException
     *             when XML document transformer fails to initialize
     */
    public AbstractRdfSerializer(OdnRepositoryStoreInterface<RdfData> repository) throws IllegalArgumentException, ParserConfigurationException,
            TransformerConfigurationException {

        super(repository);

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docBuilderFactory.newDocumentBuilder();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformer = transformerFactory.newTransformer();
        // TODO: nice for debugging, but might hurt performance in production
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }

    protected Element appendTextNode(Document doc, String name, String value) {
        Element element = doc.createElement(name);
        Text textNode = doc.createTextNode(value);
        element.appendChild(textNode);
        return element;
    }

    protected Element appendResourceNode(Document doc, String name, String attr, String value) {

        Element element = doc.createElement(name);
        element.setAttribute(attr, value);
        return element;
    }

    /**
     * Serialize one given record into RDF and store the result in given 'concept' (which in turn is in given 'doc').
     * 
     * @param doc
     *            XML document we are serializing into
     * @param rdfElement
     *            XML document element into which to append the serialization of given record
     * @param record
     *            record to serialize into RDF
     */
    public abstract void serializeRecord(Document doc, Element rdfElement, RecordType record);

    /**
     * Override this method if you need to add custom RDF NS elements to the XML document.
     * 
     * @param rdfElement
     *            RDF element of the XML document
     */
    public void addCustomRdfNsElements(Element rdfElement) {
        // nothing to do if there are no custom elements needed
    }

    @Override
    public String serialize(List<RecordType> records) throws Exception {

        Document doc = docBuilder.newDocument();

        Element rdfElement = doc.createElementNS(NS_RDF, "rdf:RDF");
        rdfElement.setAttribute("xmlns:rdf", NS_RDF);
        rdfElement.setAttribute("xmlns:dc", NS_DC);
        rdfElement.setAttribute("xmlns:locn", NS_LOCN);
        rdfElement.setAttribute("xmlns:org", NS_ORG);
        rdfElement.setAttribute("xmlns:rov", NS_ROV);
        rdfElement.setAttribute("xmlns:skos", NS_SKOS);
        rdfElement.setAttribute("xmlns:opendata", NS_OPENDATA);
        addCustomRdfNsElements(rdfElement);
        doc.appendChild(rdfElement);

        for (RecordType record : records)
            serializeRecord(doc, rdfElement, record);

        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new Exception(e.getMessage(), e);
        }

        return sw.toString();
    }
}
