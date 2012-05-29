/**  
        * @title CommonPlaceDetailActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-29 下午1:41:04  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.place;

import java.util.ArrayList;
import java.util.List;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.PlaceImageAdapter;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.mission.PlaceMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-29 下午1:41:04  
 */

public abstract class CommonPlaceDetailActivity extends Activity
{

	private static final String TAG = "CommonPlaceDetailActivity";
	
	
	abstract public Place getPlaceById();
	abstract public String getPlaceIntroTitle();
	abstract public boolean isSupportSpecialTrafficStyle();
	abstract public boolean isSupportTicket();
	abstract public boolean isSupportKeyWords();
	abstract public boolean isSupportTips();
	abstract public boolean isSupportHotelStart();
	abstract public boolean isSupportService();
	abstract public boolean isSupportRoomPrice();
	int placeId;
	Place place;
	ImageView[] imageViews;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.common_place_detail);	
		init();
	}

	
	private void init()
	{
		Place place = getPlaceById();
		if(place != null)
		{
			List<String> imagePath = place.getImagesList();
			LayoutInflater inflater = getLayoutInflater();
			ArrayList<View> imageViewlist = new ArrayList<View>();		
			int size=imagePath.size();
			for(int i=0;i<size;i++)
			{
				Anseylodar anseylodar = new Anseylodar();
				View view = inflater.inflate(R.layout.place_detail_image, null);
				ImageView imageView = (ImageView) view.findViewById(R.id.place_image_item);
				String url ;
				url = imagePath.get(i);	
				// update anseylodar.showimgAnsy judge http or local
				anseylodar.showimgAnsy(imageView, url, ConstantField.DATA_HTTP);
				imageViewlist.add(view);
			}
		imageViews = new ImageView[size];	
		ViewGroup main = (ViewGroup) inflater.inflate(R.layout.common_place_detail, null);
		ViewGroup group = (ViewGroup) main.findViewById(R.id.place_images_group);
		ViewPager hotelImage = (ViewPager) main.findViewById(R.id.place_images);
		ImageView imageView;
		for (int i = 0; i < size; i++) {  
            imageView = new ImageView(CommonPlaceDetailActivity.this);  
            imageView.setLayoutParams(new LayoutParams(10, 10));  
            imageView.setPadding(10, 0, 10, 0);  
            imageViews[i] = imageView;  
            if (i == 0) {    
                imageViews[i].setBackgroundResource(R.drawable.guide_dot_white);  
            } else {  
                imageViews[i].setBackgroundResource(R.drawable.guide_dot_black);  
            }  
            group.addView(imageView);  
        } 
		PlaceImageAdapter sceneryAdapter = new PlaceImageAdapter(imageViewlist);	
		
		
		//serviceGroup
		if(isSupportService())
		{
			ViewGroup serviceGroup = (ViewGroup) main.findViewById(R.id.proServiceGroup);
			serviceGroup.setVisibility(View.VISIBLE);
			for(int id:place.getProvidedServiceIdList())
			{
				
				 ImageView serviceImageView = new ImageView(CommonPlaceDetailActivity.this);  
				 serviceImageView.setLayoutParams(new LayoutParams(new LayoutParams((int)this.getResources().getDimension(R.dimen.serviceIcon2),android.view.WindowManager.LayoutParams.WRAP_CONTENT)));  
				 //serviceImageView.setPadding(10, 0, 10, 0);  
				 serviceImageView.setScaleType(ScaleType.FIT_CENTER);
				 serviceImageView.setImageResource(TravelUtil.getServiceImage(id));
		         serviceGroup.addView(serviceImageView);
			}
		}
		
		
		
		String trafficInfos = place.getTransportation();
		String[] traffic= trafficInfos.split(";"); 		
		ViewGroup trafficLocationGroup = (ViewGroup) main.findViewById(R.id.traffic_location_group);
		ViewGroup trafficDistanceGroup = (ViewGroup) main.findViewById(R.id.traffic_distance_group);
		if(traffic.length >0)
		{
			for(String trafficInfo:traffic)
			{
				String[] trafficDetail = trafficInfo.split(":");
				if(trafficDetail.length == 2)
				{
					TextView  locationTextView = new TextView(CommonPlaceDetailActivity.this); 
					TextView  distanceTextView = new TextView(CommonPlaceDetailActivity.this);
					locationTextView.setLayoutParams(new LayoutParams(android.view.WindowManager.LayoutParams.WRAP_CONTENT,android.view.WindowManager.LayoutParams.WRAP_CONTENT));  
					distanceTextView.setLayoutParams(new LayoutParams(android.view.WindowManager.LayoutParams.WRAP_CONTENT,android.view.WindowManager.LayoutParams.WRAP_CONTENT));  
					locationTextView.setText(trafficDetail[0]);
					distanceTextView.setText(trafficDetail[1]);
					trafficLocationGroup.addView(locationTextView);
					trafficDistanceGroup.addView(distanceTextView);
				}
				
			}
		}
		setContentView(main);
		hotelImage.setAdapter(sceneryAdapter);
		hotelImage.setOnPageChangeListener(scenecyImageListener);		
		TextView hotelDetailTitle = (TextView) findViewById(R.id.place_detail_title);
		TextView placeIntroTitle = (TextView) findViewById(R.id.place_intro_title);
		TextView hotelIntro = (TextView) findViewById(R.id.place_intro);
		hotelDetailTitle.setText(place.getName());
		placeIntroTitle.setText(getPlaceIntroTitle());
		hotelIntro.setText(place.getIntroduction());
		
		//hotelStart
		if(isSupportHotelStart())
		{
			ViewGroup hotelStartGroup = (ViewGroup) findViewById(R.id.hotel_start_group);
			hotelStartGroup.setVisibility(View.VISIBLE);
			TextView hotelStart = (TextView) findViewById(R.id.hotel_start);
			hotelStart.setText(TravelUtil.getHotelStar(this,place.getHotelStar()));
		}
		
		//keyword
		if(isSupportKeyWords())
		{
			findViewById(R.id.keyword_group).setVisibility(View.VISIBLE);
			TextView keyword = (TextView) findViewById(R.id.place_keyword);
			String keywordStr = "";
			for(String key:place.getKeywordsList())
			{
				keywordStr+=key+"	";
			}
			keyword.setText(keywordStr);
		}
		//roomprice
		if(isSupportRoomPrice())
		{
			findViewById(R.id.room_price_group).setVisibility(View.VISIBLE);
			TextView roomPrice = (TextView) findViewById(R.id.room_price);
			roomPrice.setText(place.getPrice());
		}
		
		
		/*
		hotelStart = (TextView) findViewById(R.id.place_start);
		hotelAppraisalInfo = (TextView) findViewById(R.id.place_appraisal_info);
		hotelPrice = (TextView) findViewById(R.id.place_price);
		//trafficInfo = (GridView) findViewById(R.id.trafficInfo);
		phoneNum = (TextView) findViewById(R.id.phone_num);
		address = (TextView) findViewById(R.id.address);
		website = (TextView) findViewById(R.id.website);*/
		ImageView recommendImage1 = (ImageView) findViewById(R.id.place_detail_recommend_image1);
		ImageView recommendImage2 = (ImageView) findViewById(R.id.place_detail_recommend_image2);
		ImageView recommendImage3 = (ImageView) findViewById(R.id.place_detail_recommend_image3);
		
		/*mapView1 = (ImageView) findViewById(R.id.item_map_view);
		mapView2 = (ImageView)findViewById(R.id.place_detail_map_nearby);
		mapView1.setOnClickListener(clickListener);
		mapView2.setOnClickListener(clickListener);
		
		phoneGroup = (ViewGroup) findViewById(R.id.phone_group);
		websiteGroup = (ViewGroup)findViewById(R.id.website_group);
		mapGroup = (ViewGroup)findViewById(R.id.map_group);
		phoneGroup.setOnClickListener(clickListener);*/
		
		
		hotelDetailTitle.setText(place.getName());
		hotelIntro.setText(place.getIntroduction());
		/*hotelStart.setText(TravelUtil.getHotelStar(this,place.getHotelStar()));
		
		hotelAppraisalInfo.setText(keyword);
		hotelPrice.setText(place.getPrice()+"");*/
		
		
		int rank = place.getRank();
		switch (rank)
		{
		case 1:{
			recommendImage1.setImageDrawable(this.getResources().getDrawable(R.drawable.good));
			recommendImage2.setImageDrawable(this.getResources().getDrawable(R.drawable.good2));
			recommendImage3.setImageDrawable(this.getResources().getDrawable(R.drawable.good2));
			}
			break;
		case 2:{
			recommendImage1.setImageDrawable(this.getResources().getDrawable(R.drawable.good));
			recommendImage2.setImageDrawable(this.getResources().getDrawable(R.drawable.good));
			recommendImage3.setImageDrawable(this.getResources().getDrawable(R.drawable.good2));
			}
			break;
		case 3:{
			recommendImage1.setImageDrawable(this.getResources().getDrawable(R.drawable.good));
			recommendImage2.setImageDrawable(this.getResources().getDrawable(R.drawable.good));
			recommendImage3.setImageDrawable(this.getResources().getDrawable(R.drawable.good));
			}
		break;
		default:
			break;
		}
		/*String phoneNumber = "";
		String addressStr = "";
		for(String telephone:place.getTelephoneList())
		{
			phoneNumber = telephone+" ";
		}
		for(String add:place.getAddressList())
		{
			addressStr = add+" ";
		}
		phoneNum.setText(phoneNumber.trim());
		address.setText(this.getResources().getString(R.string.address)+addressStr.trim());
		website.setText(this.getResources().getString(R.string.website)+place.getWebsite());*/
	}
	}
	
	private OnPageChangeListener scenecyImageListener  = new OnPageChangeListener()
	{
		
		@Override
		public void onPageSelected(int arg0)
		{
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0]
						.setBackgroundResource(R.drawable.guide_dot_white);
				if (arg0 != i) {
					imageViews[i]
							.setBackgroundResource(R.drawable.guide_dot_black);
				}
			}
			
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0)
		{
			// TODO Auto-generated method stub
			
		}
	};




	
}
