package cz.cuni.mff.xrg.odcs.files;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

public interface WritableFilesDataUnit extends FilesDataUnit {
    interface WritableFilesIteration extends FilesDataUnit.Iteration {
        public void remove() throws DataUnitException;
    }
    
    WritableFilesDataUnit.WritableFilesIteration getFiles() throws DataUnitException;
    
    /**
     * Get base path URI where all new files should be written (only when used as output data unit). On input data unit does not have any sense.
     * Input data unit is only list of files, you can not create any new files anywhere.
     * @return base path URI where all new files should be written
     */
    String getBasePath();

    /**
     * Adds an existing file with supplied symbolic name to the data unit.
     * The symbolic name must be unique in scope of this data unit.
     * The file should be located under the getBasePath(). It is not allowed to create new files in different locations.
     * @param proposedSymbolicName symbolic name under which the file will be stored (must be unique in scope of this data unit)
     * @param existingFileURI real file location, example: http://example.com/myFile.exe, file://c:/Users/example/docs/doc.doc
     * @throws DataUnitException
     */
    void addExistingFile(String proposedSymbolicName, String existingFileURI) throws DataUnitException;

    /**
     * Generates unique file under the getBasePath().
     * Returns the newly generated full file path URI to work with.
     * It does create the file on the disk, but it does not add the file into the dataunit.
     * Typical usage:
     * {@code
     * String htmlFileOutFilename = outputFileDataUnit.createFile();
     * new HTMLWriter(new File(htmlFileOutFilename)).dumpMyData(data);
     * outputFileDataUnit.addExistingFile("htmlOutput.html", htmlFileOutFilename);
     * }
     * @return URI of real location of the newly created file
     * @throws DataUnitException
     */
    String createFile() throws DataUnitException;

    /**
     * Same as {@link createFile}, but dataunit should try to create the name of newly created file to be
     * similar to proposedSymbolicName (but still unique and filesystem-safe). For better debugging
     * when browsing files on disk.
     * @return URI of real location of the newly created file
     * @throws DataUnitException
     */
    String createFile(String proposedSymbolicName) throws DataUnitException;
}
