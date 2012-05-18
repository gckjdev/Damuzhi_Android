package com.damuzhi.travel.activity.place;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.SceneryAdapter;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.PlaceActivity;
import com.damuzhi.travel.activity.common.PlaceMap;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.service.Task;
import com.damuzhi.travel.util.LocationUtil;
import com.damuzhi.travel.util.PullToRefreshListView;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.TravelUtil.ComparatorDistance;
import com.damuzhi.travel.util.TravelUtil.ComparatorPrice;
import com.damuzhi.travel.util.TravelUtil.ComparatorRank;

public class SceneryActivity extends MenuActivity implements PlaceActivity
{
	private	static final String TAG = "SceneryActivity";
	private ListView sceneryList;
	private TextView titleView;
	private ImageView mapView;
	private ViewGroup sortSpinner;
	private ViewGroup composSpinner;
	private Dialog loadingDialog;
	private ArrayList<Place> sceneryPlaceList;
	private TravelApplication application;
	private String dataPath ;
	private static final int LOADING = 0;
	private static final int LOAD_OK = 1;
	private int loadFlag = 1;//判断是否从新加载activity
	private int compositor_position = 0;
	private int sort_position = 0;
	private int cityID = -1;
	private SceneryAdapter sceneryAdapter;
	private String[] compos;
	private String[] subCatName;
	private int[] subCatKey;
	private HashMap<Integer,String> symbolMap;
	private HashMap<Integer, String> cityAreaMap;
	private HashMap<Integer, String> subCatNameMap;
	private HashMap<String, Double> location;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.scenery);
		MainService.allActivity.add(this);//将当前的activity添加到Servicre的activity集合中		
		loadFlag = 0;
		init();
		placeInfo();
	}
	
	
	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		sceneryList = (ListView) findViewById(R.id.scenery_list);
		titleView = (TextView) findViewById(R.id.scenery_title);
		sortSpinner = (ViewGroup) findViewById(R.id.sort_spinner);
		composSpinner = (ViewGroup) findViewById(R.id.compos_spinner);
		mapView = (ImageView) findViewById(R.id.map_view);
		application = TravelApplication.getInstance();
		
		
	}
	
	private void initData(Object...param)
	{
		
		cityID = application.getCityID();
		dataPath = String.format(ConstantField.IMAGE_PATH,cityID);	
		location = application.getLocation();
		symbolMap = (HashMap<Integer, String>) param[0];
		cityAreaMap = (HashMap<Integer, String>) param[1];		
		subCatNameMap = (HashMap<Integer, String>) param[4];
		subCatName = (String[]) param[2];
		subCatKey = (int[]) param[3];
		compos = this.getResources().getStringArray(R.array.spot);
		sceneryPlaceList = application.getPlaceData();
		int size = sceneryPlaceList.size();
		titleView.setText(this.getResources().getString(R.string.scenery)+"("+size+")");
		sceneryAdapter = new SceneryAdapter(this,dataPath,sceneryPlaceList,subCatNameMap,location,symbolMap.get(cityID),cityAreaMap,application.getDataFlag());
		sceneryList.setAdapter(sceneryAdapter);
		mapView.setOnClickListener(clickListener);
		sortSpinner.setOnClickListener(clickListener);
		composSpinner.setOnClickListener(clickListener);
	}

	@Override
	public void placeInfo()
	{
		// TODO Auto-generated method stub
		sceneryList.setOnItemClickListener(itemClickListener);	
	}
	
	@Override
	public void refresh(Object...param)
	{
		// TODO Auto-generated method stub
		//thread.start();
		initData(param);
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
				loadingDialog.dismiss();	
				loadingDialog.cancel();
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
		Log.d(TAG, "loadFlag = "+loadFlag);
		Log.d(TAG, "onResume ");
		if(loadFlag == 0)
		{			
			loadFlag = 1;
			showRoundProcessDialog(SceneryActivity.this, R.layout.loading_process_dialog_anim);
			Task sceneryTask = new Task(Task.TASK_LOGIN_SCENERY,SceneryActivity.this);
			MainService.newTask(sceneryTask);
			Intent intent = new Intent(ConstantField.MAIN_SERVICE);
			startService(intent);		
		}
			
	}
	
	
	private OnItemClickListener itemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			// TODO Auto-generated method stub
			Place place = sceneryPlaceList.get(arg2);
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
			final AlertDialog dialog;
			switch (v.getId())
			{
			case R.id.map_view:
				Intent intent = new Intent(SceneryActivity.this,PlaceMap.class);
				startActivity(intent);
				break;
			case R.id.sort_spinner:
			{
                AlertDialog.Builder builder=new AlertDialog.Builder(SceneryActivity.this)
                .setSingleChoiceItems(subCatName, sort_position,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) 
                    {
                    	sort_position=position;                    	
                    	sceneryPlaceList = TravelUtil.sort(subCatKey[sort_position], application.getPlaceData());
                    	sceneryAdapter = new SceneryAdapter(SceneryActivity.this,dataPath,sceneryPlaceList,subCatNameMap,location,symbolMap.get(cityID),cityAreaMap,application.getDataFlag());          		
                    	sceneryList.setAdapter(sceneryAdapter);
                    	/*sceneryList.invalidateViews();
                    	((SceneryAdapter)((ListView)findViewById(R.id.scenery_list)).getAdapter()).notifyDataSetChanged();*/
                        dialog.cancel();
                    }
                }).setTitle(SceneryActivity.this.getResources().getString(R.string.sort));
                dialog = builder.create();
                dialog.show();

			}
				break;
			case R.id.compos_spinner:
			{
                AlertDialog.Builder builder=new AlertDialog.Builder(SceneryActivity.this)
                .setSingleChoiceItems(compos, compositor_position,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) 
                    {
                    	compositor_position=position;
                    	sceneryPlaceList = TravelUtil.placeComposite(compositor_position, sceneryPlaceList,location);
                    	//adapter = new SceneryAdapter(SceneryActivity.this,dataPath,sceneryPlaceList,subCatNameMap,location,symbolMap.get(cityID),cityAreaMap,application.getDataFlag());
                    	//sceneryAdapter.notifyDataSetChanged();
                		//sceneryList.setAdapter(adapter);
                    	sceneryAdapter = new SceneryAdapter(SceneryActivity.this,dataPath,sceneryPlaceList,subCatNameMap,location,symbolMap.get(cityID),cityAreaMap,application.getDataFlag());          		
                    	sceneryList.setAdapter(sceneryAdapter);
                        dialog.cancel();
                    }
                }).setTitle(SceneryActivity.this.getResources().getString(R.string.compositor));
                dialog = builder.create();
                dialog.show();

			}
				break;
			default:
			break;
			}
			}
	};

	
	
	
	
	
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
