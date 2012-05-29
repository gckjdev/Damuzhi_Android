package com.damuzhi.travel.activity.place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.HotelAdapter;
import com.damuzhi.travel.activity.adapter.place.SceneryAdapter;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.PlaceActivity;
import com.damuzhi.travel.activity.common.PlaceMap;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.service.Task;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.TravelUtil.ComparatorDistance;
import com.damuzhi.travel.util.TravelUtil.ComparatorPrice;
import com.damuzhi.travel.util.TravelUtil.ComparatorRank;
import com.damuzhi.travel.util.TravelUtil.ComparatorStartRank;

public class HotelActivity extends TravelActivity implements PlaceActivity
{
	private	static final String TAG = "HotelActivity";
	private ListView hotelList;
	private TextView titleView;
	private ImageView mapView;
	private ViewGroup priceSpinner;
	private ViewGroup composSpinner;
	private ViewGroup areaSpinner;
	private ViewGroup serviceSpinner;
	private Dialog loadingDialog;
	private ArrayList<Place> hotelPlaceList;
	private TravelApplication application;
	private String dataPath ;
	private static final int LOADING = 0;
	private static final int LOAD_OK = 1;
	private int compositor_position = 0;
	private int price_position = 0;
	private int area_position = 0;
	private int service_position = 0;
	private int cityID = -1;
	private HotelAdapter adapter;
	private int loadFlag = 1;//�ж��Ƿ���¼���activity
	private String[] compos;
	private String[] serviceName;
	private int[] serviceID;
	private String[] price;
	private String[] areaName;
	private int[] areaID;
	private HashMap<Integer,String> symbolMap;
	private HashMap<Integer, String> cityAreaMap;
	private HashMap<String, Double> location;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate ");
		setContentView(R.layout.hotel);
		MainService.allActivity.add(this);//����ǰ��activity��ӵ�Servicre��activity������		
		loadFlag = 0;
		init();
		placeInfo();
	}
	
	
	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		hotelList = (ListView) findViewById(R.id.hotel_list);
		titleView = (TextView) findViewById(R.id.hotel_title);
		priceSpinner = (ViewGroup) findViewById(R.id.price_spinner);
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
		areaName = (String[]) param[2];
		areaID = (int[]) param[3];
		serviceName = (String[])param[4];
		serviceID = (int[])param[5];
		compos = this.getResources().getStringArray(R.array.hotel);
		price = this.getResources().getStringArray(R.array.priceRank);
		hotelPlaceList = application.getPlaceData();
		int size = hotelPlaceList.size();
		titleView.setText(this.getResources().getString(R.string.hotel)+"("+size+")");
		adapter = new HotelAdapter(this,dataPath,hotelPlaceList,location,symbolMap.get(application.getCityID()),cityAreaMap,application.getDataFlag());
		//adapter.notifyDataSetChanged();
		hotelList.setAdapter(adapter);
		mapView.setOnClickListener(clickListener);
		priceSpinner.setOnClickListener(clickListener);
		areaSpinner.setOnClickListener(clickListener);
		serviceSpinner.setOnClickListener(clickListener);		
		composSpinner.setOnClickListener(clickListener);
	}

	@Override
	public void placeInfo()
	{
		// TODO Auto-generated method stub
		hotelList.setOnItemClickListener(itemClickListener);	
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
		Log.d(TAG, "loadFlag = "+loadFlag);
		Log.d(TAG, "onResume ");
		super.onResume();
		if(loadFlag == 0)
		{			
			loadFlag = 1;
			showRoundProcessDialog(HotelActivity.this, R.layout.loading_process_dialog_anim);
			Task hotelTask = new Task(Task.TASK_LOGIN_HOTEL,HotelActivity.this);
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
			Place place = hotelPlaceList.get(arg2);
			application.setPlace(place);
			Intent intent = new Intent(HotelActivity.this, HotelDetailActivity.class);
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
				application.setPlaceCategoryID(PlaceCategoryType.PLACE_HOTEL_VALUE);
				Intent intent = new Intent(HotelActivity.this,PlaceMap.class);
				startActivity(intent);
				break;
			case R.id.service_spinner:
			{
                AlertDialog.Builder builder=new AlertDialog.Builder(HotelActivity.this)
                .setSingleChoiceItems(serviceName, service_position,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) 
                    {
                    	service_position=position;
                    	hotelPlaceList = TravelUtil.service(serviceID[service_position], application.getPlaceData());
                    	adapter.setList(hotelPlaceList);
                    	adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                }).setTitle(HotelActivity.this.getResources().getString(R.string.service));
                dialog = builder.create();
                dialog.show();

			}
				break;
			case R.id.area_spinner:
			{
                AlertDialog.Builder builder=new AlertDialog.Builder(HotelActivity.this)
                .setSingleChoiceItems(areaName, area_position,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) 
                    {
                    	area_position=position;
                    	hotelPlaceList = TravelUtil.area(areaID[area_position], application.getPlaceData());
                    	adapter.setList(hotelPlaceList);
                    	adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                }).setTitle(HotelActivity.this.getResources().getString(R.string.area));
                dialog = builder.create();
                dialog.show();

			}
				break;
			case R.id.price_spinner:
			{
                AlertDialog.Builder builder=new AlertDialog.Builder(HotelActivity.this)
                .setSingleChoiceItems(price, price_position,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) 
                    {
                    	price_position=position;
                    	hotelPlaceList = TravelUtil.price(price_position, application.getPlaceData());
                    	adapter.setList(hotelPlaceList);
                    	adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                }).setTitle(HotelActivity.this.getResources().getString(R.string.price));
                dialog = builder.create();
                dialog.show();

			}
				break;
			case R.id.compos_spinner:
			{
                AlertDialog.Builder builder=new AlertDialog.Builder(HotelActivity.this)
                .setSingleChoiceItems(compos, compositor_position,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) 
                    {
                    	compositor_position=position;
                    	hotelPlaceList = TravelUtil.hotelComposite(compositor_position, hotelPlaceList,location);
                    	adapter.setList(hotelPlaceList);
                    	adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                }).setTitle(HotelActivity.this.getResources().getString(R.string.sort));
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
	            		Intent intent = new Intent(HotelActivity.this, IndexActivity.class);
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
