package com.damuzhi.travel.activity.common;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import android.R.integer;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.place.EntertainmentActivity;
import com.damuzhi.travel.activity.place.EntertainmentDetailActivity;
import com.damuzhi.travel.activity.place.HotelActivity;
import com.damuzhi.travel.activity.place.HotelDetailActivity;
import com.damuzhi.travel.activity.place.RestaurantActivity;
import com.damuzhi.travel.activity.place.RestaurantDetailActivity;
import com.damuzhi.travel.activity.place.SceneryActivity;
import com.damuzhi.travel.activity.place.SceneryDetailActivity;
import com.damuzhi.travel.activity.place.ShoppingActivity;
import com.damuzhi.travel.activity.place.ShoppingDetailActivity;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.util.TravelUtil;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;

public class PlaceMap extends MapActivity
{
	private static final String TAG = "PlaceMap";
	private MapView mapView;
	private View popupView;
	private ArrayList<Place> places;
	long lasttime = -1;
    MapController mapc;
    private TravelApplication application;
    private ImageButton selectListButton;
	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.map);
		TravelApplication.getInstance().addActivity(this);
		MainService.allActivity.add(this);
		application = (TravelApplication) this.getApplication();		
		mapView = (MapView) findViewById(R.id.placeMap);
		selectListButton = (ImageButton) findViewById(R.id.list_view);
		mapView.setBuiltInZoomControls(true);
		mapc = mapView.getController();
		//Drawable marker = getResources().getDrawable(R.drawable.pin_jd);
		//marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		places = application.getPlaceData(); 
		PlaceLoaction placeLoaction = new PlaceLoaction( null, places);
		mapView.getOverlays().add(placeLoaction);
		mapc.setCenter(placeLoaction.getCenter());
		mapc.setZoom(13);		
		popupView = LayoutInflater.from(this).inflate(R.layout.overlay_popup, null);
		mapView.addView(popupView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, null, MapView.LayoutParams.BOTTOM_CENTER));
		popupView.setVisibility(View.GONE);
		placeLoaction.setOnFocusChangeListener(changeListener);
		popupView.setOnClickListener(onClickListener);
		selectListButton.setOnClickListener(listClickListener);
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

	
	class PlaceLoaction extends ItemizedOverlay
	{
		//private ArrayList<int[][]> location;
		private List<OverlayItem> locaions = new ArrayList<OverlayItem>();
		
		
		/**
		 * @param defaultMarker
		 * @param marker
		 * @param location
		 */
		public PlaceLoaction( Drawable marker,ArrayList<Place> location)
		{
			super(marker);
			//this.marker = marker;
			int i=0;
			//this.location = location;
			for (Place place:location)
			{			
				int icon = TravelUtil.getForecastImage(place.getCategoryId());
				Drawable markerIcon = getResources().getDrawable(icon);
				GeoPoint geoPoint = new GeoPoint((int)(place.getLatitude()*1e6),(int)(place.getLongitude()*1e6));
				OverlayItem overlayItem = new OverlayItem(geoPoint, place.getName(), Integer.toString(i));
				markerIcon.setBounds(0, 0, markerIcon.getIntrinsicWidth(), markerIcon.getIntrinsicHeight());
				overlayItem.setMarker(markerIcon);
				locaions.add(overlayItem);
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

		/*@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow)
		{
			// TODO Auto-generated method stub
			super.draw(canvas, mapView, shadow);
			Log.d(TAG, "MAP_VIEW");
			boundCenterBottom(marker);
		}*/
		
		
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
			String position = v.getTag().toString();
			application.setPlace(places.get(Integer.parseInt(position)));
			Intent intent = new Intent();
			switch (places.get(Integer.parseInt(position)).getCategoryId())
			{
			case PlaceCategoryType.PLACE_SPOT_VALUE:
				intent.setClass(PlaceMap.this, SceneryDetailActivity.class);
				break;
			case PlaceCategoryType.PLACE_HOTEL_VALUE:
				intent.setClass(PlaceMap.this, HotelDetailActivity.class);
				break;
			case PlaceCategoryType.PLACE_RESTRAURANT_VALUE:
				intent.setClass(PlaceMap.this, RestaurantDetailActivity.class);
				break;
			case PlaceCategoryType.PLACE_SHOPPING_VALUE:
				intent.setClass(PlaceMap.this, ShoppingDetailActivity.class);
				break;
			case PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE:
				intent.setClass(PlaceMap.this, EntertainmentDetailActivity.class);
				break;
			default:
				break;
			}			
			startActivity(intent);
			popupView.setVisibility(View.GONE);
		}
		};
		
		private OnClickListener listClickListener = new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				int placeCategory = application.getPlaceCategoryID();
				Intent intent = new Intent();
				switch (placeCategory)
				{
				case PlaceCategoryType.PLACE_SPOT_VALUE:
					intent.setClass(PlaceMap.this, SceneryActivity.class);
					break;
				case PlaceCategoryType.PLACE_HOTEL_VALUE:
					intent.setClass(PlaceMap.this, HotelActivity.class);
					break;
				case PlaceCategoryType.PLACE_RESTRAURANT_VALUE:
					intent.setClass(PlaceMap.this, RestaurantActivity.class);
					break;
				case PlaceCategoryType.PLACE_SHOPPING_VALUE:
					intent.setClass(PlaceMap.this, ShoppingActivity.class);
					break;
				case PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE:
					intent.setClass(PlaceMap.this, EntertainmentActivity.class);
					break;
				default:
					break;
				}			
				startActivity(intent);
			}
			};
}
