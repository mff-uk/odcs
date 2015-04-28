package eu.unifiedviews.master.api;

import cz.cuni.mff.xrg.odcs.commons.app.auth.ShareType;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUCreateException;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUModuleManipulator;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUReplaceException;
import eu.unifiedviews.master.converter.ConvertUtils;
import eu.unifiedviews.master.authentication.AuthenticationRequired;
import eu.unifiedviews.master.model.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Path("/import")
@AuthenticationRequired
public class DPUResource {

    private static final Logger LOG = LoggerFactory.getLogger(DPUResource.class);

    @Autowired
    private DPUFacade dpuFacade;

    @Autowired
    private PipelineFacade pipelineFacade;

    @Autowired
    private DPUModuleManipulator dpuManipulator;

    @POST
    @Path("/dpu/jar")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String importJarDpu(@FormDataParam("file") InputStream inputStream, @FormDataParam("file") FormDataContentDisposition contentDispositionHeader, @QueryParam("name") String dpuName, @QueryParam("description") String dpuDescription, @QueryParam("visibility") String visibility, @QueryParam("force") boolean force) {
        LOG.debug("Importing dpu file: {}, name: {}, description: {}, visibility: {}, force?: {}", contentDispositionHeader.getFileName(), dpuName, dpuDescription, visibility, force);
        // parse input steam to file, located in temporary directory
        File jarFile;
        try {
            jarFile = ConvertUtils.inputStreamToFile(inputStream, contentDispositionHeader.getFileName());
        } catch (IOException e) {
            LOG.error("Exception at reading input stream", e);
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        // if dpu name is empty, set it to null
        if (StringUtils.isEmpty(dpuName)) {
            dpuName = null;
        }
        ShareType shareType;
        if (StringUtils.isEmpty(visibility)) { // if visibility is null, or empty, use default value
            shareType = ShareType.PUBLIC_RO;
        } else { // else parse value
            try {
                shareType = ShareType.valueOf(visibility);
            } catch (IllegalArgumentException e) {
                LOG.error("Exception at parsing share type", e);
                throw new ApiException(Response.Status.BAD_REQUEST, e.getMessage());
            }
        }

        String dpuDirName = getDirectoryName(jarFile.getName());
        if(dpuDirName == null) {
            LOG.error("Exception at processing dpu name.");
            throw new ApiException(Response.Status.BAD_REQUEST, "Cannot process DPU name!");
        }

        // check if DPU already exists in UV
        DPUTemplateRecord dpuTemplate = dpuFacade.getByDirectory(dpuDirName);
        if(dpuTemplate != null) {
            LOG.debug("DPU already exists!");
            if(force) {
                LOG.debug("Force flag detected. Deleting old DPU.");
                dpuManipulator.delete(dpuTemplate);
            } else {
                throw new ApiException(Response.Status.BAD_REQUEST, "DPU already exists!");
            }
        }
        LOG.debug("Creating DPU...");
        createDpu(dpuName, dpuDescription, shareType, jarFile);
        LOG.debug("DPU successfully imported.");
        // now we can delete the file
        deleteTempFile(jarFile);
        return "OK";
    }

    private void replaceDpu(DPUTemplateRecord dpuTemplate, File jarFile) {
        try {
            dpuManipulator.replace(dpuTemplate, jarFile);
        } catch (DPUReplaceException e) {
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void createDpu(String dpuName, String dpuDescription, ShareType shareType, File jarFile) {
        try {
            DPUTemplateRecord dpuTemplate = dpuManipulator.create(jarFile, dpuName);
            dpuTemplate.setDescription(dpuDescription);
            dpuTemplate.setShareType(shareType);
            dpuFacade.save(dpuTemplate);
        } catch (DPUCreateException e) {
            LOG.error("Exception at importing DPU", e);
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Delete temporary file created by webservice.
     *
     * Method deletes file and its parent folder.
     *
     * @param file File to delete.
     */
    private void deleteTempFile(File file) {
        File parent = file.getParentFile();
        file.delete();
        parent.delete();
    }

    private static String getDirectoryName(String sourceFileName) {
        // the name must be in format: NAME-.*.jar
        final Pattern pattern = Pattern
                .compile("(.+)-(\\d(\\.\\d+)+).*\\.jar");
        final Matcher matcher = pattern.matcher(sourceFileName);
        if (matcher.matches()) {
            // 0 - original, 1 - name, 2 - version
            return matcher.group(1);
        } else {
            return null;
        }
    }
}
