package com.github.sleepnull.lightmq.message.serialization;

/**
 * @author huangyafeng
 *
 * @param <T>
 */
public interface Serializable<T> {

	byte[] serialize(T t);

	T deserialize(byte[] buf);

}
