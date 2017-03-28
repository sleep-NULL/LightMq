package com.github.sleepnull.lightmq.network.reactor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.github.sleepnull.lightmq.network.protocol.Request;
import com.github.sleepnull.lightmq.network.protocol.Response;

/**
 * @author huangyafeng
 *
 */
public class RequestChannel {

	/**
	 * processor 发送具体的 request 给 handler 线程处理
	 */
	private ArrayBlockingQueue<Request> requestQueue;

	/**
	 * handler 线程将 request 的处理结果发给对应的 processor 进行客户端回馈
	 */
	private ArrayBlockingQueue<Response>[] responseQueue;

	@SuppressWarnings("unchecked")
	public RequestChannel(int requestCapacity, int responseCapacity, int processorCount) {
		this.requestQueue = new ArrayBlockingQueue<Request>(requestCapacity);
		this.responseQueue = new ArrayBlockingQueue[processorCount];
		for (int i = 0; i < processorCount; i++) {
			responseQueue[i] = new ArrayBlockingQueue<Response>(responseCapacity);
		}
	}

	public void putRequest(Request req) throws InterruptedException {
		this.requestQueue.put(req);
	}

	public Request getRequest() throws InterruptedException {
		return this.requestQueue.take();
	}

	public Request getRequest(long timeout) throws InterruptedException {
		return this.requestQueue.poll(timeout, TimeUnit.MILLISECONDS);
	}

	public void putResponse(Response res) throws InterruptedException {
		this.responseQueue[res.getProcessorId()].put(res);
	}

	public Response getResponse(int processorId) throws InterruptedException {
		return this.responseQueue[processorId].poll();
	}

	public Response getResponse(int processorId, long timeout) throws InterruptedException {
		return this.responseQueue[processorId].poll(timeout, TimeUnit.MILLISECONDS);
	}

}
