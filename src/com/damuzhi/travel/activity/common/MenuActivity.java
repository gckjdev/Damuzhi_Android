/**  
        * @title MenuActivity.java  
        * @package com.damuzhi.travel.activity.adapter.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-14 下午2:23:17  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.common;

import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.more.FeedBackActivity;
import com.damuzhi.travel.activity.more.MoreActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
		// TODO Auto-generated method stub
		TravelApplication.getInstance().addActivity(this);
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
			TravelApplication.getInstance().exit();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	/*private void loadPlace()
	{
		// TODO Auto-generated method stub
		AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>()
		{

			@Override
			protected Void doInBackground(String... params)
			{
				return null;
			}

			@Override
			protected void onCancelled()
			{
				// TODO Auto-generated method stub
				super.onCancelled();
			}

			@Override
			protected void onPostExecute(Void resultList)
			{				
				loadingDialog.dismiss();
				super.onPostExecute(resultList);
			}

			@Override
			protected void onPreExecute()
			{
				showRoundProcessDialog();
				super.onPreExecute();
			}

		};

		task.execute();
	}
	
	public void showRoundProcessDialog()
	{

		OnKeyListener keyListener = new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getRepeatCount() == 0)
				{
					loadingDialog.dismiss();
					Intent intent = new Intent(MenuActivity.this,IndexActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					return true;
				} else
				{
					return false;
				}
			}
		};

		loadingDialog = new ProgressDialog(this);
		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingDialog.setMessage(getResources().getString(R.string.loading));
		loadingDialog.setIndeterminate(false);
		loadingDialog.setCancelable(true);
		loadingDialog.setOnKeyListener(keyListener);
		loadingDialog.show();
	}*/

}
