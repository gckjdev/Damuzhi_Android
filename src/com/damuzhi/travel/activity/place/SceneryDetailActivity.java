package com.damuzhi.travel.activity.place;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.ScenecyImageAdapter;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.service.MainService;

public class SceneryDetailActivity extends MenuActivity
{
	private TextView sceneryDetailTitle;
	private TextView sceneryIntro;
	private TextView ticketsPrice;
	private TextView openingHour;
	private TextView trafficInfo;
	private TextView tourTips;
	private TextView phoneNum;
	private TextView address;
	private TextView website;
	private ImageView mapView;
	private ImageView recommendImage1;
	private ImageView recommendImage2;
	private ImageView recommendImage3;
	private ArrayList<View> imageViewlist;  
	private ViewGroup main, group;  
	private ImageView imageView;  
	private ImageView[] imageViews;  
	
	private ViewPager sceneryImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
		MainService.allActivity.add(this);
	}

	private void init()
	{
		TravelApplication application = (TravelApplication) this.getApplication();
		Place place = application.getPlace();
		List<String> imagePath = place.getImagesList();
		LayoutInflater inflater = getLayoutInflater();
		imageViewlist = new ArrayList<View>();
		String dataPath = String.format(ConstantField.IMAGE_PATH,application.getCityID());
		int size=imagePath.size();
		for(int i=0;i<size;i++)
		{
			Anseylodar anseylodar = new Anseylodar();
			View view = inflater.inflate(R.layout.scenery_detail_image, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.sceneryImageItem);
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
		main = (ViewGroup) inflater.inflate(R.layout.scenery_detail, null);
		group = (ViewGroup) main.findViewById(R.id.sceneryImagesGroup);
		sceneryImage = (ViewPager) main.findViewById(R.id.scenery_images);
		for (int i = 0; i < size; i++) {  
            imageView = new ImageView(SceneryDetailActivity.this);  
            imageView.setLayoutParams(new LayoutParams(10,10));  
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
		ScenecyImageAdapter sceneryAdapter = new ScenecyImageAdapter(imageViewlist);
		setContentView(main);
		sceneryImage.setAdapter(sceneryAdapter);
		sceneryImage.setOnPageChangeListener(scenecyImageListener);
		
		sceneryDetailTitle = (TextView) findViewById(R.id.scenery_detail_title);
		sceneryIntro = (TextView) findViewById(R.id.sceneryIntro);
		ticketsPrice = (TextView) findViewById(R.id.ticketsPrice);
		openingHour = (TextView) findViewById(R.id.openingHour);
		trafficInfo = (TextView) findViewById(R.id.trafficInfo);
		tourTips = (TextView) findViewById(R.id.tourTips);
		phoneNum = (TextView) findViewById(R.id.phoneNum);
		address = (TextView) findViewById(R.id.address);
		website = (TextView) findViewById(R.id.website);
		mapView = (ImageView) findViewById(R.id.item_map_view);
		recommendImage1 = (ImageView) findViewById(R.id.scenery_detail_recommend_image1);
		recommendImage2 = (ImageView) findViewById(R.id.scenery_detail_recommend_image2);
		recommendImage3 = (ImageView) findViewById(R.id.scenery_detail_recommend_image3);
		sceneryDetailTitle.setText(place.getName());
		sceneryIntro.setText(place.getIntroduction());
		ticketsPrice.setText(place.getPriceDescription());
		openingHour.setText(place.getOpenTime());
		trafficInfo.setText(place.getTransportation());		
		tourTips.setText(place.getTips());
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
		phoneNum.setText(this.getResources().getString(R.string.phoneNum)+phoneNumber.trim());
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
	
	/*
	 @Override
		public boolean onKeyDown(int keyCode, KeyEvent event)
		{
			if (  keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
				 Intent intent = new Intent(this, SceneryActivity.class);
				 //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				 startActivity(intent);
		         return true;
		    }
			else
			{
				  return super.onKeyDown(keyCode, event);	
			}
		  
		}*/
	
}
