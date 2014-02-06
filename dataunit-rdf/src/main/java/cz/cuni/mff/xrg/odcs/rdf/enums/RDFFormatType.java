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

	/**
	 * RDF type is set automatically by suffix. If no suffix, RDFXML type is
	 * set. *
	 */
	AUTO,
	/**
	 * RDF type using for storing data XML syntax*
	 */
	RDFXML,
	/**
	 * RDF type using triples - subject, predicate, object. *
	 */
	N3,
	/**
	 * RDF type using extended turle format (TTL).
	 */
	TRIG,
	/**
	 * RDF type using turle format - extension of N3 type. *
	 */
	TTL,
	/**
	 * TRIX RDF format.
	 */
	TRIX,
	/**
	 * N_TRIPLES RDF format.
	 */
	NT;

	private static Map<RDFFormat, RDFFormatType> map = new HashMap<>();

	private static Map<String, RDFFormatType> stringMap = new HashMap<>();

	private static void initializeMap() {

		map.put(RDFFormat.RDFXML, RDFXML);
		map.put(RDFFormat.N3, N3);
		map.put(RDFFormat.TRIG, TRIG);
		map.put(RDFFormat.TURTLE, TTL);

		map.put(RDFFormat.TRIX, TRIX);
		map.put(RDFFormat.NTRIPLES, NT);
	}

	private static void initializeStringMap() {
		for (RDFFormatType next : getListOfRDFType()) {
			stringMap.put(getStringValue(next), next);
		}
	}

	static {
		initializeMap();
		initializeStringMap();
	}

	/**
	 * For given {@link RDFFormat} return equivalent in {@link RDFFormatType}.
	 * If {@link RDFFormat} is not supported in application returns
	 * {@link #AUTO} type.
	 *
	 * @param format Original RDF format need for finding equivalent.
	 * @return equivalent for given {@link RDFFormat} as {@link RDFFormatType}.
	 */
	public static RDFFormatType getTypeByRDFFormat(RDFFormat format) {

		if (map.containsKey(format)) {
			return map.get(format);
		} else {
			return AUTO;
		}

	}

	/**
	 * For given desription of one of {@link RDFFormatType} return one of
	 * concrete {@link RDFFormatType}.
	 *
	 * @param value String description of {@link RDFFormatType}.
	 * @return {@link RDFFormatType} for given description.
	 */
	public static RDFFormatType getTypeByString(String value) {
		if (stringMap.containsKey(value)) {
			return stringMap.get(value);
		} else {
			return AUTO;
		}
	}

	/**
	 * For given {@link RDFFormatType} return it´s eqvivalent as
	 * {@link RDFFormat}.
	 *
	 * @param type {@link RDFFormatType} for what you can get equivalent
	 *             {@link RDFFormat}.
	 * @return concrete {@link RDFFormat} as equivalent for given
	 *         {@link RDFFormatType}.
	 */
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

	/**
	 * Return list of all {@link RDFFormatType} supported by this application.
	 *
	 * @return List of all {@link RDFFormatType} supported by this application.
	 */
	public static List<RDFFormatType> getListOfRDFType() {
		List<RDFFormatType> list = new ArrayList<>();
		list.add(AUTO);
		list.addAll(map.values());

		return list;
	}

	/**
	 * For given {@link RDFFormatType} return it´s string description.
	 *
	 * @param type {@link RDFFormatType} for you can get string description.
	 * @return string description of given {@link RDFFormatType} as method
	 *         param.
	 */
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
