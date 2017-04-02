package com.github.sleepnull.lightmq.network.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sleepnull.lightmq.log.AppendLog;
import com.github.sleepnull.lightmq.network.protocol.ByteRequest;
import com.github.sleepnull.lightmq.network.protocol.ConsumeResponse;
import com.github.sleepnull.lightmq.network.protocol.Request;
import com.github.sleepnull.lightmq.network.protocol.RequestKey;
import com.github.sleepnull.lightmq.network.protocol.Response;

/**
 * @author huangyafeng
 *
 */
public class HandlerThread implements Handler, Runnable {

	private static final Logger logger = LoggerFactory.getLogger(HandlerThread.class);

	private RequestChannel requestChannel;
	
	private AppendLog log;

	public HandlerThread(RequestChannel requestChannel, AppendLog log) {
		this.requestChannel = requestChannel;
		this.log = log;
		logger.info("Init Handler thread.");
	}

	public void run() {
		logger.info("Handler thread start...");
		while (true) {
			try {
				handle(requestChannel.getRequest());
			} catch (Exception e) {
				logger.error("Handler thread handle request occur error.", e);
			}
		}
	}

	public void handle(Request req) {
		ByteBuffer bodyBuf = ((ByteRequest)req).getBodyBuf();
		switch (RequestKey.valueOf(bodyBuf.getShort())) {
		case produce:
			ByteBuffer writeBuf = ByteBuffer.allocate(4 +  bodyBuf.remaining());
			writeBuf.putInt(bodyBuf.remaining());
			writeBuf.put(bodyBuf);
			writeBuf.flip();
			try {
				long ct = System.currentTimeMillis();
				log.append(writeBuf);
				logger.trace("Append write buffer use {} ms.", System.currentTimeMillis() - ct);
			} catch (IOException e) {
				logger.error("", e);
			}
			break;
		case consume:
			try {
				Response res = new ConsumeResponse(req.getProcessorId(), req.getClientId(), log, 0, log.size());
				requestChannel.putResponse(res);
			} catch (Exception e) {
				logger.error("", e);
			}
			break;
		default:
			logger.error("Invalid request key form {}", req.getClientId());
		}

	}

}
