package user;

/**
 *
 * @author Jiri Tomes
 */
public interface Autorizator {
    
    public boolean isAllowed(Role role,Account account, Privilege privilege);
}
