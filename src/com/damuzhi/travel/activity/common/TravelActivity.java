package com.damuzhi.travel.activity.common;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.more.FeedBackActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.google.android.maps.MapActivity;

import android.app.Activity;
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

public class TravelActivity extends MapActivity
{

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
			intent.setClass(TravelActivity.this, HelpActiviy.class);
			startActivity(intent);
			break;
		case R.id.menu_feedback:
			intent = new Intent();			
			intent.setClass(TravelActivity.this, FeedBackActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_about:
			intent = new Intent();
			String about = getResources().getString(R.string.about_damuzhi);
			intent.putExtra(ConstantField.HELP_TITLE, about);
			intent.setClass(TravelActivity.this, HelpActiviy.class);
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

	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}

	protected void setMenuBackground() {  
        this.getLayoutInflater().setFactory(new android.view.LayoutInflater.Factory() {  
    
            public View onCreateView(String name, Context context, AttributeSet attrs) {  
                if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {  
                    try {  
                        LayoutInflater f = getLayoutInflater();  
                        final View view = f.createView(name, null, attrs);  
                        new Handler().post(new Runnable() {  
                            public void run() {    
                                view.setBackgroundResource(R.color.listview_bg);  
                            }  
                        });  
                        return view;  
                    } catch (InflateException e) {  
                        e.printStackTrace();  
                    } catch (ClassNotFoundException e) {  
                        e.printStackTrace();  
                    }  
                }  
                return null;  
            }  
        });  
    }  
	

}
