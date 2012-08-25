/**  
        * @title DownloadDataListAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.download  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-12 上午10:07:09  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.adapter.download;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.FilterAdapter.ViewHolder;
import com.damuzhi.travel.activity.favorite.FavoriteActivity;
import com.damuzhi.travel.activity.more.OpenCityActivity;
import com.damuzhi.travel.db.DownloadPreference;
import com.damuzhi.travel.mission.favorite.FavoriteMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.TravelUtil;

import android.R.raw;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-12 上午10:07:09  
 */

public class DownloadDataListAdapter extends BaseAdapter
{
	private List<Integer> installedCityList;
	private Context context;
	
	
	
	/**  
	* Constructor Method   
	* @param installedCityList
	* @param context  
	*/
	public DownloadDataListAdapter(List<Integer> installedCityList,
			Context context)
	{
		super();
		this.installedCityList = installedCityList;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return installedCityList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return installedCityList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder ;
		if(convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.open_download_city_list_item, null);
			holder = new ViewHolder();
			holder.delete = (Button) convertView.findViewById(R.id.delete_button);
			holder.dataCityName = (TextView) convertView.findViewById(R.id.data_city_name);
			holder.dataCitySize = (TextView) convertView.findViewById(R.id.data_size);
			convertView.setTag(holder);
		}else {
			 holder = (ViewHolder) convertView.getTag();  
		}
		if(installedCityList.size()>0)
		{
			convertView.findViewById(R.id.installed_data_group).setVisibility(View.VISIBLE);
			int cityId = installedCityList.get(position);
			City city = AppManager.getInstance().getCityByCityId(cityId);
			String dataName = city.getCountryName()+"."+city.getCityName();
			holder.delete.setTag(position);
			holder.dataCityName.setText(dataName);
			holder.dataCitySize.setText(TravelUtil.getDataSize(city.getDataSize()));
			holder.delete.setOnClickListener(deleteOnClickListener);
		}else {
			convertView.findViewById(R.id.open_city_tips_download).setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}

	public List<Integer> getInstalledCityList()
	{
		return installedCityList;
	}

	public void setInstalledCityList(List<Integer> installedCityList)
	{
		this.installedCityList = installedCityList;
	}

	
	private OnClickListener deleteOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			
			final int position = (Integer) v.getTag();
			AlertDialog deleteAlertDialog = new AlertDialog.Builder(context).create();
			deleteAlertDialog.setMessage(context.getString(R.string.delete_download_data));
			deleteAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,context.getString(R.string.ok),new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{					
					int cityId = installedCityList.get(position);
					City city = AppManager.getInstance().getCityByCityId(cityId);
					//String zipFilePath = String.format(ConstantField.DOWNLOAD_TEMP_PATH, cityId)+HttpTool.getFileName(HttpTool.getConnection(city.getDownloadURL()), city.getDownloadURL());
					String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);			
					//String gcZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId+"gc");
					//File upZipFile = new File(upZipFilePath);
					installedCityList.remove(position);
					/*OpenCityActivity.installCityData.remove(cityId);
					OpenCityActivity.downloadDataListAdapter.setInstalledCityList(installedCityList);
					OpenCityActivity.downloadDataListAdapter.notifyDataSetChanged();*/
					DownloadPreference.deleteDownloadInfo(context, Integer.toString(cityId));
					deleteFile(upZipFilePath);
					//File gcZipFile = new File(gcZipFilePath);
					//boolean reulst = upZipFile.renameTo(gcZipFile);
					/*if(reulst)
					{
						installedCityList.remove(position);
						OpenCityActivity.installCityData.remove(cityId);
						OpenCityActivity.downloadDataListAdapter.setInstalledCityList(installedCityList);
						OpenCityActivity.downloadDataListAdapter.notifyDataSetChanged();
						DownloadPreference.deleteDownloadInfo(context, Integer.toString(cityId));
						deleteFile(gcZipFilePath);
					}	*/	
					
				}	
			} );
			deleteAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,""+context.getString(R.string.cancel),new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.cancel();
					
				}
			} );
			deleteAlertDialog.show();
			
			
			
			
			
			
				
			
		}
	};


	private void deleteFile( String folderPath)
	{
		String[] params = new String[]{folderPath};
		
		AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>()
		{

			@Override
			protected Void doInBackground(String... params)
			{
				//String zipFilePath = params[0];
				String gcZipFilePath = params[0];				
				//FileUtil.deleteFolder(zipFilePath);
				FileUtil.deleteFolder(gcZipFilePath);
				return null;
			}

			
		};
		task.execute(params);
	}
	
	public final class ViewHolder {  
        public Button delete;
        public TextView dataCityName;
        public TextView dataCitySize;
    }
	
	
	
}
