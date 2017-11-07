package com.sapient.wellington.service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.sapient.wellington.pojo.GroupOrder;
import com.sapient.wellington.pojo.Order;
import com.sapient.wellington.pojo.OrderKey;

/**
 * service class for order grouping and processing.
 * @author c.ila
 *
 */
@Service
public class GroupingService implements IGroupingService {

	ExecutorService executor;
	Map<OrderKey, GroupOrder> groupOrderDataCache;

	@PostConstruct
	void init() {
		groupOrderDataCache = new ConcurrentHashMap<OrderKey, GroupOrder>();
		executor = Executors.newFixedThreadPool(10);
		System.out.println("executor created=" + executor);
	}

	/**
	 * method for grouping of orders based on same securityId,Symbol and transactionType.
	 */
	@Override
	public void processOrderForGrouping(Order order) {
		OrderKey mapKey = new OrderKey(order.getSecurityId(), order.getSymbol(), order.getTransactionType());
		GroupOrder groupOrder=null;
		if (groupOrderDataCache.containsKey(mapKey)) {
			groupOrder = groupOrderDataCache.get(mapKey);
			groupOrder.setQuantity(groupOrder.getQuantity() + order.getQuantity());
			groupOrder.setInsertTime(new Date());
			StringBuffer buffer = new StringBuffer(groupOrder.getOrderInstruction());
			groupOrder.setOrderInstruction(buffer.append(",").append(order.getOrderInstruction()).toString());
			groupOrder.setAvgLimitprice(groupOrder.getAvgLimitprice() + order.getLimitPrice() / order.getQuantity());
		} else {
		    groupOrder = new GroupOrder();
			groupOrder.setQuantity(order.getQuantity());
			groupOrder.setAvgLimitprice(order.getLimitPrice());
			groupOrder.setOrderInstruction(order.getOrderInstruction());
			groupOrder.setInsertTime(new Date());
			
		}
		groupOrder.setSecurityId(order.getSecurityId());
		groupOrder.setTransactionType(order.getTransactionType());
		groupOrder.setSymbol(order.getSymbol());
		groupOrderDataCache.put(mapKey, groupOrder);
	}
	
	public Map<OrderKey, GroupOrder> getGroupOrderDataCache() {
		return groupOrderDataCache;
	}

	public void setGroupOrderDataCache(Map<OrderKey, GroupOrder> groupOrderDataCache) {
		this.groupOrderDataCache = groupOrderDataCache;
	}
}
