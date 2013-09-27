package cz.cuni.xrg.intlib.commons.app.scheduling;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.persistence.*;

/**
 * An abstract representation of an email address.
 *
 * @author Jan Vojt
 */
@Entity
@Table(name = "sch_email")
public class EmailAddress implements Serializable, Comparable {
	
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sch_email")
	@SequenceGenerator(name = "seq_sch_email", allocationSize = 1)
	private Long id;

	/**
	 * Username part of emaill address (everything before @).
	 */
	@Column(name = "e_user")
	private String name;
	
	/**
	 * Domain name part of the email address (everything after @).
	 */
	@Column(name = "e_domain")
	private String domain;

	/**
	 * Default constructor for JPA.
	 */
	public EmailAddress() {
	}
	
	/**
	 * Create an <code>EmailAddress</code>.
	 * 
	 * @param addressAsText a full email address
	 * @throws MalformedEmailAddressException if the parameter is not a full
	 * email address.
	 */
	public EmailAddress(String addressAsText) throws MalformedEmailAddressException {
		
		StringTokenizer st = new StringTokenizer(addressAsText, "@");

		try {
			name = st.nextToken();
			domain = st.nextToken();
		} catch (NoSuchElementException e) {
			throw new MalformedEmailAddressException(addressAsText);
		}

		if (st.hasMoreTokens()) {
			throw new MalformedEmailAddressException(addressAsText);
		}
	}

	/**
	 * Create an <code>EmailAddress</code>.
	 * 
	 * @param name the name in the address
	 * @param domain the domain part of the address, passed as an <code>String</code>
	 * @throws MalformedEmailAddressException if the parameter is not a full
	 * email address.
	 */
	public EmailAddress(String name, String domain) throws MalformedEmailAddressException {
		this.name = name;
		this.domain = domain;
	}

	/**
	 * @return the name in the <code>EmailAddress</code>
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the domain in the <code>EmailAddress</code>
	 */
	public String getDomain() {
		return domain;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + this.name.hashCode();
		hash = 97 * hash + this.domain.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EmailAddress other = (EmailAddress) obj;
		return (this.name.equals(other.getName())
				&& this.domain.equals(other.getDomain()));
	}

	/**
	 * <b>Part of
	 * <code>Comparable</code> interface. Sorts alphabetically.
	 */
	@Override
	public int compareTo(Object obj) {
		StringBuilder sb1 = new StringBuilder(name);
		sb1.append(domain);
		String s1 = sb1.toString();

		EmailAddress ea = (EmailAddress) obj;
		StringBuilder sb2 = new StringBuilder(ea.getName());
		sb2.append(ea.getDomain());
		String s2 = sb2.toString();

		return s1.compareTo(s2);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("@");
		sb.append(domain);
		return sb.toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}