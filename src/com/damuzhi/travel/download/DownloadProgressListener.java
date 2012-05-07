package com.damuzhi.travel.download;

public interface DownloadProgressListener {
	public void onDownloadSize(String strKey,long size,long fileLength);
}
