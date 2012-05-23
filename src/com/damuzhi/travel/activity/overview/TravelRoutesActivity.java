/**  
        * @title TravelRouteActivity.java  
        * @package com.damuzhi.travel.activity.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-23 下午1:33:19  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.overview;

import java.util.List;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.overview.TravelTipsAdapter;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.PlaceActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.service.Task;
import com.damuzhi.travel.util.CornerListView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-23 下午1:33:19  
 */

public class TravelRoutesActivity extends MenuActivity implements PlaceActivity
{

	private ListView listView;
	private List<CommonTravelTip> commonTravelTips;
	private int loadFlag = 1;//判断是否从新加载activity
	private Dialog loadingDialog;
	private static final int LOADING = 0;
	private static final int LOAD_OK = 1;
	private static final String TAG = "TravelTipsActivity";
	private TravelApplication application;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.travel_tips);
		MainService.allActivity.add(this);
		application = TravelApplication.getInstance();
		loadFlag = 0;
		init();
	}
	
	
	
	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		listView = (CornerListView) findViewById(R.id.travel_tips_listview);
		listView.setOnItemClickListener(clickListener);
	}

	public void initData()
	{
		TravelTipsAdapter adapter = new TravelTipsAdapter(commonTravelTips, this);
		listView.setAdapter(adapter);
	}
	
	@Override
	public void placeInfo()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Object...param)
	{
		// TODO Auto-generated method stub
		commonTravelTips = (List<CommonTravelTip>) param[0];
		initData();
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
		super.onResume();
		if(loadFlag == 0)
		{			
			loadFlag = 1;
			showRoundProcessDialog(TravelRoutesActivity.this, R.layout.loading_process_dialog_anim);
			Task hotelTask = new Task(Task.TRAVEL_TIPS,TravelRoutesActivity.this);
			MainService.newTask(hotelTask);
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
	            		Intent intent = new Intent(TravelRoutesActivity.this, IndexActivity.class);
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
	
	 private OnItemClickListener clickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			// TODO Auto-generated method stub
			 CommonTravelTip commonTravelTip = commonTravelTips.get(arg2);
			 application.setCommonTravelTip(commonTravelTip);
			 Intent intent = new Intent();
			 intent.setClass(TravelRoutesActivity.this, TravelTipsDetailActivity.class);
			 startActivity(intent);
		}
	};

}
