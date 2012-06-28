/**  
        * @title DownloadDataListAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.download  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-12 上午10:07:09  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.adapter.download;

import java.util.List;
import java.util.Map;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.more.OpenCityDataActivity;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.TravelUtil;

import android.R.raw;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
		if(convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.open_download_city_list_item, null);
		}
		if(installedCityList.size()>0)
		{
			convertView.findViewById(R.id.delete_group).setVisibility(View.VISIBLE);
			int cityId = installedCityList.get(position);
			City city = AppManager.getInstance().getCityByCityId(cityId);
			Button deleteButton = (Button) convertView.findViewById(R.id.delete_button);
			TextView dataCityName = (TextView) convertView.findViewById(R.id.data_city_name);
			TextView dataCitySize = (TextView) convertView.findViewById(R.id.data_size);
			deleteButton.setTag(position);
			dataCityName.setText(city.getCountryName()+"."+city.getCityName());
			dataCitySize.setText(TravelUtil.getDataSize(city.getDataSize()));
			deleteButton.setOnClickListener(deleteOnClickListener);
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
			int position = (Integer) v.getTag();
			int cityId = installedCityList.get(position);
			City city = AppManager.getInstance().getCityByCityId(cityId);
			String zipFilePath = String.format(ConstantField.DOWNLOAD_TEMP_PATH, cityId)+HttpTool.getFileName(HttpTool.getConnection(city.getDownloadURL()), city.getDownloadURL());
			String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);
			/*FileUtil.deleteFolder(zipFilePath);
			FileUtil.deleteFolder(upZipFilePath);*/
			deleteFile(zipFilePath,upZipFilePath);
			installedCityList.remove(position);
			OpenCityDataActivity.downloadDataListAdapter.setInstalledCityList(installedCityList);
			OpenCityDataActivity.downloadDataListAdapter.notifyDataSetChanged();
			OpenCityDataActivity.installCityData.remove(position);
		}
	};


	private void deleteFile(String zipFilePath, String folderPath)
	{
		String[] params = new String[]{zipFilePath,folderPath};
		
		AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>()
		{

			@Override
			protected Void doInBackground(String... params)
			{
				String zipFilePath = params[0];
				String upZipFilePath = params[1];				
				FileUtil.deleteFolder(zipFilePath);
				FileUtil.deleteFolder(upZipFilePath);
				return null;
			}

			
		};
		task.execute(params);
	}
	
	
}
