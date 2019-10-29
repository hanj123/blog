package com.hanj.blog.service.serviceImpl;

import com.hanj.blog.dao.UserRepository;
import com.hanj.blog.po.User;
import com.hanj.blog.service.UserService;
import com.hanj.blog.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser(String username, String password) {

        User user = userRepository.findByUsernameAndPassword(username,MD5Util.MD5Encode(password,"utf8"));

        return user;
    }
}
