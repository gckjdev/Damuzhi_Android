/**  
        * @title CommonRouteFeedbackAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.touristRoute  
        * @description   
        * @author liuxiaokun  
        * @update 2012-10-10 下午5:28:53  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.adapter.touristRoute;

import java.util.List;
import java.util.zip.Inflater;

import javax.crypto.spec.PSource;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.viewcache.LocalTripsViewCache;
import com.damuzhi.travel.activity.adapter.viewcache.RouteFeedbackViewCache;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.protos.PackageProtos.RouteFeekback;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRoute;
import com.damuzhi.travel.util.TravelUtil;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
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
 * @update 2012-10-10 下午5:28:53  
 */

public class CommonRouteFeedbackAdapter extends BaseAdapter
{

	private Context context;
	private List<RouteFeekback> routeFeekbacks;
	private LayoutInflater inflater;
	private TextView nickName;
	private TextView feedbackTime;
	private TextView feedbackContent;
	private ImageView recommendImageView1;
	private ImageView recommendImageView2;
	private ImageView recommendImageView3;
	
	public CommonRouteFeedbackAdapter(Context context,List<RouteFeekback> routeFeekbacks)
	{
		super();
		this.context = context;
		this.routeFeekbacks = routeFeekbacks;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount()
	{
		if(routeFeekbacks == null)
		return 0;
		return routeFeekbacks.size();
	}

	@Override
	public Object getItem(int position)
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		RouteFeedbackViewCache viewCache; 
		RouteFeekback routeFeekback = routeFeekbacks.get(position);
		if(convertView == null)
		{		
			convertView = inflater.inflate(R.layout.common_route_feedback_list_item, null);
			/*if(position%2 == 0)
			{
				convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.fk_bg));
			}else
			{
				convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.fk_bg2));
			}*/
			viewCache = new RouteFeedbackViewCache(convertView);
			convertView.setTag(viewCache);
		}else {
			viewCache = (RouteFeedbackViewCache) convertView.getTag();
		}
		nickName = viewCache.getNickName();	
		feedbackTime = viewCache.getFeedbackTime();
		feedbackContent = viewCache.getFeedbackContent();
		if(routeFeekback.getNickName() != null)
		{
			nickName.setText(routeFeekback.getNickName());
		}else
		{
			nickName.setText("游客");
		}
		
		feedbackTime.setText(TravelUtil.getDateString(routeFeekback.getDate()));
		feedbackContent.setText(routeFeekback.getContent());
		
		
		recommendImageView1 = viewCache.getRecommendImageView1();
		recommendImageView2 = viewCache.getRecommendImageView2();
		recommendImageView3 = viewCache.getRecommendImageView3();	
		int rank = routeFeekback.getRank();
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
		return convertView;
	}

}
