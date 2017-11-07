package com.sapient.wellington.filereader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sapient.wellington.ApplicationConstants;
import com.sapient.wellington.pojo.GroupOrder;
import com.sapient.wellington.pojo.Order;
import com.sapient.wellington.pojo.TransactionType;
import com.sapient.wellington.service.GroupingService;
import com.sapient.wellington.service.RuleGenerator;

/**
 * File Reader utility which parses the file contents to {@link Order} objects
 * The Order Objects are then processed to obtain Group Orders.
 */
@Component
public class CSVFileReader implements FileReader {
	
	private static String DIR_PATH="C:/Files";

	@Autowired
	GroupingService groupingService;

	ExecutorService groupingExecutor;
	
	@Autowired
	BlockingQueue<String> queue;
	
	@Autowired
	RuleGenerator ruleGenerator;

	@PostConstruct
	public void watchFiles() {
		groupingExecutor = Executors.newFixedThreadPool(10);
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
  	 * Thread to handle the processing when new file is uploaded to a directory 
	 */
	private class WatchQueueReader implements Runnable {
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
							//readFile(1, DIR_PATH +"/"+ event.context().toString());
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

	/**
	 *read the files from the command line argument --file.path
	 */
	@Override
	public  synchronized void readFile(int noOfFiles, String filePath) throws Exception {
		String[] filesPath = null;
		if (!filePath.isEmpty()) {
			filesPath = filePath.split(",");
		}

		//check if count of files is equal to actual files
		if (filesPath.length != noOfFiles) {
			throw new Exception("no of files are incorrect,program aborting");
		}

		for (int i = 0; i < filesPath.length; i++) {
			File file = new File(filesPath[i]);
			Scanner s = new Scanner(file);
			s.nextLine();
			while (s.hasNextLine()) {
				String line = s.nextLine();
				Order order = parseCSVToOrder(line);
				groupingExecutor.submit(() -> {
					System.out.println("Thread " + Thread.currentThread().getName() + " executing the task for order="
							+ order.getOrderId());
					groupingService.processOrderForGrouping(order);
				});
			}

		}
		groupingExecutor.shutdown();
		boolean orderDataCreated = groupingExecutor.awaitTermination(15, TimeUnit.SECONDS);
		//when orders are processed, then send message to consumer to begin processing.
		if (orderDataCreated) {
			 queue.put(ApplicationConstants.START);
		     new Thread(ruleGenerator).start();
		}
		
		
	}

	/**
	 * method to parse the content to order domain object
	 * 
	 * @param line
	 * @return
	 */
	private synchronized Order parseCSVToOrder(String line) {
		Order order = null;
		String[] arr = null;
		if (line != null && !line.isEmpty()) {
			try {
				arr = line.split(",");
				order = new Order();
				order.setOrderId(new Integer(arr[0]));
				order.setSecurityId(new Integer(arr[1]));
				order.setTransactionType(TransactionType.valueOf(arr[2]));
				order.setSymbol(arr[3]);
				order.setQuantity(new Integer(arr[4]));
				order.setLimitPrice(new Double(arr[5].toString()));
				order.setOrderInstruction(arr[6]);
			}

			catch (NumberFormatException ex) {
				System.out.println("exception in parsing the data for order" + ex.getMessage());
			}
		}
		return order;
	}
	
}
