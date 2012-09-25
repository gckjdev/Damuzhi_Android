/**  
        * @title ImagePagerAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-8-30 上午9:40:31  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.adapter.place;

import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.util.TravelUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-8-30 上午9:40:31  
 */

public class ImagePagerAdapter extends PagerAdapter
{

	private List<String> imageUrlList;
	private LayoutInflater inflater;
	//private ImageLoader imageLoader;
	private Context context;
	private int cityId;
	//private DisplayImageOptions options;
	/*public ImagePagerAdapter(List<String> imageUrlList, Context context,int cityId,ImageLoader imageLoader)
	{
		super();
		this.imageUrlList = imageUrlList;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.imageLoader = imageLoader;
		this.cityId = cityId;
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.default_s)
		.cacheInMemory()
		.cacheOnDisc()
		.imageScaleType(ImageScaleType.EXACT)
		.build();
	}*/

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public void finishUpdate(View container) {
	}

	@Override
	public int getCount() {
		return imageUrlList.size();
	}

	/*@Override
	public Object instantiateItem(View view, int position) {
		final View imageLayout = inflater.inflate(R.layout.image_pager, null);
		final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.place_image_item);
		if(position ==0)
		{
			imageView.setBackgroundResource(R.drawable.guide_dot_white);
		}
		
		String imageURL = TravelUtil.getImageUrl(cityId, imageUrlList.get(position));
		imageLoader.displayImage(imageURL, imageView,new ImageLoadingListener() {
			@Override
			public void onLoadingStarted() {
			}

			@Override
			public void onLoadingFailed(FailReason failReason) {
				String message = null;
				switch (failReason) {
					case IO_ERROR:
						message = "Input/Output error";
						break;
					case OUT_OF_MEMORY:
						message = "Out Of Memory error";
						break;
					case UNKNOWN:
						message = "Unknown error";
						break;
				}
				//Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();
				imageView.setImageResource(R.drawable.default_s);
			}

			@Override
			public void onLoadingComplete(Bitmap loadedImage) {
				Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
				imageView.setAnimation(anim);
				anim.start();
			}

			@Override
			public void onLoadingCancelled() {
				// Do nothing
			}
		});

		((ViewPager) view).addView(imageLayout, 0);
		return imageLayout;
	}
*/
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View container) {
	}

	
	/*public void recycleBitmap()
	{
		//anseylodar.recycleBitmap();
		imageLoader.clearMemoryCache();
		imageLoader.clearDiscCache();
		imageLoader.stop();
	}*/
}
