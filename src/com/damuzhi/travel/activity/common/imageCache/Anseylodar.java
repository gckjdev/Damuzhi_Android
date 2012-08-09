package com.damuzhi.travel.activity.common.imageCache;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.ImageLoader.ImageCallback;
import com.damuzhi.travel.activity.common.imageCache.PortraitLodar.PortraitImgCallback;
import com.damuzhi.travel.mission.more.MoreMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.PicUtill;
import com.damuzhi.travel.util.TravelUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

public class Anseylodar {
	
	private static final String TAG = "Anseylodar";
	private List<Bitmap> localBitmaps ;
	ImageLoader imageLoader;
	public Anseylodar(){
		imageLoader=new ImageLoader();
		localBitmaps = new ArrayList<Bitmap>();
	}
	
	
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
				 bitmap = PicUtill.getLocalBitmap(url);	
				 localBitmaps.add(bitmap);
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
				bitmap = PicUtill.getLocalBitmap(url);
				localBitmaps.add(bitmap);
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
				imageView.setImageBitmap(bitmap);
			}else {
				imageView.setImageResource(R.drawable.default_s);
			}			
		}
	};
	}
	
	
	
	public static PortraitImgCallback getporcallback(final ImageView imageView){
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
	};
	
	
	public void recycleBitmap()
	{
		imageLoader.destoryBitmap();
		destoryBitmap();
	}
	
	
	private void destoryBitmap()
	{
		if(localBitmaps!=null && localBitmaps.size()>0)
		{
			for(Bitmap bitmap:localBitmaps)
			{
				if(bitmap != null&&!bitmap.isRecycled())
				{
					bitmap.recycle();
				}
			}
		}
		System.gc();
	}
	
}
