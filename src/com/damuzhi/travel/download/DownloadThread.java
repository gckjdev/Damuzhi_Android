package com.damuzhi.travel.download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import com.damuzhi.travel.network.HttpTool;

import android.util.Log;

public class DownloadThread extends Thread {
	private static final String TAG = "DownloadThread";
	private File saveFile;
	private URL downUrl;
	private int block;
	
	private int threadId = -1;	
	private int downLength;
	private boolean finish = false;
	private FileDownloader downloader;
	private static final int INIT = 1;
	private static final int DOWNLOADING = 2;
	private static final int PAUSE = 3;
	private volatile boolean runflag = true;

	public DownloadThread(FileDownloader downloader, URL downUrl, File saveFile, int block, int downLength, int threadId) {
		this.downUrl = downUrl;
		this.saveFile = saveFile;
		this.block = block;
		this.downloader = downloader;
		this.threadId = threadId;
		this.downLength = downLength;
	}
	
	@Override
	public void run() {
		if(downLength < block){
			try {
				int startPos = block * (threadId - 1) + downLength;
				int endPos = block * threadId -1;
				InputStream inStream = HttpTool.getDownloadInputStream(downUrl, startPos, endPos);
				if(inStream !=null)
				{
					byte[] buffer = new byte[4096];
					int offset = 0;
					//Log.i(TAG, "download url = "+downUrl);
					Log.i(TAG, "Thread " + this.threadId + " start download from position "+ startPos);
					RandomAccessFile threadfile = new RandomAccessFile(this.saveFile, "rwd");
					threadfile.seek(startPos);
						while ((offset = inStream.read(buffer, 0, 4096)) != -1) {	
							if(!getrunflag())
							{
								return;
							}
							threadfile.write(buffer, 0, offset);
							downLength += offset;
							downloader.update(this.threadId, downLength);
							downloader.saveLogFile();
							downloader.append(offset);
							downloader.downloadSpeed(offset);								
						}
						runflag = false;
					threadfile.close();
					inStream.close();			
					Log.i(TAG,"Thread " + this.threadId + " download finish");
					this.finish = true;
				}				
			} catch (Exception e) {
				this.downLength = -1;
				Log.e(TAG, "Thread "+ this.threadId+ ":"+ e.toString(),e);
			}
		}
	}

	
	public boolean isFinish() {
		return finish;
	}
	
	
	public synchronized void pause() {
		downloader.saveLogFile();
		runflag = false;
	}
	
	public synchronized boolean getrunflag()
	{
	return runflag; 
	} 

	public synchronized void restart() {
		runflag = true;
	}
	
	public synchronized void cancel()
	{
		runflag = false;
	}
	
	
	public long getDownLength() {
		return downLength;
	}
}
