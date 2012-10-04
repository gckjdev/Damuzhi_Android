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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.touristRoute.CommonLocalTripsAdapter;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.location.LocationUtil;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.mission.touristRoute.TouristRouteMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRoute;
import com.damuzhi.travel.protos.TouristRouteProtos.TouristRoute;

public class CommonLocalTripsActivity extends Activity {

	protected static final String TAG = null;
	private ListView localTripsListView;
	List<LocalRoute> localRouteList = new ArrayList<LocalRoute>();
	//ImageLoader imageLoader;
	private static int start = 0;
	private static int count = 1;
	private View listViewFooter;
	private ViewGroup footerViewGroup;
	private ProgressDialog loadingDialog;
	private CommonLocalTripsAdapter adapter;
	private int currentCityId = 0;
	private int lastCityId = -100;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.common_lcoal_trips);
		ActivityMange.getInstance().addActivity(this);
		loadingDialog = new ProgressDialog(CommonLocalTripsActivity.this);
		localTripsListView = (ListView) findViewById(R.id.local_trips_listview);
		//localRouteList = TouristRouteMission.getInstance().getLocalRoutes(cityId);
		listViewFooter = getLayoutInflater().inflate(R.layout.load_more_view, null, false);
		localTripsListView.addFooterView(listViewFooter, localRouteList, false);
		localTripsListView.setFooterDividersEnabled(false);
		footerViewGroup = (ViewGroup) listViewFooter.findViewById(R.id.listView_load_more_footer);
		footerViewGroup.setVisibility(View.GONE);
		
		//imageLoader = ImageLoader.getInstance();
		adapter = new CommonLocalTripsAdapter(localRouteList, CommonLocalTripsActivity.this);
		localTripsListView.setAdapter(adapter);
		localTripsListView.setOnItemClickListener(onItemClickListener);
		localTripsListView.setOnScrollListener(listviewOnScrollListener);
		//loadPlace();
	}
	
	
	
	
	
	private void loadPlace()
	{
		AsyncTask<String, Void, List<LocalRoute>> task = new AsyncTask<String, Void, List<LocalRoute>>()
		{

			@Override
			protected List<LocalRoute> doInBackground(String... params)
			{
				List<LocalRoute> list = TouristRouteMission.getInstance().getLocalRoutes(currentCityId);
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
				
				adapter.setLocalRouteList(resultList);
				adapter.notifyDataSetChanged();
				localRouteList.clear();
				localRouteList.addAll(resultList);
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

	
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			LocalRoute localRoute = localRouteList.get(arg2);
			
			LocalRoute locaRouteDetail = TouristRouteMission.getInstance().getLocalRouteDetail(localRoute.getRouteId());
			if(locaRouteDetail != null)
			{
				Intent intent = new Intent();
				intent.setClass(CommonLocalTripsActivity.this, CommonLocalTripsDetailActivity.class);
				intent.putExtra("local_route",locaRouteDetail.toByteArray());
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
			 int size = adapter.getCount();
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
		AsyncTask<String, Void, List<LocalRoute>> task = new AsyncTask<String, Void, List<LocalRoute>>()
		{

			@Override
			protected List<LocalRoute> doInBackground(String... params)
			{
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
			lastCityId = currentCityId;
			loadPlace();
		}
		
	}





	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}
	
}
