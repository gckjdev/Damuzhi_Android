package com.damuzhi.travel.download;
import com.damuzhi.travel.download.IDownloadCallback;

interface IDownloadService{
 boolean startDownload(in int cityId,in String downloadURL,in String downloadSavePath,String tempPath);  
    /** 
     *use this function to set how many tasks can download at the same time 
     *if you don't use the function .the default is 3 
          */  
          
    void setMaxTaskCount(in int count);  
    void pauseDownload(in String downloadURL);  
    void cancelDownload(in String downloadURL);  
    void restartDownload(in String downloadURL);
    void regCallback(in IDownloadCallback cb);  
    void unregCallback(in IDownloadCallback cb);  
}