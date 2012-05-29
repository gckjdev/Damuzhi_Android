/**  
        * @title TravelTipsDetailActivity.java  
        * @package com.damuzhi.travel.activity.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-23 ����10:13:51  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.overview;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.PlaceActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-23 ����10:13:51  
 */

public class TravelTipsDetailActivity extends TravelActivity implements
		PlaceActivity
{

	private static final String TAG = "TravelTipsDetailActivity";
	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		setContentView(R.layout.travel_tips_detail);
		TravelApplication application = TravelApplication.getInstance();
		CommonTravelTip commonTravelTip = application.getCommonTravelTip();
		TextView textView = (TextView) findViewById(R.id.travel_tips_detail_title);
		WebView webView = (WebView) findViewById(R.id.travel_tips_webview);
		textView.setText(commonTravelTip.getName());	
		if(commonTravelTip.getHtml() != null)
		{
			webView.loadUrl(commonTravelTip.getHtml());
		}
	}

	
	@Override
	public void placeInfo()
	{
		// TODO Auto-generated method stub

	}

	
	@Override
	public void refresh(Object... params)
	{
		// TODO Auto-generated method stub

	}


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
	}

}
