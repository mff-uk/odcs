package commons.httpconnection.utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Class responsible for authentification to access to SPARQL endpoint.
 *
 * @author Jiri Tomes
 */
public class Authentificator {

	/**
	 * Make authentification based pair - host name and password.
	 *
	 * @param hostName string value of hostName
	 * @param password string value of password
	 */
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
