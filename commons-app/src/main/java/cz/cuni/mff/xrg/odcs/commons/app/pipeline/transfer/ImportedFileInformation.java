package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


public class ImportedFileInformation {

	private List<ExportedDpuItem> usedDpus = new ArrayList<>();
	private TreeMap<String, ExportedDpuItem> missingDpus = new TreeMap<>();

	boolean userDataFile = false;
	boolean scheduleFile = false;

	public ImportedFileInformation(List<ExportedDpuItem> usedDpus,
			TreeMap<String, ExportedDpuItem> missingDpus, boolean userDataFile, boolean scheduleFile) {

		this.usedDpus = usedDpus;
		this.missingDpus = missingDpus;
		this.userDataFile = userDataFile;
		this.scheduleFile = scheduleFile;
	}

	public List<ExportedDpuItem>  getUsedDpus() {
		return usedDpus;
	}

	public void setUsedDpus(List<ExportedDpuItem> usedDpus) {
		this.usedDpus = usedDpus;
	}

	public TreeMap<String, ExportedDpuItem> getMissingDpus() {
		return missingDpus;
	}

	public void setMissingDpus(TreeMap<String, ExportedDpuItem> missingDpus) {
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
