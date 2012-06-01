/**  
        * @title EntertainmentDetaillActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-16 ����5:12:00  
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

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.PlaceImageAdapter;
import com.damuzhi.travel.activity.common.NearbyPlaceMap;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.service.MainService;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-16 ����5:12:00  
 */

public class EntertainmentDetailActivity extends TravelActivity
{
	private TextView entertainmentDetailTitle;
	private TextView entertainmentIntro;
	private TextView openTime;
	private TextView averageComsumption;
	private TextView appraisalInfo;
	private TextView trafficInfo;
	private TextView entertainmentTips;
	private TextView phoneNum;
	private TextView address;
	private TextView website;
	private ImageView mapView1;
	private ImageView mapView2;
	private ImageView recommendImage1;
	private ImageView recommendImage2;
	private ImageView recommendImage3;
	private ArrayList<View> imageViewlist;  
	private ViewGroup main, group,phoneGroup,websiteGroup,mapGroup;  
	private ImageView imageView;  
	private ImageView[] imageViews;  	
	private ViewPager entertainmentImage;
	private Place place;
	
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
		TravelApplication application = TravelApplication.getInstance();
		place = application.getPlace();
		List<String> imagePath = place.getImagesList();
		LayoutInflater inflater = getLayoutInflater();
		imageViewlist = new ArrayList<View>();
		String dataPath = String.format(ConstantField.IMAGE_PATH,application.getCityID());
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
		main = (ViewGroup) inflater.inflate(R.layout.entertainment_detail, null);
		group = (ViewGroup) main.findViewById(R.id.entertainment_images_group);
		entertainmentImage = (ViewPager) main.findViewById(R.id.entertainment_images);
		for (int i = 0; i < size; i++) {  
            imageView = new ImageView(EntertainmentDetailActivity.this);  
            imageView.setLayoutParams(new LayoutParams(10,10));  
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
		PlaceImageAdapter entertainmentAdapter = new PlaceImageAdapter(imageViewlist);
		setContentView(main);
		entertainmentImage.setAdapter(entertainmentAdapter);
		entertainmentImage.setOnPageChangeListener(scenecyImageListener);
		
		entertainmentDetailTitle = (TextView) findViewById(R.id.entertainment_detail_title);
		entertainmentIntro = (TextView) findViewById(R.id.entertainment_intro);
		openTime = (TextView) findViewById(R.id.open_time);
		averageComsumption = (TextView) findViewById(R.id.average_consumption);
		appraisalInfo = (TextView) findViewById(R.id.appraisal_info);
		trafficInfo = (TextView) findViewById(R.id.traffic_info);		
		entertainmentTips = (TextView) findViewById(R.id.entertainment_tips);
		phoneNum = (TextView) findViewById(R.id.phone_num);
		address = (TextView) findViewById(R.id.address);
		website = (TextView) findViewById(R.id.website);
		
		mapView1 = (ImageView) findViewById(R.id.item_map_view);
		mapView2 = (ImageView)findViewById(R.id.entertainment_detail_map_nearby);
		mapView1.setOnClickListener(clickListener);
		mapView2.setOnClickListener(clickListener);
		
		phoneGroup = (ViewGroup) findViewById(R.id.phone_group);
		websiteGroup = (ViewGroup)findViewById(R.id.website_group);
		mapGroup = (ViewGroup)findViewById(R.id.map_group);
		phoneGroup.setOnClickListener(clickListener);
		
		recommendImage1 = (ImageView) findViewById(R.id.entertainment_detail_recommend_image1);
		recommendImage2 = (ImageView) findViewById(R.id.entertainment_detail_recommend_image2);
		recommendImage3 = (ImageView) findViewById(R.id.entertainment_detail_recommend_image3);
		entertainmentDetailTitle.setText(place.getName());
		entertainmentIntro.setText(place.getIntroduction());
		openTime.setText(place.getOpenTime());
		averageComsumption.setText(application.getSymbolMap().get(place.getCityId())+place.getAvgPrice());
		String keyword="";
		for(String key:place.getKeywordsList())
		{
			keyword+= key+"  ";
		}
		appraisalInfo.setText(keyword);
		trafficInfo.setText(place.getTransportation());		
		entertainmentTips.setText(place.getTips());
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
				
				intent.setClass(EntertainmentDetailActivity.this, NearbyPlaceMap.class);
				startActivity(intent);
				break;
			case R.id.entertainment_detail_map_nearby:	
				intent.setClass(EntertainmentDetailActivity.this, NearbyPlaceMap.class);
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
		AlertDialog phoneCall = new AlertDialog.Builder(EntertainmentDetailActivity.this).create();
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
				EntertainmentDetailActivity.this.startActivity(intent);
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
	
	  /*@Override
		public boolean onKeyDown(int keyCode, KeyEvent event)
		{
			if (  keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
				 Intent intent = new Intent(this, entertainmentActivity.class);
				 startActivity(intent);
		        return true;
		    }
			else
			{
				  return super.onKeyDown(keyCode, event);	
			}
		  
		}*/
}
