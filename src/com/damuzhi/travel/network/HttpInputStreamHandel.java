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

package com.damuzhi.travel.network;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;


import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Handler;
import android.os.Message;
import android.os.Looper;
import android.util.Log;

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
public class HttpInputStreamHandel extends AsyncHttpResponseHandler {
	
	private static final String TAG = "HttpInputStreamHandel";
	InputStream httpInputStream = null;
	
    // Allow images by default
    private static String[] mAllowedContentTypes = new String[] {
        "image/jpeg",
        "image/png"
    };

  
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
    
    protected void inputStreamReceived(InputStream arg0){
    	
    }

    // Interface to AsyncHttpRequest
    void sendResponseMessage(HttpResponse response) {
    	StatusLine status = response.getStatusLine();
    	Log.i(TAG, "<download file> http status code="+status.getStatusCode());
    	byte[] responseBody = null;
    	
    	if(status.getStatusCode() >= 300) {
    		sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody);
    		return;
        }    	        
             
    	if(httpInputStream != null)
    	{
    		sendSuccessMessage(responseBody);
    	}
    	
        try
		{
        	Header[] contentLengthHeaders = response.getHeaders("Content-Length");
        	if (contentLengthHeaders.length < 0){
        		return;
        	}        	
        	httpInputStream = response.getEntity().getContent();
        	inputStreamReceived(httpInputStream);
        } catch(IOException e) {			
            sendFailureMessage(e, (byte[]) null);
            return;
        } catch(Exception e){
        	sendFailureMessage(e, (byte[]) null);
        	return;
        }  
        sendSuccessMessage(responseBody);
    }

        
}