package com.damuzhi.travel.activity.adapter.viewcache;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.damuzhi.travel.R;

public class LocalTripsViewCache {
	private View convertView;
	private ImageView imageView;
	private TextView agencyName;
	private TextView routeName;
	private TextView routePrice;
	private TextView routeTour;
	private TextView routeDays;
	private ImageView recommendImageView1;
	private ImageView recommendImageView2;
	private ImageView recommendImageView3;
	private ViewGroup agencyNameViewGroup;
	/**
	 * @param convertView
	 */
	public LocalTripsViewCache(View convertView)
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
	
	public TextView getAgencyName()
	{
		if(agencyName == null)
		{
			agencyName = (TextView) convertView.findViewById(R.id.agency_name);
		}
		return agencyName;
	}
	
	public TextView getRouteName()
	{
		if(routeName == null)
		{
			routeName = (TextView) convertView.findViewById(R.id.route_name);
		}
		return routeName;
	}
	
	public TextView getRouteDays()
	{
		if(routeDays == null)
		{
			routeDays = (TextView) convertView.findViewById(R.id.route_days);
		}
		return routeDays;
	}
	
	
	public TextView getRouteTour()
	{
		if(routeTour == null)
		{
			routeTour = (TextView) convertView.findViewById(R.id.route_tour);
		}
		return routeTour;
	}
	
	
	public TextView getRoutePrice()
	{
		if(routePrice == null)
		{
			routePrice = (TextView) convertView.findViewById(R.id.route_price);
		}
		return routePrice;
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
	
	public ViewGroup getAgencyNameViewGroup()
	{
		if(agencyNameViewGroup == null)
		{
			agencyNameViewGroup = (ViewGroup) convertView.findViewById(R.id.agency_name_group);
		}
		return agencyNameViewGroup;
	}
}
