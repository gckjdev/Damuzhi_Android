package com.damuzhi.travel.activity.entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.R.integer;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.more.MoreActivity;
import com.damuzhi.travel.activity.overview.OverviewActivity;
import com.damuzhi.travel.activity.overview.TravelTipsActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.activity.place.EntertainmentActivity;
import com.damuzhi.travel.activity.place.HotelActivity;
import com.damuzhi.travel.activity.place.NearbyActivity;
import com.damuzhi.travel.activity.place.RestaurantActivity;
import com.damuzhi.travel.activity.place.SceneryActivity;
import com.damuzhi.travel.activity.place.ShoppingActivity;
import com.damuzhi.travel.activity.place.SpotActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.service.Task;

public class IndexActivity extends MenuActivity implements OnClickListener
{
	private static final String TAG = "IndexActivity";
	private ImageButton moreButton;
	private ImageButton sceneryButton;
	private ImageButton hotelButton;
	private ImageButton restaurantButton;
	private ImageButton shoppingButton;
	private ImageButton entertainmentButton;
	private ImageButton nearbyButton;
	private TravelApplication application;
	private ImageButton citybaseButton;
	private ImageButton travelPreprationButton;
	private ImageButton travelUtilityButton;
	private ImageButton travelTransportaionButton;
	private ImageButton travelTipsButton;
	private ImageButton routeTipsButton;
	private HashMap<String, Integer> cityNameMap;
	private List<String> list;
	private Spinner city;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//LocationUtil.getCurrentLocation(this);
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.index);
		setProgressBarIndeterminateVisibility(true);
		MainService.allActivity.add(this);
		city = (Spinner) findViewById(R.id.city_spinner);
		application = TravelApplication.getInstance();
		cityNameMap = application.getCityNameMap();
		Set<String> cityNameSet = cityNameMap.keySet();
		list = new ArrayList<String>();
		int position = 0;
		int flag = 0;
		for(String cityName:cityNameSet)
		{
			list.add(cityName);			
			if(cityName.equals("���"))
			{
				flag = position;
			}
			position++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_layout_item,android.R.id.text1, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		city.setAdapter(adapter);
		//city.setSelection(flag);
		city.setOnItemSelectedListener(itemSelectedListener);
		moreButton = (ImageButton) findViewById(R.id.more);
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
		routeTipsButton = (ImageButton) findViewById(R.id.travel_commend);
		nearbyButton = (ImageButton) findViewById(R.id.nearby);
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
		routeTipsButton.setOnClickListener(this);
		moreButton.setOnClickListener(this);
		Intent intent = new Intent();
		intent.setAction(ConstantField.CHECK_NET);
		sendBroadcast(intent);
		openGPSSettings();
	}

	
	
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		ImageButton button = (ImageButton) v;
		
		switch (button.getId())
		{
		case R.id.more:
			Intent intent = new Intent();
			intent.setClass(IndexActivity.this, MoreActivity.class);	
			startActivity(intent);
			break;
		case R.id.scenery:	
			Log.d(TAG, "scenery");
			Intent sceneryIntent = new Intent();
			sceneryIntent.setClass(IndexActivity.this, SpotActivity.class);	
			startActivity(sceneryIntent);
			break;
		case R.id.hotel:
			Log.d(TAG, "hotel");
			Intent hotelIntent = new Intent();
			hotelIntent.setClass(IndexActivity.this, HotelActivity.class);		
			startActivity(hotelIntent);
			break;
		case R.id.restaurant:	
			Log.d(TAG, "restaurant");
			Intent restaurantIntent = new Intent();
			restaurantIntent.setClass(IndexActivity.this, RestaurantActivity.class);		
			startActivity(restaurantIntent);
			break;
		case R.id.shopping:	
			Log.d(TAG, "shopping");
			Intent shoppingIntent = new Intent();
			shoppingIntent.setClass(IndexActivity.this, ShoppingActivity.class);		
			startActivity(shoppingIntent);
			break;
		case R.id.entertainment:	
			Log.d(TAG, "shopping");
			Intent entertainmentIntent = new Intent();
			entertainmentIntent.setClass(IndexActivity.this, EntertainmentActivity.class);		
			startActivity(entertainmentIntent);
			break;
		case R.id.nearby:	
			Log.d(TAG, "nearby");
			Intent nearbyIntent = new Intent();
			nearbyIntent.setClass(IndexActivity.this, NearbyActivity.class);		
			startActivity(nearbyIntent);
			break;
		case R.id.city_base:
			Log.d(TAG, "city_base");
			application.setOverviewType(ConstantField.CITY_BASE);
			Intent cityBaseIntent = new Intent();
			cityBaseIntent.setClass(IndexActivity.this, OverviewActivity.class);		
			startActivity(cityBaseIntent);
			break;
		case R.id.travel_prepration:	
			Log.d(TAG, "travel_prepration");
			application.setOverviewType(ConstantField.TRAVEL_PREPRATION);
			Intent travelPreprationIntent = new Intent();
			travelPreprationIntent.setClass(IndexActivity.this, OverviewActivity.class);		
			startActivity(travelPreprationIntent);
			break;
		case R.id.travel_utility:	
			Log.d(TAG, "travel_utility");
			application.setOverviewType(ConstantField.TRAVEL_UTILITY);
			Intent travelUtilityIntent = new Intent();
			travelUtilityIntent.setClass(IndexActivity.this, OverviewActivity.class);		
			startActivity(travelUtilityIntent);
			break;
		case R.id.travel_transportation:	
			Log.d(TAG, "travel_transportation");
			application.setOverviewType(ConstantField.TRAVEL_TRANSPORTAION);
			Intent travelTransportationIntent = new Intent();
			travelTransportationIntent.setClass(IndexActivity.this, OverviewActivity.class);		
			startActivity(travelTransportationIntent);
			break;
		case R.id.travel_tips:	
			Log.d(TAG, "travel_tips");
			Intent travelTipsIntent = new Intent();
			travelTipsIntent.setClass(IndexActivity.this, TravelTipsActivity.class);		
			startActivity(travelTipsIntent);
			break;
		case R.id.travel_commend:	
			Log.d(TAG, "travel_commend");
			Intent travelCOmmendIntent = new Intent();
			travelCOmmendIntent.setClass(IndexActivity.this, EntertainmentActivity.class);		
			startActivity(travelCOmmendIntent);
			break;
		default:
			break;
		}
		
	}
	
	private OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener()
	{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			// TODO Auto-generated method stub
			String cityName = list.get(arg2);
			Integer cityID = cityNameMap.get(cityName);
			city.setSelection(arg2);
			Log.d(TAG, "cityID = "+cityID);
			application.setCityID(cityID);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0)
		{
			// TODO Auto-generated method stub
			
		}
	};
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		if(keyCode == event.KEYCODE_BACK)
		{
			AlertDialog leaveAlertDialog = new AlertDialog.Builder(IndexActivity.this).create();
			leaveAlertDialog.setMessage(getBaseContext().getString(R.string.leave_alert_dilaog));
			leaveAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getBaseContext().getString(R.string.exit),new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// TODO Auto-generated method stub
					MainService.exitAPP(IndexActivity.this);				
				}
			} );
			leaveAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+getBaseContext().getString(R.string.cancel),new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// TODO Auto-generated method stub
					dialog.cancel();
					
				}
			} );
			leaveAlertDialog.show();
		return true;	
		}else{
		return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
	}
	
	private void openGPSSettings() {

		LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
		//Toast.makeText(this, "GPSģ����", Toast.LENGTH_SHORT).show();
		return;
		}
	    Intent gpsIntent = new Intent();
	    gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	    gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
	    gpsIntent.setData(Uri.parse("custom:3"));
	    try {
	        PendingIntent.getBroadcast(IndexActivity.this, 0, gpsIntent, 0).send();
	    } catch (CanceledException e) {
	        e.printStackTrace();
	    }
		}



}
