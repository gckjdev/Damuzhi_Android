/**  
        * @title RecommenedAppAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.more  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-20 下午1:17:34  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.adapter.more;

import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.TravelRoutesViewCache;
import com.damuzhi.travel.protos.AppProtos.RecommendedApp;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.util.PicUtill;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class RecommenedAppAdapter extends BaseAdapter
{
	private List<RecommendedApp> recommendedApps;
	private Context context;
	
	
	
	public RecommenedAppAdapter(List<RecommendedApp> recommendedApps,Context context)
	{
		super();
		this.recommendedApps = recommendedApps;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return recommendedApps.size();
	}

	
	@Override
	public Object getItem(int position)
	{
		return recommendedApps.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.recommended_app_listview_item, null);			
		}
		RecommendedApp recommendedApp = recommendedApps.get(position);
		TextView recommendedAppName = (TextView) convertView.findViewById(R.id.recommended_app_name);
		TextView recommendedAppIntro = (TextView) convertView.findViewById(R.id.recommended_app_driefIntro);
		ImageView recommendedAppIcon = (ImageView) convertView.findViewById(R.id.recommended_app_icon);	
		recommendedAppName.setText(recommendedApp.getName());
		recommendedAppIntro.setText(recommendedApp.getDescription());
		String iconPath = recommendedApp.getIcon();
		Bitmap bitmap = PicUtill.getbitmapByImagePath(iconPath);
		recommendedAppIcon.setImageBitmap(bitmap);
		return convertView;
	}

	public List<RecommendedApp> getRecommendApp()
	{
		return recommendedApps;
	}

	public void setRecommendApp(List<RecommendedApp> recommendedApps)
	{
		this.recommendedApps = recommendedApps;
	}


}
