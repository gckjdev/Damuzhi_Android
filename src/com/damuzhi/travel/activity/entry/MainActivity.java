package com.damuzhi.travel.activity.entry;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.location.LocationUtil;
import com.damuzhi.travel.activity.fly.CommonFlyActivity;
import com.damuzhi.travel.activity.happyRoute.CommonHappyRouteActivity;
import com.damuzhi.travel.activity.more.MoreActivity;
import com.damuzhi.travel.activity.more.OpenCityActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.activity.touristRoute.CommonLocalTripsActivity;
import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.model.app.AppManager;
import com.readystatesoftware.maps.TapControlledMapView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;



public class MainActivity extends TabActivity {

	private static final String TAG = "MainActivity";
	private TabHost mTabHost;
	//private TabWidget mTabWidget;
	private LayoutInflater mLayoutflater;
	private ViewGroup tapTopGroup;
	private TextView currentCityName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setTheme(R.style.Theme_Tabhost);
		setContentView(R.layout.main);
		ActivityMange.getInstance().addActivity(this);
		mLayoutflater = getLayoutInflater();
		mTabHost = getTabHost();
		initTabHost();
		mTabHost.setCurrentTab(0);
		
		currentCityName = (TextView) findViewById(R.id.current_city_name);
		currentCityName.setText(AppManager.getInstance().getCurrentCityName());
		tapTopGroup = (ViewGroup) findViewById(R.id.tab_top);
		tapTopGroup.setOnClickListener(tapTopOnClickListener);
		mTabHost.setOnTabChangedListener(onTabChangeListener);
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
			LocationUtil.stop();
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
		Log.d(TAG, "onPause");
	}


	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "main avtivity onResume");
		String cityName = AppManager.getInstance().getCurrentCityName();
		if(cityName == null||cityName.equals(""))
		{
			int defaultCityId = AppManager.getInstance().getDefaulCityId();
			AppManager.getInstance().setCurrentCityId(defaultCityId);
			cityName = AppManager.getInstance().getCurrentCityName();
		}
		currentCityName.setText(cityName);
	}
	
	
	
	
	private OnTabChangeListener onTabChangeListener = new OnTabChangeListener()
	{
		
		@Override
		public void onTabChanged(String tabId)
		{
			if(tabId.equals("more")||tabId.equals("fly")||tabId.equals("happy") )
			{
				tapTopGroup.setVisibility(View.GONE);
			}else
			{
				tapTopGroup.setVisibility(View.VISIBLE);
			}
			
		}
	};

	



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
					AppMission.getInstance().saveCurrentCityId(MainActivity.this);
					TravelApplication.getInstance().setToken("");
					TravelApplication.getInstance().setLoginID("");
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
	
	
	

}
