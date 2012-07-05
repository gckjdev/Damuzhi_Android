/**  
        * @title CommonScrollView.java  
        * @package com.damuzhi.travel.activity.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-7-5 下午3:39:47  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-7-5 下午3:39:47  
 */

public class CommonScrollView extends ScrollView
{

	public CommonScrollView(Context context)
	{
		super(context);
	}

	
	public CommonScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	
	public CommonScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		// TODO Auto-generated method stub
		if(ev.getX()>=ev.getY())
		{
			return false;
		}else {
			return true;
		}
		
		
	}
	
	

}
