package cz.cuni.mff.xrg.odcs.rdf.enums;

import java.util.*;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Jiri Tomes
 *
 * Set of system supported RDF data types. It´s used priority for easy setting
 * one of possible RDF types choosed by user in DPU details dialog.
 */
public enum RDFFormatType {

	AUTO/**
	 * RDF type is set automatically by suffix. If no suffix, RDFXML type is
	 * set. *
	 */
	,
	RDFXML /**
	 * RDF type using for storing data XML syntax*
	 */
	,
	N3 /**
	 * RDF type using triples - subject, predicate, object. *
	 */
	,
	TRIG /**
	 * RDF type using extended turle format (TTL).
	 */
	,
	TTL /**
	 * RDF type using turle format - extension of N3 type. *
	 */
	, TRIX /**
	 * TRIX RDF format.
	 */
	, NT /**
	 * N_TRIPLES RDF format.
	 */
	;

	private static Map<RDFFormat, RDFFormatType> map = new HashMap<>();

	private static void inicializeMap() {

		map.put(RDFFormat.RDFXML, RDFXML);
		map.put(RDFFormat.N3, N3);
		map.put(RDFFormat.TRIG, TRIG);
		map.put(RDFFormat.TURTLE, TTL);

		map.put(RDFFormat.TRIX, TRIX);
		map.put(RDFFormat.NTRIPLES, NT);
	}

	static {
		inicializeMap();
	}

	public static RDFFormatType getTypeByRDFFormat(RDFFormat format) {

		if (map.containsKey(format)) {
			return map.get(format);
		} else {
			return AUTO;
		}

	}

	public static List<RDFFormatType> getListOfRDFType() {
		List<RDFFormatType> list = new ArrayList<>();
		list.addAll(map.values());
		list.add(AUTO);

		return list;
	}
}
