/**  
        * @title RecommendedAppActivity.java  
        * @package com.damuzhi.travel.activity.more  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-20 下午1:14:08  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.more;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.damuzhi.travel.activity.adapter.more.RecommenedAppAdapter;
import com.damuzhi.travel.activity.adapter.overview.TravelRoutesAdapter;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.overview.TravelRoutesActivity;
import com.damuzhi.travel.activity.overview.TravelRoutesDetailActivity;
import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.mission.overview.TravelTipsMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.RecommendedApp;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.protos.TravelTipsProtos.TravelTipType;
import com.damuzhi.travel.R;
/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-20 下午1:14:08  
 */

public class RecommendedAppActivity extends MenuActivity
{
	private ListView listView;
	private List<RecommendedApp> recommendedApps = new ArrayList<RecommendedApp>();
	private ProgressDialog loadingDialog;
	private static final String TAG = "TravelTipsActivity";
	private TravelApplication application;
	private RecommenedAppAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommended_app);
		//TravelApplication.getInstance().addActivity(this);
		ActivityMange.getInstance().addActivity(this);
		listView = (ListView) findViewById(R.id.recommended_app_listview);
		listView.setOnItemClickListener(clickListener);
		adapter = new RecommenedAppAdapter(recommendedApps, this);
		listView.setAdapter(adapter);
		loadRecommendedApp();
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}
	
	
	private void refresh(List<RecommendedApp> list)
	{
		if(list !=null &&list.size()>0)
		{
			adapter.setRecommendApp(list);
			adapter.notifyDataSetChanged();
		}else {
			findViewById(R.id.data_not_found).setVisibility(View.VISIBLE);
		}
		
	}
	
	
	
	private void loadRecommendedApp()
	{
		AsyncTask<Void, Void, List<RecommendedApp>> task = new AsyncTask<Void, Void, List<RecommendedApp>>()
		{

			@Override
			protected List<RecommendedApp> doInBackground(Void... params)
			{
				return AppManager.getInstance().getRecommendedApp();
			}

			@Override
			protected void onCancelled()
			{
				super.onCancelled();
			}

			@Override
			protected void onPostExecute(List<RecommendedApp> result)
			{				
				recommendedApps = result;
				refresh(recommendedApps);
				loadingDialog.dismiss();
				super.onPostExecute(result);
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
					Intent intent = new Intent(RecommendedAppActivity.this,MoreActivity.class);
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
			 RecommendedApp recommendedApp = recommendedApps.get(arg2);
			 Uri appLink = Uri.parse(recommendedApp.getUrl());
			 Intent intent = new Intent(Intent.ACTION_VIEW,appLink);
			 startActivity(intent);
		}
	};
}
