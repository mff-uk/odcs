package cz.cuni.mff.xrg.odcs.dpu.filestovfsloader;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import eu.unifiedviews.dpu.config.DPUConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class FilesToVFSLoaderConfigDialog extends
		BaseConfigDialog<FilesToVFSLoaderConfig> {
	/**
     * 
     */
	private static final long serialVersionUID = -5668436075836909428L;

	private static final String DESTINATION_LABEL = "Destination directory URI";

    private static final String USERNAME_LABEL = "Username";

    private static final String PASSWORD_LABEL = "Password";

	private static final String MOVE_FILES_LABEL = "Move files instead of copy";
	
	private static final String REPLACE_EXISTING_LABEL = "Replace existing files";

    private static final String SKIP_ON_ERROR_LABEL = "Skip file on error";
    
    private ObjectProperty<String> destination = new ObjectProperty<String>("");
    private ObjectProperty<String> username = new ObjectProperty<String>("");

    private ObjectProperty<String> password = new ObjectProperty<String>("");

	private ObjectProperty<Boolean> moveFiles = new ObjectProperty<Boolean>(
			false);

	private ObjectProperty<Boolean> replaceExisting = new ObjectProperty<Boolean>(
			false);

    private ObjectProperty<Boolean> skipOnError = new ObjectProperty<Boolean>(
            false);

    public FilesToVFSLoaderConfigDialog() {
		super(FilesToVFSLoaderConfig.class);
		initialize();
	}

	private void initialize() {
		FormLayout mainLayout = new FormLayout();

		// top-level component properties
		setWidth("100%");
		setHeight("100%");
		mainLayout.addComponent(new TextField(DESTINATION_LABEL, destination));
        mainLayout.addComponent(new TextField(USERNAME_LABEL, username));
        mainLayout.addComponent(new TextField(PASSWORD_LABEL, password));
		mainLayout.addComponent(new CheckBox(MOVE_FILES_LABEL, moveFiles));
		mainLayout.addComponent(new CheckBox(REPLACE_EXISTING_LABEL, replaceExisting));
        mainLayout.addComponent(new CheckBox(SKIP_ON_ERROR_LABEL, skipOnError));
		
		setCompositionRoot(mainLayout);
	}

	@Override
	public void setConfiguration(FilesToVFSLoaderConfig conf)
			throws DPUConfigException {
		destination.setValue(conf.getDestination());
        username.setValue(conf.getUsername());
        password.setValue(conf.getPassword());
		moveFiles.setValue(conf.isMoveFiles());
		replaceExisting.setValue(conf.isReplaceExisting());
		skipOnError.setValue(conf.isSkipOnError());
	}

	@Override
	public FilesToVFSLoaderConfig getConfiguration()
			throws DPUConfigException {
		FilesToVFSLoaderConfig conf = new FilesToVFSLoaderConfig();
		conf.setDestination(destination.getValue());
        conf.setUsername(username.getValue());
        conf.setPassword(password.getValue());
		conf.setMoveFiles(moveFiles.getValue());
		conf.setReplaceExisting(replaceExisting.getValue());
		conf.setSkipOnError(skipOnError.getValue());
		return conf;
	}

}
