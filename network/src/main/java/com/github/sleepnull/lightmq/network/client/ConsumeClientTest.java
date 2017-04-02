package com.github.sleepnull.lightmq.network.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ConsumeClientTest {

	public static void main(String[] args) throws IOException {
		long ct = System.currentTimeMillis();
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress("localhost", 7777));
		OutputStream out = socket.getOutputStream();
		ByteBuffer buf = ByteBuffer.allocate(4 + 2);
		buf.putInt(2);
		buf.putShort((short) 1);
		buf.flip();
		out.write(buf.array());
		System.out.println(System.currentTimeMillis() - ct);
		InputStream in = socket.getInputStream();
		DataInputStream din = new DataInputStream(in);
		while (true) {
			int size = din.readInt();
			byte[] b = new byte[size];
			din.readFully(b);
			System.out.println(new String(b, "UTF-8"));
		}
	}
}
