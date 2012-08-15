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

import com.damuzhi.travel.db.DBOpenHelper;
import com.damuzhi.travel.db.FileDBHelper;
import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.util.FileUtil;



public class DownloadManager
{
	private Context context;
	private FileDBHelper fileDBHelper;
	
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
		fileDBHelper = FileDBHelper.getFileDBHelper(this.context);
		//fileDBHelper = new FileDBHelper(this.context);
	}



	/*public void saveDownloadInfo(int cityId, String downloadURL,String savePath, String tempPath, int i, int fileSize,Map<Integer, Integer> data)
	{
		if(!fileDBHelper.check(downloadURL))
		{
			fileDBHelper.save(cityId,downloadURL,savePath,tempPath,1,fileSize, data);
		}else
		{
			fileDBHelper.update(downloadURL, data);
		}
		fileDBHelper.save(cityId,downloadURL,savePath,tempPath,1,fileSize, data);
	}*/

	
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



	
	public DownloadBean getUnfinishDownTask(String downloadURL)
	{
		return fileDBHelper.getUnfinishDownTask(downloadURL);
	}




	public static Map<Integer, Integer> getInstallCity()
	{
		
		return FileUtil.getFiles(ConstantField.DOWNLOAD_CITY_DATA_FOLDER_PATH);
	}
	
	
	
}
