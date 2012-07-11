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

	// 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                
                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
                
                if(xDistance > yDistance){
                    return false;
                }  
        }

        return super.onInterceptTouchEvent(ev);
    }
	
	
	

}
