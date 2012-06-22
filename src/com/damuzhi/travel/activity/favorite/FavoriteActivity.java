/**  
        * @title CollectActivity.java  
        * @package com.damuzhi.travel.activity.collect  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-21 下午3:31:29  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.favorite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.favorite.FavoriteAdapter;
import com.damuzhi.travel.activity.adapter.place.NearbyPlaceAdapter;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.place.CommonPlaceDetailActivity;
import com.damuzhi.travel.activity.place.NearbyPlaceActivity;
import com.damuzhi.travel.mission.favorite.FavoriteMission;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.downlaod.DownloadManager;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.TravelUtil.ComparatorDistance;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.MapView.LayoutParams;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-21 下午3:31:29  
 */

public class FavoriteActivity extends MenuActivity
{
	private static final String TAG = "Nearby";
	private int startPosition = 1;
	private float offset;
	private int bmpW;//
	private int screenW;
	private TextView allPlace;
	private TextView spot;
	private TextView hotel;
	private TextView restaurant;
	private TextView shopping;
	private TextView entertrainment;
	private TextView move;
	private ViewGroup myFavoriteGroup,favoriteRankGroup;
	private TextView myFavoriteTitle,favoriteRankTitle;
	private ImageView delete;
	private int tabStartPosition = 0;
	private ListView listView;
	private int startLeft = 0; 
	private ProgressDialog loadingDialog;
	private List<Place> favoritePlaceList = Collections.emptyList();
	private FavoriteAdapter adapter;
	long lasttime = -1;
	private int currentPlaceCategory = ConstantField.ALL_PLACE_CATEGORY_ID;
	private int favoriteConfigure = 0;//0:myFavorite,1:favoriteRank
	private static final int MY_FAVORITE = 0;
	private static final int FAVORITE_RANK = 1;
	private boolean isShowDeleteBtn = false;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite);
		loadingDialog = new ProgressDialog(this);
		init();
		loadFavorite(favoriteConfigure,currentPlaceCategory);
	}
	
	public void init()
	{
		myFavoriteGroup = (ViewGroup) findViewById(R.id.my_favorite_group);
		favoriteRankGroup = (ViewGroup) findViewById(R.id.favorite_rank_group);
		myFavoriteTitle = (TextView) findViewById(R.id.my_favorite_title);
		favoriteRankTitle = (TextView) findViewById(R.id.favorite_rank_title);
		delete = (ImageView) findViewById(R.id.delete_button);
		myFavoriteGroup.setOnClickListener(myFavoriteOnClickListener);
		favoriteRankGroup.setOnClickListener(favoriteRankOnClickListener);
		delete.setOnClickListener(deleteOnClickListener);
		listView = (ListView) findViewById(R.id.collect_list);
		listView.setOnItemClickListener(listviewOnItemClickListener);	
		
		allPlace = (TextView) findViewById(R.id.place_all);
		spot = (TextView) findViewById(R.id.place_spot);
		hotel = (TextView) findViewById(R.id.place_hotel);
		restaurant = (TextView) findViewById(R.id.place_restaurant);
		shopping = (TextView) findViewById(R.id.place_shopping);
		entertrainment = (TextView) findViewById(R.id.place_entertrainment);	
		
		
		allPlace.setOnClickListener(allPlaceOnClickListener);
		spot.setOnClickListener(spotOnClickListener);
		hotel.setOnClickListener(hotelOnClickListener);
		restaurant.setOnClickListener(restaurantOnClickListener);
		shopping.setOnClickListener(shoppingOnClickListener);
		entertrainment.setOnClickListener(entertrainmnetOnClickListener);			
		move = (TextView) findViewById(R.id.move);
		move.setTextColor(getResources().getColor(R.color.white));
		
		adapter = new FavoriteAdapter(this, favoritePlaceList);
		listView.setAdapter(adapter);
	}

	
	
	
	public void loadFavorite(int favoriteConfig,int placeCategoryId)
	{
		Integer config[] = new Integer[]{favoriteConfig,placeCategoryId};
		AsyncTask<Integer, Void, List<Place>> task = new AsyncTask<Integer, Void, List<Place>>()
		{

			@Override
			protected List<Place> doInBackground(Integer... params)
			{
				int favoriteConfig =  params[0];
				int categoryId = params[1];
				if(favoriteConfig == MY_FAVORITE)
				{
					if(categoryId == ConstantField.ALL_PLACE_CATEGORY_ID )
					{
						return FavoriteMission.getInstance().getMyFavorite();
					}else
					{
						return FavoriteMission.getInstance().getMyFavorite(categoryId);
					}
				}else if (favoriteConfig == FAVORITE_RANK)
				{
					return FavoriteMission.getInstance().getFavorite(categoryId);
				}else {
					return Collections.emptyList();
				}
				
				
			}

			@Override
			protected void onCancelled()
			{
				super.onCancelled();
			}

			@Override
			protected void onPostExecute(List<Place> resultList)
			{
				loadingDialog.dismiss();
				favoritePlaceList = resultList;	
				refreshPlaceView(favoritePlaceList);
				if(favoritePlaceList.size()>0)
				{
					findViewById(R.id.page).setVisibility(View.VISIBLE);					
				}else
				{
					findViewById(R.id.data_not_found).setVisibility(View.VISIBLE);
				}
				super.onPostExecute(resultList);
			}

			@Override
			protected void onPreExecute()
			{
				showRoundProcessDialog();
				super.onPreExecute();
			}

		};

		task.execute(config);
		
	}
	
	
	
	private void refreshPlaceView(List<Place> list)
	{
		List<Place> origList = new ArrayList<Place>();
		origList.addAll(list);
		adapter.setList(origList);
		adapter.setShowDeleteBtn(isShowDeleteBtn);
		adapter.notifyDataSetChanged();		

	}
	
	
	
	private OnClickListener myFavoriteOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			favoriteConfigure = MY_FAVORITE;
			currentPlaceCategory = ConstantField.ALL_PLACE_CATEGORY_ID;
			myFavoriteGroup.setBackgroundResource(R.drawable.citybtn_on);
			favoriteRankGroup.setBackgroundResource(R.drawable.citybtn_off2);
			myFavoriteTitle.setTextColor(getResources().getColor(R.color.white));
			favoriteRankTitle.setTextColor(getResources().getColor(R.color.black));
			
			isShowDeleteBtn = false;
			
			
			getStartPosition(tabStartPosition);
			float endLeft = allPlace.getWidth()*0;
			move.setText("");
			tabStartPosition = 11;
			TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
			animation.setDuration(300);
			animation.setFillAfter(true);
			move.bringToFront();
			move.startAnimation(animation);					
			move.setText(setEndPosition(startPosition));
			
			loadFavorite(favoriteConfigure,currentPlaceCategory);
		}
	};
	
	
	
	private OnClickListener favoriteRankOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{

			favoriteConfigure = FAVORITE_RANK;
			currentPlaceCategory = Integer.parseInt(ConstantField.ALL_PLACE_ORDER_BY_RANK);
			delete.setVisibility(View.GONE);
			myFavoriteGroup.setBackgroundResource(R.drawable.citybtn_off);
			favoriteRankGroup.setBackgroundResource(R.drawable.citybtn_on2);
			myFavoriteTitle.setTextColor(getResources().getColor(R.color.black));
			favoriteRankTitle.setTextColor(getResources().getColor(R.color.white));
			
			isShowDeleteBtn = false;
			
			
					
			getStartPosition(tabStartPosition);
			float endLeft = allPlace.getWidth()*0;
			move.setText("");
			tabStartPosition = 11;
			TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
			animation.setDuration(300);
			animation.setFillAfter(true);
			move.bringToFront();
			move.startAnimation(animation);					
			move.setText(setEndPosition(startPosition));
			
			loadFavorite(favoriteConfigure,currentPlaceCategory);
		}
	};
	
	
	private OnClickListener deleteOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			if(isShowDeleteBtn)
			{
				isShowDeleteBtn = false;
			}else
			{
				isShowDeleteBtn = true;
			}		
			refreshPlaceView(favoritePlaceList);
		}
	};

	 
	
				
				
		 private OnClickListener allPlaceOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					if(favoriteConfigure == MY_FAVORITE)
					{
						currentPlaceCategory = ConstantField.ALL_PLACE_CATEGORY_ID;
					}else if (favoriteConfigure == FAVORITE_RANK)
					{
						currentPlaceCategory = Integer.parseInt(ConstantField.ALL_PLACE_ORDER_BY_RANK);
					}					
					loadFavorite(favoriteConfigure,currentPlaceCategory);
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*0;
					move.setText("");
					tabStartPosition = 11;
					TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
					animation.setDuration(300);
					animation.setFillAfter(true);
					move.bringToFront();
					move.startAnimation(animation);					
					move.setText(setEndPosition(startPosition));
				}
			};
			
			private OnClickListener spotOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					
					if(favoriteConfigure == MY_FAVORITE)
					{
						currentPlaceCategory = PlaceCategoryType.PLACE_SPOT_VALUE;
					}else if (favoriteConfigure == FAVORITE_RANK)
					{
						currentPlaceCategory = Integer.parseInt(ConstantField.ALL_SCENERY_ORDER_BY_RANK);
					}
					loadFavorite(favoriteConfigure,currentPlaceCategory);
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*1;
					move.setText("");
					tabStartPosition = 12;
					TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
					animation.setDuration(300);
					animation.setFillAfter(true);
					move.bringToFront();
					move.startAnimation(animation);					
					move.setText(setEndPosition(startPosition));
						
				}
			};
			
			private OnClickListener hotelOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					if(favoriteConfigure == MY_FAVORITE)
					{
						currentPlaceCategory = PlaceCategoryType.PLACE_HOTEL_VALUE;
					}else if (favoriteConfigure == FAVORITE_RANK)
					{
						currentPlaceCategory = Integer.parseInt(ConstantField.ALL_HOTEL_ORDER_BY_RANK);
					}
					loadFavorite(favoriteConfigure,currentPlaceCategory);
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*2;
					move.setText("");
					tabStartPosition = 13;
					TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
					animation.setDuration(300);
					animation.setFillAfter(true);
					move.bringToFront();
					move.startAnimation(animation);					
					move.setText(setEndPosition(startPosition));
						
				}
			};
			
			private OnClickListener restaurantOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					if(favoriteConfigure == MY_FAVORITE)
					{
						currentPlaceCategory = PlaceCategoryType.PLACE_RESTRAURANT_VALUE;
					}else if (favoriteConfigure == FAVORITE_RANK)
					{
						currentPlaceCategory = Integer.parseInt(ConstantField.ALL_RESTAURANT_ORDER_BY_RANK);
					}
					loadFavorite(favoriteConfigure,currentPlaceCategory);
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*3;
					move.setText("");
					tabStartPosition = 14;
					TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
					animation.setDuration(300);
					animation.setFillAfter(true);
					move.bringToFront();
					move.startAnimation(animation);					
					move.setText(setEndPosition(startPosition));
						
				}
			};
			
			
			private OnClickListener shoppingOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					if(favoriteConfigure == MY_FAVORITE)
					{
						currentPlaceCategory = PlaceCategoryType.PLACE_SHOPPING_VALUE;
					}else if (favoriteConfigure == FAVORITE_RANK)
					{
						currentPlaceCategory = Integer.parseInt(ConstantField.ALL_SHOPPING_ORDER_BY_RANK);
					}
					loadFavorite(favoriteConfigure,currentPlaceCategory);
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*4;
					move.setText("");
					tabStartPosition = 15;
					TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
					animation.setDuration(300);
					animation.setFillAfter(true);
					move.bringToFront();
					move.startAnimation(animation);					
					move.setText(setEndPosition(startPosition));
						
				}
			};
			
			
			private OnClickListener entertrainmnetOnClickListener = new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					if(favoriteConfigure == MY_FAVORITE)
					{
						currentPlaceCategory = PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE;
					}else if (favoriteConfigure == FAVORITE_RANK)
					{
						currentPlaceCategory = Integer.parseInt(ConstantField.ALL_FUN_ORDER_BY_RANK);
					}
					loadFavorite(favoriteConfigure,currentPlaceCategory);
					getStartPosition(tabStartPosition);
					float endLeft = allPlace.getWidth()*5;
					move.setText("");
					tabStartPosition = 16;
					TranslateAnimation animation = new TranslateAnimation(startLeft, endLeft, 0, 0); 
					animation.setDuration(300);
					animation.setFillAfter(true);
					move.bringToFront();
					move.startAnimation(animation);					
					move.setText(setEndPosition(startPosition));
						
				}
			};
	 
		
			
	
	
	
	


	
	
	
	private OnItemClickListener listviewOnItemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			Place place = favoritePlaceList.get(arg2);
			Intent intent = new Intent();
			intent.putExtra(ConstantField.PLACE_DETAIL, place.toByteArray());
			Class detailPlaceClass = CommonPlaceDetailActivity.getClassByPlaceType(place.getCategoryId());
			intent.setClass(FavoriteActivity.this, detailPlaceClass);
			startActivity(intent);
			
		}
	};
			
	
	
	
	
	
	private void getStartPosition(int tabStartPosition) {
	switch (tabStartPosition)
	{
	case 11:
		startLeft = allPlace.getWidth()*0;
		break;
	case 12:
		startLeft = allPlace.getWidth()*1;
		break;
	case 13:
		startLeft = allPlace.getWidth()*2;
		break;
	case 14:
		startLeft = allPlace.getWidth()*3;
		break;
	case 15:
		startLeft = allPlace.getWidth()*4;
		break;
	case 16:
		startLeft = allPlace.getWidth()*5;
		break;
	default:
		break;
	}
	}
	
	
	private String setEndPosition(int tabStatPosition) {
		String text = "";
		switch (tabStartPosition)
		{
		case 11:
			text = getString(R.string.all_place);
			break;
		case 12:
			text = getString(R.string.scenery);
			break;
		case 13:
			text = getString(R.string.hotel);
			break;
		case 14:
			text = getString(R.string.restaurant);
			break;
		case 15:
			text = getString(R.string.shopping);
			break;
		case 16:
			text = getString(R.string.entertainment);
			break;
		default:
			break;
		}
		return text;
	}
	
	
	public void showRoundProcessDialog()
	{

		OnKeyListener keyListener = new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getRepeatCount() == 0)
				{
					loadingDialog.dismiss();
					Intent intent = new Intent(FavoriteActivity.this,IndexActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					return true;
				} else
				{
					return false;
				}
			}
		};

		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingDialog.setMessage(getResources().getString(R.string.loading));
		loadingDialog.setIndeterminate(false);
		loadingDialog.setCancelable(true);
		loadingDialog.setOnKeyListener(keyListener);
		loadingDialog.show();
	}
	
	
	
	
	
}
