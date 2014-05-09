package cz.cuni.mff.xrg.odcs.dataunit.file.options;

/**
 * Holds options used for adding existing file/directory.
 * 
 * @author Petyr
 */
public class OptionsAdd {

    /**
     * If true then given file/directory is added only as a link. The linked
     * file/directory can not be modified.
     */
    private final boolean isLink;

    /**
     * When true and there already exist file of same name as the name of file
     * that is being added then the original file is overwritten by the new one.
     * Otherwise the old file is preserved.
     */
    private final boolean overwrite;

    /**
     * Create options that add given object as link. In case of collision the
     * old data are overwritten.
     */
    public OptionsAdd() {
        this.isLink = true;
        this.overwrite = true;
    }

    /**
     * In case of collision the old data are overwritten.
     * 
     * @param asLink
     *            true if add as link
     */
    public OptionsAdd(boolean asLink) {
        this.isLink = asLink;
        this.overwrite = true;
    }

    /**
     * Create an option class with given options.
     * 
     * @param asLink
     *            true if add as link
     * @param overwrite
     *            true if in case of collision overwrite old data
     */
    public OptionsAdd(boolean asLink, boolean overwrite) {
        this.isLink = asLink;
        this.overwrite = overwrite;
    }

    /**
     * @return True if the Handler should be added as a link.
     */
    public boolean isLink() {
        return this.isLink;
    }

    /**
     * @return True if in the case of collision the old Handler should be deleted.
     */
    public boolean overwrite() {
        return this.overwrite;
    }

}
