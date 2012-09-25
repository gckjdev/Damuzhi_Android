package com.damuzhi.travel.db;

import java.util.HashMap;
import java.util.Map;

import com.damuzhi.travel.model.downlaod.DownloadBean;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



public class FileDBHelper {
	private static final String TAG = "FileDBHelper";
	private DBOpenHelper openHelper;
	private static FileDBHelper instance;

    public static  FileDBHelper getFileDBHelper(Context context)
    {
        if (instance == null){
            instance = new FileDBHelper(context);
        }
        return instance;
    }
	
	
	public FileDBHelper(Context context) {
		openHelper = DBOpenHelper.getHelper(context);
		//openHelper = new DBOpenHelper(context);
	}
	
	
	
	public Map<Integer, Integer> getData(String path){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select threadid, downlength from FileDownloadLog where downloadurl=? ", new String[]{path});
		Map<Integer, Integer> data = new HashMap<Integer, Integer>();
		try
		{		
			while(cursor.moveToNext()){
				data.put(cursor.getInt(0), cursor.getInt(1));
			}
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}		
			//db.close();
		}
		
		return data;
	}
	
	
	public DownloadBean getUnfinishDownTask(String downloadURL)
	{
		DownloadBean downloadBean = null;
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select downloadurl, sum(downlength) downloadLength,filelength from FileDownloadLog where downloadurl = ? group by  downloadurl ", new String[]{downloadURL});
		try
		{		
			while(cursor.moveToNext()){	
				downloadBean = new DownloadBean();
				downloadBean.setDownloadURL(cursor.getString(0));
				downloadBean.setDownloadLength(cursor.getInt(1));
				downloadBean.setFileLength(cursor.getInt(2));
			}
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
			//db.close();
		}
		
		return downloadBean;
	}
	
	public void save(int cityid, String downloadURL, String savePath,String tempPath, int status,int filelength,Map<Integer, Integer> map){//int threadid, int position
		SQLiteDatabase db = openHelper.getWritableDatabase();
		while (openHelper.getWritableDatabase().isDbLockedByOtherThreads()) {   
            try {  
                Thread.sleep(10);  
            } catch (InterruptedException e) {  
               Log.e(TAG, "<save> but catch exception :"+e.toString(),e);  
            }
		}
		db.beginTransaction();
		try{
			for(Map.Entry<Integer, Integer> entry : map.entrySet()){
				db.execSQL("insert into FileDownloadLog(cityid,downloadurl, savepath,temppath,status,filelength,threadid, downlength) values(?,?,?,?,?,?,?,?)",
						new Object[]{cityid,downloadURL,savePath,tempPath,status,filelength, entry.getKey(), entry.getValue()});
			}
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
			//db.close();
		}
		
	}
	
	
	public void saveDownloadInfo(int cityid, String downloadURL, String savePath,String tempPath, int status,int filelength,int downloadLength){//int threadid, int position
		SQLiteDatabase db = openHelper.getWritableDatabase();
		while (openHelper.getWritableDatabase().isDbLockedByOtherThreads()) {   
            try {  
                Thread.sleep(10);  
            } catch (InterruptedException e) {  
               Log.e(TAG, "<save> but catch exception :"+e.toString(),e);  
            }
		}
		db.beginTransaction();
		try{
			
				db.execSQL("insert into FileDownloadLog(cityid,downloadurl, savepath,temppath,status,filelength,threadid, downlength) values(?,?,?,?,?,?,?,?)",
						new Object[]{cityid,downloadURL,savePath,tempPath,status,filelength, 0,downloadLength });
	
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
			//db.close();
		}
		
	}
	
	public void update(String downloadURL, Map<Integer, Integer> map){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		while (openHelper.getWritableDatabase().isDbLockedByOtherThreads()) {   
            try {  
                Thread.sleep(10);  
            } catch (InterruptedException e) {  
            	 Log.e(TAG, "<save> but catch exception :"+e.toString(),e);  
            }
		}
		db.beginTransaction();
		try{
			for(Map.Entry<Integer, Integer> entry : map.entrySet()){
				db.execSQL("update FileDownloadLog set downlength=? where downloadurl=? and threadid=?",new Object[]{entry.getValue(), downloadURL, entry.getKey()});
			}
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
			//db.close();
		}
		
	}
	
	
	public void updateDownloadInfo(String downloadURL,int downloadLength ){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		while (openHelper.getWritableDatabase().isDbLockedByOtherThreads()) {   
            try {  
                Thread.sleep(10);  
            } catch (InterruptedException e) {  
            	 Log.e(TAG, "<save> but catch exception :"+e.toString(),e);  
            }
		}
		db.beginTransaction();
		try{
			
				db.execSQL("update FileDownloadLog set downlength=? where downloadurl=? and threadid=?",new Object[]{downloadLength, downloadURL, 0});
			
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
			//db.close();
		}
		
	}
	
	
	
	
	
	
	public void updateDownloadSuccess(String downloadURL,Map<Integer, Integer> map)
	{
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		try{
			for(Map.Entry<Integer, Integer> entry : map.entrySet()){
				db.execSQL("update FileDownloadLog set downlength=? ,status = ? where downloadurl=? and threadid=?",new Object[]{entry.getValue(), downloadURL, entry.getKey()});
			}
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
			//db.close();
		}
		
	}
	
	public void updateDownloadStatus(String downloadURL,Map<Integer, Integer> map)
	{
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		try{
			for(Map.Entry<Integer, Integer> entry : map.entrySet()){
				db.execSQL("update FileDownloadLog set status = ? where downloadurl=? and threadid=?",new Object[]{entry.getValue(), downloadURL, entry.getKey()});
			}
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
			//db.close();
		}
		
	}
	
	
	public void delete(String path){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("delete from FileDownloadLog where downloadurl=?", new Object[]{path});
		//db.close();
	}



	
	public boolean check(String downloadURL)
	{
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from FileDownloadLog where downloadurl = ? ", new String[]{downloadURL});
		try
		{		
			while(cursor.moveToNext()){
				return true;
			}
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
			//db.close();
		}
		return false;
	}


	
	public Map<String, DownloadBean> getUnfinishDownload()
	{
		Map<String , DownloadBean> downloadMap = new HashMap<String, DownloadBean>();;
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select downloadurl, downlength downloadLength,filelength from FileDownloadLog ",null);
		try
		{		
			while(cursor.moveToNext()){	
				DownloadBean downloadBean = new DownloadBean();
				String downloadURL = cursor.getString(0);
				downloadBean.setDownloadURL(downloadURL);
				downloadBean.setDownloadLength(cursor.getInt(1));
				downloadBean.setFileLength(cursor.getInt(2));
				downloadMap.put(downloadURL, downloadBean);
			}
		}
		finally
		{
			if(cursor != null)
			{
				cursor.close();
			}
			//db.close();
		}
		
		return downloadMap;
	}



	
	
	
	
	
}
