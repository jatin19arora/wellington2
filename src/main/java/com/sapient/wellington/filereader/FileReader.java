package com.sapient.wellington.filereader;

/**
 * file reader interface for reading file capabilities
 * @author jaro13
 *
 */
public interface FileReader {
	void readFile(int noOfFiles, String filePath) throws Exception;
}
