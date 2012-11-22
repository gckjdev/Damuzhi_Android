/**  
        * @title MenuActivity.java  
        * @package com.damuzhi.travel.activity.adapter.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-14 下午2:23:17  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.damuzhi.travel.activity.more.FeedBackActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.R;
/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-14 下午2:23:17  
 */

public class MenuActivity extends Activity
{
	private ProgressDialog loadingDialog;
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{	
		//ActivityMange.getInstance().addActivity(this);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent  intent;
		switch (item.getItemId())	
		{		
		case R.id.menu_refresh:
			
			break;
		case R.id.menu_help:
			intent = new Intent();
			intent.putExtra(ConstantField.HELP_TITLE, getResources().getString(R.string.help));
			intent.setClass(MenuActivity.this, HelpActiviy.class);
			startActivity(intent);
			break;
		case R.id.menu_feedback:
			intent = new Intent();			
			intent.setClass(MenuActivity.this, FeedBackActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_about:
			intent = new Intent();
			String about = getResources().getString(R.string.about_damuzhi);
			intent.putExtra(ConstantField.HELP_TITLE, about);
			intent.setClass(MenuActivity.this, HelpActiviy.class);
			startActivity(intent);
			break;
		case R.id.menu_exit:
			ActivityMange.getInstance().AppExit(this);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	

}
