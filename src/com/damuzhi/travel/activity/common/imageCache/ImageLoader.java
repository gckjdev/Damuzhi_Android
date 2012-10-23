package com.damuzhi.travel.activity.common.imageCache;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.damuzhi.travel.util.PicUtill;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class ImageLoader {
		private static final int SOFT_CACHE_CAPACITY = 15;
		private static final String TAG = "ImageLoader";  
		private ArrayList<Task> taskQueue;
		//private volatile static ImageLoader instance;
		/*public static ImageLoader getInstance() {
			if (instance == null) {
				synchronized (ImageLoader.class) {
					if (instance == null) {
						instance = new ImageLoader();
					}
				}
			}
			instance = new ImageLoader();
			ImageLoader instance = new ImageLoader();
			return instance;
		}*/
		
		
	   
	    private   LinkedHashMap <String, WeakReference<Bitmap>> caches =   
	        new  LinkedHashMap<String, WeakReference<Bitmap>>(SOFT_CACHE_CAPACITY, 0.75f, true){  
	        @Override  
	        public WeakReference<Bitmap> put(String key, WeakReference<Bitmap> value){  
	            return super.put(key, value);  
	        }

			@Override
			protected boolean removeEldestEntry(LinkedHashMap.Entry<String, WeakReference<Bitmap>> eldest) {
				// TODO Auto-generated method stub
				if(size() > SOFT_CACHE_CAPACITY){  
	                Log.v(TAG, "Soft WeakReference limit , purge one");  
	                Bitmap bitmap = eldest.getValue().get();
	                if(bitmap!=null&&!bitmap.isRecycled())
	                {
	                	bitmap.recycle();
	                	bitmap = null;
	                }
	                return true;  
	            }  
				return false;
			}  
	        
	    } ;
		
		
		
		
		
		
		
		private Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				Task task = (Task)msg.obj;
				task.callback.loadedImage(task.path, task.bitmap);
			}
			
		};
		
		//private ExecutorService executorService = Executors.newSingleThreadExecutor();
		
		
		
		private Thread thread = new Thread(){

			@Override
			public void run() {
				while(true){
					while(taskQueue.size()>0){
						Task task = taskQueue.remove(0);
						task.bitmap = PicUtill.getbitmap(task.path);					
						caches.put(task.path, new WeakReference<Bitmap>(task.bitmap));
						if(handler!=null){
							Message msg = handler.obtainMessage();
							msg.obj = task;
							handler.sendMessage(msg);
						}
					}
					synchronized (this) {
						try {
							this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
		};
		
		
		
		
		
		/*private void loadImage()
		{
			executorService.execute(new Runnable() {
				
				@Override
				public void run() {
					while(taskQueue.size()>0){
						Task task = taskQueue.remove(0);
						task.bitmap = PicUtill.getbitmap(task.path);					
						caches.put(task.path, new WeakReference<Bitmap>(task.bitmap));
						if(handler!=null){
							Message msg = handler.obtainMessage();
							msg.obj = task;
							handler.sendMessage(msg);
						}
					}
				
				}
			});
		}*/
		
		public ImageLoader(){
			//caches =new  HashMap<String, WeakReference<Bitmap>>();
			taskQueue = new ArrayList<ImageLoader.Task>();
			//loadImage();
			thread.start();
		}
		
		
		public boolean ishavekey(String url){
			return caches.containsKey(url);
		}
		
		
		
		
		public Bitmap loadImage(final String path,final ImageCallback callback){
			
			if(caches.containsKey(path)){
				WeakReference<Bitmap> rf = caches.get(path);
				Bitmap bitmap = rf.get();
				if(bitmap==null){
					caches.remove(path);				
				}else{
					return bitmap;
				}
			}
			if(!caches.containsKey(path)){
				Task task = new Task();
				task.path = path;
				task.callback = callback;
				if(!taskQueue.contains(task)){
					taskQueue.add(task);
					synchronized(thread){
						thread.notify();
					}
					/*synchronized(executorService){
						executorService.notify();
					}*/
					
				}
			}
			return null;
		}
		
		
		
		public interface ImageCallback{
			void loadedImage(String path,Bitmap bitmap);
		}
		
		

		class Task{
			String path;
			Bitmap bitmap;
			ImageCallback callback;
			@Override
			public boolean equals(Object o) {
				if(o == null)
				{
					return false;
				}
				return ((Task)o).path.equals(path);
				
			}
		}
		
		public void recycleBitmap(){
			Iterator iter = caches.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				WeakReference<Bitmap> rf  = (WeakReference<Bitmap>) entry.getValue();
				Bitmap bitmap = rf.get();
				if(null!=bitmap&&!bitmap.isRecycled())
					bitmap.recycle();
					bitmap = null;
			}	
			caches.clear();
			//System.gc() ;
		} 
}
