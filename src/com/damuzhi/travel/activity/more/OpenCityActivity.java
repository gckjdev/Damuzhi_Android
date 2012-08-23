package com.damuzhi.travel.activity.more;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.Time;
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
import com.damuzhi.travel.mission.more.DownloadMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.downlaod.DownloadBean;
import com.damuzhi.travel.model.downlaod.DownloadManager;
import com.damuzhi.travel.model.entity.DownloadInfos;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.TravelUtil;


public class OpenCityActivity extends Activity 
{
	private static String TAG = "OpenCityDataActivity";	
	private ListView openCtiyDataListView,downloadListView;
	private static final int PROCESS_CHANGED = 1;
	private static final int UPZIP = 2;
	private static final int CONNECTION_ERROR = 3;
	private static final int PAUSE = 1;
	private static final int DOWNLOADING = 2;
	private static final int FAILED = 3;
	private static final int UPZIPING = 4;
	private static final int SUCCESS = 5;
	private int currentCityId ;
	
	public static OpenCityDataAdapter cityListAdapter;
	public static DownloadDataListAdapter downloadDataListAdapter;
	private List<City> cityList;
	private static Map<String, ProgressBar> progressBarMap = new HashMap<String, ProgressBar>();
	private static Map<String, TextView> resultTextMap = new HashMap<String, TextView>();
	private Map<String, Integer> positionMap = new HashMap<String, Integer>();
	private Map<String, Integer> downloadStatusTask;
	public static Map<Integer, Integer> installCityData = new HashMap<Integer, Integer>();
	private Map<Integer, String> newVersionCityData = new HashMap<Integer, String>();
	List<Integer> installedCityList = new ArrayList<Integer>();
	private ViewGroup dataListGroup,downloadListGroup;
	private TextView dataListTitle,downloadListTitle;
	private DownloadManager downloadManager;
	private Map<String, DownloadBean> unfinishDownload = new HashMap<String, DownloadBean>();
	private Map<Integer, Integer> unfinishInstallMap = new HashMap<Integer, Integer>();
	private Map<String, ProgressBar> stopDownloadBar = new HashMap<String, ProgressBar>();
	private Map<String, TextView> stopDownloadresultTextMap = new HashMap<String, TextView>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "oncreat");
		//Debug.startMethodTracing();
		TravelApplication.getInstance().addActivity(this);
		setContentView(R.layout.open_city);	
		downloadManager = new DownloadManager(OpenCityActivity.this);
		unfinishDownload = downloadManager.getUnfinishDownload();		
		unfinishInstallMap = DownloadPreference.getAllUnfinishInstall(OpenCityActivity.this);
		currentCityId = AppManager.getInstance().getCurrentCityId();
		
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
			initDownloadListview();
		}
		if(flag == 0)
		{
			City city = AppManager.getInstance().getCityByCityId(currentCityId);
			String downloadURL = city.getDownloadURL();
			installData(currentCityId, downloadURL);
		}
		
		Intent intent = new Intent(OpenCityActivity.this,DownloadService.class);
		startService(intent);
       // bindService(intent, conn, Context.BIND_AUTO_CREATE);
		//bindService(new Intent(OpenCityActivity.this, DownloadService.class), conn, Context.BIND_AUTO_CREATE);
	}
	
	//bindservice
/*	private ServiceConnection conn = new ServiceConnection()
	{		
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			Log.i(TAG, "ServiceDisConnection -> onServiceDisConnected");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			
		}
	};*/
	
	
	
	
	
	public  Handler downloadHandler = new Handler(){		
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			final DownloadInfos downloadInfo;
			String downloadURL;
			int cityId = 0;
			switch (msg.what)
			{
				case PROCESS_CHANGED:
					downloadInfo = (DownloadInfos) msg.obj;
					save(downloadInfo);
					refreshDownloadProgress(downloadInfo);
					break;
				case UPZIP:
					downloadInfo = (DownloadInfos) msg.obj;
					boolean upzipResult = downloadInfo.isUpzipResult();
					cityId = downloadInfo.getCityId();
					downloadURL = downloadInfo.getUrl();
					refresh(upzipResult, downloadURL, cityId);
					break;
				case CONNECTION_ERROR:
					downloadInfo = (DownloadInfos) msg.obj;
					cityId = downloadInfo.getCityId();
					downloadURL = downloadInfo.getUrl();
				    pauseDownloadCauseError(cityId, downloadURL);
					break;
				default:				
					break;
			}
		}
		
	};
	
	long lastTime = 0;
	
	
	private void save(final DownloadInfos downloadInfo)
	{
		int cityId = downloadInfo.getCityId();
		String downloadURL = downloadInfo.getUrl();
		int fileTotalLength = (int)downloadInfo.getFileLength();
		int downloadLength = (int)downloadInfo.getDownloadLength();
		long currentTime = System.currentTimeMillis()/1000;	
		if (fileTotalLength == downloadLength ||(currentTime%3 == 0&&currentTime != lastTime)){
			Log.i(TAG, "fileTotalLength = "+fileTotalLength+",downloadLength = "+downloadLength);
			Log.i(TAG, "time to save downlaodInfo to DB "+currentTime);
			downloadManager.saveDownloadInfo(cityId, downloadURL, "", "", 0, fileTotalLength, downloadLength);
			lastTime = System.currentTimeMillis()/1000;
		}
		if(fileTotalLength == downloadLength)
		{
			DownloadPreference.insertDownloadInfo(OpenCityActivity.this, Integer.toString(cityId), 0);
		}
		return;
	}
	
	
	
		
	private void refreshDownloadProgress(DownloadInfos downloadInfo)
	{
		if(downloadInfo != null)
		{
			String downloadURL = downloadInfo.getUrl();
			ProgressBar downloadBar = progressBarMap.get(downloadURL);
			TextView resultView = resultTextMap.get(downloadURL);
			if(downloadBar != null)
			{		
				
					downloadBar.setMax((int)downloadInfo.getFileLength());
					downloadBar.setProgress((int)downloadInfo.getDownloadLength());
					int persent = (int) (((float)downloadInfo.getDownloadLength()/(float)downloadInfo.getFileLength())*100);
					resultView.setText(persent+"%");
					if(!downloadInfo.isNotFinish()){
						downloadManager.deleteDownloadInfo(downloadURL);
						Log.i(TAG, "download finish delete DB info downloadURL = "+downloadURL);
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
					}else
					{
						return ;
					}	
								
			}	
		}		
	}
	
	
	
	private void refresh(boolean zipResult,String downloadURL,int cityId)
	{	
		if(zipResult)
		{
			Log.d(TAG, "installed save info to db cityId = "+cityId);
			DownloadPreference.updateDownloadInfo(OpenCityActivity.this, Integer.toString(cityId), 1);
			installCityData.put(cityId, cityId);
		}else{
			Log.d(TAG, "installed fail delete db info cityId = "+cityId);
			DownloadPreference.deleteDownloadInfo(OpenCityActivity.this, Integer.toString(cityId));
			unfinishInstallMap.remove(cityId);
		}	
		if(newVersionCityData.containsKey(cityId))
		{
			downloadDataListAdapter.notifyDataSetChanged();
		}else
		{
			cityListAdapter.setDownloadStstudTask(downloadStatusTask);
			cityListAdapter.notifyDataSetChanged();
		}	
	}
	
	
	
	
	
	
	
	
	
	private void setCityToast(String cityName)
	{
		Toast.makeText(OpenCityActivity.this,cityName , Toast.LENGTH_SHORT).show();
	}
	
	
	
	private void download( int cityId, String downloadURL,  String downloadSavePath, String tempPath,String upZipFilePath)
	{
		DownloadService.startDownload(OpenCityActivity.this,cityId,downloadURL, downloadSavePath,tempPath,upZipFilePath);
	}
		
	private void pauseDownload(final String downloadURL) 
	{
		DownloadService.pauseDownload(OpenCityActivity.this, downloadURL);
	}
			
	private void cancelDownload(final String downloadURL)
	{
		DownloadService.cancelDownload(OpenCityActivity.this, downloadURL);	
	}
	
	
	
	
	
	


	@Override
	protected void onResume()
	{
		super.onResume();
		downloadStatusTask = DownloadService.getDownloadStstudTask();
		cityListAdapter.setCityDataList(cityList);
		cityListAdapter.setDownloadStstudTask(downloadStatusTask);
		cityListAdapter.notifyDataSetChanged();
		DownloadService.setDownloadHandler(downloadHandler);
	}


	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//unbindService(conn);
		//Debug.stopMethodTracing();
		Log.d(TAG, "onDestroy");
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
			City city = cityList.get(position);
			int cityId =  city.getCityId();
			String downloadURL = city.getDownloadURL();
			String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH,cityId);
			String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);
			String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);
			ProgressBar downloadBar = (ProgressBar) openCtiyDataListView.findViewWithTag("bar"+downloadURL);	
			TextView resultView = (TextView) openCtiyDataListView.findViewWithTag("result"+downloadURL);
			downloadBar.setMax(0);
			downloadBar.setProgress(0);
			resultView.setText("");
			progressBarMap.put(downloadURL, downloadBar);
			resultTextMap.put(downloadURL, resultView);
			cityListAdapter.notifyDataSetChanged();
			download(city.getCityId(),downloadURL, downloadSavePath,tempPath,upZipFilePath);
			String startDownload = getString(R.string.start_download);
			downloadToast(startDownload);
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
			if(progressBarMap.containsKey(downloadURL))
			{
				stopDownloadBar.put(downloadURL, progressBarMap.get(downloadURL));
				stopDownloadresultTextMap.put(downloadURL,resultTextMap.get(downloadURL));	
			}
			
			cityListAdapter.notifyDataSetChanged();
			pauseDownload(downloadURL);
			String stopDownload = getString(R.string.stop_download);
			downloadToast(stopDownload);
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
			int cityId = city.getCityId();
			String downloadURL = city.getDownloadURL();
			String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH,cityId );	
			String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);
			String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);		
			if(!progressBarMap.containsKey(downloadURL))
			{
				ProgressBar downloadBar = (ProgressBar) openCtiyDataListView.findViewWithTag("bar"+downloadURL);	
				TextView resultView = (TextView) openCtiyDataListView.findViewWithTag("result"+downloadURL);
				progressBarMap.put(downloadURL, downloadBar);
				resultTextMap.put(downloadURL, resultView);
			}
			cityListAdapter.notifyDataSetChanged();
			String restsrtDownload = getString(R.string.start_download);
			download(city.getCityId(),downloadURL, downloadSavePath,tempPath,upZipFilePath);
			downloadToast(restsrtDownload);
		}
	};
	
	private OnClickListener cancelOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String tag = (String) v.getTag();
			final int position = Integer.parseInt(tag.substring(tag.lastIndexOf("l")+1));
			
			
			AlertDialog deleteAlertDialog = new AlertDialog.Builder(OpenCityActivity.this).create();
			deleteAlertDialog.setMessage(OpenCityActivity.this.getString(R.string.cancel_download_toast));
			deleteAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,OpenCityActivity.this.getString(R.string.ok),new DialogInterface.OnClickListener()
			{					
				@Override
				public void onClick(DialogInterface dialog, int which)
				{	
					City city = cityList.get(position);
					String downloadURL = city.getDownloadURL();
					downloadManager.deleteDownloadInfo(downloadURL);
					if(unfinishDownload.containsKey(downloadURL))
					{
						unfinishDownload.remove(downloadURL);
					}
					progressBarMap.remove(downloadURL);
					resultTextMap.remove(downloadURL);
					cityListAdapter.notifyDataSetChanged();
					cancelDownload(downloadURL);
				}	
			} );
			deleteAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+OpenCityActivity.this.getString(R.string.cancel),new DialogInterface.OnClickListener()
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
			Log.d(TAG, "update data...."+downloadURL);
			String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH,cityId);
			String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);
			String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);
			downloadDataListAdapter.notifyDataSetChanged();
			download(cityId,downloadURL, downloadSavePath,tempPath,upZipFilePath);	
			String startDownload = getString(R.string.start_download);
			downloadToast(startDownload);
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
				Log.d(TAG, "restart update url "+downloadURL);
				String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH, cityId);	
				String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);				
				String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);
				if(!progressBarMap.containsKey(downloadURL))
				{
					ProgressBar downloadBar = (ProgressBar) downloadListView.findViewWithTag("bar"+downloadURL);	
					TextView resultView = (TextView) downloadListView.findViewWithTag("result"+downloadURL);
					progressBarMap.put(downloadURL, downloadBar);
					resultTextMap.put(downloadURL, resultView);
				}
				downloadDataListAdapter.notifyDataSetChanged();
				download(cityId,downloadURL, downloadSavePath,tempPath,upZipFilePath);
				String startDownload = getString(R.string.start_download);
				downloadToast(startDownload);
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
				if(progressBarMap.containsKey(downloadURL))
				{
					stopDownloadBar.put(downloadURL, progressBarMap.get(downloadURL));
					stopDownloadresultTextMap.put(downloadURL,resultTextMap.get(downloadURL));
				}
				downloadDataListAdapter.notifyDataSetChanged();
				pauseDownload(downloadURL);
				String stopDownload = getString(R.string.stop_download);
				downloadToast(stopDownload);
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
	
	
	private OnClickListener installOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String tag = (String)v.getTag();
			int position = Integer.parseInt(tag.substring(tag.lastIndexOf("l")+1));
			City city = cityList.get(position);
			int cityId = city.getCityId();
			if(unfinishInstallMap.containsKey(cityId))
			{
				unfinishInstallMap.remove(cityId);
			}
			cityListAdapter.notifyDataSetChanged();
			String downloadURL = city.getDownloadURL();
			installData(cityId,downloadURL);
		}
	};
	
	
	private void installData(int cityId,String downloadURL)
	{
		DownloadService.downloadStstudTask.put(downloadURL, UPZIPING);
		String fileName = TravelUtil.getDownloadFileName(downloadURL);
		String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH,cityId);
		String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);
		String zipFilePath = downloadSavePath+fileName;
		DownloadService.upZipFile(zipFilePath, upZipFilePath, cityId, downloadURL);
	}
	
	
	
	private void deleteInstalledData(String folderPath)
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
	
	private void deleteInstallZipData(String filePath)
	{
		String[] params = new String[]{filePath};
		
		AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>()
		{

			@Override
			protected Void doInBackground(String... params)
			{
				String zipFilePath = params[0];				
				FileUtil.deleteFile(zipFilePath);
				return null;
			}
		};
		task.execute(params);
	}
	
	
	private OnClickListener cancelInstallOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String tag = (String) v.getTag();
			final int position = Integer.parseInt(tag.substring(tag.lastIndexOf("l")+1));
			
			
			AlertDialog deleteAlertDialog = new AlertDialog.Builder(OpenCityActivity.this).create();
			deleteAlertDialog.setMessage(OpenCityActivity.this.getString(R.string.delete_install_data));
			deleteAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,OpenCityActivity.this.getString(R.string.ok),new DialogInterface.OnClickListener()
			{					
				@Override
				public void onClick(DialogInterface dialog, int which)
				{	
					City city = cityList.get(position);
					int cityId = city.getCityId();
					String downloadURL = city.getDownloadURL();
					String fileName = TravelUtil.getDownloadFileName(downloadURL);
					String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH,cityId);
					String zipFilePath = downloadSavePath+fileName;
					DownloadPreference.deleteDownloadInfo(OpenCityActivity.this, Integer.toString(cityId));
					if(unfinishInstallMap.containsKey(cityId))
					{
						unfinishInstallMap.remove(cityId);
					}
					cityListAdapter.notifyDataSetChanged();	
					deleteInstallZipData(zipFilePath);
				}	
			} );
			deleteAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+OpenCityActivity.this.getString(R.string.cancel),new DialogInterface.OnClickListener()
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
	
	
	
	
	private void pauseDownloadCauseError(int cityId,String downloadURL)
	{
		ImageView stopBtn;
		ImageView restartBtn;
		if(newVersionCityData.containsKey(cityId))
		{
			pauseDownload(downloadURL);
			restartBtn = (ImageView) downloadListView.findViewWithTag("restart"+cityId);
			stopBtn = (ImageView) downloadListView.findViewWithTag("pause"+cityId);
			if(restartBtn != null)
			{
				restartBtn.setVisibility(View.VISIBLE);
				stopBtn.setVisibility(View.GONE);
			}
			
		}else
		{
			int position = positionMap.get(downloadURL);
			pauseDownload(downloadURL);
			restartBtn = (ImageView) openCtiyDataListView.findViewWithTag("restart"+position);
			stopBtn = (ImageView) openCtiyDataListView.findViewWithTag("pause"+position);
			if(restartBtn != null)
			{
				restartBtn.setVisibility(View.VISIBLE);
				stopBtn.setVisibility(View.GONE);
			}
		}
		pauseDownload(downloadURL);
	}
	
	
	private void downloadToast(String title)
	{
		Toast.makeText(OpenCityActivity.this,title, Toast.LENGTH_SHORT).show();
	}
	
	
	
	
	
	
	
	public class OpenCityDataAdapter extends BaseAdapter
	{
		private static final String TAG = "OpenCityDataAdapter";
		private List<City> cityDataList;
		private Map<String, Integer> downloadStstudTask;
		private Context context;
		
		
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
			int cityId = city.getCityId();
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
			dataCityName.setText(city.getCountryName()+"."+city.getCityName());
			TextView dataSize = viewCache.getDataSize();
			ViewGroup buttonGroup = viewCache.getButtonGroup();
			ViewGroup installGroup = viewCache.getInstallGroup();
			TextView installedTextView = viewCache.getInstalledTextView();
			TextView installingTextView = viewCache.getInstallingTextView();
			ViewGroup dataDownloadMangerGroup = viewCache.getDataDownloadMangerGroup();
			ImageView restartDownloadBtn = viewCache.getRestartDownloadBtn();
			ImageView stopDownloadBtn = viewCache.getStopDownloadBtn();
			ImageButton onlineButton = viewCache.getOnlineButton();				
			ImageButton startButton = viewCache.getStartButton();
			ImageButton cancelButton = viewCache.getCancelButton();
			ImageButton installButton = viewCache.getInstallButton();
			ImageButton cancelInstallButton = viewCache.getCancelInstallButton();
			ViewGroup startGroup = viewCache.getStartGroup();
			ViewGroup cancelGroup = viewCache.getCancelGroup();
			ProgressBar downloadBar = viewCache.getDownloadBar();
			TextView resultTextView = viewCache.getResultTextView();
			DownloadBean downloadBean = null;
			if(unfinishDownload!= null &&unfinishDownload.containsKey(downloadURL))
			{
				downloadBean = unfinishDownload.get(downloadURL);
			}
			if(installCityData!=null && installCityData.containsKey(city.getCityId()))
			{
				installedTextView.setVisibility(View.VISIBLE);
				buttonGroup.setVisibility(View.GONE);
				installGroup.setVisibility(View.GONE);
				dataDownloadMangerGroup.setVisibility(View.GONE);
				installingTextView.setVisibility(View.GONE);
				dataSize.setVisibility(View.GONE);				
			}else
			{
				installGroup.setVisibility(View.GONE);
				buttonGroup.setVisibility(View.VISIBLE);
				dataSize.setVisibility(View.GONE);
				if(downloadStatusTask.containsKey(downloadURL))
				{
					int downloadStatus = downloadStatusTask.get(downloadURL);			
					progressBarMap.put(downloadURL, downloadBar);
					resultTextMap.put(downloadURL, resultTextView);
					installingTextView.setVisibility(View.GONE);
					installedTextView.setVisibility(View.GONE);
					startGroup.setVisibility(View.GONE);
					cancelGroup.setVisibility(View.VISIBLE);
					dataDownloadMangerGroup.setVisibility(View.VISIBLE);
					if (downloadStatus == UPZIPING)
					{
						buttonGroup.setVisibility(View.GONE);
						dataDownloadMangerGroup.setVisibility(View.GONE);
						installingTextView.setVisibility(View.VISIBLE);
						progressBarMap.remove(downloadURL);
						resultTextMap.remove(downloadURL);
					}else if(downloadStatus ==  DOWNLOADING)
					{
						restartDownloadBtn.setVisibility(View.GONE);
						stopDownloadBtn.setVisibility(View.VISIBLE);
						
					}else
					{
						restartDownloadBtn.setVisibility(View.VISIBLE);
						stopDownloadBtn.setVisibility(View.GONE);
						if(stopDownloadBar.containsKey(downloadURL))
						{
							ProgressBar progress = stopDownloadBar.get(downloadURL);
							TextView result = stopDownloadresultTextMap.get(downloadURL);
							downloadBar.setMax(progress.getMax());
							downloadBar.setProgress(progress.getProgress());
							resultTextView.setText(result.getText());
						}else
						{
							if(downloadBean != null)
							{
								downloadBar.setMax(downloadBean.getFileLength());
								downloadBar.setProgress(downloadBean.getDownloadLength());
								String result = (int)((float)downloadBean.getDownloadLength()/(float)downloadBean.getFileLength()*100)+"%";
								resultTextView.setText(result);
							}	
						}			
					}
				}else
				{
					installingTextView.setVisibility(View.GONE);
					installedTextView.setVisibility(View.GONE);
					if(unfinishDownload!= null&&unfinishDownload.containsKey(downloadURL))
					{
						startGroup.setVisibility(View.GONE);
						cancelGroup.setVisibility(View.VISIBLE);
						dataDownloadMangerGroup.setVisibility(View.VISIBLE);
						restartDownloadBtn.setVisibility(View.VISIBLE);
						stopDownloadBtn.setVisibility(View.GONE);
						downloadBar.setMax(downloadBean.getFileLength());
						downloadBar.setProgress(downloadBean.getDownloadLength());
						String result = (int)((float)downloadBean.getDownloadLength()/(float)downloadBean.getFileLength()*100)+"%";
						resultTextView.setText(result);
					}else if(unfinishInstallMap.containsKey(cityId))
					{
						installGroup.setVisibility(View.VISIBLE);
						installButton.setVisibility(View.VISIBLE);
						cancelInstallButton.setVisibility(View.VISIBLE);
						buttonGroup.setVisibility(View.GONE);
					}else
					{
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
				installGroup.setTag("installGroup"+position);
				installButton.setTag("install"+position);
				cancelInstallButton.setTag("cancelInstall"+position);
				startButton.setOnClickListener(startDownloadClickListener);
				cancelButton.setOnClickListener(cancelOnClickListener);
				restartDownloadBtn.setOnClickListener(restartDownloadClickListener);
				stopDownloadBtn.setOnClickListener(stopDownloadOnClickListener);
				onlineButton.setOnClickListener(onlineOnClickListener);		
				installButton.setOnClickListener(installOnClickListener);
				cancelInstallButton.setOnClickListener(cancelInstallOnClickListener);
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

		public Map<String, Integer> getDownloadStstudTask()
		{
			return downloadStstudTask;
		}

		public void setDownloadStstudTask(Map<String, Integer> downloadStstudTask)
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
						citySize.setVisibility(View.GONE);
						DownloadBean downloadBean = null;	
						if(unfinishDownload != null && unfinishDownload.containsKey(downloadURL))
						{
							downloadBean = unfinishDownload.get(downloadURL);
						}
						if(downloadStatusTask.containsKey(downloadURL))
						{
							updateButton.setVisibility(View.GONE);
							progressBarMap.put(downloadURL, updateBar);
							resultTextMap.put(downloadURL, updateTextView);
							int downloadStatus = downloadStatusTask.get(downloadURL);
							if( downloadStatus == DOWNLOADING)
							{
								updateStatusGroup.setVisibility(View.VISIBLE);
								restartUpdateBtn.setVisibility(View.GONE);
								stopUpdateBtn.setVisibility(View.VISIBLE);	
							}else if(downloadStatus == PAUSE)
							{
								restartUpdateBtn.setVisibility(View.VISIBLE);
								stopUpdateBtn.setVisibility(View.GONE);
								if(stopDownloadBar.containsKey(downloadURL)&&stopDownloadresultTextMap.containsKey(downloadURL))
								{
									ProgressBar progress = stopDownloadBar.get(downloadURL);
									TextView result = stopDownloadresultTextMap.get(downloadURL);
									updateBar.setMax(progress.getMax());
									updateBar.setProgress(progress.getProgress());
									updateTextView.setText(result.getText());
								}else
								{
									if(downloadBean != null)
									{
										updateBar.setMax(downloadBean.getFileLength());
										updateBar.setProgress(downloadBean.getDownloadLength());
										String result = (int)((float)downloadBean.getDownloadLength()/(float)downloadBean.getFileLength()*100)+"%";
										updateTextView.setText(result);
									}	
								}
							}
							
							else
							{
								if(installedTextView.getVisibility() == View.VISIBLE ||installingTextView.getVisibility() == View.VISIBLE)
								{
									updateButton.setVisibility(View.GONE);
								}else
								{
									updateButton.setVisibility(View.VISIBLE);
								}					
								citySize.setText(TravelUtil.getDataSize(city.getDataSize()));
								citySize.setVisibility(View.VISIBLE);
								updateStatusGroup.setVisibility(View.GONE);
							}
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
							}else
							{
								
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
						if(unfinishDownload.containsKey(downloadURL))
						{
							unfinishDownload.remove(downloadURL);
						}
						OpenCityActivity.installCityData.remove(cityId);
						OpenCityActivity.downloadDataListAdapter.setInstalledCityList(installedCityList);
						OpenCityActivity.downloadDataListAdapter.notifyDataSetChanged();
						OpenCityActivity.cityListAdapter.setDownloadStstudTask(downloadStatusTask);
						OpenCityActivity.cityListAdapter.notifyDataSetChanged();
						DownloadPreference.deleteDownloadInfo(context, Integer.toString(cityId));
						deleteInstalledData(upZipFilePath);		
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
	}
	
	
	

}
