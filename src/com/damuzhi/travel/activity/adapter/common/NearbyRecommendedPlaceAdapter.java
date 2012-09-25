package com.damuzhi.travel.activity.adapter.common;

import java.util.List;

import com.damuzhi.travel.protos.PlaceListProtos.Place;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class NearbyRecommendedPlaceAdapter extends BaseAdapter {

	private List<Place> nearbyList;
	private Context context;
	
	
	
	public NearbyRecommendedPlaceAdapter(List<Place> nearbyList,Context context) {
		super();
		this.nearbyList = nearbyList;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(nearbyList == null)
		return 0;
		return nearbyList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return nearbyList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
}
