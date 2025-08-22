package com.cl3t4p.TinyNode.db;

import com.cl3t4p.TinyNode.model.User;

public interface UserRepo {

  void getAllUserIds();

  void createUser(User user);

  void getUserFromUserId(String userId);

  void updateUser(User user);

  void deleteUser(String userId);
}
