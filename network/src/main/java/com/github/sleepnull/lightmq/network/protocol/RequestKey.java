package com.github.sleepnull.lightmq.network.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huangyafeng
 *
 */
public enum RequestKey {

	produce(0), consume(1);

	private int key;
	
	private static Map<Integer, RequestKey> keys = new HashMap<Integer, RequestKey>();
	
	static {
		keys.put(0, produce);
		keys.put(1, consume);
		
	}

	private RequestKey(int key) {
		this.key = key;
	}

	public int getKey() {
		return key;
	}
	
	public static RequestKey valueOf(int i) {
		return keys.get(i);
	}

}
