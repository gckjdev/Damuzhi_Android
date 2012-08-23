package com.damuzhi.travel.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipException;

import org.apache.commons.httpclient.Header;

import android.R.integer;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.more.OpenCityActivity;
import com.damuzhi.travel.db.DownloadPreference;
import com.damuzhi.travel.db.FileDBHelper;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.downlaod.DownloadManager;
import com.damuzhi.travel.model.entity.DownloadInfos;
import com.damuzhi.travel.model.entity.DownloadStatus;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.ZipUtil;
import com.damuzhi.travel.util.ZipUtil2;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.weibo.net.HttpHeaderFactory;

public class DownloadService extends Service
{
	private static final String TAG = "DownLoadService";

	private static final int PAUSE = 1;
	private static final int DOWNLOADING = 2;
	private static final int FAILED = 3;
	private static final int UPZIPING = 4;
	private static final int SUCCESS = 5;
	private static final int PROCESS_CHANGED = 1;
	private static final int UPZIP = 2;
	private static final int CONNECTION_ERROR = 3;

	//public static Map<String, FileDownloader> downloadTask = new HashMap<String, FileDownloader>();

	public static Map<String, Integer> downloadStstudTask = new HashMap<String, Integer>();

	private static Map<String, AsyncHttpClient> downloadClientMap = new HashMap<String, AsyncHttpClient>();
	private static Map<String, DownloadHandler> downloadControlMap = new HashMap<String, DownloadHandler>();
	public static  Handler downloadHandler;
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

	}

	
	
	
	//private static AsyncHttpClient client = new AsyncHttpClient();
	private static void download(final Context context,final int cityId, final String downloadURL,  String savePath,  String tempPath,final String upZipFilePath)
	{
		AsyncHttpClient client = new AsyncHttpClient();
		downloadClientMap.put(downloadURL, client);
		Log.i(TAG, "start downloading... context="+context);

		File fileSaveDir = new File(savePath);
		if (!fileSaveDir.exists())
			fileSaveDir.mkdirs();

		File tempPathDir = new File(tempPath);
		if (!tempPathDir.exists())
			tempPathDir.mkdirs();
		String fileName = TravelUtil.getDownloadFileName(downloadURL);
		tempPath = tempPath+fileName;
		//savePath = savePath+fileName;
		final String zipFilePath = savePath+fileName;
		Log.i(TAG, "download file, temp=" + tempPath + ", save=" + savePath);

		long startDownloadLength = FileUtil.getFileSize(tempPath);
		client.addHeader("Range", "bytes=" + startDownloadLength + "-");
		
		DownloadHandler cityHandler = new DownloadHandler(savePath, tempPath)
		{
			@Override
			public void onSuccess(byte[] fileData)
			{			
				// TODO post successfully download
				downloadStstudTask.put(downloadURL, UPZIPING);
				upZipFile(zipFilePath,upZipFilePath,cityId,downloadURL);
			}

			@Override
			protected void bytesReceived(long fileTotalLength, long downloadLength, int addedLength,long lastTime)
			{
				//Log.i(TAG, "<bytesReceived> download = "+downloadLength + ", total = "+fileTotalLength);
				
				//TODO notify UI to update progress
				boolean notFinish = true;
				if(downloadLength == fileTotalLength)
				{
					notFinish = false;
				}
				 final  DownloadInfos dl = new DownloadInfos(cityId,downloadURL,fileTotalLength,downloadLength,notFinish,false);  	
				 Message msg = Message.obtain();
				 msg.what = PROCESS_CHANGED;
				 msg.obj = dl;
			     downloadHandler.sendMessage(msg);
			}

			@Override
			public void onFailure(Throwable e, byte[] arg1)
			{
				 final  DownloadInfos dl = new DownloadInfos(cityId,downloadURL,fileTotalLength,downloadLength,true,false);
				 Message msg = Message.obtain();
				 msg.what = CONNECTION_ERROR;
				 msg.obj = dl;
			     downloadHandler.sendMessage(msg);
				Toast.makeText(context,context.getString(R.string.conn_fail_exception), Toast.LENGTH_SHORT).show();
				Log.e(TAG, "<onFailure> downloading failure="+e.toString());
			}

		};
		
		downloadControlMap.put(downloadURL, cityHandler);
		
		//client.get(null, downloadURL, headers, null, new DownloadHandler(savePath, tempPath)
		client.get(context,downloadURL, cityHandler);
	}
	
	private  static void cancelHttpDownload(Context context,String downloadURL)
	{
		
		if(downloadClientMap.containsKey(downloadURL))
		{
			Log.i(TAG, "cancelHttpDownload... cancel request at context="+context);
			AsyncHttpClient client = downloadClientMap.get(downloadURL);
			client.cancelRequests(context, true);
		}
		
		if (downloadControlMap.containsKey(downloadURL)){
			Log.i(TAG, "cancelHttpDownload... cancel download for"+context);
			downloadControlMap.get(downloadURL).cancelDownload();
		}
	}

	public static Handler getDownloadHandler()
	{
		return downloadHandler;
	}

	public static void setDownloadHandler(Handler downloadHandler)
	{
		DownloadService.downloadHandler = downloadHandler;
	}
	
	
	public static void pauseDownload(Context context,String downloadURL) 
	{
		if(downloadStstudTask.containsKey(downloadURL))
		{
			downloadStstudTask.put(downloadURL, PAUSE);
		}	
		cancelHttpDownload(context,downloadURL);
	}
	
	public static void cancelDownload(Context context,String downloadURL) 
	{
		if(downloadStstudTask.containsKey(downloadURL))
		{
		downloadStstudTask.remove(downloadURL);
		}
		cancelHttpDownload(context,downloadURL);
	}

	public static boolean startDownload(Context context,int cityId, String downloadURL,
			String downloadSavePath, String tempPath,String upZipFilePath)
	{
		downloadStstudTask.put(downloadURL, DOWNLOADING);
		download(context,cityId, downloadURL, downloadSavePath, tempPath,upZipFilePath);
		return true;
	}

	public void restartDownload(String downloadURL) 
	{
		//restartDownloadTask(downloadURL);
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	static ExecutorService unzipExecutorService = Executors.newFixedThreadPool(1);
	
	public static void upZipFile(final String zipFilePath, final String upZipFilePath,final int cityId,final String downloadURL)
	{
		
		unzipExecutorService.execute(new Runnable()
		{
			
			@Override
			public void run()
			{
				//Debug.startMethodTracing("unzip");
				Log.d(TAG, "unzip file ="+zipFilePath);
				boolean result = ZipUtil.upZipFile(zipFilePath, upZipFilePath);
				//Debug.stopMethodTracing();
				//ZipUtil2 zipUtil2 = new ZipUtil2(zipFilePath);
				//zipUtil2.unzip(upZipFilePath);
				//boolean result = ZipUtil2.unZipToFolder(zipFilePath, upZipFilePath);
				if(downloadStstudTask.containsKey(downloadURL))
				{
					downloadStstudTask.remove(downloadURL);
				}
				Message msg = Message.obtain();
			    msg.what = UPZIP;
			    DownloadInfos downloadInfos = new DownloadInfos(cityId, downloadURL, 0, 0, false, result);
			    msg.obj = downloadInfos;
		        downloadHandler.sendMessage(msg);
			}
		});
		
		
		/*AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{
				boolean result = ZipUtil.upZipFile(zipFilePath, upZipFilePath);
				if(downloadStstudTask.containsKey(downloadURL))
				{
					downloadStstudTask.remove(downloadURL);
				}
				Message msg = Message.obtain();
			    msg.what = UPZIP;
			    DownloadInfos downloadInfos = new DownloadInfos(cityId, downloadURL, 0, 0, false, result);
			    msg.obj = downloadInfos;
		        downloadHandler.sendMessage(msg);
				return null;
			}
	
		};
		asyncTask.execute();*/
	}

	public static Map<String, Integer> getDownloadStstudTask()
	{
		return downloadStstudTask;
	}

	public static void setDownloadStstudTask(Map<String, Integer> downloadStstudTask)
	{
		DownloadService.downloadStstudTask = downloadStstudTask;
	}
	
}
