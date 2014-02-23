package cz.cuni.mff.xrg.odcs.commons.app.data;

import cz.cuni.mff.xrg.odcs.commons.app.data.handlers.LogAndIgnore;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUExplorer;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Edge;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;
import java.util.ArrayList;
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

	private final static ErrorHandler DEFAULT_HANDLER = new LogAndIgnore();

	/**
	 * Interface used as error handler.
	 */
	public interface ErrorHandler {

		/**
		 * Called if index of input data unit is out of range.
		 */
		public void sourceIndexOutOfRange();

		/**
		 * Called if index of output data unit is out of range.
		 */
		public void targetIndexOutOfRange();

		/**
		 * Called when there is unknown command in script.
		 *
		 * @param item Unknown command.
		 */
		public void unknownCommand(String item);

		/**
		 * Called in case of invalid mapping.
		 *
		 * @param item The invalid mapping.
		 */
		public void invalidMapping(String item);

	}

	/**
	 * Create script that maps output
	 * {@link cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo}s
	 * (sourceNames) to input
	 * {@link cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo}s
	 * (targetNames). Does not use
	 * {@link cz.cuni.mff.xrg.odcs.commons.app.facade.ModuleFacade}.
	 *
	 * @param mappings Mappings to compile into script.
	 * @param sources  List of sources data units descriptions.
	 * @param targets  List of target data units descriptions.
	 * @return Does not return null.
	 * @deprecated Use translate instead
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
					script.append(EdgeInstructions.Rename.getValue());
					script.append(' ');
					script.append(targets.get(item.right).getName());
					script.append(EdgeInstructions.Separator
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
	 * Translate given mapping into string representation. Does not check for
	 * validity of mapping.
	 *
	 * @param mappings Pairs of mapping from sources to targets.
	 * @param sources  Sources data unit.
	 * @param targets  Targets data unit.
	 * @param handler  Can be null, in such case default handler is used.
	 * @return mapping translation into string
	 */
	public String translate(List<MutablePair<Integer, Integer>> mappings,
			List<DataUnitDescription> sources,
			List<DataUnitDescription> targets,
			ErrorHandler handler) {
		// check for error handler
		if (handler == null) {
			handler = DEFAULT_HANDLER;
		}

		final StringBuilder script = new StringBuilder();
		// add information about version into mapping
//		script.append("#1");
//		script.append(EdgeInstructions.Separator.getValue());
		// go through the sources
		for (MutablePair<Integer, Integer> mapping : mappings) {
			if (mapping.left < 0 && mapping.left >= sources.size()) {
				handler.sourceIndexOutOfRange();
				continue;
			}
			if (mapping.right < 0 && mapping.right >= targets.size()) {
				handler.targetIndexOutOfRange();
				continue;
			}
			final String source = sources.get(mapping.left).getName();
			final String target = targets.get(mapping.right).getName();
			// add to mapping
			script.append(source);
			script.append(' ');
			script.append(EdgeInstructions.Rename.getValue());
			script.append(' ');
			script.append(target);
			script.append(EdgeInstructions.Separator.getValue());
		}
		return script.toString();
	}

	/**
	 * Parse given script into the mapping. Does not check for validity of
	 * mapping.
	 *
	 * @param script  The script with mapping.
	 * @param sources Sources data unit.
	 * @param targets Targets data unit.
	 * @param handler Handler to use during translation, can be null.
	 * @return If return it's never null.
	 */
	public List<MutablePair<Integer, Integer>> translate(String script,
			List<DataUnitDescription> sources,
			List<DataUnitDescription> targets,
			ErrorHandler handler) {
		// check for error handler
		if (handler == null) {
			handler = DEFAULT_HANDLER;
		}

		List<MutablePair<Integer, Integer>> mappings = new LinkedList<>();
		// if script is empty return
		if (script == null || script.isEmpty()) {
			return mappings;
		}
		// parse commands
		final String[] commands
				= script.split(EdgeInstructions.Separator.getValue());
		for (String item : commands) {
			if (item.startsWith("#")) {
				// version, does not care about it now
				continue;
			}

			final String[] cmd = item.split(" ");
			if (cmd.length != 3) {
				// other then rename -> ignore
				handler.unknownCommand(item);
				continue;
			}
			if (cmd[1].equalsIgnoreCase(EdgeInstructions.Rename.getValue())) {
				// rename action
			} else {
				// other action -> ignore
				handler.unknownCommand(item);
				continue;
			}
			// get indexes
			final Integer sourceIndex = getIndex(sources, cmd[0]);
			final Integer targetIndex = getIndex(targets, cmd[2]);
			if (sourceIndex == null || targetIndex == null) {
				// mapping for no longer existing DataUnitDescription
				handler.invalidMapping(item);
				continue;
			}
			// add mapping
			MutablePair<Integer, Integer> mapping = new MutablePair<>();
			mapping.left = sourceIndex;
			mapping.right = targetIndex;
			mappings.add(mapping);
		}
		return mappings;
	}

	/**
	 * Decompile script and create mapping from based on given
	 * {@link cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo}s
	 * names. Mapping is from sourceNames to targetNames. Does not use
	 * {@link cz.cuni.mff.xrg.odcs.commons.app.facade.ModuleFacade}.
	 *
	 * @param script  Script to decompile.
	 * @param sources List of sources data units descriptions
	 * @param targets List of targets data units descriptions.
	 * @return Does not return null.
	 * @deprecated Use translate instead
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
		String[] commands = script.split(EdgeInstructions.Separator
				.getValue());
		for (String item : commands) {
			String[] cmd = item.split(" ");
			if (cmd.length != 3) {
				// other then rename -> ignore
				continue;
			}
			if (cmd[1].equalsIgnoreCase(EdgeInstructions.Rename
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
			MutablePair<List<Integer>, Integer> mapping = new MutablePair<>();
			mapping.left = new LinkedList<>();
			mapping.left.add(sourceIndex);
			mapping.right = targetIndex;
			mappings.add(mapping);
		}
		return mappings;
	}

	/**
	 * Update script and remove invalid mappings.
	 *
	 * @param edge    Edge which script update.
	 * @param sources List of sources data units descriptions
	 * @param targets List of targets data units descriptions
	 * @return List of invalid mappings.
	 * @deprecated Use translate, for decompile with StoreInvalidMappings and
	 * then compile it again
	 */
	public List<String> update(Edge edge,
			List<DataUnitDescription> sources,
			List<DataUnitDescription> targets) {
		final List<String> invalidMappings = new LinkedList<>();
		final String script = edge.getScript();
		// if script is empty return
		if (script == null || script.isEmpty()) {
			return invalidMappings;
		}
		// parse commands
		String[] commands = script.split(EdgeInstructions.Separator.getValue());
		for (String item : commands) {
			if (item.startsWith("#")) {
				// version -> ignore for now
				continue;
			}
			String[] cmd = item.split(" ");
			if (cmd.length != 3) {
				// other then rename -> ignore
				continue;
			}
			if (cmd[1].equalsIgnoreCase(EdgeInstructions.Rename.getValue())) {
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
				invalidMappings.add(item);
			}
		}
		if (!invalidMappings.isEmpty()) {
			List<MutablePair<List<Integer>, Integer>> validMappings = decompile(
					script, sources, targets);
			edge.setScript(compile(validMappings, sources, targets));
		}
		return invalidMappings;
	}

	/**
	 * Create default mapping if source.size() == target.size() == 1. And return
	 * script for it.
	 *
	 * @param source List of sources data units descriptions
	 * @param target List of targets data units descriptions
	 * @return Empty string in case that the default mapping can not be created.
	 */
	public String createDefaultMapping(List<DataUnitDescription> source,
			List<DataUnitDescription> target) {
		if (source.size() == 1 && target.size() == 1) {
			final List<MutablePair<Integer, Integer>> mapping = new ArrayList<>();
			mapping.add(new MutablePair<>(0, 0));
			return translate(mapping, source, target, null);
		} else {
			return "";
		}
	}

	/**
	 * Constructs default mapping if source DPU has exactly one output data unit
	 * and target DPU also has exactly one input data unit. Any previous mapping
	 * will be lost.
	 *
	 * @param edge   Edge which default mapping should be created.
	 * @param source Description of source
	 *               {@link cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo}.
	 * @param target Description of source
	 *               {@link cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo}.
	 * @deprecated use createDefaultMapping instead
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
	 *
	 * @param data List of data units descriptions.
	 * @param name Name of required data unit.
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

	/**
	 *
	 * @param graph Graph to check.
	 * @param explorer 
	 * @return validation report as string
	 * @deprecated Use
	 * {@link cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineValidator}
	 * instead
	 */
	public String checkMandatoryInputsAndOutputs(PipelineGraph graph,
			DPUExplorer explorer) {
		String report = "";
		for (Node node : graph.getNodes()) {
			DPUInstanceRecord dpu = node.getDpuInstance();
			List<Edge> edgesTo = null;
			List<DataUnitDescription> inputs = explorer.getInputs(dpu);
			if (!inputs.isEmpty()) {
				edgesTo = graph.getEdgesTo(node);
			}
			for (DataUnitDescription input : inputs) {
				boolean found = false;
				if (input.getOptional()) {
					continue;
				}
				for (Edge e : edgesTo) {
					if (e.getScript().contains("-> " + input.getName())) {
						found = true;
						break;
					}
				}
				if (!found) {
					report += String.format("\nDPU: %s, Input: %s", dpu
							.getName(), input.getName());
				}
			}
			List<DataUnitDescription> outputs = explorer.getOutputs(dpu);
			List<Edge> edgesFrom = null;
			if (!outputs.isEmpty()) {
				edgesFrom = graph.getEdgesFrom(node);
			}
			for (DataUnitDescription output : outputs) {
				boolean found = false;
				if (output.getOptional()) {
					continue;
				}
				for (Edge e : edgesFrom) {
					if (e.getScript().contains(output.getName() + " ->")) {
						found = true;
						break;
					}
				}
				if (!found) {
					report += String.format("\nDPU: %s, Output: %s", dpu
							.getName(), output.getName());
				}
			}
		}
		if (report.isEmpty()) {
			return null;
		} else {
			return report;
		}
	}

}
