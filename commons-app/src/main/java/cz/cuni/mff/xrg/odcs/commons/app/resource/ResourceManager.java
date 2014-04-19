package cz.cuni.mff.xrg.odcs.commons.app.resource;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.conf.MissingConfigPropertyException;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPURecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provide access to resources.
 *
 * @author Škoda Petr
 */
public class ResourceManager {

	/**
	 * Name of sub-directory for shared DPU's data.
	 */
	private static final String DPU_JAR_DIR = "dpu";

	/**
	 * Name of sub-directory for per-user DPU's data.
	 */
	private static final String DPU_DATE_USER_DIR = "user";
	
	/**
	 * Name of sub-directory for DPU's global data.
	 */
	private static final String DPU_DATA_GLOBAL_DIR = "dpu";

	@Autowired
	private AppConfig appConfig;

	/**
	 * Return jar file of given DPU.
	 *
	 * @param dpu
	 * @return
	 * @throws MissingResourceException
	 */
	public File getDPUJarFile(DPURecord dpu) throws MissingResourceException {
		final String modulePath;
		try {
			modulePath = appConfig.getString(ConfigProperty.MODULE_PATH);
		} catch (MissingConfigPropertyException ex) {
			throw new MissingResourceException(
					"Config property module_path is no set.");
		}
		// get DPU template
		final DPUTemplateRecord template = getDPUTemplate(dpu);

		return new File(modulePath + File.separator + DPU_JAR_DIR, 
				template.getJarPath());
	}

	/**
	 *
	 * @param dpu
	 * @param user
	 * @return Path to per-user DPU data storage.
	 * @throws MissingResourceException
	 */
	public File getDPUDataUserDir(DPURecord dpu, User user) throws MissingResourceException {
		if (user == null) {
			throw new MissingResourceException("Unknown user.");
		}

		final String workingPath = getWorkingDir();
		final DPUTemplateRecord template = getDPUTemplate(dpu);
		
		// prepare relative part of the path
		final String relativePath = DPU_DATE_USER_DIR + File.separator + user
				.getUsername() + File.separator + template.getJarDirectory();

		return new File(workingPath, relativePath);
	}

	/**
	 * 
	 * @param dpu
	 * @return Path to global shared DPU data directory.
	 * @throws MissingResourceException 
	 */
	public File getDPUDataGlobalDir(DPURecord dpu) throws MissingResourceException {
		final String workingPath = getWorkingDir();
		final DPUTemplateRecord template = getDPUTemplate(dpu);
		
		// prepare relative part of the path
		final String relativePath = DPU_DATA_GLOBAL_DIR + File.separator + 
				template.getJarDirectory();

		return new File(workingPath, relativePath);
	
	}

	/**
	 * 
	 * @param dpu
	 * @return Template for given DPU. Return given DPU if it's template.
	 * @throws MissingResourceException 
	 */
	private DPUTemplateRecord getDPUTemplate(DPURecord dpu) throws MissingResourceException {
		final DPUTemplateRecord template;
		if (dpu instanceof DPUInstanceRecord) {
			template = ((DPUInstanceRecord) dpu).getTemplate();
		} else if (dpu instanceof DPUTemplateRecord) {
			template = (DPUTemplateRecord) dpu;
		} else {
			throw new MissingResourceException("Unknown DPU type.");
		}

		if (template == null) {
			throw new MissingResourceException("DPU tempalte is not set.");
		}
		return template;
	}
	
	/**
	 * 
	 * @return Working directory.
	 * @throws MissingResourceException 
	 */
	private String getWorkingDir() throws MissingResourceException {
		try {
			return appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR);
		} catch (MissingConfigPropertyException ex) {
			throw new MissingResourceException(
					"Config property module_path is no set.");
		}
	}
	
}
