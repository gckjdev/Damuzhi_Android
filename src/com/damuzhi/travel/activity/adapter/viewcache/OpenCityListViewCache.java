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
import android.widget.Button;
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
	private TextView titleTextView;
	private TextView dataCityName ;
	private ImageView dataSelectIcon ;
	private ViewGroup listViewItemGroup ;
	private TextView dataSize ;
	private ViewGroup buttonGroup;
	private ViewGroup installGroup;
	private TextView installedTextView;
	private TextView installingTextView;
	private ViewGroup dataDownloadMangerGroup;
/*	private ImageButton restartDownloadBtn;
	private ImageButton stopDownloadBtn;
	private ImageButton onlineButton;				
	//private ImageButton startButton;
	private Button startButton;
	private ImageButton cancelButton ;
	private ImageButton installButton;
	private ImageButton cancelInstallButton;*/
/*	private ViewGroup startGroup ;
	private ViewGroup cancelGroup;
	private ViewGroup restartGroup ;
	private ViewGroup pauseGroup;*/ 
	
	private Button restartDownloadBtn;
	private Button stopDownloadBtn;
	private Button onlineButton;				
	//private ImageButton startButton;
	private Button startButton;
	private Button cancelButton ;
	private Button installButton;
	private Button cancelInstallButton;
	private ProgressBar downloadBar;
	private TextView resultTextView;

	
	public OpenCityListViewCache(View convertView)
	{
		super();
		this.convertView = convertView;
	}



	public TextView getTitleTextView()
	{
		if(titleTextView == null)
		{
			titleTextView = (TextView) convertView.findViewById(R.id.item_group_title);
		}
		return titleTextView;
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

	
	public ViewGroup getInstallGroup()
	{
		if(installGroup == null)
		{
			installGroup = (ViewGroup) convertView.findViewById(R.id.install_group);
		}
		return installGroup;
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





	public Button getRestartDownloadBtn()
	{
		if(restartDownloadBtn == null)
		{
			restartDownloadBtn = (Button) convertView.findViewById(R.id.restart_download_button);
		}
		return restartDownloadBtn;
	}





	public Button getStopDownloadBtn()
	{
		if(stopDownloadBtn == null)
		{
			stopDownloadBtn = (Button) convertView.findViewById(R.id.stop_download_button);
		}
		return stopDownloadBtn;
	}





	public Button getOnlineButton()
	{
		if(onlineButton == null)
		{
			onlineButton = (Button) convertView.findViewById(R.id.online_button);	
		}
		return onlineButton;
	}





	/*public ImageButton getStartButton()
	{
		if(startButton == null)
		{
			 startButton = (ImageButton) convertView.findViewById(R.id.start_download_button);
		}
		return startButton;
	}*/
	
	public Button getStartButton()
	{
		if(startButton == null)
		{
			 startButton = (Button) convertView.findViewById(R.id.start_download_button);
		}
		return startButton;
	}

	public Button getInstallButton()
	{
		if(installButton == null)
		{
			installButton = (Button) convertView.findViewById(R.id.install_button);
		}
		return installButton;
	}



	public Button getCancelButton()
	{
		if(cancelButton == null)
		{
			cancelButton = (Button) convertView.findViewById(R.id.cancel_download_button);
		}
		return cancelButton;
	}

	public Button getCancelInstallButton()
	{
		if(cancelInstallButton == null)
		{
			cancelInstallButton = (Button) convertView.findViewById(R.id.cancel_install_button);
		}
		return cancelInstallButton;
	}



	/*public ViewGroup getStartGroup()
	{
		if(startGroup == null)
		{
			startGroup = (ViewGroup)convertView.findViewById(R.id.start_download_manager_group);
		}
		return startGroup;
	}

	
	public ViewGroup getRestartGroup()
	{
		if(restartGroup == null)
		{
			restartGroup = (ViewGroup)convertView.findViewById(R.id.restart_download_manager_group);
		}
		return restartGroup;
	}

	
	public ViewGroup getPauseGroup()
	{
		if(pauseGroup == null)
		{
			pauseGroup = (ViewGroup)convertView.findViewById(R.id.stop_download_manager_group);
		}
		return pauseGroup;
	}
	
	

	public ViewGroup getCancelGroup()
	{
		if(cancelGroup == null)
		{
			cancelGroup = (ViewGroup)convertView.findViewById(R.id.cancel_download_manager_group);
		}
		return cancelGroup;
	}*/





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
