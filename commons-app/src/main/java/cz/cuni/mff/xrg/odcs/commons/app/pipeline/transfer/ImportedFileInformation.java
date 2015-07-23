/*******************************************************************************
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
 *******************************************************************************/
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
