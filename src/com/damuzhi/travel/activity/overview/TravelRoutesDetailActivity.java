/**  
        * @title TravelRoutesDetailActivity.java  
        * @package com.damuzhi.travel.activity.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-14 下午4:44:03  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.overview;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.PlaceImageAdapter;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.google.protobuf.InvalidProtocolBufferException;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-14 下午4:44:03  
 */

public class TravelRoutesDetailActivity extends MenuActivity
{

	private static final String TAG = "TravelTipsDetailActivity";
	
	private ArrayList<View> imageViewlist; 
	private ImageView imageView;  
	private ImageView[] imageViews;  	
	private ViewPager overviewImagePager;
	private ViewGroup main,group;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		TravelApplication.getInstance().addActivity(this);
		byte[] travelInfo = getIntent().getByteArrayExtra(ConstantField.TRAVEL_ROUTES_INFO);
		if(travelInfo.length>0)
		{
			CommonTravelTip commonTravelTip = null;
			try
			{
				commonTravelTip = CommonTravelTip.parseFrom(travelInfo);
			} catch (InvalidProtocolBufferException e)
			{
				e.printStackTrace();
			}
			LayoutInflater inflater = getLayoutInflater();
			main = (ViewGroup) inflater.inflate(R.layout.travel_routes_detail, null);
			List<String> imagePath = commonTravelTip.getImagesList();
			imageViewlist = new ArrayList<View>();
			int size=imagePath.size();
			for(int i=0;i<size;i++)
			{
				Anseylodar anseylodar = new Anseylodar();
				View view = inflater.inflate(R.layout.place_detail_image, null);
				ImageView imageView = (ImageView) view.findViewById(R.id.place_image_item);
				String url  = imagePath.get(i);
				anseylodar.showimgAnsy(imageView,url);	
				imageViewlist.add(view);
			}
			imageViews = new ImageView[size];
			group = (ViewGroup) main.findViewById(R.id.travel_routes_images_group);
			overviewImagePager = (ViewPager) main.findViewById(R.id.routes_images);
			for (int i = 0; i < size; i++) {  
	            imageView = new ImageView(TravelRoutesDetailActivity.this);  	            
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
			PlaceImageAdapter citybaseAdapter = new PlaceImageAdapter(imageViewlist);
			overviewImagePager.setAdapter(citybaseAdapter);
			overviewImagePager.setOnPageChangeListener(pageChangeListener);
			main.findViewById(R.id.image_group).setVisibility(View.VISIBLE);
				
			setContentView(main);
			TextView routesName = (TextView) findViewById(R.id.travel_routes_title);
			TextView routesDetailIntro = (TextView) findViewById(R.id.travel_routes_detail_intro);
			routesName.setText(commonTravelTip.getName());
			routesDetailIntro.setText(commonTravelTip.getDetailIntro());
		}
		
	}
	
	
	private OnPageChangeListener pageChangeListener  = new OnPageChangeListener()
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
}
