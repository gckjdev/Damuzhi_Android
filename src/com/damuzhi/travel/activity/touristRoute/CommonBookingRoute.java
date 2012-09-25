package com.damuzhi.travel.activity.touristRoute;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.apache.commons.httpclient.methods.multipart.StringPart;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.FilterAdapter;
import com.damuzhi.travel.activity.adapter.common.SortAdapter;
import com.damuzhi.travel.activity.adapter.common.SortAdapter.SortViewHolder;
import com.damuzhi.travel.activity.common.calendar.CalendarActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.protos.TouristRouteProtos.Booking;
import com.damuzhi.travel.protos.TouristRouteProtos.DepartPlace;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRoute;
import com.google.protobuf.InvalidProtocolBufferException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class CommonBookingRoute extends Activity {

	
	private TextView routeNameTextView;
	//private TextView routeIdTextView;
	//private TextView departCityTextView;
	private TextView priceTextView;
	private TextView bookingNoticeTextView;
	private Button departTimeButton;
	//private Button departPlaceButton;
	private Button adultNumberButton;
	private Button childrenNumberButton;
	private Button memberBookingButton;
	private Button nonMemberBookingButton;
	private PopupWindow filterWindow;
	private int statusBarHeight;
	private SortAdapter sortAdapter;
	private LocalRoute localRoute ;
	private HashMap<Integer, Boolean> adultSelected = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> childSelected = new HashMap<Integer, Boolean>();
	//private HashMap<Integer, Boolean> departPlaceSelected = new HashMap<Integer, Boolean>();
	int adultSelectedPosition = 0;
	int childSelectedPosition = 0;
	int departPlaceSelectedPosition = 0;

	private String[] departPlaceName;
	private String[] departPlaceID;
	private String[] adultNum;
	private String[] childNum;
	private String[] bookingData;
	
	private  LayoutInflater lay;
	private  View popupView;
	private  Button cancelButton;
	private  Button filterButton;
	
	private String departTime = "";
	private int routeId = 0;
	private int filterFlag = 0; //0=adult 1,=child,2=departplace
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_booking_route);
		
		if(statusBarHeight == 0)
		{
			Class<?> c = null;
			Object obj = null;
			Field field = null;
			int x = 0;
		    try
			{
				c = Class.forName("com.android.internal.R$dimen");
				obj = c.newInstance();
			    field = c.getField("status_bar_height");
			    x = Integer.parseInt(field.get(obj).toString());
			    statusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		bookingData = new String[5];
		byte[] data = getIntent().getByteArrayExtra("booking");		
		try {
			localRoute = LocalRoute.parseFrom(data);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		bookingData[4] = localRoute.getName();
		routeNameTextView = (TextView) findViewById(R.id.route_name);
		/*routeIdTextView = (TextView) findViewById(R.id.route_id);
		departCityTextView = (TextView) findViewById(R.id.depart_city);*/
		priceTextView = (TextView) findViewById(R.id.route_price);
		bookingNoticeTextView = (TextView) findViewById(R.id.booking_notice);
		//departPlaceButton = (Button) findViewById(R.id.depart_place_button);
		departTimeButton = (Button) findViewById(R.id.depart_time_button);
		adultNumberButton = (Button) findViewById(R.id.adult_number_button);
		childrenNumberButton = (Button) findViewById(R.id.children_number_button);
		memberBookingButton = (Button) findViewById(R.id.member_booking_order);
		nonMemberBookingButton = (Button) findViewById(R.id.non_member_booking_order);
		routeNameTextView.setText(localRoute.getName());
		//routeIdTextView.setText(""+localRoute.getRouteId());
		/*String departCity = AppManager.getInstance().getcityNameById(localRoute.getCityId());
		departCityTextView.setText(departCity);*/
		//String price = AppManager.getInstance().getSymbolByCityId(localRoute.getCityId())+localRoute.getPrice();
		priceTextView.setText(localRoute.getPrice());
		Spanned notice = Html.fromHtml("<font>"+getString(R.string.shuo_ming)+"</font>"+"<br>"+getString(R.string.booking_notice_content)+"</br>");
		bookingNoticeTextView.setText(notice);
		//departPlaceButton.setOnClickListener(departPlaceOnClickListener);
		adultNumberButton.setOnClickListener(adultNumOnClickListener);
		childrenNumberButton.setOnClickListener(childNumOnClickListener);
		departTimeButton.setOnClickListener(departTimeOnClickListener);
		nonMemberBookingButton.setOnClickListener(nonMemberOnClickListener);
		initSelectValues();
	}

	
	
	
	private void initSelectValues()
	{
		departPlaceName = new String[localRoute.getDepartPlacesCount()];
		departPlaceID = new String[localRoute.getDepartPlacesCount()];
		int i=0;
		for(DepartPlace departPlace:localRoute.getDepartPlacesList())
		{
			departPlaceName[i] = departPlace.getDepartPlace();
			departPlaceID[i] = String.valueOf(departPlace.getDepartPlaceId());
			i++;
		}
		adultNum = getResources().getStringArray(R.array.adult_number);
		childNum = getResources().getStringArray(R.array.child_number);
	}
	
	private OnClickListener nonMemberOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(getBookingData())
			{
				Intent intent = new Intent();
				intent.setClass(CommonBookingRoute.this, CommonBookingConfirm.class);
				intent.putExtra("bookingData", bookingData);
				startActivity(intent);
			}
			
		}

		private boolean getBookingData() {
			
			/*if(departPlaceSelected.isEmpty())
			{
				Toast.makeText(CommonBookingRoute.this, getString(R.string.select_depart_place_toast), Toast.LENGTH_LONG).show();
				return false;
			}*/	
			if(departTime.trim().equals(""))
			{
				Toast.makeText(CommonBookingRoute.this, getString(R.string.select_depart_time_toast), Toast.LENGTH_LONG).show();
				return false;
			}
			
			bookingData[0] = String.valueOf(localRoute.getRouteId());
			//bookingData[1] = departPlaceID[departPlaceSelectedPosition];
			bookingData[1] = departTime.replaceAll("/", "");
			bookingData[2] = String.valueOf(adultSelectedPosition+1);
			bookingData[3] = String.valueOf(childSelectedPosition);
			return true;
			
		}
	};
	
	
	
	
	
	
	
	/*private OnClickListener departPlaceOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			filterFlag = 2;
			String sortTitle = getString(R.string.depart_place_title);
			if (departPlaceName != null)
			{
				sortWindow(v, departPlaceName, departPlaceSelected,sortTitle);
			}			
		}
	};*/
	
	private OnClickListener adultNumOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			filterFlag = 0;
			String sortTitle = getString(R.string.route_booking_number);
			if (adultNum != null)
			{
				sortWindow(v, adultNum, adultSelected,sortTitle);
			}			
		}
	};
	
	
	
	private OnClickListener childNumOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			filterFlag = 1;
			String sortTitle = getString(R.string.route_booking_number);
			if (childNum != null)
			{
				sortWindow(v, childNum, childSelected,sortTitle);
			}			
		}
	};
	
	
	
	
	 private void sortWindow(View parent,String[] sortTitleName,HashMap<Integer, Boolean> isSelected,String filterTitle) {  

	        
	        lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	        popupView = lay.inflate(R.layout.filter_place_popup, null); 
	        popupView.setPadding(0, statusBarHeight, 0, 0);
	        popupView.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_page_bg2));        
	        ListView sortList=(ListView)popupView.findViewById(R.id.filter_listview);
	        popupView.findViewById(R.id.listview_group).setPadding(0, (int)getResources().getDimension(R.dimen.sort_list_padding_top), 0, 0);
	        
	        cancelButton = (Button) popupView.findViewById(R.id.cancel_button);
	        filterButton = (Button) popupView.findViewById(R.id.ok_button);
	        filterButton.setVisibility(View.GONE);
	        TextView titleTextView = (TextView) popupView.findViewById(R.id.filter_title);
	        titleTextView.setText(filterTitle);
	        cancelButton.setOnClickListener(cancelSelectOnClickListener);
	    
	       
	        sortAdapter=new SortAdapter(CommonBookingRoute.this,sortTitleName);
	        sortAdapter.setIsSelected(isSelected);
	        sortList.setAdapter(sortAdapter);  
	       
	        sortAdapter.notifyDataSetChanged();
	        sortList.setItemsCanFocus(false);  
	        sortList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);  
	        sortList.setOnItemClickListener(selectedOnClickListener);  
	       
	        filterWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT,true);  
	          
	        //IsSelectedTemp = sortAdapter.getIsSelected();
	        filterWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_page_bg2));  
	        filterWindow.setFocusable(true);  
	        filterWindow.update();  
	        filterWindow.showAtLocation(parent, Gravity.TOP, 0, 0);  
		  }  
	
	
	 private OnClickListener cancelSelectOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			if(filterWindow !=null)
			{
				filterWindow.dismiss();
			}
			
		}
	};
		
		
	
	
		
	private  OnItemClickListener selectedOnClickListener = new OnItemClickListener() {  
        @Override  
        public void onItemClick(AdapterView<?> parent, View view, int position,  
                long id) {  
        	
        	SortViewHolder vHollder = (SortViewHolder) view.getTag();    
            vHollder.cBox.toggle();
            if(filterFlag == 0)
            {
            	adultSelected.clear();
            	adultSelected.put(position, vHollder.cBox.isChecked());
            	sortAdapter.setIsSelected(adultSelected);
            	adultNumberButton.setText(adultNum[position]);
            	adultSelectedPosition = position;
            }else {
            	childSelected.clear();
            	childSelected.put(position, vHollder.cBox.isChecked());
            	sortAdapter.setIsSelected(childSelected);
            	childrenNumberButton.setText(childNum[position]);
            	childSelectedPosition = position;
			}/*else
            {
				departPlaceSelected.clear();
				departPlaceSelected.put(position, vHollder.cBox.isChecked());
            	sortAdapter.setIsSelected(departPlaceSelected);
            	departPlaceButton.setText(departPlaceName[position]);
            	departPlaceSelectedPosition = position;
            }*/
            sortAdapter.notifyDataSetChanged();
            //departPlaceButton.setText(departPlaceName[position]);
           
            if(filterWindow !=null)
			{
				filterWindow.dismiss();
			}
        }  
    };  
    
    
   
    
    
    private OnClickListener departTimeOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("localRouteTime", localRoute.toByteArray());
			intent.setClass(CommonBookingRoute.this, CalendarActivity.class);
			//startActivity(intent);
			startActivityForResult(intent, 0);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			Bundle b = data.getExtras();
			departTime = b.getString("date");
			departTimeButton.setText(departTime);
			break;
		default:
			break;
		}
	}
	 
	
	
}
