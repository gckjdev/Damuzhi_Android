package com.damuzhi.travel.activity.common.imageCache;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;



import com.damuzhi.travel.util.PicUtill;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class ImageLoader {
	
	
		private HashMap<String, SoftReference<Bitmap>> caches;
		private ArrayList<Task> taskQueue;
		
		
		private Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				Task task = (Task)msg.obj;
				task.callback.loadedImage(task.path, task.bitmap);
			}
			
		};
		

		private Thread thread = new Thread(){

			@Override
			public void run() {
				while(true){
					while(taskQueue.size()>0){
						Task task = taskQueue.remove(0);
						task.bitmap=PicUtill.getbitmap(task.path);
						caches.put(task.path, new SoftReference<Bitmap>(task.bitmap));
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
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
		};
		
		
		
		public ImageLoader(){
			caches =new  HashMap<String, SoftReference<Bitmap>>();
			taskQueue = new ArrayList<ImageLoader.Task>();
			thread.start();
		}
		
		
		public boolean ishavekey(String url){
			return caches.containsKey(url);
		}
		
		
		
		
		public Bitmap loadImage(final String path,final ImageCallback callback){
			
			if(caches.containsKey(path)){
				SoftReference<Bitmap> rf = caches.get(path);
				Bitmap bitmap = rf.get();
				if(bitmap==null){
					caches.remove(path);				
				}else{
					 //Log.i("size", "-------------------"+caches.size());
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
				return ((Task)o).path.equals(path);
			}
			
			
		}
}
