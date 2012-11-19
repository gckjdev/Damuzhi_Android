package com.damuzhi.travel.activity.touristRoute;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.touristRoute.CommonLocalTripsAdapter;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.location.LocationUtil;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.more.FeedBackActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.activity.place.CommonPlaceDetailActivity;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.mission.touristRoute.TouristRouteMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRoute;
import com.damuzhi.travel.protos.TouristRouteProtos.TouristRoute;

public class CommonLocalTripsActivity extends MenuActivity {

	protected static final String TAG = null;
	private ListView localTripsListView;
	private List<LocalRoute> localRouteList = new ArrayList<LocalRoute>();
	//ImageLoader imageLoader;
	private static int start = 0;
	private static int count = 1;
	private View listViewFooter;
	private ViewGroup footerViewGroup;
	private TextView noLocalRouteTextView;
	private ProgressDialog loadingDialog;
	private CommonLocalTripsAdapter adapter;
	private int currentCityId = 0;
	private int lastCityId = -100;
	private boolean loadDataFlag = false;
	private int totalCount = 0;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.common_lcoal_trips);
		ActivityMange.getInstance().addActivity(this);
		loadingDialog = new ProgressDialog(CommonLocalTripsActivity.this);
		localTripsListView = (ListView) findViewById(R.id.local_trips_listview);
		noLocalRouteTextView = (TextView) findViewById(R.id.no_local_route);
		listViewFooter = getLayoutInflater().inflate(R.layout.load_more_view, null, false);
		localTripsListView.addFooterView(listViewFooter, localRouteList, false);
		localTripsListView.setFooterDividersEnabled(false);
		footerViewGroup = (ViewGroup) listViewFooter.findViewById(R.id.listView_load_more_footer);
		footerViewGroup.setVisibility(View.GONE);
		
		adapter = new CommonLocalTripsAdapter(localRouteList, CommonLocalTripsActivity.this);
		localTripsListView.setAdapter(adapter);
		localTripsListView.setOnItemClickListener(onItemClickListener);
		localTripsListView.setOnScrollListener(listviewOnScrollListener);
	}
	
	
	
	
	
	private void loadPlace()
	{
		AsyncTask<String, Void, List<LocalRoute>> task = new AsyncTask<String, Void, List<LocalRoute>>()
		{

			@Override
			protected List<LocalRoute> doInBackground(String... params)
			{
				loadDataFlag = true;
				List<LocalRoute> list = TouristRouteMission.getInstance().getLocalRoutes(currentCityId);
				totalCount = TouristRouteMission.getInstance().getTotalCount();
				start = 0;
				count = 1;				
				return list;
			}

			@Override
			protected void onCancelled()
			{
				loadingDialog.dismiss();
				super.onCancelled();
			}

			@Override
			protected void onPostExecute(List<LocalRoute> resultList)
			{
				localRouteList.clear();
				localRouteList.addAll(resultList);
				refresh(resultList);
				loadingDialog.dismiss();
				super.onPostExecute(resultList);
			}

			@Override
			protected void onPreExecute()
			{
				// TODO show loading here
				showRoundProcessDialog();
				super.onPreExecute();
			}

		};

		task.execute();
	}

	private void refresh(List<LocalRoute> localRoutes)
	{
		if(localRoutes!= null&&localRoutes.size()>0)
		{
			adapter.setLocalRouteList(localRoutes);
			adapter.notifyDataSetChanged();
			if(noLocalRouteTextView.getVisibility() == View.VISIBLE)
			{
				noLocalRouteTextView.setVisibility(View.GONE);
			}
			localTripsListView.setSelection(0);
		}else
		{
			noLocalRouteTextView.setVisibility(View.VISIBLE);
		}
		
	}
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			LocalRoute localRoute = localRouteList.get(arg2);
			
			//LocalRoute locaRouteDetail = TouristRouteMission.getInstance().getLocalRouteDetail(localRoute.getRouteId());
			if(localRoute != null)
			{
				Intent intent = new Intent();
				intent.setClass(CommonLocalTripsActivity.this, CommonLocalTripsDetailActivity.class);
				intent.putExtra("local_route",localRoute.getRouteId());
				startActivity(intent);
			}
			
			
		}
	};
	
	
	
	private int visibleLastIndex = 0;
	private OnScrollListener listviewOnScrollListener = new OnScrollListener()
	{	
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			/* int size = adapter.getCount();
			if(size !=0 && visibleLastIndex == size)
			{
			   localTripsListView.removeFooterView(listViewFooter);	
			}
			if(visibleLastIndex >0)
			{			
			  if(scrollState ==OnScrollListener.SCROLL_STATE_IDLE &&visibleLastIndex == size)
			  {
				  Log.d(TAG, "load more");
				  Log.d(TAG, "listview visibleLastIndex = "+visibleLastIndex);	  
				  loadMore();
			  } 
			}*/
			
			
			//数据为空--不用继续下面代码了
			if(localRouteList.size() == 0) return;
			
			//判断是否滚动到底部
			boolean scrollEnd = false;
			try {
				if(view.getPositionForView(footerViewGroup) == view.getLastVisiblePosition()){
					Log.d(TAG, "footerview position = "+view.getPositionForView(footerViewGroup));
					Log.d(TAG, "visibleLastIndex position = "+visibleLastIndex);
					scrollEnd = true;
				}
			} catch (Exception e) {
				scrollEnd = false;
			}
			
			
			if(!loadDataFlag){
				return ;
			}
			
			if(totalCount !=0 && visibleLastIndex == totalCount)
			{
			   localTripsListView.removeFooterView(listViewFooter);	
			}
			  	
			  if(loadDataFlag&&scrollEnd)
			  {
				  footerViewGroup.setVisibility(View.VISIBLE);
				  Log.d(TAG, "load more");
				  Log.d(TAG, "listview visibleLastIndex = "+visibleLastIndex);	  
				  loadMore();
			  } 
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount)
		{
			visibleLastIndex = firstVisibleItem + visibleItemCount-1;
		}
	};

	
	private void loadMore()
	{
		Log.d(TAG, "load more data");
		AsyncTask<String, Void, List<LocalRoute>> task = new AsyncTask<String, Void, List<LocalRoute>>()
		{

			@Override
			protected List<LocalRoute> doInBackground(String... params)
			{
				loadDataFlag = false;
				List<LocalRoute> localRouteList = null;				
				start = count *20;
				count++;	
				localRouteList = TouristRouteMission.getInstance().loadMoreLocalRoutes(currentCityId,start);								
				return localRouteList;
			}
			@Override
			protected void onPostExecute(List<LocalRoute> resultList)
			{
				addMoreData(resultList);
				footerViewGroup.setVisibility(View.GONE);
				loadDataFlag = true;
				super.onPostExecute(resultList);
			}			
		};

		task.execute();		
	}
	
	
	private void addMoreData(List<LocalRoute> list)
	{
		if(list!= null &&list.size()>0)
		{
			localRouteList.addAll(list);			
			adapter.addPlaceList(list);
			adapter.notifyDataSetChanged();				
		}	
		return;
	}
	
	
	public void showRoundProcessDialog()
	{

		OnKeyListener keyListener = new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
				{
					loadingDialog.dismiss();
					return true;
				} else
				{
					return false;
				}
			}
		};

		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingDialog.setMessage(getResources().getString(R.string.loading));
		loadingDialog.setIndeterminate(false);
		loadingDialog.setCancelable(false);
		loadingDialog.setOnKeyListener(keyListener);
		loadingDialog.show();
	}





	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "commonLocalTrpis activity onResume");
		currentCityId = AppManager.getInstance().getCurrentCityId();
		if(currentCityId != lastCityId)
		{
			Log.d(TAG, "load local route ");
			lastCityId = currentCityId;
			loadPlace();
		}
		
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())	
		{		
		case R.id.menu_refresh:
			loadPlace();
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
		Log.d(TAG, "onDestroy");
		adapter.recycleBitmap();
		ActivityMange.getInstance().finishActivity();
	}





	@Override
	protected void onPause()
	{
		Log.d(TAG, "onPause");
		super.onPause();
	}





	@Override
	protected void onRestart()
	{
		Log.d(TAG, "onRestart");
		super.onRestart();
	}





	@Override
	protected void onStop()
	{
		Log.d(TAG,"onStop");
		super.onStop();
	}
	
}
