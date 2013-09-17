package cz.cuni.xrg.intlib.backend.module;

import static java.nio.file.StandardWatchEventKinds.*;

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
			key = dir.register(watcher, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
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
		// change events come in couples .. we need to keep 
		// evidence about the first one till the second one arrives
		// this is done by saving their id == path 
		HashSet<Path> changeEventHolder = new HashSet();
		// the change event come also after create, we need to ignore this events
		HashSet<Path> createEventHolder = new HashSet();
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
			LOG.debug("New key is here");
			// iterate over events
			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind kind = event.kind();
				LOG.debug("Processing event ...");
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
					LOG.debug("Detected change on: {} count: {}",
							dpuRelativePath.toString(), pathEvent.count());
					/*
					if (createEventHolder.contains(dpuRelativePath)) {
						// we wait for this .. as the message after ENTRY_CREATE
						// it says that the file creation is over
						// this information has no value for us, ignore it
						createEventHolder.remove(dpuRelativePath);
						continue;
					}
					
					if (pathEvent.count() == 1) {
						// the change began, we have to wait for second message
						if (changeEventHolder.contains(dpuRelativePath)) {
							// second message arrived
							changeEventHolder.remove(dpuRelativePath);
							// modification completed .. 
							LOG.debug("Detected change on: {}",
									dpuRelativePath.toString());
							eventPublisher.publishEvent(new DPURefreshEvent(this,
									dpuRelativePath.toString()));								
						} else {
							// store and wait
							changeEventHolder.add(dpuRelativePath);
						}						
					} else { // 2++
						// the change is already done .. 
						LOG.debug("Detected change on: {}",
								dpuRelativePath.toString());
						// something has been modified
						// ask module facade to update the instance
						eventPublisher.publishEvent(new DPURefreshEvent(this,
								dpuRelativePath.toString()));														
					}*/
				} else if (kind == ENTRY_CREATE) {
					createEventHolder.add(dpuRelativePath);
					LOG.debug("Detected create on: {} count: {}",
							dpuRelativePath.toString(), pathEvent.count());
				} else if (kind == ENTRY_DELETE) {
					// ignore
					LOG.debug("Detected delete on: {} count: {}",
							dpuRelativePath.toString(), pathEvent.count());
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
