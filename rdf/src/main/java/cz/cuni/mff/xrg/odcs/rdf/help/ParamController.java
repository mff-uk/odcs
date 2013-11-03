package cz.cuni.mff.xrg.odcs.rdf.help;

import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import java.net.URL;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Responsible for test/controll parameters needed for method RDF.
 *
 * @author Jiri Tomes
 */
public class ParamController {

	private static Logger logger = Logger.getLogger(ParamController.class);

	public static void testNullParameter(Object param, String message) throws RDFException {
		if (param == null) {
			logger.debug(message);
			throw new RDFException(message);
		}
	}

	public static void testEmptyParameter(Object param, String message) throws RDFException {

		if (param != null) {
			boolean isEmpty = true;

			if (param instanceof String) {
				isEmpty = ((String) param).isEmpty();
			} else if (param instanceof List) {
				isEmpty = ((List) param).isEmpty();
			}

			if (isEmpty) {
				logger.debug(message);
				throw new RDFException(message);
			}
		}

	}

	public static void testPositiveParameter(long param, String message) throws RDFException {
		if (param <= 0) {
			logger.debug(message);
			throw new RDFException(message);
		}
	}

	public static void testEndpointSyntax(URL endpointURL) throws RDFException {

		String message = null;

		if (endpointURL != null) {
			final String endpointName = endpointURL.toString().toLowerCase();

			if (!endpointName.startsWith("http://")) {
				message = "Endpoint url name have to started with prefix \"http://\".";
			} else if (endpointName.contains(" ")) {
				message = "Endpoint url constains white spaces";
			}
			if (message != null) {
				logger.debug(message);
				throw new RDFException(message);
			}
		} else {
			message = "Mandatory URL path is null. SPARQL Endpoint URL must be specified";
			logger.debug(message);
			throw new RDFException(message);


		}
	}
}
