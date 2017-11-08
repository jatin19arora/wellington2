package com.sapient.wellington;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sapient.wellington.filereader.FileReader;
import com.sapient.wellington.util.ApplicationConstants;

/**
 * Main class for the application
 */
@SpringBootApplication
public class WellingtonApplication implements ApplicationRunner {

	@Autowired
	private FileReader fileReader;

	private static final Logger logger = LoggerFactory.getLogger(WellingtonApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WellingtonApplication.class, args);
	}

	/**
	 * Run method to read the command line arguments as input of application
	 * --file.count=3 --file.path="C:/files/File1.csv,C:/files/File2.csv"
	 */
	@Override
	public void run(ApplicationArguments args) {
		logger.debug("Starting the application....");
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
		// Read the file and begin execution
		try {
			fileReader.readFile(noOfFiles, filePath);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			logger.info("Application stopped..");
			System.exit(10);
		}
	}

}
