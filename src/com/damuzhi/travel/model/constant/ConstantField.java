package com.damuzhi.travel.model.constant;

import java.io.File;
import java.security.PublicKey;

public class ConstantField
{
	/*local data path*/
	public static final String LOCAL_APP_DATA_PATH = android.os.Environment.getDataDirectory() + "/app.dat";
	public static final String LOCAL_APP_DATA_FILE = "/data/data/com.damuzhi.travel/files/" + "/app.dat";
	public static final String APP_DATA_TEMP_FILE = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/temp/temp.dat";
	public static final String APP_DATA_PATH = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/app/";
	public static final String DATA_PATH = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/%s/";
	public static final String IMAGE_PATH = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/%s/data/";
	public static final String APP_DATA_FILE = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/app/app.dat";
	public static final String APP_DATA_TEMP_PATH = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/temp";
	
	public static final String PLACE_PATH ="/data/place/";
	public static final int DATA_LOCAL = 0;
	public static final int DATA_HTTP = 1;
	public static final int DISTANCE = 10000;
	
	/*help data */
	public static final String LOCAL_HELP_DATA_PATH = android.os.Environment.getDataDirectory() + "/help.dat";
	public static final String HELP_DATA_FILE = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/help/help.dat";
	public static final String HELP_PATH = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/help/";
	public static final String HELP_HTML_FILE_PATH = "file:///mnt/sdcard/damuzhi/data/help/helpinfo.html";
	public static final String HELP_HTML_FILE = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/help/helpinfo.html";
	public static final String HELP_JPG_FILE = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/help/help.jpg";
	public static final String HELP_DATA_TEMP_FILE = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/temp/Helptemp.dat";
	public static final String HELP_DATA_ZIP_FILE = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/help/help.zip";
	/*local file*/
	public static final String LAST_CITY_ID = "last_city_id";
	public static final String APP_FILE = "app.dat";
	public static final String APP_TEMP_FILE = "temp.dat";
	public static final String HELP_TEMP_FILE = "Helptemp.dat";
	public static final String HELP_FILE = "help.dat";
	public static final String LOCAL_HELP_HTML_FILE = "helpinfo.html";
	public static final String LOCAL_HELP_JPG_FILE = "help.jpg";
	public static final String HELP_ZIP_FILE = "help.zip";
	public static final String PACKAGE_FILE = "package.dat";
	public static final String PLACE_TAG = "place";
	public static final String GUIDE_TAG = "guide";
	public static final String PACKAGE_TAG = "package";
	public static final String OVERVIEW_TAG = "overview";
	public static final String ROUTE_TAG = "route";
	public static final String EXTENSION = ".dat"; 
	public static final String CHECK_NET = "com.damuzhi.travel.network.CheckNet";
	public static final String UTF ="UTF-8";
	
	/*location*/
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String CITY = "city";
	
	
	
	/* http data url */
	public static final String FORMAT_HTTP_URL = "http://api.trip8888.com/";
	public static final String TEST_HTTP_URL = "http://59.34.17.68:8012/";
	
	public static final String PLACE_INFO = "http://api.trip8888.com/service/queryPlace.aspx?userId=%s&placeId=%s";
	public static final String FEED_BACK = "http://api.trip8888.com/service/feedback.aspx?userId=%s&contact=%s&content=%s";
	public static final String OVERVIEW = "http://api.trip8888.com/service/queryObject.aspx?type=%s&id=%s&lang=%s";
	public static final String PLACElIST = "http://api.trip8888.com/service/queryList.aspx?type=%s&cityId=%s&lang=%s";
	public static final String PLACE_LIST_NEARBY = "http://api.trip8888.com/service/queryList.aspx?type=%s&cityId=%s&placeId=%s&latitude=%s&longitude=%s&num=%s&distance=%s&lang=%s&os=%s";
	public static final String APP = "http://api.trip8888.com/service/queryList.aspx?type=10&lang=%s&os=2";
	public static final String HELP = "http://api.trip8888.com/service/queryObject.aspx?type=8&lang=%s";
	public static final String ANDROID_VERSION = "http://api.trip8888.com/service/androidVersion.txt";
	
	
	/*public static final String PLACE_INFO = TEST_HTTP_URL+"service/queryPlace.aspx?userId=%s&placeId=%s";
	public static final String FEED_BACK = TEST_HTTP_URL+"service/feedback.aspx?userId=%s&contact=%s&content=%s";
	public static final String OVERVIEW = TEST_HTTP_URL+"service/queryObject.aspx?type=%s&id=%s&lang=%s";
	public static final String PLACElIST = TEST_HTTP_URL+"service/queryList.aspx?type=%s&cityId=%s&lang=%s";
	public static final String PLACE_LIST_NEARBY = TEST_HTTP_URL+"service/queryList.aspx?type=%s&cityId=%s&placeId=%s&latitude=%s&longitude=%s&num=%s&distance=%s&lang=%s&os=%s";
	public static final String APP = TEST_HTTP_URL+"service/queryList.aspx?type=10&lang=%s&os=2";
	public static final String HELP = TEST_HTTP_URL+"service/queryObject.aspx?type=8&lang=%s";
	public static final String ANDROID_VERSION = TEST_HTTP_URL+"service/androidVersion.txt";*/
	
	
	/* page url*/
	public static final String PLACE_PAGE_URL = "http://api.trip8888.com/service/queryList.aspx?type=%s&cityId=%s&start=%s&count=%s&needStatistics=1&lang=%s&os=2";
	public static final String PLACE_PAGE_LOAD_MORE_URL = "http://api.trip8888.com/service/queryList.aspx?type=%s&cityId=%s&subcategoryId=%s&areaId=%s&serviceId=%s&priceRankId=%s&sortType=%s&start=%s&count=%s&needStatistics=1&lang=%s&os=2";
	public static final String PLACE_PAGE_FILTER_URL = "http://api.trip8888.com/service/queryList.aspx?type=%s&cityId=%s&subcategoryId=%s&areaId=%s&serviceId=%s&priceRankId=%s&sortType=%s&start=%s&count=%s&needStatistics=1&lang=%s&os=2";
	

	/*public static final String PLACE_PAGE_URL = "http://59.34.17.68:8012/service/queryList.aspx?type=%s&cityId=%s&start=%s&count=%s&needStatistics=1&lang=%s&os=2";
	public static final String PLACE_PAGE_LOAD_MORE_URL = "http://59.34.17.68:8012/service/queryList.aspx?type=%s&cityId=%s&subcategoryId=%s&areaId=%s&serviceId=%s&priceRankId=%s&sortType=%s&start=%s&count=%s&needStatistics=1&lang=%s&os=2";
	public static final String PLACE_PAGE_FILTER_URL = "http://59.34.17.68:8012/service/queryList.aspx?type=%s&cityId=%s&subcategoryId=%s&areaId=%s&serviceId=%s&priceRankId=%s&sortType=%s&start=%s&count=%s&needStatistics=1&lang=%s&os=2";
	*/
	
	/* http data type */
	public static final String RESULT_OK = "0";
	public static final String LANG_HANS = "1";
	public static final String LANG_HANT = "2";
	public static final String LANG_ENG ="3";
	public static final String PLACE = "1";
	public static final String CITY_BASE = "2";
	public static final String TRAVEL_PREPRATION= "3";
	public static final String TRAVEL_TRANSPORTAION = "4";
	public static final String TRAVEL_UTILITY = "5";
	public static final String TRAVEL_TIPS ="6";
	public static final String TRAVEL_ROUTE = "7";
	public static final String TRAVEL_GUIDE_LIST ="5";
	public static final String TRAVEL_ROUTE_LIST = "6";
	public static final String HELP_INFO = "8";
	public static final String OPEN_CITY_LIST = "8";
	public static final String TEST_CITY_LIST = "9";
	public static final String APP_DATA = "10";
	public static final String SPOT = "21";
	public static final String HOTEL = "22";
	public static final String RESTAURANT = "23";
	public static final String SHOPPING = "24";
	public static final String ENTERTAINMENT = "25";
	public static final String ALL_PLACE_ORDER_BY_RANK = "40";
	public static final String ALL_SCENERY_ORDER_BY_RANK = "41";
	public static final String ALL_HOTEL_ORDER_BY_RANK = "42";
	public static final String ALL_RESTAURANT_ORDER_BY_RANK = "43";
	public static final String ALL_SHOPPING_ORDER_BY_RANK = "44";
	public static final String ALL_FUN_ORDER_BY_RANK = "45";
	public static final String NEARBY_PLACE_LIST = "50";
	public static final String NEARBY_SPOT_LIST = "51";
	public static final String NEARBY_HOTEL_LIST = "52";
	public static final String NEARBY_RESTAURANT_LIST = "53";
	public static final String NEARBY_SHOPPING_LIST = "54";
	public static final String NEARBY_ENTERTRAINMENT_LIST = "55";
	public static final String NEARBY_PLACE_LIST_IN_DISTANCE = "60";
	public static final String NEARBY_SPOT_LIST_IN_DISTANCE = "61";
	public static final String NEARBY_HOTEL_LIST_IN_DISTANCE = "62";
	public static final String NEARBY_RESTAURANT_LIST_IN_DISTANCE = "63";
	public static final String NEARBY_SHOPPING_LIST_IN_DISTANCE = "64";
	public static final String NEARBY_ENTERTRAINMENT_LIST_IN_DISTANCE = "65";
	
	/*distance*/
	public static final String ALL_PLACE = "全部";
	public static final int ALL_PLACE_CATEGORY_ID = -1;
	public static final String HALF_KILOMETER = "0.5";
	public static final String ONE_KILOMETER = "1";
	public static final String FIVE_KILOMETER = "5";
	public static final String TEN_KILOMETER = "10";
	
	
	/* commonPlaceDetail*/
	public static final String PLACE_DETAIL = "PLACE_DETAIL";
	
	/* collect */
	public static final String QUERY_PLACE_FAVORITE_COUNT = "http://api.trip8888.com/service/queryPlace.aspx?userId=%s&placeId=%s";
	public static final String ADD_FAVORITE = "http://api.trip8888.com/service/addFavorite.aspx?userId=%s&placeId=%s";
	public static final String DELETE_FAVORITE = "http://api.trip8888.com/service/deleteFavorite.aspx?userId=%s&placeId=%s";
	public static final String FAVORITE_COUNT_STR = "已有%s人收藏";
	public static final String FAVORITE_FILE_PATH = android.os.Environment.getExternalStorageDirectory()+ "/damuzhi/data/favorite.dat";
	
	/* history*/
	public static final String HISTORY_FILE_PATH = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/app/history.dat";
	
	/* register*/
	public static final String REGISTER = "http://api.trip8888.com/service/registerUser.aspx?type=2&deviceId=%s";
	public static final String USER_ID = "user_id";
	
	/* download*/
	
	public static final String DOWNLOAD_TEMP_PATH = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/city/temp/";
	public static final String DOWNLOAD_CITY_DATA_FOLDER_PATH = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/city/data/";
	public static final String DOWNLOAD_CITY_ZIP_DATA_PATH = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/city/data/%s/zip/";
	public static final String DOWNLOAD_CITY_DATA_PATH = android.os.Environment.getExternalStorageDirectory() + "/damuzhi/data/city/data/%s/";
	
	public static final int DOWNLOAD_INIT = 1;
	public static final int DOWNLOAD_RESTART = 2;
	public static final int DOWNLOAD_STOP = 3;
	public static final int DOWNLOAD_DONE = 4;
	public static final int DOWNLOAD_ZIP_SUCCESS = 5;
	public static final int DOWNLOAD_SUCCESS = 6;
	public static final String TRAVEL_TIPS_INFO = "travelTipsInfo";
	public static final String TRAVEL_ROUTES_INFO = "travelRoutesInfo";
	
	
	/* more*/
	public static final String HELP_TITLE = "help_title";
	public static final String SHOW_LIST_IMAGE = "show_list_image";
	public static final String U_MENG_DOWNLOAD_CONFIGURE = "download_website_android";
	public static final String U_MENG_SINA_CONSUMER_KEY = "sina_weibo_app_key";
	public static final String U_MENG_SINA_CONSUMER_SECRET = "sina_weibo_app_secret";
	public static final String U_MENG_QQ_CONSUMER_KEY = "qq_weibo_app_key";
	public static final String U_MENG_QQ_CONSUMER_SECRET = "qq_weibo_app_secret";
	public static final String U_MENG_CALL_BACK_URL = "call_back_url";
	public static final String LOCATION_FILE = android.os.Environment.getExternalStorageDirectory()+"/damuzhi/data/";
	

}
