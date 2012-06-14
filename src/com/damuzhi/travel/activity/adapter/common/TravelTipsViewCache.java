/**  
        * @title TravelTipsViewCache.java  
        * @package com.damuzhi.travel.activity.adapter.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-22 ����4:22:00  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.adapter.common;

import com.damuzhi.travel.R;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-22 ����4:22:00  
 */

public class TravelTipsViewCache
{
	private View convertView;
	private TextView travelTipsName;
	/**  
	        * Constructor Method   
	        * @param convertView  
	        */
	public TravelTipsViewCache(View convertView)
	{
		super();
		this.convertView = convertView;
	}
	
	public TextView getTravelTipsName()
	{
		if(travelTipsName == null)
		{
			travelTipsName = (TextView) convertView.findViewById(R.id.travel_tips_name);
		}
		return travelTipsName;
	}

	
	public void setBackground(Drawable drawable)
	{
		convertView.setBackgroundDrawable(drawable);
		
	}
	
}
