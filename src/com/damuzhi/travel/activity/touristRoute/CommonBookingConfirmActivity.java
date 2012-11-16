package com.damuzhi.travel.activity.touristRoute;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.entry.MainActivity;
import com.damuzhi.travel.activity.more.FeedBackActivity;
import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.mission.more.FeedbackMission;
import com.damuzhi.travel.mission.touristRoute.TouristRouteMission;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.util.TravelUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CommonBookingConfirmActivity extends Activity {

	protected static final String TAG = "CommonBookingConfirm";
	private TextView routeNameTextView;
	private EditText contactEditText;
	private EditText contactPersonEditText;
	private Button bookingButton;
	private Button cancelButton;
	private String[] bookingData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_booking_confirm);		
		ActivityMange.getInstance().addActivity(this);
		bookingData = getIntent().getStringArrayExtra("bookingData");
		routeNameTextView = (TextView) findViewById(R.id.route_name);
		contactEditText = (EditText) findViewById(R.id.contact);
		contactPersonEditText = (EditText) findViewById(R.id.contact_person);
		routeNameTextView.setText(bookingData[4]);
		
		bookingButton = (Button) findViewById(R.id.booking_button);
		cancelButton = (Button) findViewById(R.id.cancel_button);
		bookingButton.setOnClickListener(bookingOnClickListener);
		cancelButton.setOnClickListener(cancelOnClickListener);
	}

	
	
	private OnClickListener bookingOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v)
		{
			final String contact = contactEditText.getText().toString();
			final String contactPerson = contactPersonEditText.getText().toString();
			if(contactPerson==null||contactPerson.trim().equals("")){
				Toast.makeText(CommonBookingConfirmActivity.this, getString(R.string.contact_person_hint), Toast.LENGTH_SHORT).show();
			}else if (contact == null ||contact.trim().equals("")) {
				Toast.makeText(CommonBookingConfirmActivity.this, getString(R.string.contact_hint), Toast.LENGTH_SHORT).show();				
			}else{
				boolean isNumber = TravelUtil.isNumber(contact);
				if(!isNumber)
				{
					Toast.makeText(CommonBookingConfirmActivity.this, getString(R.string.contact_toast), Toast.LENGTH_SHORT).show();
				}else
				{
					AlertDialog leaveAlertDialog = new AlertDialog.Builder(CommonBookingConfirmActivity.this).create();
					leaveAlertDialog.setMessage(getBaseContext().getString(R.string.booking_order_tips));
					leaveAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getBaseContext().getString(R.string.ok),new DialogInterface.OnClickListener()
					{
						
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							try
							{
							    final String 	contactPerson2 = URLEncoder.encode(contactPerson, "UTF-8");
							    String userId = UserManager.getInstance().getUserId(CommonBookingConfirmActivity.this);
							    boolean result = TouristRouteMission.getInstance().nonMemberBookingOrder(userId, bookingData[0], "", bookingData[1], bookingData[2], bookingData[3], contactPerson2, contact);
								if(result)
								{
									Toast.makeText(CommonBookingConfirmActivity.this, R.string.booking_route_order_success, Toast.LENGTH_SHORT).show();
									Intent intent = new Intent();
									intent.setClass(CommonBookingConfirmActivity.this, CommonTouristRouteOrderListActivity.class);
									startActivity(intent);
								}else {
									Toast.makeText(CommonBookingConfirmActivity.this, R.string.booking_route_order_fail, Toast.LENGTH_SHORT).show();
								}
							} catch (Exception e)
							{
								e.printStackTrace();
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
					
				}
			}
			
		}
	};
	
	
	private OnClickListener cancelOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();			
		}
	};

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}
}
