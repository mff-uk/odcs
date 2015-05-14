package cz.cuni.mff.xrg.odcs.frontend.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;

public class UVWebAuthenticationDetailsSource implements
        AuthenticationDetailsSource<HttpServletRequest, UVAuthenticationDetails> {

    /**
     * @param context
     *            the {@code HttpServletRequest} object.
     * @return the {@code WebAuthenticationDetails} containing information about the
     *         current request
     */
    public UVAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new UVAuthenticationDetails(context);
    }
}