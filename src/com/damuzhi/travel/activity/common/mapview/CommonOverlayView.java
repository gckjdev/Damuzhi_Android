package com.damuzhi.travel.activity.common.mapview;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.TouristRouteProtos.DepartPlace;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;
import com.damuzhi.travel.R;
import com.damuzhi.travel.R;
/**  
 * @title CommonOverlayView.java  
 * @package   
 * @description   
 * @author liuxiaokun  
 * @update 2012-7-9 下午4:08:54  
 * @version V1.0  
 */

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-7-9 下午4:08:54  
 */

public class CommonOverlayView<Item extends OverlayItem> extends BalloonOverlayView<CommonOverlayItem>
{

	private static final String TAG = "CommonOverlayView";
	/**  
	* Constructor Method   
	* @param context
	* @param balloonBottomOffset  
	*/
	private TextView title;
	private ImageView image;
	
	public CommonOverlayView(Context context, int balloonBottomOffset)
	{
		super(context, balloonBottomOffset);
		
	}

	@Override
	protected void setupView(Context context, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.overlay_popup, parent);
		title = (TextView) v.findViewById(R.id.map_bubbleTitle);
		image = (ImageView) v.findViewById(R.id.map_go);
		
	}

	
	@Override
	protected void setBalloonData(CommonOverlayItem item, ViewGroup parent)
	{
		Place place = item.getPlace();
		DepartPlace departPlace = item.getDepartPlace();
		
		if(place!=null){
			Log.d(TAG, "place");
			title.setText(item.getTitle());
		}else if (departPlace != null) {
			Log.d(TAG, "depart place");
			title.setText(departPlace.getDepartPlace());			
			Log.d(TAG, "map view go imageview set gone ");
			image.setVisibility(View.INVISIBLE);

		}else {
			Log.d(TAG, " non");
			parent.getChildAt(0).setVisibility(View.GONE);
		}
		
	}

}
