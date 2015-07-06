package com.androidim.interfaces;

import java.io.UnsupportedEncodingException;
import java.sql.Date;


public interface IAppManager {
	
	public String getUsername();
	public String sendMessage(String username,String tousername, String message) throws UnsupportedEncodingException;
	public String authenticateUser(String usernameText, String passwordText) throws UnsupportedEncodingException; 
	public void messageReceived(String username, String message);
//	public void setUserKey(String value);
	public boolean isNetworkConnected();
	public boolean isUserAuthenticated();
	public String getLastRawFriendList();
	public void exit();
	public String signUpUser(String usernameText, String passwordText, String email);
	public String addNewFriendRequest(String friendUsername);
	public String sendFriendsReqsResponse(String approvedFriendNames,
			String discardedFriendNames);
//C'est moi 
	public String deleteFriendRequest(String friendUsername);
	public String fillInProfil(String username , String password,String fName,String lName, int gender,Date birth,String instrument);

	
}
