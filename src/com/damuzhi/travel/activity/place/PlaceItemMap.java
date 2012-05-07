package com.damuzhi.travel.activity.place;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class PlaceItemMap extends MapActivity
{

	private TravelApplication application;
	private OverlayItem locaions ;
	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onCreate(Bundle icicle)
	{
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.map);
		MapView mapView = (MapView) findViewById(R.id.map_view);
		application = (TravelApplication) this.getApplication();
		Place place = application.getPlace();
		Drawable marker = getResources().getDrawable(R.drawable.pin_jd);
		GeoPoint geoPoint = new GeoPoint((int)(place.getLatitude()*1e6), (int)(place.getLongitude()*1e6));
		locaions = new OverlayItem(geoPoint, place.getName(), null);
		LocationOverlay overlay = new LocationOverlay(marker);
		mapView.getOverlays().add(overlay);
		MapController mapC = mapView.getController();
		mapC.setCenter(overlay.getCenter());
		mapC.setZoom(15);
		
	}

	class LocationOverlay extends ItemizedOverlay
	{
		private Drawable marker;
		/**
		 * @param defaultMarker
		 * @param marker
		 */
		public LocationOverlay( Drawable marker)
		{
			super(marker);
			this.marker = marker;
		}

		@Override
		protected OverlayItem createItem(int i)
		{
			// TODO Auto-generated method stub
			return locaions;
		}

		@Override
		public int size()
		{
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow)
		{
			// TODO Auto-generated method stub
			super.draw(canvas, mapView, shadow);
			boundCenterBottom(marker);
		}
		
		
	}
	
}
