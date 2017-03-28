package com.github.sleepnull.lightmq.network.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sleepnull.lightmq.core.utils.ThreadUtil;
import com.github.sleepnull.lightmq.network.NetworkException;

/**
 * @author huangyafeng
 *
 */
public class Acceptor extends AbstractReactor {

	private static final Logger logger = LoggerFactory.getLogger(Acceptor.class);

	private Selector selector;

	private ServerSocketChannel serverChannel;

	private AtomicBoolean isRunning = new AtomicBoolean(false);

	private int roundRobin = 0;

	private int processorCount;

	private Processor[] processors;

	private RequestChannel requestChannel;

	public Acceptor(String hostname, int port, int processorCount, RequestChannel requestChannel) {
		try {
			this.selector = Selector.open();
			this.serverChannel = ServerSocketChannel.open();
			this.serverChannel.configureBlocking(false);
			this.serverChannel.socket().bind(new InetSocketAddress(hostname, port));
			this.serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			logger.error("Init Acceptor failed.", e);
			throw new NetworkException(e);
		}
		this.processorCount = processorCount;
		this.requestChannel = requestChannel;
		logger.info("Init acceptor bind {}:{}", hostname, port);
	}

	private void accept(SelectionKey key) throws InterruptedException, IOException {
		SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
		roundRobin = (roundRobin + 1) % processorCount;
		processors[roundRobin].assign(client);
	}

	public void run() {
		if (!isRunning.compareAndSet(false, true)) {
			return;
		}
		logger.info("Acceptor start...");
		processors = new Processor[processorCount];
		for (int i = 0; i < processorCount; i++) {
			processors[i] = new Processor(i, requestChannel);
			// 启动 processor 线程
			ThreadUtil.newThread(processors[i], "Processor-" + i);
		}
		SelectionKey key = null;
		while (isRunning.get()) {
			int keyCount = 0;
			try {
				keyCount = selector.select(300L);
			} catch (Exception e) {
				logger.error("Acceptor select occur error.", e);
			}
			if (keyCount != 0) {
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					try {
						key = it.next();
						it.remove();
						if (key.isAcceptable()) {
							accept(key);
						} else {
							closeChannel(key);
							logger.error("Invalid key in acceptor.");
						}
					} catch (Exception e) {
						closeChannel(key);
						logger.error("", e);
					}
				}
			}
		}
	}
}
