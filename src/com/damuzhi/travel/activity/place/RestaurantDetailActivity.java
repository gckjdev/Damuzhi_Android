/**  
        * @title RestaurantDetailActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-16 下午2:06:00  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.place;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.PlaceImageAdapter;
import com.damuzhi.travel.activity.common.CommendPlaceMap;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-16 下午2:06:00  
 */

public class RestaurantDetailActivity extends MenuActivity
{
	private static final String TAG = "RestaurantDetailActivity";
	private TextView restaurantDetailTitle;
	private TextView restaurantIntro;
	private TextView foodType;
	private TextView openTime;
	private TextView averageComsumption;
	private TextView appraisalInfo;
	private TextView specialFood;
	private TextView trafficInfo;
	private TextView phoneNum;
	private TextView address;
	private TextView website;
	private ImageView recommendImage1;
	private ImageView recommendImage2;
	private ImageView recommendImage3;
	private ArrayList<View> imageViewlist;  
	private ViewGroup main, group,phoneGroup,websiteGroup,mapGroup; 
	private ImageView imageView;  
	private ImageView[] imageViews;  
	private ViewPager restaurantImage;
	private Place place;
	private ImageView mapView1, mapView2;  
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
	}

	private void init()
	{
		TravelApplication application = (TravelApplication) this.getApplication();
		place = application.getPlace();
		List<String> imagePath = place.getImagesList();
		LayoutInflater inflater = getLayoutInflater();
		imageViewlist = new ArrayList<View>();
		String dataPath = String.format(ConstantField.DATA_PATH,application.getCityID());
		int size=imagePath.size();
		for(int i=0;i<size;i++)
		{
			Anseylodar anseylodar = new Anseylodar();
			View view = inflater.inflate(R.layout.place_detail_image, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.place_image_item);
			String url ;
			if(application.getDataFlag() == ConstantField.DATA_LOCAL)
			{
				url = dataPath+imagePath.get(i);
			}else
			{
				url = imagePath.get(i);			
			}		
			anseylodar.showimgAnsy(imageView, url, application.getDataFlag());
			imageViewlist.add(view);
		}
		imageViews = new ImageView[size];
		main = (ViewGroup) inflater.inflate(R.layout.restaurant_detail, null);
		group = (ViewGroup) main.findViewById(R.id.restaurant__images_group);
		restaurantImage = (ViewPager) main.findViewById(R.id.restaurant_images);
		for (int i = 0; i < size; i++) {  
            imageView = new ImageView(RestaurantDetailActivity.this);  
            imageView.setLayoutParams(new LayoutParams(10, 10));  
            imageView.setPadding(10, 0, 10, 0);  
            imageViews[i] = imageView;  
            if (i == 0) {  
                // 默认进入程序后第一张图片被选中;  
                imageViews[i].setBackgroundResource(R.drawable.guide_dot_white);  
            } else {  
                imageViews[i].setBackgroundResource(R.drawable.guide_dot_black);  
            }  
            group.addView(imageView);  
        } 
		PlaceImageAdapter sceneryAdapter = new PlaceImageAdapter(imageViewlist);		
		ViewGroup serviceGroup = (ViewGroup) main.findViewById(R.id.proServiceGroup);
		for(int id:place.getProvidedServiceIdList())
		{
			
			 ImageView serviceImageView = new ImageView(RestaurantDetailActivity.this);  
			 serviceImageView.setLayoutParams(new LayoutParams(new LayoutParams((int)this.getResources().getDimension(R.dimen.serviceIcon),android.view.WindowManager.LayoutParams.WRAP_CONTENT)));  
			 //serviceImageView.setPadding(10, 0, 10, 0);  
			 serviceImageView.setScaleType(ScaleType.FIT_CENTER);
			 serviceImageView.setImageResource(TravelUtil.getServiceImage(id));
	         serviceGroup.addView(serviceImageView);
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
					TextView  locationTextView = new TextView(RestaurantDetailActivity.this); 
					TextView  distanceTextView = new TextView(RestaurantDetailActivity.this);
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
		restaurantImage.setAdapter(sceneryAdapter);
		restaurantImage.setOnPageChangeListener(scenecyImageListener);		
		restaurantDetailTitle = (TextView) findViewById(R.id.restaurant_detail_title);
		restaurantIntro = (TextView) findViewById(R.id.restaurant_intro);
		foodType = (TextView) findViewById(R.id.food_type);
		openTime = (TextView) findViewById(R.id.open_time);
		averageComsumption = (TextView) findViewById(R.id.average_consumption);
		appraisalInfo = (TextView)findViewById(R.id.appraisal_info);
		specialFood = (TextView) findViewById(R.id.special_food);
		trafficInfo = (TextView) findViewById(R.id.traffic_info);
		phoneNum = (TextView) findViewById(R.id.phone_num);
		address = (TextView) findViewById(R.id.address);
		website = (TextView) findViewById(R.id.website);
		recommendImage1 = (ImageView) findViewById(R.id.restaurant_detail_recommend_image1);
		recommendImage2 = (ImageView) findViewById(R.id.restaurant_detail_recommend_image2);
		recommendImage3 = (ImageView) findViewById(R.id.restaurant_detail_recommend_image3);
		
		mapView1 = (ImageView) findViewById(R.id.item_map_view);
		mapView2 = (ImageView)findViewById(R.id.restaurant_detail_map_nearby);
		mapView1.setOnClickListener(clickListener);
		mapView2.setOnClickListener(clickListener);
		
		phoneGroup = (ViewGroup) findViewById(R.id.phone_group);
		websiteGroup = (ViewGroup)findViewById(R.id.website_group);
		mapGroup = (ViewGroup)findViewById(R.id.map_group);
		phoneGroup.setOnClickListener(clickListener);
		
		
		restaurantDetailTitle.setText(place.getName());
		restaurantIntro.setText(place.getIntroduction());
		foodType.setText(application.getSubCatNameMap().get(place.getSubCategoryId()));
		openTime.setText(place.getOpenTime());
		averageComsumption.setText(application.getSymbolMap().get(place.getCityId())+place.getAvgPrice());
		String keyword="";
		for(String key:place.getKeywordsList())
		{
			keyword+= key+"  ";
		}
		appraisalInfo.setText(keyword);
		String typicalDishes="";
		for(String typcial:place.getTypicalDishesList())
		{
			typicalDishes+= typcial+"  ";
		}
		specialFood.setText(typicalDishes);
		trafficInfo.setText(place.getTransportation());		
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
		String phoneNumber = "";
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
		website.setText(this.getResources().getString(R.string.website)+place.getWebsite());
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
	
	
	private OnClickListener clickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			switch (v.getId())
			{
			case R.id.item_map_view:
				
				intent.setClass(RestaurantDetailActivity.this, CommendPlaceMap.class);
				startActivity(intent);
				break;
			case R.id.restaurant_detail_map_nearby:	
				intent.setClass(RestaurantDetailActivity.this, CommendPlaceMap.class);
				startActivity(intent);
				break;
			case R.id.phone_group:
				String phoneNumber = (String) phoneNum.getText();
				if(phoneNumber.trim()!=""||!phoneNumber.trim().equals(""))
				{
					makePhoneCall(phoneNumber);
				}				
				break;
			default:
				break;
			}
			
		}
	};
	 
	public void makePhoneCall( final String phoneNumber)
	{
		AlertDialog phoneCall = new AlertDialog.Builder(RestaurantDetailActivity.this).create();
		phoneCall.setMessage(getResources().getString(R.string.make_phone_call)+"\n"+phoneNumber);
		phoneCall.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.call),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+phoneNumber));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				RestaurantDetailActivity.this.startActivity(intent);
			}
		} );
		phoneCall.setButton(DialogInterface.BUTTON_NEGATIVE,""+getBaseContext().getString(R.string.cancel),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				dialog.cancel();
				
			}
		} );
		phoneCall.show();
	}
}
