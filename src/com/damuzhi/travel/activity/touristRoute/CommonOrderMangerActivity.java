/**  
        * @title CommonOrderMangerActivity.java  
        * @package com.damuzhi.travel.activity.touristRoute  
        * @description   
        * @author liuxiaokun  
        * @update 2012-9-27 上午10:50:07  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.touristRoute;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.more.LoginActivity;
import com.damuzhi.travel.activity.more.MoreActivity;
import com.damuzhi.travel.mission.more.MoreMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRoute;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-9-27 上午10:50:07  
 */

public class CommonOrderMangerActivity extends Activity
{

	private static final String TAG = "CommonOrderMangerActivity";
	private ViewGroup localRouteOrderMamager;
	private ViewGroup groupRouteOrderMamager;
	private ViewGroup flyRouteOrderMamager;
	private Button loginButton;
	private Button loginExitButton;
	private String token = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_order_mamager);
		ActivityMange.getInstance().addActivity(this);
		localRouteOrderMamager = (ViewGroup) findViewById(R.id.local_route_order_mamager);
		groupRouteOrderMamager = (ViewGroup) findViewById(R.id.group_route_order_mamager);
		flyRouteOrderMamager = (ViewGroup) findViewById(R.id.fly_route_order_mamager);
		loginButton = (Button) findViewById(R.id.login_button);
		loginExitButton = (Button) findViewById(R.id.login_exit_button);
		
		localRouteOrderMamager.setOnClickListener(localRouteOrderMamagerOnClickListener);
		loginButton.setOnClickListener(loginOnClickListener);
		loginExitButton.setOnClickListener(loginExitOnClickListener);
	}
	
	
	private OnClickListener localRouteOrderMamagerOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			intent.setClass(CommonOrderMangerActivity.this, CommonTouristRouteOrderListActivity.class);
			startActivity(intent);
			
		}
	};
	
	
	private OnClickListener loginOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			intent.setClass(CommonOrderMangerActivity.this, LoginActivity.class);
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
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}

	
	@Override
	protected void onResume()
	{
		super.onResume();
		//MobclickAgent.onResume(this);
		loginRefresh();
		Log.d(TAG, "onResume");
	}


	
	private void loginRefresh()
	{
		token = TravelApplication.getInstance().getToken();
		if(token != null && !token.equals(""))
		{
			
			loginExitButton.setVisibility(View.VISIBLE);
			loginButton.setVisibility(View.GONE);
		}else
		{
			loginExitButton.setVisibility(View.GONE);
			loginButton.setVisibility(View.VISIBLE);
		}
	}
}
