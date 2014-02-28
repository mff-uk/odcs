package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;
import org.apache.log4j.Logger;

/**
 * Upload selected file to template directory
 *
 * @author Maria Kukhar
 *
 */
public class FileUploadReceiver implements Receiver {

	private Logger logger = Logger.getLogger(FileUploadReceiver.class);

	private File file;

	private Path path;

	/**
	 * return an OutputStream
	 * 
	 * @param MIMEType 
	 */
	@Override
	public OutputStream receiveUpload(final String filename,
			final String MIMEType) {

		try {
			//create template directory
			path = Files.createTempDirectory("jarDPU");
		} catch (IOException ex) {
			logger.debug(ex);
		}

		try {
			file = new File("/" + path + "/" + filename);
			FileOutputStream fstream = new FileOutputStream(file);

			return fstream;

		} catch (FileNotFoundException e) {
			new Notification("Could not open file<br/>", e.getMessage(),
					Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
			return null;
		}

	}

	/**
	 * Get uploaded file.
	 *
	 * @return uploaded file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Get path to file.
	 *
	 * @return path to file
	 */
	public Path getPath() {
		return path;
	}
}
