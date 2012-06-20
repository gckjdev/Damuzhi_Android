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
import com.damuzhi.travel.util.TravelUtil;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

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
	private ViewGroup serviceGroup;
	private TextView placeName;
	private TextView placePrice;
	private TextView placeTag;
	private TextView placeArea;
	private ImageView recommendImageView1;
	private ImageView recommendImageView2;
	private ImageView recommendImageView3;
	private TextView placeDistance;
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
		this.placeCategoryType = placeCategoryType;
		subCatMap = AppManager.getInstance().getPlaceSubCatMap(placeCategoryType);
		cityAreaMap = AppManager.getInstance().getCityAreaMap(AppManager.getInstance().getCurrentCityId());
		symbol = AppManager.getInstance().getSymbolMap().get(AppManager.getInstance().getCurrentCityId());
		
	}

	@Override
	public Object getItem(int position)
	{
		return placeList.get(position);
	}
	
	
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		PlaceViewCache viewCache; 
		Place place = placeList.get(position);
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.common_place_list_item, null);
			viewCache = new PlaceViewCache(convertView);
			convertView.setTag(viewCache);
		}else {
			viewCache = (PlaceViewCache) convertView.getTag();
		}
		placeName = viewCache.getPlaceName();	
		placeName.setSelected(true);		
		placePrice = viewCache.getPlacePrice();
		placeTag = viewCache.getPlaceTag();
		recommendImageView1 = viewCache.getRecommendImageView1();
		recommendImageView2 = viewCache.getRecommendImageView2();
		recommendImageView3 = viewCache.getRecommendImageView3();
		placeDistance = viewCache.getPlaceDistance();
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
		url = place.getIcon();
		anseylodar.showimgAnsy(imageView,url);		
		
		String distance = TravelUtil.getDistance(place.getLongitude(),place.getLatitude());
		placeDistance.setText(distance);		
		placeName.setText(place.getName());	
		placePrice.setText(TravelUtil.getPriceStr(place.getPrice(),symbol));
		
		if(placeCategoryType == PlaceCategoryType.PLACE_HOTEL_VALUE)
		{
			placeTag.setText(TravelUtil.getHotelStar(context,place.getHotelStar()));
		}else
		{
			String subCatName = subCatMap.get(place.getSubCategoryId());
			placeTag.setText(subCatName);	
		}		
		
		if(cityAreaMap.containsKey(place.getAreaId()))
		{
			placeArea = viewCache.getPlaceArea();
			placeArea.setText(cityAreaMap.get(place.getAreaId()));
		}
		
		if(placeCategoryType == PlaceCategoryType.PLACE_HOTEL_VALUE || placeCategoryType == PlaceCategoryType.PLACE_RESTRAURANT_VALUE)
		{
			serviceGroup = viewCache.getServiceGroup();
			serviceGroup.setVisibility(View.VISIBLE);
			if(serviceGroup.getChildCount()>0)
			{
				serviceGroup.removeAllViews();
			}
			
			for(int id:place.getProvidedServiceIdList())
			{
				 ImageView serviceImageView = new ImageView(context);  
				 serviceImageView.setLayoutParams(new LayoutParams((int)context.getResources().getDimension(R.dimen.service_icon),
						 LayoutParams.WRAP_CONTENT));   
				 serviceImageView.setScaleType(ScaleType.FIT_CENTER);
				 serviceImageView.setImageResource(TravelUtil.getServiceImage(id));
		         serviceGroup.addView(serviceImageView);
			}
		}				
		return convertView;
	}
	
	public void setList(List<Place> list)
	{
		this.placeList = list;
	}

	
	public List<Place> getPlaceList()
	{
		return placeList;
	}
	

}
