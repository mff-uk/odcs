package cz.cuni.mff.xrg.odcs.commons.httpconnection.utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Class responsible for autentification to access to SPARQL endpoint.
 *
 * @author Jiri Tomes
 */
public class Authentificator {

	public static void authenticate(String hostName, String password) {

		boolean usePassword = !(hostName.isEmpty() && password.isEmpty());

		if (usePassword) {

			final String myName = hostName;
			final String myPassword = password;

			Authenticator autentisator = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(myName, myPassword
							.toCharArray());
				}
			};

			Authenticator.setDefault(autentisator);

		}
	}
}
