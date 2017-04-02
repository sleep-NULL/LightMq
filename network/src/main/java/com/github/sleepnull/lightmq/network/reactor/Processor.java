package com.github.sleepnull.lightmq.network.reactor;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sleepnull.lightmq.network.NetworkException;
import com.github.sleepnull.lightmq.network.protocol.ByteRequest;
import com.github.sleepnull.lightmq.network.protocol.Request;
import com.github.sleepnull.lightmq.network.protocol.Response;

/**
 * @author huangyafeng
 *
 */
public class Processor extends AbstractReactor {

	private static final Logger logger = LoggerFactory.getLogger(Processor.class);

	private int id;

	private RequestChannel requestChannel;

	private Selector selector;

	private Map<String, SocketChannel> clients;

	private AtomicBoolean isRunning = new AtomicBoolean(false);

	/**
	 * 用于接受 acceptor 传递的客户度连接
	 */
	private BlockingQueue<SocketChannel> clientChannelQueue;

	public Processor(int id, RequestChannel requestChannel) {
		this.id = id;
		this.requestChannel = requestChannel;
		this.clients = new ConcurrentHashMap<String, SocketChannel>();
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			logger.error("Processor {} init failed.", id, e);
			throw new NetworkException(e);
		}
		this.clientChannelQueue = new ArrayBlockingQueue<SocketChannel>(100);
		logger.info("Init processor {}.", id);
	}

	public void assign(SocketChannel socketChannel) throws InterruptedException {
		clientChannelQueue.put(socketChannel);
	}

	/**
	 * 将 acceptor 分配的客户端连接注册读事件
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void registry() throws IOException {
		SocketChannel socketChannel = null;
		socketChannel = this.clientChannelQueue.poll();
		if (socketChannel != null) {
			socketChannel.configureBlocking(false);
			socketChannel.socket().setTcpNoDelay(true);// 禁用 nagle 算法
			socketChannel.register(selector, SelectionKey.OP_READ);
			clients.put(getClientId(socketChannel), socketChannel);
		}
	}

	/**
	 * 注册通道到写事件，并将 response 作为附件
	 * 
	 * @throws InterruptedException
	 * @throws ClosedChannelException
	 */
	private void processResponse() throws InterruptedException, ClosedChannelException {
		Response res = requestChannel.getResponse(id);
		if (res != null) {
			SocketChannel client = clients.get(res.getClientId());
			client.register(selector, SelectionKey.OP_WRITE, res);
		}
	}

	private String getClientId(SocketChannel client) {
		Socket socket = client.socket();
		return String.format("%s:%d", socket.getInetAddress().getHostAddress(), socket.getPort());
	}

	public void run() {
		if (isRunning.get()) {
			return;
		}
		logger.info("Processor {} start.", id);
		isRunning.set(true);
		while (isRunning.get()) {
			try {
				registry();
				processResponse();
				int selectNum = selector.select(300L);
				if (selectNum != 0) {
					Iterator<SelectionKey> it = selector.selectedKeys().iterator();
					while (it.hasNext()) {
						SelectionKey key = null;
						try {
							key = it.next();
							it.remove();
							if (key.isReadable()) {
								read(key);
							} else if (key.isWritable()) {
								write(key);
							} else if (!key.isValid()) {
								closeChannel(key);
								clients.remove(getClientId((SocketChannel) key.channel()));
							}
						} catch (Exception e) {
							clients.remove(getClientId((SocketChannel) key.channel()));
							closeChannel(key);
							logger.error("Processor handle client occur error.", e);
						}
					}
				}
			} catch (Throwable e) {
				logger.error("Processor occur error.", e);
			}
		}
	}

	private void read(SelectionKey key) throws IOException, InterruptedException {
		long ct = System.currentTimeMillis();
		SocketChannel client = (SocketChannel) key.channel();
		String clientId = getClientId(client);
		Request request = (Request) key.attachment();
		if (request == null) {
			request = new ByteRequest(id, clientId);
			key.attach(request);
		}
		int readCount = request.readFrom(client);
		// 客户端连接已经断开
		if (readCount < 0) {
			logger.trace("Close channel {}.", clientId);
			closeChannel(key);
			clients.remove(clientId);
		} else if (request.finish()) {
			key.attach(null);
			requestChannel.putRequest(request);
		}
		long gap = System.currentTimeMillis() - ct;
		logger.trace("Read client {} request use {} ms", clientId, gap);
	}

	private void write(SelectionKey key) throws IOException {
		long ct = System.currentTimeMillis();
		Response res = (Response) key.attachment();
		res.writeTo((SocketChannel) key.channel());
		if (res.finish()) {
			key.attach(null);// 便于垃圾回收
			key.interestOps(SelectionKey.OP_READ);
		} else {
			selector.wakeup();
		}
		logger.trace("Write response use {} ms.", System.currentTimeMillis() - ct);
	}

}
