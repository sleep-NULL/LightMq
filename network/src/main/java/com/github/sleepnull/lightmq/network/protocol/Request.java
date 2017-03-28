package com.github.sleepnull.lightmq.network.protocol;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

/**
 * @author huangyafeng
 *
 */
public abstract class Request {

	private int processorId;

	private String clientId;
	
	public Request(int processId, String clientId) {
		this.processorId = processId;
		this.clientId = clientId;
	}

	public int getProcessorId() {
		return processorId;
	}

	public String getClientId() {
		return clientId;
	}
	
	/**
	 * 从客户端读取 request
	 * 
	 * @param channel
	 * @return
	 * @throws IOException
	 */
	public abstract int readFrom(ReadableByteChannel channel) throws IOException;
	
	/**
	 * 判断一个 request 是否读取完成
	 * 
	 * @return
	 */
	public abstract boolean finish();
}
