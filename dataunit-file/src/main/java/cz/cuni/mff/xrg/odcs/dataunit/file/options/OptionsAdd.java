package cz.cuni.mff.xrg.odcs.dataunit.file.options;

/**
 * Holds options for process of adding existing file/directory. 
 * 
 * @author Petyr
 */
public class OptionsAdd {
	
	/**
	 * If true then given file/directory is added only as a link.
	 */
	private final boolean isLink;
	
	/**
	 * When true and there already exist file of same name as the name of file
	 * that is being added then the original file is overwritten by the new one.
	 * Otherwise the old file is preserved.
	 */
	private final boolean overwrite;
	
	public OptionsAdd() {
		this.isLink = true;
		this.overwrite = true;
	}

	public OptionsAdd(boolean asLink) {
		this.isLink = asLink;
		this.overwrite = true;
	}

	public OptionsAdd(boolean asLink, boolean overwrite) {
		this.isLink = asLink;
		this.overwrite = overwrite;
	}
	
	
	public boolean isLink() {
		return this.isLink;
	}
	
	public boolean overwrite() {
		return this.overwrite;
	}
	
}
