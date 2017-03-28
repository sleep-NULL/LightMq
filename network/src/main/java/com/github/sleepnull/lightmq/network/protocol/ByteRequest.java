package com.github.sleepnull.lightmq.network.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ByteRequest extends Request {

	private ByteBuffer sizeBuf = ByteBuffer.allocate(4);

	private ByteBuffer bodyBuf = null;

	private boolean finishFlag = false;

	public ByteRequest(int processId, String clientId) {
		super(processId, clientId);
	}

	@Override
	public int readFrom(ReadableByteChannel channel) throws IOException {
		int length = 0;
		if (sizeBuf.hasRemaining()) {
			length += channel.read(sizeBuf);
		}
		if (bodyBuf == null && !sizeBuf.hasRemaining()) {
			sizeBuf.flip();
			int size = sizeBuf.getInt();
			// TODO 检测客户端 request 的大小
			bodyBuf = ByteBuffer.allocate(size);
		}
		if (bodyBuf != null) {
			length += channel.read(bodyBuf);
			if (!bodyBuf.hasRemaining()) {
				bodyBuf.flip();
				finishFlag = true;
			}
		}
		return length;
	}

	@Override
	public boolean finish() {
		return finishFlag;
	}
	
	public ByteBuffer getBodyBuf() {
		return bodyBuf;
	}

}
