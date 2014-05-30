/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * @author tomasknap
 */
public abstract class HandlerImpl implements Handler {

    private static final Logger LOG = LoggerFactory.getLogger(
            HandlerImpl.class);

    /**
     * True if this {@link HandlerImpl} log warning about wrong
     * file format.
     */
    @XStreamOmitField
    private boolean hasReportNameChange = false;

    /**
     * Remove dangerous character from string so the string can be safely
     * used as a file or directory name.
     * 
     * @param origString
     * @return
     */
    @Override
    public String normalizeFileName(String origString) {
        String result = origString.replaceAll("[\\/:*?\"<>|]", "");
        if (!origString.equals(result)) {
            if (!hasReportNameChange) {
                LOG.warn("At least one file/dir name has been changed as it contained special chars [\\/:*?\"<>|]. More details can be found as 'info' level logs.");
                hasReportNameChange = true;
            }
            LOG.info("Name '{}' contained special chars and so it has been changed to '{}'.", origString, result);
        }
        return result;
    }
}
