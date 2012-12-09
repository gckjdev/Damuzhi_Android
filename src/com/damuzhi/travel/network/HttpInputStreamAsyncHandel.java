/**  
        * @title HttpInputStreamAsyncHandel.java  
        * @package com.damuzhi.travel.network  
        * @description   
        * @author liuxiaokun  
        * @update 2012-8-24 下午2:33:55  
        * @version V1.0  
 */
package com.damuzhi.travel.network;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import android.os.Message;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-8-24 下午2:33:55  
 */

public class HttpInputStreamAsyncHandel extends AsyncHttpResponseHandler
{
	 protected static final int SUCCESS_MESSAGE = 0;
	    protected static final int FAILURE_MESSAGE = 1;
	    protected static final int START_MESSAGE = 2;
	    protected static final int FINISH_MESSAGE = 3;

	
	    // Callbacks to be overridden, typically anonymously
	    //

	    /**
	     * Fired when a request returns successfully, override to handle in your own code
	     * @param content the body of the HTTP response from the server
	     */
	    public void onSuccess(InputStream inputStream) {}

	  
	    /**
	     * Fired when a request fails to complete, override to handle in your own code
	     * @param error the underlying cause of the failure
	     * @param content the response body, if any
	     */
	    @Override
		public void onFailure(Throwable error, String content) {
	        // By default, call the deprecated onFailure(Throwable) for compatibility
	        onFailure(error);
	    }


	    //
	    // Pre-processing of messages (executes in background threadpool thread)
	    //

	    protected void sendSuccessMessage(InputStream inputStream) {
	        sendMessage(obtainMessage(SUCCESS_MESSAGE, inputStream));
	    }

	    @Override
		protected void sendFailureMessage(Throwable e, String responseBody) {
	        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
	    }
	    
	    @Override
		protected void sendFailureMessage(Throwable e, byte[] responseBody) {
	        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
	    }

	    @Override
		protected void sendStartMessage() {
	        sendMessage(obtainMessage(START_MESSAGE, null));
	    }

	    @Override
		protected void sendFinishMessage() {
	        sendMessage(obtainMessage(FINISH_MESSAGE, null));
	    }


	    //
	    // Pre-processing of messages (in original calling thread, typically the UI thread)
	    //

	    protected void handleSuccessMessage(InputStream inputStream) {
	        onSuccess(inputStream);
	    }

	    @Override
		protected void handleFailureMessage(Throwable e, String responseBody) {
	        onFailure(e, responseBody);
	    }



	    // Methods which emulate android's Handler and Message methods
	    @Override
		protected void handleMessage(Message msg) {
	        switch(msg.what) {
	            case SUCCESS_MESSAGE:
	                handleSuccessMessage((InputStream)msg.obj);
	                break;
	            case FAILURE_MESSAGE:
	                Object[] repsonse = (Object[])msg.obj;
	                handleFailureMessage((Throwable)repsonse[0], (String)repsonse[1]);
	                break;
	            case START_MESSAGE:
	                onStart();
	                break;
	            case FINISH_MESSAGE:
	                onFinish();
	                break;
	        }
	    }

	   

	    // Interface to AsyncHttpRequest
	    void sendResponseMessage(HttpResponse response) {
	        StatusLine status = response.getStatusLine();
	        InputStream inputStream = null;
	        HttpEntity entity = null;
	        try {          
	            HttpEntity temp = response.getEntity();
	            if(temp != null) {
	                entity = new BufferedHttpEntity(temp);
	                inputStream = entity.getContent();
	            }
	        } catch(IOException e) {
	            sendFailureMessage(e, (String) null);
	        }

	        if(status.getStatusCode() >= 300) {
	            sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()),"FAIL");
	        } else {
	            sendSuccessMessage(inputStream);
	        }
	    }
}
