/**  
        * @title NearListViewCache.java  
        * @package com.damuzhi.travel.activity.adapter.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-31 下午12:26:51  
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
 * @update 2012-5-31 下午12:26:51  
 */

public class NearListViewCache
{
	private View convertView;
	private ImageView imageView;
	private TextView placeName;
	private TextView placeDistance;
	private ImageView recommendImageView1;
	private ImageView recommendImageView2;
	private ImageView recommendImageView3;
	/**
	 * @param convertView
	 */
	public NearListViewCache(View convertView)
	{
		super();
		this.convertView = convertView;
	}
	
	
	public ImageView getImageView()
	{
		if(imageView == null)
		{
			imageView = (ImageView) convertView.findViewById(R.id.place_category);
		}
		return imageView;
	}
	
	public TextView getPlaceName()
	{
		if(placeName == null)
		{
			placeName = (TextView) convertView.findViewById(R.id.place_name);
		}
		return placeName;
	}
	
	
	public TextView getPlaceDistance()
	{
		if(placeDistance == null)
		{
			placeDistance = (TextView) convertView.findViewById(R.id.place_distance);
		}
		return placeDistance;
	}
	
	public ImageView getRecommendImageView1()
	{
		if(recommendImageView1 == null)
		{
			recommendImageView1 = (ImageView) convertView.findViewById(R.id.place_detail_recommend_image1);
		}
		return recommendImageView1;
	}
	
	public ImageView getRecommendImageView2()
	{
		if(recommendImageView2 == null)
		{
			recommendImageView2 = (ImageView) convertView.findViewById(R.id.place_detail_recommend_image2);
		}
		return recommendImageView2;
	}
	
	public ImageView getRecommendImageView3()
	{
		if(recommendImageView3 == null)
		{
			recommendImageView3 = (ImageView) convertView.findViewById(R.id.place_detail_recommend_image3);
		}
		return recommendImageView3;
	}
}
