package cz.cuni.mff.xrg.odcs.dpu.filestolocaldirectoryloader;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class FilesToLocalDirectoryLoaderConfigDialog extends
		BaseConfigDialog<FilesToLocalDirectoryLoaderConfig> {
	/**
     * 
     */
	private static final long serialVersionUID = -5668436075836909428L;

	private static final String DESTINATION_LABEL = "Destination directory absolute path";

	private static final String MOVE_FILES_LABEL = "Move files instead of copy";

	private ObjectProperty<String> destination = new ObjectProperty<String>("");

	private ObjectProperty<Boolean> moveFiles = new ObjectProperty<Boolean>(
			false);

	public FilesToLocalDirectoryLoaderConfigDialog() {
		super(FilesToLocalDirectoryLoaderConfig.class);
		initialize();
	}

	private void initialize() {
		FormLayout mainLayout = new FormLayout();

		// top-level component properties
		setWidth("100%");
		setHeight("100%");
		mainLayout.addComponent(new TextField(DESTINATION_LABEL, destination));
		mainLayout.addComponent(new CheckBox(MOVE_FILES_LABEL, moveFiles));

		setCompositionRoot(mainLayout);
	}

	@Override
	public void setConfiguration(FilesToLocalDirectoryLoaderConfig conf)
			throws ConfigException {
		destination.setValue(conf.getDestination());
		moveFiles.setValue(conf.isMoveFiles());
	}

	@Override
	public FilesToLocalDirectoryLoaderConfig getConfiguration()
			throws ConfigException {
		FilesToLocalDirectoryLoaderConfig conf = new FilesToLocalDirectoryLoaderConfig();
		conf.setDestination(destination.getValue());
		conf.setMoveFiles(moveFiles.getValue());
		return conf;
	}

}
