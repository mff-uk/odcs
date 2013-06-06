package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import javax.persistence.*;

/**
 * Holds information about single DataUnit' context.
 * 
 * @author Petyr
 *
 */
@Entity
@Table(name = "dataunit_info")
public class DataUnitInfo {

	/**
	 * Primary key.
	 */
	@Id
	private Long id;
	
	/**
	 * Associated working directory. Absolute path.
	 */
	@Column(name="directory")
	private File directory;
		
	/**
	 * DataUnit type. 
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name="type")
	private DataUnitType type;
	
	/**
	 * True if use as input.
	 */
	@Column(name="is_input")
	private boolean isInput;
	
	/**
	 * Empty ctor because of JAP.
	 */	
	public DataUnitInfo() { }
	
	public DataUnitInfo(File directory, DataUnitType type, boolean isInput) {
		this.directory = directory;
		this.type = type;
		this.isInput = isInput;
	}

	public File getDirectory() {
		return directory;
	}

	public DataUnitType getType() {
		return type;
	}

	public boolean isInput() {
		return isInput;
	}
}