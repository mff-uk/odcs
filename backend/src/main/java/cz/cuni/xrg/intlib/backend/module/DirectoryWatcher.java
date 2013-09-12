package cz.cuni.xrg.intlib.backend.module;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

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

import cz.cuni.xrg.intlib.backend.module.event.DPURefreshEvent;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacadeConfig;

public class DirectoryWatcher implements Runnable {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private ModuleFacadeConfig moduleFacadeConfig;

	private WatchService watcher;

	private Map<WatchKey, Path> keys;

	private final static Logger LOG = LoggerFactory
			.getLogger(DirectoryWatcher.class);

	public void start() {
		try {
			watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			LOG.error("Failed to start DPU change watcher.", e);
			return;
		}
		keys = new HashMap<WatchKey, Path>();
		// register directories to watch
		register(Paths.get(moduleFacadeConfig.getDpuFolder()));
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
			key = dir.register(watcher, ENTRY_MODIFY);
		} catch (IOException e) {
			LOG.error("Failed to register watcher for directory: {}",
					dir.toString(), e);
			return;
		}
		LOG.debug("Watcher registred for directory: '{}'", dir.toString());
		keys.put(key, dir);
	}

	@Override
	public void run() {
		// start service
		start();
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
				LOG.warn("WatchKey not recognized!!");
				continue;
			}

			// iterate over events
			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind kind = event.kind();

				// for lost or discarded events
				if (kind == OVERFLOW) {
					continue;
				}
				// otherwise recast
				@SuppressWarnings("unchecked")
				WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
				// here we get path relative to the registered
				// in our case DPU's relative path
				Path dpuRelativePath = pathEvent.context();
				if (kind == ENTRY_MODIFY) {
					LOG.debug("Detected DPU's change on: {}",
							dpuRelativePath.toString());
					// something has been modified
					// ask module facade to update the instance
					eventPublisher.publishEvent(new DPURefreshEvent(this,
							dpuRelativePath.toString()));
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
