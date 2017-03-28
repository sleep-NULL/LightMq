package com.github.sleepnull.lightmq.core.utils;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 线程创建工具类
 * 
 * @author huangyafeng
 *
 */
public class ThreadUtil {
	
	public static void newThread(Runnable runnable, String name, boolean isDaemon, UncaughtExceptionHandler handler) {
		Thread thread = new Thread(runnable, name);
		thread.setDaemon(isDaemon);
		thread.setUncaughtExceptionHandler(handler);
		thread.start();
	}

	public static void newThread(Runnable runnable, String name) {
		newThread(runnable, name, false, null);
	}

	public static void newThread(Runnable runnable, String name, boolean isDaemon) {
		newThread(runnable, name, isDaemon, null);
	}

	public static void newThread(Runnable runnable, String name, UncaughtExceptionHandler handler) {
		newThread(runnable, name, false, handler);
	}

}
