/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package cz.cuni.mff.xrg.odcs.commons.app;


import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.*;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ImportTest {

    @Test
    public void ExportTest() throws IOException, ImportException, ExportException, URISyntaxException {
        ImportService importService = new ImportService();
        Path tmpPath = Files.createTempDirectory("dir");
        File tmpDir = tmpPath.toFile();

        URL zipResource = ImportTest.class.getResource(
                "/pipeline.zip");

        File zipFile = new File(zipResource.toURI());
        ZipCommons.unpack(zipFile, tmpDir);
        Pipeline pipeline = importService.loadPipeline(tmpDir);
        ExportService exportService = new ExportService();
        ExportService exportServiceMock = spy(exportService);
        TreeSet<DpuItem> usedDpus = exportService.getDpusInformation(pipeline);
        Assert.assertEquals(usedDpus.size(), 2);

        File tmpTarget = File.createTempFile("temp", ".tmp");
        ExportSetting setting = new ExportSetting(false, false, false);
        AuthenticationContext authMock = mock(AuthenticationContext.class);
        User userMock = new User();
        when(authMock.getUser()).thenReturn(userMock);
        exportServiceMock.exportPipeline(pipeline, tmpTarget, setting, authMock);


    }

    @Test
    public void ImportTest() throws IOException, ImportException, ExportException, URISyntaxException {
        ImportService importService = new ImportService();
        Path tmpPath = Files.createTempDirectory("dir");
        File tmpDir = tmpPath.toFile();

        URL zipResource = ImportTest.class.getResource(
                "/pipelineWithUsedDpus.zip");

        File zipFile = new File(zipResource.toURI());
        ZipCommons.unpack(zipFile, tmpDir);
        List<DpuItem> result = importService.loadUsedDpus(tmpDir);
        Assert.assertEquals(result.size(), 2);
    }

}

