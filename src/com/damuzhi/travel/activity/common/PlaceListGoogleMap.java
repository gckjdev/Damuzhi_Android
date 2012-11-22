package com.damuzhi.travel.activity.common;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
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
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.place_google_map);
		ActivityMange.getInstance().addActivity(this);
		mapView = (TapControlledMapView) findViewById(R.id.common_place_mapview);
		mapc = mapView.getController();
		mapc.setZoom(14);
		mapView.setStreetView(true);
		mapView.setOnSingleTapListener(onSingleTapListener);
		myLocateButton = (ImageView) findViewById(R.id.my_locate);
		canceLocateButton = (ImageView)findViewById(R.id.cancel_locate);
		myLocateButton.setOnClickListener(myLocateOnClickListener);
		canceLocateButton.setOnClickListener(cancelLocateOnClickListener);
		
		
		byte[] data = getIntent().getByteArrayExtra(ConstantField.PLACE_GOOGLE_MAP);
		if(data == null)
		{
			data = getIntent().getByteArrayExtra(ConstantField.NEARBY_GOOGLE_MAP);
			isNearbyGoogleMap = true;
		}
		try {
			placeList = PlaceList.parseFrom(data);
			Log.d(TAG, "place size = "+placeList.getListCount());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		location = TravelApplication.getInstance().getLocation();
		locationMager = new LocationMager(this);
		initMapView();
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
		openGPSSettings();	
		mapView.getOverlays().clear();
		mapView.removeAllViews();
		if(placeList.getListList()!= null&&placeList.getListList().size()>0)
		{
			initMapOverlayView(placeList.getListList());
		}
		if(isNearbyGoogleMap)
		{
			//getMyLocation();
			initMyLocationOverlayView(location);
		}
		
	}	
	
	
	
	
	
	private void initMapOverlayView(List<Place> placeList)
	{	
		List<Overlay> mapOverlays = mapView.getOverlays();
		GeoPoint geoPoint = null;
		CommonOverlayItem commonOverlayItem = null;
		Drawable markerIcon = null;
		if(isNearbyGoogleMap)
		{
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
			markerIcon = getResources().getDrawable(TravelUtil.getForecastImage(placeList.get(0).getCategoryId()));
			itemizedOverlay = new CommonItemizedOverlay<CommonOverlayItem>(markerIcon, mapView);
			for (Place place : placeList)
			{				
				geoPoint = new GeoPoint((int) (place.getLatitude() * 1e6),(int) (place.getLongitude() * 1e6));
				commonOverlayItem = new CommonOverlayItem(geoPoint,place.getName(), null,place,null);
				itemizedOverlay.addOverlay(commonOverlayItem);
				mapOverlays.add(itemizedOverlay);
			}
		}		
		if(itemizedOverlay.size()>0)
		{
			mapc.setCenter(itemizedOverlay.getCenter());
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
		byte[] data = newIntent.getByteArrayExtra(ConstantField.PLACE_GOOGLE_MAP);
		if(data == null)
		{
			data = newIntent.getByteArrayExtra(ConstantField.NEARBY_GOOGLE_MAP);
			isNearbyGoogleMap = true;
		}
		try {
			placeList = PlaceList.parseFrom(data);
			Log.d(TAG, "on newIntent place size = "+placeList.getListCount());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		initMapView();
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
