package com.damuzhi.travel.activity.entry;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.R.integer;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.ContactsContract.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.damuzhi.travel.activity.adapter.common.SortAdapter;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.location.LocationUtil;
import com.damuzhi.travel.activity.favorite.MyFavoritePlaceActivity;
import com.damuzhi.travel.activity.more.MoreActivity;
import com.damuzhi.travel.activity.more.OpenCityActivity;
import com.damuzhi.travel.activity.overview.CommonCtiyBaseActivity;
import com.damuzhi.travel.activity.overview.CommonTravelPreprationActivity;
import com.damuzhi.travel.activity.overview.CommonTravelTransportationActivity;
import com.damuzhi.travel.activity.overview.CommonTravelUtilityActivity;
import com.damuzhi.travel.activity.overview.TravelRoutesActivity;
import com.damuzhi.travel.activity.overview.TravelGuidesActivity;
import com.damuzhi.travel.activity.place.CommonEntertainmentActivity;
import com.damuzhi.travel.activity.place.CommonHotelActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.activity.place.CommonRestaurantActivity;
import com.damuzhi.travel.activity.place.CommonShoppingActivity;

import com.damuzhi.travel.activity.place.CommonNearbyPlaceActivity;
import com.damuzhi.travel.activity.place.CommonSpotActivity;
import com.damuzhi.travel.activity.share.Share2Weibo;
import com.damuzhi.travel.db.DownloadPreference;
import com.damuzhi.travel.download.DownloadService;
import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.mission.favorite.FavoriteMission;
import com.damuzhi.travel.mission.more.UpdateMission;
import com.damuzhi.travel.mission.more.MoreMission;
import com.damuzhi.travel.mission.place.LocalStorageMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.App;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;
import com.google.android.maps.MapView.LayoutParams;
import com.umeng.analytics.MobclickAgent;
import com.damuzhi.travel.R;

import dalvik.system.VMRuntime;
public class IndexActivity extends MenuActivity implements OnClickListener
{
	private static final String TAG = "IndexActivity";
	private ImageButton sceneryButton;
	private ImageButton hotelButton;
	private ImageButton restaurantButton;
	private ImageButton shoppingButton;
	private ImageButton entertainmentButton;
	private ImageButton nearbyButton;
	private ImageButton citybaseButton;
	private ImageButton travelPreprationButton;
	private ImageButton travelUtilityButton;
	private ImageButton travelTransportaionButton;
	private ImageButton travelTipsButton;
	private ImageButton favoriteButton;

	private final static int HEAP_SIZE = 8* 1024* 1024 ;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		ActivityMange.getInstance().addActivity(this);
		setContentView(R.layout.index);		
		MobclickAgent.updateOnlineConfig(this);
		VMRuntime.getRuntime().setMinimumHeapSize(HEAP_SIZE);
		
		sceneryButton = (ImageButton) findViewById(R.id.scenery);
		hotelButton = (ImageButton) findViewById(R.id.hotel);		
		restaurantButton = (ImageButton) findViewById(R.id.restaurant);
		shoppingButton = (ImageButton) findViewById(R.id.shopping);
		entertainmentButton = (ImageButton) findViewById(R.id.entertainment);
		nearbyButton = (ImageButton) findViewById(R.id.nearby);
		citybaseButton = (ImageButton) findViewById(R.id.city_base);		
		travelPreprationButton = (ImageButton) findViewById(R.id.travel_prepration);
		travelUtilityButton = (ImageButton) findViewById(R.id.travel_utility);
		travelTransportaionButton = (ImageButton) findViewById(R.id.travel_transportation);
		travelTipsButton = (ImageButton) findViewById(R.id.travel_tips);
		nearbyButton = (ImageButton) findViewById(R.id.nearby);
		favoriteButton = (ImageButton) findViewById(R.id.favorite);
		
		sceneryButton.setOnClickListener(this);
		hotelButton.setOnClickListener(this);
		restaurantButton.setOnClickListener(this);
		shoppingButton.setOnClickListener(this);
		entertainmentButton.setOnClickListener(this);
		nearbyButton.setOnClickListener(this);
		citybaseButton.setOnClickListener(this);
		travelPreprationButton.setOnClickListener(this);
		travelUtilityButton.setOnClickListener(this);
		travelTransportaionButton.setOnClickListener(this);
		travelTipsButton.setOnClickListener(this);
		
		favoriteButton.setOnClickListener(favoriteOnClickListener);
		
		
		
		
		
	}

	
	
	
	
	@Override
	protected void onResume()
	{
		
		super.onResume();
		/*Log.d(TAG, "index activity onResume");
		float availableMemory  = TravelUtil.getAvailableInternalMemorySize();
		Log.d(TAG, " available memory = "+availableMemory);
		ActivityManager activityMange =  (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		int activityMemory = activityMange.getMemoryClass();
		Log.d(TAG, "activity large memory "+activityMemory);
		
		long runTimeFreeMemory = Runtime.getRuntime().freeMemory();
		Log.d(TAG, "run time free memory = "+TravelUtil.getDataSize(runTimeFreeMemory));
		long runTimeTotalMemory = Runtime.getRuntime().totalMemory();
		Log.d(TAG, "run time total memory = "+TravelUtil.getDataSize(runTimeTotalMemory));
		long runTimeMaxMemory = Runtime.getRuntime().maxMemory();
		Log.d(TAG, "run time max menory = "+TravelUtil.getDataSize(runTimeMaxMemory));*/
	}
	
	
	
	
	@Override
	public void onClick(View v)
	{
		ImageButton button = (ImageButton) v;
		
		switch (button.getId())
		{
		case R.id.scenery:	
			 	Intent sceneryIntent = new Intent();
				sceneryIntent.setClass(IndexActivity.this, CommonSpotActivity.class);	
				startActivity(sceneryIntent);
			break;
		case R.id.hotel:
				Intent hotelIntent = new Intent();
				hotelIntent.setClass(IndexActivity.this, CommonHotelActivity.class);		
				startActivity(hotelIntent);
			break;
		case R.id.restaurant:
				Intent restaurantIntent = new Intent();
				restaurantIntent.setClass(IndexActivity.this, CommonRestaurantActivity.class);		
				startActivity(restaurantIntent);
			break;
		case R.id.shopping:	
				Intent shoppingIntent = new Intent();
				shoppingIntent.setClass(IndexActivity.this, CommonShoppingActivity.class);		
				startActivity(shoppingIntent);
			break;
		case R.id.entertainment:
				Intent entertainmentIntent = new Intent();
				entertainmentIntent.setClass(IndexActivity.this, CommonEntertainmentActivity.class);		
				startActivity(entertainmentIntent);
			break;
		case R.id.nearby:
				boolean gpsEnable = checkGPSisOpen();
				if(gpsEnable)
				{
					Intent nearbyIntent = new Intent();
					nearbyIntent.setClass(IndexActivity.this, CommonNearbyPlaceActivity.class);		
					startActivity(nearbyIntent);
				}else {
					setGPSDialog();
				}
			break;
		case R.id.city_base:
			LocationUtil.stop();
			Intent cityBaseIntent = new Intent();
			cityBaseIntent.setClass(IndexActivity.this, CommonCtiyBaseActivity.class);		
			startActivity(cityBaseIntent);
			break;
		case R.id.travel_prepration:	
			LocationUtil.stop();
			Intent travelPreprationIntent = new Intent();
			travelPreprationIntent.setClass(IndexActivity.this, CommonTravelPreprationActivity.class);		
			startActivity(travelPreprationIntent);
			break;
		case R.id.travel_utility:	
			LocationUtil.stop();
			Intent travelUtilityIntent = new Intent();
			travelUtilityIntent.setClass(IndexActivity.this, CommonTravelUtilityActivity.class);		
			startActivity(travelUtilityIntent);
			break;
		case R.id.travel_transportation:	
			LocationUtil.stop();
			Intent travelTransportationIntent = new Intent();
			travelTransportationIntent.setClass(IndexActivity.this, CommonTravelTransportationActivity.class);		
			startActivity(travelTransportationIntent);
			break;
		case R.id.travel_tips:	
			LocationUtil.stop();
			Intent travelTipsIntent = new Intent();
			travelTipsIntent.setClass(IndexActivity.this, TravelGuidesActivity.class);		
			startActivity(travelTipsIntent);
			break;
		default:
			break;
		}
		
	}
	
	
	

	
	
	private OnClickListener favoriteOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent  intent = new Intent();
			intent.setClass(IndexActivity.this, MyFavoritePlaceActivity.class);
			startActivity(intent);
			
		}
	};


	
	private boolean checkGPSisOpen() {
		LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		}
			Toast.makeText(this, getString(R.string.open_gps_tips2), Toast.LENGTH_SHORT).show();
			return false;
	}
	
	private void setGPSDialog()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(IndexActivity.this).create();
		alertDialog.setMessage(getBaseContext().getString(R.string.go_to_gps_setting_tips));
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getBaseContext().getString(R.string.ok),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				 Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				 startActivity(gpsIntent);
				
			}	
		} );
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+getBaseContext().getString(R.string.cancel),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				
			}
		} );
		alertDialog.show();	
	}
	
}
