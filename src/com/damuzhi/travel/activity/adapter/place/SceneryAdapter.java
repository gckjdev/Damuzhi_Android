package com.damuzhi.travel.activity.adapter.place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.ViewCache;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.PlaceListProtos.Place;

public class SceneryAdapter extends BaseAdapter
{
	private static final String TAG = "SceneryAdapter";
	private static final int SHOE_IMAGE = 3;
	private Context context;
	private String datapath ;
	private ArrayList<Place> list;
	private HashMap<Integer, NameIdPair> subCatMap;
	private HashMap<Integer, NameIdPair> proSerMap;
	private HashMap<String, Double> location;
	private HashMap<Integer, City> cityMap;
	private HashMap<Integer, CityArea> cityAreaMap;
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
	public SceneryAdapter(Context context, String datapath,
			ArrayList<Place> list, HashMap<Integer, NameIdPair> subCatMap,
			HashMap<Integer, NameIdPair> proSerMap,
			HashMap<String, Double> location, HashMap<Integer, City> cityMap,
			HashMap<Integer, CityArea> cityAreaMap, int dataFlag)
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
		this.dataFlag = dataFlag;
		anseylodar = new Anseylodar();
		inflater = LayoutInflater.from(context);

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
		ViewCache viewCache; 
		Place place = list.get(position);
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.scenery_list_item, null);
			viewCache = new ViewCache(convertView);
			convertView.setTag(viewCache);
		}else {
			viewCache = (ViewCache) convertView.getTag();
		}
		NameIdPair subCatName= subCatMap.get(place.getSubCategoryId());		
		TextView sceneryName = viewCache.getSceneryName();
		TextView sceneryPrice = viewCache.getSceneryPrice();
		TextView sceneryTag = viewCache.getSceneryTag();
		TextView sceneryArea = viewCache.getSceneryArea();
		ImageView recommendImageView1 = viewCache.getRecommendImageView1();
		ImageView recommendImageView2 = viewCache.getRecommendImageView2();
		ImageView recommendImageView3 = viewCache.getRecommendImageView3();
		TextView sceneryDistance = viewCache.getSceneryDistance();
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
		sceneryName.setText(place.getName());
		sceneryPrice.setText(cityMap.get(place.getCityId()).getCurrencySymbol()+place.getPrice());
		sceneryTag.setText(subCatName.getName());
		sceneryArea.setText(cityAreaMap.get(place.getAreaId()).getAreaName());
		sceneryDistance.setText("100m");		
		return convertView;
	}

	

}
