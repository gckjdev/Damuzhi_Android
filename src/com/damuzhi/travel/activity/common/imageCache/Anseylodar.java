package com.damuzhi.travel.activity.common.imageCache;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.imageCache.ImageLoader.ImageCallback;
import com.damuzhi.travel.activity.common.imageCache.PortraitLodar.PortraitImgCallback;
import com.damuzhi.travel.model.constant.ConstantField;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class Anseylodar {
	
	ImageLoader imageLoader;
	public Anseylodar(){
		imageLoader=new ImageLoader();
	}
	
	/**
	 * 加载内容图片ICON
	 * @param imageView
	 * @param url
	 *//*
	public  void showimgAnsy(ImageView imageView,String url,boolean isstar){
		imageView.setTag(url);
		Bitmap bitmap=null;
		bitmap=imageLoader.loadImage(url, getImagelodarcallback( imageView));
		if (bitmap==null) {
			imageView.setImageResource(R.drawable.dmzlogo);
		}
	     imageView.setImageBitmap(bitmap);
	}
	*/
	
	/**
	 * 加载内容图片图片
	 * @param imageView
	 * @param url
	 */
	public  void showimgAnsy(ImageView imageView,String url,int dataflag){
		imageView.setTag(url);
		if(dataflag == ConstantField.DATA_HTTP)
		{			
			Bitmap bitmap=null;
			bitmap=imageLoader.loadImage(url, getImagelodarcallback( imageView));
			if (bitmap==null) {
				imageView.setImageResource(R.drawable.dmzlogo);
			}
		     imageView.setImageBitmap(bitmap);
		}else {
			FileInputStream fileInputStream;
			try
			{
				fileInputStream = new FileInputStream(new File(url));
				Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
				imageView.setImageBitmap(bitmap);
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}
	
	
	
	
	/**
	 * 获取 callback接口  
	 * @param url
	 * @param imageView
	 * @return
	 */
	private static ImageCallback getImagelodarcallback(final ImageView imageView){
		
	 return	new ImageCallback() {
		@Override
		public void loadedImage(String path, Bitmap bitmap) {
			if (path.equals(imageView.getTag().toString())) {
				imageView.setImageBitmap(bitmap);
			}else {
				imageView.setImageResource(R.drawable.dmzlogo);
			}			
		}
	};
	}
	
	
	
	public static PortraitImgCallback getporcallback(final ImageView imageView){
		return new PortraitImgCallback() {
			
			@Override
			public void loadedImage(String path, Bitmap bitmap) {
				if (path.equals(imageView.getTag().toString())) {
					imageView.setImageBitmap(bitmap);
				}else {
					imageView.setImageResource(R.drawable.dmzlogo);
				}
			}
		};
	};
}
