package cz.cuni.xrg.intlib.commons.app.module.impl;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import cz.cuni.xrg.intlib.commons.app.dpu.event.DPURefreshEvent;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;

/**
 * Should be used with cooperation with {@link FileNotifierClient}. Use
 * separated thread in order to watch directory for notification files.
 * 
 * Require Spring @Scheduled and @Async annotations support in Spring's
 * context.xml file.
 * 
 * @author Petyr
 * 
 */
class FileNotifierServer implements Runnable {

	public static final String NOTIFICATION_EXT = "notifi";

	private static final Logger LOG = LoggerFactory
			.getLogger(FileNotifierClient.class);

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	/**
	 * Used to get information about modules.
	 */
	@Autowired
	private ModuleFacade moduleFacade;

	private WatchService watcher = null;

	private Map<WatchKey, Path> keys = new HashMap<>();

	private Thread watcherThread = null;
	
	/**
	 * Contains names of directories 
	 * in which first notification will be ignored.
	 */
	private Set<String> toIgnore = new HashSet<>();
	
	/**
	 * Start watching for DPUs changes.
	 */
	public void start() {
		if (watcherThread == null) {
			// continue
		} else {
			// already started
			LOG.warn("Duplicit start. Second start ignored.");
			return;
		}
		
		try {
			watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			LOG.error("Failed to start DPU change watcher.", e);
			return;
		}
		// register directories to watch
		register(Paths.get(moduleFacade.getDPUDirectory()));
		// start watching .. the function run asynchronously
		watcherThread = new Thread(this);
		watcherThread.start();
	}

	/**
	 * Terminates watcher.
	 */
	public void stop() {
		watcherThread.interrupt();
	}

	/**
	 * Ignore first change in given directory.
	 * @param directory
	 */
	public void ignore(String directory) {
		toIgnore.add(directory);
	}
	
	/**
	 * Register given directory and all sub-directories for watching.
	 * 
	 * @param dir
	 * @throws IOException
	 */
	private void register(Path dir) {
		WatchKey key = null;
		try {
			key = dir.register(watcher, ENTRY_CREATE);
		} catch (IOException e) {
			LOG.error("Failed to register watcher for directory: {}",
					dir.toString(), e);
			return;
		}
		LOG.debug("File watcher registered for: {}", dir.toString());
		keys.put(key, dir);
		// now sub directories
		File []files = (new File(dir.toString())).listFiles();
		if (files == null) {
			// empty directory
		} else {
			// call for sub-directories
			for (File item : files) {
				if (item.isDirectory()) {
					register(Paths.get(item.toString()));
				}
			}
		}
	}

	/**
	 * React on create event in given directory.
	 * @param dir
	 * @param eventPath
	 */
	private void onCreate(Path dir, Path eventPath) {
		if (eventPath.getFileName().toString().endsWith(NOTIFICATION_EXT)) {
			// DPU change notification
			
			// delete the notification file
			File notifyFile = new File(dir.toFile(), eventPath.toString());			
			try {
				if(!notifyFile.delete()) {
					LOG.debug("Failed to delete notification file.");
				}
				// we use Exception as it can throw IOException as
				// well
			} catch (Exception e) {
				LOG.debug("Failed to delete notification file.", e);
			}
			
			// get the directory
			String directoryName = dir.getFileName().toString();
			// get new DPUs file
			String jarName = FilenameUtils.removeExtension(eventPath.getFileName().toString());
			
			if (toIgnore.contains(directoryName)) {
				// we have orders to ignore this
				toIgnore.remove(directoryName);
				LOG.debug("Ignoring notification in: {}", directoryName);
				return;
			}
			
			LOG.debug("Notification in: {} new DPU: {}", directoryName, jarName);
			// publish event about notification
			eventPublisher.publishEvent(
					new DPURefreshEvent(this, directoryName, jarName));
		} else {
			if (eventPath.toFile().isDirectory()) {
				// new directory .. register !
				register(dir.toAbsolutePath());
			}
		}
	}
	
	/**
	 * The main function here we watch for changes.
	 */
	@Override
	public void run() {
		LOG.info("DPU's change watcher is running ... ");
		// wait for event
		for (;;) {
			if (Thread.interrupted()) {
				// if yes finish
				return;
			}

			WatchKey key = null;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				// stop the execution
				return;
			}
			// get the path for the event
			Path dir = keys.get(key);
			if (dir == null) {
				LOG.warn("WatchKey not recognized!");
				continue;
			}
			// iterate over events
			for (WatchEvent<?> event : key.pollEvents()) {
				final WatchEvent.Kind kind = event.kind();
				// for lost or discarded events
				if (kind == OVERFLOW) {
					continue;
				}
				// otherwise recast
				@SuppressWarnings("unchecked")
				final WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
				// here we get path relative to the registered
				// in our case DPU's relative path
				final Path eventPath = pathEvent.context();
				if (kind == ENTRY_CREATE) {
					onCreate(dir, eventPath);					
				}
			}
			boolean valid = key.reset();
			if (valid) {
				// everything is all right
			} else {
				// it is broken ... the directory may be deleted
				keys.remove(key);
				LOG.debug("Key for: '{}' is no longer valid.", dir.toString());
			}
		}
	}

}
