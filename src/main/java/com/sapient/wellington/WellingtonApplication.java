package com.sapient.wellington;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.sapient.wellington.filereader.FileReader;

/**
 *Main class for the application 
 */
@SpringBootApplication
public class WellingtonApplication implements ApplicationRunner {
	@Autowired
	private FileReader fileReader;

	private static final Logger logger = LoggerFactory.getLogger(WellingtonApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WellingtonApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		int noOfFiles = 0;
		String filePath = null;
		for (String name : args.getOptionNames()) {
			if (name.equals(ApplicationConstants.FILE_COUNT)) {
				try {
					noOfFiles = Integer.parseInt(args.getOptionValues(ApplicationConstants.FILE_COUNT).get(0));
				}

				catch (NumberFormatException ex) {
					logger.debug("exception in parsing argument file.path" + ex.getMessage());
				}
			}

			if (name.equals(ApplicationConstants.FILE_PATH)) {
				filePath = args.getOptionValues(ApplicationConstants.FILE_PATH).get(0).toString();
			}

		}
		//Read the file and begin execution
		fileReader.readFile(noOfFiles, filePath);
	}
	
	/**
	 * bean for array blocking queue
	 * @return
	 */
	@Bean(name="queue")
    public BlockingQueue<String> blockingQueue() {
        return new ArrayBlockingQueue<String>(1);
    }
	
	/**
	 * bean for group order cache
	 * @return
	 */
	@Bean(name="groupOrderWarningCache")
    public List<String> orderWarningCache() {
		return new CopyOnWriteArrayList<String>();
    }
}
