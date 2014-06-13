package cz.cuni.mff.xrg.odcs.dpu.filestolocaldirectoryloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUCancelledException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsLoader;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit.FilesDataUnitEntry;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit.FilesIteration;

@AsLoader
public class FilesToLocalDirectoryLoader extends
		ConfigurableBase<FilesToLocalDirectoryLoaderConfig> implements
		ConfigDialogProvider<FilesToLocalDirectoryLoaderConfig> {
	private static final Logger LOG = LoggerFactory
			.getLogger(FilesToLocalDirectoryLoader.class);

	@InputDataUnit(name = "fileInput")
	public FilesDataUnit filesInput;

	public FilesToLocalDirectoryLoader() {
		super(FilesToLocalDirectoryLoaderConfig.class);
	}

	@Override
	public void execute(DPUContext dpuContext) throws DPUException,
			DataUnitException, InterruptedException {
		String shortMessage = this.getClass().getSimpleName() + " starting.";
		String longMessage = String.format(
				"Configuration: destination: %d, moveFiles: %s",
				config.getDestination(), config.isMoveFiles());
		dpuContext.sendMessage(MessageType.INFO, shortMessage, longMessage);
		LOG.info(shortMessage + " " + longMessage);

		FilesIteration filesIteration = filesInput.getFiles();
		String destinationAbsolutePath = new File(config.getDestination())
				.getAbsolutePath();
		boolean moveFiles = config.isMoveFiles();
		try {
			while (filesIteration.hasNext()) {
				checkCancelled(dpuContext);

				FilesDataUnitEntry entry = filesIteration.next();
				Path inputPath = new File(entry.getFilesystemURI()).toPath();
				Path outputPath = new File(destinationAbsolutePath + '/'
						+ entry.getSymbolicName()).toPath();
				try {
					if (dpuContext.isDebugging()) {
						LOG.debug("Starting loading of file "
								+ entry.getSymbolicName() + " path URI "
								+ entry.getFilesystemURI());
					}
					if (moveFiles) {
						java.nio.file.Files.move(inputPath, outputPath);
					} else {
						java.nio.file.Files.copy(inputPath, outputPath);
					}
					if (dpuContext.isDebugging()) {
						LOG.debug("Finished loading of file "
								+ entry.getSymbolicName() + " path URI "
								+ entry.getFilesystemURI());
					}
				} catch (IOException ex) {
					dpuContext.sendMessage(MessageType.ERROR,
							"Error when loading.",
							"Symbolic name " + entry.getSymbolicName()
									+ " path URI " + entry.getFilesystemURI(),
							ex);
				}
			}
		} finally {
			filesIteration.close();
		}
	}

	@Override
	public AbstractConfigDialog<FilesToLocalDirectoryLoaderConfig> getConfigurationDialog() {
		return new FilesToLocalDirectoryLoaderConfigDialog();
	}

	private void checkCancelled(DPUContext dpuContext)
			throws DPUCancelledException {
		if (dpuContext.canceled()) {
			throw new DPUCancelledException();
		}
	}
}
