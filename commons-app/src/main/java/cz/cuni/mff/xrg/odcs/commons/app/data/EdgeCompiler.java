package cz.cuni.mff.xrg.odcs.commons.app.data;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUExplorer;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;

import cz.cuni.mff.xrg.odcs.commons.app.execution.DataUnitMergerInstructions;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Edge;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;
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
     * Create script that maps output {@link DataUnit}s (sourceNames) to input
     * {@link DataUnit}s (targetNames). Does not use {@link #moduleFacade}.
     *
     * @param mappings
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
     * Update script and remove invalid mappings.
     *
     * @param edge
     * @param sources
     * @param targets
     * @return List of invalid mappings.
     *
     */
    public List<String> update(Edge edge, List<DataUnitDescription> sources, List<DataUnitDescription> targets) {
        List<String> invalidMappings = new LinkedList<>();
        String script = edge.getScript();
        // if script is empty return
        if (script == null || script.isEmpty()) {
            return invalidMappings;
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
            Integer sourceIndex = getIndex(sources, cmd[0]);
            Integer targetIndex = getIndex(targets, cmd[2]);
            if (sourceIndex == null || targetIndex == null) {
                // mapping for no longer existing DataUnitDescription
                invalidMappings.add(item);
            }
        }
        if (!invalidMappings.isEmpty()) {
            List<MutablePair<List<Integer>, Integer>> validMappings = decompile(script, sources, targets);
            edge.setScript(compile(validMappings, sources, targets));
        }
        return invalidMappings;
    }

    /**
     * Constructs default mapping if source DPU has exactly one output data unit
     * and target DPU also has exactly one input data unit. Any previous mapping
     * will be lost.
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
     *
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

    public String checkMandatoryInputs(PipelineGraph graph, DPUExplorer explorer) {
        String report = "";
        for (Node node : graph.getNodes()) {
            DPUInstanceRecord dpu = node.getDpuInstance();
            List<Edge> edges = null;
            List<DataUnitDescription> inputs = explorer.getInputs(dpu);
            if (!inputs.isEmpty()) {
                edges = graph.getEdgesTo(node);
            }
            for (DataUnitDescription input : inputs) {
                boolean found = false;
                if (input.getOptional()) {
                    continue;
                }
                for (Edge e : edges) {
                    if (e.getScript().contains("-> " + input.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    report += String.format("\nDPU: %s, Input: %s", dpu.getName(), input.getName());
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
