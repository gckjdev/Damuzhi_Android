/**  
        * @title CommonRouteEvaluateActivity.java  
        * @package com.damuzhi.travel.activity.touristRoute  
        * @description   
        * @author liuxiaokun  
        * @update 2012-10-10 上午10:23:56  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.touristRoute;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.touristRoute.TouristRouteMission;
import com.damuzhi.travel.protos.TouristRouteProtos.Order;
import com.google.protobuf.InvalidProtocolBufferException;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-10-10 上午10:23:56  
 */

public class CommonRouteFeedbackActivity extends Activity
{

	private Order order;
	private ImageView good1ImageView;
	private ImageView good2ImageView;
	private ImageView good3ImageView;
	private Button sendButton;
	private EditText contentEditText;
	int praiseRank = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_route_feedback);
		ActivityMange.getInstance().addActivity(this);
		byte[] data = getIntent().getByteArrayExtra("route_order");
		try
		{
			order = Order.parseFrom(data);
		} catch (InvalidProtocolBufferException e)
		{
			e.printStackTrace();
		}
		good1ImageView = (ImageView) findViewById(R.id.good1);
		good2ImageView = (ImageView) findViewById(R.id.good2);
		good3ImageView = (ImageView) findViewById(R.id.good3);
		good1ImageView.setTag(1);
		good2ImageView.setTag(2);
		good3ImageView.setTag(3);
		sendButton = (Button) findViewById(R.id.send_button);
		contentEditText = (EditText) findViewById(R.id.content);
		initGoodImageView();
		if(order.getFeedback()!= null)
		{
			contentEditText.setText(order.getFeedback());
		}
		good1ImageView.setOnClickListener(imageViewOnClickListener);
		good2ImageView.setOnClickListener(imageViewOnClickListener);
		good3ImageView.setOnClickListener(imageViewOnClickListener);
		sendButton.setOnClickListener(sendOnClickListener);
	}

	
	private void initGoodImageView()
	{
		switch (order.getPraiseRank())
		{
		case 1:
			good1ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good));
			break;
		case 2:
			good1ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good));
			good2ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good));
			break;
		case 3:
			good1ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good));
			good2ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good));
			good3ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good));
			break;
		default:
			break;
		}
	}
	
	private OnClickListener imageViewOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			int tag = (Integer)v.getTag();
			if(tag == 1)
			{
				good1ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good));
				good2ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good2));
				good3ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good2));
				praiseRank = 1;
				return ;
			}
			if(tag == 2)
			{
				good1ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good));
				good2ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good));
				good3ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good2));
				praiseRank = 2;
				return ;
			}
			if(tag == 3)
			{
				good1ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good));
				good2ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good));
				good3ImageView.setImageDrawable(getResources().getDrawable(R.drawable.good));
				praiseRank = 3;
				return;
			}
		}
	};
	
	
	private OnClickListener sendOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String content = contentEditText.getText().toString();
			if(content == null||content.equals(""))
			{
				Toast.makeText(CommonRouteFeedbackActivity.this, getString(R.string.feedback_content_hint), Toast.LENGTH_SHORT).show();
				return;
			}
			try
			{
				content = URLEncoder.encode(content, "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			String loginId = TravelApplication.getInstance().getLoginID();
			String token = TravelApplication.getInstance().getToken();
			boolean result = TouristRouteMission.getInstance().routeFeedBack(loginId,token,order.getRouteId(),order.getOrderId(),praiseRank,content);
			
			if(result)
			{			
				Toast.makeText(CommonRouteFeedbackActivity.this, getString(R.string.route_feedback_success), Toast.LENGTH_LONG).show();
				finish();
			}else
			{
				Toast.makeText(CommonRouteFeedbackActivity.this, TouristRouteMission.getInstance().getResultInfo(), Toast.LENGTH_LONG).show();
			}
			
		}
	};
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}

}
