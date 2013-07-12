package cz.cuni.xrg.intlib.commons.app.execution;

/**
 * Contains definition of instruction that can be used on edges in the pipeline graph.
 * 
 * The grammar for the instruction is:
 * Start -> Element | Element; Start
 * Element -> Name Command		// The commands apply on all the DataUnits with given name
 * Command -> rename Name		// rename DataUnit
 * Command -> drop				// drop DataUnit
 * 
 * @author Petyr
 *
 */
public enum DataUnitMergerInstructions {
	Rename("rename"),
	Drop("drop"),
	Separator(";");
	
	/**
	 * The command name.
	 */
	private String command;
	
	private DataUnitMergerInstructions(String command) {
		this.command = command;
	}
	
	public String getValue() {
		return this.command;
	}
}
