/**  
 * @title CommendPlaceActivity.java  
 * @package com.damuzhi.travel.activity.adapter.place  
 * @description   
 * @author liuxiaokun  
 * @update 2012-5-24 下午3:51:48  
 * @version V1.0  
 */
package com.damuzhi.travel.activity.place;

import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.CommonPlaceListAdapter;
import com.damuzhi.travel.protos.PlaceListProtos.Place;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @description
 * @version 1.0
 * @author liuxiaokun
 * @update 2012-5-24 下午3:51:48
 */

public abstract class CommonPlaceActivity extends Activity
{

	abstract public List<Place> getAllPlace();
	 
	abstract public String getCategoryName();
	
	abstract public String getCategorySize();
	
	abstract public int getCategoryType();
	
	abstract public void createFilterButtons(ViewGroup spinner,CommonPlaceListAdapter adapter);
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_place);
		
		TextView placeTitle = (TextView) findViewById(R.id.place_title);
		TextView placeSize = (TextView) findViewById(R.id.place_num);
		ViewGroup spinner = (ViewGroup) findViewById(R.id.spinner_group);
		
		
		ListView placeListView = (ListView) findViewById(R.id.place_listview);
		CommonPlaceListAdapter adapter = new CommonPlaceListAdapter(this, getAllPlace(),getCategoryType());
		placeListView.setAdapter(adapter);
		createFilterButtons(spinner,adapter);
		placeTitle.setText(getCategoryName());
		placeSize.setText(getCategorySize());
	}

}
