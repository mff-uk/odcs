package cz.cuni.xrg.intlib.commons;

/**
 * Basic types of DPU.
 *
 * @author Jiri Tomes
 * @author Jan Vojt
 */
public enum DpuType {
	
    EXTRACTOR("extractor"),
    TRANSFORMER("transformer"),
    LOADER("loader");
	
	/** String representation of this enum. */
	private final String name;
	
	/** String constructor. */
	private DpuType(final String name) {
		this.name = name;
	}

	/** Implicit conversion to String. */
	@Override
	public String toString() {
		return name;
	}
	
}
