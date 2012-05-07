package com.damuzhi.travel.download;
import com.damuzhi.travel.download.IDownloadCallback;

interface IDownloadService{
 boolean addTask(in String strKey,in String strURL,in String strSavePath);  
    /** 
     *use this function to set how many tasks can download at the same time 
     *if you don't use the function .the default is 3 
          */  
          
    void setMaxTaskCount(in int count);  
    void pauseTask(in String strKey);  
    void cancelTask(in String strkey);  
    void regCallback(in IDownloadCallback cb);  
    void unregCallback(in IDownloadCallback cb);  
}