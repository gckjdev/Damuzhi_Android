/**  
        * @title TravelTipsAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-22 ����4:14:35  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.adapter.overview;

import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.viewcache.PlaceViewCache;
import com.damuzhi.travel.activity.adapter.viewcache.TravelTipsViewCache;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView.Validator;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-22 ����4:14:35  
 */

public class TravelTipsAdapter extends BaseAdapter
{
	private List<CommonTravelTip> commonTravelTips;
	private Context context;
	
	
	

	
	public TravelTipsAdapter(List<CommonTravelTip> commonTravelTips,
			Context context)
	{
		super();
		this.commonTravelTips = commonTravelTips;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return commonTravelTips.size();
	}

	
	@Override
	public Object getItem(int position)
	{
		return commonTravelTips.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		TravelTipsViewCache viewCache; 
		if(convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.travel_guides_list_item, null);
			viewCache = new TravelTipsViewCache(convertView);
			convertView.setTag(viewCache);
		}else {
			viewCache = (TravelTipsViewCache) convertView.getTag();
		}
		if(position == 0)
		{
			viewCache.setBackground(context.getResources().getDrawable(R.drawable.select_bg_top));
		}else if (position == commonTravelTips.size()-1) {
			viewCache.setBackground(context.getResources().getDrawable(R.drawable.select_bg_down));
		}else {
			viewCache.setBackground(context.getResources().getDrawable(R.drawable.select_bg_center));
		}
		CommonTravelTip commonTravelTip = commonTravelTips.get(position);
		TextView travelTipsName = viewCache.getTravelTipsName();
		travelTipsName.setText(commonTravelTip.getName());
		
		return convertView;
	}

	public List<CommonTravelTip> getCommonTravelTips()
	{
		return commonTravelTips;
	}

	public void setCommonTravelTips(List<CommonTravelTip> commonTravelTips)
	{
		this.commonTravelTips = commonTravelTips;
	}

}
