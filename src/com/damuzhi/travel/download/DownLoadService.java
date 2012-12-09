package com.damuzhi.travel.download;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.downlaod.DownloadBean;
import com.damuzhi.travel.model.entity.DownloadInfo;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.ZipUtil;
import com.damuzhi.travel.util.ZipUtil2;
import com.loopj.android.http.AsyncHttpClient;

import dalvik.system.VMRuntime;

public class DownloadService extends Service
{
	private static final String TAG = "DownLoadService";

	
	private static final int DOWNLOADING = 1;
	private static final int PAUSE = 2;
	private static final int UPZIPING = 3;
	private static final int UNZIP_PAUSE = 4;
	
	private static final int PROCESS_CHANGED = 1;
	private static final int UPZIP = 2;
	private static final int CONNECTION_ERROR = 3;
	public static Map<String, Integer> downloadStstudTask = new HashMap<String, Integer>();
	private  Map<String, DownloadHandler> downloadControlMap = new HashMap<String, DownloadHandler>();
	private  Map<String, DownloadBean> downloadInfoMap = new HashMap<String, DownloadBean>();
	public static  Handler downloadHandler;
	private boolean isLowMemory = false;
	private final static float TARGET_HEAP_UTILIZATION = 0.75f;
	private AsyncHttpClient client;
	private boolean isUnZiping = false;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d(TAG, "service onStartCommand");
		Log.d(TAG, "is low memory = "+isLowMemory);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);
		client = TravelApplication.getInstance().getAsyncHttpClient();
		Log.d(TAG, "service onCreate");
	}

	
	
	static ExecutorService downloadExecutorService = Executors.newSingleThreadExecutor();
	
	public  void download(final int cityId, final String downloadURL,  final String savePath,  final String tempPath,final String upZipFilePath)
	{
		downloadExecutorService.execute(new Runnable()
		{
			@Override
			public void run()
			{
				File fileSaveDir = new File(savePath);
				if (!fileSaveDir.exists())
					fileSaveDir.mkdirs();

				File tempPathDir = new File(tempPath);
				if (!tempPathDir.exists())
					tempPathDir.mkdirs();
				String fileName = TravelUtil.getDownloadFileName(downloadURL);
				//tempPath = tempPath+fileName;
				String saveTempPath = tempPath+fileName;
				//savePath = savePath+fileName;
				final String zipFilePath = savePath+fileName;
				Log.d(TAG, "download file, temp=" + saveTempPath + ", save=" + savePath);

				long startDownloadLength = FileUtil.getFileSize(saveTempPath);
				client.addHeader("Range", "bytes=" + startDownloadLength + "-");
				
				DownloadHandler cityHandler = new DownloadHandler(savePath, saveTempPath)
				{
					@Override
					public void onSuccess(byte[] fileData)
					{			
						// TODO post successfully download		
						cancelHttpDownload(downloadURL);
						downloadInfoMap.remove(downloadURL);		
						allPauseDownload();			
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
						 DownloadInfo dl = new DownloadInfo(cityId,downloadURL,fileTotalLength,downloadLength,notFinish,false);  	
						 Message msg = Message.obtain();
						 msg.what = PROCESS_CHANGED;
						 msg.obj = dl;
					     downloadHandler.sendMessage(msg);
					}

					@Override
					public void onFailure(Throwable e, byte[] arg1)
					{
						 DownloadInfo dl = new DownloadInfo(cityId,downloadURL,fileTotalLength,downloadLength,true,false);
						 Message msg = Message.obtain();
						 msg.what = CONNECTION_ERROR;
						 msg.obj = dl;
					     downloadHandler.sendMessage(msg);
						 Log.e(TAG, "<onFailure> downloading failure="+e.toString());
					}

				};
				
				downloadControlMap.put(downloadURL, cityHandler);
				client.get(getApplicationContext(),downloadURL, cityHandler);
				
			}
		});
	}
	
	private  void cancelHttpDownload(String downloadURL)
	{
		if (downloadControlMap.containsKey(downloadURL)){
			Log.d(TAG, "cancle download url = "+downloadURL);
			DownloadHandler downloadHandler = downloadControlMap.get(downloadURL);
			downloadHandler.cancelDownload();
			client.delete(getApplicationContext(),downloadURL, downloadHandler);
			downloadHandler = null;
			downloadControlMap.remove(downloadURL);
		}
		
		
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

	private void allPauseDownload()
	{
		Log.d(TAG, "pause all downloading city data ");
		for(DownloadHandler downloadHandler:downloadControlMap.values()){
			downloadHandler.cancelDownload();
			downloadHandler = null;
		}
		for(String downloadURL:downloadInfoMap.keySet()){
			Log.d(TAG, "pause all download task except unziping task");
			downloadStstudTask.put(downloadURL, UNZIP_PAUSE);
		}
		downloadControlMap.clear();
	}
	
	
	private void allStartDownload(){
		isUnZiping = false;
		Log.d(TAG, "start all pasue download city data");
		for(DownloadBean downloadBean:downloadInfoMap.values()){
			startDownload(downloadBean.getCityId(), downloadBean.getDownloadURL(),downloadBean.getSavePath(),downloadBean.getTempPath(),downloadBean.getUpZipFilePath());
		}
	}
	
	
	
	
	public  void startDownload(int cityId, String downloadURL,String downloadSavePath, String tempPath,String upZipFilePath)
	{
		DownloadBean downloadBean = new DownloadBean(cityId, downloadURL, downloadSavePath, tempPath,upZipFilePath, 0, 0);
		downloadStstudTask.put(downloadURL, DOWNLOADING);
		downloadInfoMap.put(downloadURL, downloadBean);
		download(cityId, downloadURL, downloadSavePath, tempPath,upZipFilePath);
		Log.d(TAG, "isUnZiping = "+isUnZiping);
		if(isUnZiping){
			allPauseDownload();
		}
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
				downloadStstudTask.put(downloadURL, UPZIPING);
				isUnZiping = true;
				boolean result = ZipUtil.upZipFile(zipFilePath, upZipFilePath);
				//ZipUtil2 zipUtil2 = new ZipUtil2();
				//boolean result = zipUtil2.unZipToFolder(zipFilePath, upZipFilePath);
				allStartDownload();
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
			    DownloadInfo downloadInfos = new DownloadInfo(cityId, downloadURL, 0, 0, false, result);
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

	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		Log.d(TAG, "service on low menory ");
		allPauseDownload();
	}
	
}
