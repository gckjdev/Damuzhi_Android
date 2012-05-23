package com.damuzhi.travel.activity.adapter.place;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class PlaceImageAdapter extends PagerAdapter
{

	private ArrayList<View> list;
	
	
	/**
	 * @param list
	 */
	public PlaceImageAdapter(ArrayList<View> list)
	{
		super();
		this.list = list;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1)
	{
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		// TODO Auto-generated method stub
		((ViewPager) container).removeView(list.get(position));
	}

	@Override
	public int getItemPosition(Object object)
	{
		// TODO Auto-generated method stub
		return super.getItemPosition(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		// TODO Auto-generated method stub
		((ViewPager) container).addView(list.get(position));
		return list.get(position);
	}
	
	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

}
