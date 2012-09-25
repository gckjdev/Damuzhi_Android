package com.damuzhi.travel.activity.common.indexSidebar;

import com.damuzhi.travel.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SideBar extends View {  
		private static final String TAG = "SideBar";
		private char[] l;  
	    private SectionIndexer sectionIndexter = null;  
	    private ListView list;  
	    private TextView mDialogText;
	    private final float m_nItemHeight = getResources().getDimension(R.dimen.side_bar_height);  
	    
	    
	    private float mIndexbarWidth;
		private float mIndexbarMargin;
		private float mPreviewPadding;
		private float mDensity;
		private float mScaledDensity;
		private float mAlphaRate = 1;
		private int mListViewWidth;
		private int mListViewHeight;

		//private String[] mSections = null;
		private RectF mIndexbarRect;
		
	    public SideBar(Context context) {  
	        super(context);  
	        init();  
	    }  
	    public SideBar(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	        init();  
	    }  
	    private void init() {  
	    	mDensity = getResources().getDisplayMetrics().density;
			mScaledDensity = getResources().getDisplayMetrics().scaledDensity;
			
			mIndexbarWidth = 23 * mDensity;
			mIndexbarMargin = 5 * mDensity;
			mPreviewPadding = 5 * mDensity;
	        l = new char[] { '#','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',  
	                'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };  
	    }  
	    public SideBar(Context context, AttributeSet attrs, int defStyle) {  
	        super(context, attrs, defStyle); 
	        init();  
	    }  
	    public void setListView(ListView _list) {  
	        list = _list;  
	        sectionIndexter = (SectionIndexer) _list.getAdapter();  
	       // mSections = (String[]) sectionIndexter.getSections();
	    }  
	    
	    public void setTextView(TextView mDialogText) {  
	    	this.mDialogText = mDialogText;  
	    } 
	    
	    int idx;
	    int position;
	    @Override
	    public boolean onTouchEvent(MotionEvent event) {  
	        super.onTouchEvent(event);  
	        idx = getSectionByPoint(event.getY());
	        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {  
	        	mDialogText.setVisibility(View.VISIBLE);
	        	mDialogText.setText(""+l[idx]);
	            if (sectionIndexter == null) {  
	                sectionIndexter = (SectionIndexer) list.getAdapter();  
	            }  
	            position = sectionIndexter.getPositionForSection(l[idx]);
	            if(idx==0)
	            {
	            	list.setSelection(0); 
	            	mDialogText.setText("热门");
	            }
	            if (position == -1) {  
	                return true;  
	            }  	            
	            list.setSelection(position);         
	        }else {
	        	mDialogText.setVisibility(View.INVISIBLE);
			}
	        return true;  
	    }  
	    @Override
	    protected void onDraw(Canvas canvas) {  
	    	Paint indexbarPaint = new Paint();
			indexbarPaint.setColor(Color.BLACK);
			indexbarPaint.setAlpha((int) (64 * mAlphaRate));
			indexbarPaint.setAntiAlias(true);
			canvas.drawRoundRect(mIndexbarRect, 5 * mDensity, 5 * mDensity, indexbarPaint);
			
			if (l != null && l.length > 0) {				
				Paint indexPaint = new Paint();
				indexPaint.setColor(Color.WHITE);
				indexPaint.setAlpha((int) (255 * mAlphaRate));
				indexPaint.setAntiAlias(true);
				indexPaint.setTextSize(12 * mScaledDensity);
				
				float sectionHeight = (mIndexbarRect.height() - 2 * mIndexbarMargin) / l.length;
				float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint.ascent())) / 2;
				for (int i = 0; i < l.length; i++) {
					if(i==0)
					{
						float paddingLeft = (mIndexbarWidth - indexPaint.measureText("热门")) / 2;
						canvas.drawText("热门", mIndexbarRect.left + paddingLeft
								, mIndexbarRect.top + mIndexbarMargin + sectionHeight * i + paddingTop - indexPaint.ascent(), indexPaint);
					}else
					{
						float paddingLeft = (mIndexbarWidth - indexPaint.measureText(String.valueOf(l[i]))) / 2;
						canvas.drawText(String.valueOf(l[i]), mIndexbarRect.left + paddingLeft
								, mIndexbarRect.top + mIndexbarMargin + sectionHeight * i + paddingTop - indexPaint.ascent(), indexPaint);
					}
					
				}
			}
	        super.onDraw(canvas);  
	    }  
	    
	    @Override
		public void onSizeChanged(int w, int h, int oldw, int oldh) {
			mListViewWidth = w;
			mListViewHeight = h;
			mIndexbarRect = new RectF(w - mIndexbarMargin - mIndexbarWidth
					, mIndexbarMargin
					, w - mIndexbarMargin
					, h - mIndexbarMargin);
		}
	    
	    private int getSectionByPoint(float y) {
			if (l == null || l.length == 0)
				return 0;
			if (y < mIndexbarRect.top + mIndexbarMargin)
				return 0;
			if (y >= mIndexbarRect.top + mIndexbarRect.height() - mIndexbarMargin)
				return l.length - 1;
			return (int) ((y - mIndexbarRect.top - mIndexbarMargin) / ((mIndexbarRect.height() - 2 * mIndexbarMargin) / l.length));
		}
}
