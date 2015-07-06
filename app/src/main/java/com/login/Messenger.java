package com.login;

import com.sideActivities.BaseActivity;

public class Messenger extends BaseActivity{
 
	 public UsersDAO usersdb = new UsersDAO(this) ;
	 
	 public void start(){
		 usersdb.open();
	 }
}
