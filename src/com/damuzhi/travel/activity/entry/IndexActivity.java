package com.damuzhi.travel.activity.entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.R.integer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.more.MoreActivity;
import com.damuzhi.travel.activity.place.SceneryActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.service.Task;

public class IndexActivity extends MenuActivity implements OnClickListener
{
	private static final String TAG = "IndexActivity";
	private ImageButton moreButton;
	private ImageButton sceneryButton;
	private TravelApplication application;
	private HashMap<String, Integer> cityNameList;
	private List<String> list;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.index);
		Log.d(TAG, "onCreate");
		setProgressBarIndeterminateVisibility(true);
		MainService.allActivity.add(this);//将当前的activity添加到Servicre的activity集合中
		Spinner city = (Spinner) findViewById(R.id.city_spinner);
		application = (TravelApplication) this.getApplication();
		cityNameList = application.getCityList();
		Set<String> cityNameSet = cityNameList.keySet();
		list = new ArrayList<String>();
		int position = 0;
		int flag = 0;
		for(String cityName:cityNameSet)
		{
			list.add(cityName);
			
			if(cityName.equals("香港"))
			{
				//System.out.println("position = "+position);
				flag = position;
			}
			position++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_layout_item,android.R.id.text1, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		city.setAdapter(adapter);
		city.setSelection(flag);
		city.setOnItemSelectedListener(itemSelectedListener);
		moreButton = (ImageButton) findViewById(R.id.more);
		sceneryButton = (ImageButton) findViewById(R.id.scenery);
		sceneryButton.setOnClickListener(this);
		moreButton.setOnClickListener(this);
		Intent intent = new Intent();
		intent.setAction(ConstantField.CHECK_NET);
		sendBroadcast(intent);
		
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		ImageButton button = (ImageButton) v;
		Intent intent = new Intent();
		switch (button.getId())
		{
		case R.id.more:
			intent.setClass(IndexActivity.this, MoreActivity.class);
			startActivity(intent);
			break;
		case R.id.scenery:			
			intent.setClass(IndexActivity.this,SceneryActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);			
			break;
		default:
			break;
		}
		
	}
	
	private OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener()
	{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			// TODO Auto-generated method stub
			String cityName = list.get(arg2);
			Integer cityID = cityNameList.get(cityName);
			System.out.println("cityID= "+cityID);
			application.setCityID(cityID);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0)
		{
			// TODO Auto-generated method stub
			
		}
	};
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		if(keyCode == event.KEYCODE_BACK)
		{
			AlertDialog leaveAlertDialog = new AlertDialog.Builder(IndexActivity.this).create();
			leaveAlertDialog.setMessage(getBaseContext().getString(R.string.leave_alert_dilaog));
			leaveAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getBaseContext().getString(R.string.exit),new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// TODO Auto-generated method stub
					MainService.exitAPP(IndexActivity.this);				
				}
			} );
			leaveAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+getBaseContext().getString(R.string.cancel),new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// TODO Auto-generated method stub
					dialog.cancel();
					
				}
			} );
			leaveAlertDialog.show();
		return true;	
		}else{
		return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
	}
	

}
