package com.damuzhi.travel.activity.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.location.LocationMager;
import com.damuzhi.travel.activity.common.location.LocationUtil;
import com.damuzhi.travel.activity.common.mapview.CommonItemizedOverlay;
import com.damuzhi.travel.activity.common.mapview.CommonOverlayItem;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.entry.MainActivity;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.readystatesoftware.maps.TapControlledMapView;

public class PlaceGoogleMap extends MapActivity
{

	private static final String TAG = "CommendPlaceMap";
	private TapControlledMapView mapView;
	private List<Place> nearbyPlaceList = null;
	private ImageView myLocateButton;
	private ImageView canceLocateButton;
	private CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay;
    private MapController mapc;
    private Place targetPlace;
    private HashMap<String, Double> location ;
    private ProgressDialog loadingDialog;
    private LocationMager locationMager;
    private CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay3;
    private View locationDialogView;
    private PopupWindow locationPopupWindow;
	private boolean isGetMyLocation = false;
    
	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.nearby_place_map);
		loadingDialog = new ProgressDialog(this);
		nearbyPlaceList = new ArrayList<Place>();
		checkGPSisOpen();
		locationMager = new LocationMager(this);
		ActivityMange.getInstance().addActivity(this);
		try
		{
			targetPlace = Place.parseFrom(getIntent().getByteArrayExtra(ConstantField.PLACE_DETAIL));	
		} catch (Exception e)
		{
			Log.e(TAG, "<NearbyPlaceMap> get place data but catch exception = "+e.toString(),e);
		}
		TextView titleTextView = (TextView) findViewById(R.id.place_title);
		titleTextView.setText(targetPlace.getName());
		mapView = (TapControlledMapView) findViewById(R.id.commendPlaceMap);
		mapView.setReticleDrawMode(MapView.ReticleDrawMode.DRAW_RETICLE_OVER);
		mapView.setBuiltInZoomControls(true);
		mapView.setSelected(true);
		mapView.preLoad();
		mapc = mapView.getController();
		mapc.setZoom(17);
		mapc = mapView.getController();			
		mapView.setStreetView(true);
		mapc.setZoom(16);		
		myLocateButton = (ImageView) findViewById(R.id.my_locate);
		canceLocateButton = (ImageView)findViewById(R.id.cancel_locate);
		myLocateButton.setOnClickListener(myLocateOnClickListener);
		canceLocateButton.setOnClickListener(cancelLocateOnClickListener);
		mapInit();
		
	}
	
	
	private void mapInit()
	{
		AsyncTask<Void, Void, List<Place>> asyncTask = new  AsyncTask<Void, Void, List<Place>>()
		{

			@Override
			protected List<Place> doInBackground(Void... params)
			{
				/*if(TravelApplication.getInstance().mLocationClient !=null)
				{
					TravelApplication.getInstance().mLocationClient.stop();
				}*/
				return PlaceMission.getInstance().getPlaceNearbyInDistance(targetPlace, 10f);
			}

			@Override
			protected void onPostExecute(List<Place> result)
			{
				super.onPostExecute(result);
				nearbyPlaceList.clear();
				nearbyPlaceList.addAll(result);
				if(targetPlace!=null)
				{
					initMapOverlayView(targetPlace ,nearbyPlaceList);	
				}
				loadingDialog.dismiss();
			}

			@Override
			protected void onPreExecute()
			{
				showRoundProcessDialog();
				super.onPreExecute();
			}
				
		};
		asyncTask.execute();
	}

	
	
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
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
					location = (HashMap<String, Double>) msg.obj;
					if(locationPopupWindow!=null&&locationPopupWindow.isShowing())
					{
						locationPopupWindow.dismiss();
					}
					if (location != null&&location.size()>0)
					{
						initMyLocationOverlay();
					}else
					{
						Toast.makeText(PlaceGoogleMap.this, getString(R.string.get_location_fail), Toast.LENGTH_LONG).show();
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
			getMyLocation(v);
			myLocateButton.setVisibility(View.GONE);
			canceLocateButton.setVisibility(View.VISIBLE);
		}
	};
	
	private OnClickListener cancelLocateOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			isGetMyLocation = false;
			canceLocateButton.setVisibility(View.GONE);
			myLocateButton.setVisibility(View.VISIBLE);
			if(itemizedOverlay3!=null)
			{
				mapView.getOverlays().remove(itemizedOverlay3);
			}
			if(targetPlace!=null)
			{	
				itemizedOverlay.onTap(0);
				mapc.animateTo(itemizedOverlay.getCenter());
			}
		}
	};
	
	private void getMyLocation(View v)
	{
		//checkGPSisOpen();	
		location = TravelApplication.getInstance().getLocation();
		if(location ==null||location.size()==0){
			locationMager.getLocation(handler);
			isGetMyLocation = true;
			alertWindow(v);		
		}else
		{
			initMyLocationOverlay();
		}
	}
	
	
	private void initMyLocationOverlay(){
		GeoPoint geoPoint = new GeoPoint((int) (location.get(ConstantField.LATITUDE) * 1E6),(int) (location.get(ConstantField.LONGITUDE) * 1E6));
		Drawable drawable = getResources().getDrawable(R.drawable.my_location);
		CommonOverlayItem overlayItem = new CommonOverlayItem(geoPoint, "", "", null,null);
		itemizedOverlay3 = new CommonItemizedOverlay<CommonOverlayItem>(drawable, mapView);
		itemizedOverlay3.addOverlay(overlayItem);
		mapView.getOverlays().add(itemizedOverlay3);
		mapc.animateTo(geoPoint);
	}
	
	
	
	

	
	private void initMapOverlayView(Place targetPlace ,List<Place> placeList)
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
		int icon =0;
		Drawable markerDrawable = null;
		CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay2 = null;
		GeoPoint geoPoint2 = null;
		CommonOverlayItem commonOverlayItem2 = null;
		for (Place place : placeList)
		{
			icon = TravelUtil.getForecastImage(place.getCategoryId());
			markerDrawable = getResources().getDrawable(icon);			
			itemizedOverlay2 = new CommonItemizedOverlay<CommonOverlayItem>(markerDrawable, mapView);				
			geoPoint2 = new GeoPoint((int)(place.getLatitude()*1e6),(int)(place.getLongitude()*1e6));	
			commonOverlayItem2 = new CommonOverlayItem(geoPoint2,place.getName(), null,place,null);
			itemizedOverlay2.addOverlay(commonOverlayItem2);
			mapOverlays.add(itemizedOverlay2);
		}
		if(itemizedOverlay.size()>0)
		{			
			mapc.setCenter(itemizedOverlay.getCenter());
		}		
		mapOverlays.add(itemizedOverlay);
		MotionEvent motionEvent = MotionEvent.obtain(3000,1000,MotionEvent.ACTION_DOWN,200,200,0);
		mapView.onTouchEvent(motionEvent);
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
		recycle();
		locationMager.destroyMyLocation();
		ActivityMange.getInstance().finishActivity();
	}
	
	
	private void recycle()
	{
		if(nearbyPlaceList != null&&nearbyPlaceList.size()>0)
		{
			nearbyPlaceList.clear();
		}
		itemizedOverlay = null;
		itemizedOverlay3 = null;
		mapView.removeAllViews();
		mapView = null;
		targetPlace = null;
		System.gc();
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
					Intent intent = new Intent(PlaceGoogleMap.this,MainActivity.class);
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


	@Override
	protected void onPause()
	{
		super.onPause();
		locationMager.disableMyLocation();
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
}
