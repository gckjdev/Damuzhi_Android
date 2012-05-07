package com.damuzhi.travel.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.more.OpenCityDataActivity;

public class OpenCityDataAdapter extends BaseAdapter
{
private String []city;
private OpenCityDataActivity context;
private ProgressBar downloadBar;
private TextView resultTextView;

	/**
 * @param city
 * @param context
 */
public OpenCityDataAdapter(String[] city, OpenCityDataActivity context)
{
	super();
	this.city = city;
	this.context = context;
}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return city.length;
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return position;
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
		if(convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.open_city_item, null);
		}
		convertView.setTag(position);
		Button btn = (Button) convertView.findViewById(R.id.data_city_download);
		downloadBar = (ProgressBar) convertView.findViewById(R.id.downloadbar);
		resultTextView = (TextView) convertView.findViewById(R.id.download_persent);
		btn.setTag("btn"+position);;
		resultTextView.setTag(1000+position);
		downloadBar.setTag("bar"+position);
		return convertView;
	}

	
	
	
	
}
