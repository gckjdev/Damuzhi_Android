/**  
        * @title CommonOverViewActivity.java  
        * @package com.damuzhi.travel.activity.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-14 上午9:30:40  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.overview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.PlaceImageAdapter;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.activity.common.mapview.CommonOverlayView;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.activity.place.CommonPlaceDetailActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverview;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-14 上午9:30:40  
 */

public abstract class CommonOverViewActivity extends MenuActivity
{

	private static final String TAG = "CommonOverViewActivity";
	abstract public CommonOverview loadData(Activity activity);
	abstract public boolean isSupportViewpager();
	abstract public String setTitleName();
	String titleName;
	private ProgressDialog loadingDialog;
	private ArrayList<View> imageViewlist; 
	private ImageView imageView;  
	private ImageView[] imageViews;  	
	private ViewPager overviewImagePager;
	private ViewGroup main,group;
	private WebView overviewWebview;
	 CommonOverview commonOverview;
	 Anseylodar anseylodar;
	@Override
	protected void onCreate(Bundle arg0)
	{
		
		super.onCreate(arg0);	
		TravelApplication.getInstance().addActivity(this);
		//refresh();
		loadCityOverView();	
		}
		
	private void refresh()
	{
		if(commonOverview!=null)
		{
			
			LayoutInflater inflater = getLayoutInflater();
			main = (ViewGroup) inflater.inflate(R.layout.common_overview, null);
			if(isSupportViewpager()){
				List<String> imagePath = commonOverview.getImagesList();
				imageViewlist = new ArrayList<View>();
				int size=imagePath.size();
				for(int i=0;i<size;i++)
				{
					anseylodar = new Anseylodar();
					View view = inflater.inflate(R.layout.place_detail_image, null);
					ImageView imageView = (ImageView) view.findViewById(R.id.place_image_item);
					String url  = imagePath.get(i);
					anseylodar.showimgAnsy(imageView,url);	
					imageViewlist.add(view);
				}
				imageViews = new ImageView[size];
				
				group = (ViewGroup) main.findViewById(R.id.overview_images_group);
				overviewImagePager = (ViewPager) main.findViewById(R.id.overview_images);
				for (int i = 0; i < size; i++) {  
		            imageView = new ImageView(CommonOverViewActivity.this);  
		            
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
			}
			overviewWebview = (WebView) main.findViewById(R.id.overview_webview);
			String url = TravelUtil.getHtmlUrl(commonOverview.getHtml());
			overviewWebview.loadUrl(url);			
			setContentView(main);
			TextView textView = (TextView) findViewById(R.id.overview_title);
			titleName = setTitleName();
			textView.setText(titleName);
		}else
		{
			setContentView(R.layout.common_overview);
			TextView textView = (TextView) findViewById(R.id.overview_title);
			titleName = setTitleName();
			textView.setText(titleName);
			findViewById(R.id.data_not_found).setVisibility(View.VISIBLE);
		}
	}
	
	
	
	private void loadCityOverView()
	{
		AsyncTask<Void, Void, CommonOverview> task = new AsyncTask<Void, Void, CommonOverview>()
		{

			@Override
			protected CommonOverview doInBackground(Void... params)
			{
				CommonOverview commonOverview = loadData(CommonOverViewActivity.this);
				return commonOverview;
			}

			@Override
			protected void onCancelled()
			{
				super.onCancelled();
			}

			@Override
			protected void onPostExecute(CommonOverview resultOverview)
			{				
				commonOverview = resultOverview;
				refresh();
				loadingDialog.dismiss();
				super.onPostExecute(commonOverview);
			}

			@Override
			protected void onPreExecute()
			{
				showRoundProcessDialog();
				super.onPreExecute();
			}

			

		};

		task.execute();
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
	
	
	
	public void showRoundProcessDialog()
	{

		OnKeyListener keyListener = new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getRepeatCount() == 0)
				{
					loadingDialog.dismiss();
					Intent intent = new Intent(CommonOverViewActivity.this,IndexActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					return true;
				} else
				{
					return false;
				}
			}
		};

		loadingDialog = new ProgressDialog(this);
		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingDialog.setMessage(getResources().getString(R.string.loading));
		loadingDialog.setIndeterminate(false);
		loadingDialog.setCancelable(false);
		loadingDialog.setOnKeyListener(keyListener);
		loadingDialog.show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())	
		{		
		case R.id.menu_refresh:
			loadCityOverView();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onDestroy()
	{
		if(anseylodar != null)
		{
			anseylodar.recycleBitmap();
		}	
		if(loadingDialog  != null)
		{
			loadingDialog.dismiss();
		}
	}
}
