/**  
        * @title NearbyPlaceActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-12 下午12:48:32  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.ActivityGroup;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html.ImageGetter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.NearbyPlaceAdapter;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.PlaceGoogleMap;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.location.LocationUtil;
import com.damuzhi.travel.activity.common.mapview.CommonItemizedOverlay;
import com.damuzhi.travel.activity.common.mapview.CommonOverlayItem;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.entry.MainActivity;
import com.damuzhi.travel.activity.more.FeedBackActivity;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.TravelUtil.ComparatorDistance;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class CommonNearbyPlaceActivity extends ActivityGroup
{
	private static final String TAG = "Nearby";
	private ImageButton startButton;
	private ImageButton oneKMbutton;
	private ImageButton fiveKMButtom;
	private ImageButton tenKMButton;
	private ImageView redStart;
	private int startPosition = 1;
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
	private ProgressDialog loadingDialog;
	ArrayList<Place> placeList = new ArrayList<Place>();;
	private HashMap<String, Double> location;
	private NearbyPlaceAdapter adapter;
	//private View popupView;//
	private ViewGroup mapViewGroup;
	long lasttime = -1;
  
    private ImageButton modelButton;
    private TextView modelTextView;
    private String currentDistance ="";
    private String currentPlaceCategory = ConstantField.NEARBY_ALL;
   // private LocationClient mLocClient;
    private int model = 1;//1== map,2= list
    private View listViewFooter;
	private ViewGroup footerViewGroup;
	private static int start = 0;
	private static int count = 1;
	private int totalCount = 0;
	private boolean loadDataFlag = false;
	private boolean addFooterViewFlag = false;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		ActivityMange.getInstance().addActivity(this);
		setContentView(R.layout.common_nearby_place);
		currentDistance = ConstantField.HALF_KILOMETER;
		currentPlaceCategory = ConstantField.NEARBY_ALL;
		loadingDialog = new ProgressDialog(this);
		init();
		loadPlace();		
		
	}
	
	public void init()
	{
		listView = (ListView) findViewById(R.id.nearby_list);
		mapViewGroup = (ViewGroup) findViewById(R.id.mapview_group);
	
		
		modelButton = (ImageButton) findViewById(R.id.model_button);
		modelTextView = (TextView) findViewById(R.id.model_text);
		modelButton.setOnClickListener(modelOnClickListener);

		listView.setOnItemClickListener(listviewOnItemClickListener);
		
		
		redStart = (ImageView) findViewById(R.id.position);
		startButton = (ImageButton) findViewById(R.id.start_button);
		oneKMbutton = (ImageButton) findViewById(R.id.one_km);
		fiveKMButtom = (ImageButton) findViewById(R.id.five_km);
		tenKMButton = (ImageButton) findViewById(R.id.ten_km);	
		
		startButton.setOnClickListener(fiveOnClickListener);
		fiveKMButtom.setOnClickListener(fiveKmOnClickListener);
		oneKMbutton.setOnClickListener(oneKmOnClickListener);
		tenKMButton.setOnClickListener(tenKmOnClickListener);
		
		
		
		allPlace = (TextView) findViewById(R.id.place_all);
		spot = (TextView) findViewById(R.id.place_spot);
		hotel = (TextView) findViewById(R.id.place_hotel);
		restaurant = (TextView) findViewById(R.id.place_restaurant);
		shopping = (TextView) findViewById(R.id.place_shopping);
		entertrainment = (TextView) findViewById(R.id.place_entertrainment);	
		
		allPlace.setOnClickListener(allPlaceOnClickListener);
		spot.setOnClickListener(spotOnClickListener);
		hotel.setOnClickListener(hotelOnClickListener);
		restaurant.setOnClickListener(restaurantOnClickListener);
		shopping.setOnClickListener(shoppingOnClickListener);
		entertrainment.setOnClickListener(entertrainmnetOnClickListener);			
		move = (TextView) findViewById(R.id.move);
		move.setTextColor(getResources().getColor(R.color.white));
		
		
		listViewFooter = getLayoutInflater().inflate(R.layout.load_more_view, null, false);
		listView.addFooterView(listViewFooter);
		//listView.setFooterDividersEnabled(false);
		footerViewGroup = (ViewGroup) listViewFooter.findViewById(R.id.listView_load_more_footer);
		footerViewGroup.setVisibility(View.GONE);
		
	//	popupView = LayoutInflater.from(this).inflate(R.layout.overlay_popup, null);
		adapter = new NearbyPlaceAdapter(this, placeList);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(listviewOnScrollListener);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenW = dm.widthPixels;
		//getMyLocation();
	}

	
	
	
	public void loadPlace()
	{
		AsyncTask<String, Void, List<Place>> task = new AsyncTask<String, Void, List<Place>>()
		{

			@Override
			protected List<Place> doInBackground(String... params)
			{
				getMyLocation();
				List<Place> resultList = PlaceMission.getInstance().getNearbyInDistance(location, currentDistance,start,currentPlaceCategory);
				return resultList;
			}

			@Override
			protected void onCancelled()
			{
				super.onCancelled();
			}

			@Override
			protected void onPostExecute(List<Place> resultList)
			{
				loadingDialog.dismiss();
				if(resultList != null){
					placeList.clear();
					placeList.addAll(resultList);
				}
				if(addFooterViewFlag){
					listView.addFooterView(footerViewGroup, placeList, false);	
					addFooterViewFlag = false;
				}
				
				refreshPlaceView();
				super.onPostExecute(resultList);
			}

			@Override
			protected void onPreExecute()
			{
				showRoundProcessDialog();
				loadDataFlag = true;
				super.onPreExecute();
			}

		};

		task.execute();
		
	}
	
	
	private void loadMore()
	{
		AsyncTask<String, Void, List<Place>> task = new AsyncTask<String, Void, List<Place>>()
		{

			@Override
			protected List<Place> doInBackground(String... params)
			{		
				loadDataFlag = false;
				start = count *20;
				//Log.d(TAG, "load more start from = "+start);
				count++;		
				return PlaceMission.getInstance().getNearbyInDistance(location, currentDistance,start,currentPlaceCategory);
			}
			@Override
			protected void onPostExecute(List<Place> resultList)
			{
				loadDataFlag = true;
				addMoreData(resultList);
				footerViewGroup.setVisibility(View.GONE);
				super.onPostExecute(resultList);
			}			
		};

		task.execute();		
	}
	
	
	
	
	
	
	private void refreshPlaceView()
	{
		ComparatorDistance comparatorDistance = new ComparatorDistance(location);
		Collections.sort(placeList, comparatorDistance);
		if(listView.getVisibility() == View.VISIBLE){
			adapter.setList(placeList);
			adapter.notifyDataSetChanged();
		}else
		{	
			goMapView(placeList);
		}
		updateTitle();
		if(placeList.size()>0)
		{
			findViewById(R.id.page).setVisibility(View.VISIBLE);					
		}else
		{
			findViewById(R.id.data_not_found).setVisibility(View.VISIBLE);
		}
	}
	
	
	private void addMoreData(List<Place> list)
	{
		if(list != null){
			Log.d(TAG, "load more nearby place list size = "+list.size());
			placeList.addAll(list);
			ComparatorDistance comparatorDistance = new ComparatorDistance(location);
			Collections.sort(placeList, comparatorDistance);
			if(listView.getVisibility() == View.VISIBLE){
				//adapter.addPlaceList(list);
				//Log.d(TAG, "after nearby list add load more data  list size = "+adapter.getCount());
				//adapter.setList(list);
				adapter.notifyDataSetChanged();
			}else {
				goMapView(placeList);
			}
			updateTitle();
		}		
	}
	
	
	
	private void updateTitle()
	{
		footerViewGroup.setVisibility(View.INVISIBLE);
		TextView placeSize = (TextView) findViewById(R.id.place_num);		 
		totalCount = PlaceMission.getInstance().getPlaceTotalCount();
		String sizeString = "(" + totalCount+ ")";
		placeSize.setText(sizeString);
	}
	
	
	private int visibleLastIndex = 0;
	private OnScrollListener listviewOnScrollListener = new OnScrollListener()
	{	
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			Log.d(TAG, "onScrollStateChanged");
			if(!loadDataFlag)
			{
				return ;
			}
			if(totalCount !=0 && visibleLastIndex == totalCount)
			{
			   listView.removeFooterView(listViewFooter);	
			   addFooterViewFlag = true;
			   //footerViewGroup.setVisibility(View.INVISIBLE);
				//loadDataFlag = false;
			}
			Log.d(TAG, "visibleLastIndex = "+visibleLastIndex);
			if(visibleLastIndex >0)
			{	
				footerViewGroup.setVisibility(View.VISIBLE);
			  int size = adapter.getCount();	
			  if(scrollState ==OnScrollListener.SCROLL_STATE_IDLE &&visibleLastIndex == size)
			  {
				  Log.d(TAG, "load more");
				 // Log.d(TAG, "listview visibleLastIndex = "+visibleLastIndex);	  
				  loadMore();
			  } 
			}
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount)
		{
			visibleLastIndex = firstVisibleItem + visibleItemCount -1;
		}
	};
	

	 
	 private OnClickListener fiveOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			if(startPosition !=0)
			{
				currentDistance = ConstantField.TWO_HUNDRED_AND_FIFTY;	
				float endSet = screenW*-0.12f;
				if(startPosition == 2)
				{
					offset = offset - screenW*0.05f;
				}
				if(startPosition == 3)
				{
					offset = offset - screenW*0.15f;
				}
				changeDistance(endSet);
				startPosition = 0;			
			}
			

		}
	};
	
	 private OnClickListener oneKmOnClickListener = new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(startPosition != 1)
				{	
					currentDistance = ConstantField.HALF_KILOMETER;	
					float endSet = screenW*0f;		
					if(startPosition == 2)
					{
						offset = offset - screenW*0.05f;
					}
					if(startPosition == 3)
					{
						offset = offset - screenW*0.15f;
					}
					changeDistance(endSet);
					startPosition = 1;
				}
				
				
			}
		};
	 
		
		 private OnClickListener fiveKmOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					if(startPosition != 2)
					{
						currentDistance = ConstantField.ONE_KILOMETER;
						float endSet = screenW*0.18f;	
						if(startPosition>2)
						{
							offset = offset - screenW*0.15f;
						}
						changeDistance(endSet);
						startPosition = 2;					
					}
					
				}
			};
			
			
			
		 private OnClickListener tenKmOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					if(startPosition != 3)
					{
						currentDistance = ConstantField.FIVE_KILOMETER;	
						float endSet = screenW*0.69f;
						changeDistance(endSet);
						startPosition = 3;	
					}
					
				}
			};
		
		private void changeDistance(float endSet)
		{
			start = 0;
			count = 1;
			getOffSet(redStart,startPosition);
			Animation animation = null;
			animation = new TranslateAnimation(offset,endSet, 0, 0);
			animation.setDuration(500);		
			redStart.startAnimation(animation);
			animation.setFillAfter(true);
			loadPlace();
		}	
				
		 private OnClickListener allPlaceOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					currentPlaceCategory = ConstantField.NEARBY_ALL;
					loadPlace();
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*0;					
					tabStartPosition = 11;
					changePlaceCategory(endLeft);
				}
			};
			
			private OnClickListener spotOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					currentPlaceCategory = ConstantField.NEARBY_SPOT;
					loadPlace();
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*1;					
					tabStartPosition = 12;
					changePlaceCategory(endLeft);
				}
			};
			
			private OnClickListener hotelOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					currentPlaceCategory = ConstantField.NEARBY_HOTEL;
					loadPlace();
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*2;				
					tabStartPosition = 13;	
					changePlaceCategory(endLeft);
				}
			};
			
			private OnClickListener restaurantOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					currentPlaceCategory = ConstantField.NEARBY_RESTAURANT;
					loadPlace();
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*3;					
					tabStartPosition = 14;	
					changePlaceCategory(endLeft);
				}
			};
			
			
			private OnClickListener shoppingOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					currentPlaceCategory = ConstantField.NEARBY_SHOPPING;
					loadPlace();
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*4;			
					tabStartPosition = 15;
					changePlaceCategory(endLeft);
						
				}
			};
			
			
			private OnClickListener entertrainmnetOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					
					currentPlaceCategory = ConstantField.NEARBY_ENTERTRAINMENT;
					loadPlace();					
					float endLeft = allPlace.getWidth()*5;				
					tabStartPosition = 16;
					changePlaceCategory(endLeft);
						
				}
			};
	 
		
			private void  changePlaceCategory(float endLeft)
			{
				start = 0;
				count = 1;
				listView.setSelection(0);
				getStartPosition(tabStartPosition);
				move.setText("");
				TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
				animation.setDuration(300);
				animation.setFillAfter(true);
				move.bringToFront();
				move.startAnimation(animation);					
				move.setText(setEndPosition());
			}
			
			
			
	private OnClickListener modelOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			if(model == 1)
			{
				try{
				 	Class.forName("com.google.android.maps.MapActivity");
					modelTextView.setText(getString(R.string.list));
					listView.setVisibility(View.GONE);
					mapViewGroup.setVisibility(View.VISIBLE);
					findViewById(R.id.mapview_group).setVisibility(View.VISIBLE);
					goMapView(placeList);
					model = 2;
				 }catch(Exception  e) {
		                Toast.makeText(CommonNearbyPlaceActivity.this, R.string.google_map_not_found2, Toast.LENGTH_LONG).show();
		            }
			}else
			{
				modelTextView.setText(getString(R.string.map));
				mapViewGroup.setVisibility(View.GONE);
				mapViewGroup.removeAllViews();
				listView.setVisibility(View.VISIBLE);					
				adapter.setList(placeList);
				adapter.notifyDataSetChanged();	
				model = 1;
			}
			
		}
	};		
	
	
	private void goMapView(List<Place> list) {
		if(list!=null)
		{
			
			 	PlaceList.Builder placeList = PlaceList.newBuilder();
			 	placeList.addAllList(list);
			 	Intent intent = new Intent(CommonNearbyPlaceActivity.this, PlaceGoogleMap.class);
			 	intent.putExtra(ConstantField.NEARBY_GOOGLE_MAP, placeList.build().toByteArray());
			 	mapViewGroup.removeAllViews();
			 	mapViewGroup.addView(getLocalActivityManager().startActivity(ConstantField.NEARBY_GOOGLE_MAP,intent).getDecorView());
           
		}	
	}
			
	
	
	private OnItemClickListener listviewOnItemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			List<Place> list = adapter.getPlaceList();
			Place place = list.get(arg2);
			Intent intent = new Intent();
			intent.putExtra(ConstantField.PLACE_DETAIL, place.toByteArray());
			Class detailPlaceClass = CommonPlaceDetailActivity.getClassByPlaceType(place.getCategoryId());
			intent.setClass(CommonNearbyPlaceActivity.this, detailPlaceClass);
			startActivity(intent);
			
		}
	};
			
	
	private void getMyLocation()
	{
		checkGPSisOpen();		
		int i=0;
		while (location==null||location.size()==0)
		{
			LocationUtil.getInstance().getLocation(CommonNearbyPlaceActivity.this);
			location = TravelApplication.getInstance().getLocation();
			
		}
		
		/*if(mLocClient !=null)
		{
			mLocClient.stop();
		}*/				
	}
	
	

	
	private void getOffSet(ImageView imageView,int startPosition) {
		switch (startPosition)
		{
			case 0:
				offset = screenW*-0.12f;
				break;
			case 1:
				offset = screenW*0f;
				break;
			case 2:
				offset = screenW*0.23f;
				break;
			case 3:
				offset = screenW*0.84f;
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
	
	
	private String setEndPosition() {
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
	
	
	public void showRoundProcessDialog()
	{

		OnKeyListener keyListener = new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0)
				{
					loadingDialog.dismiss();
					Intent intent = new Intent(CommonNearbyPlaceActivity.this,MainActivity.class);
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
		loadingDialog.setCancelable(true);
		loadingDialog.setOnKeyListener(keyListener);
		loadingDialog.show();
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
		adapter.recycleBitmap();
		LocationUtil.stop();
		if(loadingDialog  != null)
		{
			loadingDialog.dismiss();
		}
		ActivityMange.getInstance().finishActivity();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		//TravelApplication.getInstance().addActivity(this);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent  intent;
		switch (item.getItemId())	
		{		
		case R.id.menu_refresh:
			loadPlace();
			break;
		case R.id.menu_help:
			intent = new Intent();
			intent.putExtra(ConstantField.HELP_TITLE, getResources().getString(R.string.help));
			intent.setClass(CommonNearbyPlaceActivity.this, HelpActiviy.class);
			startActivity(intent);
			break;
		case R.id.menu_feedback:
			intent = new Intent();			
			intent.setClass(CommonNearbyPlaceActivity.this, FeedBackActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_about:
			intent = new Intent();
			String about = getResources().getString(R.string.about_damuzhi);
			intent.putExtra(ConstantField.HELP_TITLE, about);
			intent.setClass(CommonNearbyPlaceActivity.this, HelpActiviy.class);
			startActivity(intent);
			break;
		case R.id.menu_exit:
			//TravelApplication.getInstance().exit();
			ActivityMange.getInstance().AppExit(CommonNearbyPlaceActivity.this);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
