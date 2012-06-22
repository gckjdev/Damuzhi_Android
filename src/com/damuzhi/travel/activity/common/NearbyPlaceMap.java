package com.damuzhi.travel.activity.common;

import java.util.ArrayList;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.place.CommonPlaceDetailActivity;
import com.damuzhi.travel.activity.place.EntertainmentDetailActivity;
import com.damuzhi.travel.activity.place.HotelDetailActivity;
import com.damuzhi.travel.activity.place.RestaurantDetailActivity;
import com.damuzhi.travel.activity.place.SceneryDetailActivity;
import com.damuzhi.travel.activity.place.ShoppingDetailActivity;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.service.Task;
import com.damuzhi.travel.util.TravelUtil;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.Projection;
import com.google.protobuf.InvalidProtocolBufferException;

public class NearbyPlaceMap extends MapActivity
{

	private static final String TAG = "CommendPlaceMap";
	private static final int PLACE_LIST = 1;
	private static final String TARGET_PLACE_POSITION = "-1";
	private MapView mapView;
	private View popupView;//
	private View targetPlaceView;
	private List<Place> nearbyPlaceList;
	//private ImageButton selectListButton;
	private Dialog loadingDialog;
	long lasttime = -1;
    MapController mapc;
   // private TravelApplication application;
    private Place targetPlace;
   // int placeId =0 ;
    
	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.nearby_place_map);
		try
		{
			targetPlace = Place.parseFrom(getIntent().getByteArrayExtra(ConstantField.PLACE_DETAIL));	
			mapView = (MapView) findViewById(R.id.commendPlaceMap);
			//mapView.setBuiltInZoomControls(true);
			mapc = mapView.getController();			
			mapView.setStreetView(true);
			mapc.setZoom(20);		
			MapInitAsynTask asynTask = new MapInitAsynTask();
			asynTask.execute();
		} catch (InvalidProtocolBufferException e)
		{
			Log.e(TAG, "<CommendPlaceMap> get place data but catch exception = "+e.toString(),e);
		}
		
	}
	
	
	

	class MapInitAsynTask extends AsyncTask<Void, Void, List<Place>>
	{

		@Override
		protected List<Place> doInBackground(Void... params)
		{
			// TODO Auto-generated method stub
			return PlaceMission.getInstance().getPlaceNearbyInDistance(targetPlace, 10f);
		}

		@Override
		protected void onPostExecute(List<Place> result)
		{
			super.onPostExecute(result);
			nearbyPlaceList = result;
			//selectListButton = (ImageButton) findViewById(R.id.list_view);
			Drawable marker = getResources().getDrawable(R.drawable.locate_back1);
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
			PlaceLoaction placeLoaction = new PlaceLoaction( marker,targetPlace, nearbyPlaceList);
			mapView.getOverlays().add(placeLoaction);		
			GeoPoint pt = placeLoaction.getCenter();
			mapc.setCenter(pt);
			
			targetPlaceView = LayoutInflater.from(NearbyPlaceMap.this).inflate(R.layout.overlay_popup, null);
			GeoPoint geoPoint = new GeoPoint((int)(targetPlace.getLatitude()*1e6),(int)(targetPlace.getLongitude()*1e6));
			TextView titleView = (TextView) targetPlaceView.findViewById(R.id.map_bubbleTitle);
			titleView.setText(targetPlace.getName());
			mapView.addView(targetPlaceView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, geoPoint, MapView.LayoutParams.BOTTOM_CENTER));
			
			
			popupView = LayoutInflater.from(NearbyPlaceMap.this).inflate(R.layout.overlay_popup, null);
			mapView.addView(popupView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, null, MapView.LayoutParams.BOTTOM_CENTER));
			popupView.setVisibility(View.GONE);
			placeLoaction.setOnFocusChangeListener(changeListener);
			popupView.setOnClickListener(nearbyItemOnClickListener);
		}
		
	}
	
	public void init()
	{	
		nearbyPlaceList = PlaceMission.getInstance().getPlaceNearbyInDistance(targetPlace, 10f);
		//selectListButton = (ImageButton) findViewById(R.id.list_view);
		mapView = (MapView) findViewById(R.id.commendPlaceMap);
		//mapView.setBuiltInZoomControls(true);
		mapc = mapView.getController();
		Drawable marker = getResources().getDrawable(R.drawable.locate_back1);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		PlaceLoaction placeLoaction = new PlaceLoaction( marker,targetPlace, nearbyPlaceList);
		mapView.getOverlays().add(placeLoaction);
		//mapView.postInvalidate();
		mapView.setStreetView(true);
		GeoPoint pt = placeLoaction.getCenter();
		mapc.setCenter(pt);
		mapc.setZoom(20);		
		
		targetPlaceView = LayoutInflater.from(NearbyPlaceMap.this).inflate(R.layout.overlay_popup, null);
		GeoPoint geoPoint = new GeoPoint((int)(targetPlace.getLatitude()*1e6),(int)(targetPlace.getLongitude()*1e6));
		TextView titleView = (TextView) targetPlaceView.findViewById(R.id.map_bubbleTitle);
		titleView.setText(targetPlace.getName());
		mapView.addView(targetPlaceView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, geoPoint, MapView.LayoutParams.BOTTOM_CENTER));
		
		
		popupView = LayoutInflater.from(NearbyPlaceMap.this).inflate(R.layout.overlay_popup, null);
		mapView.addView(popupView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, null, MapView.LayoutParams.BOTTOM_CENTER));
		popupView.setVisibility(View.GONE);
		placeLoaction.setOnFocusChangeListener(changeListener);
		//popupView.setOnClickListener(onClickListener);
		
	}
	
	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}

 
	class PlaceLoaction extends ItemizedOverlay<OverlayItem>
	{
		private Drawable marker;
		//private ArrayList<int[][]> location;
		private List<OverlayItem> locaions = new ArrayList<OverlayItem>();
		
		
	
		public PlaceLoaction( Drawable iconMarker,Place targetPlace ,List<Place> placeList)
		{
			super(iconMarker);
			this.marker = iconMarker;
			int i=0;
			GeoPoint placeGeoPoint = new GeoPoint((int)(targetPlace.getLatitude()*1e6),(int)(targetPlace.getLongitude()*1e6));
			OverlayItem placeOverlayItem = new OverlayItem(placeGeoPoint, targetPlace.getName(),TARGET_PLACE_POSITION );
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
			placeOverlayItem.setMarker(marker);
			locaions.add(placeOverlayItem);		
			for (Place place:placeList)
			{			
				int icon = TravelUtil.getForecastImage(place.getCategoryId());
				Drawable markerDrawable = getResources().getDrawable(icon);
				GeoPoint geoPoint = new GeoPoint((int)(place.getLatitude()*1e6),(int)(place.getLongitude()*1e6));
				OverlayItem overlayItem = new OverlayItem(geoPoint, place.getName(), Integer.toString(i));
				markerDrawable.setBounds(0, 0, (int)((markerDrawable.getIntrinsicWidth()/2)*1.5), (int)(markerDrawable.getIntrinsicHeight()/2*1.5));
				overlayItem.setMarker(markerDrawable);
				locaions.add(overlayItem);
				i++;
			}
			populate();
		}

		@Override
		protected OverlayItem createItem(int i)
		{
			return locaions.get(i);
		}

		@Override
		public int size()
		{
			return locaions.size();
		}

		/*@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow)
		{
			// TODO Auto-generated method stub
			super.draw(canvas, mapView, shadow);			
			Projection projection = mapView.getProjection();
			Double latitude = targetPlace.getLatitude()*1e6;
			Double longitude = targetPlace.getLongitude()*1e6;
			GeoPoint geoPoint = new GeoPoint(latitude.intValue(), longitude.intValue());
			Point myPoint = new Point();
			projection.toPixels(geoPoint, myPoint);
			Paint paint = new Paint();
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(CommendPlaceMap.this.getResources().getDimension(R.dimen.location_info));
			paint.setColor(CommendPlaceMap.this.getResources().getColor(R.color.white));
			canvas.drawText(targetPlace.getName(), myPoint.x+(marker.getIntrinsicWidth()/2), myPoint.y+(marker.getIntrinsicHeight()/2), paint);
			
		}*/

		

		
		
	}
	
	
	
	
	private  final ItemizedOverlay.OnFocusChangeListener changeListener = new ItemizedOverlay.OnFocusChangeListener()
	{

		@Override
		public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus)
		{
			if(popupView !=null)
			{
				popupView.setVisibility(View.GONE);
			}
			if(newFocus != null)
			{
				if(newFocus.getSnippet() == TARGET_PLACE_POSITION)
				{
					/*Intent intent = new Intent();				
					startActivity(intent);*/
				    return;
				}
				MapView.LayoutParams geoLP = (LayoutParams) popupView.getLayoutParams();
				geoLP.point = newFocus.getPoint();
				TextView titleView = (TextView) popupView.findViewById(R.id.map_bubbleTitle);
				titleView.setText(newFocus.getTitle());
				
				popupView.setTag(newFocus.getSnippet());
				mapView.updateViewLayout(popupView, geoLP);
				popupView.setVisibility(View.VISIBLE);
			}
		}
	};
	
		
		
	private OnClickListener nearbyItemOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			int position = Integer.parseInt((String)v.getTag());
			Place nearbyPlace = nearbyPlaceList.get(position);
			Intent intent = new Intent();
			intent.putExtra(ConstantField.PLACE_DETAIL, nearbyPlace.toByteArray());
			Class activity = CommonPlaceDetailActivity.getClassByPlaceType(nearbyPlace.getCategoryId());
			intent.setClass(NearbyPlaceMap.this, activity);
			//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			
		}
	};
	
	

		





	@Override
	protected void onPause()
	{
		super.onPause();
		Log.d(TAG, "onpause");
		popupView.setVisibility(View.GONE);
	}

	
}
