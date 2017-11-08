package com.sapient.wellington.service;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sapient.wellington.pojo.GroupOrder;
import com.sapient.wellington.pojo.Order;
import com.sapient.wellington.pojo.OrderKey;

/**
 * service class for order grouping and processing.
 * 
 * @author jaro13
 *
 */
@Service
public class GroupingService implements IGroupingService {
	private static final Logger logger = LoggerFactory.getLogger(GroupingService.class);

	/**
	 * map for key as same securityId,TransactionType and symbol and GroupOrder as
	 * value
	 */
	@Autowired
	Map<OrderKey, GroupOrder> groupOrderDataCache;

	/**
	 * method for grouping of orders based on same securityId,Symbol and
	 * transactionType.
	 */
	@Override
	public void processOrderForGrouping(Order order) {
		OrderKey mapKey = new OrderKey(order.getSecurityId(), order.getSymbol(), order.getTransactionType());
		GroupOrder groupOrder = null;
		// check if order already exists for the key,
		// perform grouping
		if (groupOrderDataCache.containsKey(mapKey)) {
			groupOrder = groupOrderDataCache.get(mapKey);
			groupOrder.setQuantity(groupOrder.getQuantity() + order.getQuantity());
			groupOrder.setInsertTime(new Date());
			StringBuffer buffer = new StringBuffer(groupOrder.getOrderInstruction());
			groupOrder.setOrderInstruction(buffer.append(",").append(order.getOrderInstruction()).toString());
			groupOrder.setAvgLimitprice(groupOrder.getAvgLimitprice() + order.getLimitPrice() / order.getQuantity());
		}
		// create the group order if is not present already.
		else {
			groupOrder = new GroupOrder();
			groupOrder.setQuantity(order.getQuantity());
			groupOrder.setAvgLimitprice(order.getLimitPrice());
			groupOrder.setOrderInstruction(order.getOrderInstruction());
			groupOrder.setInsertTime(new Date());

		}
		// common values
		groupOrder.setSecurityId(order.getSecurityId());
		groupOrder.setTransactionType(order.getTransactionType());
		groupOrder.setSymbol(order.getSymbol());
		groupOrderDataCache.put(mapKey, groupOrder);
		logger.info("group order entry created with securityId=" + mapKey.getSecurityId() + ", transactionType= "
				+ mapKey.getTransactionType()+", symbol="+mapKey.getSymbol()+"group order id="+groupOrder.getId());
		logger.info("group orders size="+groupOrderDataCache.size());
	}

	public Map<OrderKey, GroupOrder> getGroupOrderDataCache() {
		return groupOrderDataCache;
	}

	public void setGroupOrderDataCache(Map<OrderKey, GroupOrder> groupOrderDataCache) {
		this.groupOrderDataCache = groupOrderDataCache;
	}
}
