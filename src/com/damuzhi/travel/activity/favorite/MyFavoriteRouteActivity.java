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
import com.damuzhi.travel.activity.touristRoute.CommonLocalTripsActivity;
import com.damuzhi.travel.activity.touristRoute.CommonLocalTripsDetailActivity;
import com.damuzhi.travel.mission.favorite.FavoriteMission;
import com.damuzhi.travel.mission.touristRoute.TouristRouteMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.favorite.FavoriteManager;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRoute;

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
	private ListView localTripsListView;
	List<LocalRoute> localRouteList = new ArrayList<LocalRoute>();
	//ImageLoader imageLoader;
	private CommonLocalTripsAdapter adapter;
	private ProgressBar loadingBar;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.common_my_favorite_route);
		ActivityMange.getInstance().addActivity(this);
		localTripsListView = (ListView) findViewById(R.id.local_trips_listview);
		loadingBar = (ProgressBar) findViewById(R.id.loading_progress);
		adapter = new CommonLocalTripsAdapter(localRouteList, MyFavoriteRouteActivity.this);
		localTripsListView.setAdapter(adapter);
		localTripsListView.setOnItemClickListener(onItemClickListener);
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
