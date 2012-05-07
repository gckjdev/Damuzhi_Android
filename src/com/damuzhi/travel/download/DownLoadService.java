package com.damuzhi.travel.download;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.model.entity.DLState;

public class DownLoadService extends Service
{
	private static final String TAG ="DownLoadService";
	private static final int FINISH = 1;
	private static final int PAUSE = 2;
	private static final int DOWNLOADING = 3;
	private static final int FAILED = 4;
	private static final int RESTART = 5;
	// 将所有任务放到任务集合中
	public static Map<String,FileDownloader> task = new HashMap<String,FileDownloader>();
	public static Map<String, DLState> DLstateTask = new HashMap<String, DLState>();
	public static IDownloadCallback iDownloadCallback;
	private final  IDownloadService.Stub iDownCallService = new IDownloadService.Stub()
	
	{
		
		@Override
		public void unregCallback(IDownloadCallback cb) throws RemoteException
		{
			// TODO Auto-generated method stub
			unRegCallbackService(cb);
		}
		
		@Override
		public void setMaxTaskCount(int count) throws RemoteException
		{
			// TODO Auto-generated method stub
			// setTaskCount(count); 
		}
		
		@Override
		public void regCallback(IDownloadCallback cb) throws RemoteException
		{
			// TODO Auto-generated method stub
			regCallbackService(cb);
		}
		
		@Override
		public void pauseTask(String strKey) throws RemoteException
		{
			// TODO Auto-generated method stub
			pauseTaskService(strKey);
		}
		
		@Override
		public void cancelTask(String strkey) throws RemoteException
		{
			// TODO Auto-generated method stub
			restartDownload(strkey);
		}
		
		@Override
		public boolean addTask(String strKey, String strURL, String strSavePath)
				throws RemoteException
		{
			// TODO Auto-generated method stub
			boolean flag = false;
			try
			{
				flag = addTaskService(strKey, strURL, strSavePath);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return flag;
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
	
	//暂停下载
	public void onTaskChanged(DLState state)  
	{  
	    //if(state.mStatus == DLState.FAILED || state.mStatus == DLState.FINISH)  
	    {}  //removeFromQueue(state.mKey,false);  
	  if(iDownloadCallback == null)
	  {
		  int n = callbackList.beginBroadcast();  
		    
	    	 for(int i = 0;i < n; i++)  
	 	    {  
	 	        try {  
	 	        	callbackList.getBroadcastItem(i).onTaskStatusChanged(state.mKey,state.mStatus);  
	 	        } catch (RemoteException e) {  
	 	            // TODO Auto-generated catch block  
	 	            e.printStackTrace();  
	 	        }  
	 	    }  
		
	   
	    callbackList.finishBroadcast();  
	  }
	   
	}  
	
	
	
	//更新下载信息
	public void onProcessChanged(FileDownloader downloader)  
		{  
		    int pid = android.os.Process.myPid();  
		   // Log.d("********pid****service","pid ="+pid);  
		    
		    	if(iDownloadCallback == null )
		    	{
		    		 int n = callbackList.beginBroadcast();  
			    	 for(int i = 0;i < n ;i++)  
					    {  	    		 
					        try {    
					        	/*Set<String> keyStr = task.keySet();
					        	for(String key:keyStr)
					        	{*/
					        		//FileDownloader fileDownloader = task.get(key);
					        	FileDownloader fileDownloader =downloader;
					        		fileDownloader.download(new DownloadProgressListener()
									{									
										@Override
										public void onDownloadSize(String strKey, long size, long fileLength)
										{
											// TODO Auto-generated method stub
											try
											{
												DLState dlState = DLstateTask.get(strKey);
												if(dlState.mStatus == PAUSE)
												{
													return;
												}
												iDownloadCallback = callbackList.getBroadcastItem(0);
												iDownloadCallback.onTaskProcessStatusChanged(strKey, size, fileLength, 0);
												if(size == fileLength)
												{
													task.remove(strKey);
												}
											} catch (RemoteException e)
											{
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									});
					        		
					        	//}
					        	
					        } catch (Exception e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}  
					    } 
		    	}else
		    	{
		    		try {    
			        	/*Set<String> keyStr = task.keySet();
			        	for(String key:keyStr)
			        	{*/
			        		//Log.d(TAG, "key = "+key);
			        		//FileDownloader fileDownloader = task.get(key);
			        		FileDownloader fileDownloader = downloader;
			        		fileDownloader.download(new DownloadProgressListener()
							{
								
								@Override
								public void onDownloadSize(String strKey, long size, long fileLength)
								{
									// TODO Auto-generated method stub
									try
									{
										DLState dlState = DLstateTask.get(strKey);
										if(dlState.mStatus == PAUSE)
										{
											return;
										}
										iDownloadCallback = callbackList.getBroadcastItem(0);
										iDownloadCallback.onTaskProcessStatusChanged(strKey, size, fileLength, 0);
										Log.d(TAG, "url = "+strKey);
										if(size == fileLength)
										{
											task.remove(strKey);
										}
									} catch (RemoteException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
			        		
			        	//}
			        	
			        } catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
		    	}					    	 
		    callbackList.finishBroadcast();  
		}  
	
	
	//添加任务到下载队列
	public  boolean addTaskService(String strKey, String strURL, String strSavePath) throws Exception
	{
		boolean flag = false;
		/*if(task.containsKey(strKey))
		{
			DLState dlState = DLstateTask.get(strKey);
			dlState.mStatus = RESTART;
			onProcessChanged();
			flag = true;
		}else {*/
			FileDownloader fileDownloader = new FileDownloader(this, 3);
			File dir = new File(strSavePath);
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				dir = Environment.getExternalStorageDirectory();//文件保存目录
			}else{
				Toast.makeText(DownLoadService.this, R.string.sdcarderror, 1).show();
			}
			flag = fileDownloader.FileDownloaderCheeck(strURL, dir);
			Log.d(TAG, "flag = "+flag);
			if(flag)
			{
				Log.d(TAG, strKey+"downloading");
				task.put(strKey, fileDownloader);
				DLState dlState = new DLState(DOWNLOADING, strKey);
				DLstateTask.put(strKey, dlState);
				onProcessChanged(fileDownloader);
					
			}
		//}
		
		return flag;
	}
	
	
	//暂停下载
	public void pauseTaskService(String strKey)
	{
		DLState dlState = DLstateTask.get(strKey);
		dlState.mStatus = PAUSE;
		FileDownloader fileDownloader = task.get(strKey);
		//Log.d(TAG, "url = "+strKey);
		fileDownloader.pauseDownload();
		//task.remove(strKey);
		//DLstateTask.remove(strKey);
	}
	//重启下载
	public void restartDownload(String downloadPath)
	{
		DLState dlState = DLstateTask.get(downloadPath);
		dlState.mStatus = RESTART;
		FileDownloader fileDownloader = task.get(downloadPath);
		//Log.d(TAG, "url = "+strKey);
		fileDownloader.restartDownload();
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return iDownCallService;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		
		super.onCreate();
		
	}

	
}
