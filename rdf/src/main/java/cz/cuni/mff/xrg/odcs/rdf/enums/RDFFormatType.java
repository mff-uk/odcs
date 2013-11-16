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

	private static Map<String, RDFFormatType> stringMap = new HashMap<>();

	private static void inicializeMap() {

		map.put(RDFFormat.RDFXML, RDFXML);
		map.put(RDFFormat.N3, N3);
		map.put(RDFFormat.TRIG, TRIG);
		map.put(RDFFormat.TURTLE, TTL);

		map.put(RDFFormat.TRIX, TRIX);
		map.put(RDFFormat.NTRIPLES, NT);
	}

	private static void inicializeStringMap() {
		for (RDFFormatType next : getListOfRDFType()) {
			stringMap.put(getStringValue(next), next);
		}
	}

	static {
		inicializeMap();
		inicializeStringMap();
	}

	public static RDFFormatType getTypeByRDFFormat(RDFFormat format) {

		if (map.containsKey(format)) {
			return map.get(format);
		} else {
			return AUTO;
		}

	}

	public static RDFFormatType getTypeByString(String value) {
		if (stringMap.containsKey(value)) {
			return stringMap.get(value);
		} else {
			return AUTO;
		}
	}

	public static RDFFormat getRDFFormatByType(RDFFormatType type) {
		RDFFormat result;

		switch (type) {
			case AUTO:
				result = null;
				break;
			case N3:
				result = RDFFormat.N3;
				break;
			case NT:
				result = RDFFormat.NTRIPLES;
				break;
			case RDFXML:
				result = RDFFormat.RDFXML;
				break;
			case TRIG:
				result = RDFFormat.TRIG;
				break;
			case TRIX:
				result = RDFFormat.TRIX;
				break;
			case TTL:
				result = RDFFormat.TURTLE;
				break;
			default:
				result = null;
		}

		return result;
	}

	public static List<RDFFormatType> getListOfRDFType() {
		List<RDFFormatType> list = new ArrayList<>();
		list.add(AUTO);
		list.addAll(map.values());

		return list;
	}

	public static String getStringValue(RDFFormatType type) {

		String result;

		switch (type) {
			case AUTO:
			case N3:
			case TRIX:
			case TRIG:
			case TTL:
				result = type.toString();
				break;
			case RDFXML:
				result = "RDF/XML";
				break;
			case NT:
				result = "N-TRIPLES";
				break;
			default:
				result = "";
		}

		return result;

	}
}
