package com.damuzhi.travel.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;
import android.widget.Toast;

public class CheckNet extends BroadcastReceiver
{
	private static final String TAG = "CheckNet";

	/**
     * 测试ConnectivityManager ConnectivityManager主要管理和网络连接相关的操作
     * 相关的TelephonyManager则管理和手机、运营商等的相关信息；WifiManager则管理和wifi相关的信息。
     * 想访问网络状态，首先得添加权限<uses-permission
     * android:name="android.permission.ACCESS_NETWORK_STATE"/>
     * NetworkInfo类包含了对wifi和mobile两种网络模式连接的详细描述,通过其getState()方法获取的State对象则代表着
     * 连接成功与否等状态。
     *
     */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO Auto-generated method stub
		boolean available = false;
		// TODO Auto-generated method stub
          ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);;
          // 获取代表联网状态的NetWorkInfo对象
          NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
          // 获取当前的网络连接是否可用
          if (null == networkInfo)
          {
              Toast.makeText(context, "当前的网络不可用", Toast.LENGTH_SHORT).show();
              //当网络不可用时，跳转到网络设置页面
          } else
          {
               available = networkInfo.isAvailable();
              if (!available)
              {
            	  Log.d(TAG, "当前的网络不可用");
                  Toast.makeText(context, "当前的网络不可用", Toast.LENGTH_SHORT).show();
              } 
          }
          State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
          if (State.CONNECTED == state)
          {
              Log.d(TAG, "GPRS网络已连接");
              //Toast.makeText(context, "GPRS网络已连接", Toast.LENGTH_SHORT).show();
          }
          state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
          if (State.CONNECTED == state)
          {
              Log.d(TAG, "WIFI网络已连接");
              //Toast.makeText(context, "WIFI网络已连接", Toast.LENGTH_SHORT).show();
          }

          // // 跳转到无线网络设置界面
          //startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
          // // 跳转到无限wifi网络设置界面
          // startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
           //return START_STICKY;
	}
}
