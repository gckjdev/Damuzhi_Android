package com.damuzhi.travel.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.util.Log;
import android.widget.Toast;

import com.damuzhi.travel.model.constant.ConstantField;

public class HttpTool
{

	private static final String TAG = "HttpTool";
	
	// TODO move all http handling here
	
	public  InputStream sendGetRequest(String url) 
	{
		 HttpURLConnection urlConnection = null;
		 try{
			 	urlConnection = (HttpURLConnection)new URL(url).openConnection();
				urlConnection.setDoInput(true);
				urlConnection.setUseCaches(true);
				urlConnection.setRequestProperty("Content-Type", "application/octet-stream");
				urlConnection.setRequestProperty("Connection", "Keep-Alive");// 
				urlConnection.setRequestProperty("Charset", "UTF-8"); 
		        urlConnection.setConnectTimeout(5000);
		        urlConnection.setRequestMethod("GET");
		        if (urlConnection.getResponseCode() != 200)
		        {
		        	Log.d(TAG, "<sendGetRequest> can not get http connection");
		        	return null;
		        }			          
		        return urlConnection.getInputStream();
		
		} catch (Exception e)
		{			
			Log.e(TAG, "<sendGetRequest> but catch exception = "+e.toString(),e);
			return null;
		}
	}
	
	
	public static HttpURLConnection getConnection(String url) 
	{
			 HttpURLConnection conn = null;
			 try{
				 conn = (HttpURLConnection)new URL(url).openConnection();
				 	//HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5*1000);
					conn.setRequestMethod("GET");
					//conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
					conn.setRequestProperty("Accept-Language", "zh-CN");
					conn.setRequestProperty("Referer", url); 
					conn.setRequestProperty("Charset", "UTF-8");
					conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
					conn.setRequestProperty("Connection", "Keep-Alive");
			        return conn ;
			
			} catch (Exception e)
			{			
				Log.e(TAG, "<getConnection> but catch exception = "+e.toString(),e);
				return null;
			}
	}
	
	public static InputStream getDownloadInputStream(URL url,int startPos,int endPos) 
	{
			// HttpURLConnection urlConnection = null;
			 try{
				 	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5 * 1000);
					conn.setRequestMethod("GET");
					//http.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
					conn.setRequestProperty("Accept-Language", "zh-CN");
					conn.setRequestProperty("Referer", url.toString()); 
					conn.setRequestProperty("Charset", "UTF-8");
					conn.setRequestProperty("Range", "bytes=" + startPos + "-"+ endPos);
					conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
					conn.setRequestProperty("Connection", "Keep-Alive");
					return conn.getInputStream();
					/*if(conn.getResponseCode()==200)
					{
						return conn.getInputStream();
					}
					return null;*/
					
			} catch (Exception e)
			{			
				Log.e(TAG, "<getDownloadInputStream> but catch exception = "+e.toString(),e);
				return null;
			}
	}
	
	
	
	public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null) break;
			header.put(http.getHeaderFieldKey(i), mine);
		}
		return header;
		}
	
	
	
	public static String getTempFileName(HttpURLConnection conn,String downloadURL) {
		String filename = downloadURL.substring(downloadURL.lastIndexOf('/') + 1);
		if(filename==null || "".equals(filename.trim())){
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null) break;
				if("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())){
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
					if(m.find()) 
					return m.group(1);
				}
			}
			filename = ".temp"+UUID.randomUUID();
		}
		filename =  ".temp"+filename;
		return filename;
	}
	
	public static String getFileName(HttpURLConnection conn,String downloadURL) {
		String filename = downloadURL.substring(downloadURL.lastIndexOf('/') + 1);
		if(filename==null || "".equals(filename.trim())){
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null) break;
				if("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())){
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
					if(m.find()) 
					return m.group(1);
				}
			}
			filename = UUID.randomUUID()+ ".temp";
		}
		return filename;
	}
	
	/*public static String getFileName(String downloadURL) {
		String filename = downloadURL.substring(downloadURL.lastIndexOf('/') + 1);
		if(filename==null || "".equals(filename.trim())){
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null) break;
				if("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())){
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
					if(m.find()) 
					return m.group(1);
				}
			}
			filename = UUID.randomUUID()+ ".tmp";
		}
		return filename;
	}*/
}
