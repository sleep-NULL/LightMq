package com.github.sleepnull.lightmq.network.protocol;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

/**
 * @author huangyafeng
 *
 */
public abstract class Response {

	private int processorId;

	private String clientId;

	public Response(int processorId, String clientId) {
		this.processorId = processorId;
		this.setClientId(clientId);
	}

	public int getProcessorId() {
		return processorId;
	}

	public void setProcessorId(int processorId) {
		this.processorId = processorId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * 发送数据给客户端
	 * 
	 * @param channel
	 * @throws IOException 
	 */
	public abstract void writeTo(WritableByteChannel channel) throws IOException;

	/**
	 * 判断是否数据已经发送完成
	 * 
	 * @return
	 */
	public abstract boolean finish();

}
