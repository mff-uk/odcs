package cz.cuni.intlib.xrg.commons.app.data;

/**
 * For identifying/saving object to DB/Virtuoso.
 *
 * @author Jiri Tomes
 */
public interface Resource {

    public String getID();

    public String createUniqueID();
}
