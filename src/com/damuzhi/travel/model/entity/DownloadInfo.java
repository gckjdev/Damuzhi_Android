package com.damuzhi.travel.model.entity;

import android.R.integer;

public class DownloadInfo {

//	public long threadId;// 下载器id
//	public long startPos;// 开始点
//	public long endPos;// 结束点
//	public long compeleteSize;// 完成度
	public String url;// 下载器网络标识
	public long currentPosition;//
	public long totalBytes;//已下载文件大小
	public long speed;//下载速度
	/**
	 * @param url
	 * @param currentPosition
	 * @param totalBytes
	 * @param speed
	 */
	public DownloadInfo(String url, long currentPosition, long totalBytes,
			long speed)
	{
		super();
		this.url = url;
		this.currentPosition = currentPosition;
		this.totalBytes = totalBytes;
		this.speed = speed;
	}
	
	
	
}
