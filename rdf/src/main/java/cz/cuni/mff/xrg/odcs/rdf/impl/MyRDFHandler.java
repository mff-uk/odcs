package cz.cuni.mff.xrg.odcs.rdf.impl;

import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;

import java.io.OutputStream;
import java.io.Writer;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
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
public class MyRDFHandler implements RDFHandler {

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
			default:
				handler = new RDFXMLWriter(os);
				break;
		}

	}

	private void setHandler(Writer writer) {

		switch (formatType) {

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
			default:
				handler = new RDFXMLWriter(writer);
				break;


		}

	}

	@Override
	public void startRDF() throws RDFHandlerException {
		handler.startRDF();
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		handler.endRDF();
	}

	@Override
	public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
		handler.handleNamespace(prefix, uri);
	}

	@Override
	public void handleStatement(Statement st) throws RDFHandlerException {
		handler.handleStatement(st);
	}

	@Override
	public void handleComment(String comment) throws RDFHandlerException {
		handler.handleComment(comment);
	}
}
