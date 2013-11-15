package cz.cuni.mff.xrg.odcs.commons.app.execution;

/**
 * Contains definition of instruction that can be used on edges in the pipeline graph.
 * 
 * The grammar for the instruction is:
 * Start -> Element | Element; Start
 * Element -> Name Command		// The commands apply on all the DataUnits with given name
 * Command -> rename Name		// rename DataUnit
 * 
 * @author Petyr
 *
 */
public enum DataUnitMergerInstructions {
	Rename("->"),
	Separator(";");
	
	/**
	 * The command name.
	 */
	private final String command;
	
	private DataUnitMergerInstructions(String command) {
		this.command = command;
	}
	
	public String getValue() {
		return this.command;
	}
}
