package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer;

import java.util.TreeMap;

public class ImportedFileInformation {

	private TreeMap<String, String> usedDpus = new TreeMap<>();
	private TreeMap<String, String> missingDpus = new TreeMap<>();

	boolean userDataFile = false;
	boolean scheduleFile = false;

	public ImportedFileInformation(TreeMap<String, String> usedDpus,
			TreeMap<String, String> missingDpus, boolean userDataFile, boolean scheduleFile) {

		this.usedDpus = usedDpus;
		this.missingDpus = missingDpus;
		this.userDataFile = userDataFile;
		this.scheduleFile = scheduleFile;
	}

	public TreeMap<String, String> getUsedDpus() {
		return usedDpus;
	}

	public void setUsedDpus(TreeMap<String, String> usedDpus) {
		this.usedDpus = usedDpus;
	}

	public TreeMap<String, String> getMissingDpus() {
		return missingDpus;
	}

	public void setMissingDpus(TreeMap<String, String> missingDpus) {
		this.missingDpus = missingDpus;
	}

	public boolean isUserDataFile() {
		return userDataFile;
	}

	public void setUserDataFile(boolean userDataFile) {
		this.userDataFile = userDataFile;
	}

	public boolean isScheduleFile() {
		return scheduleFile;
	}

	public void setScheduleFile(boolean scheduleFile) {
		this.scheduleFile = scheduleFile;
	}
}
