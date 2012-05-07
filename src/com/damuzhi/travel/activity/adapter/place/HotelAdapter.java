package com.damuzhi.travel.activity.adapter.place;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import com.damuzhi.travel.R;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.PlaceListProtos.Place;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HotelAdapter extends BaseAdapter
{

	private Context context;
	private String datapath ;
	private ArrayList<Place> list;
	private HashMap<Integer, NameIdPair> subCatMap;
	private HashMap<Integer, NameIdPair> proSerMap;
	private HashMap<String, Double> location;
	private HashMap<Integer, City> cityMap;
	private HashMap<Integer, CityArea> cityAreaMap;



	

	
	/**
	 * @param context
	 * @param datapath
	 * @param list
	 * @param subCatMap
	 * @param proSerMap
	 * @param location
	 * @param cityMap
	 * @param cityAreaMap
	 */
	public HotelAdapter(Context context, String datapath,
			ArrayList<Place> list, HashMap<Integer, NameIdPair> subCatMap,
			HashMap<Integer, NameIdPair> proSerMap,
			HashMap<String, Double> location, HashMap<Integer, City> cityMap,
			HashMap<Integer, CityArea> cityAreaMap)
	{
		super();
		this.context = context;
		this.datapath = datapath;
		this.list = list;
		this.subCatMap = subCatMap;
		this.proSerMap = proSerMap;
		this.location = location;
		this.cityMap = cityMap;
		this.cityAreaMap = cityAreaMap;
	}

	
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return list.size();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.scenery_list_item, null);
		}
		Place place = list.get(position);
		NameIdPair subCatName= subCatMap.get(place.getSubCategoryId());
		ImageView imageView = (ImageView) convertView.findViewById(R.id.scenery_image);
		TextView sceneryName = (TextView) convertView.findViewById(R.id.scenery_name);
		TextView sceneryPrice = (TextView) convertView.findViewById(R.id.scenery_price);
		TextView sceneryTag = (TextView) convertView.findViewById(R.id.scenery_tag);
		TextView sceneryArea = (TextView) convertView.findViewById(R.id.scenery_city_area);
		ImageView recommendImageView1 = (ImageView) convertView.findViewById(R.id.scenery_recommend_image1);
		ImageView recommendImageView2 = (ImageView) convertView.findViewById(R.id.scenery_recommend_image2);
		ImageView recommendImageView3 = (ImageView) convertView.findViewById(R.id.scenery_recommend_image3);
		TextView sceneryDistance = (TextView) convertView.findViewById(R.id.scenery_city_distance);
		int rank = place.getRank();
		switch (rank)
		{
		case 1:{
			recommendImageView1.setImageDrawable(context.getResources().getDrawable(R.drawable.good));
			recommendImageView2.setImageDrawable(context.getResources().getDrawable(R.drawable.good2));
			recommendImageView3.setImageDrawable(context.getResources().getDrawable(R.drawable.good2));
			}
			break;
		case 2:{
			recommendImageView1.setImageDrawable(context.getResources().getDrawable(R.drawable.good));
			recommendImageView2.setImageDrawable(context.getResources().getDrawable(R.drawable.good));
			recommendImageView3.setImageDrawable(context.getResources().getDrawable(R.drawable.good2));
			}
			break;
		case 3:{
			recommendImageView1.setImageDrawable(context.getResources().getDrawable(R.drawable.good));
			recommendImageView2.setImageDrawable(context.getResources().getDrawable(R.drawable.good));
			recommendImageView3.setImageDrawable(context.getResources().getDrawable(R.drawable.good));
			}
		break;
		default:
			break;
		}
		FileInputStream fileInputStream;
		try
		{
			fileInputStream = new FileInputStream(new File(datapath+place.getIcon()));
			Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
			imageView.setImageBitmap(bitmap);
			sceneryName.setText(place.getName());
			sceneryPrice.setText(cityMap.get(place.getCityId()).getCurrencySymbol()+place.getPrice());
			sceneryTag.setText(subCatName.getName());
			sceneryArea.setText(cityAreaMap.get(place.getAreaId()).getAreaName());
			sceneryDistance.setText("100m");
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return convertView;
	}

}
