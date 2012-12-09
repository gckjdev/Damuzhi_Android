package com.damuzhi.travel.activity.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.location.LocationMager;
import com.damuzhi.travel.activity.common.location.LocationUtil;
import com.damuzhi.travel.activity.fly.CommonFlyActivity;
import com.damuzhi.travel.activity.happyRoute.CommonHappyRouteActivity;
import com.damuzhi.travel.activity.more.MoreActivity;
import com.damuzhi.travel.activity.more.OpenCityActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.activity.touristRoute.CommonLocalTripsActivity;
import com.damuzhi.travel.db.DownloadPreference;
import com.damuzhi.travel.download.DownloadService;
import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.mission.common.HelpMission;
import com.damuzhi.travel.mission.more.UpdateMission;
import com.damuzhi.travel.mission.more.MoreMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.util.TravelUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.maps.TapControlledMapView;
import com.umeng.analytics.MobclickAgent;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;



public class MainActivity extends TabActivity {

	private static final String TAG = "MainActivity";
	private TabHost mTabHost;
	private LayoutInflater mLayoutflater;
	private ViewGroup tapTopGroup;
	private TextView currentCityName;
	private TextView titleTextView;
	boolean flag = false;//flag go to OpenCityActivity
	private ImageView moveFlag;
	// AlertDialog alertDialog;
	private Bundle bundle;
	private PopupWindow alertPopupWindow;
	private View alertDialogView;
	private int lastNotifyId = -100;
	LocationMager locationMager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.d(TAG, "oncreate");
		ActivityMange.getInstance().addActivity(this);
		mLayoutflater = getLayoutInflater();
		mTabHost = getTabHost();
		initTabHost();
		mTabHost.setCurrentTab(0);
		titleTextView = (TextView) findViewById(R.id.title);
		currentCityName = (TextView) findViewById(R.id.current_city_name);
		currentCityName.setText(AppManager.getInstance().getCurrentCityName());
		moveFlag = (ImageView) findViewById(R.id.move_flag);
		tapTopGroup = (ViewGroup) findViewById(R.id.tab_top);
		tapTopGroup.setOnClickListener(tapTopOnClickListener);
		mTabHost.setOnTabChangedListener(onTabChangeListener);
		
		bundle = getIntent().getBundleExtra("notify");
		locationMager = new LocationMager(this);
		locationMager.getLocation();
		checkData();
	}
	
	
	private void initTabHost()
	{
		View tabHostItem1 = mLayoutflater.inflate(R.layout.tab_host_item, null);
		ImageView tabItemButton1 = (ImageView) tabHostItem1.findViewById(R.id.tab_item_button);
		tabItemButton1.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_menu_guide_btn));
		
		Intent intent1 = new Intent(MainActivity.this,IndexActivity.class);
		TabSpec tabSpec1 = mTabHost.newTabSpec("guide");
		tabSpec1.setIndicator(tabHostItem1);
		tabSpec1.setContent(intent1);
		mTabHost.addTab(tabSpec1);
		
		
		View tabHostItem2 = mLayoutflater.inflate(R.layout.tab_host_item, null);
		ImageView tabItemButton2 = (ImageView) tabHostItem2.findViewById(R.id.tab_item_button);
		tabItemButton2.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_menu_local_travel_btn));	
		
		Intent intent2 = new Intent(MainActivity.this,CommonLocalTripsActivity.class);
		TabSpec tabSpec2 = mTabHost.newTabSpec("local");
		tabSpec2.setIndicator(tabHostItem2);
		tabSpec2.setContent(intent2);
		mTabHost.addTab(tabSpec2);
		
		
		View tabHostItem3 = mLayoutflater.inflate(R.layout.tab_host_item, null);
		ImageView tabItemButton3 = (ImageView) tabHostItem3.findViewById(R.id.tab_item_button);
		tabItemButton3.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_menu_fly_btn));	
		Intent intent3 = new Intent(MainActivity.this,CommonFlyActivity.class);
		TabSpec tabSpec3 = mTabHost.newTabSpec("fly");
		tabSpec3.setIndicator(tabHostItem3);
		tabSpec3.setContent(intent3);
		mTabHost.addTab(tabSpec3);
		
		
		View tabHostItem4 = mLayoutflater.inflate(R.layout.tab_host_item, null);
		ImageView tabItemButton4 = (ImageView) tabHostItem4.findViewById(R.id.tab_item_button);
		tabItemButton4.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_menu_happy_trips_btn));	
		Intent intent4 = new Intent(MainActivity.this,CommonHappyRouteActivity.class);
		TabSpec tabSpec4 = mTabHost.newTabSpec("happy");
		tabSpec4.setIndicator(tabHostItem4);
		tabSpec4.setContent(intent4);
		mTabHost.addTab(tabSpec4);
		
		
		View tabHostItem5 = mLayoutflater.inflate(R.layout.tab_host_item, null);
		ImageView tabItemButton5 = (ImageView) tabHostItem5.findViewById(R.id.tab_item_button);
		tabItemButton5.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_menu_more_btn));	
		Intent intent5 = new Intent(MainActivity.this,MoreActivity.class);
		TabSpec tabSpec5 = mTabHost.newTabSpec("more");
		tabSpec5.setIndicator(tabHostItem5);
		tabSpec5.setContent(intent5);
		mTabHost.addTab(tabSpec5);
		
	}


	private OnClickListener tapTopOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			//LocationUtil.stop();
			flag = true;
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, OpenCityActivity.class);
			startActivity(intent);
		}
	};

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}


	@Override
	protected void onPause()
	{	
		super.onPause();
		MobclickAgent.onPause(this);
		Log.d(TAG, "onPause");
	}


	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "main avtivity onResume");
		MobclickAgent.onResume(this);
		String cityName = AppManager.getInstance().getCurrentCityName();
		if(cityName == null||cityName.equals(""))
		{
			int defaultCityId = AppManager.getInstance().getDefaulCityId();
			AppManager.getInstance().setCurrentCityId(defaultCityId);
			cityName = AppManager.getInstance().getCurrentCityName();
		}
		currentCityName.setText(cityName);
		boolean cityFlag = TravelApplication.getInstance().isCityFlag();
		if((flag&&mTabHost.getCurrentTab()==1)||cityFlag)
		{
			mTabHost.setCurrentTab(0);
			flag = false;
			TravelApplication.getInstance().setCityFlag(false);
		}
		
		if(TravelApplication.getInstance().getLocation()!=null&&TravelApplication.getInstance().getLocation().size()>0)
		{
			locationMager.destroyMyLocation();
		}
		
		
		if(bundle != null)
		{
			Log.d(TAG, "get notify bundle onNewIntent");
			String title = bundle.getString("title");
			String content = bundle.getString("content");
			String type = bundle.getString("Type");
			int notifyId = 0;
			if(type != null &&!type.equals(""))
			{
				notifyId = Integer.parseInt(type);
			}
			if(lastNotifyId !=notifyId)
			{
				lastNotifyId = notifyId;
				alertWindow(notifyId, title, content);
			}
			
		}
		
	}
	
	
	
	
	private OnTabChangeListener onTabChangeListener = new OnTabChangeListener()
	{
		
		@Override
		public void onTabChanged(String tabId)
		{
			ImageLoader.getInstance().clearMemoryCache();
			move(tabId);
			if(tabId.equals("more")||tabId.equals("fly")||tabId.equals("happy") )
			{
				tapTopGroup.setVisibility(View.GONE);
			}else
			{
				tapTopGroup.setVisibility(View.VISIBLE);
			}
			if(tabId.equals("guide"))
			{
				titleTextView.setText(getString(R.string.city_guide));
				return;
			}
			if(tabId.equals("local"))
			{
				titleTextView.setText(getString(R.string.local_route));
				return;
			}
		}
	};

	private float endSet = 0;
	private float offSet = 0;
	
	private void move(String tabId)
	{
		Animation animation = null;
		getSet(tabId);
		animation = new TranslateAnimation(offSet,endSet, 0, 0);
		animation.setDuration(500);		
		moveFlag.startAnimation(animation);
		animation.setFillAfter(true);
		offSet = endSet;
	}

	private void getSet(String tabId)
	{
		if(tabId.equals("guide"))
		{
			endSet = 0;
			return;
		}
		if(tabId.equals("local"))
		{
			endSet = 95;
			return;
		}
		if(tabId.equals("fly"))
		{
			endSet = 195;
			return;
		}
		if(tabId.equals("happy"))
		{
			endSet = 288;
			return;
		}
		if(tabId.equals("more"))
		{
			endSet = 388;
			return;
		}
	}
	


    @Override  
    public boolean dispatchKeyEvent(KeyEvent event) {  
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){  
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {   

			AlertDialog leaveAlertDialog = new AlertDialog.Builder(MainActivity.this).create();
			leaveAlertDialog.setMessage(getBaseContext().getString(R.string.leave_alert_dilaog));
			leaveAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getBaseContext().getString(R.string.exit),new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					//TravelApplication.getInstance().getHttpClient().getConnectionManager().shutdown();
					AppMission.getInstance().saveCurrentCityId(MainActivity.this);
					TravelApplication.getInstance().setToken("");
					TravelApplication.getInstance().setLoginID("");
					HttpTool.getInstance().closeConnection();
					ActivityMange.getInstance().AppExit(MainActivity.this);
				}
			} );
			leaveAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+getBaseContext().getString(R.string.cancel),new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.cancel();
					
				}
			} );
			leaveAlertDialog.show();
        }  
        return true;  
        }  
        return super.dispatchKeyEvent(event);  
    }  
	
    private void checkData()
	{
    	HelpMission.getInstance().updateHelpData(MainActivity.this);  
    	
		AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{
				
				float remoteVersion = UpdateMission.getInstance().getNewVersion();
				float localVersion = TravelUtil.getVersionName(MainActivity.this);
				Log.d(TAG, "app Version = "+localVersion);
				if(remoteVersion>localVersion)
				{
					String title = UpdateMission.getInstance().getAppUpdateTile();
					String content = UpdateMission.getInstance().getAppUpdateContent();
					Looper.prepare();
					updateAppVersion(title,content);
					Looper.loop();					
				}	
				City city = AppManager.getInstance().getCityByCityId(AppManager.getInstance().getCurrentCityId());
				String downloadURL =null;
				if(city != null &&city.hasDownloadURL())
				{	
					downloadURL = city.getDownloadURL();
					Map<Integer, Integer> unfinishInstallCity = DownloadPreference.getAllUnfinishInstall(MainActivity.this);
					Map<Integer, Integer> installCityData = DownloadPreference.getAllDownloadInfo(MainActivity.this);
					Map<Integer, String> newVersionCityData = TravelApplication.getInstance().getNewVersionCityData();
					List<Integer> installedCityList = new ArrayList<Integer>();
					installedCityList.clear();
					installedCityList.addAll(installCityData.keySet());
					if(installCityData != null&&installCityData.size()>0)
					{
						if(newVersionCityData == null)
						{
							newVersionCityData = UpdateMission.getInstance().getNewVersionCityData(installedCityList);
							TravelApplication.getInstance().setNewVersionCityData(newVersionCityData);
						}
						
					}
					int currentCityId = AppManager.getInstance().getCurrentCityId();
					if(downloadURL != null&&!downloadURL.equals(""))
					{
						if(newVersionCityData!= null&&newVersionCityData.containsKey(currentCityId)&&!DownloadService.downloadStstudTask.containsKey(downloadURL))
						{
							String title = city.getCountryName()+"."+city.getCityName();
							String content = getResources().getString(R.string.update_city_data_content)+"\n"+"大小:"+TravelUtil.getDataSize(city.getDataSize());
							Looper.prepare();
							checkDataVersion(title,content);
							Looper.loop();
						}
						if(unfinishInstallCity.containsKey(currentCityId)&&!DownloadService.downloadStstudTask.containsKey(downloadURL))
						{
							String content = getResources().getString(R.string.install_data_unfinish);
							Looper.prepare();
							installData("",content);
							Looper.loop();
						}
					}
				}
				return null;
			}

		
	
		};
		asyncTask.execute();
	}
	
    
    private void checkDataVersion(String title,String content)
	{
		alertWindow(3, title,content);
	}
    
    
    private void updateAppVersion(String title,String content)
	{
    	alertWindow(2, title, content);
	}
	
	
	
	
	
	
	private void installData(String title,String content)
	{
		
		alertWindow(4, title, content);
	}

	
	private void alertWindow(int infoType,String title,String content)
	{
		alertDialogView = getLayoutInflater().inflate(R.layout.alert_dialog_2, null);
		alertPopupWindow = new PopupWindow(alertDialogView, LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.half_transparent));
		alertPopupWindow.setBackgroundDrawable(background);
		alertPopupWindow.setOutsideTouchable(true);
		alertPopupWindow.update();
		
		TextView alertTitleTextView = (TextView) alertDialogView.findViewById(R.id.alert_dialog_title);
		TextView titleTextView = (TextView) alertDialogView.findViewById(R.id.title);
		TextView contentTextView = (TextView) alertDialogView.findViewById(R.id.content);
		Button positiveButton = (Button) alertDialogView.findViewById(R.id.positive_button);
		Button negativeButton = (Button) alertDialogView.findViewById(R.id.negative_button);
		titleTextView.setText(title);
		contentTextView.setText(content);
		negativeButton.setOnClickListener(negativeButtonClickListener);
		
		switch (infoType)
		{
		case 1:
			alertTitleTextView.setText(getString(R.string.message_tips));
			positiveButton.setVisibility(View.GONE);
			break;
		case 2:
			alertTitleTextView.setText(getString(R.string.new_version_tips));
			negativeButton.setText(getString(R.string.update_later));
			positiveButton.setOnClickListener(updateVersionOnClickListener);
			break;
		case 3:
			alertTitleTextView.setText(getString(R.string.new_city_data_tips));
			negativeButton.setText(getString(R.string.update_later));
			positiveButton.setOnClickListener(updateCityDataClickListener);
			break;
		case 4:
			alertTitleTextView.setText(getString(R.string.install_data_unfinish));
			positiveButton.setText(getString(R.string.install_now));
			negativeButton.setText(getString(R.string.install_later));
			positiveButton.setOnClickListener(installClickListener);
			break;
		default:
			alertTitleTextView.setText(getString(R.string.message_tips));
			positiveButton.setVisibility(View.GONE);
			break;
		}
		alertPopupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER,0, 350);
	}
	
	
	private OnClickListener updateVersionOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Uri uri = Uri.parse(MobclickAgent.getConfigParams(MainActivity.this, ConstantField.U_MENG_DOWNLOAD_CONFIGURE));
			Log.d(TAG, "<updateAppVersion> uri = "+uri);
			if(uri!=null&&!uri.equals(""))
			{
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
			if(alertPopupWindow != null)
			{
				alertPopupWindow.dismiss();
			}
		}
	};
	
	
	private OnClickListener updateCityDataClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			intent.putExtra("updateData", 1);
			intent.setClass(MainActivity.this, OpenCityActivity.class);
			startActivity(intent);	
			if(alertPopupWindow != null)
			{
				alertPopupWindow.dismiss();
			}
		}
	};
	
	private OnClickListener negativeButtonClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			if(alertPopupWindow != null)
			{
				alertPopupWindow.dismiss();
			}
		}
	};
	
	private OnClickListener installClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			intent.putExtra("updateData", 0);
			intent.setClass(MainActivity.this, OpenCityActivity.class);
			startActivity(intent);
			if(alertPopupWindow != null)
			{
				alertPopupWindow.dismiss();
			}
		}
	};
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		Log.d(TAG, "onNewIntent");
		bundle = intent.getBundleExtra("notify");
		
	}
	
	
	
	
}

