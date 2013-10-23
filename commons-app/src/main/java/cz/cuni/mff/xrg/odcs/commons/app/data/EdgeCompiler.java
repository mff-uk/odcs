package cz.cuni.mff.xrg.odcs.commons.app.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPURecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.annotation.AnnotationContainer;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.annotation.AnnotationGetter;
import cz.cuni.mff.xrg.odcs.commons.app.execution.DataUnitMergerInstructions;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Edge;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import java.util.Arrays;

/**
 * Class provides functionality that enables to create script for the single
 * edge from list of DataUnit's names and list of indexes. Also the list of
 * indexes can be recreated from list of names and program.
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
	 * 
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
	@Deprecated
	private Object getInstance(DPURecord dpu) {
		try {
			// TODO Petyr: Work with exception ..
			dpu.loadInstance(moduleFacade);
		} catch (ModuleException e) {
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
	 * @deprecated Use {@ DPUExplorer#getInputss}
	 */
	@Deprecated
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
	 * @deprecated Use {@ DPUExplorer#getOutputs}
	 */
	@Deprecated
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
	 * @deprecated use {@link #compile(List, List, List)}.
	 */
	@Deprecated
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
	 * Create script that maps output {@link DataUnit}s (sourceNames) to input
	 * {@link DataUnit}s (targetNames). Does not use {@link #moduleFacade}.
	 * 
	 * @param mapping
	 * @param sources
	 * @param targets
	 * @return Does not return null.
	 */
	public String compile(List<MutablePair<List<Integer>, Integer>> mappings,
			List<DataUnitDescription> sources,
			List<DataUnitDescription> targets) {
		StringBuilder script = new StringBuilder();
		// for each sourceNames we need action
		for (int index = 0; index < sources.size(); ++index) {
			boolean used = false;
			// we need to know in which mapping is used
			String source = sources.get(index).getName();
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
					script.append(targets.get(item.right).getName());
					script.append(DataUnitMergerInstructions.Separator
							.getValue());
					used = true;
				}
			}
			if (!used) {
				// dataUnits are dropped automatically if they are not used
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
	 * @deprecated use {@link #decompile(String, List, List)}
	 */
	@Deprecated
	public List<MutablePair<List<Integer>, Integer>> decompileMapping(String script,
			List<String> sourceNames,
			List<String> targetNames) {
		List<MutablePair<List<Integer>, Integer>> mappings = new LinkedList<>();
		// if script is empty return
		if (script == null || script.isEmpty()) {
			return mappings;
		}
		// parse commands
		String[] commands = script.split(DataUnitMergerInstructions.Separator
				.getValue());
		for (String item : commands) {
			String[] cmd = item.split(" ");
			if (cmd.length != 3) {
				// other then rename -> ignore
				continue;
			}
			if (cmd[1].equalsIgnoreCase(DataUnitMergerInstructions.Rename
					.getValue())) {
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
	 * Decompile script and create mapping from based on given {@link DataUnit}s
	 * names. Mapping is from sourceNames to targetNames. Does not use
	 * {@link #moduleFacade}.
	 * 
	 * @param script
	 * @param sources
	 * @param targets
	 * @return Does not return null.
	 */
	public List<MutablePair<List<Integer>, Integer>> decompile(String script,
			List<DataUnitDescription> sources,
			List<DataUnitDescription> targets) {
		List<MutablePair<List<Integer>, Integer>> mappings = new LinkedList<>();
		// if script is empty return
		if (script == null || script.isEmpty()) {
			return mappings;
		}
		// parse commands
		String[] commands = script.split(DataUnitMergerInstructions.Separator
				.getValue());
		for (String item : commands) {
			String[] cmd = item.split(" ");
			if (cmd.length != 3) {
				// other then rename -> ignore
				continue;
			}
			if (cmd[1].equalsIgnoreCase(DataUnitMergerInstructions.Rename
					.getValue())) {
				// rename action
			} else {
				// other action -> ignore
				continue;
			}
			// get indexes
			Integer sourceIndex = getIndex(sources, cmd[0]);
			Integer targetIndex = getIndex(targets, cmd[2]);
			if (sourceIndex == null || targetIndex == null) {
				// mapping for no longer existing DataUnitDescription
				continue;
			}
			
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
	 * Constructs default mapping if source DPU has exactly one output data unit
	 * and target DPU also has exactly one input data unit.
	 * 
	 * @param edge Edge which default mapping should be created.
	 * @deprecated Use {@link #addDefaultMapping(Edge,List<DataUnitDescription>,List<DataUnitDescription>)}
	 */
	@Deprecated
	public void addDefaultMapping(Edge edge) {
		List<String> outputDataUnits = getOutputNames(edge.getFrom()
				.getDpuInstance());
		List<String> inputDataUnits = getInputNames(edge.getTo()
				.getDpuInstance());
		if (outputDataUnits.size() == 1 && inputDataUnits.size() == 1) {
			MutablePair<List<Integer>, Integer> defaultMapping = new MutablePair<>(
					Arrays.asList(0), 0);
			String script = compileScript(Arrays.asList(defaultMapping),
					outputDataUnits, inputDataUnits);
			edge.setScript(script);
		}
	}
	
	/**
	 * Constructs default mapping if source DPU has exactly one output data unit
	 * and target DPU also has exactly one input data unit. 
	 * Any previous mapping will be lost.
	 * 
	 * @param edge Edge which default mapping should be created.
	 * @param source Description of source {@link DataUnits}.
	 * @param target Description of source {@link DataUnits}.
	 */
	public void setDefaultMapping(Edge edge, List<DataUnitDescription> source, 
			List<DataUnitDescription> target) {
		if (source.size() == 1 && target.size() == 1) {
			MutablePair<List<Integer>, Integer> defaultMapping = new MutablePair<>(
					Arrays.asList(0), 0);
			String script = compile(Arrays.asList(defaultMapping),
					source, target);
			edge.setScript(script);
			
		}
	}
	
	/**
	 * Return index of first {@link DataUnitDescription} with given name or null
	 * if there is no {@link DataUnitDescription} with required name.
	 * @param data
	 * @param name
	 * @return Index of to data or null.
	 */
	private Integer getIndex(List<DataUnitDescription> data, String name) {
		int index = 0;
		for (DataUnitDescription item : data) {
			if (item.getName().compareTo(name) == 0) {
				return index; 
			}
			++index;
		}		
		return null;
	}
	
}
