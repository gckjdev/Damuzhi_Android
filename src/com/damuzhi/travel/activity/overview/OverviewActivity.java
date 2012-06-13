/**  
        * @title CityBaseOverView.java  
        * @package com.damuzhi.travel.activity.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-22 ����11:23:39  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.overview;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.R.string;
import com.damuzhi.travel.activity.adapter.place.PlaceImageAdapter;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.PlaceActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.place.EntertainmentDetailActivity;
import com.damuzhi.travel.activity.place.HotelActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverview;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.service.Task;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-22 ����11:23:39  
 */

public class OverviewActivity extends TravelActivity implements PlaceActivity
{
	private static final String TAG = "CityBaseOverView";
	private WebView citybaseWebview;
	private CommonOverview citybaseOverview;
	private ArrayList<View> imageViewlist; 
	private TravelApplication application;
	private ImageView imageView;  
	private ImageView[] imageViews;  	
	private ViewPager citybaseImagePager;
	private ViewGroup main,group;
	private int loadFlag = 1;//�ж��Ƿ���¼���activity
	private Dialog loadingDialog;
	private static final int LOADING = 0;
	private static final int LOAD_OK = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MainService.allActivity.add(this);
		application = TravelApplication.getInstance();
		loadFlag = 0;
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		List<String> imagePath = citybaseOverview.getImagesList();
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
				Log.d(TAG, "image url = "+url);
			}		
			anseylodar.showimgAnsy(imageView, url, application.getDataFlag());
			imageViewlist.add(view);
		}
		imageViews = new ImageView[size];
		main = (ViewGroup) inflater.inflate(R.layout.overview, null);
		group = (ViewGroup) main.findViewById(R.id.overview_images_group);
		citybaseImagePager = (ViewPager) main.findViewById(R.id.overview_images);
		for (int i = 0; i < size; i++) {  
            imageView = new ImageView(OverviewActivity.this);  
            imageView.setLayoutParams(new LayoutParams(10,10));  
            imageView.setPadding(10, 0, 10, 0);  
            imageViews[i] = imageView;  
            if (i == 0) {  
                imageViews[i].setBackgroundResource(R.drawable.guide_dot_white);  
            } else {  
                imageViews[i].setBackgroundResource(R.drawable.guide_dot_black);  
            }  
            group.addView(imageView);  
        } 
		PlaceImageAdapter citybaseAdapter = new PlaceImageAdapter(imageViewlist);
		setContentView(main);
		citybaseImagePager.setAdapter(citybaseAdapter);
		citybaseImagePager.setOnPageChangeListener(pageChangeListener);
		TextView textView = (TextView) findViewById(R.id.overview_title);
		if(application.getOverviewType().equals(ConstantField.CITY_BASE))
		{
			textView.setText(getString(R.string.city_base));
		}else if (application.getOverviewType().equals(ConstantField.TRAVEL_PREPRATION))
		{
			textView.setText(getString(R.string.travel_prepration));
		}else if (application.getOverviewType().equals(ConstantField.TRAVEL_UTILITY))
		{
			textView.setText(getString(R.string.travel_utility));
		}else if (application.getOverviewType().equals(ConstantField.TRAVEL_TRANSPORTAION))
		{
			textView.setText(getString(R.string.travel_transportaion));
		}
		citybaseWebview = (WebView) findViewById(R.id.overview_webview);
		Log.d(TAG, "html = "+citybaseOverview.getHtml());
		citybaseWebview.loadUrl(citybaseOverview.getHtml());
	}

	
	@Override
	public void placeInfo()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Object...param)
	{
		// TODO Auto-generated method stub
		citybaseOverview = (CommonOverview) param[0];
		init();
		Message message = handler.obtainMessage();
		message.what = LOAD_OK;
		message.obj = null;
		handler.sendMessage(message);
	}
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what)
			{
			case LOADING:				
				break;
			case LOAD_OK:							
				loadingDialog.dismiss();	
				loadingDialog.cancel();
				break;			
			default:
				break;
			}
	}};
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "loadFlag = "+loadFlag);
		Log.d(TAG, "onResume ");
		super.onResume();
		if(loadFlag == 0)
		{			
			Log.d(TAG, "overview type = "+application.getOverviewType());
			loadFlag = 1;
			showRoundProcessDialog(OverviewActivity.this, R.layout.loading_process_dialog_anim);
			Task hotelTask = new Task(Task.OVERVIEW,OverviewActivity.this);
			MainService.newTask(hotelTask);
			Intent intent = new Intent(ConstantField.MAIN_SERVICE);
			startService(intent);	
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
	
	 public void showRoundProcessDialog(Context mContext, int layout)
	    {
	        OnKeyListener keyListener = new OnKeyListener()
	        {
	            @Override
	            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
	            {
	            	if ( keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
	            		loadingDialog.dismiss();
	            		Intent intent = new Intent(OverviewActivity.this, IndexActivity.class);
	   				    startActivity(intent);
	   				    return true;
		   		    }
		   			else
		   			{
		   				  return false;	
		   			}
	            }
	        };

	        loadingDialog = new AlertDialog.Builder(mContext).create();
	        loadingDialog.setOnKeyListener(keyListener);
	        loadingDialog.show();
	        // ע��˴�Ҫ����show֮�� ����ᱨ�쳣
	        loadingDialog.setContentView(layout);
	    }
}
