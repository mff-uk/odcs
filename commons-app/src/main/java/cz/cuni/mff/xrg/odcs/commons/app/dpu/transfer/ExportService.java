package cz.cuni.mff.xrg.odcs.commons.app.dpu.transfer;

import com.thoughtworks.xstream.XStream;
import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.transfer.xstream.JPAXStream;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ScheduleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.resource.MissingResourceException;
import cz.cuni.mff.xrg.odcs.commons.app.resource.ResourceManager;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import java.io.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Export given pipeline into file.
 *
 * @author Škoda Petr
 */
public class ExportService {

	private static final String PIPELINE_ENTRY = "pipeline.xml";

	private static final String SCHEDULE_ENTRY = "schedule.xml";

	private static final String DPU_JAR_ENTRY = "dpu_jar";

	private static final String DPU_DATA_GLOBAL_ENTRY = "dpu_data_global";

	private static final String DPU_DATA_USER_ENTRY = "dpu_data_user";

	private static final Logger LOG = LoggerFactory.getLogger(
			ExportService.class);

	@Autowired
	private ScheduleFacade scheduleFacade;

	@Autowired
	private ResourceManager resourceManager;

	@Autowired(required = false)
	private AuthenticationContext authCtx;

	/**
	 * Create a temp file and export pipeline into it.
	 *
	 * @param pipeline
	 * @return File with exported pipeline.
	 * @throws cz.cuni.mff.xrg.odcs.commons.app.dpu.transfer.TransferException
	 */
	public File exportIntoTempFile(Pipeline pipeline) throws TransferException {
		// TODO we could utilize some central storage
		final String relativePath = "odcs" + File.separator + "export"
				+ File.separator;
		final File tempDirectory = new File(FileUtils.getTempDirectory(),
				relativePath);
		
		tempDirectory.mkdirs();
		
		// try to get unique file name
		int attemp = 0;
		File targetFile;
		while (true) {
			try {
				targetFile = File.createTempFile("pipeline_", ".zip",
						tempDirectory);
				break;
			} catch (Exception ex) {
				LOG.trace("Failed to get temp file.", ex);
				attemp++;
				if (attemp > 10) {
					throw new TransferException("Failed to get temp file.");
				}
			}
		}

		export(pipeline, targetFile);
		return targetFile;
	}

	/**
	 * Export given pipeline and all it's dependencies into given file.
	 *
	 * @param pipeline
	 * @param targetFile
	 * @throws cz.cuni.mff.xrg.odcs.commons.app.dpu.transfer.TransferException
	 */
	public void export(Pipeline pipeline, File targetFile)
			throws TransferException {

		if (authCtx == null) {
			throw new TransferException("AuthenticationContext is null.");
		}
		final User user = authCtx.getUser();
		if (user == null) {
			throw new TransferException("Unknown user.");
		}

		try (FileOutputStream fos = new FileOutputStream(targetFile);
				ZipOutputStream zipStream = new ZipOutputStream(fos)) {
			// save information about pipeline and schedule
			savePipeline(pipeline, zipStream);
			saveSchedule(pipeline, zipStream);
			// save jar and dpu files
			HashSet<Long> savedTemplateId = new HashSet<>();
			for (Node node : pipeline.getGraph().getNodes()) {
				final DPUInstanceRecord dpu = node.getDpuInstance();
				final DPUTemplateRecord template = dpu.getTemplate();
				if (savedTemplateId.contains(template.getId())) {
					// already saved				
				} else {
					savedTemplateId.add(template.getId());
					// copy data
					saveDPUJar(template, zipStream);
					saveDPUDataUser(template, user, zipStream);
					saveDPUDataGlobal(template, zipStream);
				}
			}
		} catch (IOException ex) {
			targetFile.delete();
			throw new TransferException(
					"Failed to prepare file with exported pipeline", ex);
		} catch (TransferException ex) {
			targetFile.delete();
			throw ex;
		}
	}

	/**
	 * Serialise pipeline into zip stream.
	 *
	 * @param pipeline
	 * @param zipStream
	 * @throws TransferException
	 */
	private void savePipeline(Pipeline pipeline, ZipOutputStream zipStream)
			throws TransferException {
		final XStream xStream = JPAXStream.createForPipeline();
		try {
			final ZipEntry ze = new ZipEntry(PIPELINE_ENTRY);
			zipStream.putNextEntry(ze);
			// write into entry
			xStream.toXML(pipeline, zipStream);
		} catch (IOException ex) {
			throw new TransferException("Failed to serialize pipeline.", ex);
		}
	}

	/**
	 * Serialise all schedule that are visible to current used into given zip
	 * stream.
	 *
	 * @param pipeline
	 * @param zipStream
	 * @throws TransferException
	 */
	private void saveSchedule(Pipeline pipeline, ZipOutputStream zipStream)
			throws TransferException {
		final XStream xStream = JPAXStream.createForSchedule();
		final List<Schedule> schedules = scheduleFacade
				.getSchedulesFor(pipeline);
		try {
			final ZipEntry ze = new ZipEntry(SCHEDULE_ENTRY);
			zipStream.putNextEntry(ze);
			// write into entry
			xStream.toXML(schedules, zipStream);
		} catch (IOException ex) {
			throw new TransferException("Failed to serialize schedule.", ex);
		}
	}

	/**
	 * Save jar file for given DPU into subdirectory in given directory.
	 *
	 * @param template
	 * @param zipStream
	 * @throws cz.cuni.mff.xrg.odcs.commons.app.dpu.transfer.TransferException
	 */
	private void saveDPUJar(DPUTemplateRecord template,
			ZipOutputStream zipStream)
			throws TransferException {
		// we copy the structure in dpu directory
		final File source;
		try {
			source = resourceManager.getDPUJarFile(template);
		} catch (MissingResourceException ex) {
			throw new TransferException("Failed to get path to jar file.");
		}
		byte[] buffer = new byte[4096];
		try {
			final ZipEntry ze = new ZipEntry(DPU_JAR_ENTRY + File.separator
					+ template.getJarPath());
			zipStream.putNextEntry(ze);
			// move jar file into the zip file
			try (FileInputStream in = new FileInputStream(source)) {
				int len;
				while ((len = in.read(buffer)) > 0) {
					zipStream.write(buffer, 0, len);
				}
			}
		} catch (IOException ex) {
			throw new TransferException("Failed to copy jar file.", ex);
		}
	}

	/**
	 * Export DPU's user-based data into given zip stream.
	 *
	 * @param template
	 * @param user
	 * @param zipStream
	 * @throws TransferException
	 */
	private void saveDPUDataUser(DPUTemplateRecord template, User user,
			ZipOutputStream zipStream) throws TransferException {
		final File source;
		try {
			source = resourceManager.getDPUDataUserDir(template, user);
		} catch (MissingResourceException ex) {
			throw new TransferException("Failed to get path to jar file.");
		}

		final String zipPrefix = DPU_DATA_USER_ENTRY + File.separator
				+ template.getJarDirectory();

		saveDirectory(source, zipPrefix, zipStream);
	}

	/**
	 * Export DPU's global data into given zip stream.
	 *
	 * @param template
	 * @param zipStream
	 * @throws TransferException
	 */
	private void saveDPUDataGlobal(DPUTemplateRecord template,
			ZipOutputStream zipStream) throws TransferException {
		final File source;
		try {
			source = resourceManager.getDPUDataGlobalDir(template);
		} catch (MissingResourceException ex) {
			throw new TransferException("Failed to get path to jar file.");
		}

		final String zipPrefix = DPU_DATA_GLOBAL_ENTRY + File.separator
				+ template.getJarDirectory();

		saveDirectory(source, zipPrefix, zipStream);
	}

	/**
	 * Add files and directories from given directory into a zip. Relative path
	 * from the given directory is used to identify the relative path in zip.
	 *
	 * @param source
	 * @param targetPrefix Path prefix in output zip, it should not end with
	 *                     separator.
	 * @param zipStream
	 * @throws TransferException
	 */
	private void saveDirectory(File source, String targetPrefix,
			ZipOutputStream zipStream) throws TransferException {
		if (!source.exists()) {
			// nothing to export
			LOG.trace("Skipping '{}' as it does not exist.", source.toString());
			return;
		}
		LOG.trace("Copy '{}' under '{}'.", source.toString(), targetPrefix);

		// no we add files into the 
		byte[] buffer = new byte[4096];
		final int sourceLenght;
		try {
			sourceLenght = source.getCanonicalPath().length() + 1;
		} catch (IOException ex) {
			throw new TransferException("Failed to get canonical path.", ex);
		}

		final Collection<File> files = FileUtils.listFiles(source,
				TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

		for (File file : files) {
			if (!file.isFile()) {
				// not a file -> skip
				continue;
			}
			try {
				// prepare relative path in archive
				final String relativePath = targetPrefix + File.separator
						+ file.getCanonicalPath().substring(sourceLenght);
				// ...
				final ZipEntry ze = new ZipEntry(relativePath);
				zipStream.putNextEntry(ze);
			} catch (IOException ex) {
				throw new TransferException("Preparation of zip entry failed",
						ex);
			}
			// transfer data
			try (FileInputStream in = new FileInputStream(file)) {
				int len;
				while ((len = in.read(buffer)) > 0) {
					zipStream.write(buffer, 0, len);
				}
			} catch (IOException ex) {
				throw new TransferException("Failed to add file into archive.",
						ex);
			}
		}
	}

}
