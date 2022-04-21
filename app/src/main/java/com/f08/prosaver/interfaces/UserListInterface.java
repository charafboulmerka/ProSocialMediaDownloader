package com.f08.prosaver.interfaces;


import com.f08.prosaver.model.FBStoryModel.NodeModel;
import com.f08.prosaver.model.story.TrayModel;

public interface UserListInterface {
    void userListClick(int position, TrayModel trayModel);
    void fbUserListClick(int position, NodeModel trayModel);
}
