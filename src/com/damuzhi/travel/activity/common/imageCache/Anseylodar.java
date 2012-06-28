package com.damuzhi.travel.activity.common.imageCache;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.imageCache.ImageLoader.ImageCallback;
import com.damuzhi.travel.activity.common.imageCache.PortraitLodar.PortraitImgCallback;
import com.damuzhi.travel.mission.more.MoreMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.util.TravelUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

public class Anseylodar {
	
	private static final String TAG = "Anseylodar";
	ImageLoader imageLoader;
	public Anseylodar(){
		imageLoader=new ImageLoader();
	}
	
	/**
	 * ��������ͼƬICON
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
				
			}else {
				FileInputStream fileInputStream = null;
				try
				{
					String dataPath = TravelUtil.getCityDataPath()+url;
					fileInputStream = new FileInputStream(new File(dataPath));
					bitmap = BitmapFactory.decodeStream(fileInputStream);
					fileInputStream.close();
					//imageView.setImageBitmap(bitmap);
				} catch (Exception e)
				{
					Log.e(TAG, "<showimgAnsy> but catch exception :"+e.toString(),e);
				}			
				finally
				{
					try
					{
						fileInputStream.close();
					} catch (Exception e)
					{
					}
				}
			}
			if (bitmap==null) {
				imageView.setImageResource(R.drawable.default_s);
			}
		     imageView.setImageBitmap(bitmap);
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
					imageView.setImageBitmap(bitmap);
				}else {
					imageView.setImageResource(R.drawable.default_s);
				}
			}
		};
	};
}
