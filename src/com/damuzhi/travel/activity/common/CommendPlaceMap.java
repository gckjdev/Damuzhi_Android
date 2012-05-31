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
import com.damuzhi.travel.activity.place.EntertainmentDetailActivity;
import com.damuzhi.travel.activity.place.HotelDetailActivity;
import com.damuzhi.travel.activity.place.RestaurantDetailActivity;
import com.damuzhi.travel.activity.place.SceneryDetailActivity;
import com.damuzhi.travel.activity.place.ShoppingDetailActivity;
import com.damuzhi.travel.mission.PlaceMission;
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

public class CommendPlaceMap extends MapActivity
{

	private static final String TAG = "CommendPlaceMap";
	private static final int PLACE_LIST = 1;
	private static final String TARGET_PLACE_POSITION = "-1";
	private MapView mapView;
	private View popupView;//
	private View targetPlaceView;
	private List<Place> placeList;
	private ImageButton selectListButton;
	private Dialog loadingDialog;
	long lasttime = -1;
    MapController mapc;
    private TravelApplication application;
    private Place targetPlace;
    int placeId =0 ;
    
	@Override
	protected void onCreate(Bundle icicle)
	{
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.commend_place_map);
		MainService.allActivity.add(this);
		application = (TravelApplication) this.getApplication();
		placeId = getIntent().getIntExtra(ConstantField.PLACE_CATEGORY_ID, -1);
		//Log.d(TAG, "onCreate");
	}
	
	
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();		
		//Log.d(TAG, "onResume() ");
		if(placeList==null)
		{
			showRoundProcessDialog(CommendPlaceMap.this, R.layout.loading_process_dialog_anim);
			Task sceneryTask = new Task(Task.MAP_NEARBY,CommendPlaceMap.this);
			MainService.newTask(sceneryTask);
			Intent intent = new Intent(ConstantField.MAIN_SERVICE);
			startService(intent);		
		}	
	}
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what)
			{
			case PLACE_LIST:
				 placeList = PlaceMission.getInstance().getAllPlace(PlaceCategoryType.PLACE_SPOT_VALUE, CommendPlaceMap.this);
				 loadingDialog.dismiss();
				 init();		
				break;
			default:
				break;
			}
		}
	};
	
	/**  
	        *   
	        * @description   ��ʼ��
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-8 ����10:48:49  
	        */
	public void init()
	{	
		targetPlace = PlaceMission.getInstance().getPlaceById(placeId);
		selectListButton = (ImageButton) findViewById(R.id.list_view);
		mapView = (MapView) findViewById(R.id.commendPlaceMap);
		mapView.setBuiltInZoomControls(true);
		mapc = mapView.getController();
		Drawable marker = getResources().getDrawable(R.drawable.location);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		PlaceLoaction placeLoaction = new PlaceLoaction( marker,targetPlace, placeList);
		mapView.getOverlays().add(placeLoaction);
		mapView.postInvalidate();
		GeoPoint pt = placeLoaction.getCenter();
		mapc.setCenter(pt);
		mapc.setZoom(13);		
		
		targetPlaceView = LayoutInflater.from(CommendPlaceMap.this).inflate(R.layout.overlay_popup, null);
		GeoPoint geoPoint = new GeoPoint((int)(targetPlace.getLatitude()*1e6),(int)(targetPlace.getLongitude()*1e6));
		TextView titleView = (TextView) targetPlaceView.findViewById(R.id.map_bubbleTitle);
		titleView.setText(targetPlace.getName());
		mapView.addView(targetPlaceView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, geoPoint, MapView.LayoutParams.BOTTOM_CENTER));
		
		
		popupView = LayoutInflater.from(CommendPlaceMap.this).inflate(R.layout.overlay_popup, null);
		mapView.addView(popupView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, null, MapView.LayoutParams.BOTTOM_CENTER));
		popupView.setVisibility(View.GONE);
		placeLoaction.setOnFocusChangeListener(changeListener);
		popupView.setOnClickListener(onClickListener);
		
	}
	
	public void refresh(Object param)
	{
		Message message = handler.obtainMessage();
		message.what = PLACE_LIST;
		message.obj = param;
		handler.sendMessage(message);
	}
	
	
	
	
	
	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

 
	
	/**  
	        * @description   ��ͼ��ʾͼ��
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-8 ����10:44:53  
	        */  
	class PlaceLoaction extends ItemizedOverlay<OverlayItem>
	{
		private Drawable marker;
		//private ArrayList<int[][]> location;
		private List<OverlayItem> locaions = new ArrayList<OverlayItem>();
		
		
		/**
		 * @param defaultMarker
		 * @param marker
		 * @param location
		 */
		public PlaceLoaction( Drawable iconMarker,Place targetPlace ,List<Place> placeList)
		{
			super(iconMarker);
			this.marker = iconMarker;
			int i=0;
			/*GeoPoint placeGeoPoint = new GeoPoint((int)(targetPlace.getLatitude()*1e6),(int)(targetPlace.getLongitude()*1e6));
			OverlayItem placeOverlayItem = new OverlayItem(placeGeoPoint, targetPlace.getName(),TARGET_PLACE_POSITION );
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
			placeOverlayItem.setMarker(marker);
			locaions.add(placeOverlayItem);		*/
			for (Place place:placeList)
			{			
				int icon = TravelUtil.getForecastImage(place.getCategoryId());
				Drawable markerDrawable = getResources().getDrawable(icon);
				GeoPoint geoPoint = new GeoPoint((int)(place.getLatitude()*1e6),(int)(place.getLongitude()*1e6));
				OverlayItem overlayItem = new OverlayItem(geoPoint, place.getName(), Integer.toString(i));
				markerDrawable.setBounds(0, 0, markerDrawable.getIntrinsicWidth(), markerDrawable.getIntrinsicHeight());
				overlayItem.setMarker(markerDrawable);
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
	
	
	
	/**  
	        * @fields changeListener  
	        */  
	private  final ItemizedOverlay.OnFocusChangeListener changeListener = new ItemizedOverlay.OnFocusChangeListener()
	{

		@Override
		public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus)
		{
			// TODO Auto-generated method stub
			//Log.d(TAG, "force");
			if(popupView !=null)
			{
				popupView.setVisibility(View.GONE);
			}
			if(newFocus != null)
			{
				if(newFocus.getSnippet() == TARGET_PLACE_POSITION)
				{
					Intent intent = new Intent();
					switch (targetPlace.getCategoryId())
					{
					case 1:
						intent.setClass(CommendPlaceMap.this, SceneryDetailActivity.class);
						break;
					case 2:
						intent.setClass(CommendPlaceMap.this, HotelDetailActivity.class);
						break;
					case 3:
						intent.setClass(CommendPlaceMap.this, RestaurantDetailActivity.class);
						break;
					case 4:
						intent.setClass(CommendPlaceMap.this, ShoppingDetailActivity.class);
						break;
					case 5:
						intent.setClass(CommendPlaceMap.this, EntertainmentDetailActivity.class);
						break;
					default:
						break;
					}			
					startActivity(intent);
				    return;
				}
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
	
		
		 /**  
	     * @param mContext
	     * @param layout  
	     * @description   ���صȴ���ʾ�� 
	     * @version 1.0  
	     * @author liuxiaokun  
	     * @update 2012-5-8 ����11:47:37  
	     */
	public void showRoundProcessDialog(Context mContext, int layout)
	 {
	     OnKeyListener keyListener = new OnKeyListener()
	     {
	         @Override
	         public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
	         {
	         	if ( keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
	         		loadingDialog.dismiss();
	         		Intent intent = new Intent();
	    			Log.d(TAG, "cateID = "+targetPlace.getCategoryId());
	    			switch (targetPlace.getCategoryId())
	    			{
	    			case PlaceCategoryType.PLACE_SPOT_VALUE:
	    				intent.setClass(CommendPlaceMap.this, SceneryDetailActivity.class);
	    				break;
	    			case PlaceCategoryType.PLACE_HOTEL_VALUE:
	    				intent.setClass(CommendPlaceMap.this, HotelDetailActivity.class);
	    				break;
	    			case PlaceCategoryType.PLACE_RESTRAURANT_VALUE:
	    				intent.setClass(CommendPlaceMap.this, RestaurantDetailActivity.class);
	    				break;
	    			case PlaceCategoryType.PLACE_SHOPPING_VALUE:
	    				intent.setClass(CommendPlaceMap.this, ShoppingDetailActivity.class);
	    				break;
	    			case PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE:
	    				intent.setClass(CommendPlaceMap.this, EntertainmentDetailActivity.class);
	    				break;
	    			default:
	    				break;
	    			}			
	    			startActivity(intent);
				    return true;
		   		    }
		   			else
		   			{
	   				  return false;	
		   			}
	         }
	     };
	
	     loadingDialog = new AlertDialog.Builder(mContext).create();
	     loadingDialog.setOnKeyListener(keyListener);
	     loadingDialog.show();
	     // ע��˴�Ҫ����show֮�� ����ᱨ�쳣
	     loadingDialog.setContentView(layout);
	 }
	
	/**  
	        * @fields onClickListener  
	        */  
	private OnClickListener onClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			String position = v.getTag().toString();
			if (!position.equals(TARGET_PLACE_POSITION))
			{
				application.setPlace(placeList.get(Integer.parseInt(position)));
			}
			Intent intent = new Intent();			
			switch (placeList.get(Integer.parseInt(position)).getCategoryId())
			{
			case PlaceCategoryType.PLACE_SPOT_VALUE:
				intent.setClass(CommendPlaceMap.this, SceneryDetailActivity.class);
				break;
			case PlaceCategoryType.PLACE_HOTEL_VALUE:
				intent.setClass(CommendPlaceMap.this, HotelDetailActivity.class);
				break;
			case PlaceCategoryType.PLACE_RESTRAURANT_VALUE:
				intent.setClass(CommendPlaceMap.this, RestaurantDetailActivity.class);
				break;
			case PlaceCategoryType.PLACE_SHOPPING_VALUE:
				intent.setClass(CommendPlaceMap.this, ShoppingDetailActivity.class);
				break;
			case PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE:
				intent.setClass(CommendPlaceMap.this, EntertainmentDetailActivity.class);
				break;
			default:
				break;
			}			
			startActivity(intent);
			popupView.setVisibility(View.GONE);
		}
	};

	
	 
	 
	

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy() ");
	}



	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "onpause");
		popupView.setVisibility(View.GONE);
	}

	
}
