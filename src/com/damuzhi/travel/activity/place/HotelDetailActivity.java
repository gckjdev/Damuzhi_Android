package com.damuzhi.travel.activity.place;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.PlaceImageAdapter;
import com.damuzhi.travel.activity.common.NearbyPlaceMap;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;

public class HotelDetailActivity extends TravelActivity
{
	private static final String TAG = "HotelDeatilActivity";
	private TextView hotelDetailTitle;
	private TextView hotelIntro;
	private TextView hotelStart;
	private TextView hotelAppraisalInfo;
	private TextView hotelPrice;
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
	private ViewPager hotelImage;
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
		main = (ViewGroup) inflater.inflate(R.layout.hotel_detail, null);
		group = (ViewGroup) main.findViewById(R.id.hotel_images_group);
		hotelImage = (ViewPager) main.findViewById(R.id.hotel_images);
		for (int i = 0; i < size; i++) {  
            imageView = new ImageView(HotelDetailActivity.this);  
            imageView.setLayoutParams(new LayoutParams(10, 10));  
            imageView.setPadding(10, 0, 10, 0);  
            imageViews[i] = imageView;  
            if (i == 0) {  
                // Ĭ�Ͻ��������һ��ͼƬ��ѡ��;  
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
			
			 ImageView serviceImageView = new ImageView(HotelDetailActivity.this);  
			 serviceImageView.setLayoutParams(new LayoutParams(new LayoutParams((int)this.getResources().getDimension(R.dimen.hotel_start_icon),android.view.WindowManager.LayoutParams.WRAP_CONTENT)));  
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
					TextView  locationTextView = new TextView(HotelDetailActivity.this); 
					TextView  distanceTextView = new TextView(HotelDetailActivity.this);
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
		hotelDetailTitle = (TextView) findViewById(R.id.hotel_detail_title);
		hotelIntro = (TextView) findViewById(R.id.hotel_intro);
		hotelStart = (TextView) findViewById(R.id.hotel_start);
		hotelAppraisalInfo = (TextView) findViewById(R.id.hotel_appraisal_info);
		hotelPrice = (TextView) findViewById(R.id.hotel_price);
		//trafficInfo = (GridView) findViewById(R.id.trafficInfo);
		phoneNum = (TextView) findViewById(R.id.phone_num);
		address = (TextView) findViewById(R.id.address);
		website = (TextView) findViewById(R.id.website);
		recommendImage1 = (ImageView) findViewById(R.id.hotel_detail_recommend_image1);
		recommendImage2 = (ImageView) findViewById(R.id.hotel_detail_recommend_image2);
		recommendImage3 = (ImageView) findViewById(R.id.hotel_detail_recommend_image3);
		
		mapView1 = (ImageView) findViewById(R.id.item_map_view);
		mapView2 = (ImageView)findViewById(R.id.hotel_detail_map_nearby);
		mapView1.setOnClickListener(clickListener);
		mapView2.setOnClickListener(clickListener);
		
		phoneGroup = (ViewGroup) findViewById(R.id.phone_group);
		websiteGroup = (ViewGroup)findViewById(R.id.website_group);
		mapGroup = (ViewGroup)findViewById(R.id.map_group);
		phoneGroup.setOnClickListener(clickListener);
		
		
		hotelDetailTitle.setText(place.getName());
		hotelIntro.setText(place.getIntroduction());
		hotelStart.setText(TravelUtil.getHotelStar(this,place.getHotelStar()));
		String keyword = "";
		for(String key:place.getKeywordsList())
		{
			keyword+=key+"	";
		}
		hotelAppraisalInfo.setText(keyword);
		hotelPrice.setText(place.getPrice()+"��");
		
		
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
	
	
	private OnClickListener clickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			switch (v.getId())
			{
			case R.id.item_map_view:
				
				intent.setClass(HotelDetailActivity.this, NearbyPlaceMap.class);
				startActivity(intent);
				break;
			case R.id.hotel_detail_map_nearby:	
				intent.setClass(HotelDetailActivity.this, NearbyPlaceMap.class);
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
		AlertDialog phoneCall = new AlertDialog.Builder(HotelDetailActivity.this).create();
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
				HotelDetailActivity.this.startActivity(intent);
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
