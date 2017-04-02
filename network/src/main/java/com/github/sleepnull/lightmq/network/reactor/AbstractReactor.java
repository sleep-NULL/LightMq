package com.github.sleepnull.lightmq.network.reactor;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author huangyafeng
 *
 */
public abstract class AbstractReactor implements Runnable {

	public void closeChannel(SelectionKey key) {
		if (key != null) {
			SocketChannel channel = (SocketChannel) key.channel();
			try {
				channel.socket().close();
			} catch (Exception e) {
				// ignore
			}
			try {
				channel.close();
			} catch (Exception e) {
				// ignore
			}
			key.attach(null);
			key.cancel();
		}
	}

}
