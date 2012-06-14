/**  
        * @title TravelTipsActivity.java  
        * @package com.damuzhi.travel.activity.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-22 ����3:53:27  
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
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.overview.TravelTipsAdapter;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.PlaceActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.mission.TravelTipsMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverview;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTipList;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.service.Task;
import com.damuzhi.travel.util.CornerListView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-22 ����3:53:27  
 */

public class TravelTipsActivity extends MenuActivity
{
	private ListView listView;
	private List<CommonTravelTip> commonTravelTips = new ArrayList<CommonTravelTip>();
	private ProgressDialog loadingDialog;
	private static final String TAG = "TravelTipsActivity";
	private TravelApplication application;
	private TravelTipsAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_tips);
		listView = (ListView) findViewById(R.id.travel_tips_listview);
		listView.setOnItemClickListener(clickListener);
		adapter = new TravelTipsAdapter(commonTravelTips, this);
		listView.setAdapter(adapter);
		//refresh(commonTravelTips);
		loadTravelTips();
	}
	
	
	
	private void refresh(List<CommonTravelTip> list)
	{
		adapter.setCommonTravelTips(list);
		adapter.notifyDataSetChanged();
	}
	
	
	
	private void loadTravelTips()
	{
		AsyncTask<Void, Void, CommonTravelTipList> task = new AsyncTask<Void, Void, CommonTravelTipList>()
		{

			@Override
			protected CommonTravelTipList doInBackground(Void... params)
			{
				int currentCityId = AppManager.getInstance().getCurrentCityId();
				return TravelTipsMission.getInstance().getTravelTips(ConstantField.TRAVEL_TIPS_LIST, currentCityId, TravelTipsActivity.this);
			}

			@Override
			protected void onCancelled()
			{
				super.onCancelled();
			}

			@Override
			protected void onPostExecute(CommonTravelTipList commonTravelTipList)
			{
			
				commonTravelTips = commonTravelTipList.getTipListList();
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
					Intent intent = new Intent(TravelTipsActivity.this,IndexActivity.class);
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
			 intent.putExtra(ConstantField.TRAVEL_TIPS_INFO, commonTravelTip.toByteArray());
			 intent.setClass(TravelTipsActivity.this, TravelTipsDetailActivity.class);
			 startActivity(intent);
		}
	};

}
