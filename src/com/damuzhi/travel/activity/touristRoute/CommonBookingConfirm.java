package com.damuzhi.travel.activity.touristRoute;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.more.FeedBackActivity;
import com.damuzhi.travel.mission.more.FeedbackMission;
import com.damuzhi.travel.mission.touristRoute.TouristRouteMission;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.util.TravelUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CommonBookingConfirm extends Activity {

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
			String contact = contactEditText.getText().toString();
			String contactPerson = contactPersonEditText.getText().toString();
			if(contactPerson==null||contactPerson.trim().equals(""))
			{
				Toast.makeText(CommonBookingConfirm.this, getString(R.string.contact_hint), Toast.LENGTH_SHORT).show();
			}else if (contact == null ||contact.trim().equals("")) {
				Toast.makeText(CommonBookingConfirm.this, getString(R.string.contact_person_hint), Toast.LENGTH_SHORT).show();
			}else
			{
				boolean isNumber = TravelUtil.isNumber(contact);
				if(!isNumber)
				{
					Toast.makeText(CommonBookingConfirm.this, getString(R.string.contact_toast), Toast.LENGTH_SHORT).show();
				}else
				{
					try
					{
						contactPerson = URLEncoder.encode(contactPerson, "UTF-8");
					} catch (UnsupportedEncodingException e)
					{
						e.printStackTrace();
					}
					String userId = UserManager.getInstance().getUserId(CommonBookingConfirm.this);
					String bookingURL = String.format(ConstantField.LOCAL_ROUTE_NON_MENBER_USER_SUBMIT_BOOKING_URL, userId,bookingData[0],bookingData[1],"",bookingData[2],bookingData[3],contactPerson,contact);
					Log.d(TAG, "booking date = "+bookingData[2]);
					TouristRouteMission touristRouteMission = TouristRouteMission.getInstance();
					boolean result = touristRouteMission.submitBooking(bookingURL);
					if(result)
					{
						Toast.makeText(CommonBookingConfirm.this, getString(R.string.feedback_submit_success), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(CommonBookingConfirm.this, CommonTouristRouteOrderList.class);
						startActivity(intent);
					}else {
						Toast.makeText(CommonBookingConfirm.this, getString(R.string.feedback_submit_fail), Toast.LENGTH_SHORT).show();
					}
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
}
