package com.damuzhi.travel.activity.place;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.SceneryAdapter;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.PlaceActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.service.Task;
import com.damuzhi.travel.util.PullToRefreshListView;

public class SceneryActivity extends MenuActivity implements PlaceActivity
{
	private	static final String TAG = "SceneryActivity";
	private PullToRefreshListView placeList;
	private TextView titleView;
	private ImageView mapView;
	private Spinner sortSpinner;
	private Spinner compositorSpinner;
	private Dialog loadingDialog;
	private ArrayList<Place> sceneryList;
	private TravelApplication application;
	private String dataPath ;
	private static final int LOADING = 0;
	private static final int LOAD_OK = 1;
	private static final int SHOE_IMAGE = 3;
	private SceneryAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scenery);
		MainService.allActivity.add(this);//将当前的activity添加到Servicre的activity集合中		
		init();
		placeInfo();
	}
	
	
	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		Log.d(TAG, "init()....");
		placeList = (PullToRefreshListView) findViewById(R.id.scenery_list);
		titleView = (TextView) findViewById(R.id.scenery_title);
		sortSpinner = (Spinner) findViewById(R.id.scenery_sort);
		compositorSpinner = (Spinner) findViewById(R.id.scenery_order);
		mapView = (ImageView) findViewById(R.id.map_view);
		
	}

	@Override
	public void placeInfo()
	{
		// TODO Auto-generated method stub
		placeList.setOnItemClickListener(itemClickListener);	
		
	}
	
	
	private OnItemClickListener itemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			// TODO Auto-generated method stub
			Place place = sceneryList.get(arg2-1);
			application.setPlace(place);
			Intent intent = new Intent(SceneryActivity.this, SceneryDetailActivity.class);
			startActivity(intent);
		}
	};

	private OnClickListener clickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent(SceneryActivity.this,PlaceMap.class);
			startActivity(intent);
			
		}
	};

	private void initData()
	{
		application = (TravelApplication) this.getApplication();
		dataPath = String.format(ConstantField.IMAGE_PATH,application.getCityID());
		HashMap<Integer,NameIdPair> subCatMap = application.getSubCatMap();
		HashMap<Integer,NameIdPair> proSerMap = application.getProSerMap();		
		HashMap<Integer,City> cityMap = application.getCity();
		HashMap<Integer, CityArea> cityAreaMap = application.getCityAreaMap();
		HashMap<String, Double> location = application.getLocation();
		List<String> subCatList = application.getSubCatList();
		List<String> proSerList = application.getProSerList();
		subCatList.add(getResources().getString(R.string.sort));
		proSerList.add(getResources().getString(R.string.compositor));
		ArrayAdapter<String> subCatAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout_item,android.R.id.text1, subCatList);
		ArrayAdapter<String> proSerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout_item,android.R.id.text1, proSerList);
		subCatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		proSerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sortSpinner.setAdapter(subCatAdapter);
		sortSpinner.setSelection(subCatList.size()-1);
		compositorSpinner.setAdapter(proSerAdapter);
		compositorSpinner.setSelection(proSerList.size()-1);
		//PlaceData placeData = new PlaceData(dataPath);
		sceneryList = application.getPlaceData();
		int size = sceneryList.size();
		titleView.setText(this.getResources().getString(R.string.scenery)+"("+size+")");
		adapter = new SceneryAdapter(this,dataPath,sceneryList,subCatMap,proSerMap,location,cityMap,cityAreaMap,application.getDataFlag());
		adapter.notifyDataSetChanged();
		placeList.setAdapter(adapter);
		mapView.setOnClickListener(clickListener);
	}

	
	
	
	
	@Override
	public void refresh()
	{
		// TODO Auto-generated method stub
		//thread.start();
		Message message = handler.obtainMessage();
		message.what = LOAD_OK;
		message.obj = null;
		handler.sendMessage(message);
	}
	
	private Handler handler = new Handler(){
		@Override
	public void handleMessage(Message msg)
	{
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		switch (msg.what)
		{
		case LOADING:
			
			break;
		case LOAD_OK:
			initData();			
			loadingDialog.dismiss();	
			break;
		
		default:
			break;
		}
	}};

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		if(application == null)
		{
			Task sceneryTask = new Task(Task.TASK_LOGIN_SCENERY);
			MainService.newTask(sceneryTask);
			showRoundProcessDialog(SceneryActivity.this, R.layout.loading_process_dialog_anim);
			Intent intent = new Intent(ConstantField.MAIN_SERVICE);
			startService(intent);		
		}
		
	}
	
	  public void showRoundProcessDialog(Context mContext, int layout)
	    {
	        OnKeyListener keyListener = new OnKeyListener()
	        {
	            @Override
	            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
	            {
	            	if ( keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
	            		loadingDialog.dismiss();
	            		Intent intent = new Intent(SceneryActivity.this, IndexActivity.class);
	   				    startActivity(intent);
	   				    return true;
		   		    }
		   			else
		   			{
		   				  return false;	
		   			}
	            }
	        };

	        loadingDialog = new AlertDialog.Builder(mContext).create();
	        loadingDialog.setOnKeyListener(keyListener);
	        loadingDialog.show();
	        // 注意此处要放在show之后 否则会报异常
	        loadingDialog.setContentView(layout);
	    }
	  
	  
	  @Override
		public boolean onKeyDown(int keyCode, KeyEvent event)
		{
			if (  keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
				 Intent intent = new Intent(this, IndexActivity.class);
				 startActivity(intent);
		        return true;
		    }
			else
			{
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
	
}
