package cz.cuni.intlib.xrg.commons.app.data.user;

/**
 * Set of roles in the system.
 *
 * @author Jiri Tomes
 */
public enum Role {

    USER, ADMINISTRATOR;

    public String getStringRole() {
        return name();
    }
}
