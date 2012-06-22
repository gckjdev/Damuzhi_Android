/**  
        * @title FilterAdapter.java  
        * @package com.damuzhi.travel.activity.adapter.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-22 下午5:34:35  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.adapter.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.damuzhi.travel.R;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-22 下午5:34:35  
 */

public class FilterAdapter extends BaseAdapter
{

	private LayoutInflater mInflater;  
    private List<String> mData;  
    public static Map<Integer, Boolean> isSelected;  
  
   
	public FilterAdapter(Context context,
			List<String> filterTitleList, HashMap<Integer, Boolean> isSelected)
	{
		mInflater = LayoutInflater.from(context);
		mData = filterTitleList;
		this.isSelected = isSelected;
	}



	@Override  
    public int getCount() {  
        return mData.size();  
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
        ViewHolder holder = null;  
        if (convertView == null) {  
            holder = new ViewHolder();  
            convertView = mInflater.inflate(R.layout.filter_place_popup_listview_item, null);  
            holder.title = (TextView) convertView.findViewById(R.id.filter_title);  
            holder.cBox = (CheckBox) convertView.findViewById(R.id.filter_checkbox);  
            convertView.setTag(holder);  
        } else {  
            holder = (ViewHolder) convertView.getTag();  
        }  
        holder.title.setText(mData.get(position));  
        holder.cBox.setChecked(isSelected.get(position));  
        return convertView;  
    }  
  
    public final class ViewHolder {  
        public TextView title;  
        public CheckBox cBox;  
    }  

}
