/**  
        * @title OpenCityUpdateViewcache.java  
        * @package com.damuzhi.travel.activity.adapter.viewcache  
        * @description   
        * @author liuxiaokun  
        * @update 2012-8-9 上午11:58:42  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.adapter.viewcache;

import com.damuzhi.travel.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-8-9 上午11:58:42  
 */

public class OpenCityDownloadViewcache
{
	private View convertView;
	private TextView dataCityName ;
	private ImageView dataSelectIcon ;
	private ViewGroup installedDataGroup ;
	private ViewGroup updateStatusGroup;
	private TextView dataSize ;
	private ViewGroup buttonGroup;
	private TextView updatedTextView;
	private TextView updatingTextView;	
	private ImageButton restartUpdateBtn;
	private ImageButton stopUpdateBtn;			
	private ImageButton updateButton;
	private ImageButton cancelUpdateButton ;
	private Button deleteButton;
	private ViewGroup startUpdateGroup ;
	private ViewGroup restartUpdateGroup;
	private ViewGroup stopUpdateGroup;
	private ViewGroup cancelUpdateGroup;
	private ProgressBar updateBar ;
	private TextView updateTextView;

	
	public OpenCityDownloadViewcache(View convertView)
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





	public ViewGroup getInstalledDataGroup()
	{
		if(installedDataGroup == null)
		{
			installedDataGroup = (ViewGroup) convertView.findViewById(R.id.installed_data_group);
		}
		return installedDataGroup;
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





	public TextView getUpdatedTextView()
	{
		if(updatedTextView == null)
		{
			updatedTextView = (TextView) convertView.findViewById(R.id.updated);
		}
		return updatedTextView;
	}





	public TextView getUpdatingTextView()
	{
		if(updatingTextView == null)
		{
			updatingTextView = (TextView) convertView.findViewById(R.id.updating);
		}
		return updatingTextView;
	}





	public ViewGroup getUpdateStatusGroup()
	{
		if(updateStatusGroup == null)
		{
			updateStatusGroup = (ViewGroup) convertView.findViewById(R.id.update_status_group);
		}
		return updateStatusGroup;
	}



	public Button getDeleteBtn()
	{
		if(deleteButton == null)
		{
			deleteButton = (Button) convertView.findViewById(R.id.delete_button);
		}
		return deleteButton;
	}

	public ImageButton getRestartUpdateBtn()
	{
		if(restartUpdateBtn == null)
		{
			restartUpdateBtn = (ImageButton) convertView.findViewById(R.id.restart_update_download_btn);
		}
		return restartUpdateBtn;
	}





	public ImageButton getStopUpdateBtn()
	{
		if(stopUpdateBtn == null)
		{
			stopUpdateBtn = (ImageButton) convertView.findViewById(R.id.stop_update_download_btn);
		}
		return stopUpdateBtn;
	}





	





	public ImageButton getUpdateButton()
	{
		if(updateButton == null)
		{
			updateButton = (ImageButton) convertView.findViewById(R.id.update_downlaod_button);
		}
		return updateButton;
	}

	public ImageButton getCancelUpdateButton()
	{
		if(cancelUpdateButton == null)
		{
			cancelUpdateButton = (ImageButton) convertView.findViewById(R.id.cancel_update_download_button);
		}
		return cancelUpdateButton;
	}




	





	public ViewGroup getUpdateGroup()
	{
		if(startUpdateGroup == null)
		{
			startUpdateGroup = (ViewGroup)convertView.findViewById(R.id.update_download_manager_group);
		}
		return startUpdateGroup;
	}





	public ViewGroup getCancelGroup()
	{
		if(cancelUpdateGroup == null)
		{
			cancelUpdateGroup = (ViewGroup)convertView.findViewById(R.id.cancel_update_download_manager_group);
		}
		return cancelUpdateGroup;
	}


	
	
	public ViewGroup getStopUpdateGroup()
	{
		if(stopUpdateGroup == null)
		{
			stopUpdateGroup = (ViewGroup)convertView.findViewById(R.id.stop_update_download_manager_group);
		}
		return stopUpdateGroup;
	}





	public ViewGroup getRestartGroup()
	{
		if(restartUpdateGroup == null)
		{
			restartUpdateGroup = (ViewGroup)convertView.findViewById(R.id.restart_update_download_manager_group);
		}
		return restartUpdateGroup;
	}
	
	
	
	
	



	public ProgressBar getUpdateBar()
	{
		if(updateBar == null)
		{
			updateBar = (ProgressBar) convertView.findViewById(R.id.update_bar);
		}
		return updateBar;
	}





	public TextView getUpdateTextView()
	{
		if(updateTextView == null)
		{
			updateTextView = (TextView) convertView.findViewById(R.id.update_persent);
		}
		return updateTextView;
	}
	
}
