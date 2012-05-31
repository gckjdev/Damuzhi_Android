/**  
        * @title NearListAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-31 下午12:23:28  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.adapter.place;

import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.NearListViewCache;
import com.damuzhi.travel.activity.adapter.common.PlaceViewCache;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-31 下午12:23:28  
 */

public class NearbyPlaceListAdapter extends BaseAdapter
{
	 private static final String TAG = "NearbyPlaceListAdapter";
	 private Context context;
	 private Place locatePlace;
	 private List<Place> nearbyPlaceList;
	 

	
	public NearbyPlaceListAdapter(Context context, Place place, List<Place> placeList)
	{
		super();
		this.context = context;
		this.locatePlace = place;
		this.nearbyPlaceList = placeList;
	}

	@Override
	public int getCount()
	{
		
		if (nearbyPlaceList == null)
			return 0;
		
		return nearbyPlaceList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return nearbyPlaceList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		NearListViewCache viewCache; 
		Place place = nearbyPlaceList.get(position);
		if(convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.nearby_list_item, null);
			viewCache = new NearListViewCache(convertView);
			convertView.setTag(viewCache);
		}else {
			viewCache = (NearListViewCache) convertView.getTag();
		}
		if(position == 0)
		{
			convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.table4_top));
		}else if (position == nearbyPlaceList.size()) {
			convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.table4_down));
		}else {
			convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.table4_center));
		}
		ImageView placeCategoryImage = viewCache.getImageView();
		int placeCategoryIcon = TravelUtil.getPlaceCategoryImage(place.getCategoryId());
		placeCategoryImage.setImageDrawable(context.getResources().getDrawable(placeCategoryIcon));
		TextView placeName = viewCache.getPlaceName();
		TextView distance = viewCache.getPlaceDistance();
		
		
		placeName.setText(place.getName());
		String distanceStr = TravelUtil.getDistance(locatePlace.getLongitude(), locatePlace.getLatitude(),place.getLongitude(),place.getLatitude());
		distance.setText(distanceStr);
		ImageView recommendImageView1 = viewCache.getRecommendImageView1();
		ImageView recommendImageView2 = viewCache.getRecommendImageView2();
		ImageView recommendImageView3 = viewCache.getRecommendImageView3();
		int rank = place.getRank();
		switch (rank)
		{
		case 1:{
			recommendImageView1.setVisibility(View.VISIBLE);		
			}
			break;
		case 2:{
			recommendImageView1.setVisibility(View.VISIBLE);
			recommendImageView2.setVisibility(View.VISIBLE);
			}
			break;
		case 3:{
			recommendImageView1.setVisibility(View.VISIBLE);
			recommendImageView2.setVisibility(View.VISIBLE);
			recommendImageView3.setVisibility(View.VISIBLE);
			}
		break;
		default:
			break;
		}	
		return convertView;
	}

	public List<Place> getNearbyPlaceList()
	{
		return nearbyPlaceList;
	}

	public void setNearbyPlaceList(List<Place> nearbyPlaceList)
	{
		this.nearbyPlaceList = nearbyPlaceList;
	}

	

}
