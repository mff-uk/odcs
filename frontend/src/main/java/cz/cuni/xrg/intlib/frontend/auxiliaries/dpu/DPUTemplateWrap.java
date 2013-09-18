package cz.cuni.xrg.intlib.frontend.auxiliaries.dpu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.thirdparty.guava.common.io.Files;

import cz.cuni.xrg.intlib.commons.app.auth.VisibilityType;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUExplorer;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUType;
import cz.cuni.xrg.intlib.commons.app.module.BundleInstallFailedException;
import cz.cuni.xrg.intlib.commons.app.module.ClassLoadFailedException;
import cz.cuni.xrg.intlib.commons.app.module.DpuLockedException;
import cz.cuni.xrg.intlib.commons.app.module.ModuleChangeNotifier;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacadeConfig;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.components.FileUploadReceiver;

/**
 * Wrap {@link DPUTemplateRecord} to made work with configuration and
 * configuration dialog easier.
 * 
 * @author Petyr
 * 
 */
public class DPUTemplateWrap extends DPURecordWrap {

	private static final Logger LOG = LoggerFactory
			.getLogger(DPUTemplateWrap.class);

	/**
	 * Wrapped DPUTemplateRecord.
	 */
	private DPUTemplateRecord dpuTemplate;

	/**
	 * Create wrap for DPUTemplateRecord.
	 * 
	 * @param dpuTemplate
	 */
	public DPUTemplateWrap(DPUTemplateRecord dpuTemplate) {
		super(dpuTemplate);
		this.dpuTemplate = dpuTemplate;
	}

	/**
	 * Save wrapped DPUInstanceInto database. To save configuration from dialog
	 * as well call {{@link #saveConfig()} first.
	 */
	public void save() {
		App.getDPUs().save(dpuTemplate);
	}

	/**
	 * Delete the wrapped {@link DPUTemplateRecord} from database, uninstall it
	 * as a bundle and delete the folder with respective jar file.
	 */
	public void delete() {

		// delete from database
		App.getApp().getDPUs().delete(dpuTemplate);

		// try to delete DPU's directory or at least the DPU's file
		final ModuleFacadeConfig moduleConfig = App.getApp().getBean(
				ModuleFacadeConfig.class);

		final File directory = new File(moduleConfig.getDpuFolder(),
				dpuTemplate.getJarDirectory());
		LOG.debug("Deleting {}", directory.toString());
		try {
			FileUtils.deleteDirectory(directory);
		} catch (IOException e) {
			LOG.error("Failed to delete directory.", e);
		}
		// uninstall bundle
		App.getApp().getModules().uninstall(dpuTemplate.getJarPath());
	}

	/**
	 * Return file for the backUp to given DPU's jar file.
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
	 * @param newDpu
	 * @throws DPUReplaceException
	 */
	protected void createBackUp(File originalDpu, File newDpu) throws DPUReplaceException {
		if (newDpu == originalDpu) {
			// they are the same .. we need backup
		} else {
			// they are not the same, no backup is needed
		}
		
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
	protected void recoverFromBackUp(File originalDpu, File newDpu) {
		if (newDpu == originalDpu) {
			// they are the same, backup can exist, recover
		} else {
			// they are not the same, so original file is untouched
		}		
		
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
	 * @param originalDpu
	 */
	protected void deleteBackUp(File originalDpu) {
		File originalDpuBackUp = createBackUpName(originalDpu);
		originalDpuBackUp.delete();
	}
	
	/**
	 * Replace DPU's jar file with given jar-file. In case of error the original
	 * DPU 's jar file is preserved and the {@link DPUReplaceException} is
	 * throws.
	 * 
	 * @param sourceDpuFile
	 * @throws DPUReplaceException Contains message for the user.
	 */
	public void replace(File sourceDpuFile) throws DPUReplaceException {
		ModuleFacadeConfig moduleConfig = App.getApp().getBean(
				ModuleFacadeConfig.class);
		// get file to the source and to the originalDPU
		final File originalDpuFile = new File(moduleConfig.getDpuFolder(), 
				dpuTemplate.getJarPath());
		
		// validate input DPU's name
		try {
			getDirectoryName(sourceDpuFile.getName());
		} catch(DPUCreateException e) {
			throw new DPUReplaceException(e.getMessage());
		}
		// get directory from current DPU's jar path
		String directoryName = dpuTemplate.getJarDirectory();

		// get name for new DPU
		final File newDPUFile = 
				new File(moduleConfig.getDpuFolder() + File.separator + 
						directoryName, sourceDpuFile.getName()); 
		final String newRelativePath = directoryName + File.separator + sourceDpuFile.getName();
		
		// we need a backup of the original file here
		createBackUp(originalDpuFile, newDPUFile);

		ModuleFacade moduleFacade = App.getApp().getModules();
		// we have to lock bundle here .. 
		// other user may rise request and upload the old bundle
		// this will result in situation were the "same" bundle is loaded twice		
		moduleFacade.lock(dpuTemplate.getJarPath());
		
		// we replace the current DPU
		// OSGI keep data in cache, so we can first try to replace file
		try {
			Files.copy(sourceDpuFile, newDPUFile);
		} catch (IOException e) {
			// failed to copy DPU
			LOG.error("Failed to copy new DPU jar file {}",
					newDPUFile.getPath(), e);
			// recover
			recoverFromBackUp(originalDpuFile, newDPUFile);
			moduleFacade.unlock(dpuTemplate.getJarPath());
			throw new DPUReplaceException("Can't copy new DPU jar file.");
		}

		// now we try to load new instance of DPU		
		Object newDpuInstance = null;
				
		// we can use update, but the uninstall do not throw bundle exception
		moduleFacade.uninstall(dpuTemplate.getJarPath());
		try {
			newDpuInstance = App.getApp().getModules().getObject(newRelativePath);
		} catch (BundleInstallFailedException | ClassLoadFailedException
				| FileNotFoundException e) {
			LOG.warn("Failed to load bunle during replace.", e);
			// recover
			recoverFromBackUp(originalDpuFile, newDPUFile);
			moduleFacade.uninstall(newRelativePath);
			moduleFacade.unlock(dpuTemplate.getJarPath());
			// the old bundle will be loaded with first user request
			throw new DPUReplaceException(
					"Can't load instance from new bundle. Exception: "
							+ e.getMessage());
		}
		
		// if we are here we have backUp bundle which has been uninstalled
		// we have new bundle which is functional

		// now we can examine the DPU
		DPUExplorer dpuExplorer = App.getApp().getDPUExplorere();
		DPUType dpuType = dpuExplorer.getType(newDpuInstance, newRelativePath);
		if (dpuType == dpuTemplate.getType()) {
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
			recoverFromBackUp(originalDpuFile, newDPUFile);
			moduleFacade.uninstall(newRelativePath);
			moduleFacade.unlock(dpuTemplate.getJarPath());
			// DPU type changed .. we do not allow this
			throw new DPUReplaceException(
					"New DPU has different type then the old one. "
							+ typeMessage);
		}
		// get jarName
		final String oldJarPath = dpuTemplate.getJarPath();
		final String newJarName = newDPUFile.getName();
		// get new data from manifest.mf and save this changes
		final String jarDescription = dpuExplorer.getJarDescription(newRelativePath);
		dpuTemplate.setJarDescription(jarDescription);
		dpuTemplate.setJarName(newJarName);
		App.getDPUs().save(dpuTemplate);
		// we delete the backup
		deleteBackUp(originalDpuFile);
		
		// here we can unlock the bundle, important is that we have
		// lock on original instance
		moduleFacade.unlock(oldJarPath);
		
		// at the end we notify backend about new DPU's version
		App.getApp().getBean(ModuleChangeNotifier.class).updated(dpuTemplate);
	}

	public DPUTemplateRecord getDPUTemplateRecord() {
		return dpuTemplate;
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
	 * Prepare directory with given name in DPU's directory. In case of failure
	 * or if the directory is already used throw exception.
	 * 
	 * @param newDpuDirName
	 * @return Existing directory.
	 * @throws DPUCreateException
	 */
	protected static File prepareDirectory(String newDpuDirName)
			throws DPUCreateException {
		final ModuleFacadeConfig moduleConfig = App.getApp().getBean(
				ModuleFacadeConfig.class);
		final File newDPUDir = new File(moduleConfig.getDpuFolder(),
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
	 * Create {@link DPUTemplateWrap} for given DPU. If success then return new
	 * instance of {@link DPUTemplateWrap} which wrap the new
	 * {@link DPUTemplateRecord} in such case the DPU is loaded into application
	 * the DPU's and is presented in DPU's directory. The visibility of new
	 * {@link DPUTemplateWrap} is set to {@link VisibilityType#PRIVATE}.
	 * 
	 * Use setters to set additional DPU's fields like name, description,
	 * visibility, ... those are not set by the {@link create} method.
	 * 
	 * In case throw {@link DPUCreateException}. Use
	 * {@link DPUCreateException#getMessage()} to get description that can be
	 * shown to the user.
	 * 
	 * @param name Name for new {@link DPUTemplateWrap}.
	 * @param sourceFile File with DPU's jar file, can be null.
	 * @return {@link DPUTemplateWrap} for new {@link DPUTemplateWrap} or null
	 *         in case of error.
	 * @throws DPUCreateException
	 */
	public static DPUTemplateWrap create(File sourceFile, String name)
			throws DPUCreateException {
		if (sourceFile == null) {
			// failed to load file
			throw new DPUCreateException(
					"The DPU's file has not been loaded properly.");
		}
		// get directory name and also validate the DPU's file name
		final String newDpuFileName = sourceFile.getName();
		final String newDpuDirName = getDirectoryName(newDpuFileName);
		// get unique directory for DPU's
		final File newDPUDir = prepareDirectory(newDpuDirName);

		// copy the file to the directory
		final File newDPUFile = new File(newDPUDir, newDpuFileName);
		try {
			Files.copy(sourceFile, newDPUFile);
		} catch (IOException e) {
			// release
			newDPUDir.delete();
			// failed to copy file
			LOG.error("Failed to copy file, when creating new DPU.", e);
			throw new DPUCreateException("Failed to create DPU file.");
		}

		// try to load bundle
		final String dpuRelativePath = newDpuDirName + File.separator
				+ newDpuFileName;
		Object dpuObject = null;
		try {
			dpuObject = App.getApp().getModules().getObject(dpuRelativePath);
		} catch (BundleInstallFailedException | ClassLoadFailedException
				| FileNotFoundException e) {
			// release
			newDPUDir.delete();
			newDPUFile.delete();
			LOG.error("Failed to load new DPU bundle.", e);
			throw new DPUCreateException(
					"Failed to load DPU bacuse of exception:" + e.getMessage());
		}

		final DPUExplorer dpuExplorer = App.getApp().getDPUExplorere();
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
			App.getApp().getModules().uninstall(dpuRelativePath);
			throw new DPUCreateException("DPU has unspecified type.");
		}

		// ok we can create the record
		DPUTemplateRecord newTemplete = new DPUTemplateRecord(name, dpuType);

		newTemplete.setDescription("");
		newTemplete.setJarDescription(jarDescription);
		newTemplete.setJarDirectory(newDpuDirName);
		newTemplete.setJarName(newDpuFileName);
		newTemplete.setVisibility(VisibilityType.PRIVATE);

		// and save it into DB
		try {
			App.getDPUs().save(newTemplete);
		} catch (Throwable e) {
			// release
			newDPUDir.delete();
			newDPUFile.delete();
			App.getApp().getModules().uninstall(dpuRelativePath);
			//
			LOG.error("Failed to save new DPU record", e);
			throw new DPUCreateException("Failed to save new DPU: "
					+ e.getMessage());
		}

		// create wrap and return it
		return new DPUTemplateWrap(newTemplete);
	}

}
