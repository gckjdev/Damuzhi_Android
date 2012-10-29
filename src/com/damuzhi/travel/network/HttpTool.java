package com.damuzhi.travel.network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;

import android.R.integer;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.common.HelpMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class HttpTool
{

	private static final String TAG = "HttpTool";
	//private  HttpURLConnection urlConnection = null;
	private HttpClient httpClient;
	//private static AsyncHttpClient client = new AsyncHttpClient();
	//byte[] data ;
	
	
	private static HttpTool instance = null;
	
	private HttpTool() {
	}
	public static HttpTool getInstance() {
		//if (instance == null) {
			instance = new HttpTool();
		//}
		return instance;
	}
	
	
	
	
	public  InputStream sendGetRequest(String url) 
	{
		int retry = 5;
		int count = 0;
		while (count<5)
		{
			count += 1;
			try
			{
				return executeHttpClient(url);
			} catch (Exception e)
			{
				if(count <retry)
				{
					Log.d(TAG, "sendGetRequest retry times = "+count);
					continue;
				}else {
					Log.d(TAG, "colud not success with retry...");
				}
			}
			
		}
		return null;
	}
	
	
	private InputStream executeHttpClient(String url) throws Exception
	{
		BufferedInputStream inputStream = null;
		httpClient = TravelApplication.getInstance().getHttpClient();
		HttpGet request = new HttpGet();
		request.setURI(URI.create(url));
		HttpResponse response = httpClient.execute(request);
		inputStream = new BufferedInputStream(response.getEntity().getContent());	
		return inputStream;
	}
	
	
	
	
//	
//	public  HttpURLConnection getConnection(String url) 
//	{
//			 try{
//				 boolean connEnable = TravelApplication.getInstance().checkNetworkConnection();
//				 if(connEnable)
//				 {
//					 URL connUrl = new URL(url);
//					 urlConnection = (HttpURLConnection)connUrl.openConnection();
//					 urlConnection.setConnectTimeout(5*1000);
//					 urlConnection.setRequestMethod("GET");
//					 urlConnection.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
//					 urlConnection.setRequestProperty("Accept-Language", "zh-CN");
//					 urlConnection.setRequestProperty("Accept-Encoding", "identity");
//					 urlConnection.setRequestProperty("Referer", url); 
//					 urlConnection.setRequestProperty("Charset", "UTF-8");
//					 urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
//					 urlConnection.setRequestProperty("Connection", "Keep-Alive");
//				 }
//		         return urlConnection ;
//			
//			} catch (Exception e)
//			{			
//				Log.e(TAG, "<getConnection> but catch exception = "+e.toString(),e);
//				return null;
//			}
//	}
//	
//	
//	
//	
//	
//	
	public   void stopConnection()
	{
		/*if(urlConnection != null)
		{
			urlConnection.disconnect();
		}*/
		/*if(httpClient != null)
		{
			httpClient.getConnectionManager().shutdown();
		}*/
	}
	
	
	
	
	
	
	public static boolean checkNetworkConnection(Context context)
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if (ni.isConnected())
                {
                    haveConnectedWifi = true;
                   // Log.i(TAG,"<haveNetworkConnection> WIFI CONNECTION ----> AVAILABLE");
                } 
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
            {
                if (ni.isConnected())
                {
                    haveConnectedMobile = true;
                   // Log.i(TAG,"<haveNetworkConnection> MOBILE INTERNET CONNECTION ----> AVAILABLE");
                } 
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
	
	// TODO move all http handling here
	
		/*public  InputStream sendGetRequest(String url) 
		{
			client.get(url, new HttpInputStreamHandel()
			{
				@Override
				public void onSuccess(String arg0)
				{
					super.onSuccess(arg0);
				}
				
				@Override
				protected void inputStreamReceived(InputStream arg0)
				{
					inputStream = arg0;
				}
				
				@Override
				public void onFailure(Throwable arg0, String arg1)
				{
					super.onFailure(arg0, arg1);
				}
				
				
			});
			return inputStream;
			 
		}*/
		
		
		
		
		/*public  byte[] sendGetRequestGetData(String url) 
		{
			 boolean connEnable = TravelApplication.getInstance().checkNetworkConnection();
			 if(connEnable)
			 {
				 try{
					 	URL url2 = new URL(url);
					 	urlConnection = (HttpURLConnection)url2.openConnection();
						urlConnection.setDoInput(true);
						urlConnection.setUseCaches(true);
						urlConnection.setRequestProperty("Content-Type", "application/octet-stream");
						urlConnection.setRequestProperty("Connection", "Keep-Alive");// 
						urlConnection.setRequestProperty("Charset", "UTF-8"); 
						urlConnection.setRequestProperty("Accept-Encoding", "identity");
				        urlConnection.setConnectTimeout(5000);
				        urlConnection.setRequestMethod("GET");
				        if(urlConnection !=null&&urlConnection.getDoInput())
				        {		          
				        	InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
				        	if(inputStream != null)
				        	{
				        		int length = urlConnection.getContentLength();
				        		Log.d(TAG, "data length="+length);
				        		data = new byte[length];
				        		inputStream.read(data);
				        		inputStream.close();
				        	}
				        	return data;
				        }else {
							return null;
						}	
				} catch (Exception e)
				{	
					TravelApplication.getInstance().downloadFailToast();
					Log.e(TAG, "<sendGetRequest> but catch exception = "+e.toString(),e);
					return null;
				}
			 }else {
				 TravelApplication.getInstance().makeToast();
				 return null;
			}
			 
		}*/
		
		
		
		
		
		
		/*public  InputStream sendGetRequest(String url) 
		{
			 boolean connEnable = TravelApplication.getInstance().checkNetworkConnection();
			 if(connEnable)
			 {
				 try{
					 	URL url2 = new URL(url);
					 	urlConnection = (HttpURLConnection)url2.openConnection();
						urlConnection.setDoInput(true);
						urlConnection.setUseCaches(true);
						urlConnection.setRequestProperty("Content-Type", "application/octet-stream");
						urlConnection.setRequestProperty("Connection", "Keep-Alive");// 
						urlConnection.setRequestProperty("Charset", "UTF-8"); 
						urlConnection.setRequestProperty("Accept-Encoding", "identity");
				        urlConnection.setConnectTimeout(5000);
				        urlConnection.setRequestMethod("GET");
				        if(urlConnection !=null&&urlConnection.getDoInput())
				        {		          
				        	InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
					        return inputStream;
				        }else {
							return null;
						}	
				} catch (Exception e)
				{	
					TravelApplication.getInstance().downloadFailToast();
					Log.e(TAG, "<sendGetRequest> but catch exception = "+e.toString(),e);
					return null;
				}
			 }else {
				 TravelApplication.getInstance().makeToast();
				 return null;
			}
			 
		}*/
	
	
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
	
	
	/*public static String getTempFileName(HttpURLConnection conn,String downloadURL) {
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
		filename = UUID.randomUUID()+".temp";
		return filename;
	}
	filename =  filename+".temp";
	return filename;
}*/

/*public static String getFileName(HttpURLConnection conn,String downloadURL) {
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
}*/
	/*public  InputStream getDownloadInputStream(URL url,int startPos,int endPos) 
	{
			 try{
				 urlConnection = (HttpURLConnection) url.openConnection();
				 urlConnection.setConnectTimeout(5 * 1000);
				 urlConnection.setRequestMethod("GET");
				 urlConnection.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, *");
				 urlConnection.setRequestProperty("Accept-Language", "zh-CN");
				 urlConnection.setRequestProperty("Referer", url.toString()); 
				 urlConnection.setRequestProperty("Charset", "UTF-8");
				 urlConnection.setRequestProperty("Accept-Encoding", "identity");
				 urlConnection.setRequestProperty("Range", "bytes=" + startPos + "-"+ endPos);
				 urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
				 urlConnection.setRequestProperty("Connection", "Keep-Alive");
				 InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
				 return inputStream;
										
			} catch (Exception e)
			{			
				Log.e(TAG, "<getDownloadInputStream> but catch exception = "+e.toString(),e);
				throw new RuntimeException("get downloadURL inputStream fail,conn error",e);
			}
	}
	*/
	
	
	/*public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
	Map<String, String> header = new LinkedHashMap<String, String>();
	for (int i = 0;; i++) {
		String mine = http.getHeaderField(i);
		if (mine == null) break;
		header.put(http.getHeaderFieldKey(i), mine);
	}
	return header;
	}*/





/*public static String getFileName(String downloadURL) {
	HttpTool httpTool = new HttpTool();
	URLConnection conn = httpTool.getConnection(downloadURL);
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
*/
}
