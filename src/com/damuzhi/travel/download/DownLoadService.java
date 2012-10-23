package com.damuzhi.travel.download;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.model.entity.DownloadInfos;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.ZipUtil;
import com.damuzhi.travel.util.ZipUtil2;
import com.loopj.android.http.AsyncHttpClient;

import dalvik.system.VMRuntime;

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
	public static Map<String, Integer> downloadStstudTask = new HashMap<String, Integer>();
	private  Map<String, AsyncHttpClient> downloadClientMap = new HashMap<String, AsyncHttpClient>();
	private  Map<String, DownloadHandler> downloadControlMap = new HashMap<String, DownloadHandler>();
	public static  Handler downloadHandler;
	//private final static float TARGET_HEAP_UTILIZATION = 0.75f;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		//Log.d(TAG, "service onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		//VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);
		//Log.d(TAG, "service onCreate");
	}

	
	
	
	//private static AsyncHttpClient client = new AsyncHttpClient();
	private  void download(final int cityId, final String downloadURL,  String savePath,  String tempPath,final String upZipFilePath)
	{
		AsyncHttpClient client = new AsyncHttpClient();
		downloadClientMap.put(downloadURL, client);

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
		Log.d(TAG, "download file, temp=" + tempPath + ", save=" + savePath);

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
			protected void bytesReceived(long fileTotalLength, long downloadLength, int addedLength)
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
				Toast.makeText(getApplicationContext(),getApplicationContext().getString(R.string.conn_fail_exception), Toast.LENGTH_SHORT).show();
				Log.e(TAG, "<onFailure> downloading failure="+e.toString());
			}

		};
		
		downloadControlMap.put(downloadURL, cityHandler);
		
		//client.get(null, downloadURL, headers, null, new DownloadHandler(savePath, tempPath)
		client.get(getApplicationContext(),downloadURL, cityHandler);
	}
	
	private   void cancelHttpDownload(String downloadURL)
	{
		
		if(downloadClientMap.containsKey(downloadURL))
		{
			AsyncHttpClient client = downloadClientMap.get(downloadURL);
			client.cancelRequests(getApplicationContext(), true);
			client = null;
		}
		downloadClientMap.remove(downloadURL);
		if (downloadControlMap.containsKey(downloadURL)){
			Log.i(TAG, "cancelHttpDownload... cancel download for"+getApplicationContext());
			//downloadControlMap.get(downloadURL).cancelDownload();
			DownloadHandler downloadHandler = downloadControlMap.get(downloadURL);
			downloadHandler.cancelDownload();
			downloadHandler = null;
		}
		downloadControlMap.remove(downloadURL);
	}

	public  Handler getDownloadHandler()
	{
		return downloadHandler;
	}

	public  void setDownloadHandler(Handler downloadHandler)
	{
		DownloadService.downloadHandler = downloadHandler;
	}
	
	
	public  void pauseDownload(String downloadURL) 
	{
		if(downloadStstudTask.containsKey(downloadURL))
		{
			downloadStstudTask.put(downloadURL, PAUSE);
		}	
		cancelHttpDownload(downloadURL);
	}
	
	public  void cancelDownload(String downloadURL) 
	{
		if(downloadStstudTask.containsKey(downloadURL))
		{
		downloadStstudTask.remove(downloadURL);
		}
		cancelHttpDownload(downloadURL);
	}

	public  boolean startDownload(int cityId, String downloadURL,
			String downloadSavePath, String tempPath,String upZipFilePath)
	{
		downloadStstudTask.put(downloadURL, DOWNLOADING);
		download(cityId, downloadURL, downloadSavePath, tempPath,upZipFilePath);
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
		return servoceBinder;
	}
	
	
	public class DownloadServiceBinder extends Binder{
        
        public DownloadService getService(){
            return DownloadService.this;
        }
    }
    
    private DownloadServiceBinder servoceBinder = new DownloadServiceBinder();
	
	static ExecutorService unzipExecutorService = Executors.newFixedThreadPool(1);
	
	public  void upZipFile(final String zipFilePath, final String upZipFilePath,final int cityId,final String downloadURL)
	{
		
		unzipExecutorService.execute(new Runnable()
		{
			
			@Override
			public void run()
			{
				Log.d(TAG, "unzip file ="+zipFilePath);
				//boolean result = ZipUtil.upZipFile(zipFilePath, upZipFilePath);
				boolean result = ZipUtil2.unZipToFolder(zipFilePath, upZipFilePath);
				if(result)
				{
					FileUtil.deleteFile(zipFilePath);
				}
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
			}

	public  Map<String, Integer> getDownloadStstudTask()
	{
		return downloadStstudTask;
	}

	public  void setDownloadStstudTask(Map<String, Integer> downloadStstudTask)
	{
		DownloadService.downloadStstudTask = downloadStstudTask;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "service onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
		Log.d(TAG, "service onStart");
	}
	
}
