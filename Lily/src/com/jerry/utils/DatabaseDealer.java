package com.jerry.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class DatabaseDealer {
	private static final String DEFAULT_DATABASE_NAME = "LILY";

	private static final SQLiteDatabase createDataBase(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context, DEFAULT_DATABASE_NAME);
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		return database;
	}
	
	public static final void deleteFromBlock(Context context, String name) {
		SQLiteDatabase db = createDataBase(context);
		db.execSQL("delete from block where name = '" + name + "'");
		db.close();
	}
	
	public static final void add2Block(Context context,String name) {
		SQLiteDatabase db = createDataBase(context);
		ContentValues values = new ContentValues();
		values.put("name", name);
		db.insert("block", null, values);
		db.close();
	}
	
	public static final List<String> getBlockList(Context context) {
		SQLiteDatabase db = createDataBase(context);
		Cursor cursor = db.rawQuery("select name from block",null);
		List<String> blockList = new ArrayList<String>();
		while(cursor.moveToNext()){
			blockList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return blockList;
	}

	public static final void insert(Context context,String username, String password) {
		SQLiteDatabase db = createDataBase(context);
		ContentValues values = new ContentValues();
		values.put("username", username);
		values.put("password", password);
		db.insert("userinfo", null, values);
		db.close();
	}
	
	public static final void deleteAcc(Context context) {
		SQLiteDatabase db = createDataBase(context);
		db.execSQL("delete from userinfo");
		db.close();
	}

	public static final void clearOnlineFav(Context context) {
		SQLiteDatabase db = createDataBase(context);
		db.execSQL("delete from fav where islocal = 0");
		db.close();
	}
	
	public static final void updateSettings(Context context, String key, String value) {
		SQLiteDatabase db = createDataBase(context);
		db.execSQL("update settings set value = '" + value + "' where key = '" + key + "'");
		db.close();
	}
	
	public static final com.jerry.model.Settings getSettings(Context context) {
		SQLiteDatabase db = createDataBase(context);
		Bundle bundle = new Bundle();
		Cursor cursor = db.rawQuery("select key,value from settings",null);
		while(cursor.moveToNext()){
			bundle.putString(cursor.getString(0), cursor.getString(1));
		}
		cursor.close();
		db.close();
		com.jerry.model.Settings settings = new com.jerry.model.Settings();
		settings.setLogin(bundle.getString("isLogin").equals("1"));
		settings.setSavePic(bundle.getString("isSavePic").equals("1"));
		settings.setShowPic(bundle.getString("isShowPic").equals("1"));
		settings.setSendMail(bundle.getString("isSendMail").equals("1"));
		settings.setSign(bundle.getString("sign"));
		return settings;
	}

	public static final boolean isFav(Context context, String boardName) {
		SQLiteDatabase db = createDataBase(context);
		Cursor cursor = db.rawQuery("select english from fav where english ='" + boardName + "'",null);
		if(cursor.getCount() > 0) {
			db.close();
			cursor.close();
			return true;
		}
		db.close();
		cursor.close();
		return false;
	}

	public static final Bundle query(Context context) {
		Bundle bundle = new Bundle();
		SQLiteDatabase db = createDataBase(context);
		Cursor cursor = db.rawQuery("select username,password from userinfo order by _id desc",null);
		while(cursor.moveToNext()){
			bundle.putString("username", cursor.getString(0));
			bundle.putString("password", cursor.getString(1));
		}
		cursor.close();
		db.close();
		return bundle;
	}
	
	public static final void addBoard2LocalFav(Context context,String boardName) {
		ContentValues values = new ContentValues();
		values.put("english", boardName);
		values.put("islocal", 1);
		SQLiteDatabase db = createDataBase(context);
		db.insert("fav", null, values);
		db.close();
	}

	public static final void addFav(Context context,List<ContentValues> valueList) {
		if (valueList == null || valueList.size() == 0) {
			return;
		}
		SQLiteDatabase db = createDataBase(context);
		for(int i = 0; i < valueList.size(); i++) {
			db.insert("fav", null, valueList.get(i));
		}
		db.close();
	}

	public static List<String> getFavList(Context context) {
		SQLiteDatabase db = createDataBase(context);
		Cursor cursor = db.rawQuery("select distinct english from fav",null);
		List<String> fav = new ArrayList<String>();
		while(cursor.moveToNext()){
			fav.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return fav;
	}

	public static List<String> getAllBoardList(Context context) {
		SQLiteDatabase db = createDataBase(context);
		Cursor cursor = db.rawQuery("select distinct english,chinese from board",null);
		List<String> fav = new ArrayList<String>();
		while(cursor.moveToNext()) {
			fav.add(cursor.getString(0) + "(" + cursor.getString(1) + ")");
		}
		cursor.close();
		db.close();
		return fav;
	}

	public static final Cursor getAllBoardCursor(Context context) {
		SQLiteDatabase db = createDataBase(context);
		return db.rawQuery("select english,chinese from board order by english",null);
	}
}
