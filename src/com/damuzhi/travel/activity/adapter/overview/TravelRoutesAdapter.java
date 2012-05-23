/**  
        * @title TravelRoutesAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-23 ÏÂÎç1:36:36  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.adapter.overview;

import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.TravelTipsViewCache;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-23 ÏÂÎç1:36:36  
 */

public class TravelRoutesAdapter extends BaseAdapter
{

	private List<CommonTravelTip> commonTravelTips;
	private Context context;
	
	
	

	/**  
	        * Constructor Method   
	        * @param commonTravelTips
	        * @param context  
	        */
	public TravelRoutesAdapter(List<CommonTravelTip> commonTravelTips,
			Context context)
	{
		super();
		this.commonTravelTips = commonTravelTips;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return commonTravelTips.size();
	}

	
	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return commonTravelTips.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		TravelTipsViewCache viewCache; 
		if(convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.travel_routes_list_item, null);
			viewCache = new TravelTipsViewCache(convertView);
			convertView.setTag(viewCache);
		}else {
			viewCache = (TravelTipsViewCache) convertView.getTag();
		}
		CommonTravelTip commonTravelTip = commonTravelTips.get(position);
		TextView travelTipsName = viewCache.getTravelTipsName();
		travelTipsName.setText(commonTravelTip.getName());
		
		return convertView;
	}

}
