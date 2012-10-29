package com.damuzhi.travel.activity.entry;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.R.integer;
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
import com.damuzhi.travel.mission.more.DownloadMission;
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
	//private ImageButton moreButton;
	private ImageButton sceneryButton;
	private ImageButton hotelButton;
	private ImageButton restaurantButton;
	private ImageButton shoppingButton;
	private ImageButton entertainmentButton;
	private ImageButton nearbyButton;
	//private ImageButton helpButton;
	private ImageButton citybaseButton;
	private ImageButton travelPreprationButton;
	private ImageButton travelUtilityButton;
	private ImageButton travelTransportaionButton;
	private ImageButton travelTipsButton;
	//private ImageButton routeTipsButton;
	private ImageButton favoriteButton;
	//private ImageButton shareButton;
	//TextView currentCityName;
	private PopupWindow shareWindow;
	private static final String SHARE_CONFIG = "share_config";
	private static final  int SHARE_2_SINA = 1;
	private static final  int SHARE_2_QQ = 2;
	private final static int HEAP_SIZE = 8* 1024* 1024 ;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//TravelApplication.getInstance().addActivity(this);
		ActivityMange.getInstance().addActivity(this);
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.index);		
		MobclickAgent.updateOnlineConfig(this);
		VMRuntime.getRuntime().setMinimumHeapSize(HEAP_SIZE);
		
		//currentCityName = (TextView) findViewById(R.id.current_city_name);
		//ViewGroup currentCitygGroup = (ViewGroup) findViewById(R.id.current_group);	
		//currentCityName.setText(AppManager.getInstance().getCurrentCityName());
		//currentCitygGroup.setOnClickListener(currentGroupOnClickListener);
		//moreButton = (ImageButton) findViewById(R.id.more);
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
		//routeTipsButton = (ImageButton) findViewById(R.id.travel_commend);
		nearbyButton = (ImageButton) findViewById(R.id.nearby);
		//helpButton = (ImageButton) findViewById(R.id.help);
		favoriteButton = (ImageButton) findViewById(R.id.favorite);
		//shareButton = (ImageButton) findViewById(R.id.share);
		
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
		//routeTipsButton.setOnClickListener(this);
		//moreButton.setOnClickListener(this);
		//helpButton.setOnClickListener(helpOnClickListener);
		favoriteButton.setOnClickListener(favoriteOnClickListener);
		//shareButton.setOnClickListener(shareOnClickListener);		
		
		//checkData();
	}

	
	/*private void checkDataVersion()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(IndexActivity.this).create();
		alertDialog.setMessage(IndexActivity.this.getString(R.string.data_has_new_version));
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,IndexActivity.this.getString(R.string.update_now),new DialogInterface.OnClickListener()
		{					
			@Override
			public void onClick(DialogInterface dialog, int which)
			{	
				Intent intent = new Intent();
				intent.putExtra("updateData", 1);
				intent.setClass(IndexActivity.this, OpenCityActivity.class);
				startActivity(intent);
			}	
		} );
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+IndexActivity.this.getString(R.string.update_later),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		} );
		alertDialog.show();
	}*/
	
	
	@Override
	protected void onResume()
	{
		
		super.onResume();
		Log.d(TAG, "index activity onResume");
		/*String cityName = AppManager.getInstance().getCurrentCityName();
		if(cityName == null||cityName.equals(""))
		{
			int defaultCityId = AppManager.getInstance().getDefaulCityId();
			AppManager.getInstance().setCurrentCityId(defaultCityId);
			cityName = AppManager.getInstance().getCurrentCityName();
		}*/
		//currentCityName.setText(cityName);
		//checkData();
	}
	
	
	/*private void updateAppVersion()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(IndexActivity.this).create();
		alertDialog.setMessage(IndexActivity.this.getString(R.string.app_has_new_version));
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,IndexActivity.this.getString(R.string.update_now),new DialogInterface.OnClickListener()
		{					
			@Override
			public void onClick(DialogInterface dialog, int which)
			{	
				Uri uri = Uri.parse(MobclickAgent.getConfigParams(IndexActivity.this, ConstantField.U_MENG_DOWNLOAD_CONFIGURE));
				Log.d(TAG, "<updateAppVersion> uri = "+uri);
				if(uri!=null&&!uri.equals(""))
				{
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}			
			}	
		} );
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+IndexActivity.this.getString(R.string.update_later),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		} );
		alertDialog.show();
	}
	
	
	
	
	
	
	private void installData()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(IndexActivity.this).create();
		alertDialog.setMessage(IndexActivity.this.getString(R.string.install_data_unfinish));
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,IndexActivity.this.getString(R.string.install_now),new DialogInterface.OnClickListener()
		{					
			@Override
			public void onClick(DialogInterface dialog, int which)
			{	
				Intent intent = new Intent();
				intent.putExtra("updateData", 0);
				intent.setClass(IndexActivity.this, OpenCityActivity.class);
				startActivity(intent);
			}	
		} );
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+IndexActivity.this.getString(R.string.install_later),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		} );
		alertDialog.show();
	}
	*/
	
	@Override
	public void onClick(View v)
	{
		ImageButton button = (ImageButton) v;
		
		switch (button.getId())
		{
		/*case R.id.more:
			Intent intent = new Intent();
			intent.setClass(IndexActivity.this, MoreActivity.class);	
			startActivity(intent);
			break;*/
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
		/*case R.id.travel_commend:	
			LocationUtil.stop();
			Intent travelRoutesIntent = new Intent();
			travelRoutesIntent.setClass(IndexActivity.this, TravelRoutesActivity.class);		
			startActivity(travelRoutesIntent);
			break;*/
		default:
			break;
		}
		
	}
	
	
	
/*	private OnClickListener currentGroupOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			LocationUtil.stop();
			Intent intent = new Intent();
			intent.setClass(IndexActivity.this, OpenCityActivity.class);
			startActivity(intent);
		}
	};*/
	
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			AlertDialog leaveAlertDialog = new AlertDialog.Builder(IndexActivity.this).create();
			leaveAlertDialog.setMessage(getBaseContext().getString(R.string.leave_alert_dilaog));
			leaveAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getBaseContext().getString(R.string.exit),new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					AppMission.getInstance().saveCurrentCityId(IndexActivity.this);
					//TravelApplication.getInstance().exit();	
					ActivityManger.getInstance().AppExit(IndexActivity.this);
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
		return true;	
		}else{
		return super.onKeyDown(keyCode, event);
		}
	}*/



	
	
	
	/*private void checkData()
	{
		AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{
				
				float remoteVersion = MoreMission.getInstance().getNewVersion();
				float localVersion = TravelUtil.getVersionName(IndexActivity.this);
				Log.d(TAG, "app Version = "+localVersion);
				if(remoteVersion>localVersion)
				{
					Looper.prepare();
					updateAppVersion();
					Looper.loop();					
				}	
				City city = AppManager.getInstance().getCityByCityId(AppManager.getInstance().getCurrentCityId());
				String downloadURL =null;
				if(city != null &&city.hasDownloadURL())
				{	
					downloadURL = city.getDownloadURL();
					Map<Integer, Integer> unfinishInstallCity = DownloadPreference.getAllUnfinishInstall(IndexActivity.this);
					Map<Integer, Integer> installCityData = DownloadPreference.getAllDownloadInfo(IndexActivity.this);
					Map<Integer, String> newVersionCityData = TravelApplication.getInstance().getNewVersionCityData();
					List<Integer> installedCityList = new ArrayList<Integer>();
					installedCityList.clear();
					installedCityList.addAll(installCityData.keySet());
					if(installCityData != null&&installCityData.size()>0)
					{
						if(newVersionCityData == null)
						{
							newVersionCityData = DownloadMission.getInstance().getNewVersionCityData(installedCityList);
							TravelApplication.getInstance().setNewVersionCityData(newVersionCityData);
						}
						
					}
					int currentCityId = AppManager.getInstance().getCurrentCityId();
					if(downloadURL != null&&!downloadURL.equals(""))
					{
						if(newVersionCityData!= null&&newVersionCityData.containsKey(currentCityId)&&!DownloadService.downloadStstudTask.containsKey(downloadURL))
						{
							Looper.prepare();
							checkDataVersion();
							Looper.loop();
						}
						if(unfinishInstallCity.containsKey(currentCityId)&&!DownloadService.downloadStstudTask.containsKey(downloadURL))
						{
							Looper.prepare();
							installData();
							Looper.loop();
						}
					}
				}
				return null;
			}

		
	
		};
		asyncTask.execute();
	}*/
	

	
	/*
	private OnClickListener helpOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent  intent = new Intent();
			intent.putExtra(ConstantField.HELP_TITLE, getResources().getString(R.string.help));
			intent.setClass(IndexActivity.this, HelpActiviy.class);
			startActivity(intent);
			
		}
	};*/
	
	
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


	
/*	private OnClickListener shareOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			shareWindow(v);
			
		}
	};
	
	private void shareWindow(View parent)
	{
		//LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		LayoutInflater lay = getLayoutInflater();
        View v = lay.inflate(R.layout.share_popup, null);        
        Button shareByMessageButton = (Button) v.findViewById(R.id.share_by_message_btn);
        Button share2sinaButton = (Button) v.findViewById(R.id.share_2_sina_btn);
        Button share2qqButton = (Button) v.findViewById(R.id.share_2_qq_btn);
        Button shareCancelButton = (Button) v.findViewById(R.id.share_cancel);
        LinearLayout shareGroup = (LinearLayout) v.findViewById(R.id.share_view_group);        
        shareByMessageButton.setOnClickListener(shareByMessage);
        share2sinaButton.setOnClickListener(share2sinaWeiboOnClickListener);
        share2qqButton.setOnClickListener(share2qqWeiboOnClickListener);
        shareCancelButton.setOnClickListener(shareCancelOnClickListener);
        shareWindow = new PopupWindow(v, android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT);   
        shareWindow.setFocusable(true);  
        shareWindow.update();  
      //  shareWindow.showAtLocation(findViewById(R.id.share), Gravity.CENTER, 0, 0);  
        shareGroup.setOnKeyListener(new OnKeyListener()
		{
					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event)
					{
						if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK)
							shareWindow.dismiss();
						return false;
					}
        		 
        		});
	}
	
	
	private OnClickListener shareByMessage = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
            String messageCont = getString(R.string.share_content);
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
            intent.putExtra("sms_body", messageCont);
            startActivity(intent);
		}
	};
	
	private OnClickListener share2sinaWeiboOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			intent.putExtra(SHARE_CONFIG, SHARE_2_SINA);
			intent.setClass(IndexActivity.this, Share2Weibo.class);
			startActivity(intent);
		}
	};
	
	
	private OnClickListener share2qqWeiboOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			intent.putExtra(SHARE_CONFIG, SHARE_2_QQ);
			intent.setClass(IndexActivity.this, Share2Weibo.class);
			startActivity(intent);
		}
	};
	
	
	private OnClickListener shareCancelOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			if(shareWindow !=null)
			{
				shareWindow.dismiss();
			}
		}
	};*/

	
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
