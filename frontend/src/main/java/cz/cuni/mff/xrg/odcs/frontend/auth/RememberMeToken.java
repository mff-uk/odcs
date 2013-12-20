package cz.cuni.mff.xrg.odcs.frontend.auth;

import java.io.Serializable;
import java.util.Date;


/**
 * Token with a signed information about the user who authenticates with this
 * token.
 * 
 * TODO override writeObject and readObject to serialize effectively
 *
 * @author Jan Vojt
 */
public class RememberMeToken implements Serializable {
	
	/**
	 * Remember-me cookie separator.
	 */
	public static final Character SEP = '#';
	
	private final String username;
	
	private final Date created;
	
	private final String hash;

	public RememberMeToken(String username, Date created, String hash) {
		this.username = username;
		this.created = created;
		this.hash = hash;
	}
	
	public String getUsername() {
		return username;
	}

	public Date getCreated() {
		return created;
	}

	public String getHash() {
		return hash;
	}

}
