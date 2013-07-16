package cz.cuni.xrg.intlib.rdf.impl;

import static cz.cuni.xrg.intlib.rdf.enums.RDFFormatType.*;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;
import java.io.OutputStream;
import java.io.Writer;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.n3.N3Writer;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.openrdf.rio.trig.TriGWriter;
import org.openrdf.rio.trix.TriXWriter;
import org.openrdf.rio.turtle.TurtleWriter;

/**
 * Class for better writing RDF data to file using RDFHandler.
 *
 * @author Jiri Tomes
 */
public class MyRDFHandler {

	private RDFHandler handler;

	private RDFFormatType formatType;

	public MyRDFHandler(Writer writer, RDFFormatType formatType) {
		this.formatType = formatType;
		setHandler(writer);
	}

	public MyRDFHandler(OutputStream os, RDFFormatType formatType) {
		this.formatType = formatType;
		setHandler(os);
	}

	public RDFHandler getRDFHandler() {
		return handler;
	}

	private void setHandler(OutputStream os) {

		switch (formatType) {
			case AUTO:
				handler = new RDFXMLWriter(os);
				break;
			case N3:
				handler = new N3Writer(os);
				break;
			case RDFXML:
				handler = new RDFXMLPrettyWriter(os);
				break;
			case TRIG:
				handler = new TriGWriter(os);
				break;
			case TTL:
				handler = new TurtleWriter(os);
				break;
			case NT:
				handler = new NTriplesWriter(os);
				break;
			case TRIX:
				handler = new TriXWriter(os);
				break;
		}

	}

	private void setHandler(Writer writer) {

		switch (formatType) {
			case AUTO:
				handler = new RDFXMLWriter(writer);
				break;
			case N3:
				handler = new N3Writer(writer);
				break;
			case RDFXML:
				handler = new RDFXMLPrettyWriter(writer);
				break;
			case TRIG:
				handler = new TriGWriter(writer);
				break;
			case TTL:
				handler = new TurtleWriter(writer);
				break;
			case NT:
				handler = new NTriplesWriter(writer);
				break;
			case TRIX:
				handler = new TriXWriter(writer);
				break;


		}

	}
}
