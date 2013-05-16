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
class DataUnitInfo {
	
	/**
	 * Associated directory.
	 */
	@XmlAttribute
	public File directory;
	
	/**
	 * DataUnit type. 
	 */
	@XmlValue
	public DataUnitType type;
	
	public DataUnitInfo(File directory, DataUnitType type) {
		this.directory = directory;
		this.type = type;
	}
}