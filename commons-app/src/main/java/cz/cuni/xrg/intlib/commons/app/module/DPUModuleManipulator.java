package cz.cuni.xrg.intlib.commons.app.module;

import java.io.File;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.xrg.intlib.commons.app.auth.VisibilityType;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUExplorer;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUFacade;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUType;

/**
 * Class provide one-place access to create/update/delete actions for DPUs. It
 * takes care about functionality connected with {@link DPUFacade} as well with
 * {@link ModuleFacade}.
 * 
 * @author Petyr
 * 
 */
public class DPUModuleManipulator {

	private static final Logger LOG = LoggerFactory
			.getLogger(DPUModuleManipulator.class);

	@Autowired
	private DPUFacade dpuFacade;

	@Autowired
	private ModuleFacade moduleFacade;

	@Autowired
	private DPUExplorer dpuExplorer;

	@Autowired
	private ModuleChangeNotifier notifier;

	/**
	 * Create {@link DPUTemplateRecord} for given DPU. If success then return
	 * new instance {@link DPUTemplateRecord} in such case the DPU is loaded
	 * into application the DPU's and is presented in DPU's directory. The
	 * visibility of new {@link DPUTemplateWrap} is set to
	 * {@link VisibilityType#PRIVATE}.
	 * 
	 * Use setters to set additional DPU's fields like name, description,
	 * visibility, ... those are not set by the {@link create} method.
	 * 
	 * In case throw {@link DPUCreateException}. Use
	 * {@link DPUCreateException#getMessage()} to get description that can be
	 * shown to the user.
	 * 
	 * @param sourceFile
	 * @param name
	 * @return
	 * @throws DPUCreateException
	 */
	public DPUTemplateRecord create(File sourceFile, String name)
			throws DPUCreateException {
		if (sourceFile == null) {
			// failed to load file
			throw new DPUCreateException(
					"The DPU's file has not been loaded properly.");
		}
		// get directory name and also validate the DPU's file name
		final String newDpuFileName = sourceFile.getName();
		final String newDpuDirName = getDirectoryName(newDpuFileName);
		// prepare directory secure that this method
		// will not continue for same jar-file twice .. use synchronisation
		// over file system.
		final File newDPUDir = prepareDirectory(newDpuDirName);
		final File newDPUFile = new File(newDPUDir, newDpuFileName);
		// copy
		try {
			FileUtils.copyFile(sourceFile, newDPUFile);
		} catch (IOException e) {
			// release
			newDPUDir.delete();
			// failed to copy file
			LOG.error("Failed to copy file, when creating new DPU.", e);
			throw new DPUCreateException("Failed to create DPU file.");
		}

		// we need dpu template to work wit DPUs
		DPUTemplateRecord newTemplate = new DPUTemplateRecord(name, null);
		newTemplate.setJarDirectory(newDpuDirName);
		newTemplate.setJarName(newDpuFileName);

		// try to load bundle
		final String dpuRelativePath = newDpuDirName + File.separator
				+ newDpuFileName;
		Object dpuObject = null;
		try {
			dpuObject = moduleFacade.getInstance(newTemplate);
		} catch (ModuleException e) {
			// release
			newDPUDir.delete();
			newDPUFile.delete();
			LOG.error("Failed to load new DPU bundle.", e);
			throw new DPUCreateException(
					"Failed to load DPU bacuse of exception:" + e.getMessage());
		}

		String jarDescription = dpuExplorer.getJarDescription(dpuRelativePath);
		if (jarDescription == null) {
			// failed to read description .. use empty string
			jarDescription = "";
		}
		// check type ..
		final DPUType dpuType = dpuExplorer.getType(dpuObject, dpuRelativePath);
		if (dpuType == null) {
			// release
			newDPUDir.delete();
			newDPUFile.delete();
			moduleFacade.unLoad(newTemplate);
			throw new DPUCreateException("DPU has unspecified type.");
		}
		// set other DPUs variables
		newTemplate.setType(dpuType);
		newTemplate.setDescription("");
		newTemplate.setJarDescription(jarDescription);
		newTemplate.setVisibility(VisibilityType.PRIVATE);
		// and save it into DB
		try {
			dpuFacade.save(newTemplate);
		} catch (Throwable e) {
			// release
			newDPUDir.delete();
			newDPUFile.delete();
			moduleFacade.unLoad(newTemplate);
			//
			LOG.error("Failed to save new DPU record", e);
			throw new DPUCreateException("Failed to save new DPU: "
					+ e.getMessage());
		}
		
		// notify the rest of the application
		notifier.created(newTemplate);		
		
		// return new DPUTempateRecord
		return newTemplate;
	}

	/**
	 * Try to replace jar-file for given DPU with given file. During the replace
	 * the given DPU is inaccessible through the {@link ModuleFacade}. If the
	 * new jar file can not be used, the old jar file is preserved and the
	 * {@link DPUReplaceException} is thrown.
	 * 
	 * @param dpu
	 * @param sourceDpuFile
	 * @throws DPUReplaceException
	 */
	public void replace(DPUTemplateRecord dpu, File sourceDpuFile)
			throws DPUReplaceException {
		// get file to the source and to the originalDPU
		final File originalDpuFile = new File(moduleFacade.getDPUDirectory(),
				dpu.getJarPath());
		final String directoryName = dpu.getJarDirectory();
		// validate input DPU's name
		try {
			getDirectoryName(sourceDpuFile.getName());
		} catch (DPUCreateException e) {
			throw new DPUReplaceException(e.getMessage());
		}
		// prepare the paths for new DPU
		final String newDpuName = sourceDpuFile.getName();
		final File newDpuFile = new File(moduleFacade.getDPUDirectory()
				+ File.separator + directoryName, newDpuName);
		final String newRelativePath = directoryName + File.separator
				+ sourceDpuFile.getName();

		// we have to lock bundle here ..
		// this prevent every other user .. from working with DPUs in give
		// directory, so we are the only one here .. we can do what we want :)

		// the backend should keep it's copy loaded .. so it do not
		// try to access the directory either
		moduleFacade.beginUpdate(dpu);

		// we need a backup of the original file here
		createBackUp(originalDpuFile);

		// copy new file, can replace the old one possibly
		try {
			FileUtils.copyFile(sourceDpuFile, newDpuFile);
		} catch (IOException e) {
			// failed to copy DPU
			LOG.error("Failed to copy new DPU jar file {}",
					newDpuFile.getPath(), e);
			// recover
			recoverFromBackUp(originalDpuFile);
			moduleFacade.endUpdate(dpu, true);
			throw new DPUReplaceException("Can't copy new DPU jar file.");
		}

		// now we try to load new instance of DPU
		Object newDpuInstance = null;

		// we can use update
		try {
			// update
			newDpuInstance = moduleFacade.update(directoryName, newDpuName);
		} catch (ModuleException e) {
			LOG.warn("Failed to load bunle during replace.", e);
			// recover
			recoverFromBackUp(originalDpuFile);
			// finish update and unload remove DPU record
			moduleFacade.endUpdate(dpu, true);
			// the old bundle will be loaded with first user request
			throw new DPUReplaceException(
					"Can't load instance from new bundle. Exception: "
							+ e.getMessage());
		}

		// if we are here we have backUp bundle which has been uninstalled
		// we have new bundle which is functional

		// now we can examine the DPU
		DPUType dpuType = dpuExplorer.getType(newDpuInstance, newRelativePath);
		if (dpuType == dpu.getType()) {
			// type match .. we can continue
		} else {
			// we store message about type here
			String typeMessage = "";
			if (dpuType == null) {
				typeMessage = "New DPU has unspecified type. Check the DPU's annotations";
			} else {
				typeMessage = "New DPU has different type then the old one.";
			}
			// recover
			recoverFromBackUp(originalDpuFile);
			// finish update and unload remove DPU record
			moduleFacade.endUpdate(dpu, true);
			// DPU type changed .. we do not allow this
			throw new DPUReplaceException(
					"New DPU has different type then the old one. "
							+ typeMessage);
		}

		// get new data from manifest.mf and save this changes
		final String jarDescription = dpuExplorer.getJarDescription(newRelativePath);
		dpu.setJarDescription(jarDescription);
		dpu.setJarName(newDpuName);
		dpuFacade.save(dpu);
		
		// we delete the backup
		deleteBackUp(originalDpuFile);		
		
		// here we can unlock the bundle
		// we else say mofuleFacade to not drop the current module
		// as the replace has been successful
		moduleFacade.endUpdate(dpu, false);
		
		// notify the rest of the application
		notifier.updated(dpu);
	}

	/**
	 * Delete given DPU record from database, unload it's bundle if loaded and
	 * also delete it's jar file.
	 * 
	 * @param dpu
	 */
	public void delete(DPUTemplateRecord dpu) {
		dpuFacade.delete(dpu);
		moduleFacade.delete(dpu);
		
		// notify the rest of the application
		notifier.deleted(dpu);
	}

	/**
	 * Prepare directory with given name in DPU's directory. In case of failure
	 * or if the directory is already used throw exception.
	 * 
	 * @param newDpuDirName
	 * @return Existing directory.
	 * @throws DPUCreateException
	 */
	protected File prepareDirectory(String newDpuDirName)
			throws DPUCreateException {
		final File newDPUDir = new File(moduleFacade.getDPUDirectory(),
				newDpuDirName);
		if (newDPUDir.exists()) {
			// directory name already used
			throw new DPUCreateException(String.format(
					"DPU's directory with name '%s' already exist.",
					newDpuDirName));
		}
		// create directory
		if (newDPUDir.mkdir()) {
			// ok, directory has been created
		} else {
			// failed
			throw new DPUCreateException(String.format(
					"Failed to create DPU's directory: '%s'", newDpuDirName));
		}
		return newDPUDir;
	}

	/**
	 * Validate the sourcePath. If the sourcePath is in right format then return
	 * the name for DPU's directory otherwise throws.
	 * 
	 * @param sourceFileName
	 * @return
	 * @throws DPUCreateException
	 */
	protected static String getDirectoryName(String sourceFileName)
			throws DPUCreateException {
		// the name must be in format: NAME-?.?.?.jar
		final Pattern pattern = Pattern
				.compile("(.+)-(\\d+\\.\\d+\\.\\d+)\\.jar");
		final Matcher matcher = pattern.matcher(sourceFileName);
		if (matcher.matches()) {
			// 0 - original, 1 - name, 2 - version
			return matcher.group(1);
		} else {
			throw new DPUCreateException(
					"DPU's name must be in format NAME-NUMBER.NUMBER.NUMBER.jar");
		}
	}

	/**
	 * Return file for the backUp to given DPU's jar file.
	 * 
	 * @param originalDPU
	 * @return
	 */
	protected File createBackUpName(File originalDpU) {
		return new File(originalDpU.toString() + ".backup");
	}

	/**
	 * Try to create backup for given file. If the file do not exist then log
	 * this information but do not thrown.
	 * 
	 * @param originalDpu
	 * @throws DPUReplaceException
	 */
	protected void createBackUp(File originalDpu) throws DPUReplaceException {
		File originalDpuBackUp = createBackUpName(originalDpu);
		// check if original file exist
		if (originalDpu.exists()) {
			// yes, continue
		} else {
			// nothing to backup .. end
			LOG.warn("Original DPU does not exist.");
			return;
		}
		// check if backup is not already used
		if (originalDpuBackUp.exists()) {
			// try to delete it
			if (originalDpuBackUp.delete()) {
				// if we fail .. we can do nothing ..
				LOG.warn("Failed to delete previous DPU backUp file: {}",
						originalDpuBackUp.getPath());
			}
		}
		if (originalDpu.renameTo(originalDpuBackUp)) {
			// we have backup .. we can continue
		} else {
			// no backup no continue .. if we continue then we can lose the only
			// functional version we have
			LOG.error("Failed to create backUp file: {}",
					originalDpuBackUp.getPath());
			throw new DPUReplaceException(
					"Failed to create original DPU backup.");
		}
	}

	/**
	 * I there is no backup then nothing happened. If there is backup, then try
	 * to use it to recover original DPU's jar file. The function does not
	 * require the originalDpu to has been deleted.
	 * 
	 * @param originalDpu
	 */
	protected void recoverFromBackUp(File originalDpu) {
		File originalDpuBackUp = createBackUpName(originalDpu);
		if (originalDpuBackUp.exists()) {
			// remove new file
			if (originalDpu.exists()) {
				originalDpu.delete();
			}
			// use backup
			originalDpuBackUp.renameTo(originalDpu);
		}
	}

	/**
	 * Delete backUp if exist.
	 * 
	 * @param originalDpu
	 */
	protected void deleteBackUp(File originalDpu) {
		File originalDpuBackUp = createBackUpName(originalDpu);
		originalDpuBackUp.delete();
	}

}
