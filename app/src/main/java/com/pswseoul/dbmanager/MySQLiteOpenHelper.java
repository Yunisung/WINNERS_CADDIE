package com.pswseoul.dbmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	
	//
	public MySQLiteOpenHelper(Context context, String name, CursorFactory factory,  int version) {
	   super(context, name, factory, version);
	}
	
	//
	public void onCreate(SQLiteDatabase db) {
	 Log.i("xxx", "onCreate >>>>>>>>>>>>>>>.....");
	 String sql = "create table person ( " +
	         " _id integer primary key autoincrement , " +
	         " gubun text , " +			 
	         " content text , " +	         
	         " regdate text )";
	       db.execSQL(sql);
	       
	}//end onCreate
	
	//
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	 Log.i("xxx", "onUpgrade >>>>>>>>>>>>>>>.....");
	  
	 String sql = "drop table if exists person";
	       db.execSQL(sql);	        
	       onCreate(db);
	}//end onUpgrade
	
}//end class