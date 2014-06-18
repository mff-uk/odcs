package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.TreeMap;

public class ImportExportCommons {
    private static final Logger LOG = LoggerFactory.getLogger(ImportExportCommons.class);

    public static TreeMap<String, String> getDpusInformation(Pipeline pipeline) {
        LOG.debug(">>> Entering getDpusInformation(pipeline={})", pipeline);

        TreeMap<String, String> informationMap = new TreeMap<>();
        if (pipeline != null) {
            PipelineGraph graph = pipeline.getGraph();
            if (graph != null) {
                for (Node node : graph.getNodes()) {
                    DPUInstanceRecord dpu = node.getDpuInstance();
                    if (dpu == null)
                        continue;

                    DPUTemplateRecord template = dpu.getTemplate();
                    String instanceName = dpu.getName();

                    if (template == null)
                        continue;
                    String name = template.getJarName();

                    if (!informationMap.containsKey(instanceName)) {
                        informationMap.put(instanceName, name);
                    }
                }
            }
        }

        LOG.debug("<<< Leaving getDpusInformation: {}", informationMap);
        return informationMap;
    }
    
    
    
}
