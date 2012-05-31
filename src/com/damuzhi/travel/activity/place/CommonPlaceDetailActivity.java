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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.PlaceImageAdapter;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.mission.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
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
	abstract public boolean isSupportOpenTime();
	abstract public boolean isSupportFood();
	abstract public boolean isSupportAvgPrice();
	abstract public boolean isSupportSpecialFood();
	
	
	int placeId;
	Place place;
	ImageView[] imageViews;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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
			ViewPager placeImage = (ViewPager) main.findViewById(R.id.place_images);
			ImageView imageView;
			for (int i = 0; i < size; i++) {  
	            imageView = new ImageView(CommonPlaceDetailActivity.this);  
	            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
	            params.setMargins((int)getResources().getDimension(R.dimen.image_margin), 0, (int)getResources().getDimension(R.dimen.image_margin), 0);
	            imageView.setLayoutParams(params);  
	          //  imageView.setPadding((int)getResources().getDimension(R.dimen.image_margin), 0, (int)getResources().getDimension(R.dimen.image_margin), 0);
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
				 serviceImageView.setLayoutParams(new LayoutParams(new LayoutParams((int)this.getResources().getDimension(R.dimen.service_icon2),android.view.WindowManager.LayoutParams.WRAP_CONTENT)));  
				 //serviceImageView.setPadding(10, 0, 10, 0);  
				 serviceImageView.setScaleType(ScaleType.FIT_CENTER);
				 serviceImageView.setImageResource(TravelUtil.getServiceImage(id));
		         serviceGroup.addView(serviceImageView);
			}
		}
		
		
		placeImage.setAdapter(sceneryAdapter);
		placeImage.setOnPageChangeListener(scenecyImageListener);
		setContentView(main);
		
		if(isSupportSpecialTrafficStyle())
		{
			main.findViewById(R.id.special_trans_group).setVisibility(View.VISIBLE);
			String trafficInfos = place.getTransportation();
			String[] traffic= trafficInfos.split(";"); 		
			ViewGroup specialTrans = (ViewGroup) main.findViewById(R.id.special_trans);
			if(traffic.length >0)
			{
				int i= 1;
				for(String trafficInfo:traffic)
				{
					RelativeLayout row = new RelativeLayout(CommonPlaceDetailActivity.this);
					row.setLayoutParams(new LayoutParams((int)getResources().getDimension(R.dimen.transport_width),(int)getResources().getDimension(R.dimen.transport_height)));
					if(i==traffic.length)
					{
						row.setBackgroundDrawable(getResources().getDrawable(R.drawable.table5_down));
					}else {
						row.setBackgroundDrawable(getResources().getDrawable(R.drawable.table5_center));
					}
					
					
					String[] trafficDetail = trafficInfo.split(":");
					if(trafficDetail.length == 2)
					{
						TextView  locationTextView = new TextView(CommonPlaceDetailActivity.this); 
						TextView  distanceTextView = new TextView(CommonPlaceDetailActivity.this);
						RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
						RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
						params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
						params1.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
						params1.setMargins((int)getResources().getDimension(R.dimen.transport_margin_left), 0, 0, 0);
						params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
						params2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
						params2.setMargins((int)getResources().getDimension(R.dimen.transport_margin_right), 0, 0, 0);
						locationTextView.setLayoutParams(params1);  
						distanceTextView.setLayoutParams(params2);  
						locationTextView.setTextColor(getResources().getColor(R.color.place_title_color));
						locationTextView.setTextSize(getResources().getDimension(R.dimen.transport_text_size));
						distanceTextView.setTextColor(getResources().getColor(R.color.place_title_color));
						distanceTextView.setTextSize(getResources().getDimension(R.dimen.transport_text_size));
						locationTextView.setText(trafficDetail[0]);
						distanceTextView.setText(trafficDetail[1]);
						row.addView(locationTextView);
						row.addView(distanceTextView);
						specialTrans.addView(row,i);
						i++;
					}
					
				}
			}		
					
		}else {
			main.findViewById(R.id.transport_group).setVisibility(View.VISIBLE);
			TextView transport = (TextView) main.findViewById(R.id.transport);
			transport.setText(place.getTransportation());
		}
		
		
		
		
		
		
		
		//ticket
		if(isSupportTicket())
		{
			findViewById(R.id.ticket_group).setVisibility(View.VISIBLE);
			TextView ticket = (TextView) findViewById(R.id.ticket);			
			ticket.setText(place.getPriceDescription());
		}
		
		//food
		if(isSupportFood())
		{
			findViewById(R.id.food_group).setVisibility(View.VISIBLE);
			TextView food = (TextView) findViewById(R.id.food);			
			food.setText(AppManager.getInstance().getAllSubCatMap().get(place.getSubCategoryId()));
		}
		
		//openTime
		if(isSupportOpenTime())
		{
			findViewById(R.id.open_time_group).setVisibility(View.VISIBLE);
			if(place.getCategoryId()!=PlaceCategoryType.PLACE_SPOT_VALUE)
			{
				TextView openTimeTitle = (TextView) findViewById(R.id.open_time_title);
				openTimeTitle.setText(getString(R.string.open_time1));
			}
			
			TextView openTime = (TextView) findViewById(R.id.open_time);			
			openTime.setText(place.getOpenTime());
		}
		//avgPrice
		if(isSupportAvgPrice())
		{
			findViewById(R.id.avg_price_group).setVisibility(View.VISIBLE);
			TextView avgPrice = (TextView) findViewById(R.id.avg_price);			
			StringBuffer symbol = new StringBuffer(AppManager.getInstance().getSymbolMap().get(TravelApplication.getInstance().getCityID()));
			avgPrice.setText(symbol+place.getAvgPrice());
		}
		
		//specialFood
		if(isSupportSpecialFood())
		{
			findViewById(R.id.special_food_group).setVisibility(View.VISIBLE);
			TextView specialFood = (TextView) findViewById(R.id.special_food);		
			StringBuffer typicalDishes = new StringBuffer();
			for(String typcial:place.getTypicalDishesList())
			{
				typicalDishes.append(typcial);
				typicalDishes.append("  ");
			}
			specialFood.setText(typicalDishes);
		}
				
		//tips
		if(isSupportTips())
		{
			findViewById(R.id.tips_group).setVisibility(View.VISIBLE);
			TextView tips = (TextView) findViewById(R.id.tips);			
			tips.setText(place.getTips());
		}
		
		//hotelStart
		if(isSupportHotelStart())
		{
			findViewById(R.id.hotel_start_group).setVisibility(View.VISIBLE);
			TextView hotelStart = (TextView) findViewById(R.id.hotel_start);
			hotelStart.setText(TravelUtil.getHotelStar(this,place.getHotelStar()));
			ViewGroup hotelStartImageGroup = (ViewGroup) findViewById(R.id.hotel_start_image);
			for(int i=0;i<place.getHotelStar();i++)
			{
				
				 ImageView hotelStartImage = new ImageView(CommonPlaceDetailActivity.this);  
				 hotelStartImage.setLayoutParams(new LayoutParams(new LayoutParams((int)this.getResources().getDimension(R.dimen.service_icon),android.view.WindowManager.LayoutParams.WRAP_CONTENT)));  
				 //serviceImageView.setPadding(10, 0, 10, 0);  
				 hotelStartImage.setScaleType(ScaleType.FIT_CENTER);
				 hotelStartImage.setImageResource(R.drawable.star_ico);
				 hotelStartImageGroup.addView(hotelStartImage);
			}
		}
		
		//keyword
		if(isSupportKeyWords())
		{
			findViewById(R.id.keyword_group).setVisibility(View.VISIBLE);
			TextView keyword = (TextView) findViewById(R.id.place_keyword);
			StringBuffer keywordStr = new StringBuffer();
			for(String key:place.getKeywordsList())
			{
				keywordStr.append(key);
				keywordStr.append("、");
			}
			keyword.setText(keywordStr.substring(0, keywordStr.length()-1));
		}
		
		
		//roomprice
		if(isSupportRoomPrice())
		{
			findViewById(R.id.room_price_group).setVisibility(View.VISIBLE);
			TextView roomPrice = (TextView) findViewById(R.id.room_price);
			StringBuffer symbol = new StringBuffer(AppManager.getInstance().getSymbolMap().get(TravelApplication.getInstance().getCityID()));
			symbol.append(place.getPrice());
			symbol.append("起");
			roomPrice.setText(symbol);
		}
		
		
		/*
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
		
		TextView placeDetailTitle = (TextView) findViewById(R.id.place_detail_title);
		TextView placeIntroTitle = (TextView) findViewById(R.id.place_intro_title);
		TextView placeIntro = (TextView) findViewById(R.id.place_intro);
		placeDetailTitle.setText(place.getName());
		placeIntroTitle.setText(getPlaceIntroTitle());
		placeIntro.setText("	"+place.getIntroduction());
		
		
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
		
		
		if(place.getTelephoneList().size()>0)
		{
			TextView phoneNum = (TextView) findViewById(R.id.phone_num);
			phoneNum.setSelected(true);
			ImageView phoneCall = (ImageView) findViewById(R.id.phone_call);
			phoneNum.setVisibility(View.VISIBLE);
			phoneCall.setVisibility(View.VISIBLE);
			StringBuffer phoneNumber = new StringBuffer();
			for(String telephone:place.getTelephoneList())
			{
				phoneNumber.append(telephone);
				phoneNumber.append(" ");
			}
			phoneNum.setText(getString(R.string.phone_number)+phoneNumber);
		}
		
		if(place.getAddressList().size()>0)
		{
			TextView address = (TextView) findViewById(R.id.address);
			address.setSelected(true);
			ImageView addressMapView = (ImageView) findViewById(R.id.address_map_view);
			address.setVisibility(View.VISIBLE);
			addressMapView.setVisibility(View.VISIBLE);
			StringBuffer addressStr = new StringBuffer();
			for(String addres:place.getAddressList())
			{
				addressStr.append(addres);
				addressStr.append(" ");
			}
			address.setText(getString(R.string.address)+addressStr);
		}
		
		if(place.getWebsite()!=null)
		{
			TextView website = (TextView) findViewById(R.id.website);
			website.setText(getString(R.string.website)+place.getWebsite());
		}
		
	}
	}
	
	private OnPageChangeListener scenecyImageListener  = new OnPageChangeListener()
	{
		
		@Override
		public void onPageSelected(int arg0)
		{
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0].setBackgroundResource(R.drawable.guide_dot_white);
				if (arg0 != i) {
					imageViews[i].setBackgroundResource(R.drawable.guide_dot_black);
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


	
	public static Class getClassByPlaceType(int categoryId)
	{
		switch (categoryId)
		{
		case PlaceCategoryType.PLACE_SPOT_VALUE:
			return CommonSpotDetailActivity.class;
		case PlaceCategoryType.PLACE_HOTEL_VALUE:
			return CommonHotelDetailActivity.class;
		case PlaceCategoryType.PLACE_RESTRAURANT_VALUE:
			return CommonRestaurantDetailActivity.class;
		case PlaceCategoryType.PLACE_SHOPPING_VALUE:
			return CommonShoppingActivity.class;
		case PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE:
			return CommonEntertainmentActivity.class;
		}
		return null;
	}




	
}
