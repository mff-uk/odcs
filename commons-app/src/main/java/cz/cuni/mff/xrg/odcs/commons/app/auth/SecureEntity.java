package cz.cuni.mff.xrg.odcs.commons.app.auth;

/**
 *
 * @author Jan Vojt
 */
public interface SecureEntity {
	
	public boolean isDeletable();
	
	public void setDeletable(boolean deletable);

}
