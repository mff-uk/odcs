package cz.cuni.mff.xrg.odcs.extractor.file;

import org.openrdf.rio.RDFFormat;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

/**
 * File extractor configuration.
 * 
 * @author Petyr
 * @author Jiri Tomes
 */
public class FileExtractorConfig extends DPUConfigObjectBase {

    public void setPath(String path) {
        Path = path;
    }

    private String Path;

    private String FileSuffix;

    private RDFFormat RDFFormatValue;

    private FileExtractType fileExtractType;

    private boolean OnlyThisSuffix;

    private boolean UseStatisticalHandler;

    private boolean failWhenErrors;

    public void setFileSuffix(String fileSuffix) {
        FileSuffix = fileSuffix;
    }

    public void setRDFFormatValue(RDFFormat RDFFileFormat) {
        this.RDFFormatValue = RDFFileFormat;

    }

    public void setFileExtractType(FileExtractType fileExtractType) {
        this.fileExtractType = fileExtractType;
    }

    public void setFailWhenErrors(boolean failWhenErrors) {
        this.failWhenErrors = failWhenErrors;
    }

    public FileExtractorConfig() {
        this.Path = "";
        this.FileSuffix = "";
        this.RDFFormatValue = null;
        this.fileExtractType = FileExtractType.PATH_TO_FILE;
        this.OnlyThisSuffix = false;
        this.UseStatisticalHandler = true;
        this.failWhenErrors = false;
    }

    public FileExtractorConfig(String Path, String FileSuffix,
            RDFFormat RDFFormatValue, FileExtractType fileExtractType,
            boolean OnlyThisSuffix, boolean UseStatisticalHandler,
            boolean failWhenErrors) {

        this.Path = Path;
        this.FileSuffix = FileSuffix;
        this.RDFFormatValue = RDFFormatValue;
        this.fileExtractType = fileExtractType;
        this.OnlyThisSuffix = OnlyThisSuffix;
        this.UseStatisticalHandler = UseStatisticalHandler;
        this.failWhenErrors = failWhenErrors;
    }

    /**
     * Returns the path to file as string value.
     * 
     * @return the path to file as string value.
     */
    public String getPath() {
        return Path;
    }

    /**
     * Returns the value of file suffix, empty string if no suffix is defined.
     * 
     * @return the file suffix, empty string if no suffix is defined.
     */
    public String getFileSuffix() {
        return FileSuffix;
    }

    /**
     * Returns selected RDFFormatType for extracted RDF data.
     * 
     * @return selected link RDFFormatType for extracted RDF data.
     */
    public RDFFormat getRDFFormatValue() {
        return RDFFormatValue;
    }

    /**
     * Returns one of possibilities defined in {@link FileExtractType} how to
     * data extract.
     * 
     * @return one of possibilities defined in {@link FileExtractType} how to
     *         data extract.
     */
    public FileExtractType getFileExtractType() {
        return fileExtractType;
    }

    /**
     * Returns true, if only files with defined suffix are used for data
     * extraction, false otherwise.
     * 
     * @return true, if only files with defined suffix are used for data
     *         extraction, false otherwise.
     */
    public boolean useOnlyThisSuffix() {
        return OnlyThisSuffix;
    }

    /**
     * Returns true, if is used statistical handler for data extraction, false
     * otherwise.
     * 
     * @return true, if is used statistical handler for data extraction, false
     *         otherwise.
     */
    public boolean isUsedStatisticalHandler() {
        return UseStatisticalHandler;
    }

    /**
     * Returns true, if execution should fail when some errors are detected,
     * false otherwise.
     * 
     * @return true, if execution should fail when some errors are detected,
     *         false otherwise.
     */
    public boolean isFailWhenErrors() {
        return failWhenErrors;
    }

    public boolean isOnlyThisSuffix() {
        return OnlyThisSuffix;
    }

    public void setOnlyThisSuffix(boolean OnlyThisSuffix) {
        this.OnlyThisSuffix = OnlyThisSuffix;
    }

    public boolean isUseStatisticalHandler() {
        return UseStatisticalHandler;
    }

    public void setUseStatisticalHandler(boolean UseStatisticalHandler) {
        this.UseStatisticalHandler = UseStatisticalHandler;
    }

    /**
     * Returns true, if DPU configuration is valid, false otherwise.
     * 
     * @return true, if DPU configuration is valid, false otherwise.
     */
    @Override
    public boolean isValid() {
        return Path != null
                && FileSuffix != null
                && fileExtractType != null;
    }
}
