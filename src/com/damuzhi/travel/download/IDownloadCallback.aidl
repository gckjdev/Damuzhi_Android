package com.damuzhi.travel.download;
interface IDownloadCallback{
	void onTaskStatusChanged(in String downloadURL, in int status);
	void onTaskProcessStatusChanged(in int cityId,in String downloadURL,in long speed ,in long totalBytes,in long curPos,in boolean notFinish);

}