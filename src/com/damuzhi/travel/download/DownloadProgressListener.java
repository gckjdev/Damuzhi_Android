package com.damuzhi.travel.download;

public interface DownloadProgressListener {
	public void onDownloadSize(int cityId,String downloadURL,long downloadSpeed,long downloadLength,long fileLength);
}
