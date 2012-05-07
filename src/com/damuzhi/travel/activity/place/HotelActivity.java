package com.damuzhi.travel.activity.place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.HotelAdapter;
import com.damuzhi.travel.activity.adapter.place.SceneryAdapter;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.PlaceActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.PlaceListProtos.Place;

public class HotelActivity extends MenuActivity implements PlaceActivity
{
	private	static final String TAG = "HotelActivity";
	private ListView placeList;
	private TextView titleView;
	private Spinner sortSpinner;
	private Spinner compositorSpinner;
	private ArrayList<Place> hotelList;
	private TravelApplication application;
	private String dataPath ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotel);
		init();
		placeInfo();
	}
	
	
	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		application = (TravelApplication) this.getApplication();
		dataPath = String.format(ConstantField.DATA_PATH,application.getCityID());
		HashMap<Integer,NameIdPair> subCatMap = application.getSubCatMap();
		HashMap<Integer,NameIdPair> proSerMap = application.getProSerMap();
		List<String> subCatList = application.getSubCatList();
		List<String> proSerList = application.getProSerList();
		HashMap<Integer,City> cityMap = application.getCity();
		HashMap<Integer, CityArea> cityAreaMap = application.getCityAreaMap();
		HashMap<String, Double> location = application.getLocation();
		placeList = (ListView) findViewById(R.id.hotel_list);
		titleView = (TextView) findViewById(R.id.hotel_title);
		sortSpinner = (Spinner) findViewById(R.id.hotel_sort);
		compositorSpinner = (Spinner) findViewById(R.id.hotel_order);
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
		Log.d(TAG, "datapath = "+dataPath);
		//PlaceData placeData = new PlaceData(dataPath);
		hotelList = application.getPlaceData();
		int size = hotelList.size();
		titleView.setText(this.getResources().getString(R.string.scenery)+"("+size+")");
		HotelAdapter adapter = new HotelAdapter(this,dataPath,hotelList,subCatMap,proSerMap,location,cityMap,cityAreaMap);
		placeList.setAdapter(adapter);
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
			Place place = hotelList.get(arg2);
			application.setPlace(place);
			Intent intent = new Intent(HotelActivity.this, SceneryDetailActivity.class);
			startActivity(intent);
		}
	};

	@Override
	public void refresh()
	{
		// TODO Auto-generated method stub
		
	}

}
