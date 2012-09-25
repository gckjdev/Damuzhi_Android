package com.damuzhi.travel.activity.touristRoute;

import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.touristRoute.CommonTouristRouteBooingOrderAdapter;
import com.damuzhi.travel.mission.touristRoute.TouristRouteMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.TouristRouteProtos.Order;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

public class CommonTouristRouteOrderList extends Activity {

	private static final String TAG = "CommonTouristRouteOrderList";
	private ExpandableListView expandableListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_tourist_route_order_list);
		expandableListView = (ExpandableListView) findViewById(R.id.tourist_route_order_list);
		int cityId = AppManager.getInstance().getCurrentCityId();
		String orderType = ConstantField.TOURIST_ROUTE_LOCAL_ROUTE_BOOKIING_LIST;
		String userId = UserManager.getInstance().getUserId(CommonTouristRouteOrderList.this);
		String loginId = "";
		String token = "";
		List<Order> orderList = TouristRouteMission.getInstance().getOrderList(cityId,orderType,userId,loginId,token);
		Log.d(TAG, "orderlist size = "+orderList.size());
		CommonTouristRouteBooingOrderAdapter adapter = new CommonTouristRouteBooingOrderAdapter(CommonTouristRouteOrderList.this, orderList);
		expandableListView.setAdapter(adapter);
		for(int i= 0;i <orderList.size();i++)
		{
			expandableListView.expandGroup(i);
		}
		
	}

}
