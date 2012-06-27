/**  
        * @title HelpActiviy.java  
        * @package com.damuzhi.travel.activity.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-13 下午3:05:02  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.common;

import com.damuzhi.travel.R;
import com.damuzhi.travel.model.constant.ConstantField;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;



public class HelpActiviy extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		TravelApplication.getInstance().addActivity(this);
		String title = getIntent().getStringExtra(ConstantField.HELP_TITLE);
		TextView textView = (TextView) findViewById(R.id.place_title);
		textView.setText(title);
		WebView helpView = (WebView) findViewById(R.id.help_webview); 
		helpView.loadUrl(ConstantField.HELP_HTML_FILE_PATH);
	}

}
