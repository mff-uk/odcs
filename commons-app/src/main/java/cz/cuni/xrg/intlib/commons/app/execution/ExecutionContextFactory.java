package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;

/**
 * Factory for execution context.
 * 
 * @author Petyr
 *
 */
public class ExecutionContextFactory {

	private ExecutionContextFactory() {}	
	
	/**
	 * Create new context manager and return write access.
	 * @param directory The root directory of context.
	 */
	public static ExecutionContextWriter createNew(File directory) {		
		return new ExecutionContextImpl(directory);
	}
	
	/**
	 * Restore execution context from given directory and return write 
	 * access. If the file does't exist the FileNotFoundException is thrown.
	 * @param directory The root directory of context.
	 * @return Null if the loading failed.
	 * throws FileNotFoundException
	 */
	public static ExecutionContextWriter restoreAsWrite(File directory) throws FileNotFoundException {
		ExecutionContextWriter context = null;
		
		try {
			context = restore(directory);
		} catch (JAXBException e) {
			Logger.getLogger(ExecutionContextFactory.class).error(e);
		}
		
		return context;
	}	
	
	/**
	 * Restore execution context from given directory and return read 
	 * only access. If the file does't exist the FileNotFoundException is thrown.
	 * @param directory The root directory of context.
	 * @return Null if the loading failed.
	 * throws FileNotFoundException
	 */
	public static ExecutionContextReader restoreAsRead(File directory) throws FileNotFoundException {
		ExecutionContextReader context = null;
		
		try {
			context = restore(directory);
		} catch (JAXBException e) {
			Logger.getLogger(ExecutionContextFactory.class).error(e);
		}
		
		return context;
	}
	
	/**
	 * Delete context in given directory. In case of any problem throws. 
	 * @param directory The root directory of context.
	 * @throws Exception
	 */
	public static void delete(File directory) throws Exception {
		FileUtils.deleteDirectory(directory);		
	}
	
	/**
	 * Restore execution context from given directory.
	 * If the file does't exist the FileNotFoundException is thrown.
	 * @param directory The root directory of context.
	 * @return
	 * @throws FileNotFoundException
	 * @throws JAXBException In case of wrong format.
	 */	
	private static ExecutionContextImpl restore(File directory) throws FileNotFoundException, JAXBException {
		ExecutionContextImpl exec = new ExecutionContextImpl(directory);
		// get file
		File file = exec.getloadFilePath();
		if (!file.exists()) {
			// file not exist .. 
			throw new FileNotFoundException();
		}
		JAXBContext jaxbContext = JAXBContext.newInstance(ExecutionContextImpl.class, DPUContextInfo.class); 
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		// load ..
		exec = (ExecutionContextImpl) jaxbUnmarshaller.unmarshal(file);
		// set working directory (previous was lost)
		exec.setWorkingDirectory(directory);		
		return exec;
	}
}
