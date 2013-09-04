package cz.cuni.xrg.intlib.commons.app.dpu;

import java.util.jar.Attributes;

import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;

import cz.cuni.xrg.intlib.commons.dpu.annotation.AsExtractor;
import cz.cuni.xrg.intlib.commons.dpu.annotation.AsTransformer;
import cz.cuni.xrg.intlib.commons.dpu.annotation.AsLoader;

/**
 * Class provides methods that can be used to explore DPU instance.
 * 
 * @author Petyr
 *
 */
public class DPUExplorer {
	
	/**
	 * Name of property that store jar-file's description.
	 */
	private static final String DPU_JAR_DESCRIPTION_NAME = "Description";
	
	/**
	 * Module facade used to access the DPU instances.
	 */
	@Autowired
	private ModuleFacade moduleFacade;
		
	/**
	 * Try to find out given DPU instance type. 
	 * @param DPUInstance
	 * @param relativePath Relative path to the DPU.
	 * @return Null if nothing about type can be found.
	 */
	public DPUType getType(Object DPUInstance, String relativePath) {
		// try use annotations to resolve DPU type
		Class<?> objectClass = DPUInstance.getClass();
		if (objectClass.getAnnotation(AsExtractor.class) != null) {
			return DPUType.EXTRACTOR;
		} else if (objectClass.getAnnotation(AsTransformer.class) != null) {
			return DPUType.TRANSFORMER;
		} else if (objectClass.getAnnotation(AsLoader.class) != null) {
			return DPUType.LOADER;
		}		
		
		// we do not know
		return null;
	}
	
	/**
	 * Return content of manifest for given bundle that is stored in DPU's
	 * directory. This method does not load DPU into system.
	 * 
	 * @param relativePath Relative path in DPU's directory.
	 * @return Description stored in manifest file or null in case of error.
	 */	
	public String getJarDescription(String relativePath) {
		// we try to use pom.xml information
		Attributes attributes = moduleFacade.getJarProperties(relativePath);
		if (attributes == null) {
			// can't load information .. we run out of options
			return null;
		}
		return attributes.getValue(DPU_JAR_DESCRIPTION_NAME);
	}
}
