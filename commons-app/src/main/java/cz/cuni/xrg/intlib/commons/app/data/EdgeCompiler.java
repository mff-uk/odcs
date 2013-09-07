package cz.cuni.xrg.intlib.commons.app.data;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;

import cz.cuni.xrg.intlib.commons.app.dpu.DPURecord;
import cz.cuni.xrg.intlib.commons.app.dpu.annotation.AnnotationContainer;
import cz.cuni.xrg.intlib.commons.app.dpu.annotation.AnnotationGetter;
import cz.cuni.xrg.intlib.commons.app.execution.DataUnitMergerInstructions;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Edge;
import cz.cuni.xrg.intlib.commons.dpu.annotation.InputDataUnit;
import cz.cuni.xrg.intlib.commons.dpu.annotation.OutputDataUnit;
import java.util.Arrays;

/**
 * Class provides functionality that enables to create script for the single edge from
 * list of DataUnit's names and list of indexes. Also the list of indexes can be
 * recreated from list of names and program.
 * 
 * @author Petyr
 * 
 */
public final class EdgeCompiler {

	/**
	 * We need to load instances as they contains the information about
	 * DataUnit's name.
	 */
	private ModuleFacade moduleFacade;

	/**
	 * {@link #moduleFacade} can be null as it is not used by all the methods
	 * for more details see function respective comments. 
	 * @param moduleFacade Can be null. 
	 */
	public EdgeCompiler(ModuleFacade moduleFacade) {
		this.moduleFacade = moduleFacade;
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
		} catch (ModuleException | FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return dpu.getInstance();
	}

	/**
	 * Return list of input {@link DataUnit}'s names for given DPU. Need to load
	 * instance of given DPU. Does use {@link #moduleFacade}.
	 * 
	 * @param dpu
	 * @return Does not return null.
	 */
	public List<String> getInputNames(DPURecord dpu) {
		// we need to load and get instance
		Object dpuInstance = getInstance(dpu);
		// get annotations
		List<AnnotationContainer<InputDataUnit>> inputs = AnnotationGetter
				.getAnnotations(dpuInstance, InputDataUnit.class);
		// translate to list
		List<String> result = new ArrayList<String>(inputs.size());
		for (AnnotationContainer<InputDataUnit> item : inputs) {
			result.add(item.getAnnotation().name());
		}
		return result;
	}

	/**
	 * Return list of input {@link DataUnit}'s names for given DPU. Need to load
	 * instance of given DPU. Does use {@link #moduleFacade}.
	 * 
	 * @param dpu
	 * @return
	 */
	public List<String> getOutputNames(DPURecord dpu) {
		// we need to load and get instance
		Object dpuInstance = getInstance(dpu);
		// get annotations
		List<AnnotationContainer<OutputDataUnit>> inputs = AnnotationGetter
				.getAnnotations(dpuInstance, OutputDataUnit.class);
		// translate to list
		List<String> result = new ArrayList<String>(inputs.size());
		for (AnnotationContainer<OutputDataUnit> item : inputs) {
			result.add(item.getAnnotation().name());
		}
		return result;
	}

	/**
	 * Create script that maps output {@link DataUnit}s (sourceNames) to input
	 * {@link DataUnit}s (targetNames). Does not use {@link #moduleFacade}.
	 * 
	 * @param mapping
	 * @param sourceNames
	 * @param targetNames
	 * @return Does not return null.
	 */
	public String compileScript(List<MutablePair<List<Integer>, Integer>> mappings,
			List<String> sourceNames,
			List<String> targetNames) {
		StringBuilder script = new StringBuilder();
		// for each sourceNames we need action
		for (int index = 0; index < sourceNames.size(); ++index) {
			boolean used = false;
			// we need to know in which mapping is used
			String source = sourceNames.get(index);
			for (MutablePair<List<Integer>, Integer> item : mappings) {
				if (item.left.contains(index)) {
					if (used) {
						// multiple usage
						// TODO Petyr: throw exception
					}
					// dataUnit is used in this renaming
					script.append(source);
					script.append(' ');
					script.append(DataUnitMergerInstructions.Rename.getValue());
					script.append(' ');
					script.append(targetNames.get(item.right));
					script.append(DataUnitMergerInstructions.Separator
							.getValue());
					used = true;
				}
			}
			if (!used) {
				// TODO Petyr: drop DataUnit
			}
		}
		return script.toString();
	}

	/**
	 * Decompile script and create mapping from based on given {@link DataUnit}s
	 * names. Mapping is from sourceNames to targetNames. Does not use 
	 * {@link #moduleFacade}.
	 * 
	 * @param script
	 * @param sourceNames
	 * @param targetNames
	 * @return Does not return null.
	 */
	public List<MutablePair<List<Integer>, Integer>> decompileMapping(
			String script,
			List<String> sourceNames,
			List<String> targetNames) {
		List<MutablePair<List<Integer>, Integer>> mappings  = new LinkedList<>();
		// if script is empty return 
		if (script == null || script.isEmpty()) {
			return mappings;
		}
		// parse commands
		String[] commands = script.split(DataUnitMergerInstructions.Separator.getValue());
		for (String item : commands) {
			String[] cmd = item.split(" ");
			if (cmd.length != 3) {
				// other then rename -> ignore
				continue;
			}
			if (cmd[1].equalsIgnoreCase(DataUnitMergerInstructions.Rename.getValue())) {
				// rename action 
			} else {
				// other action -> ignore
				continue;
			}
			// get indexes
			Integer sourceIndex = sourceNames.indexOf(cmd[0]);
			Integer targetIndex = targetNames.indexOf(cmd[2]);
			// add mapping
			boolean added = false;
			for (MutablePair<List<Integer>, Integer> mapping : mappings) {
				if (mapping.right == targetIndex) {
					mapping.left.add(sourceIndex);
					added = true;
					break;
				}
			}
			if (added) {
				// mapping added 
			} else {
				// this is the first mapping for targetIndex
				MutablePair<List<Integer>, Integer> mapping = new MutablePair<>(); 
				mapping.left = new LinkedList<>();
				mapping.left.add(sourceIndex);
				mapping.right = targetIndex;
				// add to mapping
				mappings.add(mapping);
			}
		}
		return mappings;
	}

	/**
	 * Constructs default mapping if source DPU has exactly one output data unit and target DPU also has exactly one input data unit.
	 * 
	 * @param edge Edge which default mapping shloud be created.
	 */
	public void addDefaultMapping(Edge edge) {
		List<String> outputDataUnits = getOutputNames(edge.getFrom().getDpuInstance());
		List<String> inputDataUnits = getInputNames(edge.getTo().getDpuInstance());
		if(outputDataUnits.size() == 1 && inputDataUnits.size() == 1) {
			MutablePair<List<Integer>, Integer> defaultMapping = new MutablePair<>(Arrays.asList(0), 0);
			String script = compileScript(Arrays.asList(defaultMapping), outputDataUnits, inputDataUnits);
			edge.setScript(script);
		}
	}
}
