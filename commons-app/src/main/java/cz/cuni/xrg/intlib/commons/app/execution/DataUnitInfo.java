package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
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
@Entity
@Table(name = "dataUnit_info")
public class DataUnitInfo {
	
	/**
	 * Associated working directory. Absolute path.
	 */
	@Column(name="directory")
	private File directory;
		
	/**
	 * DataUnit type. 
	 */
	@Enumerated(EnumType.ORDINAL)
	private DataUnitType type;
	
	/**
	 * True if use as input.
	 */
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