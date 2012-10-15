/**  
        * @title MyFavoriteRouteActivity.java  
        * @package com.damuzhi.travel.activity.favorite  
        * @description   
        * @author liuxiaokun  
        * @update 2012-10-4 下午3:59:10  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.favorite;

import java.util.ArrayList;
import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.touristRoute.CommonLocalTripsAdapter;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.MainActivity;
import com.damuzhi.travel.activity.touristRoute.CommonLocalTripsActivity;
import com.damuzhi.travel.activity.touristRoute.CommonLocalTripsDetailActivity;
import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.mission.favorite.FavoriteMission;
import com.damuzhi.travel.mission.touristRoute.TouristRouteMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.favorite.FavoriteManager;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRoute;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-10-4 下午3:59:10  
 */

public class MyFavoriteRouteActivity extends Activity
{

	protected static final String TAG = null;
	private ListView followRouteListView;
	List<LocalRoute> localRouteList = new ArrayList<LocalRoute>();
	//ImageLoader imageLoader;
	private CommonLocalTripsAdapter adapter;
	private ProgressBar loadingBar;
	private Button managerButton;
	private Button clearButton ;
	private Button deleteButton;
	private Button doneButton;
	boolean flag = false;
	boolean managerFlag = false;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.common_my_favorite_route);
		ActivityMange.getInstance().addActivity(this);
		managerButton = (Button) findViewById(R.id.mamger_button);
		clearButton = (Button) findViewById(R.id.clear_button);
		doneButton = (Button) findViewById(R.id.done_button);
		followRouteListView = (ListView) findViewById(R.id.local_trips_listview);
		loadingBar = (ProgressBar) findViewById(R.id.loading_progress);
		adapter = new CommonLocalTripsAdapter(localRouteList, MyFavoriteRouteActivity.this);
		followRouteListView.setAdapter(adapter);
		followRouteListView.setOnItemClickListener(onItemClickListener);
		managerButton.setOnClickListener(managerOnClickListener);
		doneButton.setOnClickListener(doneOnClickListener);
		clearButton.setOnClickListener(clearOnClickListener);
		followRouteListView.setOnScrollListener(onScrollListener);
		loadPlace();
	}
	
	
	
	
	
	private void loadPlace()
	{
		AsyncTask<String, Void, List<LocalRoute>> task = new AsyncTask<String, Void, List<LocalRoute>>()
		{

			@Override
			protected List<LocalRoute> doInBackground(String... params)
			{
				List<LocalRoute> list = FavoriteMission.getInstance().getMyFavoriteRoutes();
							
				return list;
			}

			@Override
			protected void onCancelled()
			{
				loadingBar.setVisibility(View.GONE);
				super.onCancelled();
			}

			@Override
			protected void onPostExecute(List<LocalRoute> resultList)
			{
				
				adapter.setLocalRouteList(resultList);
				adapter.notifyDataSetChanged();
				localRouteList.clear();
				localRouteList.addAll(resultList);
				loadingBar.setVisibility(View.GONE);
				super.onPostExecute(resultList);
			}

			@Override
			protected void onPreExecute()
			{
				// TODO show loading here
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
				intent.setClass(MyFavoriteRouteActivity.this, CommonLocalTripsDetailActivity.class);
				intent.putExtra("local_route",locaRouteDetail.toByteArray());
				startActivity(intent);
			}
			
			
		}
	};
	
	

	
	private OnClickListener managerOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			managerFlag = true;
			flag = true;
			clearButton.setVisibility(View.VISIBLE);
			doneButton.setVisibility(View.VISIBLE);
			v.setVisibility(View.GONE);
			for(int i=0;i<localRouteList.size();i++)
			{
				deleteButton = (Button) followRouteListView.findViewWithTag(i);
				if(deleteButton != null)
				{
					deleteButton.setVisibility(View.VISIBLE);
					deleteButton.setOnClickListener(deleteOnClickListener);
				}
				
			}
		}
	};

	private OnClickListener doneOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			flag = false;
			clearButton.setVisibility(View.GONE);
			v.setVisibility(View.GONE);
			managerButton.setVisibility(View.VISIBLE);
			for(int i=0;i<localRouteList.size();i++)
			{
				deleteButton = (Button) followRouteListView.findViewWithTag(i);
				if(deleteButton != null)
				{				
					deleteButton.setVisibility(View.GONE);
				}
				
			}
		}
	};


	private OnScrollListener onScrollListener = new OnScrollListener()
	{
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			
			
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount)
		{
			if(totalItemCount>visibleItemCount&&managerFlag)
			{
				for(int i=0;i<totalItemCount;i++)
				{
					deleteButton = (Button) followRouteListView.findViewWithTag(i);
					if(flag)
					{
						if(deleteButton != null&&deleteButton.getVisibility() == view.GONE)
						{
							deleteButton.setVisibility(View.VISIBLE);
							deleteButton.setOnClickListener(deleteOnClickListener);
						}
					}else
					{
						if(deleteButton != null&&deleteButton.getVisibility() == view.VISIBLE)
						{
							deleteButton.setVisibility(View.GONE);
						}
					}
				}
				
			}
			
			
		}
	};
	
	
	
	
	private OnClickListener clearOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			boolean result = FavoriteMission.getInstance().clearFavoriteRoute();
			if(result)
			{
				localRouteList.clear();
				adapter.setLocalRouteList(localRouteList);
				adapter.notifyDataSetChanged();
			}			
		}
	};
	
	
	private OnClickListener deleteOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			final int position = (Integer)v.getTag();
			AlertDialog leaveAlertDialog = new AlertDialog.Builder(MyFavoriteRouteActivity.this).create();
			leaveAlertDialog.setMessage(getBaseContext().getString(R.string.delete_rotue_follow_alert));
			leaveAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getBaseContext().getString(R.string.ok),new DialogInterface.OnClickListener()
			{				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					
					LocalRoute localRoute = localRouteList.get(position);
					boolean result = FavoriteMission.getInstance().deleteFavoriteRoute(localRoute.getRouteId());
					if(result)
					{
						localRouteList.remove(position);
						adapter.setLocalRouteList(localRouteList);
						adapter.notifyDataSetChanged();
					}
				}
			} );
			leaveAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+getBaseContext().getString(R.string.cancel),new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.cancel();			
				}
			} );
			leaveAlertDialog.show();
			
			
		}
	};
	
	

	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "myfavorite route  activity onResume");	
	}





	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}
	
}
