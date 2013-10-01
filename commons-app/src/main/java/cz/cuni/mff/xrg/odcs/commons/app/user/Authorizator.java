package cz.cuni.mff.xrg.odcs.commons.app.user;

/**
 * Abstract for deciding whether given {@link User} has a given {@link Privilege}.
 * 
 * @author Jiri Tomes
 */
public interface Authorizator {
    
    public boolean isAllowed(Role role, User account, Privilege privilege);
}
