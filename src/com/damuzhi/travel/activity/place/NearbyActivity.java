/**  
        * @title Nearby.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-17 ����10:55:09  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.place;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.HotelAdapter;
import com.damuzhi.travel.activity.adapter.place.NearbyAdapter;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.PlaceActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
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
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-17 ����10:55:09  
 */

public class NearbyActivity extends MapActivity implements PlaceActivity
{
	private static final String TAG = "Nearby";
	private ImageButton startButton;
	private ImageButton oneKMbutton;
	private ImageButton fiveKMButtom;
	private ImageButton tenKMButton;
	private ImageView redStart;
	private int startPosition = 0;
	private float offset;
	private int bmpW;//
	private int screenW;
	private TextView allPlace;
	private TextView spot;
	private TextView hotel;
	private TextView restaurant;
	private TextView shopping;
	private TextView entertrainment;
	private TextView move;
	private int tabStartPosition = 0;
	private ListView listView;
	private int startLeft = 0; 
	private static final int LOADING = 0;
	private static final int LOAD_OK = 1;
	private Dialog loadingDialog;
	private ArrayList<Place> placeList;
	private int loadFlag = 1;
	private HashMap<Integer,String> symbolMap;
	private HashMap<Integer, String> cityAreaMap;
	private HashMap<String, Double> location;
	private int cityID = -1;
	private NearbyAdapter adapter;
	private String dataPath ;
	private MapView mapView;
	private View popupView;//
	long lasttime = -1;
    MapController mapc;
    private TravelApplication application;
    private ImageButton selectMapViewButton;
    private ImageButton selectListViewButton;
    private int placeCategoryID = ConstantField.ALL_PLACE_CATEGORY_ID;
    private int placeDistance = 500;
    private PlaceLoaction placeLoaction;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby);
		
		loadFlag = 0;
		init();
		initTab();
		
	}
	@Override
	public void init()
	{
		listView = (ListView) findViewById(R.id.nearby_list);
		mapView = (MapView) findViewById(R.id.placeMap);
		mapc = mapView.getController();
		selectMapViewButton = (ImageButton) findViewById(R.id.map_view);
		selectListViewButton = (ImageButton)findViewById(R.id.list_view);
		redStart = (ImageView) findViewById(R.id.position);
		startButton = (ImageButton) findViewById(R.id.start_button);
		oneKMbutton = (ImageButton) findViewById(R.id.one_km);
		fiveKMButtom = (ImageButton) findViewById(R.id.five_km);
		tenKMButton = (ImageButton) findViewById(R.id.ten_km);	
		startButton.setOnClickListener(listener);
		fiveKMButtom.setOnClickListener(listener);
		oneKMbutton.setOnClickListener(listener);
		tenKMButton.setOnClickListener(listener);
		popupView = LayoutInflater.from(this).inflate(R.layout.overlay_popup, null);
		application= TravelApplication.getInstance();
	}

	private void initData(Object...param)
	{
		cityID = application.getCityID();
		dataPath = String.format(ConstantField.IMAGE_PATH,cityID);	
		location = application.getLocation();
		symbolMap = (HashMap<Integer, String>) param[0];
		cityAreaMap = (HashMap<Integer, String>) param[1];		
		placeList = TravelUtil.getPlaceInDistance(500, application.getPlaceData(), location,ConstantField.ALL_PLACE_CATEGORY_ID);
		//titleView.setText(this.getResources().getString(R.string.hotel)+"("+size+")");
		adapter = new NearbyAdapter(this,dataPath,placeList,location,symbolMap.get(application.getCityID()),cityAreaMap,application.getDataFlag());
		//adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);
		selectMapViewButton.setOnClickListener(onClickListener);
	}
	
	public void initMapView()
	{	
		listView.setVisibility(View.GONE);
		selectMapViewButton.setVisibility(View.GONE);		
		mapView.setVisibility(View.VISIBLE);
		selectListViewButton.setVisibility(View.VISIBLE);		
		mapView.setBuiltInZoomControls(true);
		mapc.setZoom(13);				
		addPopupView();
		selectListViewButton.setOnClickListener(onClickListener);
		placeLoaction = new PlaceLoaction( null, placeList);
		mapView.getOverlays().add(placeLoaction);
		mapc.setCenter(placeLoaction.getCenter());
		placeLoaction.setOnFocusChangeListener(changeListener);
	}
	
	public void addPopupView()
	{
		mapView.addView(popupView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, null, MapView.LayoutParams.BOTTOM_CENTER));
		popupView.setVisibility(View.GONE);	
		popupView.setOnClickListener(popupViewOnClickListener);
	}
	
	
	@Override
	public void refresh(Object...param)
	{
		initData(param);
		Message message = handler.obtainMessage();
		message.what = LOAD_OK;
		message.obj = null;
		handler.sendMessage(message);
	}
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case LOADING:				
				break;
			case LOAD_OK:							
				loadingDialog.dismiss();	
				loadingDialog.cancel();
				break;			
			default:
				break;
			}
	}};

	
	@Override
	public void placeInfo()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if(loadFlag == 0)
		{			
			loadFlag = 1;
			showRoundProcessDialog(NearbyActivity.this, R.layout.loading_process_dialog_anim);
			Task nearbyTask = new Task(Task.TASK_LOGIN_NEARBY,NearbyActivity.this);
			MainService.newTask(nearbyTask);
			Intent intent = new Intent(ConstantField.MAIN_SERVICE);
			startService(intent);	
		}
				
	}

	
	
	 private void initTab(){			
			allPlace = (TextView) findViewById(R.id.place_all);
			spot = (TextView) findViewById(R.id.place_spot);
			hotel = (TextView) findViewById(R.id.place_hotel);
			restaurant = (TextView) findViewById(R.id.place_restaurant);
			shopping = (TextView) findViewById(R.id.place_shopping);
			entertrainment = (TextView) findViewById(R.id.place_entertrainment);			
			allPlace.setOnClickListener(onClickListener);
			spot.setOnClickListener(onClickListener);
			hotel.setOnClickListener(onClickListener);
			restaurant.setOnClickListener(onClickListener);
			shopping.setOnClickListener(onClickListener);
			entertrainment.setOnClickListener(onClickListener);
			
			move = (TextView) findViewById(R.id.move);
		}
		
		
		private OnClickListener onClickListener = new OnClickListener(){
			public void onClick(View v) {	
				int endLeft = 0;				
				boolean run = false;
				switch (v.getId()) {
				case R.id.place_all:
					placeCategoryID = ConstantField.ALL_PLACE_CATEGORY_ID;
					placeList = TravelUtil.getPlaceInDistance(placeDistance, application.getPlaceData(), location,placeCategoryID);
					if(listView.getVisibility() == View.VISIBLE)
					{
						adapter.setList(placeList);
						adapter.notifyDataSetChanged();
					}else
					{
						mapView.getOverlays().clear();
						placeLoaction = new PlaceLoaction( null, placeList);						
						if(placeList.size()!=0)
						{
							mapView.removeAllViews();
							mapView.getOverlays().add(placeLoaction);
							mapc.setCenter(placeLoaction.getCenter());	
							addPopupView();
							placeLoaction.setOnFocusChangeListener(changeListener);
						}
						mapView.postInvalidate();						
					}
					getStartPosition(tabStartPosition);
					endLeft = allPlace.getWidth()*0;
					move.setText("");
					run = true;
					tabStartPosition = 11;					
					break;
				case R.id.place_spot:
					placeCategoryID = PlaceCategoryType.PLACE_SPOT_VALUE;
					placeList = TravelUtil.getPlaceInDistance(placeDistance, application.getPlaceData(), location,placeCategoryID);
					if(listView.getVisibility() == View.VISIBLE)
					{
						adapter.setList(placeList);
						adapter.notifyDataSetChanged();
					}else
					{
						mapView.getOverlays().clear();
						placeLoaction = new PlaceLoaction( null, placeList);						
						if(placeList.size()!=0)
						{
							mapView.removeAllViews();
							mapView.getOverlays().add(placeLoaction);
							mapc.setCenter(placeLoaction.getCenter());
							addPopupView();		
							placeLoaction.setOnFocusChangeListener(changeListener);
						}	
						mapView.postInvalidate();
					}										
					getStartPosition(tabStartPosition);
					endLeft = allPlace.getWidth()*1;
					move.setText("");
					run = true;
					tabStartPosition = 12;
					break;
				case R.id.place_hotel:
					placeCategoryID = PlaceCategoryType.PLACE_HOTEL_VALUE;
					placeList = TravelUtil.getPlaceInDistance(placeDistance, application.getPlaceData(), location,placeCategoryID);
					if(listView.getVisibility() == View.VISIBLE)
					{
						adapter.setList(placeList);
						adapter.notifyDataSetChanged();
					}else
					{
						mapView.getOverlays().clear();
						placeLoaction = new PlaceLoaction( null, placeList);						
						if(placeList.size()!=0)
						{
							mapView.removeAllViews();
							mapView.getOverlays().add(placeLoaction);
							mapc.setCenter(placeLoaction.getCenter());		
							addPopupView();
							placeLoaction.setOnFocusChangeListener(changeListener);
						}	
						mapView.postInvalidate();
					}
					getStartPosition(tabStartPosition);
					endLeft = allPlace.getWidth()*2;
					move.setText("");
					run = true;
					tabStartPosition = 13;
					break;
				case R.id.place_restaurant:
					placeCategoryID = PlaceCategoryType.PLACE_RESTRAURANT_VALUE;
					placeList = TravelUtil.getPlaceInDistance(placeDistance, application.getPlaceData(), location,placeCategoryID);
					if(listView.getVisibility() == View.VISIBLE)
					{
						adapter.setList(placeList);
						adapter.notifyDataSetChanged();
					}else
					{
						mapView.getOverlays().clear();
						placeLoaction = new PlaceLoaction( null, placeList);						
						if(placeList.size()!=0)
						{
							mapView.removeAllViews();
							mapView.getOverlays().add(placeLoaction);
							mapc.setCenter(placeLoaction.getCenter());
							addPopupView();
							placeLoaction.setOnFocusChangeListener(changeListener);
						}	
						mapView.postInvalidate();
					}
					getStartPosition(tabStartPosition);
					endLeft = allPlace.getWidth()*3;
					move.setText("");
					run = true;
					tabStartPosition = 14;
					break;
				case R.id.place_shopping:
					placeCategoryID = PlaceCategoryType.PLACE_SHOPPING_VALUE;
					placeList = TravelUtil.getPlaceInDistance(placeDistance, application.getPlaceData(), location,placeCategoryID);
					if(listView.getVisibility() == View.VISIBLE)
					{
						adapter.setList(placeList);
						adapter.notifyDataSetChanged();
					}else
					{
						mapView.getOverlays().clear();
						placeLoaction = new PlaceLoaction( null, placeList);						
						if(placeList.size()!=0)
						{
							mapView.removeAllViews();
							mapView.getOverlays().add(placeLoaction);
							mapc.setCenter(placeLoaction.getCenter());	
							addPopupView();
							placeLoaction.setOnFocusChangeListener(changeListener);
						}	
						mapView.postInvalidate();
					}
					getStartPosition(tabStartPosition);
					endLeft = allPlace.getWidth()*4;
					move.setText("");
					run = true;
					tabStartPosition = 15;
					break;
				case R.id.place_entertrainment:
					placeCategoryID = PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE;
					placeList = TravelUtil.getPlaceInDistance(placeDistance, application.getPlaceData(), location,placeCategoryID);
					if(listView.getVisibility() == View.VISIBLE)
					{
						adapter.setList(placeList);
						adapter.notifyDataSetChanged();
					}else
					{
						mapView.getOverlays().clear();
						placeLoaction = new PlaceLoaction( null, placeList);						
						if(placeList.size()!=0)
						{
							mapView.removeAllViews();
							mapView.getOverlays().add(placeLoaction);
							mapc.setCenter(placeLoaction.getCenter());		
							addPopupView();
							placeLoaction.setOnFocusChangeListener(changeListener);
						}	
						mapView.postInvalidate();
					}
					getStartPosition(tabStartPosition);
					endLeft = allPlace.getWidth()*5;
					move.setText("");
					run = true;
					tabStartPosition = 16;
					break;
				case R.id.map_view:
					initMapView();
					break;
				case R.id.list_view:
					mapView.setVisibility(View.GONE);
					selectListViewButton.setVisibility(View.GONE);
					selectMapViewButton.setVisibility(View.VISIBLE);
					listView.setVisibility(View.VISIBLE);					
					adapter.setList(placeList);
					adapter.notifyDataSetChanged();					
					break;
				default:
					break;
				}
				
				if(run){
					TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
					animation.setDuration(300);
					animation.setFillAfter(true);
					move.bringToFront();
					move.startAnimation(animation);					
					move.setText(setEndPosition(startPosition));
				}
				
			}

		};
	
	private OnClickListener listener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			Animation animation = null;
			float endSet = 0;
			boolean run = false;
			switch (v.getId())
			{
			case R.id.start_button:
				getOffSet(redStart,startPosition);
				endSet = screenW*0.01f;
				startPosition = 0;
				run = true;
				placeDistance = 500;//ConstantField.HALF_KILOMETER;
				placeList = TravelUtil.getPlaceInDistance(placeDistance, application.getPlaceData(), location,placeCategoryID);
				if(listView.getVisibility() == View.VISIBLE)
				{
					adapter.setList(placeList);
					adapter.notifyDataSetChanged();
				}else
				{
					mapView.getOverlays().clear();
					placeLoaction = new PlaceLoaction( null, placeList);						
					if(placeList.size()!=0)
					{
						mapView.removeAllViews();
						mapView.getOverlays().add(placeLoaction);
						mapc.setCenter(placeLoaction.getCenter());		
						addPopupView();
						placeLoaction.setOnFocusChangeListener(changeListener);
					}
					mapView.postInvalidate();
					Log.d(TAG, "500m");
				}
				break;
			case R.id.one_km:
				getOffSet(redStart,startPosition);
				endSet = screenW*0.18f;											
				startPosition = 1;
				run = true;
				placeDistance = 1000;//ConstantField.ONE_KILOMETER;
				placeList = TravelUtil.getPlaceInDistance(placeDistance, application.getPlaceData(), location,placeCategoryID);
				if(listView.getVisibility() == View.VISIBLE)
				{
					adapter.setList(placeList);
					adapter.notifyDataSetChanged();
				}else
				{
					mapView.getOverlays().clear();
					placeLoaction = new PlaceLoaction( null, placeList);						
					if(placeList.size()!=0)
					{
						mapView.removeAllViews();
						mapView.getOverlays().add(placeLoaction);
						mapc.setCenter(placeLoaction.getCenter());							
						addPopupView();
						placeLoaction.setOnFocusChangeListener(changeListener);
					}				
					mapView.postInvalidate();
					Log.d(TAG, "1km");
				}
				break;
			case R.id.five_km:
				getOffSet(redStart,startPosition);
				endSet = screenW*0.43f;							
				startPosition = 2;
				run = true;
				placeDistance = 5000;//ConstantField.FIVE_KILOMETER;
				placeList = TravelUtil.getPlaceInDistance(placeDistance, application.getPlaceData(), location,placeCategoryID);
				if(listView.getVisibility() == View.VISIBLE)
				{
					adapter.setList(placeList);
					adapter.notifyDataSetChanged();
				}else
				{		
					mapView.getOverlays().clear();
					placeLoaction = new PlaceLoaction( null, placeList);						
					if(placeList.size()!=0)
					{
						mapView.removeAllViews();
						mapView.getOverlays().add(placeLoaction);
						mapc.setCenter(placeLoaction.getCenter());		
						addPopupView();
						placeLoaction.setOnFocusChangeListener(changeListener);
					}				
					mapView.postInvalidate();
					Log.d(TAG, "5km");
				}
				break;
			case R.id.ten_km:		
				getOffSet(redStart,startPosition);
				endSet = screenW*0.82f;
				startPosition = 3;
				run = true;
				placeDistance = 10000;//ConstantField.TEN_KILOMETER;
				placeList = TravelUtil.getPlaceInDistance(placeDistance, application.getPlaceData(), location,placeCategoryID);
				if(listView.getVisibility() == View.VISIBLE)
				{
					adapter.setList(placeList);
					adapter.notifyDataSetChanged();
				}else
				{
					
					mapView.getOverlays().clear();
					placeLoaction = new PlaceLoaction( null, placeList);						
					if(placeList.size()!=0)
					{
						mapView.removeAllViews();
						mapView.getOverlays().add(placeLoaction);
						mapc.setCenter(placeLoaction.getCenter());	
						addPopupView();
						placeLoaction.setOnFocusChangeListener(changeListener);
					}
					mapView.postInvalidate();
					Log.d(TAG, "10km");
				}
				break;
			default:
				break;
			}
			if(run)
			{
				animation = new TranslateAnimation(offset,endSet, 0, 0);
				animation.setDuration(700);		
				redStart.startAnimation(animation);
				animation.setFillAfter(true);
			}				
			
			
		}
	};
	
	
	private void getOffSet(ImageView imageView,int startPosition) {
	
	DisplayMetrics dm = new DisplayMetrics();
	getWindowManager().getDefaultDisplay().getMetrics(dm);
	screenW = dm.widthPixels;//
	switch (startPosition)
	{
	case 0:
		Log.d(TAG, "startposition = "+startPosition);
		offset = 0;
		break;
	case 1:
		offset = screenW*0.18f;
		break;
	case 2:
		offset = screenW*0.43f;
		break;
	case 3:
		offset = screenW*0.82f;
		break;
	default:
		break;
	}
	Matrix matrix = new Matrix();
	matrix.postTranslate(offset, 0);
	imageView.setImageMatrix(matrix);
	}
	
	
	private void getStartPosition(int tabStartPosition) {
	switch (tabStartPosition)
	{
	case 11:
		startLeft = allPlace.getWidth()*0;
		break;
	case 12:
		startLeft = allPlace.getWidth()*1;
		break;
	case 13:
		startLeft = allPlace.getWidth()*2;
		break;
	case 14:
		startLeft = allPlace.getWidth()*3;
		break;
	case 15:
		startLeft = allPlace.getWidth()*4;
		break;
	case 16:
		startLeft = allPlace.getWidth()*5;
		break;
	default:
		break;
	}
	}
	
	
	private String setEndPosition(int tabStatPosition) {
		String text = "";
		switch (tabStartPosition)
		{
		case 11:
			text = getString(R.string.all_place);
			break;
		case 12:
			text = getString(R.string.scenery);
			break;
		case 13:
			text = getString(R.string.hotel);
			break;
		case 14:
			text = getString(R.string.restaurant);
			break;
		case 15:
			text = getString(R.string.shopping);
			break;
		case 16:
			text = getString(R.string.entertainment);
			break;
		default:
			break;
		}
		return text;
	}
	
	
	 public void showRoundProcessDialog(Context mContext, int layout)
	    {
	        OnKeyListener keyListener = new OnKeyListener()
	        {
	            @Override
	            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
	            {
	            	if ( keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
	            		loadingDialog.dismiss();
	            		Intent intent = new Intent(NearbyActivity.this, IndexActivity.class);
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
	        loadingDialog.setContentView(layout);
	    }

	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}
	
	class PlaceLoaction extends ItemizedOverlay<OverlayItem>
	{
		private List<OverlayItem> placeOverlayItems = new ArrayList<OverlayItem>();
		private ArrayList<Place> places;
		
		
		public PlaceLoaction( Drawable marker,ArrayList<Place> placeList)
		{
			super(marker);
			int i=0;
			this.places = placeList;
			for (Place place:placeList)
			{			
				int icon = TravelUtil.getForecastImage(place.getCategoryId());
				Drawable markerIcon = getResources().getDrawable(icon);
				GeoPoint geoPoint = new GeoPoint((int)(place.getLatitude()*1e6),(int)(place.getLongitude()*1e6));
				OverlayItem overlayItem = new OverlayItem(geoPoint, place.getName(), Integer.toString(i));
				markerIcon.setBounds(0, 0, markerIcon.getIntrinsicWidth(), markerIcon.getIntrinsicHeight());
				overlayItem.setMarker(markerIcon);
				placeOverlayItems.add(overlayItem);
				i++;
			}
			populate();
		}

		@Override
		protected OverlayItem createItem(int i)
		{
			return placeOverlayItems.get(i);
		}

		@Override
		public int size()
		{
			return placeOverlayItems.size();
		}

		public  void removeAllItems()
		{
			for(int i=0;i<places.size();i++)
			{
				placeOverlayItems.remove(i);
			}
			
		}
		
		
		
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
				MapView.LayoutParams geoLP =  (LayoutParams) popupView.getLayoutParams();
				geoLP.point = newFocus.getPoint();
				TextView titleView = (TextView) popupView.findViewById(R.id.map_bubbleTitle);
				titleView.setText(newFocus.getTitle());
				popupView.setTag(newFocus.getSnippet());
				mapView.updateViewLayout(popupView, geoLP);
				popupView.setVisibility(View.VISIBLE);
			}
		}
	};
	
	
	private OnClickListener popupViewOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			String position = v.getTag().toString();
			application.setPlace(placeList.get(Integer.parseInt(position)));
			Intent intent = new Intent();
			switch (placeList.get(Integer.parseInt(position)).getCategoryId())
			{
			case PlaceCategoryType.PLACE_SPOT_VALUE:
				intent.setClass(NearbyActivity.this, SceneryDetailActivity.class);
				break;
			case PlaceCategoryType.PLACE_HOTEL_VALUE:
				intent.setClass(NearbyActivity.this, HotelDetailActivity.class);
				break;
			case PlaceCategoryType.PLACE_RESTRAURANT_VALUE:
				intent.setClass(NearbyActivity.this, RestaurantDetailActivity.class);
				break;
			case PlaceCategoryType.PLACE_SHOPPING_VALUE:
				intent.setClass(NearbyActivity.this, ShoppingDetailActivity.class);
				break;
			case PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE:
				intent.setClass(NearbyActivity.this, EntertainmentDetailActivity.class);
				break;
			default:
				break;
			}			
			startActivity(intent);
			popupView.setVisibility(View.GONE);
		}
		};
		
		private OnItemClickListener itemClickListener = new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				Place place = placeList.get(arg2);
				application.setPlace(place);
				Intent intent = new Intent();
				switch (place.getCategoryId())
				{
				case PlaceCategoryType.PLACE_SPOT_VALUE:
					intent.setClass(NearbyActivity.this, SceneryDetailActivity.class);
					break;
				case PlaceCategoryType.PLACE_HOTEL_VALUE:
					intent.setClass(NearbyActivity.this, HotelDetailActivity.class);
					break;
				case PlaceCategoryType.PLACE_RESTRAURANT_VALUE:
					intent.setClass(NearbyActivity.this, RestaurantDetailActivity.class);
					break;
				case PlaceCategoryType.PLACE_SHOPPING_VALUE:
					intent.setClass(NearbyActivity.this, ShoppingDetailActivity.class);
					break;
				case PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE:
					intent.setClass(NearbyActivity.this, EntertainmentDetailActivity.class);
					break;
				default:
					break;
				}			
				startActivity(intent);
	
			}
		};
	
}
