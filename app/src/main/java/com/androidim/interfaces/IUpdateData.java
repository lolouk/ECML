package com.androidim.interfaces;
import com.androidim.types.FriendInfo;
import com.androidim.types.MessageInfo;


public interface IUpdateData {
	public void updateData(MessageInfo[] messages, FriendInfo[] friends, FriendInfo[] unApprovedFriends, String userKey);

}
