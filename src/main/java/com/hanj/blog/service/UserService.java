package com.hanj.blog.service;

import com.hanj.blog.po.User;

public interface UserService {

    User checkUser(String username, String password);
}
