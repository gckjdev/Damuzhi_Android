/**  
        * @title NearbyAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-18 ����2:58:44  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.adapter.place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.PlaceViewCache;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.LocationUtil;
import com.damuzhi.travel.util.TravelUtil;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-18 ����2:58:44  
 */

public class NearbyAdapter extends BaseAdapter
{
	private static final String TAG = "hotelAdapter";
	private Context context;
	private String datapath ;
	private ArrayList<Place> list;
	private double latitude;
	private double longitude;
	private String symbolMap;
	private HashMap<Integer, String> cityAreaMap;
	private int dataFlag;
	public Anseylodar anseylodar;
	private LayoutInflater inflater;
	private ImageView imageView;
	private ViewGroup serviceGroup;
	private TravelApplication application;
	/**
	 * @param context
	 * @param datapath
	 * @param list
	 * @param subCatMap
	 * @param proSerMap
	 * @param location
	 * @param cityMap
	 * @param cityAreaMap
	 * @param dataFlag
	 */
	public NearbyAdapter(Context context, String datapath,
			ArrayList<Place> list, 
			HashMap<String, Double> location,String symbolMap,
			HashMap<Integer, String> cityAreaMap, int dataFlag)
	{
		super();
		this.context = context;
		this.datapath = datapath;
		this.list = list;
		this.symbolMap = symbolMap;
		this.cityAreaMap = cityAreaMap;
		this.dataFlag = dataFlag;
		anseylodar = new Anseylodar();
		inflater = LayoutInflater.from(context);
		latitude = location.get(ConstantField.LATITUDE);
		longitude = location.get(ConstantField.LONGITUDE);
		application = TravelApplication.getInstance();
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
				PlaceViewCache viewCache; 
				Place place = list.get(position);
				if(convertView == null)
				{
					convertView = inflater.inflate(R.layout.nearby_list_item, null);
					viewCache = new PlaceViewCache(convertView);
					convertView.setTag(viewCache);
				}else {
					viewCache = (PlaceViewCache) convertView.getTag();
				}
					
				if(place.getProvidedServiceIdList().size()>=0)
				{
					serviceGroup = viewCache.getServiceGroup();
					if(serviceGroup.getChildCount()>0)
					{
						serviceGroup.removeAllViews();
					}
					
					for(int id:place.getProvidedServiceIdList())
					{
						 ImageView serviceImageView = new ImageView(context);  
						 serviceImageView.setLayoutParams(new LayoutParams(new LayoutParams((int)context.getResources().getDimension(R.dimen.service_icon),android.view.WindowManager.LayoutParams.WRAP_CONTENT)));  
						 //serviceImageView.setPadding(10, 0, 10, 0);  
						 serviceImageView.setScaleType(ScaleType.FIT_CENTER);
						 serviceImageView.setImageResource(TravelUtil.getServiceImage(id));
				         serviceGroup.addView(serviceImageView);
					}
				}
				
				
				TextView placeName = viewCache.getPlaceName();	
				placeName.setSelected(true);		
				TextView placePrice = viewCache.getPlacePrice();
				TextView placeTag = viewCache.getPlaceTag();
				TextView placeArea = viewCache.getPlaceArea();
				ImageView recommendImageView1 = viewCache.getRecommendImageView1();
				ImageView recommendImageView2 = viewCache.getRecommendImageView2();
				ImageView recommendImageView3 = viewCache.getRecommendImageView3();
				TextView hotelDistance = viewCache.getPlaceDistance();
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
				String url = "";
				imageView = viewCache.getImageView();
				imageView.setTag(position);	
				if(dataFlag == ConstantField.DATA_LOCAL)
				{
					url = datapath+place.getIcon();
				}else{
					url = place.getIcon();				
				}	
				anseylodar.showimgAnsy(imageView,url);
				int distance = (int) LocationUtil.GetDistance(longitude, latitude, place.getLongitude(), place.getLatitude());
				//Log.d(TAG, "distance = " +distance);
				if(distance >1000)
				{
					hotelDistance.setText(distance/1000+context.getResources().getString(R.string.kilometer));
				}else {
					hotelDistance.setText(distance+context.getResources().getString(R.string.meter));
				}		
				placeName.setText(place.getName());
				String price ;
				if(!place.getPrice().equals("")||place.getPrice()!=null)
				{
					if(place.getPrice().equals("0"))
					{
						price = context.getResources().getString(R.string.free);
						placePrice.setText(price);
					}else {
						price = place.getPrice();
						placePrice.setText(symbolMap+price);
					}
				}				
				String placeCategory;//
				
				if(application.getPlaceMetaMap().size() >0)
				{
					
					placeCategory = application.getPlaceMetaMap().get(PlaceCategoryType.valueOf(place.getCategoryId())).getName();					
					if(!placeTag.equals("")||placeTag!=null)
					{
						placeTag.setText(placeCategory);
					}
				}
				
				
				//placeType.setText("nearby");
				if(cityAreaMap.containsKey(place.getAreaId()))
				{
					placeArea.setText(cityAreaMap.get(place.getAreaId()));
				}
				
						
				return convertView;
	}


	


	public void setList(ArrayList<Place> list)
	{
		this.list = list;
	}

}
