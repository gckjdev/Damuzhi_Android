/**  
        * @title CommonPlaceDetailActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-29 下午1:41:04  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorJoiner.Result;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.NearbyPlaceListAdapter;
import com.damuzhi.travel.activity.adapter.place.PlaceImageAdapter;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.NearbyPlaceMap;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.Anseylodar;
import com.damuzhi.travel.mission.favorite.FavoriteMission;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-29 下午1:41:04  
 */

public abstract class CommonPlaceDetailActivity extends Activity
{

	private static final String TAG = "CommonPlaceDetailActivity";
	
	
	abstract public Place getPlaceById();
	abstract public String getPlaceIntroTitle();
	abstract public boolean isSupportSpecialTrafficStyle();
	abstract public boolean isSupportTicket();
	abstract public boolean isSupportKeyWords();
	abstract public boolean isSupportTips();
	abstract public boolean isSupportHotelStart();
	abstract public boolean isSupportService();
	abstract public boolean isSupportRoomPrice();
	abstract public boolean isSupportOpenTime();
	abstract public boolean isSupportFood();
	abstract public boolean isSupportAvgPrice();
	abstract public boolean isSupportSpecialFood();
	abstract public boolean isSupportPark();
	
	
	private NearbyPlaceListAdapter nearbyAdapter;
	int placeId;
	Place place;
	ImageView[] imageViews;
	String tipsTitle;
	private TextView phoneNum,favoriteCount,collect;
	private ImageView coolectBtn;
	private List<Place> nearbyPlaceList = new ArrayList<Place>();
	private AsyncTask<Void, Void, List<Place>> nearbyAsyncTask;
	ViewGroup placeDetailGroup,nearbyListGroup;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		TravelApplication.getInstance().addActivity(this);
		init();
		getNearbyList();
		getPlaceFavoriteCount();
	}

	
	private void init()
	{
		 place = getPlaceById();
		if(place != null)
		{
			List<String> imagePath = place.getImagesList();
			LayoutInflater inflater = getLayoutInflater();
			ArrayList<View> imageViewlist = new ArrayList<View>();		
			int size=imagePath.size();
			for(int i=0;i<size;i++)
			{
				Anseylodar anseylodar = new Anseylodar();
				View view = inflater.inflate(R.layout.place_detail_image, null);
				ImageView imageView = (ImageView) view.findViewById(R.id.place_image_item);
				String url ;
				url = imagePath.get(i);	
				// update anseylodar.showimgAnsy judge http or local
				anseylodar.showimgAnsy(imageView, url);
				imageViewlist.add(view);
			}
			imageViews = new ImageView[size];	
			ViewGroup main = (ViewGroup) inflater.inflate(R.layout.common_place_detail, null);
			ViewGroup group = (ViewGroup) main.findViewById(R.id.place_images_group);
			ViewPager placeImage = (ViewPager) main.findViewById(R.id.place_images);
			ImageView imageView;
			for (int i = 0; i < size; i++) {  
	            imageView = new ImageView(CommonPlaceDetailActivity.this);  
	            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
	            params.setMargins((int)getResources().getDimension(R.dimen.image_margin), 0, (int)getResources().getDimension(R.dimen.image_margin), 0);
	            imageView.setLayoutParams(params);  
	          //  imageView.setPadding((int)getResources().getDimension(R.dimen.image_margin), 0, (int)getResources().getDimension(R.dimen.image_margin), 0);
	            imageViews[i] = imageView;  
	            if (i == 0) {    
	                imageViews[i].setBackgroundResource(R.drawable.guide_dot_white);  
	            } else {  
	                imageViews[i].setBackgroundResource(R.drawable.guide_dot_black);  
	            }  
	            group.addView(imageView);  
	        } 
			PlaceImageAdapter sceneryAdapter = new PlaceImageAdapter(imageViewlist);	
		
		
		//serviceGroup
		if(isSupportService())
		{
			ViewGroup serviceGroup = (ViewGroup) main.findViewById(R.id.proServiceGroup);
			serviceGroup.setVisibility(View.VISIBLE);
			for(int id:place.getProvidedServiceIdList())
			{
				
				 ImageView serviceImageView = new ImageView(CommonPlaceDetailActivity.this);  
				 serviceImageView.setLayoutParams(new LayoutParams(new LayoutParams((int)this.getResources().getDimension(R.dimen.service_icon2),android.view.WindowManager.LayoutParams.WRAP_CONTENT)));  
				 //serviceImageView.setPadding(10, 0, 10, 0);  
				 serviceImageView.setScaleType(ScaleType.FIT_CENTER);
				 serviceImageView.setImageResource(TravelUtil.getServiceImage(id));
		         serviceGroup.addView(serviceImageView);
			}
		}
		
		
		placeImage.setAdapter(sceneryAdapter);
		placeImage.setOnPageChangeListener(scenecyImageListener);
		setContentView(main);
		
		if(isSupportSpecialTrafficStyle())
		{
			main.findViewById(R.id.special_trans_group).setVisibility(View.VISIBLE);
			String trafficInfos = place.getTransportation();
			String[] traffic= trafficInfos.split(";"); 		
			ViewGroup specialTrans = (ViewGroup) main.findViewById(R.id.special_trans);
			if(traffic.length >0)
			{
				int i= 1;
				for(String trafficInfo:traffic)
				{
					RelativeLayout row = new RelativeLayout(CommonPlaceDetailActivity.this);
					row.setLayoutParams(new LayoutParams((int)getResources().getDimension(R.dimen.transport_width),(int)getResources().getDimension(R.dimen.transport_height)));
					if(i==traffic.length)
					{
						row.setBackgroundDrawable(getResources().getDrawable(R.drawable.table5_down));
					}else {
						row.setBackgroundDrawable(getResources().getDrawable(R.drawable.table5_center));
					}
					
					
					String[] trafficDetail = trafficInfo.split(":");
					if(trafficDetail.length == 2)
					{
						TextView  locationTextView = new TextView(CommonPlaceDetailActivity.this); 
						TextView  distanceTextView = new TextView(CommonPlaceDetailActivity.this);
						RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
						RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
						params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
						params1.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
						params1.setMargins((int)getResources().getDimension(R.dimen.transport_margin_left), 0, 0, 0);
						params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
						params2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
						params2.setMargins((int)getResources().getDimension(R.dimen.transport_margin_right), 0, 0, 0);
						locationTextView.setLayoutParams(params1);  
						distanceTextView.setLayoutParams(params2);  
						locationTextView.setTextColor(getResources().getColor(R.color.place_title_color));
						locationTextView.setTextSize(getResources().getDimension(R.dimen.transport_text_size));
						distanceTextView.setTextColor(getResources().getColor(R.color.place_title_color));
						distanceTextView.setTextSize(getResources().getDimension(R.dimen.transport_text_size));
						locationTextView.setText(trafficDetail[0]);
						distanceTextView.setText(trafficDetail[1]);
						row.addView(locationTextView);
						row.addView(distanceTextView);
						specialTrans.addView(row,i);
						i++;
					}
					
				}
			}		
					
		}else {
			main.findViewById(R.id.transport_group).setVisibility(View.VISIBLE);
			TextView transport = (TextView) main.findViewById(R.id.transport);
			transport.setSelected(true);
			transport.setText(place.getTransportation());
		}
		
		
		
		
		
		
		
		//ticket
		if(isSupportTicket())
		{
			findViewById(R.id.ticket_group).setVisibility(View.VISIBLE);
			TextView ticket = (TextView) findViewById(R.id.ticket);			
			ticket.setText(place.getPriceDescription());
		}
		
		//food
		if(isSupportFood())
		{
			findViewById(R.id.food_group).setVisibility(View.VISIBLE);
			TextView food = (TextView) findViewById(R.id.food);			
			food.setText(AppManager.getInstance().getAllSubCatMap().get(place.getSubCategoryId()));
		}
		
		//openTime
		if(isSupportOpenTime())
		{
			findViewById(R.id.open_time_group).setVisibility(View.VISIBLE);
			if(place.getCategoryId()!=PlaceCategoryType.PLACE_SPOT_VALUE)
			{
				TextView openTimeTitle = (TextView) findViewById(R.id.open_time_title);
				openTimeTitle.setText(getString(R.string.open_time1));
			}
			
			TextView openTime = (TextView) findViewById(R.id.open_time);			
			openTime.setText(place.getOpenTime());
		}
		//avgPrice
		if(isSupportAvgPrice())
		{
			findViewById(R.id.avg_price_group).setVisibility(View.VISIBLE);
			TextView avgPrice = (TextView) findViewById(R.id.avg_price);			
			StringBuffer symbol = new StringBuffer(AppManager.getInstance().getSymbolMap().get(AppManager.getInstance().getCurrentCityId()));
			avgPrice.setText(symbol+place.getAvgPrice());
		}
		
		//specialFood
		if(isSupportSpecialFood())
		{
			findViewById(R.id.special_food_group).setVisibility(View.VISIBLE);
			TextView specialFood = (TextView) findViewById(R.id.special_food);		
			StringBuffer typicalDishes = new StringBuffer();
			for(String typcial:place.getTypicalDishesList())
			{
				typicalDishes.append(typcial);
				typicalDishes.append("  ");
			}
			specialFood.setText(typicalDishes);
		}
				
		//tips
		if(isSupportTips())
		{
			findViewById(R.id.tips_group).setVisibility(View.VISIBLE);
			TextView tipsTitles = (TextView) findViewById(R.id.tips_title);
			TextView tips = (TextView) findViewById(R.id.tips);
			tipsTitles.setText(tipsTitle);
			tips.setText(place.getTips());
		}
		
		//park
				if(isSupportPark())
				{
					findViewById(R.id.park_group).setVisibility(View.VISIBLE);
					TextView park = (TextView) findViewById(R.id.park);
					park.setText(place.getParkingGuide());
				}
				
		//hotelStart
		if(isSupportHotelStart())
		{
			findViewById(R.id.hotel_start_group).setVisibility(View.VISIBLE);
			TextView hotelStart = (TextView) findViewById(R.id.hotel_start);
			hotelStart.setText(TravelUtil.getHotelStar(this,place.getHotelStar()));
			ViewGroup hotelStartImageGroup = (ViewGroup) findViewById(R.id.hotel_start_image);
			for(int i=0;i<place.getHotelStar();i++)
			{
				
				 ImageView hotelStartImage = new ImageView(CommonPlaceDetailActivity.this);  
				 hotelStartImage.setLayoutParams(new LayoutParams(new LayoutParams((int)this.getResources().getDimension(R.dimen.service_icon),android.view.WindowManager.LayoutParams.WRAP_CONTENT)));  
				 //serviceImageView.setPadding(10, 0, 10, 0);  
				 hotelStartImage.setScaleType(ScaleType.FIT_CENTER);
				 hotelStartImage.setImageResource(R.drawable.star_ico);
				 hotelStartImageGroup.addView(hotelStartImage);
			}
		}
		
		//keyword
		if(isSupportKeyWords())
		{
			findViewById(R.id.keyword_group).setVisibility(View.VISIBLE);
			TextView keyword = (TextView) findViewById(R.id.place_keyword);
			StringBuffer keywordStr = new StringBuffer();
			for(String key:place.getKeywordsList())
			{
				keywordStr.append(key);
				keywordStr.append("、");
			}
			keyword.setText(keywordStr.substring(0, keywordStr.length()-1));
		}
		
		
		//roomprice
		if(isSupportRoomPrice())
		{
			findViewById(R.id.room_price_group).setVisibility(View.VISIBLE);
			TextView roomPrice = (TextView) findViewById(R.id.room_price);
			StringBuffer symbol = new StringBuffer(AppManager.getInstance().getSymbolMap().get(AppManager.getInstance().getCurrentCityId()));
			symbol.append(place.getPrice());
			symbol.append("起");
			roomPrice.setText(symbol);
		}
		
		ImageView recommendImage1 = (ImageView) findViewById(R.id.place_detail_recommend_image1);
		ImageView recommendImage2 = (ImageView) findViewById(R.id.place_detail_recommend_image2);
		ImageView recommendImage3 = (ImageView) findViewById(R.id.place_detail_recommend_image3);
				
		TextView placeDetailTitle = (TextView) findViewById(R.id.place_detail_title);
		TextView placeIntroTitle = (TextView) findViewById(R.id.place_intro_title);
		TextView placeIntro = (TextView) findViewById(R.id.place_intro);
		placeDetailTitle.setText(place.getName());
		placeIntroTitle.setText(getPlaceIntroTitle());
		placeIntro.setText("	"+place.getIntroduction());
		
		
		int rank = place.getRank();
		switch (rank)
		{
		case 1:{
			recommendImage1.setImageDrawable(this.getResources().getDrawable(R.drawable.good));
			recommendImage2.setImageDrawable(this.getResources().getDrawable(R.drawable.good2));
			recommendImage3.setImageDrawable(this.getResources().getDrawable(R.drawable.good2));
			}
			break;
		case 2:{
			recommendImage1.setImageDrawable(this.getResources().getDrawable(R.drawable.good));
			recommendImage2.setImageDrawable(this.getResources().getDrawable(R.drawable.good));
			recommendImage3.setImageDrawable(this.getResources().getDrawable(R.drawable.good2));
			}
			break;
		case 3:{
			recommendImage1.setImageDrawable(this.getResources().getDrawable(R.drawable.good));
			recommendImage2.setImageDrawable(this.getResources().getDrawable(R.drawable.good));
			recommendImage3.setImageDrawable(this.getResources().getDrawable(R.drawable.good));
			}
		break;
		default:
			break;
		}
		
		
		if(place.getTelephoneList().size()>0)
		{
			ViewGroup phoneGroup = (ViewGroup) findViewById(R.id.phone_group);
			phoneNum = (TextView) findViewById(R.id.phone_num);
			phoneNum.setSelected(true);
			ImageView phoneCall = (ImageView) findViewById(R.id.phone_call);
			phoneGroup.setVisibility(View.VISIBLE);
			StringBuffer phoneNumber = new StringBuffer();
			for(String telephone:place.getTelephoneList())
			{
				phoneNumber.append(telephone);
				phoneNumber.append(" ");
			}
			phoneNum.setText(getString(R.string.phone_number)+phoneNumber);
			phoneCall.setOnClickListener(phoneCallOnClickListener);
			phoneGroup.setOnClickListener(phoneCallOnClickListener);
		}
		
		if(place.getAddressList().size()>0)
		{
			ViewGroup addressGroup = (ViewGroup)findViewById(R.id.address_group);
			TextView address = (TextView) findViewById(R.id.address);
			address.setSelected(true);
			ImageView addressMapView = (ImageView) findViewById(R.id.address_map_view);
			addressGroup.setVisibility(View.VISIBLE);
			StringBuffer addressStr = new StringBuffer();
			for(String addres:place.getAddressList())
			{
				addressStr.append(addres);
				addressStr.append(" ");
			}
			address.setText(getString(R.string.address)+addressStr);
			addressMapView.setOnClickListener(addressLocateOnClickListener);
			addressGroup.setOnClickListener(addressLocateOnClickListener);
		}
		
		if(place.getWebsite()!=null &&!place.getWebsite().equals(""))
		{
			ViewGroup websiteGroup = (ViewGroup) findViewById(R.id.website_group);
			websiteGroup.setVisibility(View.VISIBLE);
			TextView website = (TextView) findViewById(R.id.website);
			website.setText(getString(R.string.website)+place.getWebsite());
		}
		//placeDetailGroup = (ViewGroup) findViewById(R.id.place_detail_group);
		nearbyListGroup = (ViewGroup) findViewById(R.id.nearby_list_group);
		favoriteCount = (TextView) findViewById(R.id.favorite_count);
		collect = (TextView) findViewById(R.id.collect);
		coolectBtn = (ImageView) findViewById(R.id.collect_btn);
		coolectBtn.setOnClickListener(addFavoriteOnClickListener);
		ImageButton locationButton = (ImageButton) findViewById(R.id.location_button);
		ImageView help2Button = (ImageView) findViewById(R.id.help2);
		locationButton.setOnClickListener(locationOnClickListener);
		help2Button.setOnClickListener(helpOnClickListener);
	  }
	}
	
	private OnPageChangeListener scenecyImageListener  = new OnPageChangeListener()
	{
		
		@Override
		public void onPageSelected(int arg0)
		{
			
			
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0].setBackgroundResource(R.drawable.guide_dot_white);
				if (arg0 != i) {
					imageViews[i].setBackgroundResource(R.drawable.guide_dot_black);
				}
			}
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0)
		{
			// TODO Auto-generated method stub		
		}
		
	};


	private OnClickListener phoneCallOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String phoneNumber = (String)phoneNum.getText();
			if(phoneNumber.trim()!=""||!phoneNumber.trim().equals(""))
			{
				makePhoneCall(phoneNumber);
			}	
		}
	};
	
	private OnClickListener addressLocateOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			openGPSSettings();
			Intent intent = new Intent();
			intent.putExtra(ConstantField.PLACE_DETAIL, place.toByteArray());
			intent.setClass(CommonPlaceDetailActivity.this, NearbyPlaceMap.class);
			startActivity(intent);		
		}
	};
	
	public static Class getClassByPlaceType(int categoryId)
	{
		switch (categoryId)
		{
		case PlaceCategoryType.PLACE_SPOT_VALUE:
			return CommonSpotDetailActivity.class;
		case PlaceCategoryType.PLACE_HOTEL_VALUE:
			return CommonHotelDetailActivity.class;
		case PlaceCategoryType.PLACE_RESTRAURANT_VALUE:
			return CommonRestaurantDetailActivity.class;
		case PlaceCategoryType.PLACE_SHOPPING_VALUE:
			return CommonShoppingDetailActivity.class;
		case PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE:
			return CommonEntertainmentDetailActivity.class;
		}
		return null;
	}

	public void makePhoneCall( final String phoneNumber)
	{
		AlertDialog phoneCall = new AlertDialog.Builder(CommonPlaceDetailActivity.this).create();
		phoneCall.setMessage(getResources().getString(R.string.make_phone_call)+"\n"+phoneNumber);
		phoneCall.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.call),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+phoneNumber));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				CommonPlaceDetailActivity.this.startActivity(intent);
			}
		} );
		phoneCall.setButton(DialogInterface.BUTTON_NEGATIVE,""+getBaseContext().getString(R.string.cancel),new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				
			}
		} );
		phoneCall.show();
	}

	private void getNearbyList()
	{
		 nearbyAsyncTask = new AsyncTask<Void, Void, List<Place>>()
		{

			@Override
			protected List<Place> doInBackground(Void... params)
			{				
				return PlaceMission.getInstance().getPlaceNearby(place,10);
			}

			@Override
			protected void onPostExecute(List<Place> result)
			{
				super.onPostExecute(result);
				if(nearbyPlaceList.size()>0)
				{
					nearbyPlaceList.clear();
				}			
				nearbyPlaceList.addAll(result);
				int size = 0;
				if(result.size()>10)
				{
					size = 10;
				}else {
					size = result.size();
				}
				for(int i=0;i<size;i++)
				{
					View nearbyListItemView = getLayoutInflater().inflate(R.layout.nearby_list_item, null);
					nearbyListItemView.setTag(i);
					nearbyListItemView.setOnClickListener(nearbyListItemOnClickListener);
					nearbyListItemView.setLayoutParams(new LayoutParams((int)getResources().getDimension(R.dimen.nearby_list_width), (int)getResources().getDimension(R.dimen.nearby_list_height)));
					if(i == 0)
					{
						nearbyListItemView.setBackgroundDrawable(getResources().getDrawable(R.drawable.table4_top));
					}else if (i == 9) {
						nearbyListItemView.setBackgroundDrawable(getResources().getDrawable(R.drawable.table4_down));
					}else {
						nearbyListItemView.setBackgroundDrawable(getResources().getDrawable(R.drawable.table4_center));
					}
					Place placeItem = result.get(i);
					
					ImageView placeCategoryImage = (ImageView) nearbyListItemView.findViewById(R.id.place_category);
					int placeCategoryIcon = TravelUtil.getPlaceCategoryImage(placeItem.getCategoryId());
					placeCategoryImage.setImageDrawable(getResources().getDrawable(placeCategoryIcon));
					
					TextView placeName = (TextView) nearbyListItemView.findViewById(R.id.place_name);;
					TextView distance = (TextView) nearbyListItemView.findViewById(R.id.place_distance);
					
					
					placeName.setText(placeItem.getName());
					String distanceStr = TravelUtil.getDistance(placeItem.getLongitude(), placeItem.getLatitude(),place.getLongitude(),place.getLatitude());
					distance.setText(distanceStr);
					
					ImageView recommendImageView1 = (ImageView) nearbyListItemView.findViewById(R.id.place_detail_recommend_image1);
					ImageView recommendImageView2 = (ImageView) nearbyListItemView.findViewById(R.id.place_detail_recommend_image2);
					ImageView recommendImageView3 =(ImageView) nearbyListItemView.findViewById(R.id.place_detail_recommend_image3);
					int rank = placeItem.getRank();
					switch (rank)
					{
					case 1:
						recommendImageView1.setVisibility(View.VISIBLE);		
						break;
					case 2:
						recommendImageView1.setVisibility(View.VISIBLE);
						recommendImageView2.setVisibility(View.VISIBLE);
						break;
					case 3:
						recommendImageView1.setVisibility(View.VISIBLE);
						recommendImageView2.setVisibility(View.VISIBLE);
						recommendImageView3.setVisibility(View.VISIBLE);
					break;
					
					}					
					nearbyListGroup.addView(nearbyListItemView);
				}
				
			}
			
		};
		nearbyAsyncTask.execute();
	}
	

	private OnClickListener nearbyListItemOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			int position = (Integer)v.getTag();
			Place nearbyPlace = nearbyPlaceList.get(position);
			Intent intent = new Intent();
			intent.putExtra(ConstantField.PLACE_DETAIL, nearbyPlace.toByteArray());
			Class activity = getClassByPlaceType(nearbyPlace.getCategoryId());
			intent.setClass(CommonPlaceDetailActivity.this, activity);
			//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}; 
	
	private void getPlaceFavoriteCount()
	{
		AsyncTask< Void, Void, Integer> asyncTask = new AsyncTask<Void, Void, Integer>()
		{

			@Override
			protected Integer doInBackground(Void... params)
			{
				Integer count = FavoriteMission.getInstance().getFavoriteCount(place.getPlaceId());
				return count;
			}

			@Override
			protected void onPostExecute(Integer result)
			{
				String favoriteCountStr = String.format(ConstantField.FAVORITE_COUNT_STR, result);
				favoriteCount.setText(favoriteCountStr);
				if(FavoriteMission.getInstance().checkPlaceIsCollected(place.getPlaceId()))
				{
					collect.setText(R.string.collected);
					coolectBtn.setClickable(false);
				}
				super.onPostExecute(result);
			}};
			asyncTask.execute();
	}
	
	
	private void addFavorite(Place place)
	{
		int reulst = FavoriteMission.getInstance().addFavorite(UserManager.getInstance().getUserId(this),place);
		Toast toast;
		toast = Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT);
		if(reulst == 0)
		{			
			collect.setText(R.string.collected);
			coolectBtn.setClickable(false);
			getPlaceFavoriteCount();
		}else {
			toast = Toast.makeText(this, "收藏失败", Toast.LENGTH_SHORT);
		}
		toast.show();
	}
	
	
	private OnClickListener addFavoriteOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			addFavorite(place);
			
		}
	};
	
	
	private OnClickListener helpOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent  intent = new Intent();
			intent.putExtra(ConstantField.HELP_TITLE, getResources().getString(R.string.help));
			intent.setClass(CommonPlaceDetailActivity.this, HelpActiviy.class);
			startActivity(intent);
			
		}
	};
	
	private OnClickListener locationOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			openGPSSettings();
			Intent intent = new Intent();
			intent.putExtra(ConstantField.PLACE_DETAIL, place.toByteArray());
			intent.setClass(CommonPlaceDetailActivity.this, NearbyPlaceMap.class);
			startActivity(intent);	
			
		}
	};
	
	
	
	
	private void openGPSSettings() {

		LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return;
		}
			Toast.makeText(this, getString(R.string.open_gps_tips), Toast.LENGTH_SHORT).show();
	}
	
}
