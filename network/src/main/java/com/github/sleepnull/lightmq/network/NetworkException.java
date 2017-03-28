package com.github.sleepnull.lightmq.network;

/**
 * @author huangyafeng
 *
 */
public class NetworkException extends RuntimeException {

	private static final long serialVersionUID = 3420651461420908580L;
	
	public NetworkException() {
		super();
	}
	
	public NetworkException(String message) {
		super(message);
	}
	
	public NetworkException(Throwable cause) {
		super(cause);
	}

}
