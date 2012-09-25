/**  
        * @title TravelRouteActivity.java  
        * @package com.damuzhi.travel.activity.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-23 ����1:33:19  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.overview;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.damuzhi.travel.activity.adapter.overview.TravelRoutesAdapter;
import com.damuzhi.travel.activity.adapter.overview.TravelTipsAdapter;
import com.damuzhi.travel.activity.common.ActivityManger;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.mission.overview.TravelTipsMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTipList;
import com.damuzhi.travel.protos.TravelTipsProtos.TravelTipType;
import com.damuzhi.travel.R;


public class TravelRoutesActivity extends MenuActivity
{
	private ListView listView;
	private List<CommonTravelTip> commonTravelTips = new ArrayList<CommonTravelTip>();
	private ProgressDialog loadingDialog;
	private static final String TAG = "TravelTipsActivity";
	private TravelRoutesAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_route);
	//	TravelApplication.getInstance().addActivity(this);
		ActivityManger.getInstance().addActivity(this);
		listView = (ListView) findViewById(R.id.travel_route_listview);
		listView.setOnItemClickListener(clickListener);
		adapter = new TravelRoutesAdapter(commonTravelTips, this);
		listView.setAdapter(adapter);
		loadTravelTips();
	}
	
	
	
	private void refresh(List<CommonTravelTip> list)
	{
		if(list!=null &&list.size()>0)
		{
			adapter.setCommonTravelTips(list);
			adapter.notifyDataSetChanged();
		}else
		{
			findViewById(R.id.data_not_found).setVisibility(View.VISIBLE);
		}		
	}
	
	
	
	private void loadTravelTips()
	{
		AsyncTask<Void, Void, List<CommonTravelTip>> task = new AsyncTask<Void, Void, List<CommonTravelTip>>()
		{

			@Override
			protected List<CommonTravelTip> doInBackground(Void... params)
			{
				int currentCityId = AppManager.getInstance().getCurrentCityId();
				return TravelTipsMission.getInstance().getTravelTips(TravelTipType.ROUTE_VALUE, currentCityId, TravelRoutesActivity.this);
			}

			@Override
			protected void onCancelled()
			{
				super.onCancelled();
			}

			@Override
			protected void onPostExecute(List<CommonTravelTip> commonTravelTipList)
			{				
				commonTravelTips = commonTravelTipList;
				refresh(commonTravelTips);
				loadingDialog.dismiss();
				super.onPostExecute(commonTravelTipList);
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
					Intent intent = new Intent(TravelRoutesActivity.this,IndexActivity.class);
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
		loadingDialog.setCancelable(true);
		loadingDialog.setOnKeyListener(keyListener);
		loadingDialog.show();
	}
	
	
	
	
	
	 private OnItemClickListener clickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			 CommonTravelTip commonTravelTip = commonTravelTips.get(arg2);
			 /*application.setCommonTravelTip(commonTravelTip);*/
			 Intent intent = new Intent();
			 intent.putExtra(ConstantField.TRAVEL_ROUTES_INFO, commonTravelTip.toByteArray());
			 intent.setClass(TravelRoutesActivity.this, TravelRoutesDetailActivity.class);
			 startActivity(intent);
		}
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())	
		{		
		case R.id.menu_refresh:
			loadTravelTips();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}



	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(loadingDialog  != null)
		{
			loadingDialog.dismiss();
		}
		ActivityManger.getInstance().finishActivity();
	}

}
