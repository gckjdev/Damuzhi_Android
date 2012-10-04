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

import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.favorite.MyFavoriteRouteActivity;
import com.damuzhi.travel.activity.share.Share2Weibo;
import com.damuzhi.travel.activity.touristRoute.CommonBookingConfirmActivity;
import com.damuzhi.travel.activity.touristRoute.CommonOrderMangerActivity;
import com.damuzhi.travel.activity.touristRoute.CommonTouristRouteOrderListActivity;
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
	private static final String TAG = "MoreActivity";
	private ViewGroup openCtiyGroup,browseHistoryGroup,feedback,about,recommendedApp,updateVersion,showImage,nonMemberOrderMamger,orderManger,userInfo,myConcern;
	private TextView currentCityName;
	private SlidButton slidButton;
	private Button loginButton;
	private Button loginExitButton;
	private boolean isShowListImage;
	private int IS_SHOW_RECOMMENDED_APP = -1;
	private int IS_SHOW_UPDATE_VERSION = -1;
	private String token;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.more);
		ActivityMange.getInstance().addActivity(this);
		MobclickAgent.updateOnlineConfig(this);
		String iS_SHOW_APP_FLAG = MobclickAgent.getConfigParams(MoreActivity.this, ConstantField.U_MENG_IS_SHOW_RECOMMENDED_APP);
		IS_SHOW_UPDATE_VERSION = Integer.parseInt(MobclickAgent.getConfigParams(MoreActivity.this, ConstantField.U_MENG_IS_SHOW_UPDATE_VERSION));
		IS_SHOW_RECOMMENDED_APP = Integer.parseInt(iS_SHOW_APP_FLAG);
		openCtiyGroup = (ViewGroup) findViewById(R.id.open_city_group);
		browseHistoryGroup = (ViewGroup) findViewById(R.id.browser_history_group);
		feedback = (ViewGroup) findViewById(R.id.feedback_group);
		about = (ViewGroup) findViewById(R.id.about_damuzhi_group);
		recommendedApp = (ViewGroup) findViewById(R.id.recommended_app_group);
		nonMemberOrderMamger = (ViewGroup) findViewById(R.id.non_member_order_list);
		currentCityName = (TextView) findViewById(R.id.current_city_name);
		slidButton = (SlidButton) findViewById(R.id.is_show_list_image);
		updateVersion = (ViewGroup) findViewById(R.id.update_version_group);
		showImage = (ViewGroup) findViewById(R.id.show_image_group);
		loginButton = (Button) findViewById(R.id.login_button);
		loginExitButton = (Button) findViewById(R.id.login_exit_button);
		orderManger = (ViewGroup) findViewById(R.id.order_mamger);
		userInfo = (ViewGroup) findViewById(R.id.user_info);
		myConcern = (ViewGroup) findViewById(R.id.my_concern_group);
		currentCityName.setText(AppManager.getInstance().getCurrentCityName());
		openCtiyGroup.setOnClickListener(openCityOnClickListener);
		browseHistoryGroup.setOnClickListener(browseHistoryOnClickListener);
		feedback.setOnClickListener(feedbackOnClickListener);
		about.setOnClickListener(aboutOnClickListener);
		nonMemberOrderMamger.setOnClickListener(nonMemberOrderListOnClickListener);
		loginButton.setOnClickListener(loginOnClickListener);
		orderManger.setOnClickListener(OrderManagerOnClickListener);
		userInfo.setOnClickListener(userInfoOnClickListener);
		loginExitButton.setOnClickListener(loginExitOnClickListener);
		myConcern.setOnClickListener(myConcernOnClickListener);
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
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		ActivityMange.getInstance().finishActivity();
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
	
	private OnClickListener myConcernOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();			
			intent.setClass(MoreActivity.this, MyFavoriteRouteActivity.class);
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
	
	
	private OnClickListener loginExitOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			TravelApplication.getInstance().setToken("");
			TravelApplication.getInstance().setLoginID("");
			loginRefresh();
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
			intent.setClass(MoreActivity.this, CommonOrderMangerActivity.class);
			startActivity(intent);
			
		}
	};
	
	private OnClickListener OrderManagerOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MoreActivity.this, CommonOrderMangerActivity.class);
			startActivity(intent);
			
		}
	};
	
	private OnClickListener userInfoOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MoreActivity.this, CommonTouristRouteOrderListActivity.class);
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
		currentCityName.setText(AppManager.getInstance().getCurrentCityName());
		isShowListImage = MoreMission.getInstance().isShowListImage();
		slidButton.setCheck(isShowListImage);
		loginRefresh();
		Log.d(TAG, "onResume");
	}


	
	private void loginRefresh()
	{
		token = TravelApplication.getInstance().getToken();
		if(token != null && !token.equals(""))
		{
			nonMemberOrderMamger.setVisibility(View.GONE);
			orderManger.setVisibility(View.VISIBLE);
			userInfo.setVisibility(View.VISIBLE);
			loginExitButton.setVisibility(View.VISIBLE);
			loginButton.setVisibility(View.GONE);
		}else
		{
			nonMemberOrderMamger.setVisibility(View.VISIBLE);
			orderManger.setVisibility(View.GONE);
			userInfo.setVisibility(View.GONE);
			loginExitButton.setVisibility(View.GONE);
			loginButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}
	

}
