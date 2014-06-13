package cz.cuni.mff.xrg.odcs.dpu.filestolocaldirectoryloader;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

public class FilesToLocalDirectoryLoaderConfig extends DPUConfigObjectBase {
	/**
     * 
     */
	private static final long serialVersionUID = -3161162556703740405L;

	private String destination = "/tmp";

	private boolean moveFiles = false;

	// DPUTemplateConfig must provide public non-parametric constructor
	public FilesToLocalDirectoryLoaderConfig() {
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public boolean isMoveFiles() {
		return moveFiles;
	}

	public void setMoveFiles(boolean moveFiles) {
		this.moveFiles = moveFiles;
	}
}
