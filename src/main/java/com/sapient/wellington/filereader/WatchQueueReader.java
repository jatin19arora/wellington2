package com.sapient.wellington.filereader;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

/**
 * Thread to handle the processing when new file is uploaded to a directory
 */
public class WatchQueueReader implements Runnable {
	/** the watchService that is passed in from above */
	private WatchService watcher;

	public WatchQueueReader(WatchService myWatcher) {
		this.watcher = myWatcher;
	}

	@Override
	public void run() {
		try {
			WatchKey watchKey = watcher.take();
			while (watchKey != null) {
				List<WatchEvent<?>> events = watchKey.pollEvents();
				for (WatchEvent event : events) {
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
						// readFile(1, DIR_PATH +"/"+ event.context().toString());
					}
				}
				watchKey.reset();
				watchKey = watcher.take();
			}

		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}

	}

}