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

import com.damuzhi.travel.activity.common.ActivityManger;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.util.TravelUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import com.damuzhi.travel.R;


public class TravelGuidesDetailActivity extends MenuActivity 
{

	private static final String TAG = "TravelTipsDetailActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_guides_detail);
		//TravelApplication.getInstance().addActivity(this);
		ActivityManger.getInstance().addActivity(this);
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
			String htmlUrl = TravelUtil.getHtmlUrl(commonTravelTip.getHtml());
			webView.loadUrl(htmlUrl);
		}
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityManger.getInstance().finishActivity();
	}

}
