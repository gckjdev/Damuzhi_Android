/**  
        * @title BrowseHistoryAdapter.java  
        * @package com.damuzhi.travel.activity.more  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-18 上午11:26:22  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.adapter.more;

import java.util.HashMap;
import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.PlaceViewCache;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;

import android.content.Context;
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
 * @update 2012-6-18 上午11:26:22  
 */

public class BrowseHistoryAdapter extends BaseAdapter
{

	private static final String TAG = "CommonPlaceListAdapter";
	private Context context;
	private List<Place> placeList;
	private HashMap<Integer, String> subCatMap;
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

	
	public BrowseHistoryAdapter(Context context, List<Place> placeList)
	{
		super();
		this.context = context;
		this.placeList = placeList;		
		this.inflater = LayoutInflater.from(context);
		this.anseylodar = new Anseylodar();
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
		int placeCategoryType = place.getCategoryId();
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.nearby_place_listview_item, null);
			viewCache = new PlaceViewCache(convertView);
			convertView.setTag(viewCache);
		}else {
			viewCache = (PlaceViewCache) convertView.getTag();
		}		
		subCatMap = AppManager.getInstance().getPlaceSubCatMap(placeCategoryType);
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
		else{
			serviceGroup = viewCache.getServiceGroup();
			serviceGroup.setVisibility(View.GONE);
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
