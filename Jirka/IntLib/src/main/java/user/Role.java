package user;

/**
 *
 * @author Jiri Tomes
 */
public enum Role {

    USER, ADMINISTRATOR;

    public String getStringRole() {
        return name();
    }
}
