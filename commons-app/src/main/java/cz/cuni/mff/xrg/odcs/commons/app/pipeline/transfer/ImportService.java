/**
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
 */
package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.auth.EntityPermissions;
import cz.cuni.mff.xrg.odcs.commons.app.auth.PermissionUtils;
import cz.cuni.mff.xrg.odcs.commons.app.auth.ShareType;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ScheduleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.i18n.Messages;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUJarNameFormatException;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUJarUtils;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUModuleManipulator;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.xstream.JPAXStream;
import cz.cuni.mff.xrg.odcs.commons.app.resource.MissingResourceException;
import cz.cuni.mff.xrg.odcs.commons.app.resource.ResourceManager;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import cz.cuni.mff.xrg.odcs.commons.app.user.UserActor;

/**
 * Service for importing pipelines exported by {@link ExportService}.
 *
 * @author Škoda Petr
 */
public class ImportService {

    private static final Logger LOG = LoggerFactory.getLogger(
            ImportService.class);

    @Autowired
    private ScheduleFacade scheduleFacade;

    @Autowired
    private ResourceManager resourceManager;

    @Autowired
    private DPUFacade dpuFacade;

    @Autowired
    private PipelineFacade pipelineFacade;

    @Autowired(required = false)
    private AuthenticationContext authCtx;

    @Autowired(required = false)
    private PermissionUtils permissionUtils;

    @Autowired
    private DPUModuleManipulator moduleManipulator;

    @Autowired
    private AppConfig appConfig;

    @PreAuthorize("hasRole('pipeline.import') and hasRole('pipeline.create')")
    public Pipeline importPipeline(File zipFile, boolean importUserDataFile, boolean importScheduleFile)
            throws ImportException, IOException {
        final File tempDir;
        try {
            tempDir = resourceManager.getNewImportTempDir();
        } catch (MissingResourceException ex) {
            throw new ImportException(Messages.getString("ImportService.pipeline.temp.dir.fail"), ex);
        }
        return importPipeline(zipFile, tempDir, importUserDataFile, importScheduleFile);
    }

    @PreAuthorize("hasRole('pipeline.import') and hasRole('pipeline.create')")
    public Pipeline importPipeline(File zipFile, File tempDirectory, boolean importUserDataFile, boolean importScheduleFile)
            throws ImportException, IOException {
        // delete tempDirectory
        ResourceManager.cleanupQuietly(tempDirectory);

        if (authCtx == null) {
            throw new ImportException(Messages.getString("ImportService.pipeline.authenticationContext.null"));
        }
        final User user = authCtx.getUser();
        if (user == null) {
            throw new ImportException(Messages.getString("ImportService.pipeline.unknown.user"));
        }
        final UserActor actor = this.authCtx.getUser().getUserActor();
        // unpack
        Pipeline pipe;
        try {
            ZipCommons.unpack(zipFile, tempDirectory);

            pipe = loadPipeline(tempDirectory);
            pipe.setUser(user);
            pipe.setActor(actor);
            pipe.setShareType(ShareType.PRIVATE);

            Map<DPUTemplateRecord, DPUTemplateRecord> importedTemplates = new HashMap<>();
            for (Node node : pipe.getGraph().getNodes()) {
                final DPUInstanceRecord dpu = node.getDpuInstance();
                final DPUTemplateRecord template = dpu.getTemplate();
                final DPUTemplateRecord templateToUse;
                if (importedTemplates.containsKey(template)) {
                    // already imported
                    templateToUse = importedTemplates.get(template);
                } else {
                    // prepare data for import
                    final File jarFile = new File(tempDirectory,
                            ArchiveStructure.DPU_JAR.getValue() + File.separator
                                    + template.getJarPath());
                    final File userDataFile = new File(tempDirectory,
                            ArchiveStructure.DPU_DATA_USER.getValue() + File.separator
                                    + template.getJarDirectory());
                    final File globalDataFile = new File(tempDirectory,
                            ArchiveStructure.DPU_DATA_GLOBAL.getValue() + File.separator
                                    + template.getJarDirectory());
                    // import
                    templateToUse = findDPUTemplate(dpu, template, user, jarFile,
                            userDataFile, globalDataFile, importUserDataFile);
                    // add to cache
                    importedTemplates.put(template, templateToUse);
                }
                // set DPU instance
                dpu.setTemplate(templateToUse);
            }
            // save pipeline
            pipelineFacade.save(pipe);
            // add schedules
            final File scheduleFile = new File(tempDirectory,
                    ArchiveStructure.SCHEDULE.getValue());
            if (scheduleFile.exists() && importScheduleFile) {
                importSchedules(scheduleFile, pipe, user);
            }

        } catch (ImportException ex) {
            throw ex;
        } finally {
            ResourceManager.cleanupQuietly(tempDirectory);
        }
        return pipe;
    }

    /**
     * @param baseDir
     * @return
     * @throws ImportException
     */
    @PreAuthorize("hasRole('pipeline.import')")
    public static Pipeline loadPipeline(File baseDir) throws ImportException {
        final XStream xStream = JPAXStream.createForPipeline(new DomDriver("UTF-8"));
        final File sourceFile = new File(baseDir, ArchiveStructure.PIPELINE
                .getValue());
        try {
            return (Pipeline) xStream.fromXML(sourceFile);
        } catch (Throwable t) {
            String msg = Messages.getString("ImportService.pipeline.xml.load.file.fail");
            LOG.error(msg);
            throw new ImportException(msg, t);
        }
    }

    @PreAuthorize("hasRole('pipeline.import')")
    public List<DpuItem> loadUsedDpus(File baseDir) throws ImportException {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("dpus", List.class);
        xStream.alias("dpu", DpuItem.class);

        final File sourceFile = new File(baseDir, ArchiveStructure.USED_DPUS
                .getValue());
        if (!sourceFile.exists()) {
            LOG.warn("file: {} is not exist", sourceFile.getName());
            return null;
        }
        try {
            @SuppressWarnings("unchecked")
            List<DpuItem> result = (List<DpuItem>) xStream.fromXML(sourceFile);
            return result;
        } catch (Throwable t) {
            String msg = Messages.getString("ImportService.pipeline.dpu.file.wrong");
            LOG.error(msg);
            throw new ImportException(msg, t);
        }
    }

    /**
     * Check if given template exist (compare for jar directory and jar name).
     * If not then import new DPU template into system. In both cases the user
     * and global DPU's data are copied into respective directories.
     *
     * @param dpu 
     * @param template
     * @param user
     * @param jarFile
     * @param userDataDir
     * @param globalDataDir
     * @return Template that is stored in database and is equivalent to the
     *         given one.
     * @throws ImportException
     */
    private DPUTemplateRecord findDPUTemplate(DPUInstanceRecord dpu, DPUTemplateRecord template,
            User user, File jarFile, File userDataDir, File globalDataDir, boolean importUserDataFile)
                    throws ImportException {
        
        String dpuDir;
        try {
            dpuDir = DPUJarUtils.parseNameFromJarName(jarFile.getName());
        } catch (DPUJarNameFormatException e) {
            throw new ImportException(Messages.getString("ImportService.pipeline.dpu.import.fail"), e);
        }
        
        DPUTemplateRecord parentDpu = dpuFacade.getByDirectory(dpuDir);
        DPUTemplateRecord matchingNameDpu = dpuFacade.getByDirectoryAndName(dpuDir, template.getName());
        DPUTemplateRecord result = null;
        
        if (parentDpu == null) {
            throw new ImportException(Messages.getString("ImportService.pipeline.dpu.import.fail.not.found",
                                                         template.getJarDirectory(),
                                                         jarFile.getName()));
        } else {
            
            try {
                int compareVersion = moduleManipulator.compareVersions(parentDpu.getJarName(), jarFile.getName());
                
                if (compareVersion < 0) {
                    throw new ImportException(Messages.getString("ImportService.pipeline.dpu.import.fail.version",
                                                                jarFile.getName()));
                }
            } catch (DPUJarNameFormatException e) {
                throw new ImportException(e.getMessage(), e);
            }
        }
        
        if (dpu.isUseTemplateConfig()) {
            
            if (matchingNameDpu == null) {
                throw new ImportException(Messages.getString("ImportService.pipeline.dpu.import.fail.not.found",
                                                             template.getName(),
                                                             jarFile.getName()));
            } else if (haveTheSameConfig(matchingNameDpu, template)) {
                checkPersmissions(matchingNameDpu, user);
                result = matchingNameDpu;
            } else {
                throw new ImportException(Messages.getString("ImportService.pipeline.dpu.import.fail.different.config",
                                                            template.getName(),
                                                            jarFile.getName()));
            }
        } else {
            checkPersmissions(parentDpu, user);
            result = parentDpu;
        }
        
        // copy user data
        if (userDataDir.exists() && importUserDataFile) {
            if (!hasUserPermission(EntityPermissions.PIPELINE_IMPORT_USER_DATA)) {
                throw new ImportException(Messages.getString("ImportService.pipeline.dpu.import.data.permissions"));
            }
            try {
                final File dest = resourceManager
                        .getDPUDataUserDir(result, user);
                FileUtils.copyDirectory(userDataDir, dest);
            } catch (MissingResourceException ex) {
                throw new ImportException(Messages.getString("ImportService.pipeline.missing.resource"), ex);
            } catch (IOException ex) {
                throw new ImportException(Messages.getString("ImportService.pipeline.userData.copy.fail"), ex);
            }
        }

        // copy global data
        if (globalDataDir.exists()) {
            try {
                final File dest = resourceManager.getDPUDataGlobalDir(result);
                FileUtils.copyDirectory(globalDataDir, dest);
            } catch (MissingResourceException ex) {
                throw new ImportException(Messages.getString("ImportService.pipeline.missing.resource"), ex);
            } catch (IOException ex) {
                throw new ImportException(Messages.getString("ImportService.pipeline.globalData.copy.fail"), ex);
            }
        }

        return result;
    }

    private void checkPersmissions(DPUTemplateRecord dpuTemplate, User user) throws ImportException {
        // check visibility
        if (dpuTemplate.getShareType() == ShareType.PRIVATE && !dpuTemplate.getOwner().equals(user)) {
            throw new ImportException(Messages.getString("ImportService.pipeline.import.dpu.fail.permission"));
        }
    }

    private boolean haveTheSameConfig(DPUTemplateRecord matchingNameDpu, DPUTemplateRecord template) {
        if (matchingNameDpu.getRawConf() == null
                || !matchingNameDpu.getRawConf().equals(template.getRawConf())) {
            return false;
        }
        return true;
    }

    /**
     * Load schedules from given file. The given use and pipeline is set to them
     * and then they are imported into system.
     *
     * @param scheduleFile
     *            File with schedules to load.
     * @param pipeline
     * @param user
     * @throws ImportException
     */
    @SuppressWarnings("unchecked")
    @PreAuthorize("hasRole('pipeline.importScheduleRules')")
    private void importSchedules(File scheduleFile, Pipeline pipeline, User user)
            throws ImportException {
        final XStream xStream = JPAXStream.createForPipeline(new DomDriver("UTF-8"));
        final List<Schedule> schedules;
        try {
            schedules = (List<Schedule>) xStream.fromXML(scheduleFile);
        } catch (Throwable t) {
            throw new ImportException(Messages.getString("ImportService.pipeline.schedule.deserialization.fail"), t);
        }

        for (Schedule schedule : schedules) {
            // bind
            schedule.setPipeline(pipeline);
            schedule.setOwner(user);
            // save into database
            scheduleFacade.save(schedule);
        }
    }

    public ImportedFileInformation getImportedInformation(File zipFile)
            throws ImportException, MissingResourceException, IOException {
        LOG.debug(">>> Entering getImportedInformation(zipFile={})", zipFile);

        boolean isUserData = false;
        boolean isScheduleFile = false;

        File tempDirectory = resourceManager.getNewImportTempDir();
        try {
            ZipCommons.unpack(zipFile, tempDirectory);
            Pipeline pipeline = loadPipeline(tempDirectory);
            
            List<DpuItem> usedDpus = loadUsedDpus(tempDirectory);
            Map<String, DpuItem> missingDpus = new TreeMap<>();
            Map<String, DpuItem> oldDpus = new TreeMap<>();
            
            if (pipeline != null) {
                PipelineGraph graph = pipeline.getGraph();
                if (graph != null) {
                    Set<Node> nodes = graph.getNodes();
                    if (nodes != null) {
                        for (Node node : nodes) {
                            DPUInstanceRecord dpu = node.getDpuInstance();
                            if (dpu == null) {
                                continue;
                            }
                            
                            DPUTemplateRecord template = dpu.getTemplate();
                            
                            if (template == null) {
                                continue;
                            }
                            
                            String version = "unknown";
                            String jarDir = null;
                            
                            try {
                                jarDir = DPUJarUtils.parseNameFromJarName(template.getJarName());
                                version = DPUJarUtils.parseVersionStringFromJarName(template.getJarName());
                            } catch (DPUJarNameFormatException e) {
                                throw new ImportException(e.getMessage(), e);
                            }
                            
                            DpuItem dpuItem = new DpuItem(dpu.getName(), template.getJarName(), version);
                            DPUTemplateRecord parentDpuTemplate = dpuFacade.getByDirectory(jarDir);
                            
                            
                            try {
                                // checking version
                                if (parentDpuTemplate != null 
                                        && moduleManipulator.compareVersions(parentDpuTemplate.getJarName(), template.getJarName()) < 0) {
                                    
                                    missingDpus.put(jarDir, dpuItem);
                                    oldDpus.put(jarDir, dpuItem);
                                } else {
                                    if (dpu.isUseTemplateConfig()) {
                                        DPUTemplateRecord dpuTemplateRecord = dpuFacade.getByDirectoryAndName(jarDir, template.getName());
                                        
                                        if (dpuTemplateRecord == null
                                                || dpuTemplateRecord.getRawConf() == null
                                                || !dpuTemplateRecord.getRawConf().equals(template.getRawConf())) {
                                            
                                            missingDpus.put(template.getName(), dpuItem);
                                        }
                                    } else if (parentDpuTemplate == null) {
                                        dpuItem.setDpuName(jarDir);
                                        missingDpus.put(jarDir, dpuItem);
                                    }
                                }
                            } catch (DPUJarNameFormatException e) {
                                // this DPU will fail to import ...
                                throw new ImportException(e.getMessage(), e);
                            }
                            
                            final File userDataFile = new File(tempDirectory,
                                    ArchiveStructure.DPU_DATA_USER.getValue() + File.separator
                                    + template.getJarDirectory());
                            
                            if (userDataFile.exists()) {
                                isUserData = true;
                                
                            }
                            LOG.debug("userDataFile: " + userDataFile.toString());
                        }
                        
                        final File scheduleFile = new File(tempDirectory,
                                ArchiveStructure.SCHEDULE.getValue());
                        if (scheduleFile.exists()) {
                            isScheduleFile = true;
                        }
                    }
                }
            }
            
            ImportedFileInformation result = new ImportedFileInformation(usedDpus,
                    missingDpus, isUserData, isScheduleFile, oldDpus);
            
            LOG.debug("<<< Leaving getImportedInformation: {}", result);
            return result;
        } finally {
            ResourceManager.cleanupQuietly(tempDirectory);
        }
    }

    public boolean hasUserPermission(String permission) {
        if (this.permissionUtils != null) {
            return this.permissionUtils.hasUserAuthority(permission);
        }
        return true;
    }

}
