/**  
        * @title ShoppingAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-16 下午3:23:53  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.adapter.place;

import java.util.ArrayList;
import java.util.HashMap;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.PlaceViewCache;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.LocationUtil;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-16 下午3:23:53  
 */

public class ShoppingAdapter extends BaseAdapter
{

	private static final String TAG = "ShoppingAdapter";
	private Context context;
	private String datapath ;
	private ArrayList<Place> list;
	private HashMap<Integer, String> subCatMap;
	private double latitude;
	private double longitude;
	private HashMap<Integer, String> cityAreaMap;
	private int dataFlag;
	public Anseylodar anseylodar;
	private LayoutInflater inflater;
	private ImageView imageView;
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
	public ShoppingAdapter(Context context, String datapath,
			ArrayList<Place> list, HashMap<Integer, String> subCatMap,
			HashMap<String, Double> location, HashMap<Integer, String> cityAreaMap, int dataFlag)
	{
		super();
		this.context = context;
		this.datapath = datapath;
		this.list = list;
		this.subCatMap = subCatMap;
		this.cityAreaMap = cityAreaMap;
		this.dataFlag = dataFlag;
		anseylodar = new Anseylodar();
		inflater = LayoutInflater.from(context);
		latitude = location.get(ConstantField.LATITUDE);
		longitude = location.get(ConstantField.LONGITUDE);
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
		return list.get(position);
	}
	
	 public  void starAnsy(){
		   //istarAnsy=true;
		   this.notifyDataSetChanged();
		   Log.i("-----------------------------------", "star");
	   } 
	 
	   public  void pauseAnsy(){
		  // istarAnsy=false;
		   Log.i("----------------------------------", "pause");
	   }
	   
	 //请求更多的数据
		public void addmoreDate(ArrayList<Place> addmore){
			if (list!=null) {
				this.list.addAll(addmore);//吧新传得数据加到现在的list中
				this.notifyDataSetChanged();//将数据追加到ListView中显示
			}
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
			convertView = inflater.inflate(R.layout.shopping_list_item, null);
			viewCache = new PlaceViewCache(convertView);
			convertView.setTag(viewCache);
		}else {
			viewCache = (PlaceViewCache) convertView.getTag();
		}
		String subCatName = subCatMap.get(place.getSubCategoryId());
		TextView shoppingName = viewCache.getPlaceName();	
		shoppingName.setSelected(true);		
		TextView shoppingTag = viewCache.getPlaceTag();
		TextView shoppingArea = viewCache.getPlaceArea();
		ImageView recommendImageView1 = viewCache.getRecommendImageView1();
		ImageView recommendImageView2 = viewCache.getRecommendImageView2();
		ImageView recommendImageView3 = viewCache.getRecommendImageView3();
		TextView sceneryDistance = viewCache.getPlaceDistance();
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
		anseylodar.showimgAnsy(imageView,url, dataFlag);
		int distance = (int) LocationUtil.GetDistance(longitude, latitude, place.getLongitude(), place.getLatitude());
		if(distance >1000)
		{
			sceneryDistance.setText(distance/1000+context.getResources().getString(R.string.kilometer));
		}else {
			sceneryDistance.setText(distance+context.getResources().getString(R.string.meter));
		}		
		shoppingName.setText(place.getName());
		shoppingTag.setText(subCatName);
		if(cityAreaMap.containsKey(place.getAreaId()))
		{
			shoppingArea.setText(cityAreaMap.get(place.getAreaId()));
		}
		
				
		return convertView;
	}
	
	public void setList(ArrayList<Place> list)
	{
		this.list = list;
	}
	


}
