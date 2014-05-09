package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.thoughtworks.xstream.XStream;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ScheduleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.xstream.JPAXStream;
import cz.cuni.mff.xrg.odcs.commons.app.resource.MissingResourceException;
import cz.cuni.mff.xrg.odcs.commons.app.resource.ResourceManager;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;

/**
 * Export given pipeline into file.
 * 
 * @author Škoda Petr
 */
public class ExportService {

    private static final Logger LOG = LoggerFactory.getLogger(
            ExportService.class);

    @Autowired
    private ScheduleFacade scheduleFacade;

    @Autowired
    private ResourceManager resourceManager;

    @Autowired(required = false)
    private AuthenticationContext authCtx;

    /**
     * Create a temp file and exportPipeline pipeline into it.
     * 
     * @param pipeline
     * @param setting
     * @return File with exportPipelineed pipeline.
     * @throws ExportException
     */
    public File exportPipeline(Pipeline pipeline, ExportSetting setting) throws ExportException {
        final File tempDir;
        try {
            tempDir = resourceManager.getNewExportTempDir();
        } catch (MissingResourceException ex) {
            throw new ExportException("Failed to get temp directory.", ex);
        }
        final StringBuilder fileName = new StringBuilder();
        fileName.append("pipeline-");
        fileName.append(pipeline.getId().toString());
        fileName.append(".zip");

        final File targetFile = new File(tempDir, fileName.toString());
        exportPipeline(pipeline, targetFile, setting);
        return targetFile;
    }

    /**
     * Export given pipeline and all it's dependencies into given file.
     * 
     * @param pipeline
     * @param targetFile
     * @param setting
     * @throws ExportException
     */
    public void exportPipeline(Pipeline pipeline, File targetFile, ExportSetting setting)
            throws ExportException {

        if (authCtx == null) {
            throw new ExportException("AuthenticationContext is null.");
        }
        final User user = authCtx.getUser();
        if (user == null) {
            throw new ExportException("Unknown user.");
        }

        try (FileOutputStream fos = new FileOutputStream(targetFile);
                ZipOutputStream zipStream = new ZipOutputStream(fos)) {
            // save information about pipeline and schedule
            savePipeline(pipeline, zipStream);
            saveSchedule(pipeline, zipStream);
            // save jar and dpu files
            HashSet<Long> savedTemplateId = new HashSet<>();
            for (Node node : pipeline.getGraph().getNodes()) {
                final DPUInstanceRecord dpu = node.getDpuInstance();
                final DPUTemplateRecord template = dpu.getTemplate();
                if (savedTemplateId.contains(template.getId())) {
                    // already saved				
                } else {
                    savedTemplateId.add(template.getId());
                    // export jar file
                    saveDPUJar(template, zipStream);
                    // copy data
                    if (setting.isExportDPUUserData()) {
                        saveDPUDataUser(template, user, zipStream);
                        saveDPUDataGlobal(template, zipStream);
                    }
                }
            }
        } catch (IOException ex) {
            targetFile.delete();
            throw new ExportException(
                    "Failed to prepare file with exported pipeline", ex);
        } catch (ExportException ex) {
            targetFile.delete();
            throw ex;
        }
    }

    /**
     * Serialise pipeline into zip stream.
     * 
     * @param pipeline
     * @param zipStream
     * @throws ExportException
     */
    private void savePipeline(Pipeline pipeline, ZipOutputStream zipStream)
            throws ExportException {
        final XStream xStream = JPAXStream.createForPipeline();
        try {
            final ZipEntry ze = new ZipEntry(ArchiveStructure.PIPELINE.getValue());
            zipStream.putNextEntry(ze);
            // write into entry
            xStream.toXML(pipeline, zipStream);
        } catch (IOException ex) {
            throw new ExportException("Failed to serialize pipeline.", ex);
        }
    }

    /**
     * Serialise all schedule that are visible to current used into given zip
     * stream.
     * 
     * @param pipeline
     * @param zipStream
     * @throws ExportException
     */
    private void saveSchedule(Pipeline pipeline, ZipOutputStream zipStream)
            throws ExportException {
        final XStream xStream = JPAXStream.createForSchedule();
        final List<Schedule> schedules = scheduleFacade
                .getSchedulesFor(pipeline);
        try {
            final ZipEntry ze = new ZipEntry(ArchiveStructure.SCHEDULE.getValue());
            zipStream.putNextEntry(ze);
            // write into entry
            xStream.toXML(schedules, zipStream);
        } catch (IOException ex) {
            throw new ExportException("Failed to serialize schedule.", ex);
        }
    }

    /**
     * Save jar file for given DPU into subdirectory in given directory.
     * 
     * @param template
     * @param zipStream
     * @throws ExportException
     */
    private void saveDPUJar(DPUTemplateRecord template,
            ZipOutputStream zipStream)
            throws ExportException {
        // we copy the structure in dpu directory
        final File source;
        try {
            source = resourceManager.getDPUJarFile(template);
        } catch (MissingResourceException ex) {
            throw new ExportException("Failed to get path to jar file.");
        }
        byte[] buffer = new byte[4096];
        try {
            final ZipEntry ze = new ZipEntry(ArchiveStructure.DPU_JAR.getValue() +
                    File.separator + template.getJarPath());
            zipStream.putNextEntry(ze);
            // move jar file into the zip file
            try (FileInputStream in = new FileInputStream(source)) {
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zipStream.write(buffer, 0, len);
                }
            }
        } catch (IOException ex) {
            throw new ExportException("Failed to copy jar file.", ex);
        }
    }

    /**
     * Export DPU's user-based data into given zip stream.
     * 
     * @param template
     * @param user
     * @param zipStream
     * @throws ExportException
     */
    private void saveDPUDataUser(DPUTemplateRecord template, User user,
            ZipOutputStream zipStream) throws ExportException {
        final File source;
        try {
            source = resourceManager.getDPUDataUserDir(template, user);
        } catch (MissingResourceException ex) {
            throw new ExportException("Failed to get path to jar file.");
        }

        final String zipPrefix = ArchiveStructure.DPU_DATA_USER.getValue()
                + File.separator + template.getJarDirectory();

        saveDirectory(source, zipPrefix, zipStream);
    }

    /**
     * Export DPU's global data into given zip stream.
     * 
     * @param template
     * @param zipStream
     * @throws ExportException
     */
    private void saveDPUDataGlobal(DPUTemplateRecord template,
            ZipOutputStream zipStream) throws ExportException {
        final File source;
        try {
            source = resourceManager.getDPUDataGlobalDir(template);
        } catch (MissingResourceException ex) {
            throw new ExportException("Failed to get path to jar file.");
        }

        final String zipPrefix = ArchiveStructure.DPU_DATA_GLOBAL.getValue()
                + File.separator + template.getJarDirectory();

        saveDirectory(source, zipPrefix, zipStream);
    }

    /**
     * Add files and directories from given directory into a zip. Relative path
     * from the given directory is used to identify the relative path in zip.
     * 
     * @param source
     * @param targetPrefix
     *            Path prefix in output zip, it should not end with
     *            separator.
     * @param zipStream
     * @throws ExportException
     */
    private void saveDirectory(File source, String targetPrefix,
            ZipOutputStream zipStream) throws ExportException {
        if (!source.exists()) {
            // nothing to exportPipeline
            LOG.trace("Skipping '{}' as it does not exist.", source.toString());
            return;
        }
        LOG.trace("Copy '{}' under '{}'.", source.toString(), targetPrefix);

        // no we add files into the 
        byte[] buffer = new byte[4096];
        final int sourceLenght;
        try {
            sourceLenght = source.getCanonicalPath().length() + 1;
        } catch (IOException ex) {
            throw new ExportException("Failed to get canonical path.", ex);
        }

        final Collection<File> files = FileUtils.listFiles(source,
                TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

        for (File file : files) {
            if (!file.isFile()) {
                // not a file -> skip
                continue;
            }
            try {
                // prepare relative path in archive
                final String relativePath = targetPrefix + File.separator
                        + file.getCanonicalPath().substring(sourceLenght);
                // ...
                final ZipEntry ze = new ZipEntry(relativePath);
                zipStream.putNextEntry(ze);
            } catch (IOException ex) {
                throw new ExportException("Preparation of zip entry failed",
                        ex);
            }
            // transfer data
            try (FileInputStream in = new FileInputStream(file)) {
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zipStream.write(buffer, 0, len);
                }
            } catch (IOException ex) {
                throw new ExportException("Failed to add file into archive.",
                        ex);
            }
        }
    }

}
