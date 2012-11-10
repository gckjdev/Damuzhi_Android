package com.damuzhi.travel.activity.touristRoute;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.commons.httpclient.methods.multipart.StringPart;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.FilterAdapter;
import com.damuzhi.travel.activity.adapter.common.SortAdapter;
import com.damuzhi.travel.activity.adapter.common.SortAdapter.SortViewHolder;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.calendar.CalendarActivity;
import com.damuzhi.travel.activity.more.LoginActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.mission.touristRoute.TouristRouteMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.protos.TouristRouteProtos.Booking;
import com.damuzhi.travel.protos.TouristRouteProtos.DepartPlace;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRoute;
import com.damuzhi.travel.util.TravelUtil;
import com.google.protobuf.InvalidProtocolBufferException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
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

public class CommonBookingRouteActivity extends Activity
{

	private static final String TAG = "CommonBookingRouteActivity";
	private TextView routeNameTextView;
	// private TextView routeIdTextView;
	// private TextView departCityTextView;
	private TextView priceTextView;
	private TextView bookingNoticeTextView;
	private Button departTimeButton;
	// private Button departPlaceButton;
	private Button adultNumberButton;
	private Button childrenNumberButton;
	private Button memberBookingButton;
	private Button nonMemberBookingButton;
	private Button consultButton;
	private PopupWindow filterWindow;
	private int statusBarHeight;
	private SortAdapter sortAdapter;
	private LocalRoute localRoute;
	private HashMap<Integer, Boolean> adultSelected = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> childSelected = new HashMap<Integer, Boolean>();
	// private HashMap<Integer, Boolean> departPlaceSelected = new HashMap<Integer, Boolean>();
	int adultSelectedPosition = 0;
	int childSelectedPosition = 0;
	int departPlaceSelectedPosition = 0;

	private String[] departPlaceName;
	private String[] departPlaceID;
	private String[] adultNum;
	private String[] childNum;
	private String[] bookingData;

	private LayoutInflater lay;
	private View popupView;
	private Button cancelButton;
	private Button filterButton;

	private long departTime = 0;
	private int routeId = 0;
	private int filterFlag = 0; // 0=adult 1,=child,2=departplace
	String token = "";
	String loginId ="";
	private int adultPrice = 0;
	private int childPrice = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_booking_route);
		ActivityMange.getInstance().addActivity(this);
		if (statusBarHeight == 0)
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
		try
		{
			localRoute = LocalRoute.parseFrom(data);
		} catch (InvalidProtocolBufferException e)
		{
			e.printStackTrace();
		}
		bookingData[4] = localRoute.getName();
		routeNameTextView = (TextView) findViewById(R.id.route_name);
		/*
		 * routeIdTextView = (TextView) findViewById(R.id.route_id); departCityTextView = (TextView) findViewById(R.id.depart_city);
		 */
		priceTextView = (TextView) findViewById(R.id.route_price);
		bookingNoticeTextView = (TextView) findViewById(R.id.booking_notice);
		// departPlaceButton = (Button) findViewById(R.id.depart_place_button);
		departTimeButton = (Button) findViewById(R.id.depart_time_button);
		adultNumberButton = (Button) findViewById(R.id.adult_number_button);
		childrenNumberButton = (Button) findViewById(R.id.children_number_button);
		memberBookingButton = (Button) findViewById(R.id.member_booking_order);
		nonMemberBookingButton = (Button) findViewById(R.id.non_member_booking_order);
		consultButton = (Button) findViewById(R.id.consult_button);
		routeNameTextView.setText(localRoute.getName());
		// routeIdTextView.setText(""+localRoute.getRouteId());
		/*
		 * String departCity = AppManager.getInstance().getcityNameById(localRoute.getCityId()); departCityTextView.setText(departCity);
		 */
		// String price = AppManager.getInstance().getSymbolByCityId(localRoute.getCityId())+localRoute.getPrice();
		priceTextView.setText(localRoute.getCurrency()+localRoute.getPrice());
		Spanned notice = Html.fromHtml("<font>" + getString(R.string.shuo_ming)+ "</font>" + "<br>"+ getString(R.string.booking_notice_content) + "</br>");
		bookingNoticeTextView.setText(notice);
		// departPlaceButton.setOnClickListener(departPlaceOnClickListener);
		adultNumberButton.setOnClickListener(adultNumOnClickListener);
		childrenNumberButton.setOnClickListener(childNumOnClickListener);
		departTimeButton.setOnClickListener(departTimeOnClickListener);
		nonMemberBookingButton.setOnClickListener(nonMemberOnClickListener);
		memberBookingButton.setOnClickListener(memberOnClickListener);
		consultButton.setOnClickListener(consultOnClickListener);
		adultSelected.put(0, true);
		childSelected.put(0, true);
		initSelectValues();
	}

	private void initSelectValues()
	{
		departPlaceName = new String[localRoute.getDepartPlacesCount()];
		departPlaceID = new String[localRoute.getDepartPlacesCount()];
		int i = 0;
		for (DepartPlace departPlace : localRoute.getDepartPlacesList())
		{
			departPlaceName[i] = departPlace.getDepartPlace();
			departPlaceID[i] = String.valueOf(departPlace.getDepartPlaceId());
			i++;
		}
		adultNum = getResources().getStringArray(R.array.adult_number);
		childNum = getResources().getStringArray(R.array.child_number);
	}

	private OnClickListener nonMemberOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			if (getBookingData())
			{
				Intent intent = new Intent();
				intent.setClass(CommonBookingRouteActivity.this,CommonBookingConfirmActivity.class);
				intent.putExtra("bookingData", bookingData);
				startActivity(intent);
			}

		}
	};
	
	private OnClickListener memberOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			
			if (getBookingData())
			{
				if(token!= null&&!token.equals(""))
				{

					AlertDialog leaveAlertDialog = new AlertDialog.Builder(CommonBookingRouteActivity.this).create();
					leaveAlertDialog.setMessage(getBaseContext().getString(R.string.booking_order_tips));
					leaveAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getBaseContext().getString(R.string.ok),new DialogInterface.OnClickListener()
					{
						
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							try
							{
								token = URLEncoder.encode(token, "UTF-8");
							} catch (UnsupportedEncodingException e)
							{
								e.printStackTrace();
							}
							boolean result = TouristRouteMission.getInstance().memberBookingOrder(loginId,token,bookingData[0],"",bookingData[1],bookingData[2],bookingData[3]);
							//String resultInfo = TouristRouteMission.getInstance().getResultInfo();
							if(result)
							{
								Toast.makeText(CommonBookingRouteActivity.this, R.string.booking_route_order_success, Toast.LENGTH_SHORT).show();
								Intent intent = new Intent();
								intent.setClass(CommonBookingRouteActivity.this, CommonTouristRouteOrderListActivity.class);
								startActivity(intent);
							}else {
								Toast.makeText(CommonBookingRouteActivity.this, getString(R.string.booking_route_order_fail), Toast.LENGTH_SHORT).show();
							}
							
							
						}
					} );
					leaveAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+getBaseContext().getString(R.string.cancel),new DialogInterface.OnClickListener()
					{
						
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.cancel();
							
						}
					} );
					leaveAlertDialog.show();
			}else
				{
					Intent intent = new Intent();
					intent.setClass(CommonBookingRouteActivity.this,LoginActivity.class);
					//intent.putExtra("booking", 1);
					startActivity(intent);
				}
				
			}

		}
	};

	private boolean getBookingData()
	{

		/*
		 * if(departPlaceSelected.isEmpty()) { Toast.makeText(CommonBookingRoute.this, getString(R.string.select_depart_place_toast), Toast.LENGTH_LONG).show(); return false; }
		 */
		if (departTime == 0)
		{
			Toast.makeText(CommonBookingRouteActivity.this,getString(R.string.select_depart_time_toast),Toast.LENGTH_LONG).show();
			return false;
		}

		bookingData[0] = String.valueOf(localRoute.getRouteId());
		// bookingData[1] = departPlaceID[departPlaceSelectedPosition];
		bookingData[1] = TravelUtil.getDateLongString(departTime);
		//bookingData[1] = TravelUtil.getDateShortString(departTime);
		bookingData[2] = String.valueOf(adultSelectedPosition + 1);
		bookingData[3] = String.valueOf(childSelectedPosition);
		return true;

	}

	/*
	 * private OnClickListener departPlaceOnClickListener = new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { filterFlag = 2; String sortTitle = getString(R.string.depart_place_title); if (departPlaceName != null) { sortWindow(v, departPlaceName, departPlaceSelected,sortTitle); } } };
	 */

	private OnClickListener adultNumOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			if (departTime == 0)
			{
				Toast.makeText(CommonBookingRouteActivity.this,R.string.select_depart_time,Toast.LENGTH_LONG).show();
				return;
			}
			filterFlag = 0;
			String sortTitle = getString(R.string.route_booking_number);
			if (adultNum != null)
			{
				choiceWindow(v, adultNum, adultSelected, sortTitle);
			}
		}
	};

	private OnClickListener childNumOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			if (departTime == 0)
			{
				Toast.makeText(CommonBookingRouteActivity.this,R.string.select_depart_time,Toast.LENGTH_LONG).show();
				return;
			}
			filterFlag = 1;
			String sortTitle = getString(R.string.route_booking_number);
			if (childNum != null)
			{
				choiceWindow(v, childNum, childSelected, sortTitle);
			}
		}
	};

	private void choiceWindow(View parent, String[] sortTitleName,HashMap<Integer, Boolean> isSelected, String filterTitle)
	{

		lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popupView = lay.inflate(R.layout.filter_place_popup, null);
		popupView.setPadding(0, statusBarHeight, 0, 0);
		popupView.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_page_bg2));
		ListView sortList = (ListView) popupView.findViewById(R.id.filter_listview);
		popupView.findViewById(R.id.listview_group).setPadding(0,(int) getResources().getDimension(R.dimen.sort_list_padding_top), 0, 0);

		cancelButton = (Button) popupView.findViewById(R.id.cancel_button);
		filterButton = (Button) popupView.findViewById(R.id.ok_button);
		filterButton.setVisibility(View.GONE);
		TextView titleTextView = (TextView) popupView.findViewById(R.id.filter_title);
		titleTextView.setText(filterTitle);
		cancelButton.setOnClickListener(cancelSelectOnClickListener);

		sortAdapter = new SortAdapter(CommonBookingRouteActivity.this, sortTitleName,true);
		sortAdapter.setIsSelected(isSelected);
		sortList.setAdapter(sortAdapter);
		sortAdapter.notifyDataSetChanged();
		sortList.setItemsCanFocus(false);
		sortList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		sortList.setOnItemClickListener(selectedOnClickListener);

		filterWindow = new PopupWindow(popupView,android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT, true);

		// IsSelectedTemp = sortAdapter.getIsSelected();
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
			if (filterWindow != null)
			{
				filterWindow.dismiss();
			}

		}
	};

	private OnItemClickListener selectedOnClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			int price = 0;
			SortViewHolder vHollder = (SortViewHolder) view.getTag();
			vHollder.cBox.toggle();
			if (filterFlag == 0)
			{
				adultSelected.clear();
				adultSelected.put(position, vHollder.cBox.isChecked());
				sortAdapter.setIsSelected(adultSelected);
				adultNumberButton.setText(adultNum[position]);
				adultNumberButton.setTextColor(getResources().getColor(R.color.little_blue));
				adultSelectedPosition = position;
				
			
			} else
			{
				childSelected.clear();
				childSelected.put(position, vHollder.cBox.isChecked());
				sortAdapter.setIsSelected(childSelected);
				childrenNumberButton.setText(childNum[position]);
				childrenNumberButton.setTextColor(getResources().getColor(R.color.little_blue));
				childSelectedPosition = position;			
			}
			price =  price +(adultSelectedPosition+1)*adultPrice+childSelectedPosition*childPrice;
			priceTextView.setText(localRoute.getCurrency()+price);
			sortAdapter.notifyDataSetChanged();
			if (filterWindow != null)
			{
				filterWindow.dismiss();
			}
		}
	};

	private OnClickListener departTimeOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			intent.putExtra("localRouteTime", localRoute.toByteArray());
			intent.setClass(CommonBookingRouteActivity.this, CalendarActivity.class);
			startActivityForResult(intent, 0);
		}
	};

	
	private OnClickListener consultOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			makePhoneCall(localRoute.getCustomerServiceTelephone());
			
		}
	};
	
	
	
	public void makePhoneCall( final String phoneNumber)
	{
		AlertDialog phoneCall = new AlertDialog.Builder(CommonBookingRouteActivity.this).create();
		//phoneCall.setMessage(getResources().getString(R.string.make_phone_call)+"\n"+phoneNumber);
		View view = getLayoutInflater().inflate(R.layout.alert_dialog, null);
		TextView messageTextView = (TextView) view.findViewById(R.id.message);
		messageTextView.setText(phoneNumber);
		phoneCall.setTitle(getString(R.string.make_phone_call));
		//phoneCall.setMessage(phoneNumber);
		phoneCall.setView(view);
		phoneCall.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.call),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+phoneNumber));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				CommonBookingRouteActivity.this.startActivity(intent);
			}
		} );
		phoneCall.setButton(DialogInterface.BUTTON_NEGATIVE,""+getBaseContext().getString(R.string.cancel),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				
			}
		} );
		phoneCall.show();
		
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode)
		{
		case RESULT_OK:
			Bundle b = data.getExtras();
			departTime = b.getLong("date");
			adultPrice = b.getInt("adult",0);
			childPrice = b.getInt("child",0);
			Log.d(TAG, "booking order adult price = "+adultPrice);
			Log.d(TAG, "booking order child price = "+childPrice);
			departTimeButton.setText(TravelUtil.getDateShortString(departTime));
			departTimeButton.setTextColor(getResources().getColor(R.color.little_blue));
			int price = 0;
			price =  price +(adultSelectedPosition+1)*adultPrice+childSelectedPosition*childPrice;
			priceTextView.setText(localRoute.getCurrency()+price);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		token = TravelApplication.getInstance().getToken();
		loginId = TravelApplication.getInstance().getLoginID();
		Log.d(TAG,"token = "+token);
		if(token!= null &&!token.equals(""))
		{
			nonMemberBookingButton.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}

}
