package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer;

import java.util.TreeMap;

public class MissingAndUsedDpusResult {

	private TreeMap<String, String> usedDpus = new TreeMap<>();
	private TreeMap<String, String> missingDpus = new TreeMap<>();

	public MissingAndUsedDpusResult(TreeMap<String, String> usedDpus,
			TreeMap<String, String> missingDpus) {

		this.usedDpus = usedDpus;
		this.missingDpus = missingDpus;
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

}
