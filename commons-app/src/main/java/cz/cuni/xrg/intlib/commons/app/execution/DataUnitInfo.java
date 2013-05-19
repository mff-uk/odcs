package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Holds information about single DataUnit' context.
 * 
 * @author Petyr
 *
 */
@XmlAccessorType(XmlAccessType.FIELD) 
public class DataUnitInfo {
	
	/**
	 * Associated directory.
	 */
	@XmlValue
	private File directory;
	
	/**
	 * True if the data unit is created as a input.
	 */
	@XmlValue
	private boolean isInput;
	
	/**
	 * DataUnit type. 
	 */
	@XmlAttribute
	private DataUnitType type;
	
	public DataUnitInfo(File directory, boolean isInput, DataUnitType type) {
		this.directory = directory;
		this.isInput = isInput;
		this.type = type;
	}

	public File getDirectory() {
		return directory;
	}

	public boolean isInput() {
		return isInput;
	}

	public DataUnitType getType() {
		return type;
	}
}