/**  
        * @title LocationReceiver.java  
        * @package com.damuzhi.travel.util  
        * @description   
        * @author liuxiaokun  
        * @update 2012-7-9 上午11:51:51  
        * @version V1.0  
 */
package com.damuzhi.travel.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.commonsware.cwac.locpoll.LocationPoller;
import com.damuzhi.travel.model.constant.ConstantField;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-7-9 上午11:51:51  
 */

public class LocationReceiver extends BroadcastReceiver
{

	private static final String TAG = "LocationReceiver";

	@Override
	public void onReceive(Context context, Intent intent)
	{
	//	Toast.makeText(context, "location",Toast.LENGTH_LONG).show();
		File log= new File(ConstantField.LOCATION_FILE,"LocationLog.txt");
	    try {
	      BufferedWriter out=new BufferedWriter(new FileWriter(log.getAbsolutePath(),log.exists()));
	      out.write(new Date().toString());
	      out.write(" : ");     
	      Bundle b=intent.getExtras();
	      Location loc=(Location)b.get(LocationPoller.EXTRA_LOCATION);
	      String msg;
	      if (loc==null) {
	        loc=(Location)b.get(LocationPoller.EXTRA_LASTKNOWN);
	        if (loc==null) {
	          msg=intent.getStringExtra(LocationPoller.EXTRA_ERROR);
	        }
	        else {
	          msg="TIMEOUT, lastKnown="+loc.toString();
	        }
	      }
	      else {
	        msg=loc.toString();
	      }
	      Log.i(TAG, "loaction msg = "+msg);
	      if (msg==null) {
	        msg="Invalid broadcast received!";
	      }
	      out.write(msg);
	      out.write("\n");
	      out.close();
	    }
	    catch (IOException e) {
	      Log.e(getClass().getName(), "Exception appending to log file", e);
	    }
			  

	}

}
