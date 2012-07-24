/**  
        * @title NearbyPlaceActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-12 下午12:48:32  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.place;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.NearbyPlaceAdapter;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.mapview.CommonItemizedOverlay;
import com.damuzhi.travel.activity.common.mapview.CommonOverlayItem;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.service.Task;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.TravelUtil.ComparatorDistance;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.MapView.LayoutParams;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;



public class CommonNearbyPlaceActivity extends TravelActivity
{
	private static final String TAG = "Nearby";
	private ImageButton startButton;
	private ImageButton oneKMbutton;
	private ImageButton fiveKMButtom;
	private ImageButton tenKMButton;
	private ImageView redStart;
	private int startPosition = 1;
	private float offset;
	private int bmpW;//
	private int screenW;
	private TextView allPlace;
	private TextView spot;
	private TextView hotel;
	private TextView restaurant;
	private TextView shopping;
	private TextView entertrainment;
	private TextView move;
	private int tabStartPosition = 0;
	private ListView listView;
	private int startLeft = 0; 
	private ProgressDialog loadingDialog;
	private List<Place> placeList = Collections.emptyList();
	private HashMap<String, Double> location;
	private NearbyPlaceAdapter adapter;
	private TapControlledMapView mapView;
	private View popupView;//
	long lasttime = -1;
    MapController mapc;
    private TravelApplication application;
    private ImageButton selectMapViewButton;
    private ImageButton selectListViewButton;
    private ImageView myLocateButton ;
	private ImageView canceLocateButton;
   // private PlaceLoaction placeLoaction;
    private CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay;
    private String currentDistance ="";
    private String currentPlaceCategory = ConstantField.NEARBY_PLACE_LIST_IN_DISTANCE;
    private LocationClient mLocClient;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby_place);
		currentDistance = ConstantField.ONE_KILOMETER;
		currentPlaceCategory = ConstantField.NEARBY_PLACE_LIST_IN_DISTANCE;
		boolean gpsEnable = checkGPSisOpen();
		loadingDialog = new ProgressDialog(this);
		init();
		loadPlace();		
		
	}
	
	public void init()
	{
		listView = (ListView) findViewById(R.id.nearby_list);
		mapView = (TapControlledMapView) findViewById(R.id.placeMap);
		mapc = mapView.getController();
		mapc.setZoom(18);
		mapView.setStreetView(true);
		mapView.setOnSingleTapListener(onSingleTapListener);
		selectMapViewButton = (ImageButton) findViewById(R.id.map_view);
		selectListViewButton = (ImageButton)findViewById(R.id.list_view);
		
		myLocateButton = (ImageView) findViewById(R.id.my_locate);
		canceLocateButton = (ImageView)findViewById(R.id.cancel_locate);
		myLocateButton.setOnClickListener(myLocateOnClickListener);
		canceLocateButton.setOnClickListener(cancelLocateOnClickListener);
		
		selectListViewButton.setOnClickListener(selectListViewOnClickListener);
		selectMapViewButton.setOnClickListener(selectMapViewOncClickListener);
		listView.setOnItemClickListener(listviewOnItemClickListener);
		
		
		redStart = (ImageView) findViewById(R.id.position);
		startButton = (ImageButton) findViewById(R.id.start_button);
		oneKMbutton = (ImageButton) findViewById(R.id.one_km);
		fiveKMButtom = (ImageButton) findViewById(R.id.five_km);
		tenKMButton = (ImageButton) findViewById(R.id.ten_km);	
		
		startButton.setOnClickListener(fiveOnClickListener);
		fiveKMButtom.setOnClickListener(fiveKmOnClickListener);
		oneKMbutton.setOnClickListener(oneKmOnClickListener);
		tenKMButton.setOnClickListener(tenKmOnClickListener);
		
		
		
		allPlace = (TextView) findViewById(R.id.place_all);
		spot = (TextView) findViewById(R.id.place_spot);
		hotel = (TextView) findViewById(R.id.place_hotel);
		restaurant = (TextView) findViewById(R.id.place_restaurant);
		shopping = (TextView) findViewById(R.id.place_shopping);
		entertrainment = (TextView) findViewById(R.id.place_entertrainment);	
		
		allPlace.setOnClickListener(allPlaceOnClickListener);
		spot.setOnClickListener(spotOnClickListener);
		hotel.setOnClickListener(hotelOnClickListener);
		restaurant.setOnClickListener(restaurantOnClickListener);
		shopping.setOnClickListener(shoppingOnClickListener);
		entertrainment.setOnClickListener(entertrainmnetOnClickListener);			
		move = (TextView) findViewById(R.id.move);
		move.setTextColor(getResources().getColor(R.color.white));
		
		popupView = LayoutInflater.from(this).inflate(R.layout.overlay_popup, null);
		application= TravelApplication.getInstance();	
		adapter = new NearbyPlaceAdapter(this, placeList);
		listView.setAdapter(adapter);
	}

	
	
	
	public void loadPlace()
	{
		AsyncTask<String, Void, List<Place>> task = new AsyncTask<String, Void, List<Place>>()
		{

			@Override
			protected List<Place> doInBackground(String... params)
			{
				location = TravelApplication.getInstance().getLocation();
				if(TravelApplication.getInstance().mLocationClient !=null)
				{
					TravelApplication.getInstance().mLocationClient.stop();
				}
				return PlaceMission.getInstance().getPlaceNearbyInDistance(location, currentDistance,currentPlaceCategory);
			}

			@Override
			protected void onCancelled()
			{
				super.onCancelled();
			}

			@Override
			protected void onPostExecute(List<Place> resultList)
			{
				loadingDialog.dismiss();
				placeList = resultList;	
				refreshPlaceView(placeList);
				if(placeList.size()>0)
				{
					findViewById(R.id.page).setVisibility(View.VISIBLE);					
				}else
				{
					findViewById(R.id.data_not_found).setVisibility(View.VISIBLE);
				}
				super.onPostExecute(resultList);
			}

			@Override
			protected void onPreExecute()
			{
				showRoundProcessDialog();
				super.onPreExecute();
			}

		};

		task.execute();
		
	}
	
	private void refreshPlaceView(List<Place> list)
	{
		List<Place> origList = new ArrayList<Place>();
		origList.addAll(list);
		ComparatorDistance comparatorDistance = new ComparatorDistance(location);
		Collections.sort(origList, comparatorDistance);
		if(listView.getVisibility() == View.VISIBLE)
		{
			adapter.setList(origList);
			adapter.notifyDataSetChanged();
		}else
		{	
			mapView.getOverlays().clear();
			if(origList.size()!=0)
			{
				initMapOverlayView(origList);
			}
			mapView.postInvalidate();						
		}
		updateTitle();
	}
	
	
	
	private void updateTitle()
	{
		TextView placeSize = (TextView) findViewById(R.id.place_num);
		int size = placeList.size();
		String sizeString = "(" + size + ")";
		placeSize.setText(sizeString);
	}
	
	
	
	private OnSingleTapListener onSingleTapListener = new OnSingleTapListener() {		
		@Override
		public boolean onSingleTap(MotionEvent e) {
			itemizedOverlay.hideAllBalloons();
			return true;
		}
	};

	 
	 private OnClickListener fiveOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Animation animation = null;
			currentDistance = ConstantField.HALF_KILOMETER;		
			getOffSet(redStart,startPosition);
			float endSet = screenW*-0.18f;
			if(startPosition>0)
			{
				offset = offset - screenW*0.18f;
			}
			startPosition = 0;			
			animation = new TranslateAnimation(offset,endSet, 0, 0);
			animation.setDuration(500);		
			redStart.startAnimation(animation);
			animation.setFillAfter(true);
			loadPlace();

		}
	};
	
	 private OnClickListener oneKmOnClickListener = new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Animation animation = null;
				currentDistance = ConstantField.ONE_KILOMETER;				
				getOffSet(redStart,startPosition);
				float endSet = screenW*0f;		
				if(startPosition>1)
				{
					offset = offset - screenW*0.18f;
				}
				startPosition = 1;				
				animation = new TranslateAnimation(offset,endSet, 0, 0);
				animation.setDuration(500);		
				redStart.startAnimation(animation);
				animation.setFillAfter(true);				
				loadPlace();
				
			}
		};
	 
		
		 private OnClickListener fiveKmOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					Animation animation = null;
					currentDistance = ConstantField.FIVE_KILOMETER;					
					getOffSet(redStart,startPosition);
					float endSet = screenW*0.25f;	
					if(startPosition>1)
					{
						offset = offset - screenW*0.18f;
					}
					startPosition = 2;					
					animation = new TranslateAnimation(offset,endSet, 0, 0);
					animation.setDuration(500);		
					redStart.startAnimation(animation);
					animation.setFillAfter(true);
					loadPlace();	
				}
			};
			
			
			
		 private OnClickListener tenKmOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					Animation animation = null;
					currentDistance = ConstantField.TEN_KILOMETER;
					getOffSet(redStart,startPosition);
					float endSet = screenW*0.64f;
					if(startPosition>3)
					{
						offset = offset - screenW*0.18f;
					}
					startPosition = 3;
					animation = new TranslateAnimation(offset,endSet, 0, 0);
					animation.setDuration(500);		
					redStart.startAnimation(animation);
					animation.setFillAfter(true);
					loadPlace();	
				}
			};
				
				
		 private OnClickListener allPlaceOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					currentPlaceCategory = ConstantField.NEARBY_PLACE_LIST_IN_DISTANCE;
					loadPlace();
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*0;
					move.setText("");
					tabStartPosition = 11;
					TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
					animation.setDuration(300);
					animation.setFillAfter(true);
					move.bringToFront();
					move.startAnimation(animation);					
					move.setText(setEndPosition(startPosition));
				}
			};
			
			private OnClickListener spotOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					currentPlaceCategory = ConstantField.NEARBY_SPOT_LIST_IN_DISTANCE;
					loadPlace();
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*1;
					move.setText("");
					tabStartPosition = 12;
					TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
					animation.setDuration(300);
					animation.setFillAfter(true);
					move.bringToFront();
					move.startAnimation(animation);					
					move.setText(setEndPosition(startPosition));
						
				}
			};
			
			private OnClickListener hotelOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					currentPlaceCategory = ConstantField.NEARBY_HOTEL_LIST_IN_DISTANCE;
					loadPlace();
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*2;
					move.setText("");
					tabStartPosition = 13;
					TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
					animation.setDuration(300);
					animation.setFillAfter(true);
					move.bringToFront();
					move.startAnimation(animation);					
					move.setText(setEndPosition(startPosition));
						
				}
			};
			
			private OnClickListener restaurantOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					currentPlaceCategory = ConstantField.NEARBY_RESTAURANT_LIST_IN_DISTANCE;
					loadPlace();
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*3;
					move.setText("");
					tabStartPosition = 14;
					TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
					animation.setDuration(300);
					animation.setFillAfter(true);
					move.bringToFront();
					move.startAnimation(animation);					
					move.setText(setEndPosition(startPosition));
						
				}
			};
			
			
			private OnClickListener shoppingOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					currentPlaceCategory = ConstantField.NEARBY_SHOPPING_LIST_IN_DISTANCE;
					loadPlace();
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*4;
					move.setText("");
					tabStartPosition = 15;
					TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
					animation.setDuration(300);
					animation.setFillAfter(true);
					move.bringToFront();
					move.startAnimation(animation);					
					move.setText(setEndPosition(startPosition));
						
				}
			};
			
			
			private OnClickListener entertrainmnetOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					currentPlaceCategory = ConstantField.NEARBY_ENTERTRAINMENT_LIST_IN_DISTANCE;
					loadPlace();
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*5;
					move.setText("");
					tabStartPosition = 16;
					TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
					animation.setDuration(300);
					animation.setFillAfter(true);
					move.bringToFront();
					move.startAnimation(animation);					
					move.setText(setEndPosition(startPosition));
						
				}
			};
	 
		
			
	private OnClickListener selectListViewOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			mapView.setVisibility(View.GONE);
			selectListViewButton.setVisibility(View.GONE);
			selectMapViewButton.setVisibility(View.VISIBLE);
			listView.setVisibility(View.VISIBLE);					
			adapter.setList(placeList);
			adapter.notifyDataSetChanged();	
			
		}
	};		
	
	
	private OnClickListener selectMapViewOncClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			listView.setVisibility(View.GONE);
			selectMapViewButton.setVisibility(View.GONE);		
			findViewById(R.id.mapview_group).setVisibility(View.VISIBLE);
			mapView.setVisibility(View.VISIBLE);
			selectListViewButton.setVisibility(View.VISIBLE);
			initMapOverlayView(placeList);
		}
	};

	
	private OnItemClickListener listviewOnItemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			List<Place> list = adapter.getPlaceList();
			Place place = list.get(arg2);
			Intent intent = new Intent();
			intent.putExtra(ConstantField.PLACE_DETAIL, place.toByteArray());
			Class detailPlaceClass = CommonPlaceDetailActivity.getClassByPlaceType(place.getCategoryId());
			intent.setClass(CommonNearbyPlaceActivity.this, detailPlaceClass);
			startActivity(intent);
			
		}
	};
			
	
	
	private OnClickListener myLocateOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			boolean gpsEnable = checkGPSisOpen();
			if(location == null || location.size()==0)
			{
				getLocation(CommonNearbyPlaceActivity.this);
				location = TravelApplication.getInstance().getLocation();
			}
			String address = TravelApplication.getInstance().address;
			if (address == null||address.equals(""))
			{
				location = null;
				Toast.makeText(CommonNearbyPlaceActivity.this, getString(R.string.get_location_ing), Toast.LENGTH_LONG).show();
				return;
			}
			if(mLocClient !=null)
			{
				mLocClient.stop();
			}				
			if (location != null&&location.size()>0)
			{
				GeoPoint geoPoint = new GeoPoint((int) (location.get(ConstantField.LATITUDE) * 1E6),(int) (location.get(ConstantField.LONGITUDE) * 1E6));
				Drawable drawable = getResources().getDrawable(R.drawable.my_location);
				CommonOverlayItem overlayItem = new CommonOverlayItem(geoPoint, "", "", null);
				CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay3 = new CommonItemizedOverlay<CommonOverlayItem>(drawable, mapView);
				itemizedOverlay3.addOverlay(overlayItem);
				mapView.getOverlays().add(itemizedOverlay3);
				mapc.animateTo(geoPoint);
			}else
			{
				Toast.makeText(CommonNearbyPlaceActivity.this, getString(R.string.get_location_fail), Toast.LENGTH_LONG).show();
			}
			myLocateButton.setVisibility(View.GONE);
			canceLocateButton.setVisibility(View.VISIBLE);
		}
	};
	
	private OnClickListener cancelLocateOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			canceLocateButton.setVisibility(View.GONE);
			myLocateButton.setVisibility(View.VISIBLE);
			initMapOverlayView(placeList);
		}
	};
	
	private void getOffSet(ImageView imageView,int startPosition) {
	
	DisplayMetrics dm = new DisplayMetrics();
	getWindowManager().getDefaultDisplay().getMetrics(dm);
	screenW = dm.widthPixels;
	switch (startPosition)
	{
	case 0:
		offset = screenW*0f;
		break;
	case 1:
		offset = screenW*0.18f;
		break;
	case 2:
		offset = screenW*0.43f;
		break;
	case 3:
		offset = screenW*0.82f;
		break;
	default:
		break;
	}
	Matrix matrix = new Matrix();
	matrix.postTranslate(offset, 0);
	imageView.setImageMatrix(matrix);
	}
	
	
	private void getStartPosition(int tabStartPosition) {
	switch (tabStartPosition)
	{
	case 11:
		startLeft = allPlace.getWidth()*0;
		break;
	case 12:
		startLeft = allPlace.getWidth()*1;
		break;
	case 13:
		startLeft = allPlace.getWidth()*2;
		break;
	case 14:
		startLeft = allPlace.getWidth()*3;
		break;
	case 15:
		startLeft = allPlace.getWidth()*4;
		break;
	case 16:
		startLeft = allPlace.getWidth()*5;
		break;
	default:
		break;
	}
	}
	
	
	private String setEndPosition(int tabStatPosition) {
		String text = "";
		switch (tabStartPosition)
		{
		case 11:
			text = getString(R.string.all_place);
			break;
		case 12:
			text = getString(R.string.scenery);
			break;
		case 13:
			text = getString(R.string.hotel);
			break;
		case 14:
			text = getString(R.string.restaurant);
			break;
		case 15:
			text = getString(R.string.shopping);
			break;
		case 16:
			text = getString(R.string.entertainment);
			break;
		default:
			break;
		}
		return text;
	}
	
	
	public void showRoundProcessDialog()
	{

		OnKeyListener keyListener = new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getRepeatCount() == 0)
				{
					loadingDialog.dismiss();
					Intent intent = new Intent(CommonNearbyPlaceActivity.this,IndexActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					return true;
				} else
				{
					return false;
				}
			}
		};

		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingDialog.setMessage(getResources().getString(R.string.loading));
		loadingDialog.setIndeterminate(false);
		loadingDialog.setCancelable(true);
		loadingDialog.setOnKeyListener(keyListener);
		loadingDialog.show();
	}
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())	
		{		
		case R.id.menu_refresh:
			loadPlace();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private boolean checkGPSisOpen() {
		LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		}
			Toast.makeText(this, getString(R.string.open_gps_tips2), Toast.LENGTH_SHORT).show();
			return false;
	}
	
	
	private void initMapOverlayView(List<Place> placeList)
	{	
		List<Overlay> mapOverlays = mapView.getOverlays();
		int i =0;
		for (Place place : placeList)
		{
			int icon = TravelUtil.getForecastImage(place.getCategoryId());
			Drawable markerDrawable = getResources().getDrawable(icon);			
			itemizedOverlay = new CommonItemizedOverlay<CommonOverlayItem>(markerDrawable, mapView);				
			GeoPoint geoPoint2 = new GeoPoint((int)(place.getLatitude()*1e6),(int)(place.getLongitude()*1e6));	
			CommonOverlayItem commonOverlayItem2 = new CommonOverlayItem(geoPoint2,place.getName(), null,place);
			itemizedOverlay.addOverlay(commonOverlayItem2);
			mapOverlays.add(itemizedOverlay);
			if(i== 0)
			{
				mapc.setCenter(itemizedOverlay.getCenter());
				i++;
			}
			
		}
	}
	
	public  void getLocation(Context context)
	{
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);				
		option.setCoorType("bd09ll");		
		option.setScanSpan(10000);
		mLocClient = TravelApplication.getInstance().mLocationClient;
		mLocClient.setLocOption(option);
		mLocClient.start();
		if (mLocClient != null && mLocClient.isStarted())
			mLocClient.requestLocation();
		else 
			Log.d(TAG, " baidu locationSDK locClient is null or not started");
	}


	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}
