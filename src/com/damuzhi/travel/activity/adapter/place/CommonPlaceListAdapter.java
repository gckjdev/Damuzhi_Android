/**  
        * @title CommonPlaceListAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-24 下午3:57:51  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.adapter.place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.PlaceViewCache;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.App;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.util.LocationUtil;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-24 下午3:57:51  
 */

public class CommonPlaceListAdapter extends BaseAdapter
{
	private static final String TAG = "CommonPlaceListAdapter";
	private Context context;
	private List<Place> placeList;
	private int placeCategoryType;
	private HashMap<Integer, String> subCatMap;
	private double latitude;
	private double longitude;
	private String symbol;
	private HashMap<Integer, String> cityAreaMap;
	public Anseylodar anseylodar;
	private LayoutInflater inflater;
	private ImageView imageView;
	//private int dataFlag;
	

	@Override
	public int getCount()
	{
		if (placeList == null)
			return 0;
		
		return placeList.size();
	}

	/**  
	        * Constructor Method   
	        * @param context
	        * @param placeList  
	        */
	public CommonPlaceListAdapter(Context context, List<Place> placeList,int placeCategoryType)
	{
		super();
		this.context = context;
		this.placeList = placeList;		
		this.inflater = LayoutInflater.from(context);
		this.anseylodar = new Anseylodar();
		subCatMap = AppManager.getInstance().getPlaceSubCatMap(placeCategoryType);
		cityAreaMap = AppManager.getInstance().getCityAreaMap(TravelApplication.getInstance().getCityID());
		symbol = AppManager.getInstance().getSymbolMap().get(TravelApplication.getInstance().getCityID());
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return placeList.get(position);
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
		Place place = placeList.get(position);
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.place_list_item, null);
			viewCache = new PlaceViewCache(convertView);
			convertView.setTag(viewCache);
		}else {
			viewCache = (PlaceViewCache) convertView.getTag();
		}
		String subCatName = subCatMap.get(place.getSubCategoryId());
		TextView sceneryName = viewCache.getPlaceName();	
		sceneryName.setSelected(true);		
		TextView sceneryPrice = viewCache.getPlacePrice();
		TextView sceneryTag = viewCache.getPlaceTag();
		TextView sceneryArea = viewCache.getPlaceArea();
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
		/*if(dataFlag == ConstantField.DATA_LOCAL)
		{
			url = ConstantField.DATA_PATH+place.getIcon();
		}else{
			url = place.getIcon();				
		}*/	
		url = place.getIcon();
		anseylodar.showimgAnsy(imageView,url, ConstantField.DATA_HTTP);		
		if(TravelApplication.getInstance().getLocation()!=null)
		{
			int distance = (int) LocationUtil.GetDistance(longitude, latitude, place.getLongitude(), place.getLatitude());
			if(distance >1000)
			{
				sceneryDistance.setText(distance/1000+context.getResources().getString(R.string.kilometer));
			}else {
				sceneryDistance.setText(distance+context.getResources().getString(R.string.meter));
			}
		}else {
			sceneryDistance.setText("");
		}
		sceneryDistance.setText("");	
		sceneryName.setText(place.getName());
		String price ;
		if(place.getPrice().equals("0"))
		{
			price = context.getResources().getString(R.string.free);
			sceneryPrice.setText(price);
		}else {
			price = place.getPrice();
			sceneryPrice.setText(symbol+price);
		}
		sceneryTag.setText(subCatName);
		if(cityAreaMap.containsKey(place.getAreaId()))
		{
			sceneryArea.setText(cityAreaMap.get(place.getAreaId()));
		}
		
				
		return convertView;
	}
	
	public void setList(List<Place> list)
	{
		this.placeList = list;
	}

	/**  
	        * @return  
	        * @description   
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-25 下午12:04:52  
	        */
	public List<Place> getPlaceList()
	{
		return placeList;
	}
	

}
