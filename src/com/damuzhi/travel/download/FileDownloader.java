package com.damuzhi.travel.download;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.db.FileDBHelper;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.downlaod.DownloadManager;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.util.ZipUtil;

public class FileDownloader
{

	private static final String TAG = "FileDownloader";
	private Context context;
	//private FileDBHelper fileDBHelper;
	private DownloadManager downloadManager;	
	private int downloadSize = 0;
	private int downloadSpeed = 0;
	private int cityId;
	private int fileSize = 0;

	private DownloadThread[] threads;

	private File saveFile;

	private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();

	private int block;

	private String downloadURL;

	private String savePath,tempPath;
	
	private int threadNum;
	private HttpURLConnection conn;
	private volatile  boolean runflag = true;
	private volatile  boolean notFinish;

	public int getThreadSize()
	{
		return threads.length;
	}

	public int getFileSize()
	{
		return fileSize;
	}

	protected synchronized void append(int size)
	{
		downloadSize += size;
	}

	protected synchronized void downloadSpeed(int speed)
	{
		downloadSpeed = speed;
	}
	
	protected void update(int threadId, int pos)
	{
		this.data.put(threadId, pos);
	}

	protected synchronized void saveLogFile()
	{
		this.downloadManager.updateDownloadInfo(this.downloadURL, this.data);
	}

	public FileDownloader(Context context, int threadNum, int cityId,String savePath,String tempPath,String downloadUrl)
	{
		super();
		this.context = context;
		this.threadNum = threadNum;
		this.cityId = cityId;
		this.savePath = savePath;
		this.tempPath = tempPath;
		this.downloadURL = downloadUrl;
		
	}

	public boolean FileDownloaderCheeck()
	{
		try
		{
			boolean flag = false;
			HttpURLConnection conn = HttpTool.getConnection(downloadURL);
			conn.connect();
			printResponseHeader(conn);
			if (conn.getResponseCode() == 200)
			{
				File fileSaveDir = new File(this.tempPath);
				//File fileSaveDir = new File(this.savePath);
				downloadManager = new DownloadManager(this.context);
				if (!fileSaveDir.exists())
					fileSaveDir.mkdirs();
				this.threads = new DownloadThread[threadNum];
				this.fileSize = conn.getContentLength();
				if (this.fileSize <= 0)
				{
					throw new RuntimeException("Unkown file size ");
				}
				flag = true;
				//String filename = HttpTool.getFileName(conn, downloadURL);
				String filename = HttpTool.getTempFileName(conn, downloadURL);
				this.saveFile = new File(fileSaveDir, filename);
				Map<Integer, Integer> logdata = downloadManager.getData(downloadURL);
				if (logdata.size() > 0)
				{
					for (Map.Entry<Integer, Integer> entry : logdata.entrySet())
					{
						data.put(entry.getKey(), entry.getValue());
					}
				}
				this.block = (this.fileSize % this.threads.length) == 0 ? this.fileSize/ this.threads.length: this.fileSize / this.threads.length + 1;
				if (this.data.size() == this.threads.length)
				{
					for (int i = 0; i < this.threads.length; i++)
					{
						this.downloadSize += this.data.get(i + 1);
					}
					Log.d(TAG, "downsize = " + this.downloadSize);

				}
			} else
			{
				flag = false;
				Log.e(TAG, "<FileDownloaderCheeck> download service get conn fail,response code = "+conn.getResponseCode());
			}
			return flag;
		} catch (Exception e)
		{
			Log.e(TAG,"<FileDownloaderCheeck> download city data but catch exception :" + e.toString(),e);
			return false;
		}
	}

	
	
	
	public int download(DownloadProgressListener listener) 
	{
		try
		{
			RandomAccessFile randOut = new RandomAccessFile(this.saveFile, "rw");
			if (this.fileSize > 0)
				randOut.setLength(this.fileSize);
			randOut.close();
			URL url = new URL(this.downloadURL);
			if (this.data.size() != this.threads.length)
			{
				this.data.clear();
				for (int i = 0; i < this.threads.length; i++)
				{
					this.data.put(i + 1, 0);
				}
			}
			for (int i = 0; i < this.threads.length; i++)
			{
				int downLength = this.data.get(i + 1);
				if (downLength < this.block&& this.downloadSize < this.fileSize)
				{
					this.threads[i] = new DownloadThread(this, url,this.saveFile, this.block, this.data.get(i + 1),i + 1);
					this.threads[i].setPriority(7);
					this.threads[i].setName(url.toString() + i);
					this.threads[i].start();
				} else
				{
					this.threads[i] = null;
				}
			}
			downloadManager.saveDownloadInfo(cityId, downloadURL, savePath, tempPath, 1, this.fileSize, this.data);
			notFinish = true;
			while (notFinish)
			{
				Thread.sleep(900);
				notFinish = false;
				for (int i = 0; i < this.threads.length; i++)
				{
					if (this.threads[i] != null && !this.threads[i].isFinish())
					{
						notFinish = true;
						if (this.threads[i].getDownLength() == -1)
						{
							this.threads[i] = new DownloadThread(this, url,this.saveFile, this.block,this.data.get(i + 1), i + 1);
							this.threads[i].setPriority(7);
							this.threads[i].setName(url.toString() + i);
							this.threads[i].start();
						}
					}
				}
				if (listener != null&&getrunflag())
				{
					listener.onDownloadSize(cityId, downloadURL,this.downloadSpeed,this.downloadSize, fileSize,notFinish);
					//Log.i(TAG, "has download size = "+downloadSize);
				}
			}
			
		} catch (Exception e)
		{
			Log.e(TAG,"<download> file download fail but catch exception :" + e.toString(),e);
		}
		return this.downloadSize;
	}

	public synchronized boolean getrunflag()
	{
	return runflag; 
	} 
	
	public synchronized void pauseDownload()
	{
		for (int i = 0, size = threads.length; i < size; i++)
		{
			if (this.threads[i] != null && !this.threads[i].isFinish())
			{
				threads[i].pause();
			}

		}
		runflag = false;
		saveLogFile();
	}

	
	
	public synchronized void restartDownload()
	{
		for (int i = 0, size = threads.length; i < size; i++)
		{
			if (this.threads[i] != null && !this.threads[i].isFinish())
			{
				threads[i].restart();
			}

		}
		runflag = true;
	}

	
	
	public synchronized void cancelDownload()
	{
		for (int i = 0, size = threads.length; i < size; i++)
		{
			if (this.threads[i] != null && !this.threads[i].isFinish())
			{
				threads[i].cancel();
			}

		}
		notFinish = false;
		runflag = false;
	}
	
	
	public static void printResponseHeader(HttpURLConnection http)
	{
		Map<String, String> header = HttpTool.getHttpResponseHeader(http);
		for (Map.Entry<String, String> entry : header.entrySet())
		{
			String key = entry.getKey() != null ? entry.getKey() + ":" : "";
			Log.i(TAG, key + entry.getValue());
		}
	}
	
}
