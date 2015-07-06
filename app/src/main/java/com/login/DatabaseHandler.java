package com.login;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

 class DatabaseHandler extends SQLiteOpenHelper {
	  public static final String USER_KEY = "id";
	  public static final String USER_LOGIN = "login";
	  public static final String USER_PASSWORD = "password";
	  public static final String USER_QUESTION = "question";
	  public static final String USER_ANSWER = "answer";
	  public static final String USERS_TABLE_NAME = "users";
	  public static final String USERS_TABLE_CREATE =
	    "CREATE TABLE " + USERS_TABLE_NAME + " (" +
	      USER_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
	      USER_LOGIN + " TEXT, " +
	      USER_PASSWORD + " TEXT, "+
	      USER_QUESTION + " TEXT, " +
	      USER_ANSWER + " TEXT );";
	  public DatabaseHandler(Context context, String name, CursorFactory factory, int version) {
		    super(context, name, factory, version);
		  }
      
		  @Override
		  public void onCreate(SQLiteDatabase db) {
		    db.execSQL(USERS_TABLE_CREATE);
		  }
		  
		  public static final String USERS_TABLE_DROP = "DROP TABLE IF EXISTS " + USERS_TABLE_NAME + ";";
		  
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			 db.execSQL(USERS_TABLE_DROP);
			 onCreate(db);
		}
}
