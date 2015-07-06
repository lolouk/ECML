package com.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class UsersDAO extends DAOBase {
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
			      USER_PASSWORD + " TEXT);";
	  public static final String USERS_TABLE_DROP = "DROP TABLE IF EXISTS " + USERS_TABLE_NAME + ";";
	public UsersDAO(Context pContext) {
		super(pContext);
		// TODO Auto-generated constructor stub
	}
   
	/** 
	 * Add a new user into the database 
	 * @param user
	 */
 public void add(User user){
	ContentValues value = new ContentValues();
	value.put(UsersDAO.USER_LOGIN, user.getLogin());
	value.put(UsersDAO.USER_PASSWORD, user.getPassword());
	value.put(UsersDAO.USER_QUESTION, user.getQuestion());
	value.put(UsersDAO.USER_ANSWER, user.getAnswer());
	mDb.insert(UsersDAO.USERS_TABLE_NAME, null, value);
    mDb.insert(USERS_TABLE_NAME, null,value);
    
 }
 
 /**delete a user giving his Id
  * 
  * @param id
  */
 public void delete(String login) {
	  mDb.delete(USERS_TABLE_NAME, USER_LOGIN + " = ?", new String[] {String.valueOf(login)});
	}
 
 public void modifyPassword(User user){
	 ContentValues value = new ContentValues();
	 value.put(USER_PASSWORD, user.getPassword());
	 mDb.update(USERS_TABLE_NAME, value, USER_KEY  + " = ?", new String[] {String.valueOf(user.getId())});
 }
 /**
  * Check if the login an password exist in the database 
  * @param user
  * @return
  */
 public boolean checkIfExist(User user){
	 Cursor c = mDb.rawQuery("select " + USER_LOGIN + " from " + USERS_TABLE_NAME + " where login =  ?"+ "and password = ?" ,  new String[]{user.getLogin(),user.getPassword()});
	 if( c.getCount() == 0 ){ return false;}
	 else { return true ;}
 }
 /** Check if the username is already taken
  * @param login 
  * @return true if the username already exists
  * 
  */
 public boolean checkLoginTaken(String login){
	 Cursor c = mDb.rawQuery("select " + USER_LOGIN + " from " + USERS_TABLE_NAME + " where login =  ?" ,  new String[]{login});
	 
	 if( c.getCount() == 0 ){ // the login is not taken
		 return false;}
	 else { return true ;}
 }
 /**
  * 
  * @param login
  * @return
  */
 public String getQuestion ( String login){

	 Cursor c = mDb.rawQuery("select " + USER_QUESTION + " from " + USERS_TABLE_NAME + " where login =  ?" ,  new String[]{login});

	 if(c.getCount() == 0 ){return null;}
	 else { 
	       c.moveToFirst();
	       return c.getString(0);
	       }
	 
 }
 
 /**
  * 
  * @param login
  * @return
  */
 public String getAnswer ( String login){

	 Cursor c = mDb.rawQuery("select " + USER_ANSWER + " from " + USERS_TABLE_NAME + " where login =  ?" ,  new String[]{login});

	 if(c.getCount() == 0 ){return null;}
	 else { 
	       c.moveToFirst();
	       return c.getString(0);
	       }
 }
 /**
  * Temporary function for test ( must be removed )
  * @param login
  * @return
  */
 public String getPassword ( String login){

	 Cursor c = mDb.rawQuery("select " + USER_PASSWORD + " from " + USERS_TABLE_NAME + " where login =  ?" ,  new String[]{login});

	 if(c.getCount() == 0 ){return null;}
	 else { 
	       c.moveToFirst();
	       return c.getString(0);
	       }
 }
 
}
