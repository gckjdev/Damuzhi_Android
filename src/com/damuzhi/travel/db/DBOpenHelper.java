package com.damuzhi.travel.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "downloadInfo.db";
	private static final int VERSION = 1;
	
	public DBOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//db.execSQL("CREATE TABLE IF NOT EXISTS FileDownloadLog (id integer primary key autoincrement, downloadurl varchar(100), threadid INTEGER, filelength INTEGER,downlength INTEGER)");
		//db.close();
		//status 1.downloading 2. done 3.unzip 4.zipsuccess 5.success
		db.execSQL("CREATE TABLE IF NOT EXISTS FileDownloadLog (_id integer PRIMARY KEY AUTOINCREMENT,cityid INTEGER , downloadurl varchar(100),savepath varchar(100), temppath varchar(100),  filelength INTEGER,downlength INTEGER,threadid INTEGER, status INTEGER)");		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS FileDownloadLog ");
		onCreate(db);
		//db.close();
	}

}
