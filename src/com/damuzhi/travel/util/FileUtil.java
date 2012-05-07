package com.damuzhi.travel.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import com.damuzhi.travel.model.constant.ConstantField;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class FileUtil
{
	private static final String TAG = "FileUtil";
	private List<String> lstFile = new ArrayList<String>();  //结果 List
	private ArrayList<FileInputStream> fileInput = new ArrayList<FileInputStream>();
	
	public List<String> GetFiles(String Path, String Extension, boolean IsIterative)  //搜索目录，扩展名，是否进入子文件夹
	{
	    File[] files = new File(Path).listFiles(); 
	    for (int i = 0; i < files.length; i++)
	    {
	        File f = files[i];
	        if (f.isFile())
	        {
	            if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension))
	            {
	            	lstFile.add(f.getPath());
	            }  //判断扩展名 
	            if (!IsIterative)
	                break;
	        }
	        else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)  //忽略点文件（隐藏文件/文件夹）
	            {
	        		GetFiles(f.getPath(), Extension, IsIterative);
	            }
	    }
	    return lstFile;
	}	
	
	//获取placedata文件
	public List<String> GetFiles(String Path, String type,String Extension, boolean IsIterative)  //搜索目录，扩展名，是否进入子文件夹
	{
	    File[] files = new File(Path).listFiles(); 
	    for (int i = 0; i < files.length; i++)
	    {
	        File f = files[i];
	        if (f.isFile())
	        {
	        	String fileExtension = f.getPath().substring(f.getPath().length() - Extension.length());
	        	String fileType = f.getPath().substring(f.getPath().lastIndexOf("/")+1,f.getPath().lastIndexOf("."));
	            if (fileExtension.equals(Extension)&&fileType.contains(type))
	            {
	            	lstFile.add(f.getPath());
	            }  //判断扩展名 
	            if (!IsIterative)
	                break;
	        }
	        else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)  //忽略点文件（隐藏文件/文件夹）
	            {
	        		GetFiles(f.getPath(), Extension, IsIterative);
	            }
	    }
	    return lstFile;
	}
	
	//获取placedata文件数据输入流
		public ArrayList<FileInputStream> getFileInputStreams(String Path, String type,String Extension, boolean IsIterative)  //搜索目录，扩展名，是否进入子文件夹
		{
			
		    File[] files = new File(Path).listFiles(); 
		    for (int i = 0; i < files.length; i++)
		    {
		        File f = files[i];
		        if (f.isFile())
		        {
		        	String fileExtension = f.getPath().substring(f.getPath().length() - Extension.length());
		        	String fileType = f.getPath().substring(f.getPath().lastIndexOf("/")+1,f.getPath().lastIndexOf("."));
		            if (fileExtension.equals(Extension)&&fileType.contains(type))
		            {
		            	//lstFile.add(f.getPath());
		            	FileInputStream fileInputStream;
						try
						{
							//Log.d(TAG, f.getPath());
							fileInputStream = new FileInputStream(new File(f.getPath()));
							fileInput.add(fileInputStream);
						} catch (FileNotFoundException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    			
		            }   
		            if (!IsIterative)
		                break;
		        }
		        else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)  //忽略点文件（隐藏文件/文件夹）
		            {
		        	getFileInputStreams(f.getPath(),type, Extension, IsIterative);
		            }
		    }
		    return fileInput;
		}
	
	
		public static boolean checkFileIsExits(String fileName)
		{
			String filePath = ConstantField.SAVE_PATH+fileName;
			File file = new File(filePath);
			if(file.exists())
			{
				return true;
			}
			return false;
		}
		
	
     
	    public static int freeSpaceOnSd() { 
	    	StatFs stat = new StatFs(Environment.getExternalStorageDirectory() .getPath());
	        double sdFreeMB = ((double)stat.getAvailableBlocks() * (double) stat.getBlockSize()) / 1024; 
	        return (int) sdFreeMB; 
	    } 
}
