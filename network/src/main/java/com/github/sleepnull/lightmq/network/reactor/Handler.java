package com.github.sleepnull.lightmq.network.reactor;

import com.github.sleepnull.lightmq.network.protocol.Request;

/**
 * @author huangyafeng
 *
 */
public interface Handler {
	
	public void handle(Request req);

}
