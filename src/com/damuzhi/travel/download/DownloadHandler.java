/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.damuzhi.travel.download;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;

import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.util.FileUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Used to intercept and handle the responses from requests made using
 * {@link AsyncHttpClient}. Receives response body as byte array with a 
 * content-type whitelist. (e.g. checks Content-Type against allowed list, 
 * Content-length).
 * <p>
 * For example:
 * <p>
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient();
 * String[] allowedTypes = new String[] { "image/png" };
 * client.get("http://www.example.com/image.png", new BinaryHttpResponseHandler(allowedTypes) {
 *     &#064;Override
 *     public void onSuccess(byte[] imageData) {
 *         // Successfully got a response
 *     }
 *
 *     &#064;Override
 *     public void onFailure(Throwable e, byte[] imageData) {
 *         // Response failed :(
 *     }
 * });
 * </pre>
 */
public class DownloadHandler extends AsyncHttpResponseHandler {
	
	private static final String TAG = "DownloadHandler";
	final String saveFilePath;
	final String tempFilePath;
	long fileTotalLength = 0;
	long downloadLength = 0;
	volatile boolean isStop = false;
	
    // Allow images by default
    private static String[] mAllowedContentTypes = new String[] {
        "image/jpeg",
        "image/png"
    };

    /**
     * Creates a new BinaryHttpResponseHandler
     */
    public DownloadHandler(String saveFilePath, String tempFilePath) {
        super();
        this.saveFilePath = saveFilePath;
        this.tempFilePath = tempFilePath;
    }

    /**
     * Creates a new BinaryHttpResponseHandler, and overrides the default allowed
     * content types with passed String array (hopefully) of content types.
     */
//    public DownloadHandler(String[] allowedContentTypes) {
//        super();
//        mAllowedContentTypes = allowedContentTypes;
//    }


    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when a request returns successfully, override to handle in your own code
     * @param content the body of the HTTP response from the server
     */
    public void onSuccess(byte[] binaryData) {}

    /**
     * Fired when a request fails to complete, override to handle in your own code
     * @param error the underlying cause of the failure
     * @param content the response body, if any
     */
    public void onFailure(Throwable error, byte[] binaryData) {
        // By default, call the deprecated onFailure(Throwable) for compatibility
        onFailure(error);
    }


    //
    // Pre-processing of messages (executes in background threadpool thread)
    //

    protected void sendSuccessMessage(byte[] responseBody) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, responseBody));
    }

    @Override
	protected void sendFailureMessage(Throwable e, byte[] responseBody) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
    }

    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    protected void handleSuccessMessage(byte[] responseBody) {
        onSuccess(responseBody);
    }

    protected void handleFailureMessage(Throwable e, byte[] responseBody) {
        onFailure(e, responseBody);
    }

    // Methods which emulate android's Handler and Message methods
    @Override
	protected void handleMessage(Message msg) {
        switch(msg.what) {
            case SUCCESS_MESSAGE:
                handleSuccessMessage((byte[])msg.obj);
                break;
            case FAILURE_MESSAGE:
                Object[] response = (Object[])msg.obj;
                handleFailureMessage((Throwable)response[0], (byte[])response[1]);
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
    
    protected void bytesReceived(long fileTotalLength, long downloadLength, int addedLength){
    	
    }

    // Interface to AsyncHttpRequest
    void sendResponseMessage(HttpResponse response) {
    	
    	fileTotalLength = 0;
    	downloadLength = 0;
    	
    	StatusLine status = response.getStatusLine();
    	Log.i(TAG, "<download file> http status code="+status.getStatusCode());
    	byte[] responseBody = null;
    	
    	if (status.getStatusCode() == 416){
    		downloadLength = FileUtil.getFileSize(tempFilePath);
    		fileTotalLength = downloadLength;          
    		FileUtil.fileMove(tempFilePath, saveFilePath);
    		sendSuccessMessage(responseBody);    		
    		return;
    	}
    	
    	if (status.getStatusCode() == 405){   		
    		return;
    	}
    	
    	if(status.getStatusCode() >= 300) {
    		sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody);
    		return;
        }    	        
                
        try
		{
        	Header[] contentLengthHeaders = response.getHeaders("Content-Length");
        	if (contentLengthHeaders.length < 0){
        		return;
        	}
        	
        	//long lastTime = 0;
        	
        	long contentLength = Integer.parseInt(contentLengthHeaders[0].getValue());
        	long sdCardFreeMerroy = FileUtil.getAvailableExternalMemorySize();
        	Log.d(TAG, "AvailableExternalMemorySize = "+sdCardFreeMerroy);
        	if(sdCardFreeMerroy<contentLength)
        	{
        		TravelApplication.getInstance().notEnoughMemoryToast();      		
        		return;
        	}
        	if (contentLength == 0){
        		// no more data
        		// if complete, copy file to save file path     
        		FileUtil.fileMove(tempFilePath, saveFilePath);
        		sendSuccessMessage(responseBody);
        		return;
        	}
        	
        	// read file total length        	
        	downloadLength = FileUtil.getFileSize(tempFilePath);
        	fileTotalLength = contentLength + downloadLength;
        	if (downloadLength == fileTotalLength){
            	
        		// if complete, copy file to save file path
            	FileUtil.fileMove(tempFilePath, saveFilePath);
        		sendSuccessMessage(responseBody);
        		return;
        	}
        	
        	
        	FileOutputStream output = new FileOutputStream(tempFilePath, true);        	
        	InputStream inputStream = new BufferedInputStream(response.getEntity().getContent());
        	int offset = -1;
        	byte[] buffer = new byte[10240];
        	while ((offset = inputStream.read(buffer, 0, 10240)) != -1) {        		        		
        		output.write(buffer, 0, offset);
        		output.flush();
        		downloadLength += offset;
        		
        		bytesReceived(fileTotalLength, downloadLength, offset);		
        		
        	//	lastTime = System.currentTimeMillis();
        		
        		if (isStop){
        			Log.d(TAG, "detect stop flag, stop download");
        			break;
        		}
			}
        	
        	output.close();
           	inputStream.close();
           	
           	if (isStop){
           		//sendFailureMessage(new HttpResponseException(status.getStatusCode(), "DownloadStop!"), responseBody);
           		return;
           	}
           	
        	// if complete, copy file to save file path
           	boolean moveFileFlag = FileUtil.fileMove(tempFilePath, saveFilePath);
           	Log.d(TAG, "move file flag = "+moveFileFlag);
 	
        } catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			sendFailureMessage(new HttpResponseException(status.getStatusCode(), "FileNotFound!"), responseBody);
			return;
		} catch(IOException e) {			
            sendFailureMessage(e, (byte[]) null);
            return;
        } catch(Exception e){
        	sendFailureMessage(e, (byte[]) null);
        	return;
        }

        
        sendSuccessMessage(responseBody);

    }

    public void cancelDownload(){
    	Log.d(TAG, "can download, set stop flag");
    	isStop = true;
    }
    
}