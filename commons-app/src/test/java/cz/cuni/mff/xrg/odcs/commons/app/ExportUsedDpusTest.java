package cz.cuni.mff.xrg.odcs.commons.app;


import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ExportException;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ExportService;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ExportSetting;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ExportedDpuItem;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ImportException;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ImportExportCommons;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ImportService;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


public class ExportUsedDpusTest {
    @Test
    public void Test() throws IOException, ImportException, ExportException {
        ImportService importService = new ImportService();
        Path tmpPath = Files.createTempDirectory("dir");
        File tmpDir = tmpPath.toFile();

        String zipResource = ExportUsedDpusTest.class.getResource(
                "/pipeline.zip").getPath();

        File zipFile = new File(zipResource);
        importService.unpack(zipFile, tmpDir);
        Pipeline pipeline = importService.loadPipeline(tmpDir);
        for (Node node : pipeline.getGraph().getNodes()) {
            final DPUInstanceRecord dpu = node.getDpuInstance();
            String dpuName = dpu.getName();
            DPUTemplateRecord template = dpu.getTemplate();
            String jarName = template.getJarName();
            String version = "unknown";
            ExportedDpuItem exportedDpuItem = new ExportedDpuItem(dpuName, jarName, version);
        }
        TreeSet<ExportedDpuItem> usedDpus = ImportExportCommons.getDpusInformation(pipeline);
        System.out.println("===================================================================");
        for (ExportedDpuItem dpuItem : usedDpus) {
            System.out.println(dpuItem);
        }


        ExportService exportService = new ExportService();
        ExportService exportServiceMock = spy(exportService);

        File tmpTarget = File.createTempFile("temp", ".tmp");

        ExportSetting setting = new ExportSetting(false, false, false);
        AuthenticationContext authMock = mock(AuthenticationContext.class);
        User userMock = new User();
        when(authMock.getUser()).thenReturn(userMock);
        exportServiceMock.exportPipeline(pipeline, tmpTarget, setting, authMock);

    }

}

