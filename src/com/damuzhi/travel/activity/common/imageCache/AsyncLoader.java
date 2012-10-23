package com.damuzhi.travel.activity.common.imageCache;




import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.ImageLoader.ImageCallback;
import com.damuzhi.travel.activity.common.imageCache.PortraitLodar.PortraitImgCallback;
import com.damuzhi.travel.mission.more.MoreMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.PicUtill;
import com.damuzhi.travel.util.TravelUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import com.damuzhi.travel.R;
public class AsyncLoader {
	
	private static final String TAG = "Anseylodar";
	private HashMap<String, WeakReference<Bitmap>> localBitmaps = new HashMap<String, WeakReference<Bitmap>>();;
	//ImageLoader imageLoader = ImageLoader.getInstance();
	ImageLoader imageLoader = new ImageLoader();
	/*public AsyncLoader(){
		
	}*/
	
	//private volatile static AsyncLoader instance;
	/*public static AsyncLoader getInstance() {
		if (instance == null) {
				if (instance == null) {
					instance = new AsyncLoader();
				}
		}
		instance = new AsyncLoader();
		AsyncLoader instance = new AsyncLoader();
		return instance;
	}
	*/
	
	
	public  void showimgAnsy(ImageView imageView,String url,int cityId){
		imageView.setTag(url);
		Bitmap bitmap=null;
		int dataflag = TravelUtil.checkImageSouce(url);
		boolean isShow = MoreMission.getInstance().isShowListImage();
		if(isShow)
		{
			if(dataflag == ConstantField.DATA_LOCAL)
			{
				 url = TravelUtil.getCityDataPath(cityId)+url;
				 if(localBitmaps != null&&!localBitmaps.containsKey(url))
				 {
					 bitmap = PicUtill.getLocalBitmap(url);	
					 localBitmaps.put(url,new WeakReference<Bitmap>(bitmap));
				 }else
				 {
					 WeakReference<Bitmap> rf  = localBitmaps.get(url);
					 bitmap = rf.get();
					 if(bitmap == null)
					 {
						 localBitmaps.remove(url);
						 bitmap = PicUtill.getLocalBitmap(url);	
						 localBitmaps.put(url,new WeakReference<Bitmap>(bitmap));
					 }
				 }
				
			}else
			{
				bitmap=imageLoader.loadImage(url, getImagelodarcallback( imageView));	
			}
			if (bitmap==null) {
				imageView.setImageResource(R.drawable.default_s);
			}else {
				imageView.setImageBitmap(bitmap);
			}
		     
		}else
		{
			imageView.setImageResource(R.drawable.default_s);
		}		
	}
	
	
	public  void showimgAnsy(ImageView imageView,String url){
		imageView.setTag(url);
		Bitmap bitmap=null;
		int dataflag = TravelUtil.checkImageSouce(url);
		boolean isShow = MoreMission.getInstance().isShowListImage();
		if(isShow)
		{
			if(dataflag == ConstantField.DATA_HTTP)
			{
				bitmap=imageLoader.loadImage(url, getImagelodarcallback( imageView));
			}else
			{
				 url = TravelUtil.getCityDataPath()+url;
				 if(localBitmaps != null&&!localBitmaps.containsKey(url))
				 {
					 bitmap = PicUtill.getLocalBitmap(url);	
					 localBitmaps.put(url,new WeakReference<Bitmap>(bitmap));
				 }else
				 {
					 WeakReference<Bitmap> rf  = localBitmaps.get(url);
					 bitmap = rf.get();
					 if(bitmap == null)
					 {
						 localBitmaps.remove(url);
						 bitmap = PicUtill.getLocalBitmap(url);	
						 localBitmaps.put(url,new WeakReference<Bitmap>(bitmap));
					 }
				 }
			}		
		     if (bitmap==null){
				imageView.setImageResource(R.drawable.default_s);
		     }else
		     {
		    	 imageView.setImageBitmap(bitmap); 
		     }
		}else
		{
			imageView.setImageResource(R.drawable.default_s);
		}		
	}
	
	
	private static ImageCallback getImagelodarcallback(final ImageView imageView){
		
	 return	new ImageCallback() {
		@Override
		public void loadedImage(String path, Bitmap bitmap) {
			if (path.equals(imageView.getTag().toString())) {
				if(bitmap!= null)
				{
					imageView.setImageBitmap(bitmap);
				}else
				{
					imageView.setImageResource(R.drawable.default_s);
				}
				
			}else {
				imageView.setImageResource(R.drawable.default_s);
			}			
		}
	};
	}
	
	
	
	/*public static PortraitImgCallback getporcallback(final ImageView imageView){
		return new PortraitImgCallback() {
			
			@Override
			public void loadedImage(String path, Bitmap bitmap) {
				if (path.equals(imageView.getTag().toString())) {
					if(bitmap == null)
					{
						imageView.setImageResource(R.drawable.default_s);
					}else {
						imageView.setImageBitmap(bitmap);
					}					
				}else {
					imageView.setImageResource(R.drawable.default_s);
				}
			}
		};
	};*/
	
	
	public void recycleBitmap()
	{
		imageLoader.recycleBitmap();
		imageLoader = null;
		clearBitmap();
		//System.gc();
	}
	
	
	private void clearBitmap()
	{
		if(localBitmaps!=null && localBitmaps.size()>0)
		{
			Iterator iterator = localBitmaps.entrySet().iterator();
			while (iterator.hasNext())
			{
				Entry entry = (Entry) iterator.next();
				WeakReference<Bitmap> sf = (WeakReference<Bitmap>) entry.getValue();
				Bitmap bitmap = sf.get();
				if(bitmap != null&&!bitmap.isRecycled())
				{
					bitmap.recycle();
					bitmap = null;
				}	
			}
		}
		localBitmaps.clear();
	}
	
}
