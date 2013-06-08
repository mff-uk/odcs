package cz.cuni.xrg.intlib.commons.app.dpu;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import cz.cuni.xrg.intlib.commons.configuration.Config;
import javax.persistence.Transient;

/**
 * 
 * 
 * @author Petyr
 * 
 */
@Entity
@Table(name = "dpu_template")
public class DPUTemplateRecord extends DPURecord {

	/**
	 * Visibility in DPUTree.
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name="visibility")
	private VisibilityType visibility;
	
	/**
	 * Description obtained from jar file manifest.
	 */
	@Column(name="jar_description")
	private String jarDescription;
	
	public VisibilityType getVisibility() {
		return visibility;
	}

	public void setVisibility(VisibilityType visibility) {
		this.visibility = visibility;
	}

	public String getJarDescription() {
		return jarDescription;
	}

	public void setJarDescription(String jarDescription) {
		this.jarDescription = jarDescription;
	}
}
