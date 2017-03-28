package com.github.sleepnull.lightmq.network.reactor;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public abstract class AbstractReactor implements Runnable {
	
	public void closeChannel(SelectionKey key) {
		try {
			SocketChannel channel = (SocketChannel) key.channel();
			channel.socket().close();
			channel.close();
			key.attach(null);
			key.cancel();
		} catch (Exception e) {
			// ignore
		}
	}

}
