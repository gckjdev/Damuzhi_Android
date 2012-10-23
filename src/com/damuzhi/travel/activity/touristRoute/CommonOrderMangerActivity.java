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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
	private ViewGroup flyRouteOrderMamager;
	private ViewGroup customerServicePhone;
	private Button loginButton;
	private Button loginExitButton;
	private String token = "";
	private static final String SERVICE_PHONE_NUMBER = "4000-223-321";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_order_mamager);
		ActivityMange.getInstance().addActivity(this);
		localRouteOrderMamager = (ViewGroup) findViewById(R.id.local_route_order_mamager);
		flyRouteOrderMamager = (ViewGroup) findViewById(R.id.fly_route_order_mamager);
		customerServicePhone = (ViewGroup) findViewById(R.id.customer_service_phone);
		TextView customerServicePhoneNumber = (TextView) findViewById(R.id.customer_service_phone_number);
		customerServicePhoneNumber.setText(SERVICE_PHONE_NUMBER);
		loginButton = (Button) findViewById(R.id.login_button);
		loginExitButton = (Button) findViewById(R.id.login_exit_button);
		
		localRouteOrderMamager.setOnClickListener(localRouteOrderMamagerOnClickListener);
		loginButton.setOnClickListener(loginOnClickListener);
		loginExitButton.setOnClickListener(loginExitOnClickListener);
		customerServicePhone.setOnClickListener(customerServiceOnClickListener);
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
	
	
	private OnClickListener customerServiceOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			makePhoneCall(SERVICE_PHONE_NUMBER);
			
		}
	};
	
	public void makePhoneCall( final String phoneNumber)
	{
		AlertDialog phoneCall = new AlertDialog.Builder(CommonOrderMangerActivity.this).create();
		phoneCall.setMessage(getResources().getString(R.string.make_phone_call)+"\n"+phoneNumber);
		phoneCall.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.call),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+phoneNumber));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				CommonOrderMangerActivity.this.startActivity(intent);
			}
		} );
		phoneCall.setButton(DialogInterface.BUTTON_NEGATIVE,""+getBaseContext().getString(R.string.cancel),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				
			}
		} );
		phoneCall.show();
		
	}
	
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
