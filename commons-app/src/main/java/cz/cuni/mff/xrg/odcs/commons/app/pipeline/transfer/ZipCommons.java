package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipCommons {
    private static final Logger LOG = LoggerFactory.getLogger(ZipCommons.class);

    public static String  uniteSeparator = "/";


    /**
     * Unzip given zip file into given directory.
     *
     * @param sourceZip
     * @param targetDir
     */
    @Deprecated
    public static void unpack(File sourceZip, File targetDir) throws ImportException {
        LOG.debug(">>> Entering unpack(sourceZip,targetDir={})", sourceZip,targetDir );

        byte[] buffer = new byte[4096];
        targetDir.mkdirs();

        try (ZipInputStream zipInput = new ZipInputStream(new FileInputStream(
                sourceZip))) {
            ZipEntry zipEntry = zipInput.getNextEntry();
            while (zipEntry != null) {
                final String fileName = zipEntry.getName();
                final File newFile = new File(targetDir, fileName);
                // prepare sub dirs
                newFile.getParentFile().mkdirs();
                // copy file
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zipInput.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                }
                // move to next
                zipEntry = zipInput.getNextEntry();
            }
        } catch (FileNotFoundException ex) {
            throw new ImportException("Wrong uploaded file.", ex);
        } catch (IOException ex) {
            throw new ImportException("Failed to unzip given zip file.", ex);
        }
        LOG.debug("<<< Leaving unpack");
    }
    
    public static void unpack2(File sourceZip, File targetDir) throws IOException {
        LOG.debug(">>> Entering unpack2(sourceZip,targetDir={})", sourceZip, targetDir);

        ZipFile zipFile = new ZipFile(sourceZip);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File entryDestination = new File(targetDir, entry.getName());
            entryDestination.getParentFile().mkdirs();
            if (entry.isDirectory())
                entryDestination.mkdirs();
            else {
                InputStream in = zipFile.getInputStream(entry);
                OutputStream out = new FileOutputStream(entryDestination);
                IOUtils.copy(in, out);
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }

        LOG.debug("<<< Leaving unpack");
    }

}
