package com.damuzhi.travel.activity.more;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

import android.R.integer;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.download.DownloadDataListAdapter;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.download.DownloadService;
import com.damuzhi.travel.download.IDownloadCallback;
import com.damuzhi.travel.download.IDownloadService;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.downlaod.DownloadBean;
import com.damuzhi.travel.model.downlaod.DownloadManager;
import com.damuzhi.travel.model.entity.DownloadInfos;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.ZipUtil;


public class OpenCityDataActivity extends MenuActivity 
{
	private static String TAG = "OpenCityDataActivity";	
	private ListView openCtiyDataListView,downloadListView;
	private IDownloadService iDownloadService;
	private static final int PROCESS_CHANGED = 1;
	private static final int TASK_CHANGED = 2;
	private OpenCityDataAdapter cityListAdapter;
	private DownloadDataListAdapter downloadDataListAdapter;
	private List<City> cityList;
	private static Map<String, ProgressBar> progressBarMap = new HashMap<String, ProgressBar>();
	private static Map<String, TextView> resultTextMap = new HashMap<String, TextView>();
	private Map<String, Integer> positionMap = new HashMap<String, Integer>();
	private ViewGroup dataListGroup,downloadListGroup;
	private TextView dataListTitle,downloadListTitle;
	//private ImageView restartDownloadBtn,stopDownloadBtn;
	DownloadManager downloadManager;
	Handler downloadHandler;
	private int currentCityId ;
	private Map<Integer, Integer> installCityData = new HashMap<Integer, Integer>();
	private Map<String, Integer> downloadStatus = new HashMap<String, Integer>();
	List<Integer> installedCityList = new ArrayList<Integer>();
	private static final int DOWNLOAD_ING = 1;
	private static final int DOWNLOAD_STOP = 2;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		TravelApplication.getInstance().addActivity(this);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.open_city);
		
		downloadManager = new DownloadManager(OpenCityDataActivity.this);
				
		downloadListView = (ListView) findViewById(R.id.download_data_listview);
		findViewById(R.id.open_city_tips_download).setSelected(true);
		
		cityList = AppManager.getInstance().getCityList();
				
		openCtiyDataListView = (ListView) findViewById(R.id.open_city_data_listview);
		installCityData = DownloadManager.getInstallCity();
		cityListAdapter = new OpenCityDataAdapter(cityList, OpenCityDataActivity.this);
		installedCityList.addAll(installCityData.values());
		downloadDataListAdapter = new DownloadDataListAdapter(installedCityList, this);
		
		View listViewFooter = getLayoutInflater().inflate(R.layout.open_data_listview_footer, null, false);
		TextView tipsTextView = (TextView) listViewFooter.findViewById(R.id.open_city_tips_update);
		SpannableString tips = new SpannableString(getString(R.string.open_city_tips2));
		tips.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, tips.length()-1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		tipsTextView.setText(tips);
		openCtiyDataListView.addFooterView(listViewFooter);
		openCtiyDataListView.setFooterDividersEnabled(false);
		openCtiyDataListView.setDrawingCacheEnabled(false);
		openCtiyDataListView.setAdapter(cityListAdapter);
		downloadListView.setAdapter(downloadDataListAdapter);
		
		dataListGroup = (ViewGroup) findViewById(R.id.data_list_group);
		downloadListGroup = (ViewGroup) findViewById(R.id.download_list_group);
		dataListTitle = (TextView) findViewById(R.id.city_list_title);
		downloadListTitle = (TextView) findViewById(R.id.download_manager_title);
		
		dataListGroup.setOnClickListener(dataListOnClickListener);
		downloadListGroup.setOnClickListener(downloadListOnClickListener);
		
		bindService(new Intent(OpenCityDataActivity.this, DownloadService.class), conn, Context.BIND_AUTO_CREATE);
	}
	
		
	
	
	
	
	
	//bindservice
	private ServiceConnection conn = new ServiceConnection()
	{		
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			//Log.i(TAG, "ServiceDisConnection -> onServiceDisConnected");
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
					//downloadBar.incrementProgressBy((int)downloadInfo.getSpeed());	
					int persent = (int) (((float)downloadInfo.getCurrentPosition()/(float)downloadInfo.getTotalBytes())*100);
					resultView.setText(persent+"%");
					if(!downloadInfo.isNotFinish()){
						
						Toast.makeText(OpenCityDataActivity.this, R.string.success, 1).show();
						String downloadURL = downloadInfo.getUrl();
						cancelDownload(downloadURL);
						progressBarMap.remove(downloadURL);
						resultTextMap.remove(downloadURL);
						downloadStatus.remove(downloadURL);
						
						
						int position = positionMap.get(downloadURL);
						openCtiyDataListView.findViewWithTag("button"+position).setVisibility(View.GONE);
						openCtiyDataListView.findViewWithTag("group"+position).setVisibility(View.GONE);
						openCtiyDataListView.findViewWithTag("installing"+position).setVisibility(View.VISIBLE);
						int cityId = downloadInfo.getCityId();
						
						//String zipTempFilePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH, cityId)+HttpTool.getTempFileName(HttpTool.getConnection(downloadURL), downloadURL);
						String zipTempFilePath = ConstantField.DOWNLOAD_TEMP_PATH+HttpTool.getTempFileName(HttpTool.getConnection(downloadURL), downloadURL);
						String zipFilePath = String.format(ConstantField.DOWNLOAD_TEMP_PATH, cityId)+HttpTool.getFileName(HttpTool.getConnection(downloadURL), downloadURL);
						File tempFile = new File(zipTempFilePath);
						Log.i(TAG, "<downloadInfoHandler> download save file path = "+tempFile.getAbsolutePath());
						File zipFile = new File(zipFilePath);
						String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);
						boolean reulst = tempFile.renameTo(zipFile);
						//boolean reulst = true;
						if(reulst)
						{														
							try
							{
								reulst = ZipUtil.upZipFile(zipFilePath,upZipFilePath );								
								if(reulst)
								{									
									openCtiyDataListView.findViewWithTag("installing"+position).setVisibility(View.GONE);
									openCtiyDataListView.findViewWithTag("installed"+position).setVisibility(View.VISIBLE);
								}else
								{
									openCtiyDataListView.findViewWithTag("button"+position).setVisibility(View.VISIBLE);	
									openCtiyDataListView.findViewWithTag("installing"+position).setVisibility(View.GONE);
									openCtiyDataListView.findViewWithTag("installed"+position).setVisibility(View.GONE);
									//FileUtil.deleteFolder(zipFilePath);
									//FileUtil.deleteFolder(upZipFilePath);
								}	
								DownloadManager downloadManager = new DownloadManager(OpenCityDataActivity.this);
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
							//FileUtil.deleteFolder(zipFilePath);
							//FileUtil.deleteFolder(upZipFilePath);
						}	
					}else
					{
						return;
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

	
	private IDownloadCallback callback = new IDownloadCallback.Stub()
	{
		
		@Override
		public void onTaskStatusChanged(String strkey, int status)throws RemoteException
		{
			/* if(status == 4)  
	            {  
	                Message msg = downloadInfoHandler.obtainMessage(TASK_CHANGED,4);  
	                downloadInfoHandler.sendMessage(msg);  
	            }  */
		}
		
		
		
		@Override
		public void onTaskProcessStatusChanged(int cityId,String downloadURL, long speed,long totalBytes, long curPos,boolean notFinish) throws RemoteException
		{ 
            final  DownloadInfos dl = new DownloadInfos(cityId,downloadURL,speed,totalBytes,curPos,notFinish);  	           
            Thread thread = new Thread(new Runnable()
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
			installCityData = DownloadManager.getInstallCity();
			installedCityList.clear();
			installedCityList.addAll(installCityData.values());
			downloadDataListAdapter.setInstalledCityList(installedCityList);
			downloadDataListAdapter.notifyDataSetChanged();
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
				ProgressBar downloadBar = (ProgressBar) openCtiyDataListView.findViewWithTag("bar"+position);			
				TextView resultView = (TextView) openCtiyDataListView.findViewWithTag("result"+position);		
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
				ProgressBar downloadBar = (ProgressBar) openCtiyDataListView.findViewWithTag("bar"+position);			
				TextView resultView = (TextView) openCtiyDataListView.findViewWithTag("result"+position);					
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
			intent.setClass(OpenCityDataActivity.this, IndexActivity.class);
			startActivity(intent);
			
		}
	};
	
	private OnClickListener listViewOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			int position = (Integer) v.getTag();
			City city = cityList.get(position);
			AppManager.getInstance().setCurrentCityId(city.getCityId());
			Intent intent = new Intent();
			intent.setClass(OpenCityDataActivity.this, IndexActivity.class);
			startActivity(intent);
		}
	};
	
	
	

	
	
	
	
	
	private void download(final int cityId,final String downloadURL, final String downloadSavePath,final String tempPath)
	{
		downloadStatus.put(downloadURL, DOWNLOAD_ING);
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
						Toast.makeText(OpenCityDataActivity.this, getResources().getString(R.string.download_connection_error), Toast.LENGTH_LONG).show();
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
		downloadStatus.put(downloadURL, DOWNLOAD_STOP);
		Thread thread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{					
					iDownloadService.pauseDownload(downloadURL);
					resultTextMap.remove(downloadURL);
					progressBarMap.remove(downloadURL);
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
		downloadStatus.put(downloadURL, DOWNLOAD_ING);
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
	protected void onDestroy()
	{
		unbindService(conn);
		super.onDestroy();
	}


	
	
	
	
	public class OpenCityDataAdapter extends BaseAdapter
	{
		private static final String TAG = "OpenCityDataAdapter";
		private List<City> cityDataList;
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
			if (convertView == null)
			{
				convertView = LayoutInflater.from(context).inflate(R.layout.open_city_item, null);
			}
			convertView.setTag(position);
			City city = cityDataList.get(position);
			positionMap.put(city.getDownloadURL(), position);
			TextView dataCityName = (TextView) convertView.findViewById(R.id.data_city_name);
			if(city.getCityId() == currentCityId)
			{
				ImageView dataSelectIcon = (ImageView) convertView.findViewById(R.id.data_staus);
				dataSelectIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.yes_s));
				dataCityName.setTextColor(context.getResources().getColor(R.color.red));
			}else
			{
				ImageView dataSelectIcon = (ImageView) convertView.findViewById(R.id.data_staus);
				dataSelectIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.no_s));
				dataCityName.setTextColor(context.getResources().getColor(R.color.black));
			}
			DownloadBean downloadBean = downloadManager.getUnfinishDownTask(city.getDownloadURL());
			dataCityName.setText(city.getCountryName()+"."+city.getCityName());
			ViewGroup listViewItemGroup = (ViewGroup) convertView.findViewById(R.id.listview_item_group);
			TextView dataSize = (TextView) convertView.findViewById(R.id.data_size);
			ViewGroup buttonGroup = (ViewGroup) convertView.findViewById(R.id.button_group);
			TextView installedTextView = (TextView) convertView.findViewById(R.id.installed);
			TextView installingTextView = (TextView) convertView.findViewById(R.id.installing);
			ViewGroup dataDownloadMangerGroup = (ViewGroup) convertView.findViewById(R.id.download_status_group);
			ImageView restartDownloadBtn = (ImageView) convertView.findViewById(R.id.restart_download_btn);
			ImageView stopDownloadBtn = (ImageView) convertView.findViewById(R.id.stop_download_btn);
			ImageButton onlineButton = (ImageButton) convertView.findViewById(R.id.online_button);				
			ImageButton startButton = (ImageButton) convertView.findViewById(R.id.start_download_button);
			ImageButton cancelButton = (ImageButton) convertView.findViewById(R.id.cancel_download_button);
			ViewGroup startGroup = (ViewGroup)convertView.findViewById(R.id.start_download_manager_group);
			ViewGroup cancelGroup = (ViewGroup)convertView.findViewById(R.id.cancel_download_manager_group);
			downloadBar = (ProgressBar) convertView.findViewById(R.id.downloadbar);
			resultTextView = (TextView) convertView.findViewById(R.id.download_persent);
			if(installCityData!=null && installCityData.containsKey(city.getCityId()))
			{
				installedTextView.setVisibility(View.VISIBLE);
				buttonGroup.setVisibility(View.GONE);
				dataDownloadMangerGroup.setVisibility(View.GONE);
				
			}else
			{				
				if(downloadStatus.get(city.getDownloadURL())!=null && downloadStatus.get(city.getDownloadURL()) == DOWNLOAD_ING)
				{
					dataDownloadMangerGroup.setVisibility(View.VISIBLE);
					restartDownloadBtn.setVisibility(View.GONE);
					stopDownloadBtn.setVisibility(View.VISIBLE);
					convertView.findViewById(R.id.start_download_manager_group).setVisibility(View.GONE);
					convertView.findViewById(R.id.cancel_download_manager_group).setVisibility(View.VISIBLE);
				}else
				{
					if(downloadBean != null)
					{	
						convertView.findViewById(R.id.start_download_manager_group).setVisibility(View.GONE);
						convertView.findViewById(R.id.cancel_download_manager_group).setVisibility(View.VISIBLE);
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
						if(city.getDataSize() !=0)
						{	
							convertView.findViewById(R.id.start_download_manager_group).setVisibility(View.VISIBLE);
						}
					}
				}				
				onlineButton.setTag(city.getCityId());
				startButton.setTag(position);
				listViewItemGroup.setTag(position);
				startGroup.setTag("startGroup"+position);
				cancelGroup.setTag("cancelGroup"+position);
				dataDownloadMangerGroup.setTag("group"+position);
				buttonGroup.setTag("button"+position);
				installedTextView.setTag("installed"+position);
				installingTextView.setTag("installing"+position);
				cancelButton.setTag("cancel"+position);		
				restartDownloadBtn.setTag("restart"+position);
				stopDownloadBtn.setTag("pause"+position);
				resultTextView.setTag("result"+position);
				downloadBar.setTag("bar" + position);
				dataSize.setTag("datasize"+position);
				startButton.setOnClickListener(startDownloadClickListener);
				cancelButton.setOnClickListener(cancelOnClickListener);
				restartDownloadBtn.setOnClickListener(restartDownloadClickListener);
				stopDownloadBtn.setOnClickListener(stopDownloadOnClickListener);
				onlineButton.setOnClickListener(onlineOnClickListener);
			}					
			listViewItemGroup.setOnClickListener(listViewOnClickListener);
			return convertView;
		}

		public List<City> getCityList()
		{
			return cityDataList;
		}

		public void setCityList(List<City> cityList)
		{
			this.cityDataList = cityList;
		}
		

	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (  keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
			 Intent intent = new Intent(this, MoreActivity.class);
			    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    startActivity(intent);
	        return true;
	    }
		else
		{
			  return super.onKeyDown(keyCode, event);	
		}
	  
	}






	@Override
	protected void onResume()
	{
		super.onResume();
		Log.i(TAG, "onResume");
		currentCityId = AppManager.getInstance().getCurrentCityId();
		/*installCityData = DownloadManager.getInstallCity();
		cityListAdapter.setCityList(cityList);
		cityListAdapter.notifyDataSetChanged();*/
	}


	
	
	
}
