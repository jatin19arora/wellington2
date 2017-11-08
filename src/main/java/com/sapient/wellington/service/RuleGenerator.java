package com.sapient.wellington.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sapient.wellington.filereader.CSVFileReader;
import com.sapient.wellington.pojo.GroupOrder;
import com.sapient.wellington.pojo.OrderKey;
import com.sapient.wellington.util.ApplicationConstants;

/**
 * class to create the orderWarningCache by running rules or order data.
 *
 */
@Component
public class RuleGenerator implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RuleGenerator.class);

	@Autowired
	BlockingQueue<String> queue;
	
	@Autowired
	Map<OrderKey, GroupOrder> groupOrderDataCache;

	@Autowired
	List<String> groupOrderWarningCache;

	@Override
	public void run() {
		while (true) {
			try {
				String str = (String) queue.take();
				// start processing if message is received
				if (str.equals(ApplicationConstants.START)) {
					logger.info("Message received, begin generating orderWarningCache");
					List<GroupOrder> list = new ArrayList<GroupOrder>(groupOrderDataCache.values());
					runRulesOnOrders(list);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * method to process rules to find discrepancies
	 * 
	 * @param groupOrders
	 */
	private void runRulesOnOrders(List<GroupOrder> groupOrders) {
		// run rule to find if securityId is same and transactiontype is BUY/SELL
		Map<Integer, List<GroupOrder>> ordersGroupedBySecurityId = groupOrders.stream()
				.collect(Collectors.groupingBy(GroupOrder::getSecurityId));
		for (Entry<Integer, List<GroupOrder>> entry : ordersGroupedBySecurityId.entrySet()) {
			if (checkContraTransactions(entry.getValue())) {
				buildWarningCache(entry.getValue(), ApplicationConstants.MessageType.CONTRA);
			}
		}
		// run rule to find if quantity is same and same order instruction
		Map<Integer, List<GroupOrder>> ordersGroupedByQuantity = groupOrders.stream()
				.collect(Collectors.groupingBy(GroupOrder::getQuantity));
		for (Entry<Integer, List<GroupOrder>> entry : ordersGroupedByQuantity.entrySet()) {
			if (checkSameQuanityAndInstructionTransactions(entry.getValue())) {
				buildWarningCache(entry.getValue(), ApplicationConstants.MessageType.SAME_QUANTITY);
			}
		}

	}

	/**
	 * method to build the orderswarningcache
	 * 
	 * @param orders
	 * @param messageType
	 */
	private void buildWarningCache(List<GroupOrder> orders, String messageType) {
		StringBuilder message = new StringBuilder();
		if (messageType.equals(ApplicationConstants.MessageType.CONTRA)) {
			message.append(ApplicationConstants.MessageType.CONTRA_MESSAGE);
			setMessage(orders, message);
		}

		if (messageType.equals(ApplicationConstants.MessageType.SAME_QUANTITY)) {
			message.append(ApplicationConstants.MessageType.SAME_QUANTITY_MESSAGE);
			setMessage(orders, message);
		}

	}

	/**
	 * method to set the message in cache
	 * 
	 * @param orders
	 * @param message
	 */
	private void setMessage(List<GroupOrder> orders, StringBuilder message) {
		for (int i = 0; i < orders.size(); i++) {
			message.append(ApplicationConstants.MessageType.GROUPED_ORDER).append(orders.get(i).getId());
			if (i != orders.size() - 1) {
				message.append(ApplicationConstants.MessageType.AND_DELIMITER);
			}

		}
		groupOrderWarningCache.add(message.toString());
		logger.info("warning message added to groupOrderWarningCache="+message);
	}

	/**
	 * method to check contra transaction
	 * 
	 * @param list
	 * @return
	 */
	private boolean checkContraTransactions(List<GroupOrder> list) {
		return list.stream().collect(Collectors.groupingBy(GroupOrder::getTransactionType)).size() > 1 ? true : false;
	}

	/**
	 * method to check same quantityAnd Instructions
	 * 
	 * @param list
	 * @return
	 */
	private boolean checkSameQuanityAndInstructionTransactions(List<GroupOrder> list) {
		return list.stream().collect(Collectors.groupingBy(GroupOrder::getOrderInstruction)).size() < 1 ? true : false;
	}

}
