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
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.PlaceActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.google.protobuf.InvalidProtocolBufferException;



public class TravelTipsDetailActivity extends MenuActivity 
{

	private static final String TAG = "TravelTipsDetailActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_tips_detail);
		byte[] travelInfo = getIntent().getByteArrayExtra(ConstantField.TRAVEL_TIPS_INFO);
		CommonTravelTip commonTravelTip = null;
		try
		{
			commonTravelTip = CommonTravelTip.parseFrom(travelInfo);
		} catch (InvalidProtocolBufferException e)
		{
			e.printStackTrace();
		}
		TextView textView = (TextView) findViewById(R.id.travel_tips_detail_title);
		WebView webView = (WebView) findViewById(R.id.travel_tips_webview);
		textView.setText(commonTravelTip.getName());	
		if(commonTravelTip.getHtml() != null)
		{
			webView.loadUrl(commonTravelTip.getHtml());
		}
	}

}
