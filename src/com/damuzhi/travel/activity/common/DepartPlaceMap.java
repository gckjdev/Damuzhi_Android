/**  
        * @title DepartPlaceMap.java  
        * @package com.damuzhi.travel.activity.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-10-9 上午10:50:01  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
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

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.NearbyPlaceMap.MapInitAsynTask;
import com.damuzhi.travel.activity.common.location.LocationUtil;
import com.damuzhi.travel.activity.common.mapview.CommonItemizedOverlay;
import com.damuzhi.travel.activity.common.mapview.CommonOverlayItem;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.entry.MainActivity;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.TouristRouteProtos.DepartPlace;
import com.damuzhi.travel.util.TravelUtil;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import com.readystatesoftware.maps.TapControlledMapView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-10-9 上午10:50:01  
 */

public class DepartPlaceMap extends MapActivity
{


	private static final String TAG = "CommendPlaceMap";
	private TapControlledMapView mapView;
	private CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay;
	long lasttime = -1;
    MapController mapc;
    private ProgressDialog loadingDialog;
    private DepartPlace departPlace;
    
	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.nearby_place_map);
		findViewById(R.id.my_locate).setVisibility(View.GONE);
		loadingDialog = new ProgressDialog(this);
		boolean gpsEnable = checkGPSisOpen();
		ActivityMange.getInstance().addActivity(this);
		try
		{
			departPlace = DepartPlace.parseFrom(getIntent().getByteArrayExtra(ConstantField.DEPART_PLACE));	
			
		} catch (Exception e)
		{
			Log.e(TAG, "<CommendPlaceMap> get place data but catch exception = "+e.toString(),e);
		}
		TextView titleTextView = (TextView) findViewById(R.id.place_title);
		titleTextView.setText(getString(R.string.depart_place_title_2));
		mapView = (TapControlledMapView) findViewById(R.id.commendPlaceMap);
		mapc = mapView.getController();			
		mapView.setStreetView(true);
		mapc.setZoom(16);		
		initMapView(departPlace);
		
	}
	
	
	

	
	
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}
	
	
	
	
	
	
	private void initMapView(DepartPlace departPlace)
	{
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable marker = getResources().getDrawable(R.drawable.locate_back1);
		marker.setBounds(0, 0,(int) ((marker.getIntrinsicWidth() / 2) * 1.5),(int) ((marker.getIntrinsicHeight() / 2) * 1.5));
		itemizedOverlay = new CommonItemizedOverlay<CommonOverlayItem>(marker, mapView);
		GeoPoint geoPoint = new GeoPoint((int) (departPlace.getLatitude() * 1e6),(int) (departPlace.getLongitude() * 1e6));
		CommonOverlayItem commonOverlayItem = new CommonOverlayItem(geoPoint,departPlace.getDepartPlace(), null,null,departPlace);
		itemizedOverlay.addOverlay(commonOverlayItem);
		itemizedOverlay.onTap(0);
		mapOverlays.add(itemizedOverlay);
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
		itemizedOverlay = null;
		mapView.removeAllViews();
		
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
					Intent intent = new Intent(DepartPlaceMap.this,MainActivity.class);
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
