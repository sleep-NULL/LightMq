package com.github.sleepnull.lightmq.network.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ProduceClientTest {

	public static void main(String[] args) throws IOException {
		long ct = System.currentTimeMillis();
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress("localhost", 7777));
		OutputStream out = socket.getOutputStream();
		for (int i = 0; i < 100000000; i++) {
			String body = new String("hello" + i);
			ByteBuffer buf = ByteBuffer.allocate(4 + 2 + body.getBytes().length);
			buf.putInt(2 + body.getBytes().length);
			buf.putShort((short) 0);
			buf.put(body.getBytes());
			buf.flip();
			out.write(buf.array());
		}
		// out.close();
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis() - ct);
	}
}
