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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacadeConfig;
import cz.cuni.xrg.intlib.commons.app.module.event.DPURefreshEvent;

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
public class FileNotifierServer implements Runnable {

	public static final String NOTIFICATION_EXT = ".notifi";

	private static final Logger LOG = LoggerFactory
			.getLogger(FileNotifierClient.class);

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	/**
	 * Used to get information about modules.
	 */
	@Autowired
	private ModuleFacadeConfig moduleConfig;

	/**
	 * Module facade to notify about changes.
	 */
	@Autowired
	private ModuleFacade moduleFacade;

	private WatchService watcher;

	private Map<WatchKey, Path> keys;

	private Thread watcherThread;
	
	public FileNotifierServer() {
		this.watcher = null;
		this.keys = new HashMap();
		this.watcherThread = null;
	}

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
		register(Paths.get(moduleConfig.getDpuFolder()));
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
	 * Register given directory for watching.
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
		LOG.debug("Watcher registred for directory: '{}'", dir.toString());
		keys.put(key, dir);
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
					// new file .. check for
					if (eventPath.getFileName().toString()
							.endsWith(NOTIFICATION_EXT)) {
						// get path to the notification file and delete it
						File notifyFile = new File(dir.toFile(), eventPath.toString());

						boolean fileDeleted = true;
						try {
							fileDeleted = notifyFile.delete();
							// we use Exception as it can throw IOException as
							// well
						} catch (Exception e) {
							LOG.debug("Failed to delete notification file.", e);
						}

						if (fileDeleted) {
							// ok done
						} else {
							// log exception
							LOG.debug("Failed to delete notification file");
						}

						// we need name of the file
						final int notifiExtLen = NOTIFICATION_EXT.length();
						String dpuJarFile = eventPath.getFileName().toString();
						dpuJarFile = dpuJarFile.substring(0,
								dpuJarFile.length() - notifiExtLen);

						LOG.debug("Detected change on: {}", dpuJarFile);
						// publish the message
						eventPublisher.publishEvent(new DPURefreshEvent(this,
								dpuJarFile));
					}
				}
			}

			boolean valid = key.reset();
			if (valid) {
				// everything is all right
			} else {
				// it is broken ... the directory may be deleted
				keys.remove(key);
				LOG.warn("Key for: '{}' is no longer valid.", dir.toString());
			}
		}
	}
}
