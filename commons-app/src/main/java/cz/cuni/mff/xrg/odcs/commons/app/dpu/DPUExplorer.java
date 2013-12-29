package cz.cuni.mff.xrg.odcs.commons.app.dpu;

import cz.cuni.mff.xrg.odcs.commons.app.constants.LenghtLimits;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.data.DataUnitDescription;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.annotation.AnnotationContainer;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.annotation.AnnotationGetter;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ModuleFacade;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsLoader;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsTransformer;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;


/**
 * Class which provides methods that can be used to explore DPU instance.
 * 
 * @author Petyr
 * 
 */
public class DPUExplorer {

	/**
	 * Name of property that stores jar-file's description.
	 */
	private static final String DPU_JAR_DESCRIPTION_NAME = "Bundle-Description";

	/**
	 * Module facade used to access the DPU instances.
	 */
	@Autowired
	private ModuleFacade moduleFacade;

	/**
	 * Try to find out given DPU instance type.
	 * 
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
	 * @param dpu {@link DPUTemplateRecord} to explore
	 * @return Description stored in manifest file or null in case of error.
	 */
	public String getJarDescription(DPUTemplateRecord dpu) {
		// we try to use pom.xml information
		Dictionary<String, String> attributes = moduleFacade
				.getJarProperties(dpu);
		if (attributes == null) {
			// can't load information .. we run out of options
			return "";
		}
		String jarDescription = attributes.get(DPU_JAR_DESCRIPTION_NAME);
		// check for length
		if (jarDescription.length() > LenghtLimits.DPU_JAR_DESCRIPTION.limit()) {
			jarDescription = jarDescription.substring(0, 
					LenghtLimits.DPU_JAR_DESCRIPTION.limit());
		}
		return jarDescription;
	}

	/**
	 * Return list of input {@link DataUnit}'s descriptions for given DPU. Need
	 * to load instance of given DPU.
	 * 
	 * @param dpu
	 * @return Does not return null.
	 */
	public List<DataUnitDescription> getInputs(DPURecord dpu) {
		// we need to load and get instance
		final Object dpuInstance = getInstance(dpu);
		// get annotations
		List<AnnotationContainer<InputDataUnit>> inputs = AnnotationGetter
				.getAnnotations(dpuInstance, InputDataUnit.class);
		// translate to list
		List<DataUnitDescription> result = new ArrayList<>(inputs.size());
		for (AnnotationContainer<InputDataUnit> item : inputs) {
			final InputDataUnit annotation = item.getAnnotation();
			// create description
			result.add(DataUnitDescription.createInput(annotation.name(), item
					.getField().getClass().getName(), annotation.description(),
					annotation.optional()));
		}
		return result;
	}

	/**
	 * Return list of output {@link DataUnit}'s descriptions for given DPU. Need
	 * to load instance of given DPU.
	 * 
	 * @param dpu
	 * @return Does not return null.
	 */
	public List<DataUnitDescription> getOutputs(DPURecord dpu) {
		// we need to load and get instance
		final Object dpuInstance = getInstance(dpu);
		// get annotations
		List<AnnotationContainer<OutputDataUnit>> outputs = AnnotationGetter
				.getAnnotations(dpuInstance, OutputDataUnit.class);
		// translate to list
		List<DataUnitDescription> result = new ArrayList<>(outputs.size());
		for (AnnotationContainer<OutputDataUnit> item : outputs) {
			final OutputDataUnit annotation = item.getAnnotation();
			// create description			
			result.add(DataUnitDescription.createOutput(annotation.name(), item
					.getField().getClass().getName(), annotation.description()));
		}
		return result;
	}

	/**
	 * Load and return instance of given DPU. Does use {@link #moduleFacade}.
	 * 
	 * @param dpu
	 * @return Does not return null.
	 */
	private Object getInstance(DPURecord dpu) {
		try {
			// TODO Petyr: Work with exception ..
			dpu.loadInstance(moduleFacade);
		} catch (ModuleException e) {
			throw new RuntimeException(e);
		}
		return dpu.getInstance();
	}

}
