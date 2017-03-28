package com.github.sleepnull.lightmq.log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author huangyafeng
 *
 */
public class AppendLog {

	private FileChannel fileChannel;

	private RandomAccessFile randomAccessFile;

	public AppendLog(String fileName) throws IOException {
		this.randomAccessFile = new RandomAccessFile(new File(fileName), "rw");
		this.fileChannel = randomAccessFile.getChannel();
	}

	public void append(ByteBuffer buf) throws IOException {
		do {
			fileChannel.write(buf);
		} while (buf.hasRemaining());
	}

	public void seek(long pos) throws IOException {
		fileChannel.position(pos);
	}

	public void close() throws IOException {
		randomAccessFile.close();// 该函数会关闭与文件相关的 channel，故无需再调用 channel 的 close
	}

	public void force() throws IOException {
		fileChannel.force(true);
	}

	public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
		return fileChannel.transferTo(position, count, target);
	}

}
