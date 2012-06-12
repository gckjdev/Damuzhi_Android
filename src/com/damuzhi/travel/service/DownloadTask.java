/**  
        * @title DownloadTask.java  
        * @package com.damuzhi.travel.service  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-8 下午3:16:02  
        * @version V1.0  
 */
package com.damuzhi.travel.service;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-8 下午3:16:02  
 */

public class DownloadTask
{

	private String downloadURL;
	private int status;
	
	
	
	/**  
	* Constructor Method   
	* @param downloadURL
	* @param status  
	*/
	public DownloadTask(String downloadURL, int status)
	{
		super();
		this.downloadURL = downloadURL;
		this.status = status;
	}
	
	
	public String getDownloadURL()
	{
		return downloadURL;
	}
	public int getStatus()
	{
		return status;
	}
	public void setDownloadURL(String downloadURL)
	{
		this.downloadURL = downloadURL;
	}
	public void setStatus(int status)
	{
		this.status = status;
	}
}
