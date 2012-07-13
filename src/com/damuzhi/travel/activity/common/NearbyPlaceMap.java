package com.damuzhi.travel.activity.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.mapview.CommonItemizedOverlay;
import com.damuzhi.travel.activity.common.mapview.CommonOverlayItem;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.activity.place.CommonPlaceDetailActivity;
import com.damuzhi.travel.mission.more.BrowseHistoryMission;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.service.Task;
import com.damuzhi.travel.util.TravelUtil;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.Projection;
import com.google.protobuf.InvalidProtocolBufferException;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;

public class NearbyPlaceMap extends MapActivity
{

	private static final String TAG = "CommendPlaceMap";
	private static final int PLACE_LIST = 1;
	private static final String TARGET_PLACE_POSITION = "-1";
	private TapControlledMapView mapView;
	private View popupView;//
	private View targetPlaceView;
	private List<Place> nearbyPlaceList;
	private ImageView myLocateButton;
	private ImageView canceLocateButton;
	private CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay;
	long lasttime = -1;
    MapController mapc;
    private Place targetPlace;
    
	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.nearby_place_map);
		TravelApplication.getInstance().addActivity(this);
		try
		{
			targetPlace = Place.parseFrom(getIntent().getByteArrayExtra(ConstantField.PLACE_DETAIL));	
			mapView = (TapControlledMapView) findViewById(R.id.commendPlaceMap);
			mapc = mapView.getController();			
			mapView.setStreetView(true);
			mapView.setOnSingleTapListener(onSingleTapListener);
			mapc.setZoom(20);		
			myLocateButton = (ImageView) findViewById(R.id.my_locate);
			canceLocateButton = (ImageView)findViewById(R.id.cancel_locate);
			myLocateButton.setOnClickListener(myLocateOnClickListener);
			canceLocateButton.setOnClickListener(cancelLocateOnClickListener);
			MapInitAsynTask asynTask = new MapInitAsynTask();
			asynTask.execute();
		} catch (Exception e)
		{
			Log.e(TAG, "<CommendPlaceMap> get place data but catch exception = "+e.toString(),e);
		}
		
	}
	
	
	

	class MapInitAsynTask extends AsyncTask<Void, Void, List<Place>>
	{

		@Override
		protected List<Place> doInBackground(Void... params)
		{
			return PlaceMission.getInstance().getPlaceNearbyInDistance(targetPlace, 10f);
		}

		@Override
		protected void onPostExecute(List<Place> result)
		{
			super.onPostExecute(result);
			nearbyPlaceList = result;
			if(targetPlace!=null&&nearbyPlaceList.size()>0)
			{
				initMapView(targetPlace ,nearbyPlaceList);	
			}
			
		}
		
	}
	
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}
	
	private OnClickListener myLocateOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			HashMap<String, Double> location = TravelApplication.getInstance().getLocation();
			if (location != null&&location.size()>0)
			{
				GeoPoint geoPoint = new GeoPoint(
						(int) (location.get(ConstantField.LATITUDE) * 1E6),
						(int) (location.get(ConstantField.LONGITUDE) * 1E6));
				mapc.animateTo(geoPoint);	
				MyLocationOverlay myLocationOverlay = new MyLocationOverlay(NearbyPlaceMap.this,mapView);
				myLocationOverlay.enableMyLocation();
				mapView.getOverlays().add(myLocationOverlay);
				mapc.setCenter(geoPoint);
			}else
			{
				Toast.makeText(NearbyPlaceMap.this, getString(R.string.get_location_fail), Toast.LENGTH_LONG).show();
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
			MapInitAsynTask asynTask = new MapInitAsynTask();
			asynTask.execute();
		}
	};
	
	private OnSingleTapListener onSingleTapListener = new OnSingleTapListener() {		
		@Override
		public boolean onSingleTap(MotionEvent e) {
			if(itemizedOverlay.size()>0)
			{
				itemizedOverlay.hideAllBalloons();
			}		
			return true;
		}
	};
	
	
	
	private void initMapView(Place targetPlace ,List<Place> placeList)
	{
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable marker = getResources().getDrawable(R.drawable.locate_back1);
		marker.setBounds(0, 0,(int) ((marker.getIntrinsicWidth() / 2) * 1.5),(int) ((marker.getIntrinsicHeight() / 2) * 1.5));
		itemizedOverlay = new CommonItemizedOverlay<CommonOverlayItem>(marker, mapView);
		GeoPoint geoPoint = new GeoPoint((int) (targetPlace.getLatitude() * 1e6),(int) (targetPlace.getLongitude() * 1e6));
		CommonOverlayItem commonOverlayItem = new CommonOverlayItem(geoPoint,targetPlace.getName(), null,targetPlace);
		itemizedOverlay.addOverlay(commonOverlayItem);
		itemizedOverlay.onTap(0);
		mapOverlays.add(itemizedOverlay);
		for (Place place : placeList)
		{
			int icon = TravelUtil.getForecastImage(place.getCategoryId());
			Drawable markerDrawable = getResources().getDrawable(icon);			
			CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay2 = new CommonItemizedOverlay<CommonOverlayItem>(markerDrawable, mapView);				
			GeoPoint geoPoint2 = new GeoPoint((int)(place.getLatitude()*1e6),(int)(place.getLongitude()*1e6));	
			CommonOverlayItem commonOverlayItem2 = new CommonOverlayItem(geoPoint2,place.getName(), null,place);
			itemizedOverlay2.addOverlay(commonOverlayItem2);
			mapOverlays.add(itemizedOverlay2);
		}
		if(itemizedOverlay.size()>0)
		{			
			mapc.setCenter(itemizedOverlay.getCenter());
		}		
		mapOverlays.add(itemizedOverlay);
	}
	
}
