package com.damuzhi.travel.model.entity;

public class DownloadInfos {

	private int cityId;
	private String url;//
	private long fileLength;//
	private long downloadLength;
	private boolean notFinish;
	private boolean upzipResult;
	/**  
	* Constructor Method   
	* @param cityId
	* @param url
	* @param fileLength
	* @param downloadLength
	* @param notFinish
	* @param upzipResult  
	*/
	public DownloadInfos(int cityId, String url, long fileLength,
			long downloadLength, boolean notFinish, boolean upzipResult)
	{
		super();
		this.cityId = cityId;
		this.url = url;
		this.fileLength = fileLength;
		this.downloadLength = downloadLength;
		this.notFinish = notFinish;
		this.upzipResult = upzipResult;
	}
	public int getCityId()
	{
		return cityId;
	}
	public String getUrl()
	{
		return url;
	}
	public long getFileLength()
	{
		return fileLength;
	}
	public long getDownloadLength()
	{
		return downloadLength;
	}
	public boolean isNotFinish()
	{
		return notFinish;
	}
	public boolean isUpzipResult()
	{
		return upzipResult;
	}
	public void setCityId(int cityId)
	{
		this.cityId = cityId;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	public void setFileLength(long fileLength)
	{
		this.fileLength = fileLength;
	}
	public void setDownloadLength(long downloadLength)
	{
		this.downloadLength = downloadLength;
	}
	public void setNotFinish(boolean notFinish)
	{
		this.notFinish = notFinish;
	}
	public void setUpzipResult(boolean upzipResult)
	{
		this.upzipResult = upzipResult;
	}
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	


}
