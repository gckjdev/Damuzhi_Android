/**  
        * @title RouteFeedbackViewCache.java  
        * @package com.damuzhi.travel.activity.adapter.viewcache  
        * @description   
        * @author liuxiaokun  
        * @update 2012-10-10 下午5:36:46  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.adapter.viewcache;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.damuzhi.travel.R;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-10-10 下午5:36:46  
 */

public class RouteFeedbackViewCache
{
	private View convertView;
	private TextView nickName;
	private TextView feedbackTime;
	private TextView feedbackContent;
	private ImageView recommendImageView1;
	private ImageView recommendImageView2;
	private ImageView recommendImageView3;

	/**
	 * @param convertView
	 */
	public RouteFeedbackViewCache(View convertView)
	{
		super();
		this.convertView = convertView;
	}
	
	
	
	
	public TextView getNickName()
	{
		if(nickName == null)
		{
			nickName = (TextView) convertView.findViewById(R.id.nick_name);
		}
		return nickName;
	}
	
	public TextView getFeedbackTime()
	{
		if(feedbackTime == null)
		{
			feedbackTime = (TextView) convertView.findViewById(R.id.feedback_time);
		}
		return feedbackTime;
	}
	
	public TextView getFeedbackContent()
	{
		if(feedbackContent == null)
		{
			feedbackContent = (TextView) convertView.findViewById(R.id.route_feedback_content);
		}
		return feedbackContent;
	}
	
	
	
	
	public ImageView getRecommendImageView1()
	{
		if(recommendImageView1 == null)
		{
			recommendImageView1 = (ImageView) convertView.findViewById(R.id.good1);
		}
		return recommendImageView1;
	}
	
	public ImageView getRecommendImageView2()
	{
		if(recommendImageView2 == null)
		{
			recommendImageView2 = (ImageView) convertView.findViewById(R.id.good2);
		}
		return recommendImageView2;
	}
	
	public ImageView getRecommendImageView3()
	{
		if(recommendImageView3 == null)
		{
			recommendImageView3 = (ImageView) convertView.findViewById(R.id.good3);
		}
		return recommendImageView3;
	}
	
}
