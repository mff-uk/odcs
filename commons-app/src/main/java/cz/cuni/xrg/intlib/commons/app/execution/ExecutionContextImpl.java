package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;

@XmlRootElement
class ExecutionContextImpl implements ExecutionContextReader, ExecutionContextWriter {

	/**
	 * Store context information for DPUs under
	 * DPU's id.
	 */
	@XmlElement
	private HashMap<Long, DPUContextInfo> contexts = new HashMap<>();

	/**
	 * Working directory for execution.
	 */
	private File workingDirectory = null;

	/**
	 * Empty ctor because of JAXB.
	 */
	public ExecutionContextImpl() {

	}

	/**
	 *
	 * @param workingDirectory Execution working directory doesn't have to exit.
	 */
	public ExecutionContextImpl(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	@Override
	public DataUnitInfo getDataUnitInfo(DPUInstance dpuInstance, int id) {
		return getContext(dpuInstance).getDataUnitInfo(id);
	}

	@Override
	public File createDirForDataUnit(DPUInstance dpuInstance,
			DataUnitType type, boolean isInput, int index) {
		return getContext(dpuInstance).createDirForDataUnit(type, isInput, index);
	}

	@Override
	public File getDirForDPUStorage(DPUInstance dpuInstance) {
		return getContext(dpuInstance).getDirForDPUStorage();
	}

	@Override
	public File getDirForDPUResult(DPUInstance dpuInstance) {
		return getContext(dpuInstance).getDirForDPUResult(true);
	}

	@Override
	public boolean containsData(DPUInstance dpuInstance) {
		return contexts.containsKey(dpuInstance.getId());
	}

	@Override
	public Set<Integer> getIndexesForDataUnits(DPUInstance dpuInstance) {
		if (contexts.containsKey(dpuInstance.getId())) {
			return null;
		} else {
			Long id = dpuInstance.getId();
			DPUContextInfo dpuContextInfo = contexts.get(id);
			return dpuContextInfo.getIndexForDataUnits();
		}
	}

	@Override
	public File getDirectoryForResult(DPUInstance dpuInstance) {
		if (contexts.containsKey(dpuInstance.getId())) {
			return contexts.get(dpuInstance.getId()).getDirForDPUResult(false);
		} else {
			// DPU's context does'n exist
			return null;
		}
	}

	@Override
	public void save() throws Exception {
		// make sure that the folder exist
		workingDirectory.mkdirs();
		// get output file
		File outputFile = getloadFilePath();
		// delete file if existing ..
		if (outputFile.exists()) {
			outputFile.delete();
		}
		// now create a new file and write output ..
		JAXBContext jc = JAXBContext.newInstance(ExecutionContextImpl.class, DPUContextInfo.class);
		Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(this, new FileOutputStream(outputFile) );
	}

	@Override
	public File getloadFilePath() {
		return new File(workingDirectory, "context.xml");
	}

	@Override
	public File getLog4jFile() {
		return new File(workingDirectory, "log4j.txt");
	}

	/**
	 * Return {@link DPUContextInfo} for given {@link DPUInstance}
	 * @param dpuInstance
	 * @return
	 */
	private DPUContextInfo getContext(DPUInstance dpuInstance) {
		if (contexts.containsKey(dpuInstance.getId())) {
			// context exist
			return contexts.get(dpuInstance.getId());
		} else {
			// prepare directory
			File dpuContextDir = new File(workingDirectory, dpuInstance.getId().toString() );
			// create context
			DPUContextInfo newContext = new DPUContextInfo(dpuContextDir);
			contexts.put(dpuInstance.getId(), newContext);
			return newContext;
		}
	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
}
