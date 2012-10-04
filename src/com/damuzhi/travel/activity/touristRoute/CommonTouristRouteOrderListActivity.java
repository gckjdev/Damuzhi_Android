package com.damuzhi.travel.activity.touristRoute;

import java.util.ArrayList;
import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.touristRoute.CommonTouristRouteBooingOrderAdapter;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.place.CommonPlaceDetailActivity;
import com.damuzhi.travel.mission.touristRoute.TouristRouteMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.TouristRouteProtos.Order;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

public class CommonTouristRouteOrderListActivity extends Activity {

	private static final String TAG = "CommonTouristRouteOrderList";
	private ExpandableListView expandableListView;
	private List<Order> orderList = new ArrayList<Order>();
	private ProgressBar loadingBar;
	private CommonTouristRouteBooingOrderAdapter adapter;
	private Button consultButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_tourist_route_order_list);
		ActivityMange.getInstance().addActivity(this);
		expandableListView = (ExpandableListView) findViewById(R.id.tourist_route_order_list);
		loadingBar = (ProgressBar) findViewById(R.id.loading_progress);		
		consultButton = (Button) findViewById(R.id.consult_button);
		adapter = new CommonTouristRouteBooingOrderAdapter(CommonTouristRouteOrderListActivity.this, orderList);
		expandableListView.setAdapter(adapter);
		consultButton.setOnClickListener(consultOnClickListener);
		load();
	}
	
	
	private void load()
	{
		AsyncTask<Void, Void, List<Order>> loadTask = new AsyncTask<Void, Void, List<Order>>(){

			@Override
			protected List<Order> doInBackground(Void... params)
			{
				int cityId = AppManager.getInstance().getCurrentCityId();
				String orderType = ConstantField.TOURIST_ROUTE_LOCAL_ROUTE_BOOKIING_LIST;
				String userId = UserManager.getInstance().getUserId(CommonTouristRouteOrderListActivity.this);
				String loginId = TravelApplication.getInstance().getLoginID();
				String token = TravelApplication.getInstance().getToken();
				List<Order> result = TouristRouteMission.getInstance().getOrderList(cityId,orderType,userId,loginId,token);
				Log.d(TAG, "orderlist size = "+result.size());
				return result;
			}

			@Override
			protected void onPostExecute(List<Order> result)
			{
				
				orderList.clear();
				orderList.addAll(result);
				refresh();				
				loadingBar.setVisibility(View.GONE);
				super.onPostExecute(result);
			}	
		};
			
		loadTask.execute();
			
	}
	
	
	private void refresh()
	{
		adapter.setOrderList(orderList);
		adapter.notifyDataSetChanged();
		for(int i= 0;i <orderList.size();i++)
		{
			expandableListView.expandGroup(i);
		}
	}

	
	private OnClickListener consultOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String phoneNumber = "4000008888";
			makePhoneCall(phoneNumber);
			
		}
	};
	
	
	
	public void makePhoneCall( final String phoneNumber)
	{
		AlertDialog phoneCall = new AlertDialog.Builder(CommonTouristRouteOrderListActivity.this).create();
		phoneCall.setMessage(getResources().getString(R.string.make_phone_call)+"\n"+phoneNumber);
		phoneCall.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.call),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+phoneNumber));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				CommonTouristRouteOrderListActivity.this.startActivity(intent);
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

}
