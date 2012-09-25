package com.damuzhi.travel.activity.entry;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.activity.touristRoute.CommonLocalTrips;
import com.readystatesoftware.maps.TapControlledMapView;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;



public class Main extends TabActivity {

	private TabHost mTabHost;
	//private TabWidget mTabWidget;
	private LayoutInflater mLayoutflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Tabhost);
		setContentView(R.layout.main);
		
		mLayoutflater = getLayoutInflater();
		mTabHost = getTabHost();
		initTabHost();
		mTabHost.setCurrentTab(0);
	}
	
	
	private void initTabHost()
	{
		View tabHostItem1 = mLayoutflater.inflate(R.layout.tab_host_item, null);
		ImageView tabItemButton1 = (ImageView) tabHostItem1.findViewById(R.id.tab_item_button);
		tabItemButton1.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_menu_guide_btn));
		
		Intent intent1 = new Intent(Main.this,IndexActivity.class);
		TabSpec tabSpec1 = mTabHost.newTabSpec("guide");
		tabSpec1.setIndicator(tabHostItem1);
		tabSpec1.setContent(intent1);
		mTabHost.addTab(tabSpec1);
		
		
		View tabHostItem2 = mLayoutflater.inflate(R.layout.tab_host_item, null);
		ImageView tabItemButton2 = (ImageView) tabHostItem2.findViewById(R.id.tab_item_button);
		tabItemButton2.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_menu_local_travel_btn));	
		
		Intent intent2 = new Intent(Main.this,CommonLocalTrips.class);
		TabSpec tabSpec2 = mTabHost.newTabSpec("local");
		tabSpec2.setIndicator(tabHostItem2);
		tabSpec2.setContent(intent2);
		mTabHost.addTab(tabSpec2);
		
		
		View tabHostItem3 = mLayoutflater.inflate(R.layout.tab_host_item, null);
		ImageView tabItemButton3 = (ImageView) tabHostItem3.findViewById(R.id.tab_item_button);
		tabItemButton3.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_menu_fly_btn));	
		Intent intent3 = new Intent(Main.this,IndexActivity.class);
		TabSpec tabSpec3 = mTabHost.newTabSpec("fly");
		tabSpec3.setIndicator(tabHostItem3);
		tabSpec3.setContent(intent3);
		mTabHost.addTab(tabSpec3);
		
		
		View tabHostItem4 = mLayoutflater.inflate(R.layout.tab_host_item, null);
		ImageView tabItemButton4 = (ImageView) tabHostItem4.findViewById(R.id.tab_item_button);
		tabItemButton4.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_menu_happy_trips_btn));	
		Intent intent4 = new Intent(Main.this,IndexActivity.class);
		TabSpec tabSpec4 = mTabHost.newTabSpec("happy");
		tabSpec4.setIndicator(tabHostItem4);
		tabSpec4.setContent(intent4);
		mTabHost.addTab(tabSpec4);
		
		
		View tabHostItem5 = mLayoutflater.inflate(R.layout.tab_host_item, null);
		ImageView tabItemButton5 = (ImageView) tabHostItem5.findViewById(R.id.tab_item_button);
		tabItemButton5.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_menu_more_btn));	
		Intent intent5 = new Intent(Main.this,IndexActivity.class);
		TabSpec tabSpec5 = mTabHost.newTabSpec("more");
		tabSpec5.setIndicator(tabHostItem5);
		tabSpec5.setContent(intent5);
		mTabHost.addTab(tabSpec5);
		
	}


	
	
	
	
	

}
