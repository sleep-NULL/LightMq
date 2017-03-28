package com.github.sleepnull.lightmq.network.protocol;

import java.nio.channels.WritableByteChannel;

/**
 * @author huangyafeng
 *
 */
public class ConsumeResponse extends Response {
	

	public ConsumeResponse(int processorId, String clientId) {
		super(processorId, clientId);
	}

	@Override
	public void writeTo(WritableByteChannel channel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean finish() {
		// TODO Auto-generated method stub
		return false;
	}

}
