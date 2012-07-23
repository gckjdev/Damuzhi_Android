package com.damuzhi.travel.activity.more;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
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
import com.damuzhi.travel.activity.adapter.download.DownloadDataListAdapter;
import com.damuzhi.travel.activity.adapter.viewcache.OpenCityListViewCache;
import com.damuzhi.travel.activity.adapter.viewcache.TravelRoutesViewCache;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.db.DownloadPreference;
import com.damuzhi.travel.download.DownloadService;
import com.damuzhi.travel.download.IDownloadCallback;
import com.damuzhi.travel.download.IDownloadService;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.downlaod.DownloadBean;
import com.damuzhi.travel.model.downlaod.DownloadManager;
import com.damuzhi.travel.model.entity.DownloadInfos;
import com.damuzhi.travel.model.entity.DownloadStatus;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.ZipUtil;


public class OpenCityActivity extends Activity 
{
	private static String TAG = "OpenCityDataActivity";	
	private ListView openCtiyDataListView,downloadListView;
	private IDownloadService iDownloadService;
	private static final int PROCESS_CHANGED = 1;
	private static final int TASK_CHANGED = 2;
	private static final int DOWNLOAD_STATUS_PAUSE  = 2;
	private OpenCityDataAdapter cityListAdapter;
	public static DownloadDataListAdapter downloadDataListAdapter;
	private List<City> cityList;
	private static Map<String, ProgressBar> progressBarMap = new HashMap<String, ProgressBar>();
	private static Map<String, TextView> resultTextMap = new HashMap<String, TextView>();
	private Map<String, Integer> positionMap = new HashMap<String, Integer>();
	private ViewGroup dataListGroup,downloadListGroup;
	private TextView dataListTitle,downloadListTitle;
	private DownloadManager downloadManager;
	private int currentCityId ;
	public static Map<Integer, Integer> installCityData = new HashMap<Integer, Integer>();
	List<Integer> installedCityList = new ArrayList<Integer>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		TravelApplication.getInstance().addActivity(this);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.open_city);
		
		downloadManager = new DownloadManager(OpenCityActivity.this);
				
		
		findViewById(R.id.open_city_tips_download).setSelected(true);
		
		cityList = AppManager.getInstance().getCityList();
		//installCityData = DownloadManager.getInstallCity();
		installCityData = DownloadPreference.getAllDownloadInfo(OpenCityActivity.this);
		//installedCityList.addAll(installCityData.values());
		openCtiyDataListView = (ListView) findViewById(R.id.open_city_data_listview);
		downloadListView = (ListView) findViewById(R.id.download_data_listview);				
		cityListAdapter = new OpenCityDataAdapter(cityList, OpenCityActivity.this);		
		downloadDataListAdapter = new DownloadDataListAdapter(installedCityList, this);
		
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
			DownloadInfos downloadInfo;
			ProgressBar downloadBar;
			TextView resultView;
			switch (msg.what)
			{
			case PROCESS_CHANGED:				
				downloadInfo = (DownloadInfos) msg.obj;	
				downloadBar = progressBarMap.get(downloadInfo.getUrl());
				resultView = resultTextMap.get(downloadInfo.getUrl());
				if(downloadBar != null)
				{					
					downloadBar.setMax((int)downloadInfo.getTotalBytes());
					downloadBar.setProgress((int)downloadInfo.getCurrentPosition());
					int persent = (int) (((float)downloadInfo.getCurrentPosition()/(float)downloadInfo.getTotalBytes())*100);
					resultView.setText(persent+"%");
					if(!downloadInfo.isNotFinish()){
						
						Toast.makeText(OpenCityActivity.this, R.string.success, 1).show();
						String downloadURL = downloadInfo.getUrl();
						int position = positionMap.get(downloadURL);
		
						
						
						openCtiyDataListView.findViewWithTag("button"+position).setVisibility(View.GONE);
						openCtiyDataListView.findViewWithTag("group"+position).setVisibility(View.GONE);
						openCtiyDataListView.findViewWithTag("installing"+position).setVisibility(View.VISIBLE);
						progressBarMap.remove(downloadURL);
						resultTextMap.remove(downloadURL);
						cancelDownload(downloadURL);
						
						int cityId = downloadInfo.getCityId();						
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
								upZipFile(zipFilePath, upZipFilePath,Integer.toString(cityId), position);
								DownloadManager downloadManager = new DownloadManager(OpenCityActivity.this);
								downloadManager.deleteDownloadInfo(downloadURL);
							} catch (Exception e)
							{
								Log.e(TAG, "<downloadInfoHandler.handleMessage> but catch exception:"+e.toString(),e);
							}
							
						}else
						{
							openCtiyDataListView.findViewWithTag("button"+position).setVisibility(View.VISIBLE);
							openCtiyDataListView.findViewWithTag("installing"+position).setVisibility(View.GONE);
							openCtiyDataListView.findViewWithTag("installed"+position).setVisibility(View.GONE);
							deleteFile(zipFilePath,upZipFilePath);
						}	
					}else
					{
						return;
					}					
				}				
				break;
			case -1:
				Toast.makeText(OpenCityActivity.this, R.string.error, 1).show();
				break;
			default:
				
				break;
			}
		}
		
	};

	
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
	
	
	private OnClickListener dataListOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			downloadListView.setVisibility(View.GONE);
			openCtiyDataListView.setVisibility(View.VISIBLE);
			dataListGroup.setBackgroundResource(R.drawable.citybtn_on);
			downloadListGroup.setBackgroundResource(R.drawable.citybtn_off2);
			dataListTitle.setTextColor(getResources().getColor(R.color.white));
			downloadListTitle.setTextColor(getResources().getColor(R.color.black));
			cityListAdapter.setCityDataList(cityList);
			//cityListAdapter.setDownloadStstudTask(downloadStstudTask);
			cityListAdapter.notifyDataSetChanged();
		}
	};
	
	
	
	private OnClickListener downloadListOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			downloadListView.setVisibility(View.VISIBLE);
			openCtiyDataListView.setVisibility(View.GONE);
			dataListGroup.setBackgroundResource(R.drawable.citybtn_off);
			downloadListGroup.setBackgroundResource(R.drawable.citybtn_on2);
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
	};
	
	
	
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
				progressBarMap.put(downloadURL, downloadBar);
				resultTextMap.put(downloadURL, resultView);
				download(city.getCityId(),downloadURL, downloadSavePath,tempPath);
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
			String downloadPath = city.getDownloadURL();
			pauseDownload(downloadPath);
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
				ProgressBar downloadBar = (ProgressBar) openCtiyDataListView.findViewWithTag("bar"+city.getDownloadURL());			
				TextView resultView = (TextView) openCtiyDataListView.findViewWithTag("result"+city.getDownloadURL());
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
			}		
			cancelDownload(downloadURL);
			startGroup.setVisibility(View.VISIBLE);
			dataSize.setVisibility(View.VISIBLE);
			cancleGroup.setVisibility(View.GONE);
			dataDownloadMangerGroup.setVisibility(View.GONE);
		}
	};
	
	private OnClickListener onlineOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			int cityId = (Integer)v.getTag();
			AppManager.getInstance().setCurrentCityId(cityId);
			Intent intent = new Intent();
			intent.setClass(OpenCityActivity.this, IndexActivity.class);
			startActivity(intent);
			
		}
	};
	
	
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			City city = cityList.get(arg2);
			AppManager.getInstance().setCurrentCityId(city.getCityId());
			Intent intent = new Intent();
			intent.setClass(OpenCityActivity.this, IndexActivity.class);
			startActivity(intent);
			
		}
	};
	

	
	
	
	private void download(final int cityId,final String downloadURL, final String downloadSavePath,final String tempPath)
	{
		Log.d(TAG, "download url = "+downloadURL);
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
	
	
	
	
	
	private void upZipFile(String zipFilePath, String folderPath,String cityId,final int position)
	{
		String[] params = new String[]{zipFilePath,folderPath,cityId};
		AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>()
		{

			@Override
			protected Boolean doInBackground(String... params)
			{
				String zipFilePath = params[0];
				String upZipFilePath = params[1];
				String cityId = params[2];
				DownloadPreference.insertDownloadInfo(OpenCityActivity.this, cityId, 0);
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
				return result;
			}

			@Override
			protected void onPostExecute(Boolean result)
			{
				super.onPostExecute(result);
				refresh(result, position);
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
	
	
	private void refresh(boolean zipResult,int position)
	{
		if(zipResult)
		{	
			int cityId = cityList.get(position).getCityId();
			openCtiyDataListView.findViewWithTag("installing"+position).setVisibility(View.GONE);
			openCtiyDataListView.findViewWithTag("installed"+position).setVisibility(View.VISIBLE);
		}else
		{
			openCtiyDataListView.findViewWithTag("button"+position).setVisibility(View.VISIBLE);	
			openCtiyDataListView.findViewWithTag("installing"+position).setVisibility(View.GONE);
			openCtiyDataListView.findViewWithTag("installed"+position).setVisibility(View.GONE);
		}	
	}
	


	@Override
	protected void onResume()
	{
		super.onResume();
		Log.i(TAG, "onResume");
		currentCityId = AppManager.getInstance().getCurrentCityId();
		Map<String, DownloadStatus> downloadStstudTask = DownloadService.getDownloadStstudTask();
		cityListAdapter.setCityDataList(cityList);
		cityListAdapter.setDownloadStstudTask(downloadStstudTask);
		cityListAdapter.notifyDataSetChanged();
	}


	@Override
	protected void onDestroy()
	{
		unbindService(conn);
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}
	
	
	
	
	
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
				installedCityList.add(city.getCityId());
				
			}else
			{				
				buttonGroup.setVisibility(View.VISIBLE);
				installedTextView.setVisibility(View.GONE);
				/*if(downloadStstudTask.containsKey(city.getDownloadURL()) && downloadDataStatus.get(city.getDownloadURL()) == DOWNLOAD_ING)*/
			 if(downloadStstudTask.containsKey(downloadURL)&&downloadStstudTask.get(downloadURL).mStatus != DOWNLOAD_STATUS_PAUSE&&downloadBean !=null)
				{
					dataDownloadMangerGroup.setVisibility(View.VISIBLE);
					restartDownloadBtn.setVisibility(View.GONE);
					stopDownloadBtn.setVisibility(View.VISIBLE);
					startGroup.setVisibility(View.GONE);
					cancelGroup.setVisibility(View.VISIBLE);
					dataSize.setVisibility(View.GONE);
					
					downloadBar.setMax(downloadBean.getFileLength());
					downloadBar.setProgress(downloadBean.getDownloadLength());
					String result = (int)((float)downloadBean.getDownloadLength()/(float)downloadBean.getFileLength()*100)+"%";
					resultTextView.setText(result);
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
				onlineButton.setTag(city.getCityId());
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
}
