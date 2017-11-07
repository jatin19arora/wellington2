package com.sapient.wellington.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sapient.wellington.ApplicationConstants;
import com.sapient.wellington.pojo.GroupOrder;

/**
 * class  to create the orderWarningCache by running rules or order data.
 *
 */
@Component
public class RuleGenerator implements Runnable {

	@Autowired
	GroupingService groupService;

	@Autowired
	BlockingQueue<String> queue;

	@Autowired
	List<String> groupOrderWarningCache;

	@Override
	public void run() {
		while (true) {
			try {
				String str = (String) queue.take();
				//start processing if message is received
				if (str.equals(ApplicationConstants.START)) {
					List<GroupOrder> list = new ArrayList<GroupOrder>(groupService.getGroupOrderDataCache().values());
					runRulesOnOrders(list);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * method to process rules to find discrepancies
	 * @param groupOrders
	 */
	private void runRulesOnOrders(List<GroupOrder> groupOrders) {
		//run rule to find if securityId is same and transactiontype is BUY/SELL
		Map<Integer, List<GroupOrder>> ordersGroupedBySecurityId = groupOrders.stream()
				.collect(Collectors.groupingBy(GroupOrder::getSecurityId));
		for (Entry<Integer, List<GroupOrder>> entry : ordersGroupedBySecurityId.entrySet()) {
			if (checkContraTransactions(entry.getValue())) {
				buildWarningCache(entry.getValue(), ApplicationConstants.MessageType.CONTRA);
			}
		}
		//run rule to find if quantity is same and same order instruction
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

	private void setMessage(List<GroupOrder> orders, StringBuilder message) {
		for (int i = 0; i < orders.size(); i++) {
			message.append(ApplicationConstants.MessageType.GROUPED_ORDER).append(orders.get(i).getId());
			if (i != orders.size() - 1) {
				message.append(ApplicationConstants.MessageType.AND_DELIMITER);
			}

		}
		groupOrderWarningCache.add(message.toString());
	}

	private boolean checkContraTransactions(List<GroupOrder> list) {
		return list.stream().collect(Collectors.groupingBy(GroupOrder::getTransactionType)).size() > 1 ? true : false;
	}

	private boolean checkSameQuanityAndInstructionTransactions(List<GroupOrder> list) {
		return list.stream().collect(Collectors.groupingBy(GroupOrder::getOrderInstruction)).size() < 1 ? true : false;
	}

}
