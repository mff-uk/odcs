package cz.cuni.xrg.intlib.commons.app.user;

/**
 * Interface for controling user rights.
 * 
 * @author Jiri Tomes
 */
public interface Autorizator {
    
    public boolean isAllowed(Role role, Account account, Privilege privilege);
}
