/**  
        * @title BrowseHistoryActivity.java  
        * @package com.damuzhi.travel.activity.more  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-18 上午11:29:48  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.more;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.more.BrowseHistoryAdapter;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.activity.place.CommonPlaceDetailActivity;
import com.damuzhi.travel.mission.more.BrowseHistoryMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;



public class BrowseHistoryActivity extends MenuActivity
{

	private BrowseHistoryAdapter adapter;
	private List<Place> list = new ArrayList<Place>();;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_history);
		TravelApplication.getInstance().addActivity(this);
		ListView historyListView = (ListView) findViewById(R.id.history_listview);
		ImageButton clearButton = (ImageButton) findViewById(R.id.clear_button);
		clearButton.setOnClickListener(clearListener);
		list.addAll(BrowseHistoryMission.getInstance().loadBrowseHistory());
		if(list!=null && list.size()>0)
		{
			adapter = new BrowseHistoryAdapter(this, list);
			historyListView.setAdapter(adapter);
		}
		historyListView.setOnItemClickListener(itemClickListener);
	}
	
	
	private OnItemClickListener itemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			Place place = adapter.getPlaceList().get(arg2);
			Intent intent = new Intent();
			intent.putExtra(ConstantField.PLACE_DETAIL, place.toByteArray());
			Class detailPlaceClass = CommonPlaceDetailActivity.getClassByPlaceType(place.getCategoryId());
			intent.setClass(BrowseHistoryActivity.this, detailPlaceClass);
			startActivity(intent);
			
		}
	};
	
	
	private OnClickListener clearListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			BrowseHistoryMission.getInstance().clearHistory();
			list.clear();
			adapter.setList(list);
			adapter.notifyDataSetChanged();
		}
	};

}
