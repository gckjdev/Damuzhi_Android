package com.damuzhi.travel.activity.more;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.damuzhi.travel.activity.adapter.viewcache.OpenCityDownloadViewcache;
import com.damuzhi.travel.activity.adapter.viewcache.OpenCityListViewCache;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.indexSidebar.SideBar;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.entry.MainActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.db.DownloadPreference;
import com.damuzhi.travel.download.DownloadService;
import com.damuzhi.travel.download.DownloadService.DownloadServiceBinder;
import com.damuzhi.travel.mission.more.DownloadMission;
import com.damuzhi.travel.mission.more.MoreMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.downlaod.DownloadBean;
import com.damuzhi.travel.model.downlaod.DownloadManager;
import com.damuzhi.travel.model.entity.DownloadInfos;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.TravelUtil;

import dalvik.system.VMRuntime;
import com.damuzhi.travel.R;

public class OpenCityActivity extends Activity
{
	private static String TAG = "OpenCityDataActivity";
	private ListView ctiyDataListView, installedCityListView;
	private static final int PROCESS_CHANGED = 1;
	private static final int UPZIP = 2;
	private static final int CONNECTION_ERROR = 3;
	private static final int DOWNLOADING = 2;
	private static final int UPZIPING = 4;
	private int currentCityId;

	public OpenCityDataAdapter cityListAdapter;
	public InstalledCityListAdapter installedCityListAdapter;
	private List<City> cityList;
	private List<City> hotCityList;
	private List<City> searchList;
	private List<City> searchResultList = new ArrayList<City>();
	private Map<String, ProgressBar> progressBarMap = new HashMap<String, ProgressBar>();
	private Map<String, TextView> resultTextMap = new HashMap<String, TextView>();
	private Map<String, Integer> downloadStatusTask;
	public Map<Integer, Integer> installCityData;
	private Map<Integer, String> newVersionCityData;
	private Map<String, DownloadBean> unfinishDownload = new HashMap<String, DownloadBean>();
	private Map<Integer, Integer> unfinishInstallMap = new HashMap<Integer, Integer>();
	private List<Integer> installedCityList = new ArrayList<Integer>();

	private TextView cityDataTitle, installedCityTitle;
	private ViewGroup cityDataListGroup;
	private EditText citySearch;
	private DownloadManager downloadManager;
	private Map<String, ProgressBar> stopDownloadBar = new HashMap<String, ProgressBar>();
	private Map<String, TextView> stopDownloadresultTextMap = new HashMap<String, TextView>();
	private ImageButton searchButton;
	private TextView downloadTipsTextView, emptyTipsTextView;
	private ViewGroup searchBarGroup;
	private ProgressDialog loadingDialog;
	DownloadService downloadService;
	private final static float TARGET_HEAP_UTILIZATION = 0.75f;
	private WindowManager mWindowManager;
	private TextView mDialogText;
	int flag;// install city or update city flag

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);
		Log.d(TAG, "onCreate");

		setContentView(R.layout.open_city);
		ActivityMange.getInstance().addActivity(this);
		downloadManager = new DownloadManager(OpenCityActivity.this);
		currentCityId = AppManager.getInstance().getCurrentCityId();
		loadingDialog = new ProgressDialog(this);
		downloadTipsTextView = (TextView) findViewById(R.id.open_city_download_tips);
		downloadTipsTextView.setSelected(true);
		searchBarGroup = (ViewGroup) findViewById(R.id.layout_search_bar);

		citySearch = (EditText) findViewById(R.id.city_search);
		cityDataListGroup = (ViewGroup) findViewById(R.id.city_data_list_group);
		ctiyDataListView = (ListView) findViewById(R.id.open_city_data_listview);
		installedCityListView = (ListView) findViewById(R.id.download_data_listview);
		cityListAdapter = new OpenCityDataAdapter(cityList,OpenCityActivity.this);
		installedCityListAdapter = new InstalledCityListAdapter(installedCityList, this);

		View listViewFooter = getLayoutInflater().inflate(R.layout.open_data_listview_footer, null, false);
		TextView tipsTextView = (TextView) listViewFooter.findViewById(R.id.open_city_tips_update);
		SpannableString tips = new SpannableString(getString(R.string.open_city_tips2));
		tips.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0,tips.length() - 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		tipsTextView.setText(tips);

		ctiyDataListView.setAdapter(cityListAdapter);
		installedCityListView.setAdapter(installedCityListAdapter);

		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.list_position, null);
		mDialogText.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(mDialogText, lp);

		SideBar indexBar = (SideBar) findViewById(R.id.sideBar);
		indexBar.setTextView(mDialogText);
		indexBar.setListView(ctiyDataListView);

		ctiyDataListView.setOnItemClickListener(onItemClickListener);

		cityDataTitle = (TextView) findViewById(R.id.city_list_title);
		installedCityTitle = (TextView) findViewById(R.id.installed_city_title);
		/*dataListTitle = (TextView) findViewById(R.id.city_list_title);
		downloadListTitle = (TextView) findViewById(R.id.installed_city_title);*/
		searchButton = (ImageButton) findViewById(R.id.search_button);
		emptyTipsTextView = (TextView) findViewById(R.id.empty_tips);

		cityDataTitle.setOnClickListener(cityDataListOnClickListener);
		installedCityTitle.setOnClickListener(installedCityListOnClickListener);
		searchButton.setOnClickListener(searchOnClickListener);

		citySearch.addTextChangedListener(citySearchTextWatcher);

		flag = getIntent().getIntExtra("updateData", -1);
		loadData();
	}

	private void loadData()
	{
		AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{

				unfinishDownload = downloadManager.getUnfinishDownload();
				unfinishInstallMap = DownloadPreference.getAllUnfinishInstall(OpenCityActivity.this);
				cityList = AppManager.getInstance().getCityList();
				hotCityList = AppManager.getInstance().getHotCityList();
				searchList = AppManager.getInstance().getSearchCityList();
				newVersionCityData = TravelApplication.getInstance().getNewVersionCityData();

				if (installCityData == null)
				{
					installCityData = DownloadPreference.getAllDownloadInfo(OpenCityActivity.this);
				}
				TravelApplication.getInstance().setInstallCityData(installCityData);
				installedCityList.addAll(installCityData.keySet());
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				loadingDialog.dismiss();
				emptyTipsTextView.setVisibility(View.VISIBLE);
				refreshListView();
				super.onPostExecute(result);
			}

			@Override
			protected void onPreExecute()
			{
				showRoundProcessDialog();
				emptyTipsTextView.setVisibility(View.GONE);
				super.onPreExecute();
			}

		};
		asyncTask.execute();
	}

	private void refreshListView()
	{
		cityListAdapter.setCityDataList(cityList);
		cityListAdapter.notifyDataSetChanged();
		installedCityListAdapter.setInstalledCityList(installedCityList);
		installedCityListAdapter.notifyDataSetChanged();
		if (flag == 1)
		{
			initInstalledListview();
		}
	}

	// bindservice
	private ServiceConnection conn = new ServiceConnection()
	{
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			Log.i(TAG, "onServiceConnected");
			DownloadServiceBinder serviceBinder = (DownloadServiceBinder) service;
			downloadService = serviceBinder.getService();
			downloadService.setDownloadHandler(downloadHandler);
			downloadStatusTask = downloadService.getDownloadStstudTask();
			cityListAdapter.setDownloadTask(downloadStatusTask);
			cityListAdapter.notifyDataSetChanged();
			// has unfinish installed city
			if (flag == 0)
			{
				City city = AppManager.getInstance().getCityByCityId(
						currentCityId);
				String downloadURL = city.getDownloadURL();
				installData(currentCityId, downloadURL);
			}
		}
	};

	public Handler downloadHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			final DownloadInfos downloadInfo;
			String downloadURL;
			int cityId = 0;
			switch (msg.what)
			{
			case PROCESS_CHANGED:
				downloadInfo = (DownloadInfos) msg.obj;
				save(downloadInfo);
				refreshDownloadProgress(downloadInfo);
				break;
			case UPZIP:
				downloadInfo = (DownloadInfos) msg.obj;
				boolean upzipResult = downloadInfo.isUpzipResult();
				cityId = downloadInfo.getCityId();
				downloadURL = downloadInfo.getUrl();
				refresh(upzipResult, downloadURL, cityId);
				break;
			case CONNECTION_ERROR:
				downloadInfo = (DownloadInfos) msg.obj;
				cityId = downloadInfo.getCityId();
				downloadURL = downloadInfo.getUrl();
				pauseDownloadCauseError(cityId, downloadURL);
				break;
			default:
				break;
			}
		}

	};

	long lastTime = 0;

	private void save(final DownloadInfos downloadInfo)
	{
		int cityId = downloadInfo.getCityId();
		String downloadURL = downloadInfo.getUrl();
		int fileTotalLength = (int) downloadInfo.getFileLength();
		int downloadLength = (int) downloadInfo.getDownloadLength();
		long currentTime = System.currentTimeMillis() / 1000;
		if (fileTotalLength == downloadLength|| (currentTime % 3 == 0 && currentTime != lastTime))
		{
			Log.i(TAG, "fileTotalLength = " + fileTotalLength+ ",downloadLength = " + downloadLength);
			Log.i(TAG, "time to save downlaodInfo to DB " + currentTime);
			downloadManager.saveDownloadInfo(cityId, downloadURL, "", "", 0,fileTotalLength, downloadLength);
			lastTime = System.currentTimeMillis() / 1000;
		}
		if (fileTotalLength == downloadLength)
		{
			DownloadPreference.insertDownloadInfo(OpenCityActivity.this,Integer.toString(cityId), 0);
		}
		return;
	}

	// long updateTime =0;
	private void refreshDownloadProgress(DownloadInfos downloadInfo)
	{
		if (downloadInfo != null)
		{
			String downloadURL = downloadInfo.getUrl();
			int cityId = downloadInfo.getCityId();
			// ProgressBar downloadBar = progressBarMap.get(downloadURL);
			// TextView resultView = resultTextMap.get(downloadURL);
			ProgressBar downloadBar = null;
			TextView resultView = null;
			if (progressBarMap.containsKey(downloadURL))
			{
				downloadBar = (ProgressBar) ctiyDataListView.findViewWithTag("bar" + downloadURL);
				resultView = (TextView) ctiyDataListView.findViewWithTag("result" + downloadURL);
			}
			if (newVersionCityData != null&& newVersionCityData.containsKey(cityId))
			{
				downloadBar = progressBarMap.get(downloadURL);
				resultView = resultTextMap.get(downloadURL);
			}
			/*
			 * int fileTotalLength = (int)downloadInfo.getFileLength(); int downloadLength = (int)downloadInfo.getDownloadLength(); long currentTime = System.currentTimeMillis()/1000;
			 */
			if (downloadBar != null)
			{
				downloadBar.setMax((int) downloadInfo.getFileLength());
				downloadBar.setProgress((int) downloadInfo.getDownloadLength());
				int persent = (int) (((float) downloadInfo.getDownloadLength() / (float) downloadInfo.getFileLength()) * 100);
				resultView.setText(persent + "%");
				progressBarMap.put(downloadURL, downloadBar);
				resultTextMap.put(downloadURL, resultView);
				/*
				 * if (fileTotalLength == downloadLength ||(currentTime%2 == 0&&currentTime != updateTime)){ updateTime = System.currentTimeMillis()/1000; }
				 */
				if (!downloadInfo.isNotFinish())
				{
					downloadManager.deleteDownloadInfo(downloadURL);
					Log.i(TAG, "download finish delete DB info downloadURL = "+ downloadURL);
					// int position = 0;
					downloadBar.setMax(0);
					downloadBar.setProgress(0);
					resultView.setText("");
					if (newVersionCityData != null&& newVersionCityData.containsKey(cityId))
					{
						ViewGroup buttonGroup = (ViewGroup) installedCityListView.findViewWithTag("buttonGroup" + downloadURL);
						if (buttonGroup != null)
						{
							installedCityListView.findViewWithTag("installing" + downloadURL).setVisibility(View.VISIBLE);
							installedCityListView.findViewWithTag("group" + downloadURL).setVisibility(View.GONE);
							buttonGroup.setVisibility(View.GONE);
						}
					} else
					{
						// position = positionMap.get(downloadURL);
						// openCtiyDataListView.invalidate();
						TextView installingTextView = (TextView) ctiyDataListView.findViewWithTag("installing" + downloadURL);
						if (installingTextView != null)
						{
							ctiyDataListView.findViewWithTag("button" + downloadURL).setVisibility(View.GONE);
							ctiyDataListView.findViewWithTag("group" + downloadURL).setVisibility(View.GONE);
							installingTextView.setVisibility(View.VISIBLE);
						}
					}
					progressBarMap.remove(downloadURL);
					resultTextMap.remove(downloadURL);
				} else
				{
					return;
				}

			}
		}
	}

	private void refresh(boolean zipResult, String downloadURL, int cityId)
	{
		if (zipResult)
		{
			Log.d(TAG, "installed save info to db cityId = " + cityId);
			DownloadPreference.updateDownloadInfo(OpenCityActivity.this,Integer.toString(cityId), 1);
			installCityData.put(cityId, cityId);
		} else
		{
			Toast.makeText(OpenCityActivity.this,getString(R.string.install_fail), Toast.LENGTH_SHORT).show();
			Log.d(TAG, "installed fail delete db info cityId = " + cityId);
			DownloadPreference.deleteDownloadInfo(OpenCityActivity.this,Integer.toString(cityId));
			if (unfinishInstallMap.containsKey(cityId))
			{
				unfinishInstallMap.remove(cityId);
			}
		}
		if (newVersionCityData != null&& newVersionCityData.containsKey(cityId))
		{
			newVersionCityData.remove(cityId);
			if (zipResult)
			{
				TextView installedTextView = (TextView) installedCityListView.findViewWithTag("installed" + downloadURL);
				if (installedTextView != null)
				{
					installedTextView.setVisibility(View.VISIBLE);
					installedCityListView.findViewWithTag("installing" + downloadURL).setVisibility(View.GONE);
				}
			}
			// installedCityListAdapter.notifyDataSetChanged();
		} else
		{
			cityListAdapter.setDownloadTask(downloadStatusTask);
			cityListAdapter.notifyDataSetChanged();
		}
	}

	private void pauseDownloadCauseError(int cityId, String downloadURL)
	{
		/*ViewGroup restartGroup;
		ViewGroup stopGroup;*/
		Button restartButton;
		Button stopButton;
		if (newVersionCityData != null&& newVersionCityData.containsKey(cityId))
		{
			stopButton = (Button) installedCityListView.findViewWithTag("stop" + downloadURL);
			if (progressBarMap.containsKey(downloadURL))
			{
				pauseDownload(cityId, downloadURL);
				restartButton = (Button) installedCityListView.findViewWithTag("restart" + downloadURL);
			} else
			{
				restartButton = (Button) installedCityListView.findViewWithTag("update" + downloadURL);
			}
		} else
		{
			stopButton = (Button) ctiyDataListView.findViewWithTag("stop" + downloadURL);
			if (progressBarMap.containsKey(downloadURL))
			{
				pauseDownload(cityId, downloadURL);
				restartButton = (Button) ctiyDataListView.findViewWithTag("restart" + downloadURL);
			} else
			{
				restartButton = (Button) ctiyDataListView.findViewWithTag("start" + downloadURL);
			}
		}
		if (restartButton != null)
		{
			restartButton.setVisibility(View.VISIBLE);
			stopButton.setVisibility(View.GONE);
		}
	}

	private void downloadToast(String title)
	{
		Toast.makeText(OpenCityActivity.this, title, Toast.LENGTH_SHORT).show();
	}

	private void setCityToast(String cityName)
	{
		Toast.makeText(OpenCityActivity.this, cityName, Toast.LENGTH_SHORT)
				.show();
	}

	private void download(int cityId, String downloadURL,
			String downloadSavePath, String tempPath, String upZipFilePath)
	{
		if (downloadService != null)
		{
			downloadService.startDownload(cityId, downloadURL,
					downloadSavePath, tempPath, upZipFilePath);
		} else
		{
			pauseDownloadCauseError(cityId, downloadURL);
		}

	}

	private void pauseDownload(int cityId, String downloadURL)
	{
		if (downloadService != null)
		{
			downloadService.pauseDownload(downloadURL);
		} else
		{
			pauseDownloadCauseError(cityId, downloadURL);
		}

	}

	private void cancelDownload(int cityId, String downloadURL)
	{
		if (downloadService != null)
		{
			downloadService.cancelDownload(downloadURL);
		} else
		{
			pauseDownloadCauseError(cityId, downloadURL);
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "activity onResume");
		Intent intent = new Intent(OpenCityActivity.this, DownloadService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (installCityData != null)
		{
			TravelApplication.getInstance().setInstallCityData(installCityData);
		}
		if (newVersionCityData != null)
		{
			TravelApplication.getInstance().setNewVersionCityData(
					newVersionCityData);
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unbindService(conn);
		mWindowManager.removeView(mDialogText);
		ActivityMange.getInstance().finishActivity();
		// clear();
		System.gc();
		Log.d(TAG, "activity onDestroy");
	}

	/*private void clear()
	{
		cityList.clear();
		cityList = null;
		hotCityList.clear();
		hotCityList = null;
		searchList.clear();
		searchList = null;
		searchResultList.clear();
		searchResultList = null;
		progressBarMap.clear();
		progressBarMap = null;
		resultTextMap.clear();
		resultTextMap = null;
		positionMap.clear();
		positionMap = null;
		
		 * downloadStatusTask.clear(); downloadStatusTask = null; installCityData.clear() ; installCityData = null; newVersionCityData ;
		 
		unfinishDownload.clear();
		unfinishDownload = null;
		unfinishInstallMap.clear();
		unfinishInstallMap = null;
		installedCityList.clear();
		installedCityList = null;

		stopDownloadBar.clear();
		stopDownloadBar = null;
		stopDownloadresultTextMap.clear();
		stopDownloadresultTextMap = null;
	}*/

	private void searchCityData(String condition)
	{
		searchResultList.clear();
		if (condition == null || condition.equals(""))
		{
			return;
		}
		condition = condition.trim();
		for (City city : searchList)
		{
			String cityName = city.getCityName();
			String countryName = city.getCountryName();
			String cityNameSpell = converterToFirstSpell(cityName);
			String countryNameSpell = converterToFirstSpell(countryName);
			Log.d(TAG, "cityName first 2 word = " + cityName.substring(0, 1));
			Log.d(TAG, "cityNameSpell = " + cityNameSpell);
			Log.d(TAG, "condition = " + condition);

			if (condition.length() == 1)
			{
				cityNameSpell = cityNameSpell.substring(0, 1);
				countryNameSpell = countryNameSpell.substring(0, 1);
				cityName = cityName.substring(0, 1);
				countryName = countryName.substring(0, 1);
			}
			if (isPinYin(condition))
			{
				if (cityNameSpell.contains(condition.toLowerCase())|| countryNameSpell.contains(condition.toLowerCase()))
				{
					searchResultList.add(city);
				}
			} else
			{
				if (cityName.contains(condition)|| countryName.contains(condition))
				{
					searchResultList.add(city);
				}
			}

		}
	}

	public boolean isPinYin(String str)
	{
		Pattern pattern = Pattern.compile("[ a-zA-Z]*");
		return pattern.matcher(str).matches();
	}

	TextWatcher citySearchTextWatcher = new TextWatcher()
	{
		public void onTextChanged(CharSequence s, int start, int before,
				int count)
		{
			if (s.length() != 0)
			{
				searchCityData(s.toString());
				cityListAdapter.setCityDataList(searchResultList);
			} else
			{
				searchResultList.clear();
				cityListAdapter.setCityDataList(cityList);
			}
			emptyTipsTextView.setText(getString(R.string.search_empty_tips));
			cityListAdapter.notifyDataSetChanged();
			ctiyDataListView.invalidate();
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after)
		{

		}

		public void afterTextChanged(Editable s)
		{
		}
	};

	private OnClickListener cityDataListOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			installedCityListView.setVisibility(View.GONE);
			cityDataListGroup.setVisibility(View.VISIBLE);
			v.setBackgroundResource(R.drawable.citybtn_on2);
			installedCityTitle.setBackgroundResource(R.drawable.citybtn_off2);
			cityDataTitle.setTextColor(getResources().getColor(R.color.white));
			installedCityTitle.setTextColor(getResources().getColor(R.color.black));
			emptyTipsTextView.setText(getString(R.string.not_more_data));
			searchButton.setVisibility(View.VISIBLE);
		}
	};

	private OnClickListener installedCityListOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			if (installedCityListView.getVisibility() == View.GONE)
			{
				initInstalledListview();
			}
			searchButton.setVisibility(View.GONE);
		}
	};

	private void initInstalledListview()
	{
		installedCityListView.setVisibility(View.VISIBLE);
		cityDataListGroup.setVisibility(View.GONE);
		cityDataTitle.setBackgroundResource(R.drawable.citybtn_off);
		installedCityTitle.setBackgroundResource(R.drawable.citybtn_on);
		cityDataTitle.setTextColor(getResources().getColor(R.color.black));
		installedCityTitle.setTextColor(getResources().getColor(R.color.white));
		emptyTipsTextView.setText(getString(R.string.did_not_download_anything));
		if (installCityData.size() == 0)
		{
			installedCityListView.setVisibility(View.GONE);
		} else
		{
			installedCityList.clear();
			installedCityList.addAll(installCityData.keySet());
			installedCityListAdapter.setInstalledCityList(installedCityList);
			installedCityListAdapter.notifyDataSetChanged();
			// installedCityListView.invalidate();
		}
	}

	private OnClickListener startDownloadClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			String tag = (String) v.getTag();
			int position = Integer.parseInt(tag.substring(tag.lastIndexOf("t") + 1));
			City city = cityList.get(position);
			int cityId = city.getCityId();
			String downloadURL = city.getDownloadURL();
			String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH, cityId);
			String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);
			String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);

			
			Button stopButton = (Button) ctiyDataListView.findViewWithTag("stop" + position);
			Button cancleButton = (Button) ctiyDataListView.findViewWithTag("cancel" + position);
			ViewGroup dataDownloadMangerGroup = (ViewGroup) ctiyDataListView.findViewWithTag("group" + downloadURL);
			TextView dataSize = (TextView) ctiyDataListView.findViewWithTag("datasize" + downloadURL);

			v.setVisibility(View.GONE);
			dataSize.setVisibility(View.GONE);
			cancleButton.setVisibility(View.VISIBLE);
			stopButton.setVisibility(View.VISIBLE);
			dataDownloadMangerGroup.setVisibility(View.VISIBLE);

			ProgressBar downloadBar = (ProgressBar) ctiyDataListView
					.findViewWithTag("bar" + downloadURL);
			TextView resultView = (TextView) ctiyDataListView
					.findViewWithTag("result" + downloadURL);
			downloadBar.setMax(0);
			downloadBar.setProgress(0);
			resultView.setText("");
			progressBarMap.put(downloadURL, downloadBar);
			resultTextMap.put(downloadURL, resultView);
			download(city.getCityId(), downloadURL, downloadSavePath, tempPath,upZipFilePath);
			String startDownload = getString(R.string.start_download);
			downloadToast(startDownload);
		}
	};

	private OnClickListener stopDownloadOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String tag = (String) v.getTag();

			int position = Integer.parseInt(tag.substring(tag.lastIndexOf("p") + 1));
			City city = cityList.get(position);
			String downloadURL = city.getDownloadURL();
			if (progressBarMap.containsKey(downloadURL))
			{
				stopDownloadBar.put(downloadURL,progressBarMap.get(downloadURL));
				stopDownloadresultTextMap.put(downloadURL,resultTextMap.get(downloadURL));
			}

			Button restartButton = (Button) ctiyDataListView.findViewWithTag("restart" + position);
			//Button stopButton = (Button) ctiyDataListView.findViewWithTag("stop" + downloadURL);
			v.setVisibility(View.GONE);
			restartButton.setVisibility(View.VISIBLE);
			pauseDownload(city.getCityId(), downloadURL);
			String stopDownload = getString(R.string.stop_download);
			downloadToast(stopDownload);
		}
	};

	private OnClickListener restartDownloadClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			String tag = (String) v.getTag();
			int position = Integer.parseInt(tag.substring(tag.lastIndexOf("t") + 1));
			City city = cityList.get(position);
			int cityId = city.getCityId();
			String downloadURL = city.getDownloadURL();
			String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH, cityId);
			String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);
			String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);
			if (!progressBarMap.containsKey(downloadURL))
			{
				ProgressBar downloadBar = (ProgressBar) ctiyDataListView.findViewWithTag("bar" + downloadURL);
				TextView resultView = (TextView) ctiyDataListView.findViewWithTag("result" + downloadURL);
				progressBarMap.put(downloadURL, downloadBar);
				resultTextMap.put(downloadURL, resultView);
			}
			Button stopButton= (Button) ctiyDataListView.findViewWithTag("stop" + position);
			//Button resartButton = (Button) ctiyDataListView.findViewWithTag("restart" + downloadURL);
			v.setVisibility(View.GONE);
			stopButton.setVisibility(View.VISIBLE);
			String restsrtDownload = getString(R.string.start_download);
			download(city.getCityId(), downloadURL, downloadSavePath, tempPath,upZipFilePath);
			downloadToast(restsrtDownload);
		}
	};

	private OnClickListener cancelOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String tag = (String) v.getTag();
			final int position = Integer.parseInt(tag.substring(tag.lastIndexOf("l") + 1));
			Log.d(TAG, "cancel download position = "+position);
			AlertDialog deleteAlertDialog = new AlertDialog.Builder(OpenCityActivity.this).create();
			deleteAlertDialog.setMessage(OpenCityActivity.this.getString(R.string.cancel_download_toast));
			deleteAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,OpenCityActivity.this.getString(R.string.ok),
					new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							City city = cityList.get(position);
							String downloadURL = city.getDownloadURL();
							String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);
							String fileName = TravelUtil.getDownloadFileName(downloadURL);
							tempPath = tempPath + fileName;
							deleteDownloadFile(tempPath);
							downloadManager.deleteDownloadInfo(downloadURL);
							if (unfinishDownload.containsKey(downloadURL))
							{
								unfinishDownload.remove(downloadURL);
							}
							progressBarMap.remove(downloadURL);
							resultTextMap.remove(downloadURL);
							ProgressBar downloadBar = (ProgressBar) ctiyDataListView.findViewWithTag("bar" + downloadURL);
							TextView resultView = (TextView) ctiyDataListView.findViewWithTag("result" + downloadURL);
							if (downloadBar != null)
							{
								downloadBar.setProgress(0);
								downloadBar.setMax(0);
								resultView.setText("");
								ctiyDataListView.findViewWithTag("group" + downloadURL).setVisibility(View.GONE);
							}
							
							Button cancleButton = (Button) ctiyDataListView.findViewWithTag("cancel"+ position);
							Button startButton = (Button) ctiyDataListView.findViewWithTag("start"+position);
							Button stopButton = (Button) ctiyDataListView.findViewWithTag("stop" + position);
							Button restartButton = (Button) ctiyDataListView.findViewWithTag("restart"+ position);
							ViewGroup dataDownloadMangerGroup = (ViewGroup) ctiyDataListView.findViewWithTag("group" + downloadURL);
							TextView dataSize = (TextView) ctiyDataListView.findViewWithTag("datasize" + downloadURL);
							
							cancleButton.setVisibility(View.GONE);						
							stopButton.setVisibility(View.GONE);
							restartButton.setVisibility(View.GONE);
							startButton.setVisibility(View.VISIBLE);
							dataSize.setVisibility(View.VISIBLE);	
							dataDownloadMangerGroup.setVisibility(View.GONE);
							cancelDownload(city.getCityId(), downloadURL);
							downloadToast(getString(R.string.cancel_download));
						}
					});
			deleteAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, ""+ OpenCityActivity.this.getString(R.string.cancel),
				new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
				});
			deleteAlertDialog.show();

		}
	};

	private OnClickListener searchOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			if (downloadTipsTextView.getVisibility() == View.VISIBLE)
			{
				downloadTipsTextView.setVisibility(View.GONE);
				searchBarGroup.setVisibility(View.VISIBLE);
			} else
			{
				InputMethodManager imm = (InputMethodManager) OpenCityActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(OpenCityActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				refreshListView();
				downloadTipsTextView.setVisibility(View.VISIBLE);
				searchBarGroup.setVisibility(View.GONE);
			}
		}
	};

	private OnClickListener onlineOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			int position = (Integer) v.getTag();
			City city = cityList.get(position);
			if(city != null)
			{
				int cityId = city.getCityId();
				String cityName = city.getCountryName() + city.getCityName();
				AppManager.getInstance().setCurrentCityId(cityId);
				Intent intent = new Intent();
				intent.setClass(OpenCityActivity.this, MainActivity.class);
				startActivity(intent);
				setCityToast(cityName);
			}
			
		}
	};

	private OnClickListener updateButtonClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{

			//int cityId = (Integer) v.getTag();
			String tag = (String)v.getTag();
			int cityId = Integer.parseInt(tag.substring(tag.lastIndexOf("e") + 1));
			String downloadURL = newVersionCityData.get(cityId);
			Log.d(TAG, "update data...." + downloadURL);
			String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH, cityId);
			String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);
			String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);

			Button stopButton = (Button) installedCityListView.findViewWithTag("stop"+cityId);
			Button cancleButton = (Button) installedCityListView.findViewWithTag("cancel"+cityId);
			ViewGroup dataDownloadMangerGroup = (ViewGroup) installedCityListView.findViewWithTag("group" + downloadURL);
			TextView dataSize = (TextView) installedCityListView.findViewWithTag("datasize" + downloadURL);

			v.setVisibility(View.GONE);
			dataSize.setVisibility(View.GONE);
			cancleButton.setVisibility(View.VISIBLE);
			stopButton.setVisibility(View.VISIBLE);
			dataDownloadMangerGroup.setVisibility(View.VISIBLE);

			ProgressBar downloadBar = (ProgressBar) installedCityListView.findViewWithTag("bar" + downloadURL);
			TextView resultView = (TextView) installedCityListView.findViewWithTag("result" + downloadURL);
			downloadBar.setMax(0);
			downloadBar.setProgress(0);
			resultView.setText("");
			progressBarMap.put(downloadURL, downloadBar);
			resultTextMap.put(downloadURL, resultView);
			download(cityId, downloadURL, downloadSavePath, tempPath,upZipFilePath);
			String startDownload = getString(R.string.start_download);
			downloadToast(startDownload);
		}
	};

	private OnClickListener restartUpdateOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String tag = (String)v.getTag();
			int cityId = Integer.parseInt(tag.substring(tag.lastIndexOf("t") + 1));
			String downloadURL = newVersionCityData.get(cityId);
			Log.d(TAG, "restart update url " + downloadURL);
			String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH, cityId);
			String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);
			String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);
			if (!progressBarMap.containsKey(downloadURL))
			{
				ProgressBar downloadBar = (ProgressBar) installedCityListView.findViewWithTag("bar" + downloadURL);
				TextView resultView = (TextView) installedCityListView.findViewWithTag("result" + downloadURL);
				progressBarMap.put(downloadURL, downloadBar);
				resultTextMap.put(downloadURL, resultView);
			}

			Button stopButton = (Button) installedCityListView.findViewWithTag("stop"+cityId);
			//Button cancelButton = (Button) installedCityListView.findViewWithTag("cancel"+cityId);
			stopButton.setVisibility(View.VISIBLE);
			//cancelButton.setVisibility(View.VISIBLE);
			v.setVisibility(View.GONE);

			download(cityId, downloadURL, downloadSavePath, tempPath,upZipFilePath);
			String startDownload = getString(R.string.start_download);
			downloadToast(startDownload);
		}

	};

	private OnClickListener stopUpateOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{

			
			//int cityId = (Integer) v.getTag();
			String tag = (String)v.getTag();
			int cityId = Integer.parseInt(tag.substring(tag.lastIndexOf("p") + 1));
			String downloadURL = newVersionCityData.get(cityId);
			Log.i(TAG, "stop update cityId = " + downloadURL);
			if (progressBarMap.containsKey(downloadURL))
			{
				stopDownloadBar.put(downloadURL,progressBarMap.get(downloadURL));
				stopDownloadresultTextMap.put(downloadURL,resultTextMap.get(downloadURL));
			}

			
			Button restartButton = (Button) installedCityListView.findViewWithTag("restart"+cityId);

			v.setVisibility(View.GONE);
			restartButton.setVisibility(View.VISIBLE);

			pauseDownload(cityId, downloadURL);
			String stopDownload = getString(R.string.stop_download);
			downloadToast(stopDownload);
		}

	};

	private OnClickListener cancelUpateOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			//final int cityId = (Integer) v.getTag();
			String tag = (String)v.getTag();
			final int cityId = Integer.parseInt(tag.substring(tag.lastIndexOf("l") + 1));
			AlertDialog deleteAlertDialog = new AlertDialog.Builder(OpenCityActivity.this).create();
			deleteAlertDialog.setMessage(OpenCityActivity.this.getString(R.string.cancel_download_toast));
			deleteAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,OpenCityActivity.this.getString(R.string.ok),
					new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{

							String downloadURL = newVersionCityData.get(cityId);
							Log.i(TAG, "cancel update cityId = " + downloadURL);
							downloadManager.deleteDownloadInfo(downloadURL);
							String tempPath = String.format(ConstantField.DOWNLOAD_TEMP_PATH);
							String fileName = TravelUtil.getDownloadFileName(downloadURL);
							tempPath = tempPath + fileName;
							deleteDownloadFile(tempPath);
							if (unfinishDownload.containsKey(downloadURL))
							{
								unfinishDownload.remove(downloadURL);
							}
							if (progressBarMap.containsKey(downloadURL))
							{
								progressBarMap.remove(downloadURL);
								resultTextMap.remove(downloadURL);

							}
							if (stopDownloadBar.containsKey(downloadURL))
							{
								stopDownloadBar.remove(downloadURL);
								stopDownloadresultTextMap.remove(downloadURL);
							}

							ProgressBar downloadBar = (ProgressBar) installedCityListView.findViewWithTag("bar" + downloadURL);
							TextView resultView = (TextView) installedCityListView.findViewWithTag("result" + downloadURL);
							if (downloadBar != null)
							{
								downloadBar.setProgress(0);
								downloadBar.setMax(0);
								resultView.setText("");
							}

							Button updateButton = (Button) installedCityListView.findViewWithTag("update"+cityId);
							Button stopButton = (Button) installedCityListView.findViewWithTag("stop"+cityId);
							Button restartButton = (Button) installedCityListView.findViewWithTag("restart"+cityId);
							Button cancleButton = (Button) installedCityListView.findViewWithTag("cancel"+cityId);
							ViewGroup dataDownloadMangerGroup = (ViewGroup) installedCityListView.findViewWithTag("group" + downloadURL);
							TextView dataSize = (TextView) installedCityListView.findViewWithTag("datasize" + downloadURL);

							updateButton.setVisibility(View.VISIBLE);
							restartButton.setVisibility(View.GONE);
							dataSize.setVisibility(View.VISIBLE);
							cancleButton.setVisibility(View.GONE);
							stopButton.setVisibility(View.GONE);
							dataDownloadMangerGroup.setVisibility(View.GONE);

							cancelDownload(cityId, downloadURL);
							String cancelDownload = getString(R.string.cancel_download);
							downloadToast(cancelDownload);
						}

					});
			deleteAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, ""+ OpenCityActivity.this.getString(R.string.cancel),
					new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.cancel();
						}
					});
			deleteAlertDialog.show();

		}
	};

	private OnItemClickListener onItemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			City city = cityList.get(arg2);
			if(city != null)
			{
				int cityId = city.getCityId();
				Log.d(TAG, "select city id = "+cityId);
				String cityName = city.getCountryName() + city.getCityName();
				AppManager.getInstance().setCurrentCityId(cityId);
				TravelApplication.getInstance().setCityFlag(true);
				Intent intent = new Intent();
				intent.setClass(OpenCityActivity.this, MainActivity.class);
				startActivity(intent);
				setCityToast(cityName);
			}
			
		}
	};

	private OnClickListener installOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String tag = (String) v.getTag();
			int position = Integer.parseInt(tag.substring(tag.lastIndexOf("l") + 1));
			City city = cityList.get(position);
			int cityId = city.getCityId();
			if (unfinishInstallMap.containsKey(cityId))
			{
				unfinishInstallMap.remove(cityId);
			}
			cityListAdapter.notifyDataSetChanged();
			String downloadURL = city.getDownloadURL();
			installData(cityId, downloadURL);
		}
	};

	private void installData(int cityId, String downloadURL)
	{
		DownloadService.downloadStstudTask.put(downloadURL, UPZIPING);
		String fileName = TravelUtil.getDownloadFileName(downloadURL);
		String downloadSavePath = String.format(ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH, cityId);
		String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH, cityId);
		String zipFilePath = downloadSavePath + fileName;
		downloadService.upZipFile(zipFilePath, upZipFilePath, cityId,downloadURL);
	}

	private void deleteDownloadFile(String folderPath)
	{
		String[] params = new String[]
		{ folderPath };

		AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>()
		{

			@Override
			protected Void doInBackground(String... params)
			{
				String gcZipFilePath = params[0];
				FileUtil.deleteFile(gcZipFilePath);
				return null;
			}
		};
		task.execute(params);
	}

	private void deleteInstallZipData(String filePath)
	{
		String[] params = new String[]
		{ filePath };

		AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>()
		{

			@Override
			protected Void doInBackground(String... params)
			{
				String zipFilePath = params[0];
				FileUtil.deleteFile(zipFilePath);
				return null;
			}
		};
		task.execute(params);
	}

	private OnClickListener cancelInstallOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String tag = (String) v.getTag();
			final int position = Integer.parseInt(tag.substring(tag.lastIndexOf("l") + 1));

			AlertDialog deleteAlertDialog = new AlertDialog.Builder(
					OpenCityActivity.this).create();
			deleteAlertDialog.setMessage(OpenCityActivity.this
					.getString(R.string.delete_install_data));
			deleteAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
					OpenCityActivity.this.getString(R.string.ok),
					new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							City city = cityList.get(position);
							int cityId = city.getCityId();
							String downloadURL = city.getDownloadURL();
							String fileName = TravelUtil
									.getDownloadFileName(downloadURL);
							String downloadSavePath = String.format(
									ConstantField.DOWNLOAD_CITY_ZIP_DATA_PATH,
									cityId);
							String zipFilePath = downloadSavePath + fileName;
							DownloadPreference.deleteDownloadInfo(
									OpenCityActivity.this,
									Integer.toString(cityId));
							if (unfinishInstallMap.containsKey(cityId))
							{
								unfinishInstallMap.remove(cityId);
							}
							cityListAdapter.notifyDataSetChanged();
							deleteInstallZipData(zipFilePath);
						}
					});
			deleteAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, ""+ OpenCityActivity.this.getString(R.string.cancel),
					new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.cancel();
						}
					});
			deleteAlertDialog.show();

		}
	};

	public void showRoundProcessDialog()
	{

		OnKeyListener keyListener = new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
				{
					loadingDialog.dismiss();
					Intent intent = new Intent(OpenCityActivity.this,MainActivity.class);
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
		loadingDialog.setCancelable(false);
		loadingDialog.setOnKeyListener(keyListener);
		loadingDialog.show();
	}


	public class OpenCityDataAdapter extends BaseAdapter implements SectionIndexer
	{
		private static final String TAG = "OpenCityDataAdapter";
		private List<City> cityDataList;
		private Map<String, Integer> downloadTask;
		private Context context;
		boolean isHotCity = false;

		public OpenCityDataAdapter(List<City> cityList, Context context)
		{
			super();
			this.cityDataList = cityList;
			this.context = context;
		}

		@Override
		public int getCount()
		{
			if (cityDataList == null)
				return 0;
			return cityDataList.size();
		}

		@Override
		public Object getItem(int position)
		{
			return cityDataList.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			OpenCityListViewCache viewCache = null;
			City city = cityDataList.get(position);
			String countryName = city.getCountryName();
			if (convertView == null)
			{
				convertView = LayoutInflater.from(context).inflate(
						R.layout.open_ctiy_list_item, null);
				viewCache = new OpenCityListViewCache(convertView);
				convertView.setTag(viewCache);
			} else
			{
				viewCache = (OpenCityListViewCache) convertView.getTag();
			}
			String downloadURL = city.getDownloadURL();
			int cityId = city.getCityId();
			TextView titleTextView = (TextView) viewCache.getTitleTextView();
			String catalog = converterToFirstSpell(countryName);
			if (position == 0)
			{
				titleTextView.setVisibility(View.VISIBLE);
				if (city.getHotCity() && searchResultList.size() == 0)
				{
					titleTextView.setText("热门");
				} else
				{
					titleTextView.setText(countryName);
				}
			} else
			{
				if (position < hotCityList.size()&& searchResultList.size() == 0)
				{
					titleTextView.setVisibility(View.GONE);
				} else
				{
					titleTextView.setText(countryName);
					String lastCountryName = cityDataList.get(position - 1)
							.getCountryName();
					String lastCatalog = converterToFirstSpell(lastCountryName);
					if (!catalog.equals(lastCatalog))
					{
						titleTextView.setVisibility(View.VISIBLE);
					} else
					{
						titleTextView.setVisibility(View.GONE);
					}
				}
			}

			TextView dataCityName = viewCache.getDataCityName();
			ImageView dataSelectIcon = viewCache.getDataSelectIcon();
			if (city.getCityId() == currentCityId)
			{
				dataSelectIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.yes_s));
				dataCityName.setTextColor(context.getResources().getColor(R.color.red));
			} else
			{
				dataSelectIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.no_s));
				dataCityName.setTextColor(context.getResources().getColor(R.color.black));
			}
			dataCityName.setText(city.getCityName());
			TextView dataSize = viewCache.getDataSize();
			ViewGroup buttonGroup = viewCache.getButtonGroup();
			ViewGroup installGroup = viewCache.getInstallGroup();
			TextView installedTextView = viewCache.getInstalledTextView();
			TextView installingTextView = viewCache.getInstallingTextView();
			ViewGroup dataDownloadMangerGroup = viewCache.getDataDownloadMangerGroup();
			Button restartButton = viewCache.getRestartDownloadBtn();
			Button stopButton = viewCache.getStopDownloadBtn();
			Button onlineButton = viewCache.getOnlineButton();
			Button startButton = viewCache.getStartButton();
			Button cancelButton = viewCache.getCancelButton();
			Button installButton = viewCache.getInstallButton();
			Button cancelInstallButton = viewCache.getCancelInstallButton();
			ProgressBar downloadBar = viewCache.getDownloadBar();
			TextView resultTextView = viewCache.getResultTextView();
			DownloadBean downloadBean = null;
			if (unfinishDownload != null&& unfinishDownload.containsKey(downloadURL))
			{
				downloadBean = unfinishDownload.get(downloadURL);
			}
			if (installCityData != null
					&& installCityData.containsKey(city.getCityId()))
			{
				installedTextView.setVisibility(View.VISIBLE);
				buttonGroup.setVisibility(View.GONE);
				installGroup.setVisibility(View.GONE);
				dataDownloadMangerGroup.setVisibility(View.GONE);
				installingTextView.setVisibility(View.GONE);
				dataSize.setVisibility(View.GONE);
			} else
			{
				dataDownloadMangerGroup.setVisibility(View.GONE);
				installGroup.setVisibility(View.GONE);
				buttonGroup.setVisibility(View.VISIBLE);
				dataSize.setVisibility(View.GONE);
				if (downloadStatusTask != null&& downloadStatusTask.containsKey(downloadURL))
				{
					int downloadStatus = downloadStatusTask.get(downloadURL);
					progressBarMap.put(downloadURL, downloadBar);
					resultTextMap.put(downloadURL, resultTextView);
					installingTextView.setVisibility(View.GONE);
					installedTextView.setVisibility(View.GONE);
					startButton.setVisibility(View.GONE);
					cancelButton.setVisibility(View.VISIBLE);
					//startGroup.setVisibility(View.GONE);
					//cancelGroup.setVisibility(View.VISIBLE);
					dataDownloadMangerGroup.setVisibility(View.VISIBLE);
					if (downloadStatus == UPZIPING)
					{
						buttonGroup.setVisibility(View.GONE);
						dataDownloadMangerGroup.setVisibility(View.GONE);
						installingTextView.setVisibility(View.VISIBLE);
						progressBarMap.remove(downloadURL);
						resultTextMap.remove(downloadURL);
					} else if (downloadStatus == DOWNLOADING)
					{
						restartButton.setVisibility(View.GONE);
						stopButton.setVisibility(View.VISIBLE);
					} else
					{
						restartButton.setVisibility(View.VISIBLE);
						stopButton.setVisibility(View.GONE);
						if (stopDownloadBar.containsKey(downloadURL))
						{
							ProgressBar progress = stopDownloadBar.get(downloadURL);
							TextView result = stopDownloadresultTextMap.get(downloadURL);
							downloadBar.setMax(progress.getMax());
							downloadBar.setProgress(progress.getProgress());
							resultTextView.setText(result.getText());
						} else
						{
							if (downloadBean != null)
							{
								downloadBar.setMax(downloadBean.getFileLength());
								downloadBar.setProgress(downloadBean.getDownloadLength());
								String result = (int) ((float) downloadBean.getDownloadLength()/ (float) downloadBean.getFileLength() * 100)+ "%";
								resultTextView.setText(result);
							}
						}
					}
				} else
				{
					installingTextView.setVisibility(View.GONE);
					installedTextView.setVisibility(View.GONE);
					if (unfinishDownload != null&& unfinishDownload.containsKey(downloadURL))
					{
						//startGroup.setVisibility(View.GONE);
						startButton.setVisibility(View.GONE);
						cancelButton.setVisibility(View.VISIBLE);
						dataDownloadMangerGroup.setVisibility(View.VISIBLE);
						restartButton.setVisibility(View.VISIBLE);
						stopButton.setVisibility(View.GONE);
						downloadBar.setMax(downloadBean.getFileLength());
						downloadBar.setProgress(downloadBean.getDownloadLength());
						String result = (int) ((float) downloadBean.getDownloadLength()/ (float) downloadBean.getFileLength() * 100)+ "%";
						resultTextView.setText(result);
					} else if (unfinishInstallMap.containsKey(cityId))
					{
						installGroup.setVisibility(View.VISIBLE);
						installButton.setVisibility(View.VISIBLE);
						cancelInstallButton.setVisibility(View.VISIBLE);
						buttonGroup.setVisibility(View.GONE);
					} else
					{
						dataSize.setText(TravelUtil.getDataSize(city
								.getDataSize()));
						dataSize.setVisibility(View.VISIBLE);
						dataDownloadMangerGroup.setVisibility(View.GONE);
						restartButton.setVisibility(View.GONE);
						stopButton.setVisibility(View.GONE);
						if (city.getDataSize() != 0)
						{
							//startGroup.setVisibility(View.VISIBLE);
							startButton.setVisibility(View.VISIBLE);
							cancelButton.setVisibility(View.GONE);
						} else
						{
							//startGroup.setVisibility(View.GONE);
							startButton.setVisibility(View.GONE);
							cancelButton.setVisibility(View.GONE);
							/* buttonGroup.setVisibility(View.GONE); */
						}
					}
				}
			}
			onlineButton.setTag(position);
			
			dataDownloadMangerGroup.setTag("group" + downloadURL);
			buttonGroup.setTag("button" + downloadURL);
			installedTextView.setTag("installed" + downloadURL);
			installingTextView.setTag("installing" + downloadURL);
			startButton.setTag("start"+position);
			cancelButton.setTag("cancel" + position);
			restartButton.setTag("restart" + position);
			stopButton.setTag("stop" + position);
			resultTextView.setTag("result" + downloadURL);
			downloadBar.setTag("bar" + downloadURL);
			dataSize.setTag("datasize" + downloadURL);
			installGroup.setTag("installGroup" + downloadURL);
			installButton.setTag("install" + position);
			cancelInstallButton.setTag("cancelInstall" + position);
			startButton.setOnClickListener(startDownloadClickListener);
			cancelButton.setOnClickListener(cancelOnClickListener);
			restartButton.setOnClickListener(restartDownloadClickListener);
			stopButton.setOnClickListener(stopDownloadOnClickListener);
			onlineButton.setOnClickListener(onlineOnClickListener);
			installButton.setOnClickListener(installOnClickListener);
			cancelInstallButton.setOnClickListener(cancelInstallOnClickListener);
			return convertView;
		}

		public List<City> getCityDataList()
		{
			return cityDataList;
		}

		public void setCityDataList(List<City> cityDataList)
		{
			this.cityDataList = cityDataList;
		}

		public Map<String, Integer> getDownloadTask()
		{
			return downloadTask;
		}

		public void setDownloadTask(Map<String, Integer> downloadTask)
		{
			this.downloadTask = downloadTask;
		}

		@Override
		public int getPositionForSection(int section)
		{
			for (int i = hotCityList.size(); i < cityDataList.size(); i++)
			{
				String countryName = cityDataList.get(i).getCountryName();
				String l = converterToFirstSpell(countryName).substring(0, 1);
				char firstChar = l.toUpperCase().charAt(0);
				if (firstChar == section)
				{
					return i;
				}
			}
			return -1;
		}

		@Override
		public int getSectionForPosition(int position)
		{
			return 0;
		}

		@Override
		public Object[] getSections()
		{
			return null;
		}

	}

	public static String converterToFirstSpell(String chines)
	{
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++)
		{
			if (nameChar[i] > 128)
			{
				try
				{
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0].charAt(0);
					// pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0];
				} catch (BadHanyuPinyinOutputFormatCombination e)
				{
					e.printStackTrace();
				}
			} else
			{
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}

	public class InstalledCityListAdapter extends BaseAdapter
	{
		private List<Integer> installedCityList;
		private Context context;

		public InstalledCityListAdapter(List<Integer> installedCityList,
				Context context)
		{
			super();
			this.installedCityList = installedCityList;
			this.context = context;
		}

		@Override
		public int getCount()
		{
			if (installedCityList == null)
				return 0;
			return installedCityList.size();
		}

		@Override
		public Object getItem(int position)
		{
			return installedCityList.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{

			OpenCityDownloadViewcache viewCache;
			if (convertView == null)
			{
				convertView = LayoutInflater.from(context).inflate(
						R.layout.open_installed_city_list_item, null);
				viewCache = new OpenCityDownloadViewcache(convertView);
				convertView.setTag(viewCache);
			} else
			{
				viewCache = (OpenCityDownloadViewcache) convertView.getTag();
			}
			int cityId = installedCityList.get(position);
			City city = AppManager.getInstance().getCityByCityId(cityId);
			Button cancelUpdateButton = viewCache.getCancelUpdateButton();
			Button updateButton = viewCache.getUpdateButton();
			Button restartUpdateButton = viewCache.getRestartUpdateBtn();
			Button stopUpdateButton = viewCache.getStopUpdateBtn();
			Button deleteButton = viewCache.getDeleteBtn();
			ViewGroup updateStatusGroup = viewCache.getUpdateStatusGroup();
			ViewGroup buttonGroup = viewCache.getButtonGroup();
			TextView updateTextView = viewCache.getUpdateTextView();
			ProgressBar updateBar = viewCache.getUpdateBar();
			TextView cityName = viewCache.getDataCityName();
			TextView citySize = viewCache.getDataSize();
			TextView installingTextView = viewCache.getUpdatingTextView();
			TextView installedTextView = viewCache.getUpdatedTextView();
			String downloadURL = city.getDownloadURL();
			if (city != null)
			{
				String dataName = city.getCountryName() + "."
						+ city.getCityName();
				deleteButton.setTag(position);
				cityName.setText(dataName);
				citySize.setText(TravelUtil.getDataSize(city.getDataSize()));
				deleteButton.setOnClickListener(deleteOnClickListener);
				updateStatusGroup.setVisibility(View.GONE);
				if (newVersionCityData != null && newVersionCityData.containsKey(cityId))
				{
					downloadURL = newVersionCityData.get(cityId);
					updateButton.setVisibility(View.VISIBLE);
					citySize.setVisibility(View.GONE);
					DownloadBean downloadBean = null;
					buttonGroup.setVisibility(View.VISIBLE);
					stopUpdateButton.setVisibility(View.GONE);
					if (unfinishDownload != null && unfinishDownload.containsKey(downloadURL))
					{
						downloadBean = unfinishDownload.get(downloadURL);
					}
					if (downloadStatusTask != null && downloadStatusTask.containsKey(downloadURL))
					{
						updateButton.setVisibility(View.GONE);
						updateStatusGroup.setVisibility(View.VISIBLE);
						cancelUpdateButton.setVisibility(View.VISIBLE);
						progressBarMap.put(downloadURL, updateBar);
						resultTextMap.put(downloadURL, updateTextView);
						int downloadStatus = downloadStatusTask.get(downloadURL);
						if (downloadStatus == DOWNLOADING)
						{
							restartUpdateButton.setVisibility(View.GONE);
							stopUpdateButton.setVisibility(View.VISIBLE);
						} else if (downloadStatus == UPZIPING)
						{
							installingTextView.setVisibility(View.VISIBLE);
							updateStatusGroup.setVisibility(View.GONE);
							buttonGroup.setVisibility(View.GONE);
						} else
						{
							restartUpdateButton.setVisibility(View.VISIBLE);
							if (stopDownloadBar.containsKey(downloadURL)&& stopDownloadresultTextMap.containsKey(downloadURL))
							{
								ProgressBar progress = stopDownloadBar.get(downloadURL);
								TextView result = stopDownloadresultTextMap.get(downloadURL);
								updateBar.setMax(progress.getMax());
								updateBar.setProgress(progress.getProgress());
								updateTextView.setText(result.getText());
							} else
							{
								if (downloadBean != null)
								{
									updateBar.setMax(downloadBean.getFileLength());
									updateBar.setProgress(downloadBean.getDownloadLength());
									String result = (int) ((float) downloadBean.getDownloadLength()/ (float) downloadBean.getFileLength() * 100)+ "%";
									updateTextView.setText(result);
								}
							}
						}
					} else
					{
						installedTextView.setVisibility(View.GONE);
						installingTextView.setVisibility(View.GONE);
						cancelUpdateButton.setVisibility(View.GONE);
						if (downloadBean != null)
						{
							updateButton.setVisibility(View.GONE);
							citySize.setVisibility(View.GONE);
							updateStatusGroup.setVisibility(View.VISIBLE);
							restartUpdateButton.setVisibility(View.VISIBLE);
							cancelUpdateButton.setVisibility(View.VISIBLE);
							updateBar.setMax(downloadBean.getFileLength());
							updateBar.setProgress(downloadBean.getDownloadLength());
							String result = (int) ((float) downloadBean.getDownloadLength()/ (float) downloadBean.getFileLength() * 100)+ "%";
							updateTextView.setText(result);
						}
					}

				} else
				{
					installedTextView.setVisibility(View.GONE);
					installingTextView.setVisibility(View.GONE);
					citySize.setVisibility(View.VISIBLE);
					buttonGroup.setVisibility(View.GONE);
				}
			}
			updateButton.setTag("update"+cityId);
			installedTextView.setTag("installed" + downloadURL);
			installingTextView.setTag("installing" + downloadURL);
			restartUpdateButton.setTag("restart"+cityId);
			stopUpdateButton.setTag("stop"+cityId);
			cancelUpdateButton.setTag("cancel"+cityId);
			updateTextView.setTag("result" + downloadURL);
			updateBar.setTag("bar" + downloadURL);
			updateStatusGroup.setTag("group" + downloadURL);
			buttonGroup.setTag("buttonGroup" + downloadURL);
			citySize.setTag("datasize" + downloadURL);
			updateButton.setOnClickListener(updateButtonClickListener);
			restartUpdateButton.setOnClickListener(restartUpdateOnClickListener);
			stopUpdateButton.setOnClickListener(stopUpateOnClickListener);
			cancelUpdateButton.setOnClickListener(cancelUpateOnClickListener);
			return convertView;
		}

		public List<Integer> getInstalledCityList()
		{
			return installedCityList;
		}

		public void setInstalledCityList(List<Integer> installedCityList)
		{
			this.installedCityList = installedCityList;
		}

		private OnClickListener deleteOnClickListener = new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				final int position = (Integer) v.getTag();
				AlertDialog deleteAlertDialog = new AlertDialog.Builder(context).create();
				deleteAlertDialog.setMessage(context.getString(R.string.delete_install_data));
				deleteAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
						context.getString(R.string.ok),
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								int cityId = installedCityList.get(position);
								City city = AppManager.getInstance().getCityByCityId(cityId);
								String downloadURL = city.getDownloadURL();
								String upZipFilePath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH,cityId);
								installedCityList.remove(position);
								if (newVersionCityData != null
										&& newVersionCityData
												.containsKey(cityId))
								{
									newVersionCityData.remove(cityId);
								}
								if (progressBarMap.containsKey(downloadURL))
								{
									progressBarMap.remove(downloadURL);
									resultTextMap.remove(downloadURL);
								}
								if (downloadStatusTask != null
										&& downloadStatusTask
												.containsKey(downloadURL))
								{
									downloadStatusTask.remove(downloadURL);
									cancelDownload(cityId, downloadURL);
								}
								if (unfinishDownload.containsKey(downloadURL))
								{
									unfinishDownload.remove(downloadURL);
								}
								installCityData.remove(cityId);
								installedCityListAdapter
										.setInstalledCityList(installedCityList);
								installedCityListAdapter.notifyDataSetChanged();
								cityListAdapter
										.setDownloadTask(downloadStatusTask);
								cityListAdapter.notifyDataSetChanged();
								DownloadPreference.deleteDownloadInfo(context,
										Integer.toString(cityId));
								// deleteInstalledData(upZipFilePath);
							}
						});
				deleteAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, ""
						+ context.getString(R.string.cancel),
						new DialogInterface.OnClickListener()
						{

							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								dialog.cancel();

							}
						});
				deleteAlertDialog.show();
			}
		};
	}

}
