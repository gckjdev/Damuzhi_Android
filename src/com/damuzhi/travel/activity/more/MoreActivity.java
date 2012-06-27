package com.damuzhi.travel.activity.more;




import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.more.MoreMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.util.SlidButton;
import com.damuzhi.travel.util.SlidButton.OnChangedListener;
import com.damuzhi.travel.util.TravelUtil;
import com.umeng.analytics.MobclickAgent;

public class MoreActivity extends MenuActivity 
{
	private ViewGroup openCtiyGroup,browseHistoryGroup,feedback,about,recommendedApp,updateVersion;
	private TextView currentCityName;
	private SlidButton slidButton;
	private boolean isShowListImage;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
		TravelApplication.getInstance().addActivity(this);
		MobclickAgent.updateOnlineConfig(this);
		openCtiyGroup = (ViewGroup) findViewById(R.id.open_city_group);
		browseHistoryGroup = (ViewGroup) findViewById(R.id.browser_history_group);
		feedback = (ViewGroup) findViewById(R.id.feedback_group);
		about = (ViewGroup) findViewById(R.id.about_damuzhi_group);
		recommendedApp = (ViewGroup) findViewById(R.id.recommended_app_group);
		currentCityName = (TextView) findViewById(R.id.current_city_name);
		slidButton = (SlidButton) findViewById(R.id.is_show_list_image);
		updateVersion = (ViewGroup) findViewById(R.id.update_version_group);
		currentCityName.setText(AppManager.getInstance().getCurrentCityName());
		openCtiyGroup.setOnClickListener(openCityOnClickListener);
		browseHistoryGroup.setOnClickListener(browseHistoryOnClickListener);
		feedback.setOnClickListener(feedbackOnClickListener);
		about.setOnClickListener(aboutOnClickListener);
		recommendedApp.setOnClickListener(recommendedAppOnClickListener);
		isShowListImage = MoreMission.getInstance().isShowListImage();
		slidButton.setCheck(isShowListImage);
		slidButton.SetOnChangedListener(changedListener);
		updateVersion.setOnClickListener(updateVersionClickListener);
	}
	
	private OnClickListener openCityOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();			
			intent.setClass(MoreActivity.this, OpenCityDataActivity.class);
			startActivity(intent);
		}
	};
	
	
	
	private OnClickListener browseHistoryOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();			
			intent.setClass(MoreActivity.this, BrowseHistoryActivity.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener feedbackOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();			
			intent.setClass(MoreActivity.this, FeedBackActivity.class);
			startActivity(intent);
		}
	};
	 
	
	private OnClickListener aboutOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			String about = getResources().getString(R.string.about_damuzhi);
			intent.putExtra(ConstantField.HELP_TITLE, about);
			intent.setClass(MoreActivity.this, HelpActiviy.class);
			startActivity(intent);
		}
	};
	
	
	private OnClickListener recommendedAppOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			intent.setClass(MoreActivity.this, RecommendedAppActivity.class);
			startActivity(intent);
		}
	};
	
	private OnChangedListener changedListener = new OnChangedListener()
	{
		
		@Override
		public void OnChanged(boolean CheckState)
		{
			MoreMission.getInstance().saveIsShowImage(CheckState, MoreActivity.this);			
		}
	};
	
	private OnClickListener updateVersionClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			float remoteVersion = MoreMission.getInstance().getNewVersion();
			float localVersion = TravelUtil.getVersionName(MoreActivity.this);
			if(remoteVersion>localVersion)
			{
				Uri uri = Uri.parse(MobclickAgent.getConfigParams(MoreActivity.this, ConstantField.U_MENG_DOWNLOAD_CONFIGURE));
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}else
			{
				Toast.makeText(MoreActivity.this, getResources().getString(R.string.new_version), Toast.LENGTH_SHORT).show();
			}		
		}
	};
	
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
		currentCityName.setText(AppManager.getInstance().getCurrentCityName());
		isShowListImage = MoreMission.getInstance().isShowListImage();
		slidButton.setCheck(isShowListImage);
	}



	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	

}
