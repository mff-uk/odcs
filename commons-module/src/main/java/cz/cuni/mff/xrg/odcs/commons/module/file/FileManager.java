package cz.cuni.mff.xrg.odcs.commons.module.file;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;

/**
 * Class for easier management of DPU's files. 
 * 
 * @author Petyr
 */
public final class FileManager {

	/**
	 * Associated {@link DPUContext}.
	 */
	private final DPUContext context;
	
	/**
	 * Create file manager for given {@link DPUContext}.
	 * @param context 
	 */
	public FileManager(DPUContext context) {
		this.context = context;
	}
	
	/**
	 * Return DPU's working file directory. The storage is unique for 
	 * DPU, execution and user.
	 * @return DPU's working file directory
	 */
	public Directory getWorking() {
		return new Directory(context.getWorkingDir());
	}

	/**
	 * Return DPU's user file directory. The storage is unique for
	 * DPU and user. It is shared among executions.
	 * @return DPU's user file directory
	 */
	public Directory getUser() {
		return new Directory(context.getUserDirectory());
	}

	/**
	 * Return DPU's global file directory. The storage is unique for
	 * DPU. It is shared among executions and users.
	 * @return  DPU's global file directory
	 */	
	public Directory getGlobal() {
		return new Directory(context.getGlobalDirectory());
	}
	
}
