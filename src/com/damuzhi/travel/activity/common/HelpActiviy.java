/**  
        * @title HelpActiviy.java  
        * @package com.damuzhi.travel.activity.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-13 下午3:05:02  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.common;

import java.io.File;

import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.util.FileUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import com.damuzhi.travel.R;


public class HelpActiviy extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		//TravelApplication.getInstance().addActivity(this);
		ActivityMange.getInstance().addActivity(this);
		String title = getIntent().getStringExtra(ConstantField.HELP_TITLE);
		TextView textView = (TextView) findViewById(R.id.place_title);
		textView.setText(title);
		WebView helpView = (WebView) findViewById(R.id.help_webview);
		if(FileUtil.checkFileIsExits(ConstantField.HELP_HTML_FILE))
		{	 
			helpView.loadUrl(ConstantField.HELP_HTML_FILE_PATH);
		}else{
			helpView.setVisibility(View.GONE);
			findViewById(R.id.data_not_found).setVisibility(View.VISIBLE);
		}
		
	}

	@Override
	protected void onDestroy() {
		ActivityMange.getInstance().finishActivity();
		super.onDestroy();
	}

}
