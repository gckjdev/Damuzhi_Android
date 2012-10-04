package com.damuzhi.travel.activity.touristRoute;

import java.util.ArrayList;
import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.PlaceImageAdapter;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.AsyncLoader;
import com.damuzhi.travel.activity.place.CommonPlaceDetailActivity;
import com.damuzhi.travel.mission.favorite.FavoriteMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRoute;
import com.damuzhi.travel.util.TravelUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.weibo.api.Tag_API;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.TextView;


public class CommonLocalTripsDetailActivity extends Activity
{

	protected static final String TAG = "CommonLocalTripsDetailActivity";
	private ImageButton routeIntroButton;
	private ImageButton bookingNoticeButton;
	private ImageButton userFeedBackButton;
	private TextView routeNameTextView;
	private TextView routeIdTextView;
	private TextView agencyNameTextView;
	private TextView routeIntroTextView;
	private TextView bookingNoticeTextView;
	private TextView userFeedBackTextView;
	private TextView routePriceTextView;
	private Button consultButton;
	private ImageView bookOrderImageView;
	private ViewGroup routeIntroViewGroup;
	private WebView routeDetailWebView;
	private WebView bookingNoticeWebView;
	private AsyncLoader asyncLoader;
	private ImageView[] imageViews;
	private LocalRoute localRoute;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActivityMange.getInstance().addActivity(this);
		
		
		byte[] data = getIntent().getByteArrayExtra("local_route");
		localRoute = null;
		try {
			 localRoute = LocalRoute.parseFrom(data);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
				
		List<String> imagePath = localRoute.getDetailImagesList();
		LayoutInflater inflater = getLayoutInflater();
		ArrayList<View> imageViewlist = new ArrayList<View>();	
		asyncLoader = AsyncLoader.getInstance();
		//asyncLoader = new AsyncLoader();
		int size=imagePath.size();	
		for(int i=0;i<size;i++)
		{
			View view = inflater.inflate(R.layout.place_detail_image, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.place_image_item);
			String url ;
			url = imagePath.get(i);	
			asyncLoader.showimgAnsy(imageView, url);
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
		userFeedBackButton = (ImageButton) findViewById(R.id.user_feedback);
		consultButton = (Button) findViewById(R.id.consult_button);
		routeIntroButton.setSelected(true);
		routeIdTextView = (TextView) findViewById(R.id.route_id);
		routeNameTextView = (TextView) findViewById(R.id.route_name);
		agencyNameTextView = (TextView) findViewById(R.id.agency_name);
		routeNameTextView.setText(localRoute.getName());
		routeIdTextView.setText(num+localRoute.getRouteId());
		agencyNameTextView.setText(AppManager.getInstance().getAgencyNameById(localRoute.getAgencyId()));
		agencyNameTextView.setSelected(true);
		//String price = AppManager.getInstance().getSymbolByCityId(cityId)+localRoute.getPrice();
		
		routeIntroTextView = (TextView) findViewById(R.id.route_intro_text);
		bookingNoticeTextView = (TextView) findViewById(R.id.booking_notice_text);
		userFeedBackTextView = (TextView) findViewById(R.id.user_feedback_text);
		routePriceTextView = (TextView) findViewById(R.id.route_price);
		bookOrderImageView = (ImageView) findViewById(R.id.book_order);
		routePriceTextView.setText(localRoute.getPrice());		
		routeIntroTextView.setTextColor(getResources().getColor(R.color.white));
		routeDetailWebView = (WebView) findViewById(R.id.route_detail_webview);
		bookingNoticeWebView = (WebView) findViewById(R.id.booking_notice_webview);
		routeIntroViewGroup = (ViewGroup) findViewById(R.id.route_intro_group);
		routeDetailWebView.loadUrl(localRoute.getDetailUrl());
		bookingNoticeWebView.loadUrl(localRoute.getBookingNotice());
		routeIntroButton.setOnClickListener(routeIntroOnClickListener);
		bookingNoticeButton.setOnClickListener(bookingNoticeOnClickListener);
		userFeedBackButton.setOnClickListener(feedbackIntroOnClickListener);
		bookOrderImageView.setOnClickListener(bookOrderOnClickListener);
		consultButton.setOnClickListener(consultOnClickListener);
		routeDetailWebView.setWebViewClient(webViewClient);
		routeDetailWebView.getSettings().setJavaScriptEnabled(true);
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
			userFeedBackButton.setSelected(false);
			routeIntroTextView.setTextColor(getResources().getColor(R.color.white));
			bookingNoticeTextView.setTextColor(getResources().getColor(R.color.black));
			userFeedBackTextView.setTextColor(getResources().getColor(R.color.black));
			if(routeIntroViewGroup.getVisibility() == View.GONE)
			{
				bookingNoticeWebView.setVisibility(View.GONE);
				routeIntroViewGroup.setVisibility(View.VISIBLE);	
			}
		}
	};
	
	private OnClickListener bookingNoticeOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			v.setSelected(true);
			routeIntroButton.setSelected(false);
			userFeedBackButton.setSelected(false);
			bookingNoticeTextView.setTextColor(getResources().getColor(R.color.white));
			userFeedBackTextView.setTextColor(getResources().getColor(R.color.black));
			routeIntroTextView.setTextColor(getResources().getColor(R.color.black));
			routeIntroViewGroup.setVisibility(View.GONE);
			if(bookingNoticeWebView.getVisibility() == View.GONE)
			{
				bookingNoticeWebView.setVisibility(View.VISIBLE);
				routeIntroViewGroup.setVisibility(View.GONE);
			}
			
		}
	};
	
	private OnClickListener feedbackIntroOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			v.setSelected(true);
			bookingNoticeButton.setSelected(false);
			routeIntroButton.setSelected(false);
			userFeedBackTextView.setTextColor(getResources().getColor(R.color.white));
			routeIntroTextView.setTextColor(getResources().getColor(R.color.black));
			bookingNoticeTextView.setTextColor(getResources().getColor(R.color.black));
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
	
	public void makePhoneCall( final String phoneNumber)
	{
		AlertDialog phoneCall = new AlertDialog.Builder(CommonLocalTripsDetailActivity.this).create();
		phoneCall.setMessage(getResources().getString(R.string.make_phone_call)+"\n"+phoneNumber);
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
			Log.d(TAG, "page start url = "+url);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			Log.d(TAG, "OVER RIDE URL = "+url);
			if(url.contains("type=FollowRoute"));
			{
				Log.d(TAG, "follow route");
				addFavoriteRoute(localRoute);
				routeDetailWebView.loadUrl("javascript:toggleFavor(true)");
			}
			return super.shouldOverrideUrlLoading(view, url);
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
	
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}
}
