package com.damuzhi.travel.activity.adapter.touristRoute;

import java.util.List;
import java.util.zip.Inflater;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.viewcache.LocalTripsViewCache;
import com.damuzhi.travel.activity.adapter.viewcache.PlaceViewCache;
import com.damuzhi.travel.activity.common.imageCache.AsyncLoader;
import com.damuzhi.travel.mission.favorite.FavoriteMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRoute;
import com.damuzhi.travel.protos.TouristRouteProtos.TouristRoute;
import com.damuzhi.travel.util.TravelUtil;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class CommonLocalTripsAdapter extends BaseAdapter {

	private static final String TAG = "CommonLocalTripsAdapter";
	private List<LocalRoute> localRouteList;
	private Context context;
	private ImageView imageView;
	private TextView agencyName;
	private TextView routeName;
	private TextView routePrice;
	private TextView routeTour;
	private TextView routeDays;
	private ImageView recommendImageView1;
	private ImageView recommendImageView2;
	private ImageView recommendImageView3;
	private ViewGroup agencyNameViewGroup;
	private AsyncLoader asyncLoader;
	//private int dataFlag;
	private LayoutInflater inflater;
	public CommonLocalTripsAdapter(List<LocalRoute> localRouteList, Context context) {
		super();
		this.localRouteList = localRouteList;
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		asyncLoader = AsyncLoader.getInstance();
		
	}

	public List<LocalRoute> getLocalRouteList() {
		return localRouteList;
	}

	public void setLocalRouteList(List<LocalRoute> localRouteList) {
		this.localRouteList = localRouteList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(localRouteList == null)
		return 0;
		return localRouteList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LocalTripsViewCache viewCache; 
		LocalRoute localRoute = localRouteList.get(position);
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.common_local_trips_list_item, null);
			viewCache = new LocalTripsViewCache(convertView);
			convertView.setTag(viewCache);
		}else {
			viewCache = (LocalTripsViewCache) convertView.getTag();
		}
		agencyName = viewCache.getAgencyName();	
		agencyName.setSelected(true);
		agencyNameViewGroup = viewCache.getAgencyNameViewGroup();		
		int currentAgencyId = localRoute.getAgencyId();
		String agencyNameStr = AppManager.getInstance().getAgencyNameById(currentAgencyId);
		agencyName.setText(agencyNameStr);
		if(position == 0){
			agencyNameViewGroup.setVisibility(View.VISIBLE);
		}else{			
			int lastAgencyId =  localRouteList.get(position-1).getAgencyId();
			if(currentAgencyId!=lastAgencyId){
				agencyNameViewGroup.setVisibility(View.VISIBLE);
			}else
			{
				agencyNameViewGroup.setVisibility(View.GONE);						
			}						
		}						
		routeTour = viewCache.getRouteTour();
		routeName = viewCache.getRouteName();
		routeDays = viewCache.getRouteDays();
		routePrice = viewCache.getRoutePrice();
		recommendImageView1 = viewCache.getRecommendImageView1();
		recommendImageView2 = viewCache.getRecommendImageView2();
		recommendImageView3 = viewCache.getRecommendImageView3();		
		int rank = localRoute.getAverageRank();
		switch (rank)
		{
		case 0:{
			recommendImageView1.setImageDrawable(context.getResources().getDrawable(R.drawable.good2));
			recommendImageView2.setImageDrawable(context.getResources().getDrawable(R.drawable.good2));
			recommendImageView3.setImageDrawable(context.getResources().getDrawable(R.drawable.good2));
			}
			break;
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
		url = localRoute.getThumbImage();	
		asyncLoader.showimgAnsy(imageView,url,0);	
		/*String uri = TravelUtil.getImageUrl(AppManager.getInstance().getCurrentCityId(), url);
		imageLoader.displayImage(uri, imageView,options);*/
		String days = TravelUtil.getRouteDays(localRoute.getDays());
		Spanned price = Html.fromHtml("<font color='#ff6305'>"+ localRoute.getPrice() + "</FONT>"+"<font>"+"起"+ "</FONT>");
		routeDays.setText(days);
		routeName.setText(localRoute.getName());
		routePrice.setText(price);
		routeTour.setText(localRoute.getTour());
		return convertView;
	}
	
	
	
	
	public void recycleBitmap()
	{
		//asyncLoader.recycleBitmap();
	}

	public void addPlaceList(List<LocalRoute> list) {
		localRouteList.addAll(list);
		
	}

}