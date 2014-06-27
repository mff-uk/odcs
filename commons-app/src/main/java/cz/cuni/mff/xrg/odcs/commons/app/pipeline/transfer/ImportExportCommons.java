package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ImportExportCommons {
    private static final Logger LOG = LoggerFactory.getLogger(ImportExportCommons.class);

    public static String  uniteSeparator = "/";

    public static  TreeSet<ExportedDpuItem> getDpusInformation(Pipeline pipeline) {
        LOG.debug(">>> Entering getDpusInformation(pipeline={})", pipeline.getId());

        TreeSet<ExportedDpuItem> dpusInformation = new TreeSet<>();
        if (pipeline != null) {
            PipelineGraph graph = pipeline.getGraph();
            if (graph != null) {
                Set<Node> nodes = graph.getNodes();
                if (nodes != null) {
                    for (Node node : nodes) {
                        DPUInstanceRecord dpu = node.getDpuInstance();
                        if (dpu == null)
                            continue;

                        DPUTemplateRecord template = dpu.getTemplate();
                        String instanceName = dpu.getName();

                        if (template == null)
                            continue;

                        String jarName = template.getJarName();
                        String version = "unknown";
                        ExportedDpuItem exportedDpuItem = new ExportedDpuItem(instanceName, jarName, version);
                        if (!dpusInformation.contains(exportedDpuItem)) {
                            dpusInformation.add(exportedDpuItem);
                        }
                    }
                }
            }
        }

        LOG.debug("<<< Leaving getDpusInformation: {}", dpusInformation);
        return dpusInformation;
    }
    
    
    
}
