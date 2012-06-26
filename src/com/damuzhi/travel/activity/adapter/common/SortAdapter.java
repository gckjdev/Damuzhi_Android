/**  
        * @title SortAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-26 下午12:22:33  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.adapter.common;

import java.util.HashMap;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.FilterAdapter.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-26 下午12:22:33  
 */

public class SortAdapter extends BaseAdapter
{

	private LayoutInflater mInflater;   
	private String[] mData;  
    private HashMap<Integer, Boolean> isSelected;  
  
   
	public SortAdapter(Context context,
			String[] filterTitleList)
	{
		mInflater = LayoutInflater.from(context);
		mData = filterTitleList;
	}



	@Override  
    public int getCount() {  
        return mData.length;  
    }  
  
    @Override  
    public Object getItem(int position) {  
        return null;  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return 0;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
    	SortViewHolder holder = null;     
        
        if (convertView == null) {  
            holder = new SortViewHolder();  
            convertView = mInflater.inflate(R.layout.filter_place_popup_listview_item, null);  
            holder.title = (TextView) convertView.findViewById(R.id.filter_title);  
            holder.cBox = (CheckBox) convertView.findViewById(R.id.filter_checkbox);  
            convertView.setTag(holder);  
        } else {  
            holder = (SortViewHolder) convertView.getTag();  
        } 
        if(position==mData.length-1)
        {
        	convertView.setBackgroundResource(R.drawable.select_bg_down);
        }else if (position== 0 )
		{
        	convertView.setBackgroundResource(R.drawable.select_bg_top);
		}else
        {
        	convertView.setBackgroundResource(R.drawable.select_bg_center);
        }
        holder.title.setText(mData[position]);  
        if(isSelected!=null && isSelected.size()>0&&isSelected.containsKey(position))
        {
        	boolean checkStatus = isSelected.get(position);
        	holder.cBox.setChecked(checkStatus);  
        }else
        {
        	holder.cBox.setChecked(false); 
        }     
        return convertView;  
    }  
  
    public final class SortViewHolder {  
        public TextView title;  
        public CheckBox cBox;  
    }

	public HashMap<Integer, Boolean> getIsSelected()
	{
		return isSelected;
	}



	public void setIsSelected(HashMap<Integer, Boolean> isSelected)
	{
		this.isSelected = isSelected;
	}  

}
