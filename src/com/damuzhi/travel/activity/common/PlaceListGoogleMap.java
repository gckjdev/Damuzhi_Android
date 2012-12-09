package com.damuzhi.travel.activity.common;

import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.location.LocationMager;
import com.damuzhi.travel.activity.common.location.LocationUtil;
import com.damuzhi.travel.activity.common.mapview.CommonItemizedOverlay;
import com.damuzhi.travel.activity.common.mapview.CommonOverlayItem;
import com.damuzhi.travel.activity.entry.MainActivity;
import com.damuzhi.travel.activity.place.CommonNearbyPlaceActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;
import com.damuzhi.travel.util.TravelUtil;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.google.protobuf.InvalidProtocolBufferException;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;

public class PlaceListGoogleMap extends MapActivity {

	private static final String TAG = "PlaceGoogleMap";
	private TapControlledMapView mapView;
	private MapController mapc;
	private ImageView myLocateButton;
	private ImageView canceLocateButton;
	private CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay;
	private PlaceList placeList;
	private HashMap<String, Double> location;
	private boolean isNearbyGoogleMap = false;
	private PopupWindow locationPopupWindow;
	private View locationDialogView;
	private LocationMager locationMager;
	private boolean isGetMyLocation = false;
	private ProgressDialog loadingDialog ;
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Log.d(TAG, "google mapview creat ing... ");
		setContentView(R.layout.place_google_map);
		ActivityMange.getInstance().addActivity(this);
		mapView = (TapControlledMapView) findViewById(R.id.common_place_mapview);
		mapView.setReticleDrawMode(MapView.ReticleDrawMode.DRAW_RETICLE_OVER);
		mapView.setBuiltInZoomControls(true);
		mapView.setSelected(true);
		mapView.preLoad();
		mapc = mapView.getController();
		mapc.setZoom(17);
		mapView.setStreetView(true);
		mapView.setOnSingleTapListener(onSingleTapListener);
		myLocateButton = (ImageView) findViewById(R.id.my_locate);
		canceLocateButton = (ImageView)findViewById(R.id.cancel_locate);
		myLocateButton.setOnClickListener(myLocateOnClickListener);
		canceLocateButton.setOnClickListener(cancelLocateOnClickListener);
		showRoundProcessDialog();
		location = TravelApplication.getInstance().getLocation();
		locationMager = new LocationMager(this);
		loadData(getIntent());
	}

	private void loadData(Intent intent)
	{
		final Intent dataIntent = intent;
		AsyncTask<Void, Void, PlaceList> asyncTask = new AsyncTask<Void, Void, PlaceList>(){

			@Override
			protected PlaceList doInBackground(Void... params)
			{
				PlaceList list = null;
				byte[] data = dataIntent.getByteArrayExtra(ConstantField.PLACE_GOOGLE_MAP);
				if(data == null)
				{
					data = dataIntent.getByteArrayExtra(ConstantField.NEARBY_GOOGLE_MAP);
					isNearbyGoogleMap = true;
				}
				try {
					list = PlaceList.parseFrom(data);
				} catch (InvalidProtocolBufferException e) {
					e.printStackTrace();
				}
				
				return list;
			}

			@Override
			protected void onPostExecute(PlaceList result)
			{
				super.onPostExecute(result);
				placeList = result;
				Log.d(TAG, "place data  size = "+placeList.getListCount());
				initMapView();
				loadingDialog.dismiss();			
			}

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				loadingDialog.show();
				
			}
			
		};
		asyncTask.execute();
	}
	
	

	
	public void showRoundProcessDialog()
	{
		loadingDialog = new ProgressDialog(this);
		OnKeyListener keyListener = new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
				{
					loadingDialog.dismiss();
					Intent intent = new Intent(PlaceListGoogleMap.this,MainActivity.class);
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
		loadingDialog.setCancelable(false);
		loadingDialog.setOnKeyListener(keyListener);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		if(locationPopupWindow!=null&&locationPopupWindow.isShowing())
		{
			locationPopupWindow.dismiss();
		}
		placeList = null;
		locationMager.destroyMyLocation();
		ActivityMange.getInstance().finishActivity();
		super.onDestroy();
	}

	
	private void initMapView()
	{
		Log.d(TAG, "google mapview init ing....");
		openGPSSettings();	
		mapView.getOverlays().clear();
		mapView.removeAllViews();
		if(placeList.getListList()!= null&&placeList.getListList().size()>0)
		{
			initMapOverlayView(placeList.getListList());
		}
		if(isNearbyGoogleMap)
		{
			initMyLocationOverlayView(location);
		}
		Log.d(TAG, "google mapview init finish");
		MotionEvent motionEvent = MotionEvent.obtain(3000,1000,MotionEvent.ACTION_DOWN,200,200,0);
		mapView.onTouchEvent(motionEvent);
	}	
	
	
	
	
	
	private void initMapOverlayView(List<Place> placeList)
	{	
		List<Overlay> mapOverlays = mapView.getOverlays();
		GeoPoint geoPoint = null;
		CommonOverlayItem commonOverlayItem = null;
		Drawable markerIcon = null;
		if(isNearbyGoogleMap)
		{
			Log.d(TAG, "init nearby place google map ");
			for (Place place : placeList)
			{
				markerIcon = getResources().getDrawable(TravelUtil.getForecastImage(place.getCategoryId()));
				itemizedOverlay = new CommonItemizedOverlay<CommonOverlayItem>(markerIcon, mapView);
				geoPoint = new GeoPoint((int) (place.getLatitude() * 1e6),(int) (place.getLongitude() * 1e6));
				commonOverlayItem = new CommonOverlayItem(geoPoint,place.getName(), null,place,null);
				itemizedOverlay.addOverlay(commonOverlayItem);
				mapOverlays.add(itemizedOverlay);
			}
		}else
		{
			Log.d(TAG, "init place list google map");
			markerIcon = getResources().getDrawable(TravelUtil.getForecastImage(placeList.get(0).getCategoryId()));
			itemizedOverlay = new CommonItemizedOverlay<CommonOverlayItem>(markerIcon, mapView);
			for (Place place : placeList)
			{				
				geoPoint = new GeoPoint((int) (place.getLatitude() * 1e6),(int) (place.getLongitude() * 1e6));
				commonOverlayItem = new CommonOverlayItem(geoPoint,place.getName(), null,place,null);
				itemizedOverlay.addOverlay(commonOverlayItem);
				mapOverlays.add(itemizedOverlay);
			}
			if(itemizedOverlay.size()>0)
			{
				mapc.setCenter(itemizedOverlay.getCenter());
			}	
		}			
		
	}
	
	
	private OnSingleTapListener onSingleTapListener = new OnSingleTapListener() {		
		@Override
		public boolean onSingleTap(MotionEvent e) {
			itemizedOverlay.hideAllBalloons();
			return true;
		}
	};
	CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay3;
	private void initMyLocationOverlayView(HashMap<String, Double> location)
	{
		if (location != null&&location.size()>0)
		{
			GeoPoint geoPoint = new GeoPoint((int) (location.get(ConstantField.LATITUDE) * 1E6),(int) (location.get(ConstantField.LONGITUDE) * 1E6));
			Drawable drawable = getResources().getDrawable(R.drawable.my_location);
			CommonOverlayItem overlayItem = new CommonOverlayItem(geoPoint, "", "", null,null);
			itemizedOverlay3 = new CommonItemizedOverlay<CommonOverlayItem>(drawable, mapView);
			itemizedOverlay3.addOverlay(overlayItem);
			itemizedOverlay3.setOnFocusChangeListener(onFocusChangeListener);
			mapView.getOverlays().add(itemizedOverlay3);
			mapc.animateTo(geoPoint);
		}else
		{
			Toast.makeText(PlaceListGoogleMap.this, getString(R.string.get_location_fail), Toast.LENGTH_LONG).show();
		}
	}
	
	
	
	
	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener()
	{

		
		@Override
		public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus)
		{
			if(newFocus != null)
			{
				if(newFocus.getSnippet().equals("") &&newFocus.getTitle().equals(""))
				{
					Toast.makeText(PlaceListGoogleMap.this, getString(R.string.current_location), Toast.LENGTH_LONG).show();		
				}	
			}
			
		}
	};
	

	private void getMyLocation(View view)
	{
		checkGPSisOpen();	
		alertWindow(view);
		isGetMyLocation = true;
		locationMager.getLocation(handler);			
	}
	
	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case 1:
				if(msg.obj!=null&&isGetMyLocation){
					isGetMyLocation = false;
					location = (HashMap<String, Double>) msg.obj;
					if(locationPopupWindow!=null&&locationPopupWindow.isShowing())
					{
						locationPopupWindow.dismiss();
					}
					if (location != null&&location.size()>0)
					{
						initMyLocationOverlayView(location);
					}else
					{
						Toast.makeText(PlaceListGoogleMap.this, getString(R.string.get_location_fail), Toast.LENGTH_SHORT).show();
					}
				}
				break;

			default:
				break;
			}
		}
		
	};
	
	
	
	private OnClickListener myLocateOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			if(isNearbyGoogleMap){
				mapc.animateTo(itemizedOverlay3.getCenter());
			}else{
				if(location==null||location.size()==0){
					getMyLocation(v);
				}else {
					initMyLocationOverlayView(location);
				}
						
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
			if(!isNearbyGoogleMap){
				isGetMyLocation = false;
				if(itemizedOverlay3!=null&&itemizedOverlay3.size()>0){
					mapView.getOverlays().remove(itemizedOverlay3);
				}
			}		
			canceLocateButton.setVisibility(View.GONE);
			myLocateButton.setVisibility(View.VISIBLE);
			mapc.animateTo(itemizedOverlay.getCenter());			
		}
	};
	
	
	private boolean checkGPSisOpen() {
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		}
			Toast.makeText(this, getString(R.string.open_gps_tips3), Toast.LENGTH_SHORT).show();
			return false;
	}
	
	
	
	private void openGPSSettings() {
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return;
		}
			Toast.makeText(this, getString(R.string.open_gps_tips3), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNewIntent(Intent newIntent)
	{
		super.onNewIntent(newIntent);
		Log.d(TAG, "onNewIntent");
		loadData(newIntent);
	}

	
	
	
	
	private void alertWindow(View view)
	{
		locationDialogView = getLayoutInflater().inflate(R.layout.location_popupwindow, null);
		locationPopupWindow = new PopupWindow(locationDialogView, 200,100);
		//ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.transparent));
		locationPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.gray_bg));
		locationPopupWindow.setOutsideTouchable(false);
		locationPopupWindow.update();		
		locationPopupWindow.showAtLocation(view, Gravity.CENTER,0, 0);
	}

	
	
	
	@Override
	protected void onPause()
	{
		super.onPause();
		locationMager.disableMyLocation();
	}
	
	
}
