package com.damuzhi.travel.activity.more;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.viewcache.OpenCityDownloadViewcache;
import com.damuzhi.travel.activity.adapter.viewcache.OpenCityListViewCache;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.db.DownloadPreference;
import com.damuzhi.travel.download.DownloadService;
import com.damuzhi.travel.download.IDownloadCallback;
import com.damuzhi.travel.download.IDownloadService;
import com.damuzhi.travel.mission.more.DownloadMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.downlaod.DownloadBean;
import com.damuzhi.travel.model.downlaod.DownloadManager;
import com.damuzhi.travel.model.entity.DownloadInfos;
import com.damuzhi.travel.model.entity.DownloadStatus;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.ZipUtil;


public class OpenCityActivity extends Activity 
{
	private static String TAG = "OpenCityDataActivity";	
	private ListView openCtiyDataListView,downloadListView;
	private IDownloadService iDownloadService;
	private static final int PROCESS_CHANGED = 1;
	private static final int DOWNLOAD_STATUS_PAUSE  = 2;
	private static final int DOWNLOAD_ING = 1;
	private static final int UPZIP_ING = 2;
	private static final int DOWNLOAD_PAUSE = 3;
	private int currentCityId ;
	
	private OpenCityDataAdapter cityListAdapter;
	public static DownloadDataListAdapter downloadDataListAdapter;
	private List<City> cityList;
	private static Map<String, ProgressBar> progressBarMap = new HashMap<String, ProgressBar>();
	private static Map<String, TextView> resultTextMap = new HashMap<String, TextView>();
	private Map<String, Integer> positionMap = new HashMap<String, Integer>();
	private Map<String, Integer> downloadStatusMap ;
	private Map<String, Integer> udpateStatusMap;
	private Map<String, DownloadStatus> downloadStatusTask;
	private Map<String, DownloadStatus> updateStatusTask;
	public static Map<Integer, Integer> installCityData = new HashMap<Integer, Integer>();
	private Map<Integer, String> newVersionCityData = new HashMap<Integer, String>();
	List<Integer> installedCityList = new ArrayList<Integer>();
	
	private ViewGroup dataListGroup,downloadListGroup;
	private TextView dataListTitle,downloadListTitle;
	private DownloadManager downloadManager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		TravelApplication.getInstance().addActivity(this);
		setContentView(R.layout.open_city);	
		downloadManager = new DownloadManager(OpenCityActivity.this);
				
		
		findViewById(R.id.open_city_tips_download).setSelected(true);
		cityList = AppManager.getInstance().getCityList();
		installCityData = DownloadPreference.getAllDownloadInfo(OpenCityActivity.this);
		installedCityList.addAll(installCityData.keySet());
		if(installCityData != null&&installCityData.size()>0)
		{
			newVersionCityData = DownloadMission.getInstance().getNewVersionCityData(installedCityList);
		}		
		openCtiyDataListView = (ListView) findViewById(R.id.open_city_data_listview);
		downloadListView = (ListView) findViewById(R.id.download_data_listview);				
		cityListAdapter = new OpenCityDataAdapter(cityList, OpenCityActivity.this);		
		downloadDataListAdapter = new DownloadDataListAdapter(installedCityList, this);
		//initUpdateButton();
		
		View listViewFooter = getLayoutInflater().inflate(R.layout.open_data_listview_footer, null, false);
		TextView tipsTextView = (TextView) listViewFooter.findViewById(R.id.open_city_tips_update);
		SpannableString tips = new SpannableString(getString(R.string.open_city_tips2));
		tips.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, tips.length()-1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		tipsTextView.setText(tips);
		openCtiyDataListView.addFooterView(listViewFooter,cityList,false);
		openCtiyDataListView.setFooterDividersEnabled(false);
		openCtiyDataListView.setDrawingCacheEnabled(false);
		
		openCtiyDataListView.setAdapter(cityListAdapter);
		downloadListView.setAdapter(downloadDataListAdapter);
		openCtiyDataListView.setOnItemClickListener(onItemClickListener);
		
		
		dataListGroup = (ViewGroup) findViewById(R.id.data_list_group);
		downloadListGroup = (ViewGroup) findViewById(R.id.download_list_group);
		dataListTitle = (TextView) findViewById(R.id.city_list_title);
		downloadListTitle = (TextView) findViewById(R.id.download_manager_title);
		
		dataListGroup.setOnClickListener(dataListOnClickListener);
		downloadListGroup.setOnClickListener(downloadListOnClickListener);
		
		int flag = getIntent().getIntExtra("updateData", -1);
		if(flag == 1)
		{
			openCtiyDataListView.setVisibility(View.GONE);
			downloadListView.setVisibility(View.VISIBLE);
		}
		
		bindService(new Intent(OpenCityActivity.this, DownloadService.class), conn, Context.BIND_AUTO_CREATE);
	}
	
		
	
	
	
	
	
	//bindservice
	private ServiceConnection conn = new ServiceConnection()
	{		
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			Log.i(TAG, "ServiceDisConnection -> onServiceDisConnected");
			iDownloadService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			iDownloadService = IDownloadService.Stub.asInterface(service);
			if(iDownloadService != null)
			{
				try
				{
					iDownloadService.regCallback(callback);
				} catch (RemoteException e)
				{
					 Log.e(TAG, "<onServiceConnected> but catch exception :"+e.toString(),e);
				}
				
			}
		}
	};
	
	
	
	private Handler downloadInfoHandler = new Handler(){		
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			final DownloadInfos downloadInfo;
			switch (msg.what)
			{
			case PROCESS_CHANGED:
				downloadInfo = (DownloadInfos) msg.obj;
				refresh(downloadInfo);
				break;
			case -1:
				Toast.makeText(OpenCityActivity.this, R.string.error, 1).show();
				break;
			default:
				
				break;
			}
		}
		
	};

	private void refresh(Object object)
	{
		AsyncTask<Object, Void, Object> task = new AsyncTask<Object, Void, Object>()
				{

					@Override
					protected Object doInBackground(Object... params)
					{
						Object downloadInfo =  params[0];						
						return downloadInfo;	
					}

					@Override
					protected void onPostExecute(Object result)
					{
						DownloadInfos downloadInfo = (DownloadInfos) result;
						refreshDownloadProgress(downloadInfo);
						super.onPostExecute(result);
					}
				};
				Object[] params = new Object[]{object};
				task.execute(params);
	}
	
		
	private void refreshDownloadProgress( DownloadInfos downloadInfo)
	{
		ProgressBar downloadBar = progressBarMap.get(downloadInfo.getUrl());
		TextView resultView = resultTextMap.get(downloadInfo.getUrl());
		if(downloadBar != null)
		{					
			downloadBar.setMax((int)downloadInfo.getTotalBytes());
			downloadBar.setProgress((int)downloadInfo.getCurrentPosition());
			int persent = (int) (((float)downloadInfo.getCurrentPosition()/(float)downloadInfo.getTotalBytes())*100);
			resultView.setText(persent+"%");
			if(!downloadInfo.isNotFinish()){	
				String downloadURL = downloadInfo.getUrl();
				int cityId = downloadInfo.getCityId();
				int position = 0;
				if(newVersionCityData.containsKey(cityId))
				{
					downloadListView.findViewWithTag("installing"+downloadURL).setVisibility(View.VISIBLE);
					downloadListView.findViewWithTag("group"+downloadURL).setVisibility(View.GONE);
				}else
				{
					position = positionMap.get(downloadURL);
					openCtiyDataListView.invalidate();
					TextView installingTextView = (TextView) openCtiyDataListView.findViewWithTag("installing"+position);
					if(installingTextView != null)
					{
						openCtiyDataListView.findViewWithTag("button"+position).setVisibility(View.GONE);
						openCtiyDataListView.findViewWithTag("group"+position).setVisibility(View.GONE);
						installingTextView.setVisibility(View.VISIBLE);				
					}
				}				
				progressBarMap.remove(downloadURL);
				resultTextMap.remove(downloadURL);
				//cancelDownload(downloadURL);
				
										
				String zipTempFilePath = ConstantField.DOWNLOAD_TEMP_PATH+HttpTool.getTempFileName(HttpTool.getConnection(downloadURL), downloadURL);
				String zipFilePath = String.format(ConstantField.DOWNLOAD_TEMP_PATH, cityId)+HttpTool.getFileName(HttpTool.getConnection(downloadURL), downloadURL);
				File tempFile = new File(zipTempFilePath);
				File zipFile = new File(zipFilePath);
				String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);
				boolean reulst = tempFile.renameTo(zipFile);
				if(reulst)
				{														
					try
					{
						upZipFile(zipFilePath, upZipFilePath,Integer.toString(cityId),downloadURL);
						DownloadManager downloadManager = new DownloadManager(OpenCityActivity.this);
						downloadManager.deleteDownloadInfo(downloadURL);
					} catch (Exception e)
					{
						Log.e(TAG, "<downloadInfoHandler.handleMessage> but catch exception:"+e.toString(),e);
					}				
				}else
				{
					if(newVersionCityData.containsKey(cityId))
					{
						downloadListView.findViewWithTag("installing"+downloadURL).setVisibility(View.GONE);
						downloadListView.findViewWithTag("installed"+downloadURL).setVisibility(View.GONE);
					}else
					{
						openCtiyDataListView.findViewWithTag("button"+position).setVisibility(View.VISIBLE);
						openCtiyDataListView.findViewWithTag("installing"+position).setVisibility(View.GONE);
						openCtiyDataListView.findViewWithTag("installed"+position).setVisibility(View.GONE);
					}		
					deleteFile(zipFilePath,upZipFilePath);
				}	
			}else
			{
				return ;
			}					
		}							

					
	}
	
	
	
	
	private void upZipFile(String zipFilePath, String folderPath,final String cityId,final String downloadURL)
	{
		String[] params = new String[]{zipFilePath,folderPath,cityId,downloadURL};
		AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>()
		{

			@Override
			protected Boolean doInBackground(String... params)
			{
				String zipFilePath = params[0];
				String upZipFilePath = params[1];
				String cityId = params[2];
				String downloadURL = params[3];
				DownloadPreference.insertDownloadInfo(OpenCityActivity.this, cityId, 0);
				downloadStatusMap.put(downloadURL, UPZIP_ING);
				boolean result = ZipUtil.upZipFile(zipFilePath,upZipFilePath );	
				if(result)
				{
					DownloadPreference.insertDownloadInfo(OpenCityActivity.this, cityId, 1);
					int installCityId = Integer.parseInt(cityId);
					installCityData.put(installCityId, installCityId);
				}else {
					DownloadPreference.deleteDownloadInfo(OpenCityActivity.this, cityId);
					FileUtil.deleteFolder(zipFilePath);
					FileUtil.deleteFolder(upZipFilePath);
				}
				downloadStatusMap.remove(downloadURL);
				return result;
			}

			@Override
			protected void onPostExecute(Boolean result)
			{
				super.onPostExecute(result);
				int cityID = Integer.parseInt(cityId);
				refresh(result,downloadURL,cityID);
			}
		};
		task.execute(params);
	}
	
	
	private void deleteFile(String zipFilePath, String folderPath)
	{
		String[] params = new String[]{zipFilePath,folderPath};
		
		AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>()
		{

			@Override
			protected Void doInBackground(String... params)
			{
				String zipFilePath = params[0];
				String upZipFilePath = params[1];				
				FileUtil.deleteFolder(zipFilePath);
				FileUtil.deleteFolder(upZipFilePath);
				return null;
			}

			
		};
		task.execute(params);
	}
	
	
	private void refresh(boolean zipResult,String downloadURL,int cityId)
	{
		
		TextView installingTextView = null;
		TextView installedTextView = null;
		int position = positionMap.get(downloadURL);
		
			if(newVersionCityData.containsKey(cityId))
			{
				installedTextView = (TextView) downloadListView.findViewWithTag("installed"+downloadURL);
				installingTextView = (TextView) downloadListView.findViewWithTag("installing"+downloadURL);
			}else
			{
				installingTextView = (TextView) openCtiyDataListView.findViewWithTag("installing"+position);
				installedTextView = (TextView) openCtiyDataListView.findViewWithTag("installed"+position);
			}	
			
			if(zipResult)
			{
				if(newVersionCityData.containsKey(cityId))
				{
					newVersionCityData.remove(cityId);
				}
				if(installingTextView != null)
				{
					installingTextView.setVisibility(View.GONE);
					installedTextView.setVisibility(View.VISIBLE);
				}
			}else{
				if (installedTextView != null)
				{
					if(newVersionCityData.containsKey(cityId))
					{
						downloadListView.findViewWithTag(cityId).setVisibility(View.VISIBLE);
						installingTextView.setVisibility(View.GONE);
						installedTextView.setVisibility(View.GONE);
					}else
					{
						openCtiyDataListView.findViewWithTag("button"+position).setVisibility(View.VISIBLE);
						installingTextView.setVisibility(View.GONE);
						installedTextView.setVisibility(View.GONE);
					}	
				}
		}	
		
	}
	
	
	
	
	private IDownloadCallback callback = new IDownloadCallback.Stub()
	{
		
		@Override
		public void onTaskStatusChanged(String strkey, int status)throws RemoteException
		{
		}
			
		@Override
		public void onTaskProcessStatusChanged(int cityId,String downloadURL, long speed,long totalBytes, long curPos,boolean notFinish) 
		{ 
             final  DownloadInfos dl = new DownloadInfos(cityId,downloadURL,speed,totalBytes,curPos,notFinish);  	
             Thread thread = new Thread(new Runnable(
            		 )
			{
				
				@Override
				public void run()
				{
					 Message msg = Message.obtain();
					 msg.what = PROCESS_CHANGED;
					 msg.obj = dl;
				     downloadInfoHandler.sendMessage(msg);				
				}
			});
             thread.start(); 
			  
		}
	};
	
	
	
	
	private void setCityToast(String cityName)
	{
		Toast.makeText(OpenCityActivity.this,getString(R.string.set_ctiy_tips)+cityName , Toast.LENGTH_SHORT).show();
	}
	
	
	
	private void download(final int cityId,final String downloadURL, final String downloadSavePath,final String tempPath)
	{
		//Log.d(TAG, "download url = "+downloadURL);
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					boolean result = iDownloadService.startDownload(cityId,downloadURL, downloadSavePath,tempPath);
					if(!result)
					{
						Looper.prepare();
						Toast.makeText(OpenCityActivity.this, getResources().getString(R.string.download_connection_error), Toast.LENGTH_LONG).show();
						Looper.loop();
					}
				} catch (RemoteException e)
				{
					e.printStackTrace();
				}
			}
		});
		thread.start();
		}
	
	
	
	
	
	
	private void pauseDownload(final String downloadURL) 
	{
		Thread thread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{					
					iDownloadService.pauseDownload(downloadURL);
				} catch (RemoteException e)
				{
					e.printStackTrace();
				}
				
			}
		});
		thread.start();
		
		
	}
	
	
	private void restartDownload(final String downloadURL)
	{
		Thread thread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{					
					iDownloadService.restartDownload(downloadURL);
				} catch (RemoteException e)
				{
					e.printStackTrace();
				}
				
			}
		});
		thread.start();
		
	}
	
	private void cancelDownload(final String downloadURL)
	{
		Thread thread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{					
					iDownloadService.cancelDownload(downloadURL);
				} catch (RemoteException e)
				{
					e.printStackTrace();
				}
				
			}
		});
		thread.start();
		
	}
	
	
	
	
	
	


	@Override
	protected void onResume()
	{
		super.onResume();
		currentCityId = AppManager.getInstance().getCurrentCityId();
		downloadStatusMap = TravelApplication.getInstance().downloadStatusMap;
		downloadStatusTask = DownloadService.getDownloadStstudTask();
		cityListAdapter.setCityDataList(cityList);
		cityListAdapter.setDownloadStstudTask(downloadStatusTask);
		cityListAdapter.notifyDataSetChanged();
	}


	@Override
	protected void onDestroy()
	{
		unbindService(conn);
		super.onDestroy();
	}
	
	
	
	private OnClickListener dataListOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			downloadListView.setVisibility(View.GONE);
			openCtiyDataListView.setVisibility(View.VISIBLE);
			dataListGroup.setBackgroundResource(R.drawable.citybtn_on2);
			downloadListGroup.setBackgroundResource(R.drawable.citybtn_off2);
			dataListTitle.setTextColor(getResources().getColor(R.color.white));
			downloadListTitle.setTextColor(getResources().getColor(R.color.black));
			downloadStatusTask = DownloadService.getDownloadStstudTask();
			cityListAdapter.setDownloadStstudTask(downloadStatusTask);
			cityListAdapter.notifyDataSetChanged();
		}
	};
	
	
	
	private OnClickListener downloadListOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			initDownloadListview();	
		}
	};
	
	private void initDownloadListview()
	{
		downloadListView.setVisibility(View.VISIBLE);
		openCtiyDataListView.setVisibility(View.GONE);
		dataListGroup.setBackgroundResource(R.drawable.citybtn_off);
		downloadListGroup.setBackgroundResource(R.drawable.citybtn_on);
		dataListTitle.setTextColor(getResources().getColor(R.color.black));
		downloadListTitle.setTextColor(getResources().getColor(R.color.white));
		installedCityList.clear();
		installedCityList.addAll(installCityData.keySet());
		if(installedCityList.size() == 0)
		{
			downloadListView.setVisibility(View.GONE);
		}else{			
			downloadDataListAdapter.setInstalledCityList(installedCityList);
			downloadDataListAdapter.notifyDataSetChanged();
		}
	}
	
	
	private OnClickListener startDownloadClickListener = new OnClickListener()
	{	
		@Override
		public void onClick(View v)
		{
			
				int position = (Integer) v.getTag();	
				final City city = cityList.get(position);
				final String downloadURL = city.getDownloadURL();
				final String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH, city.getCityId());
				final String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);
				
				ViewGroup startGroup = (ViewGroup) openCtiyDataListView.findViewWithTag("startGroup"+position);
				ViewGroup cancleGroup = (ViewGroup) openCtiyDataListView.findViewWithTag("cancelGroup"+position);
				ViewGroup dataDownloadMangerGroup = (ViewGroup) openCtiyDataListView.findViewWithTag("group"+position);
				TextView dataSize = (TextView) openCtiyDataListView.findViewWithTag("datasize"+position);
				ImageView restartButton = (ImageView) openCtiyDataListView.findViewWithTag("restart"+position);
				restartButton.setOnClickListener(restartDownloadClickListener);
				ProgressBar downloadBar = (ProgressBar) openCtiyDataListView.findViewWithTag("bar"+city.getDownloadURL());	
				TextView resultView = (TextView) openCtiyDataListView.findViewWithTag("result"+city.getDownloadURL());	
				downloadBar.setMax(0);
				downloadBar.setProgress(0);
				resultView.setText("");
				progressBarMap.put(downloadURL, downloadBar);
				resultTextMap.put(downloadURL, resultView);
				download(city.getCityId(),downloadURL, downloadSavePath,tempPath);
				downloadStatusMap.put(downloadURL, DOWNLOAD_ING);
				startGroup.setVisibility(View.GONE);
				dataSize.setVisibility(View.GONE);
				cancleGroup.setVisibility(View.VISIBLE);
				dataDownloadMangerGroup.setVisibility(View.VISIBLE);
		}
	};
	

	
	
	private OnClickListener stopDownloadOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String tag = (String) v.getTag();
			
			int position = Integer.parseInt(tag.substring(tag.lastIndexOf("e")+1));
			City city = cityList.get(position);
			String downloadURL = city.getDownloadURL();
			pauseDownload(downloadURL);
			downloadStatusMap.put(downloadURL, DOWNLOAD_PAUSE);
			v.setVisibility(View.GONE);
			ImageView btn = (ImageView) openCtiyDataListView.findViewWithTag("restart"+position);
			btn.setVisibility(View.VISIBLE);
			
		}
	};
	
	
	
	
	
	private OnClickListener restartDownloadClickListener = new OnClickListener()
	{	
		@Override
		public void onClick(View v)
		{
			String tag = (String) v.getTag();
			int position = Integer.parseInt(tag.substring(tag.lastIndexOf("t")+1));
			City city = cityList.get(position);
			String downloadURL = city.getDownloadURL();
			String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH, city.getCityId());	
			if(!progressBarMap.containsKey(downloadURL))
			{	
				ProgressBar downloadBar = (ProgressBar) openCtiyDataListView.findViewWithTag("bar"+downloadURL);			
				TextView resultView = (TextView) openCtiyDataListView.findViewWithTag("result"+downloadURL);
				progressBarMap.put(downloadURL, downloadBar);
				resultTextMap.put(downloadURL, resultView);
			}				
			if(v.getVisibility() == View.VISIBLE)
			{
				String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);
				download(city.getCityId(),downloadURL, downloadSavePath,tempPath);
			}else {
				restartDownload(downloadURL);
			}		
			downloadStatusMap.put(downloadURL, DOWNLOAD_ING);
			ImageView pauseButton = (ImageView) openCtiyDataListView.findViewWithTag("pause"+position);					
			v.setVisibility(View.GONE);
			pauseButton.setVisibility(View.VISIBLE);
		}
	};
	
	private OnClickListener cancelOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String tag = (String) v.getTag();
			int position = Integer.parseInt(tag.substring(tag.lastIndexOf("l")+1));
			City city = cityList.get(position);
			
			downloadManager.deleteDownloadInfo(city.getDownloadURL());
			String downloadURL = city.getDownloadURL();	
			ViewGroup cancleGroup = (ViewGroup) openCtiyDataListView.findViewWithTag("cancelGroup"+position);
			ViewGroup startGroup = (ViewGroup) openCtiyDataListView.findViewWithTag("startGroup"+position);
			ViewGroup dataDownloadMangerGroup = (ViewGroup) openCtiyDataListView.findViewWithTag("group"+position);
			TextView dataSize = (TextView) openCtiyDataListView.findViewWithTag("datasize"+position);
			ProgressBar downloadBar = progressBarMap.get(downloadURL);
			TextView resultView = resultTextMap.get(downloadURL);
			
			if(downloadBar!=null)
			{
				downloadBar.setMax(0);
				downloadBar.setProgress(0);
				resultView.setText("");
				progressBarMap.remove(downloadURL);
				resultTextMap.remove(downloadURL);
				downloadStatusTask.remove(downloadURL);
				cityListAdapter.notifyDataSetChanged();
			}				
			downloadStatusMap.remove(downloadURL);
			startGroup.setVisibility(View.VISIBLE);
			dataSize.setVisibility(View.VISIBLE);
			cancleGroup.setVisibility(View.GONE);
			dataDownloadMangerGroup.setVisibility(View.GONE);
			cancelDownload(downloadURL);
		}
	};
	

	
	private OnClickListener onlineOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			int position = (Integer)v.getTag();
			City city = cityList.get(position);
			int cityId = city.getCityId();
			String cityName = city.getCountryName()+city.getCityName();
			AppManager.getInstance().setCurrentCityId(cityId);
			Intent intent = new Intent();
			intent.setClass(OpenCityActivity.this, IndexActivity.class);
			startActivity(intent);
			setCityToast(cityName);
		}
	};
	
	
	private OnClickListener updateButtonClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{		
			
			int cityId = (Integer) v.getTag();
			String downloadURL = newVersionCityData.get(cityId);
			Log.i(TAG, "update data...."+downloadURL);
			String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH,cityId);
			String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);
			ViewGroup updateStatusGroup = (ViewGroup) downloadListView.findViewWithTag("group"+downloadURL);
			TextView dataSize = (TextView) downloadListView.findViewWithTag("datasize"+downloadURL);
			ProgressBar downloadBar = (ProgressBar) downloadListView.findViewWithTag("bar"+downloadURL);	
			TextView resultView = (TextView) downloadListView.findViewWithTag("result"+downloadURL);	
			downloadBar.setMax(0);
			downloadBar.setProgress(0);
			resultView.setText("");
			progressBarMap.put(downloadURL, downloadBar);
			resultTextMap.put(downloadURL, resultView);
			download(cityId,downloadURL, downloadSavePath,tempPath);
			downloadStatusMap.put(downloadURL, DOWNLOAD_ING);	
			dataSize.setVisibility(View.GONE);
			updateStatusGroup.setVisibility(View.VISIBLE);
			//downloadListView.findViewWithTag(cityId).setVisibility(View.GONE);
			v.setVisibility(View.GONE);
		}
	};
	
	
	private OnClickListener restartUpdateOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String tag = (String) v.getTag();
			int cityId = Integer.parseInt(tag.substring(tag.lastIndexOf("t")+1));
			if(newVersionCityData.containsKey(cityId))
			{
				String downloadURL = newVersionCityData.get(cityId);
				Log.i(TAG, "restart update url "+downloadURL);
				String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH, cityId);	
				if(!progressBarMap.containsKey(downloadURL))
				{	
					ProgressBar downloadBar = (ProgressBar) openCtiyDataListView.findViewWithTag("bar"+downloadURL);			
					TextView resultView = (TextView) openCtiyDataListView.findViewWithTag("result"+downloadURL);
					progressBarMap.put(downloadURL, downloadBar);
					resultTextMap.put(downloadURL, resultView);
				}				
				if(v.getVisibility() == View.VISIBLE)
				{
					String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);
					download(cityId,downloadURL, downloadSavePath,tempPath);
				}else {
					restartDownload(downloadURL);
				}		
				downloadStatusMap.put(downloadURL, DOWNLOAD_ING);
				ImageView pauseButton = (ImageView) downloadListView.findViewWithTag("pause"+cityId);					
				v.setVisibility(View.GONE);
				pauseButton.setVisibility(View.VISIBLE);
			}
		}
	};
	
	
	
	private OnClickListener stopUpateOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{		
			
			String tag = (String) v.getTag();	
			int cityId = Integer.parseInt(tag.substring(tag.lastIndexOf("e")+1));
			if(newVersionCityData.containsKey(cityId))
			{
				String downloadURL = newVersionCityData.get(cityId);
				Log.i(TAG, "stop update cityId = "+downloadURL);
				pauseDownload(downloadURL);
				downloadStatusMap.put(downloadURL, DOWNLOAD_PAUSE);
				v.setVisibility(View.GONE);
				ImageView btn = (ImageView) downloadListView.findViewWithTag("restart"+cityId);
				btn.setVisibility(View.VISIBLE);
			}
			
		}
	};
	
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			City city = cityList.get(arg2);
			int cityId = city.getCityId();
			final String cityName = city.getCountryName()+city.getCityName();	
			AppManager.getInstance().setCurrentCityId(city.getCityId());
			final Intent intent = new Intent();
			intent.setClass(OpenCityActivity.this, IndexActivity.class);
			if(newVersionCityData.containsKey(cityId))
			{
				AlertDialog alertDialog = new AlertDialog.Builder(OpenCityActivity.this).create();
				alertDialog.setMessage(OpenCityActivity.this.getString(R.string.data_has_new_version));
				alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,OpenCityActivity.this.getString(R.string.update_now),new DialogInterface.OnClickListener()
				{					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{	
						initDownloadListview();
					}	
				} );
				alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+OpenCityActivity.this.getString(R.string.update_later),new DialogInterface.OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
						startActivity(intent);
						setCityToast(cityName);
					}
				} );
				alertDialog.show();
			}else
			{
				startActivity(intent);
				setCityToast(cityName);
			}	
		}
	};
	
	
	
	public class OpenCityDataAdapter extends BaseAdapter
	{
		private static final String TAG = "OpenCityDataAdapter";
		private List<City> cityDataList;
		private Map<String, DownloadStatus> downloadStstudTask;
		private Context context;
		ProgressBar downloadBar ;
		TextView resultTextView ;
		
		public OpenCityDataAdapter(List<City> cityList, Context context)
		{
			super();
			this.cityDataList = cityList;
			this.context = context;
		}

		@Override
		public int getCount()
		{
			return cityDataList.size();
		}

		@Override
		public Object getItem(int position)
		{
			return cityDataList.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			
			OpenCityListViewCache viewCache; 
			if (convertView == null)
			{
				convertView = LayoutInflater.from(context).inflate(R.layout.open_city_item, null);
				viewCache = new OpenCityListViewCache(convertView);
				convertView.setTag(viewCache);
			}else
			{
				viewCache = (OpenCityListViewCache) convertView.getTag();
			}
			
			City city = cityDataList.get(position);
			String downloadURL = city.getDownloadURL();
			positionMap.put(downloadURL, position);
			TextView dataCityName = viewCache.getDataCityName();
			ImageView dataSelectIcon = viewCache.getDataSelectIcon();
			if(city.getCityId() == currentCityId)
			{
				
				dataSelectIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.yes_s));
				dataCityName.setTextColor(context.getResources().getColor(R.color.red));
			}else
			{
				dataSelectIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.no_s));
				dataCityName.setTextColor(context.getResources().getColor(R.color.black));
			}
			DownloadBean downloadBean = downloadManager.getUnfinishDownTask(downloadURL);
			dataCityName.setText(city.getCountryName()+"."+city.getCityName());
			TextView dataSize = viewCache.getDataSize();
			ViewGroup buttonGroup = viewCache.getButtonGroup();
			TextView installedTextView = viewCache.getInstalledTextView();
			TextView installingTextView = viewCache.getInstallingTextView();
			ViewGroup dataDownloadMangerGroup = viewCache.getDataDownloadMangerGroup();
			ImageView restartDownloadBtn = viewCache.getRestartDownloadBtn();
			ImageView stopDownloadBtn = viewCache.getStopDownloadBtn();
			ImageButton onlineButton = viewCache.getOnlineButton();				
			ImageButton startButton = viewCache.getStartButton();
			ImageButton cancelButton = viewCache.getCancelButton();
			ViewGroup startGroup = viewCache.getStartGroup();
			ViewGroup cancelGroup = viewCache.getCancelGroup();
			downloadBar = viewCache.getDownloadBar();
			resultTextView = viewCache.getResultTextView();
			//int downloadStatus = DownloadPreference.getDownloadInfo(context, downloadURL);
			if(installCityData!=null && installCityData.containsKey(city.getCityId()))
			{
				installedTextView.setVisibility(View.VISIBLE);
				buttonGroup.setVisibility(View.GONE);
				dataDownloadMangerGroup.setVisibility(View.GONE);
				installingTextView.setVisibility(View.GONE);
				dataSize.setVisibility(View.GONE);
				//installedCityList.add(city.getCityId());
				
			}else if (downloadStatusMap.containsKey(downloadURL)&&downloadStatusMap.get(downloadURL) == UPZIP_ING)
			{			
				buttonGroup.setVisibility(View.GONE);
				dataDownloadMangerGroup.setVisibility(View.GONE);
				installingTextView.setVisibility(View.VISIBLE);
				installingTextView.setTag("installing"+position);
				installedTextView.setTag("installed"+position);
			}else
			{		
				installingTextView.setVisibility(View.GONE);
				buttonGroup.setVisibility(View.VISIBLE);
				installedTextView.setVisibility(View.GONE);
				if(downloadStstudTask.containsKey(downloadURL)&&downloadStstudTask.get(downloadURL).mStatus != DOWNLOAD_STATUS_PAUSE)
				{
					dataDownloadMangerGroup.setVisibility(View.VISIBLE);
					restartDownloadBtn.setVisibility(View.GONE);
					stopDownloadBtn.setVisibility(View.VISIBLE);
					startGroup.setVisibility(View.GONE);
					cancelGroup.setVisibility(View.VISIBLE);
					dataSize.setVisibility(View.GONE);
					if(downloadBean !=null)
					{
						downloadBar.setMax(downloadBean.getFileLength());
						downloadBar.setProgress(downloadBean.getDownloadLength());
						String result = (int)((float)downloadBean.getDownloadLength()/(float)downloadBean.getFileLength()*100)+"%";
						resultTextView.setText(result);
					}	
					progressBarMap.put(downloadURL, downloadBar);
					resultTextMap.put(downloadURL, resultTextView);
				}else
				{			
					if(downloadBean != null)
					{	
						startGroup.setVisibility(View.GONE);
						cancelGroup.setVisibility(View.VISIBLE);
						dataSize.setVisibility(View.GONE);
						dataDownloadMangerGroup.setVisibility(View.VISIBLE);
						restartDownloadBtn.setVisibility(View.VISIBLE);
						stopDownloadBtn.setVisibility(View.GONE);
						downloadBar.setMax(downloadBean.getFileLength());
						downloadBar.setProgress(downloadBean.getDownloadLength());
						String result = (int)((float)downloadBean.getDownloadLength()/(float)downloadBean.getFileLength()*100)+"%";
						resultTextView.setText(result);
					}else {	
						dataSize.setText(TravelUtil.getDataSize(city.getDataSize()));
						dataSize.setVisibility(View.VISIBLE);
						dataDownloadMangerGroup.setVisibility(View.GONE);
						if(city.getDataSize() !=0)
						{	
							startGroup.setVisibility(View.VISIBLE);
							cancelGroup.setVisibility(View.GONE);
							
						}else {
							startGroup.setVisibility(View.GONE);
							cancelGroup.setVisibility(View.GONE);
						}
					}
				}			
				onlineButton.setTag(position);
				startButton.setTag(position);
				startGroup.setTag("startGroup"+position);
				cancelGroup.setTag("cancelGroup"+position);
				dataDownloadMangerGroup.setTag("group"+position);
				buttonGroup.setTag("button"+position);
				installedTextView.setTag("installed"+position);
				installingTextView.setTag("installing"+position);
				cancelButton.setTag("cancel"+position);		
				restartDownloadBtn.setTag("restart"+position);
				stopDownloadBtn.setTag("pause"+position);
				resultTextView.setTag("result"+city.getDownloadURL());
				downloadBar.setTag("bar" + city.getDownloadURL());
				dataSize.setTag("datasize"+position);
				startButton.setOnClickListener(startDownloadClickListener);
				cancelButton.setOnClickListener(cancelOnClickListener);
				restartDownloadBtn.setOnClickListener(restartDownloadClickListener);
				stopDownloadBtn.setOnClickListener(stopDownloadOnClickListener);
				onlineButton.setOnClickListener(onlineOnClickListener);
			}			
			return convertView;
		}

		public List<City> getCityDataList()
		{
			return cityDataList;
		}

		public void setCityDataList(List<City> cityDataList)
		{
			this.cityDataList = cityDataList;
		}

		public Map<String, DownloadStatus> getDownloadStstudTask()
		{
			return downloadStstudTask;
		}

		public void setDownloadStstudTask(Map<String, DownloadStatus> downloadStstudTask)
		{
			this.downloadStstudTask = downloadStstudTask;
		}
		

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public class DownloadDataListAdapter extends BaseAdapter
	{
		private List<Integer> installedCityList;
		private Context context;
		
		
		
		public DownloadDataListAdapter(List<Integer> installedCityList,
				Context context)
		{
			super();
			this.installedCityList = installedCityList;
			this.context = context;
		}

		@Override
		public int getCount()
		{
			return installedCityList.size();
		}

		@Override
		public Object getItem(int position)
		{
			return installedCityList.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			
			OpenCityDownloadViewcache viewCache; 
			if (convertView == null)
			{
				convertView = LayoutInflater.from(context).inflate(R.layout.open_download_city_list_item, null);
				viewCache = new OpenCityDownloadViewcache(convertView);
				convertView.setTag(viewCache);
			}else
			{
				viewCache = (OpenCityDownloadViewcache) convertView.getTag();
			}
			if(installedCityList != null && installedCityList.size()>0)
			{
				int cityId = installedCityList.get(position);
				City city = AppManager.getInstance().getCityByCityId(cityId);
				Button deleteButton = viewCache.getDeleteButton();
				Button updateButton = viewCache.getUpdateButton();
				ImageView restartUpdateBtn = viewCache.getRestartUpdateBtn();
				ImageView stopUpdateBtn = viewCache.getStopUpdateBtn();
				ViewGroup updateStatusGroup = viewCache.getUpdateStatusGroup();
				TextView updateTextView = viewCache.getUpdateTextView();
				ProgressBar updateBar = viewCache.getUpdateBar();
				TextView cityName = viewCache.getDataCityName();
				TextView citySize = viewCache.getDataSize();
				TextView installingTextView = viewCache.getUpdatingTextView();
				TextView installedTextView = viewCache.getUpdatedTextView();
				if(city != null)
				{
					String dataName = city.getCountryName()+"."+city.getCityName();
					deleteButton.setTag(position);
					cityName.setText(dataName);
					citySize.setText(TravelUtil.getDataSize(city.getDataSize()));
					deleteButton.setOnClickListener(deleteOnClickListener);
					if(newVersionCityData.containsKey(cityId))
					{
						String downloadURL = newVersionCityData.get(cityId);
						updateButton.setVisibility(View.VISIBLE);
						DownloadBean downloadBean = downloadManager.getUnfinishDownTask(downloadURL);					
						if(downloadStatusTask.containsKey(downloadURL)&&downloadStatusTask.get(downloadURL).mStatus != DOWNLOAD_STATUS_PAUSE)
						{
							updateButton.setVisibility(View.GONE);
							updateStatusGroup.setVisibility(View.VISIBLE);
							restartUpdateBtn.setVisibility(View.GONE);
							stopUpdateBtn.setVisibility(View.VISIBLE);
							citySize.setVisibility(View.GONE);
							if(downloadBean !=null)
							{
								updateBar.setMax(downloadBean.getFileLength());
								updateBar.setProgress(downloadBean.getDownloadLength());
								String result = (int)((float)downloadBean.getDownloadLength()/(float)downloadBean.getFileLength()*100)+"%";
								updateTextView.setText(result);
							}	
							progressBarMap.put(downloadURL, updateBar);
							resultTextMap.put(downloadURL, updateTextView);
						}else
						{			
							if(downloadBean != null)
							{	
								updateButton.setVisibility(View.GONE);
								citySize.setVisibility(View.GONE);
								updateStatusGroup.setVisibility(View.VISIBLE);
								restartUpdateBtn.setVisibility(View.VISIBLE);
								stopUpdateBtn.setVisibility(View.GONE);
								updateBar.setMax(downloadBean.getFileLength());
								updateBar.setProgress(downloadBean.getDownloadLength());
								String result = (int)((float)downloadBean.getDownloadLength()/(float)downloadBean.getFileLength()*100)+"%";
								updateTextView.setText(result);
							}else {	
								citySize.setText(TravelUtil.getDataSize(city.getDataSize()));
								citySize.setVisibility(View.VISIBLE);
								updateStatusGroup.setVisibility(View.GONE);
							}
						
						}
						
						updateButton.setTag(cityId);
						installedTextView.setTag("installed"+downloadURL);
						installingTextView.setTag("installing"+downloadURL);		
						restartUpdateBtn.setTag("restart"+cityId);
						stopUpdateBtn.setTag("pause"+cityId);
						updateTextView.setTag("result"+downloadURL);
						updateBar.setTag("bar" +downloadURL);
						updateStatusGroup.setTag("group"+downloadURL);
						citySize.setTag("datasize"+downloadURL);
						updateButton.setOnClickListener(updateButtonClickListener);
						restartUpdateBtn.setOnClickListener(restartUpdateOnClickListener);
						stopUpdateBtn.setOnClickListener(stopUpateOnClickListener);
					}else
					{
						updateButton.setVisibility(View.GONE);
					}
				}
			}else {
				convertView.findViewById(R.id.open_city_tips_download).setVisibility(View.VISIBLE);
			}
			
			return convertView;
		}

		public List<Integer> getInstalledCityList()
		{
			return installedCityList;
		}

		public void setInstalledCityList(List<Integer> installedCityList)
		{
			this.installedCityList = installedCityList;
		}

		
		private OnClickListener deleteOnClickListener = new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				
				final int position = (Integer) v.getTag();
				AlertDialog deleteAlertDialog = new AlertDialog.Builder(context).create();
				deleteAlertDialog.setMessage(context.getString(R.string.delete_download_data));
				deleteAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,context.getString(R.string.ok),new DialogInterface.OnClickListener()
				{					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{					
						int cityId = installedCityList.get(position);
						City city = AppManager.getInstance().getCityByCityId(cityId);
						String downloadURL = city.getDownloadURL();
						String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);			
						installedCityList.remove(position);
						if(newVersionCityData.containsKey(cityId))
						{
							newVersionCityData.remove(cityId);					
						}			
						if(progressBarMap.containsKey(downloadURL))
						{
							progressBarMap.remove(downloadURL);
							resultTextMap.remove(downloadURL);
						}
						if(downloadStatusTask.containsKey(downloadURL))
						{
							downloadStatusTask.remove(downloadURL);
							cancelDownload(downloadURL);
						}
						OpenCityActivity.installCityData.remove(cityId);
						OpenCityActivity.downloadDataListAdapter.setInstalledCityList(installedCityList);
						OpenCityActivity.downloadDataListAdapter.notifyDataSetChanged();
						DownloadPreference.deleteDownloadInfo(context, Integer.toString(cityId));
						deleteFile(upZipFilePath);						
					}	
				} );
				deleteAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+context.getString(R.string.cancel),new DialogInterface.OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
						
					}
				} );
				deleteAlertDialog.show();		
			}
		};


		private void deleteFile( String folderPath)
		{
			String[] params = new String[]{folderPath};
			
			AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>()
			{

				@Override
				protected Void doInBackground(String... params)
				{
					String gcZipFilePath = params[0];				
					FileUtil.deleteFolder(gcZipFilePath);
					return null;
				}
			};
			task.execute(params);
		}
		
		
		
		
		
	}
	

}
