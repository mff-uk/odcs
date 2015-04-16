package eu.unifiedviews.master.api;

import cz.cuni.mff.xrg.odcs.commons.app.auth.ShareType;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.i18n.Messages;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUCreateException;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUModuleManipulator;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUReplaceException;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import eu.unifiedviews.master.authentication.AuthenticationRequired;
import eu.unifiedviews.master.model.ApiException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

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
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Path("/import")
@AuthenticationRequired
public class DPUResource {

    @Autowired
    private DPUFacade dpuFacade;

    @Autowired
    private PipelineFacade pipelineFacade;

    @Autowired
    private DPUModuleManipulator dpuManipulator;

    private static final Logger LOG = LoggerFactory.getLogger(DPUResource.class);

    @POST
    @Path("/dpu/jar")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public @ResponseBody String importJarDpu(@FormDataParam("file") InputStream inputStream, @FormDataParam("file") FormDataContentDisposition contentDispositionHeader, @QueryParam("name") String dpuName, @QueryParam("description") String dpuDescription, @QueryParam("visibility") String visibility, @QueryParam("force") boolean force) {
        // parse input steam to file, located in temporary directory
        File jarFile = inputStreamToFile(inputStream, contentDispositionHeader.getFileName());
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

        // check if DPU already exists in UV
        String dpuDirName = getDirectoryName(jarFile.getName());
        if(dpuDirName == null) {
            throw new ApiException(Response.Status.BAD_REQUEST, "Cannot process DPU name!");
        }

        DPUTemplateRecord dpuTemplate = dpuFacade.getByDirectory(dpuDirName);
        if(dpuTemplate != null) { // if DPU already exists
            if(force) {
                dpuManipulator.delete(dpuTemplate);
            } else {
                throw new ApiException(Response.Status.BAD_REQUEST, "DPU already exists!");
            }
        }

        // actual import of DPU
        createDpu(dpuName, dpuDescription, shareType, jarFile);

        // now we can delete the file
        deleteTempFile(jarFile);
        return "OK";
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
     * Read input stream fo a file.
     *
     * Method creates a new temp directory and a new file in it. Content of input stream is copyied to the new file.
     *
     * File, and temp directory are marked for deletion at application stop.
     *
     * @param inputStream Input stream
     * @param filename filename of a new file.
     * @return new file.
     */
    private File inputStreamToFile(InputStream inputStream, String filename) {
        File file = null;
        try {
            java.nio.file.Path tempDir = Files.createTempDirectory(String.valueOf(inputStream.hashCode()));
            tempDir.toFile().deleteOnExit();
            file = new File(tempDir.toFile(), filename);
            file.deleteOnExit();
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            LOG.error("Exception at copying file input stream to temporary file", e);
        }
        return file;
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
