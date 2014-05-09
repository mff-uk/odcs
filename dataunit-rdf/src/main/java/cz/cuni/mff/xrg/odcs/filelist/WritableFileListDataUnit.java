package cz.cuni.mff.xrg.odcs.filelist;

public interface WritableFileListDataUnit extends FileListDataUnit {
    /**
     * Get base path where all new files should be written (only when used as output data unit). On input data unit does not have any sense.
     * Input data unit is only list of files, you can not create any new files anywhere.
     */
    String getBasePath();

    /**
     * Adds an existing file with supplied symbolic name to the data unit.
     * The symbolic name must be unique in scope of this data unit.
     * The file must be located under the getBasePath(). It is not allowed to create new files in different locations.
     */
    void addExistingFile(String proposedSymbolicName, String existingFileFullPath);

    /**
     * Generates unique new filename under the getBasePath().
     * Returns the newly generated full file path to work with.
     * It does not create the file on the disk nor it adds the file into dataunit.
     * Typical usage:
     * String htmlFileOutFilename = outputFileDataUnit.createFilename();
     * try {
     * new HTMLWriter(new File(htmlFileOutFilename)).dumpMyData(data);
     * outputFileDataUnit.addExistingFile("htmlOutput.html", htmlFileOutFilename);
     * } catch (HTMLWriterException ex) {
     * // File was not created by writer - it also naturally does not appear inside data unit
     * // inform user, log message
     * }
     */
    String createFilename();

    /**
     * Same as above, but dataunit should try to create the name of newly created file to be
     * similar to proposedSymbolicName (but still unique and filesystem-safe). For better debugging
     * when browsing files on disk.
     */
    String createFilename(String proposedSymbolicName);
}
