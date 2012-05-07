package com.damuzhi.travel.activity.more;

import java.util.HashMap;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.download.DownLoadService;
import com.damuzhi.travel.download.IDownloadCallback;
import com.damuzhi.travel.download.IDownloadService;
import com.damuzhi.travel.model.entity.DownloadInfo;


public class OpenCityDataActivity extends MenuActivity 
{
private static String TAG = "OpenCityDataActivity";	
private ListView openCtiyDataListView;
private String [] city = {"http://xiazai.kugou.com/Down/kugou_1182.exe","http://down.kuwo.cn/mbox/kuwo2012.exe"};
private IDownloadService iDownloadService;
private static final String SD_PATH = "/mnt/sdcard/";
private static final int PROCESS_CHANGED = 1;
private static final int TASK_CHANGED = 2;
private OpenCityDataAdapter adapter;
private Map<String, ProgressBar> progressBarMap = new HashMap<String, ProgressBar>();
private Map<String, TextView> resultTextMap = new HashMap<String, TextView>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.open_city);
		openCtiyDataListView = (ListView) findViewById(R.id.open_city_download_data);			
		ViewGroup mContainer = (ViewGroup) findViewById(R.id.city_data_group);
		 adapter = new OpenCityDataAdapter(city, OpenCityDataActivity.this);
		openCtiyDataListView.setAdapter(adapter);
		//openCtiyDataListView.setOnItemClickListener(itemClickListener);
		//设置需要保存缓存  
		bindService(new Intent(OpenCityDataActivity.this, DownLoadService.class), conn, Context.BIND_AUTO_CREATE);
        mContainer.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
	}
	
	
	//绑定bindservice，获得后台AIDL下载服务
	private ServiceConnection conn = new ServiceConnection()
	{
		
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			// TODO Auto-generated method stub
			Log.i(TAG, "ServiceDisConnection -> onServiceDisConnected");
			iDownloadService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			// TODO Auto-generated method stub
			//Log.i(TAG, "ServiceConnection -> onServiceConnected");
			iDownloadService = IDownloadService.Stub.asInterface(service);
			if(iDownloadService != null)
			{
				try
				{
					iDownloadService.regCallback(callback);
				} catch (RemoteException e)
				{
					// TODO: handle exception
					 e.printStackTrace();
				}
				
			}
		}
	};
	
	//获得后台回调接口，获取下载数据
	private IDownloadCallback callback = new IDownloadCallback.Stub()
	{
		
		@Override
		public void onTaskStatusChanged(String strkey, int status)
				throws RemoteException
		{
			// TODO Auto-generated method stub
			 if(status == 4)  
	            {  
	                Message msg = handler.obtainMessage(TASK_CHANGED,4);  
	                handler.sendMessage(msg);  
	            }  
		}
		
		@Override
		public void onTaskProcessStatusChanged(String strkey, long speed,
				long totalBytes, long curPos) throws RemoteException
		{
			 int pid = android.os.Process.myPid();  
	            //Log.d("****client***pid","pdi ="+pid);  
	           final  DownloadInfo dl = new DownloadInfo(strkey,speed,totalBytes,curPos);  	           
	            Thread thread = new Thread(new Runnable()
	    		{    			
	    			@Override
	    			public void run()
	    			{		
    					 Message msg = Message.obtain();
    					 msg.what = PROCESS_CHANGED;
    					 msg.obj = dl;
    			         handler.sendMessage(msg);  
	    				}		
	    		});
	    		thread.start();
		}
	};
	
	OnClickListener downloadClickListener = new OnClickListener()
	{	
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.data_city_download:
				int position = (Integer) v.getTag();	
				String downloadPath = city[position];
				Button pauseButton = (Button) openCtiyDataListView.findViewWithTag("pause"+position);
				if(progressBarMap.containsKey(downloadPath))
				{	
					download(downloadPath, SD_PATH);
				}else
				{
					ProgressBar downloadBar = (ProgressBar) openCtiyDataListView.findViewWithTag("bar"+position);			
					TextView resultView = (TextView) openCtiyDataListView.findViewWithTag("result"+position);					
					progressBarMap.put(downloadPath, downloadBar);
					resultTextMap.put(downloadPath, resultView);
					download(downloadPath, SD_PATH);
				}								
				v.setVisibility(View.GONE);
				pauseButton.setVisibility(View.VISIBLE);
				break;
			case R.id.data_city_download_pause:
				String tag = (String) v.getTag();
				int positions = Integer.parseInt(tag.substring(tag.lastIndexOf("e")+1));
				String downloadPaths = city[positions];
				pauseDownload(downloadPaths);
				v.setVisibility(View.GONE);
				Button btn = (Button) openCtiyDataListView.findViewWithTag(positions);
				btn.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
				
		}
	};
	
	
	private Handler handler = new Handler(){		
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what)
			{
			case PROCESS_CHANGED:
				DownloadInfo downloadInfo = (DownloadInfo) msg.obj;	
				ProgressBar downloadBar = progressBarMap.get(downloadInfo.url);
				if(downloadBar != null)
				{
					TextView resultView = resultTextMap.get(downloadInfo.url);
					downloadBar.setMax((int)downloadInfo.totalBytes);
					downloadBar.setProgress((int)downloadInfo.currentPosition);
					float result = (float)downloadBar.getProgress()/(float)downloadBar.getMax();
					int persent = (int) (result*100);
					resultView.setText(persent+"%");
					//Log.i(TAG, downloadInfo.url+"  ==   "+downloadInfo.currentPosition);
					if(downloadBar.getProgress()==downloadBar.getMax()){
						Toast.makeText(OpenCityDataActivity.this, R.string.success, 1).show();
						progressBarMap.remove(downloadInfo.url);
						resultTextMap.remove(downloadInfo.url);
					}
				}
				
				break;
			case -1:
				Toast.makeText(OpenCityDataActivity.this, R.string.error, 1).show();
				break;
			default:
				
				break;
			}
		}
		
	};
	
	
//对于UI控件的更新只能由主线程(UI线程)负责，如果在非UI线程更新UI控件，更新的结果不会反映在屏幕上，某些控件还会出错
	private void download(final String strKey, final String strSavePath)
	{	
		Thread thread = new Thread(new Runnable()
		{	
			@Override
			public void run()
			{
				try
				{					
					iDownloadService.addTask(strKey, strKey, strSavePath);				
				} catch (RemoteException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}		
			});
		thread.start();	
		}
	
	//pause a task download
	private void pauseDownload(final String downloadPath) 
	{
		Thread thread = new Thread(new Runnable()
		{	
			@Override
			public void run()
			{
				try
				{					
					iDownloadService.pauseTask(downloadPath);
					resultTextMap.remove(downloadPath);
					progressBarMap.remove(downloadPath);
				} catch (RemoteException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}		
			});
		thread.start();	
	}
	
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unbindService(conn);
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	
	
	
	
	
	
	public class OpenCityDataAdapter extends BaseAdapter
	{
	private String []city;
	private OpenCityDataActivity context;

		/**
	 * @param city
	 * @param context
	 */
	public OpenCityDataAdapter(String[] city, OpenCityDataActivity context)
	{
		super();
		this.city = city;
		this.context = context;
	}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return city.length;
		}

		@Override
		public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// TODO Auto-generated method stub
			if(convertView == null)
			{
				convertView = LayoutInflater.from(context).inflate(R.layout.open_city_item, null);
			}
			//convertView.setTag(position);
			Button downloadButton = (Button) convertView.findViewById(R.id.data_city_download);
			Button pauseButton = (Button) convertView.findViewById(R.id.data_city_download_pause);
			ProgressBar downloadBar = (ProgressBar) convertView.findViewById(R.id.downloadbar);
			TextView resultTextView = (TextView) convertView.findViewById(R.id.download_persent);
			downloadButton.setOnClickListener(downloadClickListener);
			pauseButton.setOnClickListener(downloadClickListener);
			downloadButton.setTag(position);;
			pauseButton.setTag("pause"+position);
			resultTextView.setTag("result"+position);
			downloadBar.setTag("bar"+position);
			return convertView;
		}

		
		
		
		
	}
}
