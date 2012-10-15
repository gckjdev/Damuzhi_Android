/**  
        * @title CommonAgencyIntroActivity.java  
        * @package com.damuzhi.travel.activity.touristRoute  
        * @description   
        * @author liuxiaokun  
        * @update 2012-10-13 下午3:44:39  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.touristRoute;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.Agency;
import com.damuzhi.travel.util.FileUtil;



public class CommonAgencyIntroActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		//TravelApplication.getInstance().addActivity(this);
		ActivityMange.getInstance().addActivity(this);
		int agencyId = getIntent().getIntExtra("agencyId", -1);
		Agency agency = AppManager.getInstance().getAgencyById(agencyId);
		TextView textView = (TextView) findViewById(R.id.place_title);
		textView.setText(getString(R.string.agency_intro));
		WebView webView = (WebView) findViewById(R.id.help_webview);
		webView.loadUrl(agency.getUrl());
		
	}

	@Override
	protected void onDestroy() {
		ActivityMange.getInstance().finishActivity();
		super.onDestroy();
	}
}
