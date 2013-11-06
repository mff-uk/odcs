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

/**
 * Upload selected file to template directory
 *
 * @author Maria Kukhar
 *
 */
public class FileUploadReceiver implements Receiver {

	private static final long serialVersionUID = 5099459605355200117L;
	private File file;
	private FileOutputStream fstream = null;
	private Path path;
	private String fName;

	/**
	 * return an OutputStream
	 */
	@Override
	public OutputStream receiveUpload(final String filename,
			final String MIMEType) {
		fName = filename;

		OutputStream fos;

		try {
			//create template directory
			path = Files.createTempDirectory("jarDPU");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			file = new File("/" + path + "/" + filename);
			fstream = new FileOutputStream(file);
			fos = new OutputStream() {
				private static final int searchedByte = '\n';

				@Override
				public void write(final int b) throws IOException {
					fstream.write(b);
				}

				@Override
				public void write(byte b[], int off, int len) throws IOException {
					if (b == null) {
						throw new NullPointerException();
					} else if ((off < 0) || (off > b.length) || (len < 0)
							|| ((off + len) > b.length) || ((off + len) < 0)) {
						throw new IndexOutOfBoundsException();
					} else if (len == 0) {
						return;
					}
					fstream.write(b, off, len);
				}

				@Override
				public void close() throws IOException {
					fstream.close();
					super.close();
				}
			};

		} catch (FileNotFoundException e) {
			new Notification("Could not open file<br/>", e.getMessage(),
					Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
			return null;
		}
		return fos;
	}
	
	public File getFile() {
		return file;
	}
	
	public Path getPath() {
		return path;
	}
}
