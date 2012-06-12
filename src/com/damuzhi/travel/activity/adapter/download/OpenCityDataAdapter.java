package com.damuzhi.travel.activity.adapter.download;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.more.OpenCityDataActivity;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.util.TravelUtil;

public class OpenCityDataAdapter extends BaseAdapter
{
	private static final String TAG = "OpenCityDataAdapter";
	private List<City> cityList;
	private Context context;
	private ProgressBar downloadBar;
	private TextView resultTextView;

	/**
	 * Constructor Method
	 * 
	 * @param cityList
	 * @param context
	 */
	public OpenCityDataAdapter(List<City> cityList, Context context)
	{
		super();
		this.cityList = cityList;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return cityList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return cityList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.open_city_item, null);
		}
		convertView.setTag(position);
		City city = cityList.get(position);
		if(city.getCityId() == AppManager.getInstance().getCurrentCityId())
		{
			ImageView dataSelectIcon = (ImageView) convertView.findViewById(R.id.data_staus);
			dataSelectIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.yes_s));
		}
		TextView dataCityName = (TextView) convertView.findViewById(R.id.data_city_name);
		TextView dataSize = (TextView) convertView.findViewById(R.id.data_size);
		ImageButton onlineButton = (ImageButton) convertView.findViewById(R.id.online_button);
		dataCityName.setText(city.getCityName());
		dataSize.setText(TravelUtil.getDataSize(city.getDataSize()));
		if(city.getDataSize() !=0)
		{	convertView.findViewById(R.id.start_download_manager_group).setVisibility(View.VISIBLE);		
			ImageButton startButton = (ImageButton) convertView.findViewById(R.id.start_download_button);
			startButton.setTag("btn" + position);
		}
		downloadBar = (ProgressBar) convertView.findViewById(R.id.downloadbar);
		resultTextView = (TextView) convertView.findViewById(R.id.download_persent);		
		resultTextView.setTag(1000 + position);
		downloadBar.setTag("bar" + position);
		return convertView;
	}

}
