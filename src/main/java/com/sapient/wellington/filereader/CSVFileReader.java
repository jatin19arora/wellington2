package com.sapient.wellington.filereader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.sapient.wellington.pojo.Order;
import com.sapient.wellington.service.GroupingService;
import com.sapient.wellington.service.RuleGenerator;
import com.sapient.wellington.util.ApplicationConstants;

/**
 * File Reader utility which parses the file contents to {@link Order} objects
 * The Order Objects are then processed to obtain Group Orders.
 */
@Component
public class CSVFileReader implements FileReader {
	private static final Logger logger = LoggerFactory.getLogger(CSVFileReader.class);
	private static String DIR_PATH = "C:/Files";

	@Autowired
	GroupingService groupingService;

	@Autowired
	ExecutorService groupingExecutor;

	@Autowired
	BlockingQueue<String> queue;

	@Autowired
	RuleGenerator ruleGenerator;

	@Autowired
	MessageSource messageSource;
	
	@Autowired
	OrderParser orderParser;

	@PostConstruct
	public void watchFiles() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Path myDir = Paths.get(DIR_PATH);
		WatchService watcher = null;
		try {
			watcher = myDir.getFileSystem().newWatchService();
			myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		WatchQueueReader fileWatcher = new WatchQueueReader(watcher);
		executor.execute(fileWatcher);

	}

	/**
	 * read the files from the command line argument --file.path
	 */
	@Override
	public synchronized void readFile(int noOfFiles, String filePath) throws Exception {
		String[] filesPath = null;
		if (!filePath.isEmpty()) {
			filesPath = filePath.split(",");
		}

		// check if count of files is equal to actual files
		if (filesPath.length != noOfFiles) {
			throw new Exception("no of files are incorrect,program aborting");
		}

		//load all the files line by line,put the task to executor for grouping
		for (int i = 0; i < filesPath.length; i++) {
			File file = new File(filesPath[i]);
			Scanner s = new Scanner(file);
			try {
				s.nextLine();
				while (s.hasNextLine()) {
					String line = s.nextLine();
					Order order = orderParser.parseCSVToOrder(line);
					groupingExecutor.submit(() -> {
						logger.info("Thread " + Thread.currentThread().getName()
								+ " executing the task for order=" + order.getOrderId());
						groupingService.processOrderForGrouping(order);
					});
				}
			} finally {
				s.close();
			}
		}
		groupingExecutor.shutdown();
		boolean orderDataCreated = groupingExecutor.awaitTermination(15, TimeUnit.SECONDS);
		// when orders are processed, then send message to consumer to begin processing.
		if (orderDataCreated) {
			logger.info("grouping of orders done,starting executing the rules");
			queue.put(ApplicationConstants.START);
			new Thread(ruleGenerator).start();
		}

	}

}
