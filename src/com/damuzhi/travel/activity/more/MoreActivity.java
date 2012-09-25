package com.damuzhi.travel.activity.more;




import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.damuzhi.travel.activity.common.ActivityManger;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.share.Share2Weibo;
import com.damuzhi.travel.activity.touristRoute.CommonBookingConfirm;
import com.damuzhi.travel.activity.touristRoute.CommonTouristRouteOrderList;
import com.damuzhi.travel.mission.more.MoreMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.util.SlidButton;
import com.damuzhi.travel.util.SlidButton.OnChangedListener;
import com.damuzhi.travel.util.TravelUtil;
import com.umeng.analytics.MobclickAgent;
import com.damuzhi.travel.R;
public class MoreActivity extends MenuActivity 
{
	private ViewGroup openCtiyGroup,browseHistoryGroup,feedback,about,recommendedApp,updateVersion,showImage,nonMemberOrder;
	private TextView currentCityName;
	private SlidButton slidButton;
	private Button loginButton;
	private boolean isShowListImage;
	private int IS_SHOW_RECOMMENDED_APP = -1;
	private int IS_SHOW_UPDATE_VERSION = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
		ActivityManger.getInstance().addActivity(this);
		MobclickAgent.updateOnlineConfig(this);
		String iS_SHOW_APP_FLAG = MobclickAgent.getConfigParams(MoreActivity.this, ConstantField.U_MENG_IS_SHOW_RECOMMENDED_APP);
		IS_SHOW_UPDATE_VERSION = Integer.parseInt(MobclickAgent.getConfigParams(MoreActivity.this, ConstantField.U_MENG_IS_SHOW_UPDATE_VERSION));
		IS_SHOW_RECOMMENDED_APP = Integer.parseInt(iS_SHOW_APP_FLAG);
		openCtiyGroup = (ViewGroup) findViewById(R.id.open_city_group);
		browseHistoryGroup = (ViewGroup) findViewById(R.id.browser_history_group);
		feedback = (ViewGroup) findViewById(R.id.feedback_group);
		about = (ViewGroup) findViewById(R.id.about_damuzhi_group);
		recommendedApp = (ViewGroup) findViewById(R.id.recommended_app_group);
		nonMemberOrder = (ViewGroup) findViewById(R.id.non_member_order_list);
		currentCityName = (TextView) findViewById(R.id.current_city_name);
		slidButton = (SlidButton) findViewById(R.id.is_show_list_image);
		updateVersion = (ViewGroup) findViewById(R.id.update_version_group);
		showImage = (ViewGroup) findViewById(R.id.show_image_group);
		loginButton = (Button) findViewById(R.id.login_button);
		currentCityName.setText(AppManager.getInstance().getCurrentCityName());
		openCtiyGroup.setOnClickListener(openCityOnClickListener);
		browseHistoryGroup.setOnClickListener(browseHistoryOnClickListener);
		feedback.setOnClickListener(feedbackOnClickListener);
		about.setOnClickListener(aboutOnClickListener);
		nonMemberOrder.setOnClickListener(nonMemberOrderListOnClickListener);
		loginButton.setOnClickListener(loginOnClickListener);
		if(IS_SHOW_RECOMMENDED_APP == 1)
		{
			recommendedApp.setOnClickListener(recommendedAppOnClickListener);
		}else
		{
			showImage.setBackgroundResource(R.drawable.more_bottom);
			recommendedApp.setVisibility(View.GONE);
		}
		if(IS_SHOW_UPDATE_VERSION == 1)
		{
			updateVersion.setOnClickListener(updateVersionClickListener);
		}else
		{
			updateVersion.setVisibility(View.GONE);
		}
		isShowListImage = MoreMission.getInstance().isShowListImage();
		slidButton.setCheck(isShowListImage);
		slidButton.SetOnChangedListener(changedListener);
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityManger.getInstance().finishActivity();
	}
	
	
	
	
	private OnClickListener openCityOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();			
			intent.setClass(MoreActivity.this, OpenCityActivity.class);
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
	
	
	private OnClickListener loginOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			intent.setClass(MoreActivity.this, LoginActivity.class);
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
				/*Uri uri = Uri.parse(MobclickAgent.getConfigParams(MoreActivity.this, ConstantField.U_MENG_DOWNLOAD_CONFIGURE));
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);*/
				updateAppVersion();
			}else
			{
				Toast.makeText(MoreActivity.this, getResources().getString(R.string.new_version), Toast.LENGTH_SHORT).show();
			}		
		}
	};
	
	
	
	private OnClickListener nonMemberOrderListOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MoreActivity.this, CommonTouristRouteOrderList.class);
			startActivity(intent);
			
		}
	};
	
	
	private void updateAppVersion()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(MoreActivity.this).create();
		alertDialog.setMessage(MoreActivity.this.getString(R.string.app_has_new_version));
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,MoreActivity.this.getString(R.string.update_now),new DialogInterface.OnClickListener()
		{					
			@Override
			public void onClick(DialogInterface dialog, int which)
			{	
				Uri uri = Uri.parse(MobclickAgent.getConfigParams(MoreActivity.this, ConstantField.U_MENG_DOWNLOAD_CONFIGURE));
				if(uri!=null&&!uri.equals(""))
				{
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}			
			}	
		} );
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+MoreActivity.this.getString(R.string.update_later),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		} );
		alertDialog.show();
	}
	
	
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (  keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
			 Intent intent = new Intent(this, IndexActivity.class);
			    startActivity(intent);
	        return false;
	    }
		else
		{
			  return super.onKeyDown(keyCode, event);	
		}
	}

}
