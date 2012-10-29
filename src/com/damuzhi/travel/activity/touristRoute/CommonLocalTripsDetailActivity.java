package com.damuzhi.travel.activity.touristRoute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.PlaceImageAdapter;
import com.damuzhi.travel.activity.adapter.touristRoute.CommonRouteFeedbackAdapter;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.DepartPlaceMap;
import com.damuzhi.travel.activity.common.NearbyPlaceMap;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.AsyncLoader;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.activity.place.CommonPlaceDetailActivity;
import com.damuzhi.travel.mission.favorite.FavoriteMission;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.mission.touristRoute.TouristRouteMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.favorite.FavoriteManager;
import com.damuzhi.travel.model.place.PlaceManager;
import com.damuzhi.travel.protos.PackageProtos.RouteFeekback;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.TouristRouteProtos.DepartPlace;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRoute;
import com.damuzhi.travel.util.TravelUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.weibo.api.Tag_API;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class CommonLocalTripsDetailActivity extends Activity
{

	protected static final String TAG = "CommonLocalTripsDetailActivity";
	private ImageButton routeIntroButton;
	private ImageButton bookingNoticeButton;
	private ImageButton routeFeedBackButton;
	private TextView routeNameTextView;
	private TextView routeIdTextView;
	private TextView agencyNameTextView;
	private TextView routeIntroTextView;
	private TextView bookingNoticeTextView;
	private TextView routeFeedBackTextView;
	private TextView routePriceTextView;
	private Button consultButton;
	private Button bookOrderImageView;
	private ViewGroup routeIntroViewGroup;
	private WebView routeDetailWebView;
	private WebView bookingNoticeWebView;
	private AsyncLoader asyncLoader;
	private ImageView[] imageViews;
	private LocalRoute localRoute;
	private HashMap<Integer, DepartPlace> departPlaceHashMap;
	private ViewGroup routeFeedbackGroup;
	private CommonRouteFeedbackAdapter adapter;
	private ListView routeFeedbackListView;
	private TextView noDataTextView;
	private boolean isFollow = false;
	private ProgressBar loadingBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActivityMange.getInstance().addActivity(this);
		setContentView(R.layout.common_local_trips_detail);
		
		int localRouteId = getIntent().getIntExtra("local_route",0);
		
		loadData(localRouteId);
		
	}
	
	
	private void loadData(final int localRouteId)
	{
		AsyncTask<Void, Void, LocalRoute> asyncTask = new AsyncTask<Void, Void, LocalRoute>()
		{

			@Override
			protected LocalRoute doInBackground(Void... params)
			{
				return TouristRouteMission.getInstance().getLocalRouteDetail(localRouteId);
			}

			@Override
			protected void onPostExecute(LocalRoute result)
			{
				localRoute = result;
				refresh();
				super.onPostExecute(result);
			}
		};
		asyncTask.execute();
	}
	
	
	private void refresh()
	{
		departPlaceHashMap = new HashMap<Integer, DepartPlace>();
		if(localRoute != null)
		{
			if(localRoute.getDepartPlacesCount()>0)
			{
				for(DepartPlace departPlace:localRoute.getDepartPlacesList())
				{
					Log.d(TAG, "depart place id = " +departPlace.getDepartPlaceId());
					departPlaceHashMap.put(departPlace.getDepartPlaceId(), departPlace);
				}
			}
			List<String> imagePath = localRoute.getDetailImagesList();
			LayoutInflater inflater = getLayoutInflater();
			ArrayList<View> imageViewlist = new ArrayList<View>();	
			//asyncLoader = AsyncLoader.getInstance();
			asyncLoader = new AsyncLoader();
			int size=imagePath.size();	
			for(int i=0;i<size;i++)
			{
				View view = inflater.inflate(R.layout.place_detail_image, null);
				/*ImageView imageView = (ImageView) view.findViewById(R.id.place_image_item);
				asyncLoader.showimgAnsy(imageView, imagePath.get(i));*/
				imageViewlist.add(view);
			}
			imageViews = new ImageView[size];	
			ViewGroup main = (ViewGroup) inflater.inflate(R.layout.common_local_trips_detail, null);
			ViewGroup group = (ViewGroup) main.findViewById(R.id.place_images_group);
			ViewPager routeViewPager = (ViewPager) main.findViewById(R.id.route_view_pager);
			ImageView imageView;
			for (int i = 0; i < size; i++) {  
	            imageView = new ImageView(CommonLocalTripsDetailActivity.this);  
	            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
	            params.setMargins((int)getResources().getDimension(R.dimen.image_margin), 0, (int)getResources().getDimension(R.dimen.image_margin), 0);
	            imageView.setLayoutParams(params);  
	            imageViews[i] = imageView;  
	            if (i == 0) {    
	                imageViews[i].setBackgroundResource(R.drawable.guide_dot_white);  
	            } else {  
	                imageViews[i].setBackgroundResource(R.drawable.guide_dot_black);  
	            }  
	            group.addView(imageView);  
	        } 
		    PlaceImageAdapter routeImageAdapter = new PlaceImageAdapter(imageViewlist);	
		    routeViewPager.setAdapter(routeImageAdapter);
		    routeViewPager.setOnPageChangeListener(routeImageOnPageChangeListener);
		    
			setContentView(main);
			int cityId = AppManager.getInstance().getCurrentCityId();
			String num = "编号：";
			routeIntroButton = (ImageButton) findViewById(R.id.route_introduce);
			bookingNoticeButton = (ImageButton) findViewById(R.id.booking_notice);
			routeFeedBackButton = (ImageButton) findViewById(R.id.route_feedback);
			consultButton = (Button) findViewById(R.id.consult_button);
			routeIntroButton.setSelected(true);
			routeIdTextView = (TextView) findViewById(R.id.route_id);
			routeNameTextView = (TextView) findViewById(R.id.route_name);
			agencyNameTextView = (TextView) findViewById(R.id.agency_name);
			routeNameTextView.setText(localRoute.getName());
			routeNameTextView.setSelected(true);
			routeIdTextView.setText(num+localRoute.getRouteId());
			agencyNameTextView.setText(AppManager.getInstance().getAgencyShortNameById(localRoute.getAgencyId()));
			agencyNameTextView.setSelected(true);
			//String price = AppManager.getInstance().getSymbolByCityId(cityId)+localRoute.getPrice();
			
			routeIntroTextView = (TextView) findViewById(R.id.route_intro_text);
			bookingNoticeTextView = (TextView) findViewById(R.id.booking_notice_text);
			routeFeedBackTextView = (TextView) findViewById(R.id.route_feedback_text);
			routePriceTextView = (TextView) findViewById(R.id.route_price);
			bookOrderImageView = (Button) findViewById(R.id.book_order);
			routePriceTextView.setText(localRoute.getCurrency()+localRoute.getPrice());		
			routeIntroTextView.setTextColor(getResources().getColor(R.color.white));
			loadingBar = (ProgressBar) findViewById(R.id.loading_progress);
			routeDetailWebView = (WebView) findViewById(R.id.route_detail_webview);
			bookingNoticeWebView = (WebView) findViewById(R.id.booking_notice_webview);
			routeIntroViewGroup = (ViewGroup) findViewById(R.id.route_intro_group);
			routeFeedbackGroup = (ViewGroup) findViewById(R.id.route_feedback_group);
			routeFeedbackListView = (ListView) findViewById(R.id.route_feedback_listview);
			noDataTextView = (TextView) findViewById(R.id.no_data);
			bookingNoticeWebView.loadUrl(localRoute.getBookingNotice());
			routeIntroButton.setOnClickListener(routeIntroOnClickListener);
			bookingNoticeButton.setOnClickListener(bookingNoticeOnClickListener);
			routeFeedBackButton.setOnClickListener(routeFeedbackOnClickListener);
			bookOrderImageView.setOnClickListener(bookOrderOnClickListener);
			consultButton.setOnClickListener(consultOnClickListener);
			routeDetailWebView.setWebViewClient(webViewClient);
			routeDetailWebView.getSettings().setJavaScriptEnabled(true);
			routeDetailWebView.loadUrl(localRoute.getDetailUrl());
			routeDetailWebView.setVisibility(View.GONE);
			isFollow = checkFavoriteRoute(localRoute.getRouteId());
			//Log.d(TAG, "route is follow = "+isFollow);
			for(int i=0;i<size;i++)
			{
				View view = imageViewlist.get(i);
				ImageView imageView2 = (ImageView) view.findViewById(R.id.place_image_item);
				asyncLoader.showimgAnsy(imageView2, imagePath.get(i));
			}
			loadRouteFeedback();
		}
				
		
	}
	
	
	
	private OnPageChangeListener routeImageOnPageChangeListener  = new OnPageChangeListener()
	{
		
		@Override
		public void onPageSelected(int arg0)
		{
			
			
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0].setBackgroundResource(R.drawable.guide_dot_white);
				if (arg0 != i) {
					imageViews[i].setBackgroundResource(R.drawable.guide_dot_black);
				}
			}
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0)
		{
			// TODO Auto-generated method stub		
		}
		
	};
	
	private OnClickListener routeIntroOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			v.setSelected(true);
			bookingNoticeButton.setSelected(false);
			routeFeedBackButton.setSelected(false);
			routeIntroTextView.setTextColor(getResources().getColor(R.color.white));
			bookingNoticeTextView.setTextColor(getResources().getColor(R.color.black));
			routeFeedBackTextView.setTextColor(getResources().getColor(R.color.black));
			if(routeIntroViewGroup.getVisibility() == View.GONE)
			{
				bookingNoticeWebView.setVisibility(View.GONE);
				routeFeedbackGroup.setVisibility(View.GONE);
				routeIntroViewGroup.setVisibility(View.VISIBLE);	
			}
		}
	};
	
	private OnClickListener bookingNoticeOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			v.setSelected(true);
			routeIntroButton.setSelected(false);
			routeFeedBackButton.setSelected(false);
			bookingNoticeTextView.setTextColor(getResources().getColor(R.color.white));
			routeFeedBackTextView.setTextColor(getResources().getColor(R.color.black));
			routeIntroTextView.setTextColor(getResources().getColor(R.color.black));
			routeIntroViewGroup.setVisibility(View.GONE);
			if(bookingNoticeWebView.getVisibility() == View.GONE)
			{
				bookingNoticeWebView.setVisibility(View.VISIBLE);
				routeIntroViewGroup.setVisibility(View.GONE);
				routeFeedbackGroup.setVisibility(View.GONE);
			}
			
		}
	};
	
	private OnClickListener routeFeedbackOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			v.setSelected(true);
			bookingNoticeButton.setSelected(false);
			routeIntroButton.setSelected(false);
			routeFeedBackTextView.setTextColor(getResources().getColor(R.color.white));
			routeIntroTextView.setTextColor(getResources().getColor(R.color.black));
			bookingNoticeTextView.setTextColor(getResources().getColor(R.color.black));
			
			if(routeFeedbackGroup.getVisibility() == View.GONE)
			{
				bookingNoticeWebView.setVisibility(View.GONE);
				routeIntroViewGroup.setVisibility(View.GONE);
				routeFeedbackGroup.setVisibility(View.VISIBLE);
			}
			
		}
	};
	
	
	private OnClickListener bookOrderOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("booking", localRoute.toByteArray());
			intent.setClass(CommonLocalTripsDetailActivity.this, CommonBookingRouteActivity.class);
			startActivity(intent);
		}
	};
	
	
	
	private OnClickListener consultOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String customerServiceTelephone = localRoute.getCustomerServiceTelephone();
			makePhoneCall(customerServiceTelephone);
			
		}
	};
	
	private void loadRouteFeedback()
	{
		AsyncTask<Void, Void, List<RouteFeekback>> asyncTask = new AsyncTask<Void, Void, List<RouteFeekback>>()
		{

			@Override
			protected List<RouteFeekback> doInBackground(Void... params)
			{
				return TouristRouteMission.getInstance().getRouteFeedBacks(localRoute.getCityId(),localRoute.getRouteId());
			}

			@Override
			protected void onPostExecute(List<RouteFeekback> result)
			{
				refreshRouteFeedback(result);
				super.onPostExecute(result);
			}
		};
		asyncTask.execute();
	}
	
	private void refreshRouteFeedback(List<RouteFeekback> routeFeekbacks)
	{
		adapter = new CommonRouteFeedbackAdapter(CommonLocalTripsDetailActivity.this, routeFeekbacks);
		routeFeedbackListView.setAdapter(adapter);
		if(adapter.getCount()>0)
		{
			noDataTextView.setVisibility(View.GONE);
		}
	}
	
	
	
	
	public void makePhoneCall( final String phoneNumber)
	{
		AlertDialog phoneCall = new AlertDialog.Builder(CommonLocalTripsDetailActivity.this).create();
		View view = getLayoutInflater().inflate(R.layout.alert_dialog, null);
		TextView messageTextView = (TextView) view.findViewById(R.id.message);
		messageTextView.setText(phoneNumber);
		phoneCall.setTitle(getString(R.string.make_phone_call));
		//phoneCall.setMessage(phoneNumber);
		phoneCall.setView(view);
		phoneCall.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.call),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+phoneNumber));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				CommonLocalTripsDetailActivity.this.startActivity(intent);
			}
		} );
		phoneCall.setButton(DialogInterface.BUTTON_NEGATIVE,""+getBaseContext().getString(R.string.cancel),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				
			}
		} );
		phoneCall.show();
		
	}
	
	
	
	
	private WebViewClient webViewClient = new WebViewClient(){

		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			super.onPageStarted(view, url, favicon);
			//Log.d(TAG, "page start url = "+url);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			Log.d(TAG, "OVER RIDE URL = "+url);					
			if(url.contains("type=DepartPlace"))
			{
				Log.d(TAG, "depart place ");
				int placeId = Integer.valueOf(url.substring(url.lastIndexOf("=")+1));	
				Log.d(TAG, "place id = "+placeId);
				if(departPlaceHashMap.containsKey(placeId))
				{
					DepartPlace departPlace = departPlaceHashMap.get(placeId);
					Log.d(TAG, "get location ");
					try {
					 	Class.forName("com.google.android.maps.MapActivity");
					 	openGPSSettings();
					 	Intent intent = new Intent();
						intent.setClass(CommonLocalTripsDetailActivity.this,DepartPlaceMap.class);
						intent.putExtra(ConstantField.DEPART_PLACE, departPlace.toByteArray());					
						startActivity(intent);
			        }catch(Exception  e) {
			            (Toast.makeText(CommonLocalTripsDetailActivity.this, getString(R.string.google_map_not_found1), Toast.LENGTH_LONG)).show();
			        }
				}
			}	
			if(url.contains("type=RelatedPlace"))
			{
				String placeId = url.substring(url.lastIndexOf("=")+1);
				Log.d(TAG, "local route relate placeId = "+placeId);
				Place place = PlaceMission.getInstance().getPlaceById(placeId);
				Intent intent = new Intent();
				//intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
				intent.putExtra(ConstantField.PLACE_DETAIL, place.toByteArray());
				Class detailPlaceClass = CommonPlaceDetailActivity.getClassByPlaceType(place.getCategoryId());
				intent.setClass(CommonLocalTripsDetailActivity.this, detailPlaceClass);
				startActivity(intent);
				detailPlaceClass = null;
			}
			if(url.contains("type=FollowRoute")&&!isFollow)
			{
				Log.d(TAG, "follow route");
				addFavoriteRoute(localRoute);
				routeDetailWebView.loadUrl("javascript:toggleFavor(true)");
				
			}				
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			super.onPageFinished(view, url);
			loadingBar.setVisibility(View.GONE);
			routeDetailWebView.setVisibility(View.VISIBLE);
			if(isFollow)
			{
				routeDetailWebView.loadUrl("javascript:toggleFavor(true)");
				Log.d(TAG, "route has follow");
			}
		}
		
	};
	
	
	private void addFavoriteRoute(LocalRoute localRoute)
	{
		String userId = UserManager.getInstance().getUserId(CommonLocalTripsDetailActivity.this);
		String loginId = TravelApplication.getInstance().getLoginID();
		String token = TravelApplication.getInstance().getToken();
		int routeId = localRoute.getRouteId();
		FavoriteMission.getInstance().addFavoriteRoute(userId,loginId,token,routeId,localRoute);
	}
	
	
	
	private boolean checkFavoriteRoute(int routeId)
	{
		return FavoriteMission.getInstance().checkLocalRouteIsFollow(routeId);
	}
	
	
	private void openGPSSettings() {

		LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return;
		}
			Toast.makeText(this, getString(R.string.open_gps_tips), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(asyncLoader != null)
		{
			asyncLoader.recycleBitmap();
		}	
		ActivityMange.getInstance().finishActivity();
	}
}
