package com.github.sleepnull.lightmq.message;

/**
 * @author huangyafeng
 *
 * @param <K>
 * @param <V>
 */
public class Message<K, V> {

	private long timestamp;

	private K key;

	private V value;

	public Message(long timestamp, K key, V value) {
		super();
		this.timestamp = timestamp;
		if (key != null) {
			this.key = key;
		}
		this.value = value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

}
