package com.damuzhi.travel.activity.adapter.viewcache;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.damuzhi.travel.R;
public class PlaceViewCache
{
	private View convertView;
	private ImageView imageView;
	private TextView placeName;
	private TextView placePrice;
	private TextView placeTag;
	private TextView placeLevel;
	private TextView placeArea;
	private TextView placeDistance;
	private TextView foodType;
	private ImageView recommendImageView1;
	private ImageView recommendImageView2;
	private ImageView recommendImageView3;
	private ImageView heart;
	private ImageView delete;
	private ViewGroup serviceGroup;
	/**
	 * @param convertView
	 */
	public PlaceViewCache(View convertView)
	{
		super();
		this.convertView = convertView;
	}
	
	
	public ImageView getImageView()
	{
		if(imageView == null)
		{
			imageView = (ImageView) convertView.findViewById(R.id.place_image);
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
	
	public TextView getPlacePrice()
	{
		if(placePrice == null)
		{
			placePrice = (TextView) convertView.findViewById(R.id.place_price);
		}
		return placePrice;
	}
	
	public TextView getPlaceTag()
	{
		if(placeTag == null)
		{
			placeTag = (TextView) convertView.findViewById(R.id.place_tag);
		}
		return placeTag;
	}
	
	
	
	
	
	public ImageView getRecommendImageView1()
	{
		if(recommendImageView1 == null)
		{
			recommendImageView1 = (ImageView) convertView.findViewById(R.id.place_recommend_image1);
		}
		return recommendImageView1;
	}
	
	public ImageView getRecommendImageView2()
	{
		if(recommendImageView2 == null)
		{
			recommendImageView2 = (ImageView) convertView.findViewById(R.id.place_recommend_image2);
		}
		return recommendImageView2;
	}
	
	public ImageView getRecommendImageView3()
	{
		if(recommendImageView3 == null)
		{
			recommendImageView3 = (ImageView) convertView.findViewById(R.id.place_recommend_image3);
		}
		return recommendImageView3;
	}
	
	
	public TextView getPlaceArea()
	{
		if(placeArea == null)
		{
			placeArea = (TextView) convertView.findViewById(R.id.place_city_area);
		}
		return placeArea;
	}
	
	public TextView getPlaceDistance()
	{
		if(placeDistance == null)
		{
			placeDistance = (TextView) convertView.findViewById(R.id.place_city_distance);
		}
		return placeDistance;
	}
	
	
	public ViewGroup getServiceGroup()
	{
		if(serviceGroup == null)
		{
			serviceGroup = (ViewGroup) convertView.findViewById(R.id.pro_service_group);
		}
		return serviceGroup;
	}
	
	
	
	
	public ImageView getHeart()
	{
		if(heart == null)
		{
			heart = (ImageView) convertView.findViewById(R.id.place_favorite_image);
		}
		return heart;
	}
	
	
	public ImageView getDelete()
	{
		if(delete == null)
		{
			delete = (ImageView) convertView.findViewById(R.id.place_favorite_delete);
		}
		return delete;
	}
}
