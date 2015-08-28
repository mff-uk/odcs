/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer;

import java.util.List;
import java.util.Map;


public class ImportedFileInformation {

	private List<DpuItem> usedDpus;
	private Map<String, DpuItem> missingDpus;
	private Map<String, DpuItem> oldDpus;

	boolean userDataFile = false;
	boolean scheduleFile = false;

	public ImportedFileInformation(List<DpuItem> usedDpus,
			Map<String, DpuItem> missingDpus, boolean userDataFile,
			boolean scheduleFile, Map<String, DpuItem> oldDpus) {

		this.usedDpus = usedDpus;
		this.missingDpus = missingDpus;
		this.userDataFile = userDataFile;
		this.scheduleFile = scheduleFile;
		this.oldDpus = oldDpus;
	}

	public List<DpuItem>  getUsedDpus() {
		return usedDpus;
	}

	public void setUsedDpus(List<DpuItem> usedDpus) {
		this.usedDpus = usedDpus;
	}

	public Map<String, DpuItem> getMissingDpus() {
		return missingDpus;
	}

	public void setMissingDpus(Map<String, DpuItem> missingDpus) {
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
	
    public Map<String, DpuItem> getOldDpus() {
        return oldDpus;
    }

    public void setOldDpus(Map<String, DpuItem> oldDpus) {
        this.oldDpus = oldDpus;
    }

    @Override
	public String toString() {
		return "ImportedFileInformation [usedDpus=" + usedDpus
				+ ", missingDpus=" + missingDpus + ", userDataFile="
				+ userDataFile + ", scheduleFile=" + scheduleFile
				+ ", oldDpus=" + oldDpus + "]";
	}
	
	
}
