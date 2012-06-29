package com.damuzhi.travel.activity.common.imageCache;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.damuzhi.travel.util.MD5Util;
import com.damuzhi.travel.util.PicUtill;


public class PortraitLodar {
		Context context;
		private HashMap<String, SoftReference<Bitmap>> caches;
		private ArrayList<DownTask> DownTaskQueue;
		private Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				DownTask DownTask = (DownTask)msg.obj;
				DownTask.callback.loadedImage(DownTask.path, DownTask.bitmap);
			}
			
		};
		private Thread thread = new Thread(){

			@Override
			public void run() {
				while(true){
					while(DownTaskQueue.size()>0){
						DownTask downTask = DownTaskQueue.remove(0);
						try {
							downTask.bitmap=PicUtill.getbitmapAndwrite(context, downTask.path);
							caches.put(downTask.path, new SoftReference<Bitmap>(downTask.bitmap));
							if(handler!=null){
								Message msg = handler.obtainMessage();
								msg.obj = downTask;
								handler.sendMessage(msg);
							}
						} catch (IOException e) {
							e.printStackTrace();
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
		
		public PortraitLodar(Context context){
			caches =new  HashMap<String, SoftReference<Bitmap>>();
			DownTaskQueue = new ArrayList<PortraitLodar.DownTask>();
			this.context=context;
			thread.start();
		}
		public Bitmap loadImage(final String path,final PortraitImgCallback callback){
	       Bitmap bitmap=null;
		   bitmap=getbitmapMap(path);
		   if (bitmap==null) {
			  bitmap=getFromFile(path);
			  caches.put(path, new SoftReference<Bitmap>(bitmap));
			   if (bitmap!=null) {
				return bitmap;
			   }else {
				    DownTask DownTask = new DownTask();
					DownTask.path = path;
					Log.i("path", path);
					DownTask.callback = callback;
					if(!DownTaskQueue.contains(DownTask)){
						DownTaskQueue.add(DownTask);
						synchronized(thread){
							thread.notify();
						}
					}
			}
		}else {
			return bitmap;
		}
			return null;
		}
		
		
		public Bitmap getbitmapMap(String path){
			  Bitmap bitmap=null;
			  if (caches.containsKey(path)) {
				  SoftReference<Bitmap> rf = caches.get(path);
					 bitmap = rf.get();
					if(bitmap==null){
						caches.remove(path);
						return null;
					}else{
						return bitmap;
					}
			}else {
				return null;
			}
				
		}
		public interface PortraitImgCallback{
			void loadedImage(String path,Bitmap bitmap);
		}
		
		private Bitmap getFromFile(String url) {
			String name = MD5Util.MD5(url);
			FileInputStream inputStream = null;
			try {
				inputStream = context.openFileInput(name);
				return BitmapFactory.decodeStream(inputStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} finally {
				if (null != inputStream) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		class DownTask{
			String path;
			Bitmap bitmap;
			PortraitImgCallback callback;
			@Override
			public boolean equals(Object o) {
				return ((DownTask)o).path.equals(path);
			}
		}
}
