package com.pswseoul.dbmanager;

import com.pswseoul.util.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

public class MySQLiteHandler  {
	 MySQLiteOpenHelper helper;
	 SQLiteDatabase db;
	  
	 //
	 public MySQLiteHandler(Context context) {
	  helper = new MySQLiteOpenHelper(context, "approve.sqlite", null, 1);
	 }
	  
	 //open
	 public static MySQLiteHandler open(Context context) {
	  return new MySQLiteHandler(context);
	 }
	  
	 //close
	 public void close() {
	  db.close();
	 }

	public void insertHashMap(String table, Map<String, String> map , String state) {

	}

	 //
	 public void insert(String gubun, String content) {
	  db = helper.getWritableDatabase();
	  ContentValues values = new ContentValues();
	        values.put("gubun", gubun);
	        values.put("content", content);
	        values.put("regdate", tools.getMysqlDate());
	        db.insert("person", null, values);
	 }//end insert
	  
	 //
	 public void update(String gubun, String content) {
	  db = helper.getWritableDatabase();
	  ContentValues values = new ContentValues();
	        values.put("content", content);
	        values.put("regdate", tools.getMysqlDate());
	        db.update("person", values, "gubun = ?", new String[]{gubun});
	 }//end update
	  
	 //
	 public void delete(String gubun) {
	  db = helper.getWritableDatabase();
	        db.delete("person", "gubun=?", new String[]{gubun});
	 }//end delete
	  
	 //
	 public Cursor select() {
	  db = helper.getReadableDatabase();
	  Cursor c = db.query("person", null, null, null, null, null, null);
	  return c;
	 }//end select
	 
	 public Cursor select(String query, String[] params) {
	   db = helper.getReadableDatabase();
	   Cursor c = db.query("person", null, query, params, null, null, null);		  
//	  Cursor c =  db.rawQuery("SELECT * FROM person WHERE gubun = 'company'" , null);
	  return c;
	}//end select
	 
}//end class