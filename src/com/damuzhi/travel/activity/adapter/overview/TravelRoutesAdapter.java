/**  
        * @title TravelRoutesAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-23 ����1:36:36  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.adapter.overview;

import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.viewcache.TravelRoutesViewCache;
import com.damuzhi.travel.activity.adapter.viewcache.TravelTipsViewCache;
import com.damuzhi.travel.activity.common.imageCache.AsyncLoader;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.util.PicUtill;
import com.damuzhi.travel.util.TravelUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 * @update 2012-5-23 ����1:36:36  
 */

public class TravelRoutesAdapter extends BaseAdapter
{

	private List<CommonTravelTip> commonTravelTips;
	private Context context;
	private AsyncLoader asyncLoader;
	private TextView travelRouteName;
	private TextView travelRouteIntro;
	private ImageView travelRouteIcon;
	
	public TravelRoutesAdapter(List<CommonTravelTip> commonTravelTips,
			Context context)
	{
		super();
		this.commonTravelTips = commonTravelTips;
		this.context = context;
		asyncLoader = AsyncLoader.getInstance();
		//asyncLoader = new AsyncLoader();
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
		TravelRoutesViewCache viewCache; 
		if(convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.travel_routes_list_item, null);
			viewCache = new TravelRoutesViewCache(convertView);
			convertView.setTag(viewCache);
		}else {
			viewCache = (TravelRoutesViewCache) convertView.getTag();
		}
		CommonTravelTip commonTravelTip = commonTravelTips.get(position);
		travelRouteName = viewCache.getTravelRoutesName();
		travelRouteIntro = viewCache.getTravelRoutesIntro();
		travelRouteIcon = viewCache.getTravelRoutesIcon();		
		travelRouteName.setText(commonTravelTip.getName());
		travelRouteIntro.setText(commonTravelTip.getBriefIntro());
		String iconPath = commonTravelTip.getIcon();
		//Bitmap bitmap = PicUtill.getbitmapByImagePath(iconPath);
		//travelRouteIcon.setImageBitmap(bitmap);
		asyncLoader.showimgAnsy(travelRouteIcon, iconPath);
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
