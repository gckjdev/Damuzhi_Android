/**  
        * @title DownloadManager.java  
        * @package com.damuzhi.travel.model.downlaod  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-5 下午5:31:55  
        * @version V1.0  
 */
package com.damuzhi.travel.model.downlaod;

import java.util.Map;

import android.content.Context;

import com.damuzhi.travel.db.DownloadDBHelper;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.entity.DownloadInfo;
import com.damuzhi.travel.util.FileUtil;



public class DownloadManager
{
	private Context context;
	private DownloadDBHelper fileDBHelper;
	
	private static DownloadManager instance;

    public static  DownloadManager getDownloadManager(Context context)
    {
        if (instance == null)
            instance = new DownloadManager(context);
        
        return instance;
    }
	
	
	public DownloadManager(Context context)
	{
		super();
		this.context = context;
		fileDBHelper = DownloadDBHelper.getFileDBHelper(this.context);
	}



	

	
	public void saveDownloadInfo(int cityId, String downloadURL,String savePath, String tempPath, int i, int fileSize,int downloadLength)
	{
		if(!fileDBHelper.check(downloadURL))
		{
			fileDBHelper.saveDownloadInfo(cityId, downloadURL, savePath, tempPath, 1, fileSize, downloadLength);
		}else
		{
			fileDBHelper.updateDownloadInfo(downloadURL, downloadLength);
		}
	}


	
	public void updateDownloadInfo(String downloadURL,
			Map<Integer, Integer> data)
	{
		fileDBHelper.update(downloadURL, data);
		
	}



	
	public Map<Integer, Integer> getData(String downloadURL)
	{	
		return fileDBHelper.getData(downloadURL);
	}



	
	public void deleteDownloadInfo(String downloadURL)
	{
		fileDBHelper.delete(downloadURL);
		
	}



	
	public DownloadInfo getUnfinishDownTask(String downloadURL)
	{
		return fileDBHelper.getUnfinishDownTask(downloadURL);
	}




	public static Map<Integer, Integer> getInstallCity()
	{
		
		return FileUtil.getFiles(ConstantField.DOWNLOAD_CITY_DATA_FOLDER_PATH);
	}


	
	public Map<String, DownloadInfo> getUnfinishDownload()
	{
		return fileDBHelper.getUnfinishDownload();
	}
	
	
	
}
