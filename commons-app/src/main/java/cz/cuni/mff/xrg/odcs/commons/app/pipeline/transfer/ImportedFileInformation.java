package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer;

import java.util.TreeMap;
import java.util.TreeSet;

public class ImportedFileInformation {

	private TreeSet<ExportedDpuItem>  usedDpus = new TreeSet<>();
	private TreeMap<String, String> missingDpus = new TreeMap<>();

	boolean userDataFile = false;
	boolean scheduleFile = false;

	public ImportedFileInformation(TreeSet<ExportedDpuItem> usedDpus,
			TreeMap<String, String> missingDpus, boolean userDataFile, boolean scheduleFile) {

		this.usedDpus = usedDpus;
		this.missingDpus = missingDpus;
		this.userDataFile = userDataFile;
		this.scheduleFile = scheduleFile;
	}

	public TreeSet<ExportedDpuItem>  getUsedDpus() {
		return usedDpus;
	}

	public void setUsedDpus(TreeSet<ExportedDpuItem> usedDpus) {
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

	@Override
	public String toString() {
		return "ImportedFileInformation [usedDpus=" + usedDpus
				+ ", missingDpus=" + missingDpus + ", userDataFile="
				+ userDataFile + ", scheduleFile=" + scheduleFile + "]";
	}
	
	
}
