package com.sapient.wellington.filereader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sapient.wellington.pojo.Order;
import com.sapient.wellington.pojo.TransactionType;

@Component
public class OrderParser {
	private static final Logger logger = LoggerFactory.getLogger(OrderParser.class);
	/**
	 * method to parse the content to order domain object
	 * 
	 * @param line
	 * @return
	 */
	public synchronized Order parseCSVToOrder(String line) {
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
				logger.debug("exception in parsing the data for order" + ex.getMessage());
			}
		}
		return order;
	}
}
