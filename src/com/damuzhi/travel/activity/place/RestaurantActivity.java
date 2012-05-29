/**  
        * @title RestaurantActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-15 ����4:30:35  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.place;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.RestaurantAdapter;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.PlaceActivity;
import com.damuzhi.travel.activity.common.PlaceMap;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.service.Task;
import com.damuzhi.travel.util.TravelUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-15 ����4:30:35  
 */

public class RestaurantActivity extends TravelActivity implements PlaceActivity
{
	private	static final String TAG = "RestaurantActivity";
	private ListView restaurantList;
	private TextView titleView;
	private ImageView mapView;
	private ViewGroup foodSpinner;
	private ViewGroup composSpinner;
	private ViewGroup areaSpinner;
	private ViewGroup serviceSpinner;
	private Dialog loadingDialog;
	private ArrayList<Place> restaurantPlaceList;
	private TravelApplication application;
	private String dataPath ;
	private static final int LOADING = 0;
	private static final int LOAD_OK = 1;
	private int compositor_position = 0;
	private int food_position = 0;
	private int area_position = 0;
	private int service_position = 0;
	private int cityID = -1;
	private RestaurantAdapter adapter;
	private int loadFlag = 1;//�ж��Ƿ���¼���activity
	private String[] compos;
	private String[] subCatName;
	private int[] subCatKey;
	private String[] serviceName;
	private int[] serviceID;
	private String[] areaName;
	private int[] areaID;
	private HashMap<Integer,String> symbolMap;
	private HashMap<Integer, String> cityAreaMap;
	private HashMap<Integer, String> subCatNameMap;
	private HashMap<String, Double> location;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate ");
		setContentView(R.layout.restaurant);
		MainService.allActivity.add(this);//����ǰ��activity��ӵ�Servicre��activity������	
		loadFlag = 0;
		init();
		placeInfo();
	}
	
	
	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		restaurantList = (ListView) findViewById(R.id.restaurant_list);
		titleView = (TextView) findViewById(R.id.restaurant_title);
		foodSpinner = (ViewGroup) findViewById(R.id.food_spinner);
		areaSpinner = (ViewGroup) findViewById(R.id.area_spinner);
		serviceSpinner = (ViewGroup) findViewById(R.id.service_spinner);
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
		subCatNameMap = (HashMap<Integer, String>) param[2];
		subCatName = (String[]) param[3];
		subCatKey = (int[]) param[4];
		areaName = (String[]) param[5];
		areaID = (int[]) param[6];
		serviceName = (String[])param[7];
		serviceID = (int[])param[8];
		compos = this.getResources().getStringArray(R.array.restaurant);
		restaurantPlaceList = application.getPlaceData();
		int size = restaurantPlaceList.size();
		titleView.setText(this.getResources().getString(R.string.restaurant)+"("+size+")");
		adapter = new RestaurantAdapter(this,dataPath,restaurantPlaceList,subCatNameMap,location,symbolMap.get(application.getCityID()),cityAreaMap,application.getDataFlag());
		//adapter.notifyDataSetChanged();
		restaurantList.setAdapter(adapter);
		mapView.setOnClickListener(clickListener);
		foodSpinner.setOnClickListener(clickListener);
		areaSpinner.setOnClickListener(clickListener);
		serviceSpinner.setOnClickListener(clickListener);		
		composSpinner.setOnClickListener(clickListener);
	}

	@Override
	public void placeInfo()
	{
		// TODO Auto-generated method stub
		restaurantList.setOnItemClickListener(itemClickListener);	
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
			showRoundProcessDialog(RestaurantActivity.this, R.layout.loading_process_dialog_anim);
			Task hotelTask = new Task(Task.TASK_LOGIN_RESTAURANT,RestaurantActivity.this);
			MainService.newTask(hotelTask);
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
			Place place = restaurantPlaceList.get(arg2);			
			application.setPlace(place);
			Intent intent = new Intent(RestaurantActivity.this, RestaurantDetailActivity.class);
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
				application.setPlaceCategoryID(PlaceCategoryType.PLACE_RESTRAURANT_VALUE);
				Intent intent = new Intent(RestaurantActivity.this,PlaceMap.class);
				startActivity(intent);
				break;
			case R.id.food_spinner:
			{
                AlertDialog.Builder builder=new AlertDialog.Builder(RestaurantActivity.this)
                .setSingleChoiceItems(subCatName, food_position,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) 
                    {
                    	food_position=position;
                    	restaurantPlaceList = TravelUtil.sort(subCatKey[food_position], application.getPlaceData());
                    	adapter.setList(restaurantPlaceList);
                    	adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                }).setTitle(RestaurantActivity.this.getResources().getString(R.string.price));
                dialog = builder.create();
                dialog.show();
			}
				break;
			case R.id.area_spinner:
			{
                AlertDialog.Builder builder=new AlertDialog.Builder(RestaurantActivity.this)
                .setSingleChoiceItems(areaName, area_position,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) 
                    {
                    	area_position=position;
                    	restaurantPlaceList = TravelUtil.area(areaID[area_position], application.getPlaceData());
                    	adapter.setList(restaurantPlaceList);
                    	adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                }).setTitle(RestaurantActivity.this.getResources().getString(R.string.area));
                dialog = builder.create();
                dialog.show();

			}
				break;
			case R.id.service_spinner:
			{
                AlertDialog.Builder builder=new AlertDialog.Builder(RestaurantActivity.this)
                .setSingleChoiceItems(serviceName, service_position,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) 
                    {
                    	service_position=position;
                    	restaurantPlaceList = TravelUtil.service(serviceID[service_position], application.getPlaceData());
                    	adapter.setList(restaurantPlaceList);
                    	adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                }).setTitle(RestaurantActivity.this.getResources().getString(R.string.service));
                dialog = builder.create();
                dialog.show();

			}
				break;
			case R.id.compos_spinner:
			{
                AlertDialog.Builder builder=new AlertDialog.Builder(RestaurantActivity.this)
                .setSingleChoiceItems(compos, compositor_position,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) 
                    {
                    	compositor_position=position;
                    	restaurantPlaceList = TravelUtil.restaurantComposite(compositor_position, restaurantPlaceList,location);
                    	adapter.setList(restaurantPlaceList);
                    	adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                }).setTitle(RestaurantActivity.this.getResources().getString(R.string.sort));
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
	            		Intent intent = new Intent(RestaurantActivity.this, IndexActivity.class);
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
	        // ע��˴�Ҫ����show֮�� ����ᱨ�쳣
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
