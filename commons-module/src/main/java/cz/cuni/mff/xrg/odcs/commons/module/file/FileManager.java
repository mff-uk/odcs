package cz.cuni.mff.xrg.odcs.commons.module.file;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;


/**
 * Class for easier management of DPU's files. 
 * 
 * @author Petyr
 */
public final class FileManager {

	private DPUContext context;
	
	public FileManager(DPUContext context) {
		this.context = context;
	}
	
	/**
	 * Return DPU's working file directory. The storage is unique for 
	 * DPU, execution and user.
	 * @return
	 */
	public Directory getWorking() {
		return new Directory(context.getWorkingDir());
	}

	/**
	 * Return DPU's user file directory. The storage is unique for
	 * DPU and user. It is shared among executions.
	 * @return
	 */
	public Directory getUser() {
		return new Directory(context.getUserDirectory());
	}

	/**
	 * Return DPU's global file directory. The storage is unique for
	 * DPU. It is shared among executions and users.
	 * @return
	 */	
	public Directory getGlobal() {
		return new Directory(context.getGlobalDirectory());
	}
	
}
