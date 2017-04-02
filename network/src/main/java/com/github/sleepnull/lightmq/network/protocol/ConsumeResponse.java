package com.github.sleepnull.lightmq.network.protocol;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

import com.github.sleepnull.lightmq.log.AppendLog;

/**
 * @author huangyafeng
 *
 */
public class ConsumeResponse extends Response {
	
	private AppendLog log;
	
	private long position;
	
	private long count;
	
	public ConsumeResponse(int processorId, String clientId, AppendLog log, long position, long count) {
		super(processorId, clientId);
		this.log = log;
		this.position = position;
		this.count = count;
	}

	@Override
	public void writeTo(WritableByteChannel channel) throws IOException {
		long transferCount = log.transferTo(position, count, channel);
		position += transferCount;
		count -= transferCount;
	}

	@Override
	public boolean finish() {
		return count == 0;
	}

}
