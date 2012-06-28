/**  
        * @title OpenCityList.java  
        * @package com.damuzhi.travel.activity.adapter.viewcache  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-28 上午9:43:41  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.adapter.viewcache;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.damuzhi.travel.R;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-28 上午9:43:41  
 */

public class OpenCityListViewCache
{
	private View convertView;
	private TextView dataCityName ;
	private ImageView dataSelectIcon ;
	private ViewGroup listViewItemGroup ;
	private TextView dataSize ;
	private ViewGroup buttonGroup;
	private TextView installedTextView;
	private TextView installingTextView;
	private ViewGroup dataDownloadMangerGroup;
	private ImageView restartDownloadBtn;
	private ImageView stopDownloadBtn;
	private ImageButton onlineButton;				
	private ImageButton startButton;
	private ImageButton cancelButton ;
	private ViewGroup startGroup ;
	private ViewGroup cancelGroup;
	private ProgressBar downloadBar ;
	private TextView resultTextView;

	
	public OpenCityListViewCache(View convertView)
	{
		super();
		this.convertView = convertView;
	}





	public TextView getDataCityName()
	{
		if(dataCityName == null)
		{
			dataCityName = (TextView) convertView.findViewById(R.id.data_city_name);
		}
		return dataCityName;
	}





	public ImageView getDataSelectIcon()
	{
		if(dataSelectIcon == null)
		{
			dataSelectIcon = (ImageView) convertView.findViewById(R.id.data_staus);
		}
		return dataSelectIcon;
	}





	public ViewGroup getListViewItemGroup()
	{
		if(listViewItemGroup == null)
		{
			listViewItemGroup = (ViewGroup) convertView.findViewById(R.id.listview_item_group);
		}
		return listViewItemGroup;
	}





	public TextView getDataSize()
	{
		if(dataSize == null)
		{
			dataSize = (TextView) convertView.findViewById(R.id.data_size);
		}
		return dataSize;
	}





	public ViewGroup getButtonGroup()
	{
		if(buttonGroup == null)
		{
			buttonGroup = (ViewGroup) convertView.findViewById(R.id.button_group);
		}
		return buttonGroup;
	}





	public TextView getInstalledTextView()
	{
		if(installedTextView == null)
		{
			installedTextView = (TextView) convertView.findViewById(R.id.installed);
		}
		return installedTextView;
	}





	public TextView getInstallingTextView()
	{
		if(installingTextView == null)
		{
			installingTextView = (TextView) convertView.findViewById(R.id.installing);
		}
		return installingTextView;
	}





	public ViewGroup getDataDownloadMangerGroup()
	{
		if(dataDownloadMangerGroup == null)
		{
			dataDownloadMangerGroup = (ViewGroup) convertView.findViewById(R.id.download_status_group);
		}
		return dataDownloadMangerGroup;
	}





	public ImageView getRestartDownloadBtn()
	{
		if(restartDownloadBtn == null)
		{
			restartDownloadBtn = (ImageView) convertView.findViewById(R.id.restart_download_btn);
		}
		return restartDownloadBtn;
	}





	public ImageView getStopDownloadBtn()
	{
		if(stopDownloadBtn == null)
		{
			stopDownloadBtn = (ImageView) convertView.findViewById(R.id.stop_download_btn);
		}
		return stopDownloadBtn;
	}





	public ImageButton getOnlineButton()
	{
		if(onlineButton == null)
		{
			onlineButton = (ImageButton) convertView.findViewById(R.id.online_button);	
		}
		return onlineButton;
	}





	public ImageButton getStartButton()
	{
		if(startButton == null)
		{
			 startButton = (ImageButton) convertView.findViewById(R.id.start_download_button);
		}
		return startButton;
	}





	public ImageButton getCancelButton()
	{
		if(cancelButton == null)
		{
			cancelButton = (ImageButton) convertView.findViewById(R.id.cancel_download_button);
		}
		return cancelButton;
	}





	public ViewGroup getStartGroup()
	{
		if(startGroup == null)
		{
			startGroup = (ViewGroup)convertView.findViewById(R.id.start_download_manager_group);
		}
		return startGroup;
	}





	public ViewGroup getCancelGroup()
	{
		if(cancelGroup == null)
		{
			cancelGroup = (ViewGroup)convertView.findViewById(R.id.cancel_download_manager_group);
		}
		return cancelGroup;
	}





	public ProgressBar getDownloadBar()
	{
		if(downloadBar == null)
		{
			downloadBar = (ProgressBar) convertView.findViewById(R.id.downloadbar);
		}
		return downloadBar;
	}





	public TextView getResultTextView()
	{
		if(resultTextView == null)
		{
			resultTextView = (TextView) convertView.findViewById(R.id.download_persent);
		}
		return resultTextView;
	}
	
	

	
	
}
