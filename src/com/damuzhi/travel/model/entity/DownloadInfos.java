package com.damuzhi.travel.model.entity;

public class DownloadInfos {

	private int cityId;
	private  String url;//
	private  long speed;	
	private  long totalBytes;//
	private  long currentPosition;
	private boolean notFinish;
	
	
	
	/**  
	* Constructor Method   
	* @param cityId
	* @param url
	* @param speed
	* @param totalBytes
	* @param currentPosition  
	*//*
	public DownloadInfos(int cityId, String url, long speed, long totalBytes,
			long currentPosition)
	{
		super();
		this.cityId = cityId;
		this.url = url;
		this.speed = speed;
		this.totalBytes = totalBytes;
		this.currentPosition = currentPosition;
	}*/
	/**  
	* Constructor Method   
	* @param url
	* @param speed
	* @param totalBytes
	* @param currentPosition  
	*//*
	public DownloadInfos(String url, long speed, long totalBytes,
			long currentPosition)
	{
		super();
		this.url = url;
		this.speed = speed;
		this.totalBytes = totalBytes;
		this.currentPosition = currentPosition;
	}*/
	public String getUrl()
	{
		return url;
	}
	/**  
	* Constructor Method   
	* @param cityId
	* @param url
	* @param speed
	* @param totalBytes
	* @param currentPosition
	* @param notFinish  
	*/
	public DownloadInfos(int cityId, String url, long speed, long totalBytes,
			long currentPosition, boolean notFinish)
	{
		super();
		this.cityId = cityId;
		this.url = url;
		this.speed = speed;
		this.totalBytes = totalBytes;
		this.currentPosition = currentPosition;
		this.notFinish = notFinish;
	}
	
	
	public long getSpeed()
	{
		return speed;
	}
	public long getTotalBytes()
	{
		return totalBytes;
	}
	public long getCurrentPosition()
	{
		return currentPosition;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	public void setSpeed(long speed)
	{
		this.speed = speed;
	}
	public void setTotalBytes(long totalBytes)
	{
		this.totalBytes = totalBytes;
	}
	public void setCurrentPosition(long currentPosition)
	{
		this.currentPosition = currentPosition;
	}
	public int getCityId()
	{
		return cityId;
	}
	public void setCityId(int cityId)
	{
		this.cityId = cityId;
	}
	public boolean isNotFinish()
	{
		return notFinish;
	}
	public void setNotFinish(boolean notFinish)
	{
		this.notFinish = notFinish;
	}
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	


}
