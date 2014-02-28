package cz.cuni.mff.xrg.odcs.commons.module.utils;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * Workaround to add triples with triple quoted literals.
 *
 * @author tomasknap
 */
public class AddTripleWorkaround {

	/**
	 * Prepares triple to be inserted to a file from which it is loaded to the
	 * data unit (workarount - triples inserted directly via Resource subj, URI
	 * pred, Value obj, cannot define whether literal should be single/triple
	 * quoted.
	 *
	 * @param subj Subject of the triple 
	 * @param pred Predicate of the triple  
	 * @param obj Object of the triple
	 * @return TTL serialization if the triple
	 */
	public static String prepareTriple(Resource subj, URI pred, Value obj) {

//        
//        Resource subj = rdfOutput.createURI(subject);
//        URI pred = rdfOutput.createURI(outputPredicate);
//        Value obj = rdfOutput.createLiteral(outputString); 
//         
		String triple = getSubjectInsertText(subj) + " "
				+ getPredicateInsertText(pred) + " "
				+ getObjectInsertText(obj) + " .";

//        String escapedrdfa = rdfa.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quote;").replaceAll("\\*", "&#42;").replaceAll("\\\\", "&#92;");
//	writer.println("<" + decisionURI + "> sdo:hasHTMLContent \"\"\"" + escapedrdfa + "\"\"\"@cs .");
		return triple;
	}

	private static String getSubjectInsertText(Resource subject) throws IllegalArgumentException {

		if (subject instanceof URI) {
			return prepareURIresource((URI) subject);
		}

		if (subject instanceof BNode) {
			return prepareBlankNodeResource((BNode) subject);
		}
		throw new IllegalArgumentException("Subject must be URI or blank node");
	}

	private static String getPredicateInsertText(URI predicate) {
		if (predicate instanceof URI) {
			return prepareURIresource((URI) predicate);
		}
		throw new IllegalArgumentException("Predicatemust be URI");

	}

	private static String getObjectInsertText(Value object) throws IllegalArgumentException {

		if (object instanceof URI) {
			return prepareURIresource((URI) object);
		}

		if (object instanceof BNode) {
			return prepareBlankNodeResource((BNode) object);
		}

		if (object instanceof Literal) {
			return prepareLiteral((Literal) object);
		}

		throw new IllegalArgumentException(
				"Object must be URI, blank node or literal");
	}

	private static String prepareURIresource(URI uri) {
		return "<" + uri.stringValue() + ">";
	}

	private static String prepareBlankNodeResource(BNode bnode) {
		return "_:" + bnode.getID();
	}

	private static String prepareLiteral(Literal literal) {
		String label = "\"\"\"" + literal.getLabel() + "\"\"\"";
		if (literal.getLanguage() != null) {
			//there is language tag
			return label + "@" + literal.getLanguage();
		} else if (literal.getDatatype() != null) {
			return label + "^^" + prepareURIresource(literal.getDatatype());
		}
		//plain literal (return in """)
		return label;
	}

}
