package com.damuzhi.travel.activity.common;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.damuzhi.travel.R;
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
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.google.protobuf.InvalidProtocolBufferException;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;

public class PlaceGoogleMap extends MapActivity {

	private static final String TAG = "PlaceGoogleMap";
	private TapControlledMapView mapView;
	private MapController mapc;
	private ImageView myLocateButton;
	private ImageView canceLocateButton;
	private CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay;
	private PlaceList placeList;
	private HashMap<String, Double> location;
	private boolean isNearbyGoogleMap = false;
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.place_google_map);
		//TravelApplication.getInstance().addActivity(this);
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
		
	}

	
	
	
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume()");
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
		initMapView();
		super.onResume();
		
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		placeList = null;
		LocationUtil.stop();
		ActivityMange.getInstance().finishActivity();
		super.onDestroy();
	}

	
	private void initMapView()
	{
		openGPSSettings();	
		mapView.getOverlays().clear();
		mapView.removeAllViews();
		List<Place> list = placeList.getListList();
		if(list!= null&&list.size()>0)
		{
			initMapOverlayView(list);
		}
		if(isNearbyGoogleMap)
		{
			getMyLocation();
			initMyLocationOverlayView(location);
		}
		
	}	
	
	
	
	
	
	private void initMapOverlayView(List<Place> placeList)
	{	
		Place arg = placeList.get(0);
		Drawable markerIcon = getResources().getDrawable(TravelUtil.getForecastImage(arg.getCategoryId()));
		itemizedOverlay = new CommonItemizedOverlay<CommonOverlayItem>(markerIcon, mapView);
		for (Place place : placeList)
		{
			GeoPoint geoPoint = new GeoPoint((int) (place.getLatitude() * 1e6),(int) (place.getLongitude() * 1e6));
			CommonOverlayItem commonOverlayItem = new CommonOverlayItem(geoPoint,place.getName(), null,place);
			itemizedOverlay.addOverlay(commonOverlayItem);
		}
		if(itemizedOverlay.size()>0)
		{
			mapc.setCenter(itemizedOverlay.getCenter());
		}
		List<Overlay> mapOverlays = mapView.getOverlays();		
		mapOverlays.add(itemizedOverlay);
	}
	
	
	private OnSingleTapListener onSingleTapListener = new OnSingleTapListener() {		
		@Override
		public boolean onSingleTap(MotionEvent e) {
			itemizedOverlay.hideAllBalloons();
			return true;
		}
	};
	
	private void initMyLocationOverlayView(HashMap<String, Double> location)
	{
		if (location != null&&location.size()>0)
		{
			GeoPoint geoPoint = new GeoPoint((int) (location.get(ConstantField.LATITUDE) * 1E6),(int) (location.get(ConstantField.LONGITUDE) * 1E6));
			Drawable drawable = getResources().getDrawable(R.drawable.my_location);
			CommonOverlayItem overlayItem = new CommonOverlayItem(geoPoint, "", "", null);
			CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay3 = new CommonItemizedOverlay<CommonOverlayItem>(drawable, mapView);
			itemizedOverlay3.addOverlay(overlayItem);
			itemizedOverlay3.setOnFocusChangeListener(onFocusChangeListener);
			mapView.getOverlays().add(itemizedOverlay3);
			mapc.animateTo(geoPoint);
		}else
		{
			Toast.makeText(PlaceGoogleMap.this, getString(R.string.get_location_fail), Toast.LENGTH_LONG).show();
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
					Toast.makeText(PlaceGoogleMap.this, getString(R.string.current_location), Toast.LENGTH_LONG).show();		
				}	
			}
			
		}
	};
	
	

	private void getMyLocation()
	{
		checkGPSisOpen();	
		LocationUtil.getLocation(PlaceGoogleMap.this);
		location = TravelApplication.getInstance().getLocation();
					
	}
	
	private OnClickListener myLocateOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			getMyLocation();		
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
				Toast.makeText(PlaceGoogleMap.this, getString(R.string.get_location_fail), Toast.LENGTH_LONG).show();
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
			initMapView();
			
		}
	};
	
	
	private boolean checkGPSisOpen() {
		LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		}
			Toast.makeText(this, getString(R.string.open_gps_tips3), Toast.LENGTH_SHORT).show();
			return false;
	}
	
	
	
	private void openGPSSettings() {
		LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return;
		}
			Toast.makeText(this, getString(R.string.open_gps_tips3), Toast.LENGTH_SHORT).show();
	}

	
}
