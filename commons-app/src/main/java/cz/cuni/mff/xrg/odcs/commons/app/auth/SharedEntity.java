package cz.cuni.mff.xrg.odcs.commons.app.auth;

/**
 * Entities implementing <code>SharedEntity</code> have special permission rules
 * applied to them when deciding authorization.
 *
 * @see IntlibPermissionEvaluator
 * @see cz.cuni.mff.xrg.odcs.commons.app.dao.db.Authorizator
 * 
 * @author Jan Vojt
 */
public interface SharedEntity {
	
	/**
	 * 
	 * @return 
	 */
	public ShareType getShareType();

}
