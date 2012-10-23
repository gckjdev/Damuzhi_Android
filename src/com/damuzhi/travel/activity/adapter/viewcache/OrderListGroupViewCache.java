/**  
        * @title OrderListGroupViewCache.java  
        * @package com.damuzhi.travel.activity.adapter.viewcache  
        * @description   
        * @author liuxiaokun  
        * @update 2012-10-17 下午4:28:15  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.adapter.viewcache;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.damuzhi.travel.R;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-10-17 下午4:28:15  
 */

public class OrderListGroupViewCache
{
	private View convertView;
	private TextView bookingId;
	private TextView bookingDate;
	private ImageView imageView;
	private ViewGroup viewGroup;
	/**
	 * @param convertView
	 */
	public OrderListGroupViewCache(View convertView)
	{
		super();
		this.convertView = convertView;
	}
	
	
	public ImageView getImageView()
	{
		if(imageView == null)
		{
			imageView = (ImageView) convertView.findViewById(R.id.line_image);
		}
		return imageView;
	}
	
	
	public ViewGroup getViewGroup()
	{
		if(viewGroup == null)
		{
			viewGroup = (ViewGroup) convertView.findViewById(R.id.expandableListView_group);
		}
		return viewGroup;
	}
	
	
	public TextView getBookingId()
	{
		if(bookingId == null)
		{
			bookingId = (TextView) convertView.findViewById(R.id.booking_id);
		}
		return bookingId;
	}
	
	
	public TextView getBookingDate()
	{
		if(bookingDate == null)
		{
			bookingDate = (TextView) convertView.findViewById(R.id.booking_time);
		}
		return bookingDate;
	}
	
}
