package com.damuzhi.travel.download;
interface IDownloadCallback{
	void onTaskStatusChanged(in String strkey, in int status);
	void onTaskProcessStatusChanged(in String strkey,in long speed ,in long totalBytes,in long curPos);

}