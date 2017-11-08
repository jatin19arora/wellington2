package com.sapient.wellington;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sapient.wellington.pojo.GroupOrder;
import com.sapient.wellington.pojo.OrderKey;

@Configuration
public class ApplicationConfiguration {

	/**
	 * bean for array blocking queue
	 * 
	 * @return
	 */
	@Bean(name = "queue")
	public BlockingQueue<String> blockingQueue() {
		return new ArrayBlockingQueue<String>(1);
	}

	/**
	 * bean for group order cache
	 * 
	 * @return
	 */
	@Bean(name = "groupOrderWarningCache")
	public List<String> orderWarningCache() {
		return new CopyOnWriteArrayList<String>();
	}

	@Bean(name = "groupOrderDataCache")
	public Map<OrderKey, GroupOrder> groupOrderDataCache() {
		return new ConcurrentHashMap<OrderKey, GroupOrder>();
	}

	@Bean(name="groupingExecutor")
	public ExecutorService groupingExecutor() {
		return Executors.newFixedThreadPool(10);
	}
}
