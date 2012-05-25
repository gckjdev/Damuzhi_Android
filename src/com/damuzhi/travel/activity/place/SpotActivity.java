/**  
        * @title SpotActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-24 下午4:07:02  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.place;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.CommonPlaceListAdapter;
import com.damuzhi.travel.activity.common.PlaceMap;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.service.DataService;
import com.damuzhi.travel.util.TravelUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-24 下午4:07:02  
 */

public class SpotActivity extends CommonPlaceActivity
{
	private int compositor_position = 0;
	private int sort_position = 0;
	private ArrayList<Place> placeList;
	
	private String[] compos;
	private String[] subCatName;
	private int[] subCatKey;
	private CommonPlaceListAdapter commonPlaceListAdapter;
	
	@Override
	public List<Place> getAllPlace()
	{
		// TODO Auto-generated method stub
		DataService dataService = new DataService();
		dataService.getPlace(ConstantField.SCENERY, TravelApplication.getInstance().getCityID(), ConstantField.LANG_HANS);	
		placeList = TravelApplication.getInstance().getPlaceData();
		return placeList;
	}

	
	@Override
	public String getCategoryName()
	{
		// TODO Auto-generated method stub
		return getString(R.string.scenery);
	}


	
	@Override
	public void createFilterButtons(ViewGroup spinnerGroup,CommonPlaceListAdapter adapter)
	{
		// TODO Auto-generated method stub
		LayoutInflater inflater = getLayoutInflater();
		View sortSpinner = inflater.inflate(R.layout.spinner, null);
		View compositorSpinner = inflater.inflate(R.layout.compositor_spinner, null);
		
		LinearLayout spinnerLayout = (LinearLayout) sortSpinner.findViewById(R.id.place_spinner);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins((int)getResources().getDimension(R.dimen.spinnerMargin), 0, 0, 0);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
		params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		spinnerLayout.setLayoutParams(params);			
		
		LinearLayout compositorLayout = (LinearLayout) compositorSpinner.findViewById(R.id.compositor_spinner);
		RelativeLayout.LayoutParams composParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		composParams.setMargins((int)getResources().getDimension(R.dimen.spinnerMargin), 0, 0, 0);
		composParams.addRule(RelativeLayout.RIGHT_OF,R.id.place_spinner);
		composParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		compositorLayout.setLayoutParams(composParams);
		
		compos = getResources().getStringArray(R.array.spot);
		subCatName = AppManager.getInstance().getSubCatNameList(PlaceCategoryType.PLACE_SPOT);
		subCatKey = AppManager.getInstance().getSubCatKeyList(PlaceCategoryType.PLACE_SPOT);
		spinnerGroup.addView(sortSpinner);
		spinnerGroup.addView(compositorSpinner);
		this.commonPlaceListAdapter = adapter;
		sortSpinner.setOnClickListener(clickListener);
		compositorSpinner.setOnClickListener(clickListener);
	}


	
	@Override
	public String getCategorySize()
	{
		String size ="("+placeList.size()+")";
		return size;
	}


	
	@Override
	public int getCategoryType()
	{
		return PlaceCategoryType.PLACE_SPOT_VALUE;
	}


	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			final AlertDialog dialog;
			switch (v.getId())
			{
			case R.id.map_view:
				TravelApplication.getInstance().setPlaceCategoryID(PlaceCategoryType.PLACE_SPOT_VALUE);
				Intent intent = new Intent(SpotActivity.this,PlaceMap.class);
				startActivity(intent);
				break;
			case R.id.place_spinner:
			{	
                AlertDialog.Builder builder=new AlertDialog.Builder(SpotActivity.this)
                .setSingleChoiceItems(subCatName, sort_position,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) 
                    {
                    	sort_position=position;                    	
                    	placeList = TravelUtil.sort(subCatKey[sort_position], TravelApplication.getInstance().getPlaceData());
                    	commonPlaceListAdapter.setList(placeList);
                    	commonPlaceListAdapter.notifyDataSetChanged();                   	
                        dialog.cancel();
                    }
                }).setTitle(SpotActivity.this.getResources().getString(R.string.sort));
                dialog = builder.create();
                dialog.show();

			}
				break;
			case R.id.compositor_spinner:
			{
                AlertDialog.Builder builder=new AlertDialog.Builder(SpotActivity.this)
                .setSingleChoiceItems(compos, compositor_position,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) 
                    {
                    	compositor_position=position;
                    	placeList = TravelUtil.placeComposite(compositor_position, placeList,TravelApplication.getInstance().getLocation());
                    	commonPlaceListAdapter.setList(placeList);
                    	commonPlaceListAdapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                }).setTitle(SpotActivity.this.getResources().getString(R.string.compositor));
                dialog = builder.create();
                dialog.show();

			}
				break;
			default:
			break;
			}
			}
	};
	
	
	

}
