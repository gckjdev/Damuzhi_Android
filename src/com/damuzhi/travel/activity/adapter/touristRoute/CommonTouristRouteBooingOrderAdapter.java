package com.damuzhi.travel.activity.adapter.touristRoute;

import java.util.ArrayList;
import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.TouristRouteProtos.Order;
import com.damuzhi.travel.util.TravelUtil;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CommonTouristRouteBooingOrderAdapter extends
		BaseExpandableListAdapter {

	private static final String TAG = "CommonTouristRouteBooingOrderAdapter";
	private Context context;
	private List<Order> orderList;
	private LayoutInflater inflater;
	private TextView bookingId;
	private TextView bookingDate;
	private TextView routeName;
	private TextView routeId;
//	private TextView departPlace;
	private TextView departTime;
	private TextView bookingTimeDetail;
	private TextView routeBookingNumber;
	private TextView damuzhiPrice;
	private TextView bookingStatus;
	private Button routeFeedback;
	private Button routeDetail;
	String bookingNumber ="";
	public CommonTouristRouteBooingOrderAdapter(Context context,
			List<Order> orderList) {
		super();
		this.context = context;
		this.orderList = orderList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		if(orderList == null ||orderList.size()==0)
		return null;
		return orderList.get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		return arg1;
	}

	@Override
	public View getChildView(int arg0, int arg1, boolean arg2, View childView,
			ViewGroup arg4) {
		childView = inflater.inflate(R.layout.common_tourist_route_order_list_item, null);
		Order order = orderList.get(arg0);
		routeName = (TextView) childView.findViewById(R.id.route_name);
		routeId = (TextView) childView.findViewById(R.id.route_id);
		//departPlace = (TextView) childView.findViewById(R.id.depart_place);
		departTime = (TextView) childView.findViewById(R.id.depart_time);
		bookingTimeDetail = (TextView) childView.findViewById(R.id.booking_time);
		routeBookingNumber = (TextView) childView.findViewById(R.id.route_booking_number);
		damuzhiPrice =(TextView)  childView.findViewById(R.id.damuzhi_price);
		bookingStatus = (TextView) childView.findViewById(R.id.booking_status);
		routeName.setText(order.getRouteName());
		routeId.setText(""+order.getRouteId());
		departTime.setText(TravelUtil.getDepartTime((long)order.getDepartDate()));
		bookingTimeDetail.setText(TravelUtil.getBookingDate((long)order.getBookDate()));
		bookingNumber = String.format(ConstantField.BOOKING_NUMBER,order.getAdult(),order.getChildren() );
		routeBookingNumber.setText(bookingNumber);
		damuzhiPrice.setText(""+order.getPrice());
		return childView;
	}

	@Override
	public int getChildrenCount(int arg0) {
		if(orderList == null ||orderList.size()==0)
		return 0;
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		if(orderList == null ||orderList.size()==0)
		return null;
		return orderList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		if(orderList == null ||orderList.size()==0)
		return 0;
		return orderList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {		
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.common_tourist_route_order_list_group_item, null);
		}
		if(groupPosition == 0)
		{
			convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.order_list_1));
		}else if (groupPosition == orderList.size()-1) {
			convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.order_list_3));
		}else {
			convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.order_list_2));
		}
		bookingId = (TextView) convertView.findViewById(R.id.booking_id);
		bookingDate = (TextView) convertView.findViewById(R.id.booking_time);
		Order order = orderList.get(groupPosition);
		bookingId.setText(""+order.getOrderId());
		String date = TravelUtil.getDate(order.getBookDate());
		bookingDate.setText(date);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		
		return true;
	}

}