package com.damuzhi.travel.activity.adapter.common;

import com.damuzhi.travel.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewCache
{
	private View convertView;
	private ImageView imageView;
	private TextView sceneryName;
	private TextView sceneryPrice;
	private TextView sceneryTag;
	private TextView sceneryArea;
	private TextView sceneryDistance;
	private ImageView recommendImageView1;
	private ImageView recommendImageView2;
	private ImageView recommendImageView3;
	/**
	 * @param convertView
	 */
	public ViewCache(View convertView)
	{
		super();
		this.convertView = convertView;
	}
	
	
	public ImageView getImageView()
	{
		if(imageView == null)
		{
			imageView = (ImageView) convertView.findViewById(R.id.scenery_image);
		}
		return imageView;
	}
	
	public TextView getSceneryName()
	{
		if(sceneryName == null)
		{
			sceneryName = (TextView) convertView.findViewById(R.id.scenery_name);
		}
		return sceneryName;
	}
	
	public TextView getSceneryPrice()
	{
		if(sceneryPrice == null)
		{
			sceneryPrice = (TextView) convertView.findViewById(R.id.scenery_price);
		}
		return sceneryPrice;
	}
	
	public TextView getSceneryTag()
	{
		if(sceneryTag == null)
		{
			sceneryTag = (TextView) convertView.findViewById(R.id.scenery_tag);
		}
		return sceneryTag;
	}
	public TextView getSceneryArea()
	{
		if(sceneryArea == null)
		{
			sceneryArea = (TextView) convertView.findViewById(R.id.scenery_city_area);
		}
		return sceneryArea;
	}
	public TextView getSceneryDistance()
	{
		if(sceneryDistance == null)
		{
			sceneryDistance = (TextView) convertView.findViewById(R.id.scenery_city_distance);
		}
		return sceneryDistance;
	}
	public ImageView getRecommendImageView1()
	{
		if(recommendImageView1 == null)
		{
			recommendImageView1 = (ImageView) convertView.findViewById(R.id.scenery_recommend_image1);
		}
		return recommendImageView1;
	}
	public ImageView getRecommendImageView2()
	{
		if(recommendImageView2 == null)
		{
			recommendImageView2 = (ImageView) convertView.findViewById(R.id.scenery_recommend_image2);
		}
		return recommendImageView2;
	}
	public ImageView getRecommendImageView3()
	{
		if(recommendImageView3 == null)
		{
			recommendImageView3 = (ImageView) convertView.findViewById(R.id.scenery_recommend_image3);
		}
		return recommendImageView3;
	}
	
	
	
	
}
