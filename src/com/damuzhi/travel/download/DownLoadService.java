package com.damuzhi.travel.download;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipException;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.db.FileDBHelper;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.downlaod.DownloadManager;
import com.damuzhi.travel.model.entity.DownloadStatus;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.ZipUtil;

public class DownloadService extends Service
{
	private static final String TAG ="DownLoadService";
	
	private static final int FINISH = 1;
	private static final int PAUSE = 2;
	private static final int DOWNLOADING = 3;
	private static final int FAILED = 4;
	private static final int RESTART = 5;
	private static final int cancel = 6;
	
	public static Map<String,FileDownloader> downloadTask = new HashMap<String,FileDownloader>();
	
	public static Map<String, DownloadStatus> downloadStstudTask = new HashMap<String, DownloadStatus>();
	
	private  IDownloadCallback iDownloadCallback;
	
	private final  IDownloadService.Stub iDownCallService = new IDownloadService.Stub()
	
	{
		
		@Override
		public void unregCallback(IDownloadCallback cb) throws RemoteException
		{
			unRegCallbackService(cb);
		}
		
		@Override
		public void setMaxTaskCount(int count) throws RemoteException
		{
			// setTaskCount(count); 
		}
		
		
		@Override
		public void regCallback(IDownloadCallback cb) throws RemoteException
		{
			regCallbackService(cb);
		}
		
		
		
		@Override
		public void pauseDownload(String downloadURL) throws RemoteException
		{
			pauseDownloadTask(downloadURL);
		}
		
		@Override
		public void cancelDownload(String downloadURL) throws RemoteException
		{
			cancelDownloadTask(downloadURL);
		}
		
		
		@Override
		public boolean startDownload( int cityId,String downloadURL, String downloadSavePath,String tempPath)
				throws RemoteException
		{
			boolean flag = false;
			try
			{
				flag = startDownloadTask( cityId,downloadURL, downloadSavePath,tempPath);
			} catch (Exception e)
			{
				Log.e(TAG, "<startDownload> but catch exception : "+e.toString(),e);
			}
			return flag;
		}

		
		
		@Override
		public void restartDownload(String downloadURL) throws RemoteException
		{
			restartDownloadTask(downloadURL);
		}
	};
	
	
	private RemoteCallbackList<IDownloadCallback> callbackList = new RemoteCallbackList<IDownloadCallback>();
	
	
	
	public void regCallbackService( IDownloadCallback iDownloadCallback)
	{
		if(iDownloadCallback != null)
		{
			callbackList.register(iDownloadCallback);
		}
	}
	
	
	public void unRegCallbackService(IDownloadCallback iDownloadCallback)
	{
		if(iDownloadCallback != null){
			callbackList.unregister(iDownloadCallback);
		}
	}
	
	
	public void removeTask(DownloadStatus state)  
	{    
	  callbackList.finishBroadcast(); 
	}  
	
	public void onTaskChanged(DownloadStatus dlState)  
	{    
		if(dlState != null)
		{			
			try
			{
				iDownloadCallback.onTaskStatusChanged(dlState.mKey, dlState.mStatus);
			} catch (RemoteException e)
			{
				Log.e(TAG, "<onTaskChanged> but catch exception : "+e.toString(),e);
			}
			
		}
	  
	   
	}  
	
	
	
	public void onDownloading(FileDownloader fileDownloader)  
	{  	
		if(iDownloadCallback == null )
		{
			 int n = callbackList.beginBroadcast();  
		   	 for(int i = 0;i < n ;i++) 
		   	 {
		   		 iDownloadCallback = callbackList.getBroadcastItem(0);
		   	 }		   	 
	   }	   	    		
		fileDownloader.download(new DownloadProgressListener()
			{									
				@Override
				public void onDownloadSize(int cityId,String downloadURL, long downloadSpeed,long size, long fileLength,boolean notFinish)
				{
					try
					{
						DownloadStatus downloadStatus = downloadStstudTask.get(downloadURL);
						if( downloadStatus != null && downloadStatus.mStatus != PAUSE)
						{
							iDownloadCallback.onTaskProcessStatusChanged(cityId,downloadURL,downloadSpeed , fileLength, size,notFinish);
							if(!notFinish)
							{
								DownloadManager downloadManager = new DownloadManager(DownloadService.this);
								downloadManager.deleteDownloadInfo(downloadURL);
								
								Iterator<String> keys = downloadTask.keySet().iterator();
								while(keys.hasNext()){
									String key = keys.next();
									if(key.equals(downloadURL))
									{										
										downloadTask.remove(downloadURL);
										downloadStstudTask.remove(downloadURL);
									}
								}
							} 
						}else {
						}						
					}catch (Exception e)
					{
						Log.e(TAG, "<onProcessChanged> but catch exception :"+e.toString(),e);
					}
				}
			});
		if(downloadTask.size() == 0)
		{
			// callbackList.finishBroadcast();  
		}   
	}  
	
	
	
	
	public  boolean startDownloadTask(int cityId,String downloadURL, String downloadSavePath,String tempPath) throws Exception
	{
		
		boolean flag = true;
		DownloadStatus dlState = new DownloadStatus(DOWNLOADING, downloadURL);
		downloadStstudTask.put(downloadURL, dlState);
		FileDownloader fileDownloader = new FileDownloader(this, 3,cityId,downloadSavePath,tempPath,downloadURL);
		flag = fileDownloader.FileDownloaderCheeck();
		downloadTask.put(downloadURL, fileDownloader);
		if(flag)
		{
			onDownloading(fileDownloader);
		}
		return flag;
	}
	
	
	
	public void pauseDownloadTask(String downloadURL)
	{
		DownloadStatus downloadStatus = downloadStstudTask.get(downloadURL);
		downloadStatus.mStatus = PAUSE;
		downloadStstudTask.put(downloadURL, downloadStatus);
		FileDownloader fileDownloader = downloadTask.get(downloadURL);
		if(fileDownloader != null)
		{
			fileDownloader.pauseDownload();
		}
	}
	
	public void restartDownloadTask(String downloadURL)
	{
		DownloadStatus dlState = downloadStstudTask.get(downloadURL);
		dlState.mStatus = RESTART;
		FileDownloader fileDownloader = downloadTask.get(downloadURL);
		if(fileDownloader != null)
		{
			fileDownloader.restartDownload();
		}
	}
	
	
	public void cancelDownloadTask(String downloadURL)
	{
		FileDownloader fileDownloader = downloadTask.get(downloadURL);
		if(fileDownloader != null)
		{
			fileDownloader.cancelDownload();
			downloadTask.remove(downloadURL);
			downloadStstudTask.remove(downloadURL);
		}
		
	}
	
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return iDownCallService;
	}

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


	public static Map<String, FileDownloader> getDownloadTask()
	{
		return downloadTask;
	}


	public static void setDownloadTask(Map<String, FileDownloader> downloadTask)
	{
		DownloadService.downloadTask = downloadTask;
	}


	public static Map<String, DownloadStatus> getDownloadStstudTask()
	{
		return downloadStstudTask;
	}


	public static void setDownloadStstudTask(
			Map<String, DownloadStatus> downloadStstudTask)
	{
		DownloadService.downloadStstudTask = downloadStstudTask;
	}
}
