/**  
        * @title TravelRoutesViewCache.java  
        * @package com.damuzhi.travel.activity.adapter.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-23 ����1:39:29  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.adapter.common;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.damuzhi.travel.R;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-23 ����1:39:29  
 */

public class TravelRoutesViewCache
{
	private View convertView;
	private TextView travelTipsName;
	private TextView travelTipsIntro;
	private ImageView icon;
	/**  
	        * Constructor Method   
	        * @param convertView  
	        */
	public TravelRoutesViewCache(View convertView)
	{
		super();
		this.convertView = convertView;
	}
	
	public TextView getTravelRoutesName()
	{
		if(travelTipsName == null)
		{
			travelTipsName = (TextView) convertView.findViewById(R.id.travel_route_name);
		}
		return travelTipsName;
	}
	
	
	public TextView getTravelRoutesIntro()
	{
		if(travelTipsIntro == null)
		{
			travelTipsIntro = (TextView) convertView.findViewById(R.id.travel_route_driefIntro);
		}
		return travelTipsIntro;
	}
	
	public ImageView getTravelRoutesIcon()
	{
		if(icon == null)
		{
			icon = (ImageView) convertView.findViewById(R.id.travel_route_icon);
		}
		return icon;
	}

	
	
	
}
