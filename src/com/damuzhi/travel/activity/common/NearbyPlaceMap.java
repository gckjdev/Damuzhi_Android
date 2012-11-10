package com.damuzhi.travel.activity.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.location.LocationUtil;
import com.damuzhi.travel.activity.common.mapview.CommonItemizedOverlay;
import com.damuzhi.travel.activity.common.mapview.CommonOverlayItem;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import com.readystatesoftware.maps.TapControlledMapView;

public class NearbyPlaceMap extends MapActivity
{

	private static final String TAG = "CommendPlaceMap";
	private TapControlledMapView mapView;
	private List<Place> nearbyPlaceList = new ArrayList<Place>();
	private ImageView myLocateButton;
	private ImageView canceLocateButton;
	private CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay;
	long lasttime = -1;
    MapController mapc;
    private Place targetPlace;
    private HashMap<String, Double> location ;
    private ProgressDialog loadingDialog;
    
	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.nearby_place_map);
		loadingDialog = new ProgressDialog(this);
		boolean gpsEnable = checkGPSisOpen();
		//TravelApplication.getInstance().addActivity(this);
		ActivityMange.getInstance().addActivity(this);
		try
		{
			targetPlace = Place.parseFrom(getIntent().getByteArrayExtra(ConstantField.PLACE_DETAIL));	
			TextView titleTextView = (TextView) findViewById(R.id.place_title);
			titleTextView.setText(targetPlace.getName());
			mapView = (TapControlledMapView) findViewById(R.id.commendPlaceMap);
			mapc = mapView.getController();			
			mapView.setStreetView(true);
			//mapView.setOnSingleTapListener(onSingleTapListener);
			mapc.setZoom(16);		
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
			location = TravelApplication.getInstance().getLocation();
			if(TravelApplication.getInstance().mLocationClient !=null)
			{
				TravelApplication.getInstance().mLocationClient.stop();
			}
			return PlaceMission.getInstance().getPlaceNearbyInDistance(targetPlace, 10f);
		}

		@Override
		protected void onPostExecute(List<Place> result)
		{
			super.onPostExecute(result);
			nearbyPlaceList.clear();
			nearbyPlaceList.addAll(result);
			/*if(targetPlace!=null&&nearbyPlaceList.size()>0)
			{
				initMapView(targetPlace ,nearbyPlaceList);	
			}*/
			if(targetPlace!=null)
			{
				initMapView(targetPlace ,nearbyPlaceList);	
			}
			loadingDialog.dismiss();
		}

		@Override
		protected void onPreExecute()
		{
			showRoundProcessDialog();
			super.onPreExecute();
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
			boolean gpsEnable = checkGPSisOpen();
			LocationUtil.getLocation(NearbyPlaceMap.this);
			location = TravelApplication.getInstance().getLocation();
			if (location != null&&location.size()>0)
			{
				GeoPoint geoPoint = new GeoPoint((int) (location.get(ConstantField.LATITUDE) * 1E6),(int) (location.get(ConstantField.LONGITUDE) * 1E6));
				Drawable drawable = getResources().getDrawable(R.drawable.my_location);
				CommonOverlayItem overlayItem = new CommonOverlayItem(geoPoint, "", "", null,null);
				CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay3 = new CommonItemizedOverlay<CommonOverlayItem>(drawable, mapView);
				itemizedOverlay3.addOverlay(overlayItem);
				mapView.getOverlays().add(itemizedOverlay3);
				mapc.animateTo(geoPoint);
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
			if(targetPlace!=null&&nearbyPlaceList.size()>0)
			{
				initMapView(targetPlace ,nearbyPlaceList);	
			}
		}
	};
	
	/*private OnSingleTapListener onSingleTapListener = new OnSingleTapListener() {		
		@Override
		public boolean onSingleTap(MotionEvent e) {
			if(itemizedOverlay != null &&itemizedOverlay.size()>0)
			{
				itemizedOverlay.hideAllBalloons();
			}		
			return true;
		}
	};*/
	
	
	
	private void initMapView(Place targetPlace ,List<Place> placeList)
	{
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable marker = getResources().getDrawable(R.drawable.locate_back1);
		marker.setBounds(0, 0,(int) ((marker.getIntrinsicWidth() / 2) * 1.5),(int) ((marker.getIntrinsicHeight() / 2) * 1.5));
		itemizedOverlay = new CommonItemizedOverlay<CommonOverlayItem>(marker, mapView);
		GeoPoint geoPoint = new GeoPoint((int) (targetPlace.getLatitude() * 1e6),(int) (targetPlace.getLongitude() * 1e6));
		CommonOverlayItem commonOverlayItem = new CommonOverlayItem(geoPoint,targetPlace.getName(), null,targetPlace,null);
		itemizedOverlay.addOverlay(commonOverlayItem);
		itemizedOverlay.onTap(0);
		mapOverlays.add(itemizedOverlay);
		for (Place place : placeList)
		{
			int icon = TravelUtil.getForecastImage(place.getCategoryId());
			Drawable markerDrawable = getResources().getDrawable(icon);			
			CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay2 = new CommonItemizedOverlay<CommonOverlayItem>(markerDrawable, mapView);				
			GeoPoint geoPoint2 = new GeoPoint((int)(place.getLatitude()*1e6),(int)(place.getLongitude()*1e6));	
			CommonOverlayItem commonOverlayItem2 = new CommonOverlayItem(geoPoint2,place.getName(), null,place,null);
			itemizedOverlay2.addOverlay(commonOverlayItem2);
			mapOverlays.add(itemizedOverlay2);
		}
		if(itemizedOverlay.size()>0)
		{			
			mapc.setCenter(itemizedOverlay.getCenter());
		}		
		mapOverlays.add(itemizedOverlay);
	}
	
	
	
	
	private boolean checkGPSisOpen() {
		LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		}
			Toast.makeText(this, getString(R.string.open_gps_tips3), Toast.LENGTH_SHORT).show();
			return false;
	}
	
	


	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
		recycle();
		LocationUtil.stop();
	}
	
	
	private void recycle()
	{
		if(nearbyPlaceList != null&&nearbyPlaceList.size()>0)
		{
			nearbyPlaceList.clear();
			nearbyPlaceList = null;
		}
		itemizedOverlay = null;
		mapView.removeAllViews();
		mapView = null;
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
					Intent intent = new Intent(NearbyPlaceMap.this,IndexActivity.class);
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
		loadingDialog.show();
	}
	
}
