package com.github.sleepnull.lightmq.network;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sleepnull.lightmq.core.utils.ThreadUtil;
import com.github.sleepnull.lightmq.log.AppendLog;
import com.github.sleepnull.lightmq.network.reactor.Acceptor;
import com.github.sleepnull.lightmq.network.reactor.HandlerThread;
import com.github.sleepnull.lightmq.network.reactor.RequestChannel;

/**
 * @author huangyafeng
 *
 */
public class LightMqServer {

	private static Logger logger = LoggerFactory.getLogger(LightMqServer.class);

	public LightMqServer(String hostname, int port, int processorCount, int handlerCount, String fileName)
			throws IOException {
		RequestChannel requestChannel = new RequestChannel(1000, 1000, processorCount);
		AppendLog log = new AppendLog(fileName);
		for (int i = 0; i < handlerCount; i++) {
			ThreadUtil.newThread(new HandlerThread(requestChannel, log), "Handler-" + i);
		}
		ThreadUtil.newThread(new Acceptor(hostname, port, processorCount, requestChannel), "Acceptor");
	}

	public static void main(String[] args) throws IOException {
		logger.info("LightMq start...");
		new LightMqServer("localhost", 7777, 5, 10, "/Users/huangyafeng/hyf.log");
	}

}
