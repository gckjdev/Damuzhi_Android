package com.damuzhi.travel.activity.place;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import android.R.integer;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.service.MainService;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;

public class PlaceMap extends MapActivity
{
	private MapView mapView;
	private View popupView;//µ¯³öÆøÅÝ
	private ArrayList<Place> places;
	long lasttime = -1;
    MapController mapc;
    private TravelApplication application;

	@Override
	protected void onCreate(Bundle icicle)
	{
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.map);
		application = (TravelApplication) this.getApplication();		
		mapView = (MapView) findViewById(R.id.placeMap);
		mapView.setBuiltInZoomControls(true);
		mapc = mapView.getController();
		mapView.setOnTouchListener(onTouchListener);
		Drawable marker = getResources().getDrawable(R.drawable.pin_jd);
		//marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		places = application.getPlaceData(); 
		PlaceLoaction placeLoaction = new PlaceLoaction( marker, places);
		mapView.getOverlays().add(placeLoaction);
		mapc.setCenter(placeLoaction.getCenter());
		mapc.setZoom(10);
		
		popupView = LayoutInflater.from(this).inflate(R.layout.overlay_popup, null);
		mapView.addView(popupView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, null, MapView.LayoutParams.BOTTOM_CENTER));
		popupView.setVisibility(View.GONE);
		placeLoaction.setOnFocusChangeListener(changeListener);
		popupView.setOnClickListener(onClickListener);
	}
	
	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isLocationDisplayed()
	{
		// TODO Auto-generated method stub
		return super.isLocationDisplayed();
	}

	private OnTouchListener onTouchListener = new OnTouchListener()
	{
		
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{if (event.getAction() == MotionEvent.ACTION_DOWN){

            if(event.getEventTime()-lasttime<2000){
                mapc.zoomInFixing((int)event.getX(),(int)event.getY());             
            }
        }       
        lasttime=event.getEventTime();
        return true;
		}
	};
	
	class PlaceLoaction extends ItemizedOverlay
	{
		private Drawable marker;
		//private ArrayList<int[][]> location;
		private List<OverlayItem> locaions = new ArrayList<OverlayItem>();
		
		
		/**
		 * @param defaultMarker
		 * @param marker
		 * @param location
		 */
		public PlaceLoaction( Drawable marker,
				ArrayList<Place> location)
		{
			super(marker);
			this.marker = marker;
			int i=0;
			//this.location = location;
			for (Place place:location)
			{			
				GeoPoint geoPoint = new GeoPoint((int)(place.getLatitude()*1e6),(int)(place.getLongitude()*1e6));
				locaions.add(new OverlayItem(geoPoint, place.getName(), Integer.toString(i)));
				i++;
			}
			populate();
		}

		@Override
		protected OverlayItem createItem(int i)
		{
			// TODO Auto-generated method stub
			return locaions.get(i);
		}

		@Override
		public int size()
		{
			// TODO Auto-generated method stub
			return locaions.size();
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow)
		{
			// TODO Auto-generated method stub
			super.draw(canvas, mapView, shadow);
			boundCenterBottom(marker);
		}
		
		
	}
	
	private  final ItemizedOverlay.OnFocusChangeListener changeListener = new ItemizedOverlay.OnFocusChangeListener()
	{

		@Override
		public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus)
		{
			// TODO Auto-generated method stub
			if(popupView !=null)
			{
				popupView.setVisibility(View.GONE);
			}
			if(newFocus != null)
			{
				MapView.LayoutParams geoLP = (LayoutParams) popupView.getLayoutParams();
				geoLP.point = newFocus.getPoint();
				TextView titleView = (TextView) popupView.findViewById(R.id.map_bubbleTitle);
				titleView.setText(newFocus.getTitle());
				/*TextView desc = (TextView) popupView.findViewById(R.id.map_bubbleText);
				if(newFocus.getSnippet()==null||newFocus.getSnippet().length() ==0)
				{
					desc.setVisibility(View.GONE);
				}else {
					desc.setVisibility(View.VISIBLE);
					desc.setText(newFocus.getSnippet());
				}*/
				popupView.setTag(newFocus.getSnippet());
				mapView.updateViewLayout(popupView, geoLP);
				popupView.setVisibility(View.VISIBLE);
			}
		}
	};
	
	
	private OnClickListener onClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			int position = Integer.parseInt(v.getTag().toString());
			application.setPlace(places.get(position));
			Intent intent = new Intent();
			intent.setClass(PlaceMap.this, SceneryDetailActivity.class);
			startActivity(intent);
		}
	};
}
