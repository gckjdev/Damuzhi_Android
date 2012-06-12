/**  
        * @title Download.java  
        * @package com.damuzhi.travel.model.downlaod  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-6 上午9:36:42  
        * @version V1.0  
 */
package com.damuzhi.travel.model.downlaod;



public class DownloadBean
{
	private int cityId;
	private String downloadURL;
	private String savePath;
	private String tempPath;
	private int fileLength;
	private int downloadLength;
	private int threadId;
	private int status;//status 1.downloading 2. done 3.unzip 4.zipsuccess 5.success
	
	
	
	
	
	
	/**  
	* Constructor Method   
	* @param cityId
	* @param downloadURL
	* @param savePath
	* @param tempPath
	* @param fileLength
	* @param downloadLength
	* @param threadId
	* @param status  
	*/
	public DownloadBean(int cityId, String downloadURL, String savePath,
			String tempPath, int fileLength, int downloadLength, int threadId,
			int status)
	{
		super();
		this.cityId = cityId;
		this.downloadURL = downloadURL;
		this.savePath = savePath;
		this.tempPath = tempPath;
		this.fileLength = fileLength;
		this.downloadLength = downloadLength;
		this.threadId = threadId;
		this.status = status;
	}
	/**  
	* Constructor Method     
	*/
	public DownloadBean()
	{
		super();
	}
	
	
	public int getCityId()
	{
		return cityId;
	}
	public String getDownloadURL()
	{
		return downloadURL;
	}
	public String getSavePath()
	{
		return savePath;
	}
	public String getTempPath()
	{
		return tempPath;
	}
	public int getFileLength()
	{
		return fileLength;
	}
	public int getDownloadLength()
	{
		return downloadLength;
	}
	public int getThreadId()
	{
		return threadId;
	}
	public int getStatus()
	{
		return status;
	}
	public void setCityId(int cityId)
	{
		this.cityId = cityId;
	}
	public void setDownloadURL(String downloadURL)
	{
		this.downloadURL = downloadURL;
	}
	public void setSavePath(String savePath)
	{
		this.savePath = savePath;
	}
	public void setTempPath(String tempPath)
	{
		this.tempPath = tempPath;
	}
	public void setFileLength(int fileLength)
	{
		this.fileLength = fileLength;
	}
	public void setDownloadLength(int downloadLength)
	{
		this.downloadLength = downloadLength;
	}
	public void setThreadId(int threadId)
	{
		this.threadId = threadId;
	}
	public void setStatus(int status)
	{
		this.status = status;
	}
	
	
}
