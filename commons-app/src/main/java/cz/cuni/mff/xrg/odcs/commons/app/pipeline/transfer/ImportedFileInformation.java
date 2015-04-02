package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


public class ImportedFileInformation {

	private List<DpuItem> usedDpus = new ArrayList<>();
	private TreeMap<String, DpuItem> missingDpus = new TreeMap<>();

	boolean userDataFile = false;
	boolean scheduleFile = false;

	public ImportedFileInformation(List<DpuItem> usedDpus,
			TreeMap<String, DpuItem> missingDpus, boolean userDataFile, boolean scheduleFile) {

		this.usedDpus = usedDpus;
		this.missingDpus = missingDpus;
		this.userDataFile = userDataFile;
		this.scheduleFile = scheduleFile;
	}

	public List<DpuItem>  getUsedDpus() {
		return usedDpus;
	}

	public void setUsedDpus(List<DpuItem> usedDpus) {
		this.usedDpus = usedDpus;
	}

	public TreeMap<String, DpuItem> getMissingDpus() {
		return missingDpus;
	}

	public void setMissingDpus(TreeMap<String, DpuItem> missingDpus) {
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
