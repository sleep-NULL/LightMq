package com.github.sleepnull.lightmq.message.serialization;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huangyafeng
 *
 */
public class StringSerializable implements Serializable<String> {
	
	private static final Logger logger  = LoggerFactory.getLogger(StringSerializable.class);

	public byte[] serialize(String t) {
		try {
			return t.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Unsupported utf-8 encoding.", e);
			return null;
		}
	}

	public String deserialize(byte[] buf) {
		try {
			return new String(buf, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Unsupported utf-8 encoding.", e);
			return null;
		}
	}

}
